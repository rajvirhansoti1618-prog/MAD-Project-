# LexiDesk Architecture Audit & Baseline Report

## Executive Summary
LexiDesk is a native Android application developed with Kotlin, Jetpack Compose, Room Database, and Google ML Kit. This report documents the baseline code architecture, design decisions, and system capabilities prior to and through the authentication, navigation, and theming enhancement milestones.

---

## Architecture Overview
The application follows the recommended Google Architecture Guide:
- **Presentation Layer**: Built completely in Jetpack Compose (Material 3). UI composables are declarative and subscribe to unidirectional DataFlow via `StateFlow` emitted by ViewModels.
- **Domain Layer**: Clean interfaces defining business contracts (`AuthRepository`, `AuthResult`) decoupling UI logic from concrete storage engines.
- **Data Layer**:
  - **Local Persistence**: SQLite database mediated via Room 2.6.1 (`LexiDeskDatabase`) with KSP.
  - **Preferences**: Jetpack DataStore Preferences for user profile, authentication state, and security settings.
  - **File Storage**: Internal application sandbox (`context.filesDir`) with `FileProvider` (`androidx.core.content.FileProvider`) for content sharing.
- **Background & Hardware**:
  - `AlarmManager` & `NotificationHelper` for reminder dispatch.
  - `WorkManager` (`DeadlineCheckWorker`) for periodic background deadline tracking.
  - Google Play Services ML Kit on-device Latin OCR for document scanning.

---

## Authentication & Session Architecture
- **Contract**: `com.example.lexidesk.domain.auth.AuthRepository`
- **Engine**: `com.example.lexidesk.data.auth.LocalAuthRepository`
- **Storage**: Key-value pairs stored in DataStore with SHA-256 password hashing.
- **Routing**: `MainActivity` evaluates `isLoggedIn` and `isOnboarded` to direct startup navigation.

---

## Verification & Quality Assurance
- Automated test coverage verified with JUnit 4.
- CI pipeline running on GitHub Actions (`.github/workflows/ci.yml`).
