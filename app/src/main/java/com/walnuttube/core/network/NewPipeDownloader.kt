package com.walnuttube.core.network

import okhttp3.MediaType
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody
import okhttp3.Response
import okhttp3.MediaType.Companion.toMediaType
import org.schabi.newpipe.extractor.downloader.Downloader
import org.schabi.newpipe.extractor.downloader.Request as NPRequest
import org.schabi.newpipe.extractor.downloader.Response as NPResponse
import org.schabi.newpipe.extractor.exceptions.ReCaptchaException
import java.io.IOException
import java.util.concurrent.TimeUnit

/**
 * OkHttp-backed NewPipe [Downloader].
 *
 * IMPORTANT: [execute] is a SYNCHRONOUS BLOCKING CALL using OkHttp's [Call.execute()].
 * It MUST be invoked on a background thread (e.g., Kotlin's Dispatchers.IO) by the caller.
 *
 * CANCELLATION LIMITATION:
 * Coroutine cancellation (CancellationException) does NOT automatically cancel the
 * underlying OkHttp Call. The HTTP request will continue to completion in the
 * background. This is a known limitation of synchronous OkHttp calls.
 * Future phases may implement Call.cancel() integration if required.
 *
 * REDIRECT HANDLING:
 * Redirects are validated and followed by [NetworkModule.RedirectValidationInterceptor]
 * up to a maximum of 5 redirects. The interceptor:
 * - Validates each redirect target against the YouTube domain allowlist ([AllowedHosts])
 * - Handles relative Location headers by resolving against the original request URL
 * - Preserves method and body for 307/308, converts to GET for 303, may convert for 301/302
 * - Strips Authorization/Cookie headers on cross-origin redirects
 * - Returns the final response (not the 3xx intermediate responses)
 */
class NewPipeDownloader(private val client: OkHttpClient) : Downloader() {

    override fun execute(request: NPRequest): NPResponse {
        AllowedHosts.validateUrl(request.url())
        val okRequest = toOkHttpRequest(request)

        // Synchronous, blocking call — runs on caller's thread
        // Caller (extraction layer) MUST invoke on a background thread
        // Redirects are automatically followed by the interceptor
        return client.newCall(okRequest).execute().use { okResponse ->
            toNewPipeResponse(okResponse)
        }
    }

    private fun toOkHttpRequest(npRequest: NPRequest): Request {
        val builder = Request.Builder()
            .url(npRequest.url())
            .method(npRequest.httpMethod(), toRequestBody(npRequest))

        // Headers: case-insensitive, no duplicates
        npRequest.headers().forEach { (name, values) ->
            values.forEach { value ->
                builder.addHeader(name, value)
            }
        }

        return builder.build()
    }

    private fun toRequestBody(npRequest: NPRequest): RequestBody? {
        val data = npRequest.dataToSend()
        return when {
            data != null -> {
                // POST body present — preserve Content-Type from request headers if supplied,
                // otherwise default to application/x-www-form-urlencoded
                val contentType = npRequest.headers()["Content-Type"]?.firstOrNull()
                    ?.toMediaType()
                    ?: "application/x-www-form-urlencoded".toMediaType()
                RequestBody.create(contentType, data)
            }
            npRequest.httpMethod() == "POST" -> {
                // POST with null body — send empty body, preserve Content-Type if supplied
                val contentType = npRequest.headers()["Content-Type"]?.firstOrNull()
                    ?.toMediaType()
                    ?: "application/x-www-form-urlencoded".toMediaType()
                RequestBody.create(contentType, "")
            }
            else -> null // GET/HEAD have no body
        }
    }

    private fun toNewPipeResponse(okResponse: Response): NPResponse {
        val responseBody = okResponse.body?.string() ?: ""
        val headers = okResponse.headers.toMultimap()
        val latestUrl = okResponse.request.url.toString()

        // okResponse.use { } ensures automatic close
        return NPResponse(
            okResponse.code,
            okResponse.message ?: "",
            headers,
            responseBody,
            latestUrl
        )
    }
}