package com.example.lexidesk.ui.navigation

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CalendarMonth
import androidx.compose.material.icons.filled.Dashboard
import androidx.compose.material.icons.filled.Folder
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material.icons.filled.People
import androidx.compose.ui.graphics.vector.ImageVector

sealed class Screen(val route: String, val title: String, val icon: ImageVector? = null) {
    // Bottom nav destinations
    object Dashboard : Screen("dashboard", "Dashboard", Icons.Default.Dashboard)
    object Clients : Screen("clients", "Clients", Icons.Default.People)
    object Work : Screen("work", "Cases", Icons.Default.Folder)
    object Calendar : Screen("calendar", "Calendar", Icons.Default.CalendarMonth)
    object More : Screen("more", "More", Icons.Default.Menu)

    // Sub-screens
    object ClientDetail : Screen("client_detail/{clientId}", "Client Details") {
        fun createRoute(clientId: Long) = "client_detail/$clientId"
    }
    object AddEditClient : Screen("add_edit_client?clientId={clientId}", "Add Client") {
        fun createRoute(clientId: Long? = null) = if (clientId != null) "add_edit_client?clientId=$clientId" else "add_edit_client"
    }

    object CaseDetail : Screen("case_detail/{caseId}", "Case Details") {
        fun createRoute(caseId: Long) = "case_detail/$caseId"
    }
    object AddEditCase : Screen("add_edit_case?caseId={caseId}&clientId={clientId}", "Case / Assignment") {
        fun createRoute(caseId: Long? = null, clientId: Long? = null) = buildString {
            append("add_edit_case?")
            if (caseId != null) append("caseId=$caseId&")
            if (clientId != null) append("clientId=$clientId")
        }.trimEnd('&', '?')
    }

    object Tasks : Screen("tasks", "Tasks & Deadlines")
    object AddEditTask : Screen("add_edit_task?taskId={taskId}&clientId={clientId}&caseId={caseId}", "Task") {
        fun createRoute(taskId: Long? = null, clientId: Long? = null, caseId: Long? = null) = buildString {
            append("add_edit_task?")
            if (taskId != null) append("taskId=$taskId&")
            if (clientId != null) append("clientId=$clientId&")
            if (caseId != null) append("caseId=$caseId")
        }.trimEnd('&', '?')
    }

    object Documents : Screen("documents", "Document Vault")
    object AddEditDocument : Screen("add_edit_document?clientId={clientId}&caseId={caseId}", "Add Document") {
        fun createRoute(clientId: Long? = null, caseId: Long? = null) = buildString {
            append("add_edit_document?")
            if (clientId != null) append("clientId=$clientId&")
            if (caseId != null) append("caseId=$caseId")
        }.trimEnd('&', '?')
    }

    object Templates : Screen("templates", "Document Templates")
    object TemplateEditor : Screen("template_editor?draftId={draftId}&templateType={templateType}", "Template Editor") {
        fun createRoute(draftId: Long? = null, templateType: String? = null) = buildString {
            append("template_editor?")
            if (draftId != null) append("draftId=$draftId&")
            if (templateType != null) append("templateType=$templateType")
        }.trimEnd('&', '?')
    }

    object Scanner : Screen("scanner", "Document Scanner & OCR")
    object Reports : Screen("reports", "Analytics & Reports")
    object Settings : Screen("settings", "Settings")
    object GlobalSearch : Screen("global_search", "Search Everything")
    object Onboarding : Screen("onboarding", "Welcome to LexiDesk")
}

val bottomNavItems = listOf(
    Screen.Dashboard,
    Screen.Clients,
    Screen.Work,
    Screen.Calendar,
    Screen.More
)
