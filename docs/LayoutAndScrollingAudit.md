# LexiDesk UI Redesign, Theme Audit & Scrolling Root-Cause Analysis

## 1. Executive Summary
This document outlines the architectural audit of the LexiDesk Android application, diagnosing user-reported scrolling failures, theme contrast deficiencies, and visual design inconsistencies across the Jetpack Compose user interface.

---

## 2. Root-Cause Analysis of Scrolling Bugs

### Issue A: Completely Unscrollable Screens on Smaller Form-Factors
* **`OnboardingScreen.kt`**: The root container uses a standard `Column` without `.verticalScroll(rememberScrollState())`. On devices with screen heights < 800dp or with font scaling enabled, the profession selector, theme picker, and "Get Started" action button are rendered off-screen, completely trapping the user.
* **`AppLockScreen.kt`**: The PIN entry view uses an unscrollable `Column`. When the software keyboard (IME) is displayed, the PIN entry field and "Unlock" button are occluded and pushed off-screen without any ability to scroll.

### Issue B: Dialog Content Clipping & Overflow
* **`CalendarScreen.kt`**: The "Schedule Event / Reminder" `AlertDialog` contains six vertical input rows (Title, Category Dropdown, Time Picker, Reminder Interval, Description). In landscape mode or with a keyboard open, the dialog overflows the window bounds and cannot be scrolled, preventing event creation.
* **`ClientDetailScreen.kt` & `CaseDetailScreen.kt`**: "Add Note" and "Edit Note" dialogs lack `verticalScroll`, causing input fields to overflow when multi-line text is entered.
* **`ForgotPasswordDialog.kt`, `DiscardChangesDialog.kt`, `LogoutDialog.kt`**: Lack explicit scrollable containers for localized text or accessibility font scaling.

### Issue C: Keyboard Inset & Scroll Clearance Failures
* Form screens (`AddEditClientScreen`, `AddEditCaseScreen`, `AddEditTaskScreen`, `AddDocumentScreen`, `TemplateEditorScreen`) lack `Modifier.imePadding()`. When focused on lower fields, the software keyboard obscures the input fields and action buttons.
* List screens (`ClientsListScreen`, `CasesListScreen`, `TasksListScreen`, `DocumentsListScreen`) require adequate bottom scroll clearance so the Floating Action Button (FAB) does not cover the last list item.

---

## 3. Dark Mode & Light Mode Audit
* **Surface Tones & Elevation**: Several custom cards and buttons rely on fixed opacity levels (`alpha = 0.08f`) that produce washed-out contrast against dark backgrounds (`#0F172A`).
* **Text Contrast**: Subtitle colors using `onSurfaceVariant` in certain badges and dropdowns require contrast enhancement to satisfy WCAG AA (4.5:1 ratio).
* **Theme Persistence**: Theme switching is cleanly mediated by `ThemeMode` in DataStore preferences; all UI components must dynamically consume `MaterialTheme.colorScheme` instead of hardcoded hex values.

---

## 4. UI Modernization Strategy
* Refine the typography hierarchy in `Type.kt` for improved line heights and tracking.
* Elevate cards with subtle rounded corners (`16.dp`), tonal elevation, and border strokes.
* Standardize `TopBarWithBack` with surface tinting and elevation.
* Enhance interactive controls (`PriorityBadge`, `StatusBadge`, `QuickActionButton`, `EmptyStateView`) with modern micro-interactions and tactile feedback.
