package com.walnuttube.core.common

import kotlinx.coroutines.CancellationException

/**
 * Base class for all application-level exceptions.
 * Provides structured error classification without relying on message parsing.
 */
open class AppException(
    message: String,
    cause: Throwable? = null
) : Exception(message, cause) {

    /**
     * Network-related failures (connectivity, timeouts, HTTP errors).
     */
    class NetworkException(
        message: String,
        cause: Throwable? = null
    ) : AppException(message, cause)

    /**
     * Extraction/parsing failures from NewPipeExtractor.
     */
    class ExtractionException(
        message: String,
        cause: Throwable? = null
    ) : AppException(message, cause)

    /**
     * Content that is not supported by the extractor (age-restricted, private, paid, geo-blocked, etc.).
     * These are expected failures for specific content, not general extraction bugs.
     */
    class UnsupportedContentException(
        message: String,
        cause: Throwable? = null
    ) : AppException(message, cause)

    /**
     * Invalid input provided by the caller (e.g., blank search query).
     */
    class InvalidInputException(
        message: String,
        cause: Throwable? = null
    ) : AppException(message, cause)

    /**
     * Unexpected failures that don't fit other categories.
     * Should be rare; indicates a bug or unanticipated condition.
     */
    class UnknownException(
        message: String,
        cause: Throwable? = null
    ) : AppException(message, cause)

    companion object {
        /**
         * Classifies a NewPipeExtractor exception into the appropriate [AppException] subtype.
         * Catch order follows the verified inheritance hierarchy (most specific first).
         */
        fun fromNewPipeException(e: Throwable): AppException = when (e) {
            // Cancellation must propagate
            is CancellationException -> throw e

            // Already classified by us
            is AppException -> e

            // Unsupported content types (most specific first)
            is org.schabi.newpipe.extractor.exceptions.ContentNotSupportedException ->
                UnsupportedContentException(e.message ?: "Content not supported", e)

            is org.schabi.newpipe.extractor.exceptions.UnsupportedContentInCountryException ->
                UnsupportedContentException(e.message ?: "Content not supported in this country", e)

            is org.schabi.newpipe.extractor.exceptions.AgeRestrictedContentException ->
                UnsupportedContentException(e.message ?: "Age-restricted content", e)

            is org.schabi.newpipe.extractor.exceptions.PrivateContentException ->
                UnsupportedContentException(e.message ?: "Private content", e)

            is org.schabi.newpipe.extractor.exceptions.PaidContentException ->
                UnsupportedContentException(e.message ?: "Paid content", e)

            is org.schabi.newpipe.extractor.exceptions.YoutubeMusicPremiumContentException ->
                UnsupportedContentException(e.message ?: "YouTube Music Premium content", e.cause ?: e)

            // Geographic restriction - caught before ContentNotAvailableException (parent)
            is org.schabi.newpipe.extractor.exceptions.GeographicRestrictionException ->
                NetworkException(e.message ?: "Geographic restriction", e)

            // Content not available - parent of several specific types above
            is org.schabi.newpipe.extractor.exceptions.ContentNotAvailableException ->
                UnsupportedContentException(e.message ?: "Content not available", e)

            // HTTP/Network level failures
            is org.schabi.newpipe.extractor.exceptions.HttpResponseException ->
                NetworkException(e.message ?: "HTTP error", e)

            is org.schabi.newpipe.extractor.exceptions.ReCaptchaException ->
                NetworkException(e.message ?: "reCAPTCHA challenge", e)

            // Parsing/extraction failures
            is org.schabi.newpipe.extractor.exceptions.ParsingException ->
                ExtractionException(e.message ?: "Parsing failed", e)

            is org.schabi.newpipe.extractor.search.SearchExtractor.NothingFoundException ->
                ExtractionException(e.message ?: "No results found", e)

            // IO failures
            is java.io.IOException ->
                NetworkException(e.message ?: "Network I/O error", e)

            // Base extraction exception
            is org.schabi.newpipe.extractor.exceptions.ExtractionException ->
                ExtractionException(e.message ?: "Extraction failed", e)

            // Let programming errors propagate - do not wrap
            else -> throw e
        }
    }
}