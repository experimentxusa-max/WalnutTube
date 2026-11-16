package com.walnuttube.data.remote

import com.walnuttube.core.common.AppException
import com.walnuttube.core.common.AppResult
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.schabi.newpipe.extractor.NewPipe
import org.schabi.newpipe.extractor.ServiceList
import org.schabi.newpipe.extractor.linkhandler.SearchQueryHandlerFactory
import org.schabi.newpipe.extractor.search.SearchExtractor
import org.schabi.newpipe.extractor.services.youtube.extractors.YoutubeSearchExtractor
import org.schabi.newpipe.extractor.services.youtube.linkHandler.YoutubeSearchQueryHandlerFactory
import org.schabi.newpipe.extractor.stream.StreamInfoItem

/**
 * Wrapper that isolates NewPipeExtractor YouTube operations from the rest of the application.
 *
 * Responsibilities:
 * - Constructs search queries using NewPipe's factory/handler/extractor chain
 * - Executes blocking extraction on Dispatchers.IO
 * - Classifies NewPipe exceptions into AppException hierarchy
 * - Returns data-layer result types (no NewPipe types escape to domain layer)
 *
 * Threading: All public methods are suspend functions that execute on Dispatchers.IO.
 * Callers (repository, use cases) should NOT add additional withContext(Dispatchers.IO).
 */
class YoutubeServiceWrapper {

    /**
     * Searches YouTube for videos matching the query.
     *
     * @param query Search query string (must not be blank).
     * @return AppResult containing VideoSearchResult on success, or AppException on failure.
     */
    suspend fun searchVideos(query: String): AppResult<VideoSearchResult> {
        // Input validation
        if (query.trim().isBlank()) {
            return AppResult.Error(AppException.InvalidInputException("Search query must not be blank"))
        }

        return withContext(Dispatchers.IO) {
            try {
                // Get the YouTube service (already a YoutubeService instance, no cast needed)
                val youtubeService = ServiceList.YouTube

                // Create search query handler using the factory
                val factory: SearchQueryHandlerFactory = YoutubeSearchQueryHandlerFactory.getInstance()
                val handler = factory.fromQuery(query.trim())

                // Create the search extractor
                val extractor = YoutubeSearchExtractor(youtubeService, handler)

                // Execute extraction - getInitialPage() is a blocking call
                val page = extractor.getInitialPage()

                // Extract stream items from the page
                val streamItems = page.items.filterIsInstance<StreamInfoItem>()

                // Build result with pagination info
                val result = VideoSearchResult(
                    items = streamItems,
                    hasNextPage = page.hasNextPage(),
                    nextPageToken = page.nextPage?.toString()
                )

                AppResult.Success(result)

            } catch (e: Throwable) {
                // Classification happens in AppException.fromNewPipeException
                // CancellationException is rethrown there
                AppResult.Error(AppException.fromNewPipeException(e))
            }
        }
    }
}