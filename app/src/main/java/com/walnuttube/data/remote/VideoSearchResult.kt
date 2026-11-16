package com.walnuttube.data.remote

import org.schabi.newpipe.extractor.stream.StreamInfoItem

/**
 * Data-layer wrapper for YouTube search extraction results.
 * Confines NewPipeExtractor types to the data layer.
 * Includes pagination metadata for potential future use.
 */
data class VideoSearchResult(
    /** Extracted stream info items (videos only, filtered from InfoItemsPage). */
    val items: List<StreamInfoItem>,

    /** Whether a next page of results is available. */
    val hasNextPage: Boolean,

    /** Token or URL for the next page, if available. Null if not provided. */
    val nextPageToken: String?
)