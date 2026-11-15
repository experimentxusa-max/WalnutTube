package com.walnuttube.core.network

import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import okhttp3.Interceptor
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.Response
import org.schabi.newpipe.extractor.downloader.Downloader
import javax.inject.Singleton
import java.net.URL
import java.util.concurrent.TimeUnit

@Module
@InstallIn(SingletonComponent::class)
object NetworkModule {

    /**
     * Interceptor that validates redirect targets against the YouTube domain allowlist
     * and follows allowed redirects up to a maximum count.
     * This prevents SSRF via malicious redirects from allowed domains to internal services.
     */
    private class RedirectValidationInterceptor : Interceptor {
        override fun intercept(chain: Interceptor.Chain): Response {
            var request = chain.request()
            var redirectCount = 0
            val maxRedirects = 5

            while (true) {
                var response = chain.proceed(request)

                // Check for redirect status codes
                val redirectCode = response.code
                if (redirectCode in 300..399 && redirectCode != 304 && redirectCount < maxRedirects) {
                    val location = response.header("Location")
                    if (location != null) {
                        // Validate redirect target against allowlist
                        AllowedHosts.validateRedirect(location, request.url.toString())

                        // Reject HTTPS-to-HTTP downgrade redirects (even to allowed hosts)
                        val originalUrl = URL(request.url.toString())
                        val redirectUrl = URL(originalUrl, location)
                        if (originalUrl.protocol.equals("https", ignoreCase = true)
                            && redirectUrl.protocol.equals("http", ignoreCase = true)) {
                            throw SecurityException(
                                "HTTPS-to-HTTP downgrade redirect blocked: ${redirectUrl.host}"
                            )
                        }

                        // Build new request for redirect
                        request = buildRedirectRequest(request, response, location)
                        redirectCount++

                        // Close the redirect response body to avoid leaks
                        response.body?.close()
                        continue
                    }
                }

                return response
            }
        }

        /**
         * Builds the redirect request following RFC 7231 semantics:
         * - 301/302: Method may change from POST to GET (historical behavior)
         * - 303: Method MUST change to GET
         * - 307/308: Method and body MUST be preserved
         */
        private fun buildRedirectRequest(
            originalRequest: Request,
            redirectResponse: Response,
            location: String
        ): Request {
            val redirectCode = redirectResponse.code
            val originalMethod = originalRequest.method
            val originalHttpUrl = originalRequest.url
            // Convert HttpUrl to java.net.URL for resolution
            val originalUrl = URL(originalHttpUrl.toString())
            // Resolve relative Location against original request URL
            val redirectUrl = URL(originalUrl, location)

            val builder = Request.Builder()
                .url(redirectUrl.toString())

            // Handle method and body based on redirect status code
            val (method, body) = when (redirectCode) {
                303 -> "GET" to null // 303 See Other: always convert to GET, drop body
                301, 302 -> {
                    // 301/302: Historically converted POST to GET, but modern practice preserves method
                    // We preserve method for POST/PUT but note this is a behavioral choice
                    if (originalMethod == "POST" || originalMethod == "PUT") {
                        originalMethod to originalRequest.body
                    } else {
                        originalMethod to null
                    }
                }
                307, 308 -> {
                    // 307/308: Must preserve method and body
                    originalMethod to originalRequest.body
                }
                else -> originalMethod to originalRequest.body
            }

            builder.method(method, body)

            // Copy headers from original request, except Host (will be set from new URL)
            // and Content-Length/Content-Type (will be set from body if present)
            originalRequest.headers.forEach { (name, value) ->
                when (name.lowercase()) {
                    "host", "content-length", "content-type" -> {} // skip, will be recomputed
                    else -> builder.addHeader(name, value)
                }
            }

            // For cross-origin redirects, strip Authorization headers for security
            // Cross-origin means different scheme, host, OR port (RFC 6454)
            if (!sameOrigin(originalUrl, redirectUrl)) {
                builder.removeHeader("Authorization")
                builder.removeHeader("Cookie")
                builder.removeHeader("Cookie2")
            }

            return builder.build()
        }

        /**
         * Checks if two URLs have the same origin (scheme, host, and port).
         * Per RFC 6454, origins differ if any of these components differ.
         */
        private fun sameOrigin(url1: URL, url2: URL): Boolean {
            return url1.protocol == url2.protocol
                && url1.host.equals(url2.host, ignoreCase = true)
                && effectivePort(url1) == effectivePort(url2)
        }

        /**
         * Returns the effective port for a URL, accounting for default ports.
         * HTTP defaults to 80, HTTPS defaults to 443.
         */
        private fun effectivePort(url: URL): Int {
            val port = url.port
            if (port != -1) return port
            return when (url.protocol.toLowerCase()) {
                "https" -> 443
                "http" -> 80
                else -> -1
            }
        }
    }

    @Provides
    @Singleton
    fun provideOkHttpClient(): OkHttpClient = OkHttpClient.Builder()
        .connectTimeout(15, TimeUnit.SECONDS)
        .readTimeout(30, TimeUnit.SECONDS)
        .writeTimeout(30, TimeUnit.SECONDS)
        // Disable automatic redirect following; we validate and follow redirects manually via interceptor
        // This ensures each redirect target is validated against the YouTube allowlist
        .followRedirects(false)
        .followSslRedirects(false)
        .addInterceptor(RedirectValidationInterceptor())
        // HttpLoggingInterceptor OMITTED for Phase C
        .build()

    @Provides
    @Singleton
    fun provideNewPipeDownloader(client: OkHttpClient): Downloader =
        NewPipeDownloader(client)
}