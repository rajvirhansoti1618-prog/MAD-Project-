# LexiDesk — Professional Productivity & Documentation Assistant

> **Mobile Application Development (MAD) College Project**  
> Built with **Kotlin**, **Jetpack Compose (Material 3)**, **MVVM**, **Room Database**, **WorkManager**, and **Google ML Kit Text Recognition**.

---

## 🏛️ 1. Project Overview

**LexiDesk** is a modern, local-first Android productivity and practice management suite designed for lawyers, Chartered Accountants (CAs), tax auditors, and corporate consultants. It unifies client directories, legal matters, assignments, court deadlines, document vaults, optical character recognition (OCR), and professional document generation in one cohesive, offline-capable application.

---

## 📱 2. Core Modules & Key Features

### 📊 A. Interactive Executive Dashboard
- **Real-time Metric Counters**: Live aggregates for Total Clients, Active Cases, Open Tasks, and Overdue Deadlines.
- **Today's Agenda**: Consolidates hearings, client briefing sessions, and tasks due today with quick toggle completion.
- **Upcoming Deadlines**: Prioritized timeline of upcoming hearings and project milestones.
- **Viva Demo Dataset**: Built-in 1-tap "Load Demo" button that seeds realistic clients, court disputes, and tax audits for demonstration.

### 👥 B. Comprehensive Client Management (Full CRUD)
- **Categorization**: Individual clients vs. Corporate/LLP business entities.
- **Direct Dial & Email Integration**: Native `Intent.ACTION_DIAL` and `Intent.ACTION_SENDTO` triggers for instant communication.
- **Integrated Client Dossier**: Displays linked cases, tasks, vault documents, and consultation notes in one profile.

### ⚖️ C. Work & Matter Management (Cases & Assignments)
- **Full Tracking**: Case titles, court venues, reference numbers, priority badges (Urgent, High, Medium, Low), and status tracking (Not Started, In Progress, Waiting, Completed, On Hold).
- **Date Pickers**: Material 3 date pickers for start and due dates.
- **Hearing Notes & Minutes**: Add timestamped proceeding notes directly to any case.

### ✅ D. Task & Deadline Manager
- **Status & Overdue Highlighting**: Automatic red alert indicators for tasks that have crossed their deadline.
- **Filter Tabs**: Filter by *All*, *Today*, *Upcoming*, *Overdue*, and *Completed*.
- **Quick Checkmark Toggle**: Check off completed items directly from the list.

### ⏰ E. Android Reminders & Background WorkManager
- **Real Notifications**: Dispatches native Android notifications via `NotificationManager` and `AlarmManager`.
- **Pre-deadline Alerts**: Configure notifications for 15 min, 30 min, 1 hour, or 1 day prior to the scheduled event.
- **Reboot Rescheduling**: `BootReceiver` restores scheduled reminders on device restart.
- **WorkManager Deadline Checker**: Periodic background worker (`DeadlineCheckWorker`) checks for upcoming or overdue deadlines.

### 🗄️ F. Document Vault & Safe File Handling
- **Storage Access Framework (SAF)**: Safe importing of PDFs, receipts, and images.
- **Internal Storage Copying**: Selected documents are copied to app-private storage (`context.filesDir`) to prevent URI permission expiration.
- **Android FileProvider**: Secure document viewing and sharing via `content://` URIs without exposing raw filesystem paths.

### 📷 G. Document Scanner & ML Kit Text Recognition (OCR)
- **On-Device Machine Learning**: Integrated with Google ML Kit Latin Text Recognition.
- **Instant Extraction**: Extracts text from photographed paper contracts, agreements, or invoices.
- **Export Actions**: Copy to clipboard, or convert into client notes with a single tap.

### 📄 H. Document Template Library & PDF Generation
- **Standard Templates Included**:
  - *Client Engagement & Retainer Letter*
  - *Formal Legal Demand Notice*
  - *Professional Fee Tax Invoice*
  - *Meeting Minutes & Consultation Record*
  - *Payment Receipt Acknowledgement*
- **Smart Placeholders**: Automatically replaces `[CLIENT_NAME]`, `[DATE]`, `[FIRM_NAME]`, and `[ADDRESS]` from client records.
- **Native PDF Engine**: Uses Android's `PdfDocument` API to draw crisp, formatted A4 documents with header banners, dates, reference codes, and footer watermarks.
- **Direct Share**: View or share the generated PDF via any Android application (WhatsApp, Gmail, Drive).

### 📅 I. Interactive Monthly Calendar
- **Unified Event Timeline**: Visual monthly grid with day event dots for court hearings, client meetings, and task deadlines.
- **Interactive Agenda**: Tap any date to view and manage appointments.
- **Event Scheduling**: Modal for scheduling court hearings, tax filing deadlines, and client conferences.

### 📈 J. Executive Analytics & Reports
- **Visual Progress Metrics**: Real-time delivery completion rate progress bar.
- **Executive PDF Export**: Generate and share high-level executive summaries in PDF format.

### 🔒 K. Security, App Lock & Offline Backups
- **App Lock PIN Protection**: Optional 4-digit PIN lock to protect sensitive client data.
- **JSON Backup & Restore**: Export all client records, cases, and tasks to an offline JSON archive, or restore them seamlessly.

---

## 🛠️ 3. Technology Stack & Architecture

- **Language**: Kotlin 2.2.10
- **UI Framework**: Jetpack Compose with Material 3
- **Architecture**: MVVM (Model-View-ViewModel) + Repository Pattern
- **Local Persistence**: Room Database (`LexiDeskDatabase`) with KSP code generation
- **Preferences**: Jetpack DataStore Preferences
- **Background Tasks**: Android WorkManager & AlarmManager
- **Machine Learning**: Google Play Services ML Kit Text Recognition
- **PDF Generation**: Android `PdfDocument` API
- **File Sharing**: Android `FileProvider` (`androidx.core.content.FileProvider`)
- **Build System**: Gradle 9.5.0 with Android Gradle Plugin (AGP) 9.3.1

---

## 📂 4. Project Directory Structure

```
app/src/main/java/com/example/lexidesk/
├── LexiDeskApp.kt                 # Application class & repository initialization
├── MainActivity.kt                # Main activity, notification permission & app lock
│
├── data/
│   ├── entity/                    # Room Entities (Client, Case, Task, Document, Reminder, Note, Draft)
│   ├── dao/                       # Room DAOs with Flow queries
│   ├── local/                     # Room Database & DataStore Preferences
│   └── repository/                # Repositories abstracting data operations
│
├── ui/
│   ├── theme/                     # Brand color palette, typography & M3 theme
│   ├── common/                    # Metric cards, status badges, search bars & pickers
│   ├── navigation/                # NavRoutes & Compose Navigation Graph
│   ├── dashboard/                 # Executive dashboard screen & ViewModel
│   ├── clients/                   # Client list, detail dossier & add/edit forms
│   ├── cases/                     # Case list, matter detail & add/edit forms
│   ├── tasks/                     # Task manager, filter tabs & quick toggle
│   ├── documents/                 # Document vault & SAF file picker
│   ├── calendar/                  # Monthly calendar grid & agenda viewer
│   ├── templates/                 # Document template editor & PDF generator
│   ├── scanner/                   # OCR text extraction screen
│   ├── reports/                   # Performance analytics & report export
│   ├── settings/                  # App preferences, app lock PIN & backup/restore
│   └── search/                    # Global search across all database entities
│
├── notifications/                 # NotificationHelper, Alarm scheduler & Boot receiver
├── pdf/                           # Android PdfDocument builder & sharing utilities
├── scanner/                       # ML Kit Text Recognition helper
└── utils/                         # Date utilities, Backup/Restore & Sample data seeder
```

---

## 🚀 5. How to Run the Project

1. Open **Android Studio** (Ladybug / Meerkat or newer recommended).
2. Choose **Open Existing Project** and select this directory (`MAD Project`).
3. Ensure Android SDK Platform **Android 14/15/16 (API 34-36)** is installed.
4. Let Gradle sync automatically.
5. Click **Run** (`Shift + F10`) or run from terminal:
   ```bash
   ./gradlew assembleDebug
   ```
6. The compiled APK will be generated at:
   ```
   app/build/outputs/apk/debug/app-debug.apk
   ```

---

## 🎓 6. MAD College Viva Q&A Guide

**Q1: What architecture does this application follow?**  
> *Answer:* LexiDesk strictly follows Google's recommended **MVVM (Model-View-ViewModel)** architecture paired with the **Repository Pattern**. UI composables observe immutable StateFlow streams from ViewModels, while ViewModels interact with Repositories that abstract Room Database DAOs and DataStore preferences.

**Q2: How does the application ensure documents remain accessible without cloud storage?**  
> *Answer:* When a user selects a file via Android's Storage Access Framework (SAF), the app immediately reads the input stream and writes a private copy to `context.filesDir`. File metadata is stored in Room, and files are shared using Android's `FileProvider` with content URIs, guaranteeing persistent access without expiring temporary permissions.

**Q3: How are reminders and deadlines scheduled?**  
> *Answer:* Reminders use `AlarmManager.setExactAndAllowWhileIdle()` to trigger `ReminderBroadcastReceiver` which shows notifications via Android's `NotificationCompat`. Additionally, a `BootReceiver` reschedules active alarms after device reboots, and a periodic `WorkManager` worker runs in the background to flag overdue tasks.

**Q4: How is OCR text extraction implemented?**  
> *Answer:* Google ML Kit's Latin Text Recognition runs on-device without requiring an internet connection or paid API key. An image URI or bitmap is passed to `InputImage.fromFilePath()`, and the recognized text blocks are returned via Kotlin Coroutines.

---

*Developed for Mobile Application Development (MAD) College Laboratory Assessment.*

## 📦 Usage

1. Open the project in Android Studio.
2. Sync Gradle and let it download dependencies.
3. Connect an Android device or start an emulator.
4. Click **Run** (Shift+F10) or use `./gradlew installDebug` to install the app.
5. Follow the on-screen onboarding to set up your profile.

## 🤝 Contributing

![CI](https://github.com/rajvirhansoti1618-prog/MAD-Project-/actions/workflows/ci.yml/badge.svg)

We welcome contributions! To get started:
1. Fork the repository.
2. Clone your fork locally.
3. Create a new branch (`git checkout -b <branch-name>`).
4. Make your changes following the existing code style.
5. Run the project to ensure it builds (`./gradlew assembleDebug`).
6. Commit with a clear conventional‑commit message.
7. Push to your fork and open a Pull Request.

Please keep the commits **small and focused** and avoid altering the core functionality unless fixing a genuine bug.
