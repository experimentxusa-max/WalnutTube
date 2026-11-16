package com.walnuttube.core.common

import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertSame
import org.junit.Assert.assertTrue
import org.junit.Test
import org.schabi.newpipe.extractor.exceptions.ContentNotSupportedException
import org.schabi.newpipe.extractor.exceptions.UnsupportedContentInCountryException
import org.schabi.newpipe.extractor.exceptions.AgeRestrictedContentException
import org.schabi.newpipe.extractor.exceptions.PrivateContentException
import org.schabi.newpipe.extractor.exceptions.PaidContentException
import org.schabi.newpipe.extractor.exceptions.YoutubeMusicPremiumContentException
import org.schabi.newpipe.extractor.exceptions.GeographicRestrictionException
import org.schabi.newpipe.extractor.exceptions.ContentNotAvailableException
import org.schabi.newpipe.extractor.exceptions.HttpResponseException
import org.schabi.newpipe.extractor.exceptions.ReCaptchaException
import org.schabi.newpipe.extractor.exceptions.ParsingException
import org.schabi.newpipe.extractor.search.SearchExtractor
import org.schabi.newpipe.extractor.exceptions.ExtractionException
import java.io.IOException

class AppExceptionTest {

    @Test
    fun hierarchy_allSubtypesAreAppException() {
        assertTrue(AppException.NetworkException("msg") is AppException)
        assertTrue(AppException.ExtractionException("msg") is AppException)
        assertTrue(AppException.UnsupportedContentException("msg") is AppException)
        assertTrue(AppException.InvalidInputException("msg") is AppException)
        assertTrue(AppException.UnknownException("msg") is AppException)
    }

    @Test
    fun cause_preserved_inAllSubtypes() {
        val cause = IOException("root cause")
        assertEquals(cause, AppException.NetworkException("msg", cause).cause)
        assertEquals(cause, AppException.ExtractionException("msg", cause).cause)
        assertEquals(cause, AppException.UnsupportedContentException("msg", cause).cause)
        assertEquals(cause, AppException.InvalidInputException("msg", cause).cause)
        assertEquals(cause, AppException.UnknownException("msg", cause).cause)
    }

    @Test
    fun message_preserved_inAllSubtypes() {
        val msg = "test message"
        assertEquals(msg, AppException.NetworkException(msg).message)
        assertEquals(msg, AppException.ExtractionException(msg).message)
        assertEquals(msg, AppException.UnsupportedContentException(msg).message)
        assertEquals(msg, AppException.InvalidInputException(msg).message)
        assertEquals(msg, AppException.UnknownException(msg).message)
    }

    @Test
    fun classify_cancellation_throws() {
        val cancellation = kotlinx.coroutines.CancellationException("cancelled")
        try {
            AppException.fromNewPipeException(cancellation)
            assertTrue("Expected CancellationException to be thrown", false)
        } catch (e: Throwable) {
            assertSame(cancellation, e)
        }
    }

    @Test
    fun classify_alreadyAppException_returnsSame() {
        val existing = AppException.NetworkException("already classified")
        val result = AppException.fromNewPipeException(existing)
        assertSame(existing, result)
    }

    @Test
    fun classify_contentNotSupported() {
        val input = ContentNotSupportedException("not supported")
        val result = AppException.fromNewPipeException(input)
        assertTrue(result is AppException.UnsupportedContentException)
        assertEquals("not supported", result.message)
        assertSame(input, result.cause)
    }

    @Test
    fun classify_unsupportedContentInCountry() {
        val input = UnsupportedContentInCountryException("not in country")
        val result = AppException.fromNewPipeException(input)
        assertTrue(result is AppException.UnsupportedContentException)
        assertEquals("not in country", result.message)
    }

    @Test
    fun classify_ageRestricted() {
        val input = AgeRestrictedContentException("age restricted")
        val result = AppException.fromNewPipeException(input)
        assertTrue(result is AppException.UnsupportedContentException)
    }

    @Test
    fun classify_privateContent() {
        val input = PrivateContentException("private")
        val result = AppException.fromNewPipeException(input)
        assertTrue(result is AppException.UnsupportedContentException)
    }

    @Test
    fun classify_paidContent() {
        val input = PaidContentException("paid")
        val result = AppException.fromNewPipeException(input)
        assertTrue(result is AppException.UnsupportedContentException)
    }

    @Test
    fun classify_youtubeMusicPremium_noArgs() {
        val input = YoutubeMusicPremiumContentException()
        val result = AppException.fromNewPipeException(input)
        assertTrue(result is AppException.UnsupportedContentException)
    }

    @Test
    fun classify_youtubeMusicPremium_withCause() {
        val cause = IOException("cause")
        val input = YoutubeMusicPremiumContentException(cause)
        val result = AppException.fromNewPipeException(input)
        assertTrue(result is AppException.UnsupportedContentException)
        // YoutubeMusicPremiumContentException uses default message, but cause is preserved in mapping
        assertSame(cause, result.cause)
    }

    @Test
    fun classify_geographicRestriction_beforeParent() {
        val input = GeographicRestrictionException("geo blocked")
        val result = AppException.fromNewPipeException(input)
        assertTrue(result is AppException.NetworkException)
        assertEquals("geo blocked", result.message)
    }

    @Test
    fun classify_contentNotAvailable() {
        val input = ContentNotAvailableException("not available")
        val result = AppException.fromNewPipeException(input)
        assertTrue(result is AppException.UnsupportedContentException)
    }

    @Test
    fun classify_httpResponse_string() {
        val input = HttpResponseException("http error")
        val result = AppException.fromNewPipeException(input)
        assertTrue(result is AppException.NetworkException)
    }

    @Test
    fun classify_reCaptcha() {
        val input = ReCaptchaException("url", "captcha required")
        val result = AppException.fromNewPipeException(input)
        assertTrue(result is AppException.NetworkException)
    }

    @Test
    fun classify_parsing() {
        val input = ParsingException("parse failed")
        val result = AppException.fromNewPipeException(input)
        assertTrue(result is AppException.ExtractionException)
    }

    @Test
    fun classify_nothingFound() {
        val input = SearchExtractor.NothingFoundException("no results")
        val result = AppException.fromNewPipeException(input)
        assertTrue(result is AppException.ExtractionException)
    }

    @Test
    fun classify_io() {
        val input = IOException("network down")
        val result = AppException.fromNewPipeException(input)
        assertTrue(result is AppException.NetworkException)
    }

    @Test
    fun classify_baseExtraction() {
        val input = ExtractionException("extraction failed")
        val result = AppException.fromNewPipeException(input)
        assertTrue(result is AppException.ExtractionException)
    }

    @Test
    fun classify_unknownRuntimeException_throws() {
        val input = RuntimeException("programming bug")
        try {
            AppException.fromNewPipeException(input)
            assertTrue("Expected RuntimeException to be thrown", false)
        } catch (e: Throwable) {
            assertSame(input, e)
        }
    }

    @Test
    fun classify_unknownError_throws() {
        val input = Error("crash")
        try {
            AppException.fromNewPipeException(input)
            assertTrue("Expected Error to be thrown", false)
        } catch (e: Throwable) {
            assertSame(input, e)
        }
    }
}