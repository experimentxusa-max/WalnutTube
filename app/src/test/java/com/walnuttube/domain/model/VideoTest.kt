package com.walnuttube.domain.model

import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Test

class VideoTest {

    @Test
    fun constructor_allFieldsPresent() {
        val video = Video(
            id = "abc123",
            title = "Test Video",
            url = "https://youtube.com/watch?v=abc123",
            thumbnailUrl = "https://img.youtube.com/vi/abc123/default.jpg",
            uploaderName = "Test Channel",
            uploaderUrl = "https://youtube.com/channel/test",
            durationSeconds = 120L,
            viewCount = 1000L,
            publishedAt = 1700000000L,
            timestampSource = TimestampSource.WEB_VERIFIED,
            timestampApproximate = false
        )

        assertEquals("abc123", video.id)
        assertEquals("Test Video", video.title)
        assertEquals("https://youtube.com/watch?v=abc123", video.url)
        assertEquals("https://img.youtube.com/vi/abc123/default.jpg", video.thumbnailUrl)
        assertEquals("Test Channel", video.uploaderName)
        assertEquals("https://youtube.com/channel/test", video.uploaderUrl)
        assertEquals(120L, video.durationSeconds)
        assertEquals(1000L, video.viewCount)
        assertEquals(1700000000L, video.publishedAt)
        assertEquals(TimestampSource.WEB_VERIFIED, video.timestampSource)
        assertEquals(false, video.timestampApproximate)
    }

    @Test
    fun constructor_nullableFieldsCanBeNull() {
        val video = Video(
            id = "minimal",
            title = null,
            url = null,
            thumbnailUrl = null,
            uploaderName = null,
            uploaderUrl = null,
            durationSeconds = null,
            viewCount = null,
            publishedAt = null,
            timestampSource = TimestampSource.UNKNOWN,
            timestampApproximate = false
        )

        assertEquals("minimal", video.id)
        assertNull(video.title)
        assertNull(video.url)
        assertNull(video.thumbnailUrl)
        assertNull(video.uploaderName)
        assertNull(video.uploaderUrl)
        assertNull(video.durationSeconds)
        assertNull(video.viewCount)
        assertNull(video.publishedAt)
        assertEquals(TimestampSource.UNKNOWN, video.timestampSource)
    }

    @Test
    fun timestampSource_enumValues() {
        assertEquals(3, TimestampSource.values().size)
        assertTrue(TimestampSource.values().contains(TimestampSource.WEB_VERIFIED))
        assertTrue(TimestampSource.values().contains(TimestampSource.EXTRACTOR_PROVIDED))
        assertTrue(TimestampSource.values().contains(TimestampSource.UNKNOWN))
    }

    @Test
    fun equals_sameId_equal() {
        val video1 = Video(
            id = "same",
            title = "Title", url = null, thumbnailUrl = null,
            uploaderName = null, uploaderUrl = null,
            durationSeconds = null, viewCount = null,
            publishedAt = null, timestampSource = TimestampSource.UNKNOWN,
            timestampApproximate = false
        )
        val video2 = Video(
            id = "same",
            title = "Title", url = null, thumbnailUrl = null,
            uploaderName = null, uploaderUrl = null,
            durationSeconds = null, viewCount = null,
            publishedAt = null, timestampSource = TimestampSource.UNKNOWN,
            timestampApproximate = false
        )

        assertEquals(video1, video2)
    }

    @Test
    fun copy_createsIndependent() {
        val original = Video(
            id = "orig", title = "Original", url = null, thumbnailUrl = null,
            uploaderName = null, uploaderUrl = null,
            durationSeconds = null, viewCount = null,
            publishedAt = null, timestampSource = TimestampSource.UNKNOWN,
            timestampApproximate = false
        )
        val copied = original.copy(title = "Modified")

        assertEquals("Original", original.title)
        assertEquals("Modified", copied.title)
        assertEquals(original.id, copied.id)
    }

    @Test
    fun toString_containsId() {
        val video = Video(
            id = "test123", title = "Test", url = null, thumbnailUrl = null,
            uploaderName = null, uploaderUrl = null,
            durationSeconds = null, viewCount = null,
            publishedAt = null, timestampSource = TimestampSource.UNKNOWN,
            timestampApproximate = false
        )
        val str = video.toString()
        assertTrue(str.contains("test123"))
    }
}