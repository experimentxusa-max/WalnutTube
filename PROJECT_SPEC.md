WalnutTube — Project Specification

Project name: WalnutTube
Platform: Android
Primary language: Kotlin
UI framework: Jetpack Compose
Project status: Initial specification
Development approach: Incremental, modular, guest-first

---

1. Project Vision

WalnutTube is an Android alternative YouTube client focused on user control, privacy, customisation, and a chronological subscription feed.

The application should provide a familiar YouTube-like experience without depending on a Google account for its core functionality.

The application should prioritise:

- Guest-first operation.
- Subscription-based content discovery.
- Chronological video feeds.
- Background playback.
- Local data storage.
- Customisable playback and interface settings.
- Minimal tracking and unnecessary dependencies.
- No algorithmic recommendations in the default Home feed.

The application should be designed so that its core features remain useful even if optional account integrations are unavailable.

2. Core Product Rules

These requirements are mandatory.

2.1 Guest-first operation

Users must be able to use the application without signing into Google.

Guest users should be able to:

- Search for videos and channels.
- Watch videos.
- Subscribe to channels locally.
- Maintain watch history.
- Create local playlists.
- Save videos for later.
- Configure playback and interface settings.
- Use supported background playback features.

Local functionality must not be presented as synchronised YouTube functionality.

2.2 Home feed

The default Home feed must contain videos from locally subscribed channels only.

Feed rules:

1. Fetch uploads from subscribed channels.
2. Prefer verified publication timestamps.
3. Sort videos from newest to oldest.
4. Display older videos further down the feed.
5. Deduplicate videos.
6. Paginate results.
7. Cache results locally.
8. Retain videos whose timestamps are unknown.

The default feed must NOT include:

- Shorts.
- Videos from unsubscribed channels.
- Trending videos.
- Algorithmic recommendations.
- Unrelated promotional recommendations.

Do not silently change these rules.

2.3 Shorts

Shorts are outside the initial project scope.

Do not create a Shorts tab, Shorts feed, or Shorts recommendation system.

2.4 Optional accounts

Account integration is a separate feature.

Never assume that Google authentication automatically provides access to YouTube subscriptions, likes, comments, or other YouTube account functionality.

Guest mode must continue working independently.

---

3. Technology Stack

Use the following technologies unless a technical limitation justifies a documented change.

Component| Technology
Main language| Kotlin
UI| Jetpack Compose
Android build| Gradle
Video extraction| NewPipeExtractor
Video playback| AndroidX Media3 ExoPlayer
Local database| Room / SQLite
Settings storage| DataStore
Image loading| Coil
Background playback| MediaSession / Media3
Networking| OkHttp or another appropriate HTTP client
Asynchronous operations| Kotlin Coroutines and Flow
Dependency injection| Hilt or another justified solution
Testing| JUnit and AndroidX testing tools

Use stable, compatible dependency versions.

Do not blindly use outdated dependency versions from tutorials. Check official documentation and compatibility before adding dependencies.

C++/NDK is optional. Do not introduce it unless a genuine native-code requirement exists.

---

4. Architecture

Use a layered architecture.

Presentation layer

Responsible for:

- Compose screens.
- Navigation.
- ViewModels.
- UI state.
- User interactions.

Domain layer

Responsible for:

- Business logic.
- Use cases.
- Feed sorting.
- Subscription rules.
- Repository interfaces.
- Application-level models.

Data layer

Responsible for:

- Local database access.
- Network access.
- Content extraction.
- Repository implementations.
- Caching and persistence.

Core components

"core/extractor"

Integrates NewPipeExtractor.

Responsibilities:

- Fetch video information.
- Fetch channel information.
- Search for videos and channels.
- Retrieve channel uploads.
- Retrieve available stream information.
- Handle extraction failures.

NewPipeExtractor must not directly control the UI.

"core/youtube-web"

A separate, isolated component for optional YouTube web metadata parsing.

Primary purpose:

- Verify precise publication timestamps when available.
- Extract metadata not reliably provided by the primary extractor.

Requirements:

- Do not tightly couple it to NewPipeExtractor.
- Handle missing or changed page structures.
- Never fabricate publication dates.
- Cache successfully verified timestamps.
- Provide fallback behaviour when parsing fails.

YouTube's web interface is not a guaranteed stable API. Exact timestamps may not always be obtainable. The application must work without this component.

Review applicable platform terms and technical restrictions before implementing extraction or automated requests.

"core/player"

Responsible for:

- Media3 ExoPlayer.
- Playback state.
- Stream selection.
- Playback speed.
- Audio-only playback.
- Subtitles.
- Background playback.
- Picture-in-Picture.
- Playback queues.

"core/database"

Responsible for:

- Room database.
- Entities.
- DAOs.
- Database migrations.
- Local persistence.

"core/network"

Responsible for:

- HTTP clients.
- Network configuration.
- Connectivity handling.
- Request policies.
- Error handling.

---

5. Home Feed Architecture

The feed pipeline:

"Subscriptions → Channel uploads → Timestamp verification → Normalisation → Deduplication → Sorting → Local cache → UI"

Timestamp priority

Use the following order:

1. Verified publication timestamp from the isolated web metadata component.
2. Publication timestamp provided by NewPipeExtractor.
3. Unknown timestamp.

Never invent a timestamp.

Unknown timestamps

Videos without timestamps must remain available.

They should be placed after videos with known timestamps unless a better reliable ordering signal is available.

Feed requirements

- Newest-first ordering.
- Pagination.
- Refresh support.
- Duplicate prevention.
- Local caching.
- Network failure handling.
- Sensible retry and rate-limiting behaviour.

Avoid repeatedly fetching the same channel data unnecessarily.

---

6. Application Features

6.1 Home

- Chronological subscription feed.
- Pull-to-refresh.
- Pagination.
- Video thumbnails.
- Channel information.
- Upload timestamps.
- Loading and error states.

6.2 Search

- Video search.
- Channel search.
- Playlist search where supported.
- Search suggestions where available.
- Search history.
- Search filters where supported.

6.3 Subscriptions

- Subscribe and unsubscribe locally.
- Display subscribed channels.
- Fetch channel uploads.
- Sort subscriptions.
- Manage subscriptions.

Local subscriptions must work without a Google account.

6.4 Channel pages

- Channel name and description.
- Avatar and banner where available.
- Subscriber count where available.
- Channel uploads.
- Available channel tabs and information.

6.5 Watch screen

- Video playback.
- Title and description.
- Channel information.
- Related information where appropriate.
- Comments where technically supported.
- Playback controls.
- Quality selection.
- Playback speed.
- Subtitle selection.
- Queue management.

6.6 Player

Use Media3 ExoPlayer.

Required goals:

- Standard playback.
- Background playback.
- Notification controls.
- Picture-in-Picture.
- Playback speed.
- Quality selection.
- Subtitle support.
- Audio-only mode.
- Queue support.
- Proper lifecycle management.

Do not store extracted stream URLs as permanent video identifiers. Stream URLs can expire. Store stable video identifiers and retrieve fresh stream information when necessary.

6.7 Library

Include:

- Watch history.
- Watch later.
- Local playlists.
- Favourites.
- Saved videos.
- Downloaded videos.
- Playback progress.

6.8 Downloads

Provide a download manager for content that can lawfully be downloaded.

Requirements:

- Download progress.
- Pause and resume where supported.
- Cancellation.
- Storage management.
- Offline library.
- Error handling.

Do not bypass DRM or access restrictions.

6.9 Social features

Investigate support for:

- Comments.
- Comment replies.
- Likes and dislikes.
- Sharing.
- Reporting.

Clearly distinguish between actions performed locally and actions actually submitted to YouTube.

If an action cannot be performed through a supported mechanism, do not display a fake success state.

6.10 Settings

Include:

- Theme selection.
- Dynamic colour where supported.
- Playback preferences.
- Default video quality.
- Default playback speed.
- Background playback preferences.
- Download preferences.
- Privacy settings.
- Search history controls.
- Cache management.
- Import/export.

---

7. Local Database Design

Use Room.

Suggested entities:

Video

- "videoId"
- "title"
- "description"
- "duration"
- "thumbnailUrl"
- "channelId"
- "publishedAt"
- "timestampSource"
- "lastUpdated"

Channel

- "channelId"
- "name"
- "description"
- "avatarUrl"
- "subscriberCount"
- "lastUpdated"

Subscription

- "channelId"
- "subscribedAt"

WatchHistory

- "videoId"
- "position"
- "duration"
- "lastWatchedAt"

Playlist

- "playlistId"
- "name"
- "createdAt"
- "updatedAt"

PlaylistVideo

- "playlistId"
- "videoId"
- "position"

SavedVideo

- "videoId"
- "savedAt"
- "category"

SearchHistory

- "query"
- "searchedAt"

PlaybackSettings

- "defaultQuality"
- "playbackSpeed"
- "audioOnly"
- "backgroundPlayback"

Account

- Local account metadata.
- Optional external account metadata.
- Authentication state where applicable.

Use stable IDs and appropriate indexes.

Do not store temporary playback URLs as permanent video identifiers.

Implement database migrations rather than destructively recreating the database when the schema changes.

---

8. Suggested Project Structure

WalnutTube/
├── app/
│   ├── MainActivity.kt
│   └── navigation/
│
├── core/
│   ├── model/
│   ├── common/
│   ├── database/
│   ├── network/
│   ├── extractor/
│   ├── youtube-web/
│   └── player/
│
├── data/
│   ├── repository/
│   ├── local/
│   └── remote/
│
├── domain/
│   ├── repository/
│   └── usecase/
│
├── feature/
│   ├── home/
│   ├── search/
│   ├── subscriptions/
│   ├── channel/
│   ├── watch/
│   ├── library/
│   ├── comments/
│   ├── downloads/
│   ├── account/
│   └── settings/
│
└── service/
    └── PlaybackService.kt

This is a logical organisation, not a requirement to create every Gradle module immediately.

Start with a manageable number of modules and split them when doing so provides a real benefit.

---

9. Development Milestones

Development must be incremental.

Milestone 1 — Foundation

- Create the Kotlin Android project.
- Configure Gradle.
- Set up Compose.
- Establish navigation.
- Configure dependency management.
- Integrate NewPipeExtractor.
- Implement basic error handling.

Completion condition: The application launches and can perform a basic extraction request.

Milestone 2 — Core experience

- Home screen.
- Search screen.
- Channel screen.
- Video details.
- Basic video playback.

Completion condition: A user can search for a video, open it, and watch it.

Milestone 3 — Personal library

- Local subscriptions.
- Chronological subscription feed.
- Watch history.
- Watch later.
- Local playlists.
- Persistent settings.

Completion condition: Core functionality works without a Google account and survives application restarts.

Milestone 4 — Advanced playback

- Background playback.
- Notification controls.
- Picture-in-Picture.
- Playback speed.
- Quality selection.
- Subtitles.
- Audio-only mode.
- Downloads.

Completion condition: Playback works reliably across screen changes, backgrounding, and supported device states.

Milestone 5 — Social features

- Comments.
- Replies.
- Likes and dislikes.
- Sharing.
- Other supported interactions.

Completion condition: Each feature accurately communicates whether an action is local or remotely supported.

Milestone 6 — Advanced integration

- Optional account integration.
- Import/export.
- Notifications.
- Synchronisation where feasible.
- Performance optimisation.
- Privacy improvements.

Completion condition: Optional integrations do not compromise guest mode.

---

10. Engineering Rules for AI-Assisted Development

Claude Code must follow these rules.

1. Read this specification before making architectural changes.
2. Inspect the existing project before modifying files.
3. Do not assume that a feature is already implemented.
4. Do not rewrite working code without a technical reason.
5. Work on one milestone or clearly scoped task at a time.
6. Build after meaningful changes.
7. Run relevant tests.
8. Fix compilation errors before adding more features.
9. Avoid unnecessary dependencies.
10. Avoid duplicated business logic.
11. Keep UI code separate from extraction and networking logic.
12. Handle network errors and empty states.
13. Never fabricate video metadata, timestamps, or successful remote actions.
14. Document architectural decisions.
15. Update this specification when an approved requirement changes.

Before implementing a task

Claude Code should:

- Inspect relevant files.
- Explain the implementation plan briefly.
- Identify affected components.
- Identify risks or missing information.
- Implement only the agreed scope.
- Build and test the result.
- Report changed files and remaining issues.

Do not generate the entire application in one enormous operation.

---

11. Testing Requirements

At minimum, test:

- Video extraction success and failure.
- Search results.
- Channel upload retrieval.
- Timestamp parsing and fallback.
- Feed sorting.
- Duplicate removal.
- Unknown timestamp handling.
- Database persistence.
- Database migrations.
- Subscription management.
- Playback state handling.
- Network disconnection.
- Empty states.
- Application restart behaviour.

The feed must always sort known timestamps correctly, even when some videos have unknown timestamps.

---

12. Non-Goals and Restrictions

The following are not part of the initial implementation:

- Shorts.
- Algorithmic Home recommendations.
- Mandatory Google login.
- A fake YouTube account system.
- Fabricated comments, likes, or remote interactions.
- Permanent storage of temporary stream URLs.
- DRM circumvention.

Do not claim full YouTube feature parity until each feature has been implemented and tested.

---

13. Definition of Success

WalnutTube's first meaningful release should allow a guest user to:

1. Open the application.
2. Search for a video or channel.
3. Watch a video.
4. Subscribe to a channel locally.
5. See uploads from subscribed channels in chronological order.
6. Save videos for later.
7. Maintain watch history.
8. Continue using the application without signing into Google.

Advanced features should build upon this stable foundation.

Primary principle: A reliable, functional application is more important than a large collection of incomplete features.