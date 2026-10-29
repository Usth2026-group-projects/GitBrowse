# GitBrowse

GitBrowse is a native Android app that can be used to search up Github repositories and users.

## Features

- Search repositories or accounts using the **Repositories** and **People** tabs.
- Load additional search results without losing the existing list.
- View repository descriptions, stars, forks, and primary languages.
- Read selectable README text with **Show more** and **Show less** controls.
- View profile names, biographies, locations, follower counts, and public repository counts.
- Browse a profile's recently updated repositories and open their details.



No GitHub sign-in or API token is required by the current implementation.

## Getting started

### Requirements

Use Android Studio with support for the project's configured Android Gradle plugin, an Android SDK installation, and an emulator or physical device running API 26 or newer.

The checked-in build configuration specifies:

| Component | Configuration |
| --- | --- |
| Minimum Android API | 26 |
| Compile / target Android API | 37 |
| Android Gradle plugin | 9.4.1 |
| Gradle wrapper | 9.6.0 |
| Gradle daemon JVM | Java 25 |
| Java source / target compatibility | Java 11 |
| Kotlin Compose compiler plugin | 2.2.10 |
| Compose BOM | 2026.02.01 |

The Gradle daemon JVM and the app's Java compilation compatibility are separate settings. JVM provisioning is configured in `gradle/gradle-daemon-jvm.properties`.

Internet access is needed to download build dependencies and to fetch GitHub data while using the app.

### Open in Android Studio

1. Clone the repository:

   ```sh
   git clone https://github.com/Usth2026-group-projects/GitBrowse.git
   cd GitBrowse
   ```

2. Open the project root in Android Studio.
3. Install Android SDK Platform 37 through SDK Manager and let Gradle sync finish.
4. Ensure the SDK path is configured. Android Studio can create `local.properties`; for command-line setup, add your own path:

   ```properties
   sdk.dir=/absolute/path/to/Android/Sdk
   ```

5. Select the `app` run configuration and an emulator or connected device, then run the app.

## Usage

1. Choose **Repositories** or **People**.
2. Enter a query and press **Search**, or use the keyboard's search action.
3. Tap a result to open its repository or profile.
4. Use **Load more** at the bottom of search results to request another page.
5. On a profile, tap a repository to view its details. Back returns to the profile; another Back returns to search.

Switching search tabs clears the submitted results while keeping the text in the search field.

## Project structure

```text
app/src/main/
├── AndroidManifest.xml              # Permissions, launcher activity, and app configuration
├── java/dev/gitbrowse/app/
│   ├── MainActivity.kt              # Hosts the Compose UI
│   ├── SearchViewModel.kt           # Screen state, requests, retries, and cancellation
│   ├── data/
│   │   └── GitHubApi.kt             # Data models, HTTP requests, and response parsing
│   └── ui/
│       ├── Screen.kt                # Screen selection, theme, search UI, and result cards
│       ├── RepositoryScreen.kt      # Repository details
│       ├── ProfileScreen.kt         # Profile details and repository list
│       └── ReadmeSection.kt         # Selectable README preview
├── keepRules/                      # Optimization keep-rule configuration
└── res/                            # App name, window theme, icons, and backup configuration
```

### Data flow

```text
User action → Screen callback → SearchViewModel → GitHubApi
                                     ↓              │
Screen observes StateFlow ← Updated UI state ← Response or error
```

`SearchViewModel` exposes separate state objects for search, repository details, and profile details. README loading and profile repository-list loading each have their own status and error fields, allowing details to remain visible while related content loads.

`GitBrowseApp` selects the visible screen from the selected repository name and profile login. A selected repository takes priority over a selected profile. Search and profile screens use saveable UI state so supported local state can be retained while viewing details.

## License

This project is distributed under the [MIT License](LICENSE).
