WalnutTube — Milestone 1: Foundation Design Document
Version: 4.0
Date: 14 November 2026
Status: Corrected Design — Ready for technical implementation review
Milestone: 1 — Foundation
1. Purpose
Milestone 1 establishes the technical foundation of WalnutTube.
The objective is to create an Android application that:
Builds successfully.
Launches successfully.
Uses Kotlin and Jetpack Compose.
Uses Hilt for dependency injection.
Initializes NewPipeExtractor correctly.
Uses a custom OkHttp-backed NewPipe Downloader.
Performs a basic YouTube search extraction.
Converts extracted results into application-owned domain models.
Displays extraction results in a basic test screen.
Handles errors without crashing or silently hiding failures.
This milestone is not intended to deliver a complete YouTube client.
1.1 Explicitly out of scope
The following features are deferred:
Full Home feed.
Subscription management.
YouTube account integration.
Google sign-in.
Video playback.
Media3 ExoPlayer.
Background playback.
Downloads.
Playlists.
Comments.
Shorts.
Recommendations.
Watch history.
Persistent search history.
Room database.
DataStore preferences.
Offline mode.
Full application UI.
These features must not be implemented as part of Milestone 1.
2. Technical architecture
2.1 Architecture decision
Use a single Gradle application module named app.
Use package-by-layer organization rather than creating multiple Gradle modules.
The application follows a layered architecture:
UI layer
Domain layer
Data layer
Core/infrastructure layer
Dependencies should point inward. Domain models must not depend on Android UI classes, OkHttp, or NewPipeExtractor classes.
2.2 Package structure
com.walnuttube/
│
├── WalnutTubeApplication.kt
│
├── core/
│   ├── common/
│   │   ├── AppResult.kt
│   │   └── AppException.kt
│   │
│   ├── network/
│   │   ├── NewPipeDownloader.kt
│   │   └── NetworkModule.kt
│   │
│   └── di/
│       └── AppModule.kt
│
├── data/
│   ├── remote/
│   │   ├── YoutubeServiceWrapper.kt
│   │   └── mapper/
│   │       └── VideoMapper.kt
│   │
│   └── repository/
│       └── VideoRepositoryImpl.kt
│
├── domain/
│   ├── model/
│   │   └── Video.kt
│   │
│   ├── repository/
│   │   └── VideoRepository.kt
│   │
│   └── usecase/
│       └── SearchVideosUseCase.kt
│
├── feature/
│   └── test/
│       ├── ExtractionTestScreen.kt
│       ├── ExtractionTestViewModel.kt
│       └── ExtractionTestUiState.kt
│
└── ui/
    ├── navigation/
    │   └── WalnutTubeNavHost.kt
    │
    ├── theme/
    │   ├── Color.kt
    │   ├── Theme.kt
    │   └── Type.kt
    │
    └── common/
        ├── LoadingIndicator.kt
        └── ErrorScreen.kt
Do not create additional Gradle modules.
Do not add unnecessary abstraction layers or empty packages that have no implementation purpose.
3. Technology stack
3.1 Versions
Use the following pinned baseline for this milestone:
Component
Version
Kotlin
2.0.20
Android Gradle Plugin
8.5.2
Gradle Wrapper
8.7
Compose BOM
2024.08.00
Hilt
2.52
AndroidX Hilt
1.3.0
OkHttp
4.12.0
NewPipeExtractor
0.27.1
Java
17
compileSdk
34
targetSdk
34
minSdk
24
The pinned versions are a reproducible baseline, not a claim that they are the latest available releases.
Do not silently upgrade or downgrade dependencies. Any version change must be documented with its reason and compatibility implications.
3.2 Gradle plugins
Use:
com.android.application
org.jetbrains.kotlin.android
org.jetbrains.kotlin.plugin.compose
com.google.dagger.hilt.android
org.jetbrains.kotlin.kapt
Use Kotlin 2.0's Compose compiler Gradle plugin. Do not configure the old Compose compiler extension version independently.
Use KAPT for Hilt in this milestone. Do not mix KAPT and KSP for the same Hilt processor.
3.3 Dependency declarations
Required dependencies:
Compose BOM.
Compose UI.
Compose Foundation.
Compose Material 3.
Compose UI tooling.
Activity Compose.
Navigation Compose.
Lifecycle Runtime Compose.
Lifecycle ViewModel KTX.
Hilt Android.
Hilt Android compiler through KAPT.
AndroidX Hilt lifecycle ViewModel Compose.
OkHttp.
NewPipeExtractor.
Kotlin standard library as supplied by the Kotlin plugin.
Testing dependencies:
JUnit.
Kotlin test support where required.
AndroidX test core.
AndroidX test runner.
Compose UI test dependencies if UI tests are implemented.
Do not include unused dependencies merely because they may be needed in later milestones.
3.4 NewPipeExtractor dependency
Use the pinned dependency:
implementation("com.github.TeamNewPipe:NewPipeExtractor:0.27.1")
Use JitPack in the repository configuration because this dependency is distributed through JitPack.
The exact resolved dependency must be checked during the first build. Do not assume that a successful Gradle sync proves that the required NewPipe APIs are being used correctly.
4. Gradle configuration
4.1 Repository configuration
Repositories must be declared centrally.
Use:
Google Maven.
Maven Central.
JitPack.
Avoid declaring different repository lists in multiple places without a specific reason.
4.2 Version catalog
Use gradle/libs.versions.toml as the single source of truth for dependency and plugin versions.
Do not duplicate plugin versions in both the version catalog and settings.gradle.kts.
4.3 Android configuration
The app module must configure:
Namespace: com.walnuttube
Application ID: com.walnuttube
compileSdk: 34
minSdk: 24
targetSdk: 34
Java source compatibility: 17
Java target compatibility: 17
Kotlin JVM target: 17
Enable BuildConfig generation only if the project actually uses BuildConfig.
If enabled, ensure BuildConfig is imported from the correct application namespace.
4.4 Build types
Configure debug and release build types.
For Kotlin DSL, use:
isMinifyEnabled = false
for a release configuration where minification is not yet enabled.
Do not use Groovy-style minifyEnabled syntax in Kotlin DSL.
Do not add broad ProGuard rules preemptively.
A minified release build must be tested before any keep rules are considered necessary.
4.5 Desugaring
Use core library desugaring only if the code requires APIs that need it for the minimum supported Android version.
If enabled, put the configuration in the correct Android compile options and add the matching desugaring dependency.
Do not add NIO desugaring unless the implementation actually needs APIs from the NIO package.
4.6 Gradle properties
Keep only properties that are known to be supported and useful for this project.
Do not copy obsolete Gradle properties from older tutorials.
Do not add experimental performance flags without verifying their purpose and compatibility.
5. Application initialization
5.1 Application class
Create:
WalnutTubeApplication.kt
Responsibilities:
Annotate the application class with @HiltAndroidApp.
Provide the application entry point for Hilt.
Avoid performing network requests or extraction work during application startup.
5.2 MainActivity
Responsibilities:
Annotate with @AndroidEntryPoint.
Set the Compose content.
Apply the WalnutTube theme.
Display the navigation host.
Do not initialize NewPipe directly from the Activity.
NewPipe initialization belongs in the dependency-injection setup.
5.3 Manifest
The manifest must:
Reference WalnutTubeApplication.
Declare MainActivity.
Declare the Internet permission.
Use a valid application label.
Reference resources that actually exist.
Do not reference missing resources such as launcher icons, strings, or themes.
If launcher icons are not yet designed, use valid generated Android launcher resources rather than leaving broken manifest references.
6. Dependency injection
6.1 Hilt setup
Use:
@HiltAndroidApp on the application class.
@AndroidEntryPoint on MainActivity.
@HiltViewModel on the extraction ViewModel.
@Inject where constructor injection is appropriate.
@Module and @InstallIn(SingletonComponent::class) for application-wide dependencies.
Use javax.inject.Singleton for singleton-scoped providers.
Do not import a nonexistent dagger.hilt.android.scopes.Singleton.
6.2 NewPipe initialization
NewPipeExtractor uses global initialization state.
Create one application-scoped provider responsible for initialization.
The provider must:
Create or receive the custom Downloader.
Initialize NewPipe with that Downloader.
Construct and expose the YouTube service wrapper only after initialization.
The application must not initialize NewPipe independently in multiple classes.
Do not provide the same class through both an @Inject constructor and a separate @Provides method.
For this design, use one construction path: an explicit provider that guarantees initialization happens before the wrapper is created.
6.3 Hilt ViewModel Compose integration
Because AndroidX Hilt 1.3.0 moved the Compose ViewModel API, use:
Dependency:
androidx.hilt:hilt-lifecycle-viewmodel-compose:1.3.0
Import:
androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
Do not use the old androidx.hilt.navigation.compose.hiltViewModel import with this dependency.
7. NewPipe Downloader
7.1 Purpose
Implement a custom NewPipe Downloader backed by OkHttp.
The Downloader is responsible for:
Executing HTTP requests.
Sending request headers.
Sending request bodies when present.
Receiving response codes.
Receiving response messages.
Receiving response headers.
Receiving response bodies.
Returning the final response URL.
7.2 Implementation requirements
Extend NewPipe's Downloader class.
Implement the required abstract execute(Request) method.
Use OkHttp 4.12.0.
Use a single reusable OkHttp client rather than creating a new client for every request.
Close the OkHttp response using use or an equivalent guaranteed cleanup mechanism.
Read the response body safely.
Preserve the response information required by NewPipe.
Construct NewPipe's Response using the constructor order defined by NewPipeExtractor 0.27.1:
Response code.
Response message.
Response headers.
Response body.
Final URL.
Do not copy a Response constructor from another NewPipeExtractor version.
7.3 POST body handling
The POST implementation must not use an unchecked body!!.
dataToSend() may return null.
Define explicit behavior:
If a POST request has a body, send it.
If the API permits an empty POST body, send a valid empty body.
If the request requires a body and none is supplied, return a clear error.
Never allow a null body to cause an unexpected application crash.
7.4 HTTP header handling
HTTP header names are case-insensitive.
Do not compare header names using case-sensitive equality.
Avoid adding duplicate Accept-Language headers.
NewPipe Request already supports localization information and can merge localization headers into the request headers.
The implementation must respect that behavior.
If a request explicitly supplies a localization header, define how it takes precedence over the default localization header.
Do not blindly append another localization header.
7.5 Error handling
Handle:
Connection failures.
Timeouts.
HTTP errors.
Empty response bodies.
Unexpected response formats.
Do not convert every network failure into a successful empty response.
Preserve meaningful failure information for the repository and UI.
7.6 Coroutine cancellation limitation
OkHttp's synchronous execute() call is blocking.
Rethrowing CancellationException in a coroutine does not automatically cancel the underlying OkHttp Call.
For this milestone:
Network execution must not run on the main thread.
Coroutine cancellation must not be swallowed.
CancellationException must be rethrown.
The documentation must explicitly state that underlying OkHttp call cancellation is not propagated yet.
Do not claim complete network cancellation support unless the implementation actually connects coroutine cancellation to Call.cancel().
8. NewPipe service wrapper
8.1 Purpose
Create:
YoutubeServiceWrapper.kt
This class isolates NewPipeExtractor-specific operations from the rest of the application.
It should provide the extraction operations required by Milestone 1.
At minimum:
YouTube search.
Channel information extraction, only if included in the final test scope.
Do not implement playback or stream URL extraction in this milestone.
8.2 Correct service access
After NewPipe initialization, use:
ServiceList.YouTube
Do not write:
NewPipe.getService(ServiceList.YouTube) as YoutubeService
ServiceList.YouTube is already a YoutubeService instance.
Do not cast it unnecessarily.
8.3 Search extraction
The search implementation must:
Receive a search query.
Validate that the query is not blank.
Create a YouTube search extractor using the API available in NewPipeExtractor 0.27.1.
Execute the extraction.
Retrieve the resulting InfoItems.
Convert supported stream items into application domain models.
Return a structured success or failure result.
The exact YoutubeSearchQueryHandlerFactory package and constructor must be taken from the resolved 0.27.1 dependency source.
Do not guess its package based on another NewPipeExtractor version.
Before implementing the wrapper, inspect the pinned dependency source or compile a minimal API verification test.
8.4 Search result types
NewPipe uses an InfoItem/collector pattern for list results.
Search results returned by InfoItemsPage.getItems() are InfoItem objects.
For stream search results, use the actual StreamInfoItem model.
Do not filter the returned InfoItems as StreamInfoItem and then pass them into a mapper that expects YoutubeStreamInfoItemExtractor.
Those are different types.
The mapper must accept the actual returned model type.
Unsupported InfoItem types should be skipped or handled explicitly. They must not crash the entire search result conversion.
8.5 Channel extraction
If channel extraction is included in Milestone 1:
Use the actual ChannelInfo API model returned by the pinned dependency.
Do not use an internal extractor implementation as a substitute for the returned result model.
Validate the URL or identifier before extraction.
Preserve extraction failures as structured errors.
If the exact channel API is not verified, remove channel extraction from the acceptance criteria rather than claiming it works.
9. Domain models
9.1 Video model
Create an application-owned Video model.
Suggested fields:
id
title
url
thumbnailUrl
uploaderName
uploaderUrl
durationSeconds
viewCount
publishedAt
timestampSource
timestampApproximate
Use nullable fields for metadata that NewPipe may not provide.
Do not fabricate missing metadata.
For example:
Do not replace an unknown title with an empty string.
Do not invent a duration.
Do not treat an unavailable upload date as the current date.
Do not use zero as a substitute for an unknown view count.
If a required identity field is missing, reject or skip the invalid item with a diagnostic rather than constructing a misleading domain object.
9.2 Timestamp representation
Use one consistent timestamp representation.
For this milestone, publishedAt may use Unix epoch seconds.
Keep timestamp provenance separate from the timestamp value.
timestampSource must distinguish:
Exact timestamp.
Approximate timestamp.
Unknown timestamp.
Use the NewPipe DateWrapper approximation information.
Do not silently treat approximate dates as exact dates.
Retrieve the upload date once and derive all timestamp-related fields from that same result.
10. Mapping layer
Create:
VideoMapper.kt
Responsibilities:
Convert NewPipe StreamInfoItem models into WalnutTube Video models.
Validate required fields.
Preserve nullable metadata.
Preserve approximate-date information.
Avoid fabricating missing values.
The mapper must use the actual API model type returned by the search collector.
Do not use YoutubeStreamInfoItemExtractor as the mapper input for an InfoItemsPage.getItems() result.
10.1 Video ID extraction
Do not extract IDs using fragile string operations such as:
substringAfterLast("v=")
substringAfterLast("/")
These methods can fail for URLs containing additional query parameters, shortened URLs, or different URL formats.
Use a verified NewPipe ID extraction API if one is available for the pinned version.
Otherwise, parse supported YouTube URL formats using a dedicated, tested parser.
If an ID cannot be reliably determined, reject the item rather than storing an incorrect ID.
11. Result and exception handling
11.1 AppResult
Create a sealed result type representing:
Success.
Error.
The result type must preserve the actual error object.
Do not use empty lists to represent extraction failures.
An empty successful search and a failed search are different states.
11.2 AppException
Create application-level exceptions for meaningful failure categories.
Examples:
Network failure.
Extraction failure.
Unsupported content.
Invalid input.
Unknown failure.
Do not classify errors solely by matching their message text.
Message-based classification is fragile and must not be treated as reliable.
11.3 safeExtract behavior
The extraction wrapper must:
Execute the extraction operation.
Return a success result when extraction succeeds.
Rethrow CancellationException.
Preserve already-classified application exceptions.
Convert known NewPipe exceptions into appropriate application errors.
Convert unexpected exceptions into a generic extraction failure while retaining the cause.
The catch order must be checked against the actual exception inheritance hierarchy.
A subclass must not be caught after a superclass if that makes its catch block unreachable.
Do not wrap an already-classified UnsupportedContentException into a generic exception.
12. Repository and use case
12.1 VideoRepository
Create a domain repository interface:
VideoRepository.kt
It should expose the search operation required by Milestone 1.
The domain layer must not import NewPipeExtractor classes.
12.2 VideoRepositoryImpl
Create the data implementation.
Responsibilities:
Call the YouTube service wrapper.
Convert the result into domain models.
Return AppResult.
Preserve meaningful errors.
12.3 SearchVideosUseCase
Create a use case that:
Accepts a search query.
Validates that the query is not blank.
Calls the repository.
Returns the repository result.
Do not add business logic unrelated to Milestone 1.
13. Extraction test feature
13.1 Purpose
Create a minimal screen to verify that the foundation works.
This is a developer/test feature, not the final WalnutTube Home screen.
13.2 ExtractionTestScreen
The screen must provide:
Search input.
Search action.
Loading indicator.
Error display.
Result list.
Empty-result state.
Use Compose Material 3 components.
Use correct Compose imports, including:
Arrangement.spacedBy
androidx.compose.foundation.text.KeyboardActions
androidx.compose.foundation.text.KeyboardOptions
Use lifecycle-aware state collection with collectAsStateWithLifecycle.
Do not use nonexistent imports or APIs.
13.3 ExtractionTestViewModel
The ViewModel must:
Be annotated with @HiltViewModel.
Receive its dependencies through constructor injection.
Expose an immutable StateFlow using asStateFlow().
Perform extraction work in viewModelScope.
Update loading, success, empty, and error states.
Rethrow cancellation rather than swallowing it.
Do not expose a MutableStateFlow directly to the UI.
Do not use duplicate or invalid ViewModel imports.
13.4 UI state
Create an explicit UI state model.
It must distinguish:
Idle.
Loading.
Success with results.
Success with no results.
Error.
Do not use a single Boolean to represent all possible screen states.
14. Navigation
Use Navigation Compose.
The navigation host must have a clear start destination.
The extraction test screen must be reachable through the navigation graph.
If a placeholder Home screen is used, it must provide a visible way to navigate to the extraction test screen.
Do not register a destination that cannot be reached.
Use navigation arguments correctly.
For a destination argument, retrieve its value using the appropriate key, for example:
backStackEntry.arguments?.getString("channelId")
Do not call getString() without a key.
Use navigation argument APIs from the correct package.
Do not import navArgument from androidx.navigation.compose.
Keep navigation minimal. There is no need to create a full application navigation system in Milestone 1.
15. Theme and common UI
Create a basic WalnutTube Compose theme.
Files:
Color.kt
Theme.kt
Type.kt
Define Typography before referencing it.
Import isSystemInDarkTheme() from the correct Compose package.
The theme must compile and be applied by MainActivity.
Common components may include:
LoadingIndicator.
ErrorScreen.
The ErrorScreen must display a meaningful error message.
Do not use a Spacer with only padding as a substitute for actual spacing. Give it an explicit size or use a layout arrangement.
Do not add visual complexity unrelated to the milestone.
16. Resources and manifest validation
All manifest references must resolve.
Check:
@string/app_name
Launcher icon resources.
Application theme.
Activity declaration.
Application class.
Internet permission.
If a resource is referenced, it must exist.
Do not leave placeholder resource references that cause a build failure.
17. ProGuard and release configuration
Do not add broad NewPipe or Rhino keep rules without evidence that they are required.
Start with the normal release configuration.
If minification causes a reproducible failure:
Identify the failing class or reflection path.
Reproduce the issue.
Add the narrowest justified keep rule.
Rebuild the minified release.
Test the affected feature again.
Do not claim that broad keep rules are minimal or necessary without testing.
18. Testing strategy
Testing must be separated into three categories.
18.1 Unit tests
Test deterministic application logic:
Query validation.
Domain model mapping.
Missing required metadata.
Timestamp conversion.
Approximate timestamp handling.
Error conversion.
UI state transitions.
These tests should not require live YouTube access.
18.2 Integration tests
Test the interaction between:
Downloader.
NewPipeExtractor.
Service wrapper.
Repository.
Use controlled responses or explicit test seams where possible.
NewPipe uses global initialization state, so tests must not accidentally share or overwrite Downloader configuration.
Do not assume that mocking an arbitrary Downloader automatically isolates NewPipe's global state.
18.3 Live network test
Perform a manual live search against YouTube.
This test is subject to:
Network connectivity.
YouTube website/API changes.
Rate limiting.
Extractor compatibility.
Regional restrictions.
Temporary service failures.
A live extraction failure must be recorded separately from a deterministic unit-test failure.
Do not claim the entire application is broken solely because a live YouTube request fails.
Likewise, do not claim the extractor works merely because the application launches.
19. Required build and verification sequence
The implementation must follow this order.
Phase A — Project foundation
Create the Gradle project.
Configure the version catalog.
Configure repositories.
Configure the app module.
Add the application class.
Add MainActivity.
Add the manifest and required resources.
Add the Compose theme.
Build the project.
Checkpoint A: The empty application must compile and launch.
Phase B — Hilt
Configure Hilt plugins and dependencies.
Add the Hilt application annotation.
Add the Activity entry-point annotation.
Add the DI modules.
Verify ViewModel injection.
Checkpoint B: Hilt must compile and inject a test ViewModel successfully.
Phase C — Downloader
Implement the OkHttp-backed Downloader.
Handle request methods.
Handle nullable POST bodies.
Handle case-insensitive headers.
Handle response conversion.
Handle errors and resource cleanup.
Checkpoint C: Downloader behavior must be tested independently.
Phase D — NewPipe integration
Initialize NewPipe with the custom Downloader.
Verify ServiceList.YouTube.
Inspect the resolved v0.27.1 API for search construction.
Verify the exact search factory package and constructor.
Verify result item types.
Implement the service wrapper.
Implement mapping.
Checkpoint D: A minimal test must compile against the pinned NewPipeExtractor dependency before the complete extraction feature is written.
Phase E — Domain and UI
Add domain models.
Add repository interface and implementation.
Add search use case.
Add extraction ViewModel.
Add extraction test screen.
Add navigation.
Verify loading, success, empty, and error states.
Checkpoint E: The user must be able to navigate to the extraction test screen and execute a search.
Phase F — Final verification
Run:
Debug build.
Unit tests.
Relevant integration tests.
Manual launch test.
Manual live extraction test.
Record the results.
Do not report a test as passed if it was not actually executed.
20. Acceptance criteria
Milestone 1 is complete only when all applicable criteria are satisfied.
Requirement
Acceptance condition
Gradle
Project syncs and builds
Kotlin
No unresolved compilation errors
Compose
Main screen renders
Hilt
Dependency injection works
Application
App launches without crashing
Downloader
Correctly handles requests and responses
NewPipe
Initializes using the custom Downloader
Service
YouTube service is accessed correctly
Search
Search extraction compiles against v0.27.1
Mapping
Actual returned InfoItem types are mapped
Metadata
Missing values are not fabricated
Errors
Failures are represented correctly
Cancellation
CancellationException is not swallowed
Navigation
Extraction test screen is reachable
UI
Loading, empty, success, and error states work
Tests
Actual test results are recorded
If a criterion has not been tested, mark it Not verified.
Do not mark the milestone complete based on code generation alone.
21. Known limitations
The following limitations are acceptable for Milestone 1 if they are documented:
NewPipeExtractor relies on external services that may change.
Live extraction is not guaranteed to work indefinitely.
Synchronous OkHttp calls do not yet propagate coroutine cancellation to the underlying Call.
No persistent database exists.
No playback implementation exists.
No account integration exists.
No subscription feed exists.
No offline functionality exists.
No final UI design exists.
These limitations must not be presented as completed features.
22. Implementation rules
The following rules apply throughout the milestone.
Do not silently change the approved architecture.
Do not add unnecessary Gradle modules.
Do not expand the milestone scope.
Do not invent NewPipe APIs or package names.
Do not use types that do not match the actual returned API objects.
Do not fabricate missing metadata.
Do not swallow coroutine cancellation.
Do not hide extraction failures behind empty results.
Do not add unverified ProGuard rules.
Do not leave invalid imports or unresolved resources.
Do not claim a feature works without testing it.
Do not replace real implementation with placeholder logic.
Do not upgrade dependency versions without documenting the reason.
Do not overwrite existing project files without first showing the planned changes.
Show the proposed file tree before implementation.
Explain any remaining uncertainty before making a decision that affects architecture or scope.
23. Final status
Milestone 1 architecture: Retained.
Single-module structure: Retained.
Technology direction: Retained.
Milestone scope: Unchanged.
Known v3 technical errors: Addressed in this design.
NewPipe API verification: Must be completed against the pinned 0.27.1 dependency before implementation is considered verified.
Implementation authorization: Not granted by this document alone.
The next step is a technical implementation review of this v4 document. After that review, the file tree and implementation plan must be presented for approval.
No implementation should begin until the user explicitly approves the plan.