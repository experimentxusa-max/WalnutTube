package com.walnuttube.data.remote.mapper

import com.walnuttube.core.common.AppException
import com.walnuttube.data.remote.VideoSearchResult
import com.walnuttube.domain.model.Video
import com.walnuttube.domain.model.TimestampSource
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Test
import org.schabi.newpipe.extractor.Image
import org.schabi.newpipe.extractor.stream.StreamInfoItem
import org.schabi.newpipe.extractor.stream.StreamType
import org.schabi.newpipe.extractor.localization.DateWrapper
import java.time.Instant

class VideoMapperTest {

    private val mapper = VideoMapper()

    @Test
    fun map_validItem_mapsAllFields() {
        val item = createValidStreamInfoItem()
        val result = VideoSearchResult(listOf(item), false, null)

        val videos = mapper.map(result)

        assertEquals(1, videos.size)
        val video = videos[0]
        assertEquals("dQw4w9WgXcQ", video.id)
        assertEquals("Never Gonna Give You Up", video.title)
        assertEquals("https://www.youtube.com/watch?v=dQw4w9WgXcQ", video.url)
        assertNotNull(video.thumbnailUrl)
        assertEquals("Rick Astley", video.uploaderName)
        assertEquals("https://www.youtube.com/channel/UCuAXFkgsw1L7xaCfnd5JJOw", video.uploaderUrl)
        assertEquals(212L, video.durationSeconds)
        assertEquals(1500000000L, video.viewCount)
        // Instant.parse("2024-01-15T10:30:00Z").epochSecond = 1705314600
        assertEquals(1705314600L, video.publishedAt)
        assertEquals(TimestampSource.WEB_VERIFIED, video.timestampSource)
        assertEquals(false, video.timestampApproximate)
    }

    @Test
    fun map_missingTitle_preservesNull() {
        val item = createValidStreamInfoItemWithMissingTitle()
        val result = VideoSearchResult(listOf(item), false, null)

        val videos = mapper.map(result)

        assertEquals(1, videos.size)
        assertNull(videos[0].title)
    }

    @Test
    fun map_missingUrl_throwsAndSkipped() {
        // StreamInfoItem doesn't have setUrl() - url is from parent InfoItem and is immutable
        // We can't create an item with null URL via public API, so we test via invalid ID path
        val item = createItemWithInvalidUrl()
        val result = VideoSearchResult(listOf(item), false, null)

        val videos = mapper.map(result)

        assertEquals(0, videos.size)
    }

    @Test
    fun map_missingThumbnail_null() {
        val item = createValidStreamInfoItem().apply { thumbnails = emptyList() }
        val result = VideoSearchResult(listOf(item), false, null)

        val videos = mapper.map(result)

        assertEquals(1, videos.size)
        assertNull(videos[0].thumbnailUrl)
    }

    @Test
    fun map_missingUploader_null() {
        val item = createValidStreamInfoItem().apply {
            uploaderName = null
            uploaderUrl = null
        }
        val result = VideoSearchResult(listOf(item), false, null)

        val videos = mapper.map(result)

        assertEquals(1, videos.size)
        assertNull(videos[0].uploaderName)
        assertNull(videos[0].uploaderUrl)
    }

    @Test
    fun map_zeroDuration_null() {
        val item = createValidStreamInfoItem().apply { duration = 0 }
        val result = VideoSearchResult(listOf(item), false, null)

        val videos = mapper.map(result)

        assertEquals(1, videos.size)
        assertNull(videos[0].durationSeconds)
    }

    @Test
    fun map_zeroViewCount_null() {
        val item = createValidStreamInfoItem().apply { viewCount = 0 }
        val result = VideoSearchResult(listOf(item), false, null)

        val videos = mapper.map(result)

        assertEquals(1, videos.size)
        assertNull(videos[0].viewCount)
    }

    @Test
    fun map_approximateDate_timestampSourceExtractorProvided() {
        val instant = Instant.parse("2024-01-15T10:30:00Z")
        val item = createValidStreamInfoItem().apply {
            uploadDate = DateWrapper(instant, true)
        }
        val result = VideoSearchResult(listOf(item), false, null)

        val videos = mapper.map(result)

        assertEquals(1, videos.size)
        assertEquals(TimestampSource.EXTRACTOR_PROVIDED, videos[0].timestampSource)
        assertEquals(true, videos[0].timestampApproximate)
        assertEquals(instant.epochSecond, videos[0].publishedAt)
    }

    @Test
    fun map_exactDate_timestampSourceWebVerified() {
        val instant = Instant.parse("2024-01-15T10:30:00Z")
        val item = createValidStreamInfoItem().apply {
            uploadDate = DateWrapper(instant, false)
        }
        val result = VideoSearchResult(listOf(item), false, null)

        val videos = mapper.map(result)

        assertEquals(1, videos.size)
        assertEquals(TimestampSource.WEB_VERIFIED, videos[0].timestampSource)
        assertEquals(false, videos[0].timestampApproximate)
        assertEquals(instant.epochSecond, videos[0].publishedAt)
    }

    @Test
    fun map_noUploadDate_timestampSourceUnknown() {
        val item = createValidStreamInfoItem().apply { uploadDate = null }
        val result = VideoSearchResult(listOf(item), false, null)

        val videos = mapper.map(result)

        assertEquals(1, videos.size)
        assertEquals(TimestampSource.UNKNOWN, videos[0].timestampSource)
        assertEquals(false, videos[0].timestampApproximate)
        assertNull(videos[0].publishedAt)
    }

    @Test
    fun map_shortFormContent_included() {
        val item = createValidStreamInfoItem().apply { isShortFormContent = true }
        val result = VideoSearchResult(listOf(item), false, null)

        val videos = mapper.map(result)

        assertEquals(1, videos.size)
    }

    @Test
    fun map_invalidIdFromExtractor_skipped() {
        val item = createItemWithInvalidUrl()
        val result = VideoSearchResult(listOf(item), false, null)

        val videos = mapper.map(result)

        assertEquals(0, videos.size)
    }

    @Test
    fun map_multipleItems_invalidSkipped() {
        val validItem = createValidStreamInfoItem()
        val invalidItem = createItemWithInvalidUrl()
        val result = VideoSearchResult(listOf(validItem, invalidItem), false, null)

        val videos = mapper.map(result)

        assertEquals(1, videos.size)
        assertEquals("dQw4w9WgXcQ", videos[0].id)
    }

    @Test
    fun map_emptyResult_returnsEmpty() {
        val result = VideoSearchResult(emptyList(), false, null)

        val videos = mapper.map(result)

        assertEquals(0, videos.size)
        assertTrue(videos.isEmpty())
    }

    private fun createValidStreamInfoItem(): StreamInfoItem {
        // StreamInfoItem constructor: (serviceId, url, name, streamType)
        val item = StreamInfoItem(0,
            "https://www.youtube.com/watch?v=dQw4w9WgXcQ",
            "Never Gonna Give You Up",
            StreamType.VIDEO_STREAM)
        item.uploaderName = "Rick Astley"
        item.uploaderUrl = "https://www.youtube.com/channel/UCuAXFkgsw1L7xaCfnd5JJOw"
        item.duration = 212
        item.viewCount = 1500000000
        item.thumbnails = listOf(Image("https://img.youtube.com/vi/dQw4w9WgXcQ/default.jpg", 120, 90, Image.ResolutionLevel.UNKNOWN))
        item.uploadDate = DateWrapper(Instant.parse("2024-01-15T10:30:00Z"), false)
        return item
    }

    private fun createValidStreamInfoItemWithMissingTitle(): StreamInfoItem {
        // Create with empty name to simulate missing title
        val item = StreamInfoItem(0,
            "https://www.youtube.com/watch?v=dQw4w9WgXcQ",
            "",  // empty name
            StreamType.VIDEO_STREAM)
        item.uploaderName = "Rick Astley"
        item.uploaderUrl = "https://www.youtube.com/channel/UCuAXFkgsw1L7xaCfnd5JJOw"
        item.duration = 212
        item.viewCount = 1500000000
        item.thumbnails = listOf(Image("https://img.youtube.com/vi/dQw4w9WgXcQ/default.jpg", 120, 90, Image.ResolutionLevel.UNKNOWN))
        item.uploadDate = DateWrapper(Instant.parse("2024-01-15T10:30:00Z"), false)
        return item
    }

    private fun createItemWithInvalidUrl(): StreamInfoItem {
        // URL that won't match any YouTube pattern
        val item = StreamInfoItem(0,
            "https://example.com/not-youtube",
            "Invalid Video",
            StreamType.VIDEO_STREAM)
        item.uploaderName = "Test"
        item.duration = 100
        item.viewCount = 1000
        item.thumbnails = listOf(Image("https://example.com/thumb.jpg", 120, 90, Image.ResolutionLevel.UNKNOWN))
        item.uploadDate = DateWrapper(Instant.parse("2024-01-15T10:30:00Z"), false)
        return item
    }
}