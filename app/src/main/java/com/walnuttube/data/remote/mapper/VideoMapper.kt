package com.walnuttube.data.remote.mapper

import com.walnuttube.core.common.AppException
import com.walnuttube.data.remote.VideoSearchResult
import com.walnuttube.domain.model.Video
import com.walnuttube.domain.model.TimestampSource
import org.schabi.newpipe.extractor.stream.StreamInfoItem

/**
 * Maps NewPipeExtractor StreamInfoItem models to application domain Video models.
 *
 * Responsibilities:
 * - Validates required fields (ID must be present)
 * - Preserves nullable metadata (never fabricates missing values)
 * - Preserves approximate-date information via timestampSource/timestampApproximate
 * - Skips invalid items rather than failing entire batch
 */
class VideoMapper {

    /**
     * Maps a VideoSearchResult to a list of domain Videos.
     * Invalid items (missing ID) are skipped.
     */
    fun map(result: VideoSearchResult): List<Video> {
        return result.items.mapNotNull { item ->
            try {
                mapItem(item)
            } catch (e: Exception) {
                // Skip invalid item rather than failing entire result
                null
            }
        }
    }

    private fun mapItem(item: StreamInfoItem): Video {
        // Required: video URL - reject if missing (cannot extract ID without URL)
        val url = item.url
            ?: throw AppException.UnsupportedContentException("Video item missing URL")

        // Required: video ID - reject if extraction fails
        val id = extractVideoId(url)
            .takeIf { it.isNotBlank() }
            ?: throw AppException.UnsupportedContentException("Video item missing required ID")

        // Extract thumbnail URL (best available)
        val thumbnailUrl = item.thumbnails?.firstOrNull()?.url

        // Duration: NewPipe returns seconds, 0 means unknown
        val durationSeconds = if (item.duration > 0) item.duration.toLong() else null

        // View count: 0 means unknown
        val viewCount = if (item.viewCount > 0) item.viewCount else null

        return Video.fromStreamInfoItem(
            id = id,
            title = item.name?.takeIf { it.isNotBlank() },
            url = item.url?.takeIf { it.isNotBlank() },
            thumbnailUrl = thumbnailUrl?.takeIf { it.isNotBlank() },
            uploaderName = item.uploaderName?.takeIf { it.isNotBlank() },
            uploaderUrl = item.uploaderUrl?.takeIf { it.isNotBlank() },
            durationSeconds = durationSeconds,
            viewCount = viewCount,
            uploadDate = item.uploadDate
        )
    }

    /**
     * Extracts YouTube video ID from a URL using NewPipe's utility.
     * Supports various YouTube URL formats (watch, shorts, embed, etc.).
     */
    private fun extractVideoId(url: String): String {
        // Use NewPipe's StreamLinkHandlerFactory for reliable ID extraction
        val factory = org.schabi.newpipe.extractor.services.youtube.linkHandler.YoutubeStreamLinkHandlerFactory.getInstance()
        return try {
            factory.getId(url)
        } catch (e: Exception) {
            // Fallback: best effort for common formats
            extractVideoIdFallback(url)
        }
    }

    private fun extractVideoIdFallback(url: String): String {
        // Handle common YouTube URL patterns
        val patterns = listOf(
            "v=",           // watch?v=ID
            "shorts/",      // shorts/ID
            "embed/",       // embed/ID
            "youtu.be/"     // youtu.be/ID
        )

        for (pattern in patterns) {
            val idx = url.indexOf(pattern)
            if (idx >= 0) {
                val start = idx + pattern.length
                val end = findEndOfVideoId(url, start)
                val candidate = url.substring(start, end)
                if (candidate.length == 11 && candidate.all { it.isLetterOrDigit() || it in "-_" }) {
                    return candidate
                }
            }
        }

        // Last resort: check if the URL itself is just an ID
        if (url.length == 11 && url.all { it.isLetterOrDigit() || it in "-_" }) {
            return url
        }

        return ""
    }

    private fun findEndOfVideoId(url: String, start: Int): Int {
        for (i in start until url.length) {
            val c = url[i]
            if (c in "&?#/") {
                return i
            }
        }
        return url.length
    }
}