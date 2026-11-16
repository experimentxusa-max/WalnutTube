package com.walnuttube.core.di

import com.walnuttube.data.remote.mapper.VideoMapper
import com.walnuttube.data.remote.YoutubeServiceWrapper
import org.schabi.newpipe.extractor.downloader.Downloader
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton
import org.schabi.newpipe.extractor.NewPipe
import org.schabi.newpipe.extractor.localization.ContentCountry
import org.schabi.newpipe.extractor.localization.Localization

/**
 * Hilt module for application-wide dependencies.
 *
 * Initializes NewPipeExtractor exactly once with the custom OkHttp Downloader.
 * Provides the YouTube service wrapper and video mapper as singletons.
 */
@Module
@InstallIn(SingletonComponent::class)
object AppModule {

    /**
     * Initializes NewPipeExtractor with the custom Downloader and default localization.
     * This is called exactly once by Hilt when the singleton component is created.
     *
     * @param downloader The OkHttp-backed Downloader provided by NetworkModule.
     * @return The initialized YoutubeServiceWrapper.
     */
    @Provides
    @Singleton
    fun provideYoutubeServiceWrapper(downloader: Downloader): YoutubeServiceWrapper {
        // Initialize NewPipe with custom downloader and explicit defaults
        // Using Localization.DEFAULT and ContentCountry.DEFAULT for clarity and configurability
        NewPipe.init(downloader, Localization.DEFAULT, ContentCountry.DEFAULT)
        return YoutubeServiceWrapper()
    }

    /**
     * Provides the VideoMapper for converting extractor models to domain models.
     */
    @Provides
    @Singleton
    fun provideVideoMapper(): VideoMapper = VideoMapper()
}