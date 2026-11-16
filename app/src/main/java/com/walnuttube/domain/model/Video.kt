package com.walnuttube.domain.model

import java.time.Instant

/**
 * Application-owned video domain model.
 * Independent of NewPipeExtractor types.
 * All metadata fields are nullable because NewPipe may not provide them.
 * Missing values are never fabricated.
 */
data class Video(
    /** Unique video identifier (e.g., YouTube video ID). Never null for valid items. */
    val id: String,

    /** Video title. Null if not provided by extractor. */
    val title: String?,

    /** Canonical video URL. Null if not provided. */
    val url: String?,

    /** Best available thumbnail URL. Null if not provided. */
    val thumbnailUrl: String?,

    /** Uploader/channel display name. Null if not provided. */
    val uploaderName: String?,

    /** Uploader/channel URL. Null if not provided. */
    val uploaderUrl: String?,

    /** Duration in seconds. Null if unknown. */
    val durationSeconds: Long?,

    /** View count. Null if unknown. */
    val viewCount: Long?,

    /** Publication timestamp as Unix epoch seconds. Null if unknown. */
    val publishedAt: Long?,

    /** Source of the timestamp for provenance tracking. */
    val timestampSource: TimestampSource,

    /** Whether the timestamp is approximate (extractor couldn't determine exact date). */
    val timestampApproximate: Boolean
) {
    /**
     * Creates a Video with derived timestamp fields from a DateWrapper.
     * Use this factory when mapping from NewPipe StreamInfoItem.
     */
    companion object {
        fun fromStreamInfoItem(
            id: String,
            title: String?,
            url: String?,
            thumbnailUrl: String?,
            uploaderName: String?,
            uploaderUrl: String?,
            durationSeconds: Long?,
            viewCount: Long?,
            uploadDate: org.schabi.newpipe.extractor.localization.DateWrapper?
        ): Video {
            val (publishedAt, timestampSource, timestampApproximate) = when (uploadDate) {
                null -> Triple(null, TimestampSource.UNKNOWN, false)
                else -> {
                    val instant = uploadDate.instant
                    val approx = uploadDate.isApproximation()
                    Triple(
                        instant.epochSecond,
                        if (approx) TimestampSource.EXTRACTOR_PROVIDED else TimestampSource.WEB_VERIFIED,
                        approx
                    )
                }
            }
            return Video(
                id = id,
                title = title,
                url = url,
                thumbnailUrl = thumbnailUrl,
                uploaderName = uploaderName,
                uploaderUrl = uploaderUrl,
                durationSeconds = durationSeconds,
                viewCount = viewCount,
                publishedAt = publishedAt,
                timestampSource = timestampSource,
                timestampApproximate = timestampApproximate
            )
        }
    }
}

/**
 * Provenance of the publication timestamp.
 */
enum class TimestampSource {
    /** Exact timestamp verified from the source page (e.g., YouTube watch page). */
    WEB_VERIFIED,

    /** Timestamp provided by the extractor but marked as approximate. */
    EXTRACTOR_PROVIDED,

    /** No timestamp available. */
    UNKNOWN
}