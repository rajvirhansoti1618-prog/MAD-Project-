package com.example.lexidesk.ui.navigation

import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.material3.MaterialTheme
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.example.lexidesk.LexiDeskApp
import com.example.lexidesk.ui.calendar.CalendarScreen
import com.example.lexidesk.ui.calendar.CalendarViewModel
import com.example.lexidesk.ui.cases.AddEditCaseScreen
import com.example.lexidesk.ui.cases.CaseDetailScreen
import com.example.lexidesk.ui.cases.CasesListScreen
import com.example.lexidesk.ui.cases.CasesViewModel
import com.example.lexidesk.ui.clients.AddEditClientScreen
import com.example.lexidesk.ui.clients.ClientDetailScreen
import com.example.lexidesk.ui.clients.ClientsListScreen
import com.example.lexidesk.ui.clients.ClientsViewModel
import com.example.lexidesk.ui.dashboard.DashboardScreen
import com.example.lexidesk.ui.dashboard.DashboardViewModel
import com.example.lexidesk.ui.documents.AddDocumentScreen
import com.example.lexidesk.ui.documents.DocumentsListScreen
import com.example.lexidesk.ui.documents.DocumentsViewModel
import com.example.lexidesk.ui.more.MoreScreen
import com.example.lexidesk.ui.onboarding.OnboardingScreen
import com.example.lexidesk.ui.reports.ReportsScreen
import com.example.lexidesk.ui.scanner.ScannerScreen
import com.example.lexidesk.ui.search.GlobalSearchScreen
import com.example.lexidesk.ui.settings.SettingsScreen
import com.example.lexidesk.ui.settings.SettingsViewModel
import com.example.lexidesk.ui.tasks.AddEditTaskScreen
import com.example.lexidesk.ui.tasks.TasksListScreen
import com.example.lexidesk.ui.tasks.TasksViewModel
import com.example.lexidesk.ui.templates.TemplateEditorScreen
import com.example.lexidesk.ui.templates.TemplatesListScreen
import com.example.lexidesk.ui.templates.TemplatesViewModel

@Composable
fun LexiDeskAppShell(
    app: LexiDeskApp,
    startDestination: String = Screen.Dashboard.route
) {
    val navController = rememberNavController()

    // Shared ViewModels initialized with app scope
    val dashboardViewModel = DashboardViewModel(app)
    val clientsViewModel = ClientsViewModel(app)
    val casesViewModel = CasesViewModel(app)
    val tasksViewModel = TasksViewModel(app)
    val documentsViewModel = DocumentsViewModel(app)
    val calendarViewModel = CalendarViewModel(app)
    val templatesViewModel = TemplatesViewModel(app)
    val settingsViewModel = SettingsViewModel(app)

    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = navBackStackEntry?.destination?.route

    val showBottomBar = bottomNavItems.any { it.route == currentRoute }

    Scaffold(
        bottomBar = {
            if (showBottomBar) {
                NavigationBar(
                    containerColor = MaterialTheme.colorScheme.surface,
                    tonalElevation = 8.dp
                ) {
                    bottomNavItems.forEach { screen ->
                        NavigationBarItem(
                            icon = { screen.icon?.let { Icon(it, contentDescription = screen.title) } },
                            label = { Text(screen.title, fontWeight = if (currentRoute == screen.route) FontWeight.Bold else FontWeight.Normal) },
                            selected = currentRoute == screen.route,
                            colors = androidx.compose.material3.NavigationBarItemDefaults.colors(
                                selectedIconColor = com.example.lexidesk.ui.theme.NavyPrimary,
                                selectedTextColor = com.example.lexidesk.ui.theme.NavyPrimary,
                                indicatorColor = com.example.lexidesk.ui.theme.TealSecondary.copy(alpha = 0.18f),
                                unselectedIconColor = MaterialTheme.colorScheme.onSurfaceVariant,
                                unselectedTextColor = MaterialTheme.colorScheme.onSurfaceVariant
                            ),
                            onClick = {
                                navController.navigate(screen.route) {
                                    popUpTo(navController.graph.findStartDestination().id) {
                                        saveState = true
                                    }
                                    launchSingleTop = true
                                    restoreState = true
                                }
                            }
                        )
                    }
                }
            }
        }
    ) { innerPadding ->
        NavHost(
            navController = navController,
            startDestination = startDestination,
            modifier = Modifier.padding(innerPadding)
        ) {
            // Dashboard
            composable(Screen.Dashboard.route) {
                DashboardScreen(
                    viewModel = dashboardViewModel,
                    onNavigateToClients = { navController.navigate(Screen.Clients.route) },
                    onNavigateToCases = { navController.navigate(Screen.Work.route) },
                    onNavigateToTasks = { navController.navigate(Screen.Tasks.route) },
                    onNavigateToCalendar = { navController.navigate(Screen.Calendar.route) },
                    onNavigateToDocuments = { navController.navigate(Screen.Documents.route) },
                    onNavigateToScanner = { navController.navigate(Screen.Scanner.route) },
                    onNavigateToSearch = { navController.navigate(Screen.GlobalSearch.route) },
                    onAddClient = { navController.navigate(Screen.AddEditClient.createRoute()) },
                    onAddCase = { navController.navigate(Screen.AddEditCase.createRoute()) },
                    onAddTask = { navController.navigate(Screen.AddEditTask.createRoute()) },
                    onCaseClick = { caseId -> navController.navigate(Screen.CaseDetail.createRoute(caseId)) }
                )
            }

            // Clients List
            composable(Screen.Clients.route) {
                ClientsListScreen(
                    viewModel = clientsViewModel,
                    onClientClick = { clientId -> navController.navigate(Screen.ClientDetail.createRoute(clientId)) },
                    onAddClientClick = { navController.navigate(Screen.AddEditClient.createRoute()) }
                )
            }

            // Client Detail
            composable(
                route = Screen.ClientDetail.route,
                arguments = listOf(navArgument("clientId") { type = NavType.LongType })
            ) { backStackEntry ->
                val clientId = backStackEntry.arguments?.getLong("clientId") ?: 0L
                ClientDetailScreen(
                    clientId = clientId,
                    viewModel = clientsViewModel,
                    onBackClick = { navController.popBackStack() },
                    onEditClientClick = { id -> navController.navigate(Screen.AddEditClient.createRoute(id)) },
                    onCaseClick = { caseId -> navController.navigate(Screen.CaseDetail.createRoute(caseId)) },
                    onAddCaseClick = { id -> navController.navigate(Screen.AddEditCase.createRoute(clientId = id)) },
                    onAddTaskClick = { id -> navController.navigate(Screen.AddEditTask.createRoute(clientId = id)) },
                    onAddDocumentClick = { id -> navController.navigate(Screen.AddEditDocument.createRoute(clientId = id)) }
                )
            }

            // Add/Edit Client
            composable(
                route = Screen.AddEditClient.route,
                arguments = listOf(navArgument("clientId") {
                    type = NavType.LongType
                    defaultValue = -1L
                })
            ) { backStackEntry ->
                val clientId = backStackEntry.arguments?.getLong("clientId")?.takeIf { it > 0 }
                AddEditClientScreen(
                    clientId = clientId,
                    viewModel = clientsViewModel,
                    onBackClick = { navController.popBackStack() },
                    onSaveSuccess = { navController.popBackStack() }
                )
            }

            // Work / Cases List
            composable(Screen.Work.route) {
                CasesListScreen(
                    viewModel = casesViewModel,
                    onCaseClick = { caseId -> navController.navigate(Screen.CaseDetail.createRoute(caseId)) },
                    onAddCaseClick = { navController.navigate(Screen.AddEditCase.createRoute()) }
                )
            }

            // Case Detail
            composable(
                route = Screen.CaseDetail.route,
                arguments = listOf(navArgument("caseId") { type = NavType.LongType })
            ) { backStackEntry ->
                val caseId = backStackEntry.arguments?.getLong("caseId") ?: 0L
                CaseDetailScreen(
                    caseId = caseId,
                    viewModel = casesViewModel,
                    onBackClick = { navController.popBackStack() },
                    onEditCaseClick = { id -> navController.navigate(Screen.AddEditCase.createRoute(caseId = id)) },
                    onClientClick = { clientId -> navController.navigate(Screen.ClientDetail.createRoute(clientId)) },
                    onAddTaskClick = { id -> navController.navigate(Screen.AddEditTask.createRoute(caseId = id)) },
                    onAddDocumentClick = { id -> navController.navigate(Screen.AddEditDocument.createRoute(caseId = id)) }
                )
            }

            // Add/Edit Case
            composable(
                route = Screen.AddEditCase.route,
                arguments = listOf(
                    navArgument("caseId") { type = NavType.LongType; defaultValue = -1L },
                    navArgument("clientId") { type = NavType.LongType; defaultValue = -1L }
                )
            ) { backStackEntry ->
                val caseId = backStackEntry.arguments?.getLong("caseId")?.takeIf { it > 0 }
                val clientId = backStackEntry.arguments?.getLong("clientId")?.takeIf { it > 0 }
                AddEditCaseScreen(
                    caseId = caseId,
                    initialClientId = clientId,
                    viewModel = casesViewModel,
                    onBackClick = { navController.popBackStack() },
                    onSaveSuccess = { navController.popBackStack() }
                )
            }

            // Tasks List
            composable(Screen.Tasks.route) {
                TasksListScreen(
                    viewModel = tasksViewModel,
                    onTaskClick = { taskId -> navController.navigate(Screen.AddEditTask.createRoute(taskId = taskId)) },
                    onAddTaskClick = { navController.navigate(Screen.AddEditTask.createRoute()) }
                )
            }

            // Add/Edit Task
            composable(
                route = Screen.AddEditTask.route,
                arguments = listOf(
                    navArgument("taskId") { type = NavType.LongType; defaultValue = -1L },
                    navArgument("clientId") { type = NavType.LongType; defaultValue = -1L },
                    navArgument("caseId") { type = NavType.LongType; defaultValue = -1L }
                )
            ) { backStackEntry ->
                val taskId = backStackEntry.arguments?.getLong("taskId")?.takeIf { it > 0 }
                val clientId = backStackEntry.arguments?.getLong("clientId")?.takeIf { it > 0 }
                val caseId = backStackEntry.arguments?.getLong("caseId")?.takeIf { it > 0 }
                AddEditTaskScreen(
                    taskId = taskId,
                    initialClientId = clientId,
                    initialCaseId = caseId,
                    viewModel = tasksViewModel,
                    onBackClick = { navController.popBackStack() },
                    onSaveSuccess = { navController.popBackStack() }
                )
            }

            // Documents List
            composable(Screen.Documents.route) {
                DocumentsListScreen(
                    viewModel = documentsViewModel,
                    onAddDocumentClick = { navController.navigate(Screen.AddEditDocument.createRoute()) },
                    onScannerClick = { navController.navigate(Screen.Scanner.route) }
                )
            }

            // Add Document
            composable(
                route = Screen.AddEditDocument.route,
                arguments = listOf(
                    navArgument("clientId") { type = NavType.LongType; defaultValue = -1L },
                    navArgument("caseId") { type = NavType.LongType; defaultValue = -1L }
                )
            ) { backStackEntry ->
                val clientId = backStackEntry.arguments?.getLong("clientId")?.takeIf { it > 0 }
                val caseId = backStackEntry.arguments?.getLong("caseId")?.takeIf { it > 0 }
                AddDocumentScreen(
                    initialClientId = clientId,
                    initialCaseId = caseId,
                    viewModel = documentsViewModel,
                    onBackClick = { navController.popBackStack() },
                    onSaveSuccess = { navController.popBackStack() }
                )
            }

            // Calendar
            composable(Screen.Calendar.route) {
                CalendarScreen(viewModel = calendarViewModel)
            }

            // Templates Library
            composable(Screen.Templates.route) {
                TemplatesListScreen(
                    viewModel = templatesViewModel,
                    onTemplateSelect = { type -> navController.navigate(Screen.TemplateEditor.createRoute(templateType = type)) },
                    onDraftSelect = { draftId -> navController.navigate(Screen.TemplateEditor.createRoute(draftId = draftId)) }
                )
            }

            // Template Editor
            composable(
                route = Screen.TemplateEditor.route,
                arguments = listOf(
                    navArgument("draftId") { type = NavType.LongType; defaultValue = -1L },
                    navArgument("templateType") { type = NavType.StringType; defaultValue = "" }
                )
            ) { backStackEntry ->
                val draftId = backStackEntry.arguments?.getLong("draftId")?.takeIf { it > 0 }
                val templateType = backStackEntry.arguments?.getString("templateType")?.takeIf { it.isNotBlank() }
                TemplateEditorScreen(
                    draftId = draftId,
                    templateType = templateType,
                    viewModel = templatesViewModel,
                    onBackClick = { navController.popBackStack() }
                )
            }

            // Document Scanner & OCR
            composable(Screen.Scanner.route) {
                ScannerScreen(
                    onBackClick = { navController.popBackStack() },
                    onSaveAsNote = { text ->
                        // Navigates to Templates or Cases with OCR text
                        navController.navigate(Screen.Templates.route)
                    }
                )
            }

            // Reports Screen
            composable(Screen.Reports.route) {
                ReportsScreen(
                    dashboardViewModel = dashboardViewModel,
                    onBackClick = { navController.popBackStack() }
                )
            }

            // More Hub
            composable(Screen.More.route) {
                MoreScreen(
                    onNavigateToTasks = { navController.navigate(Screen.Tasks.route) },
                    onNavigateToDocuments = { navController.navigate(Screen.Documents.route) },
                    onNavigateToTemplates = { navController.navigate(Screen.Templates.route) },
                    onNavigateToScanner = { navController.navigate(Screen.Scanner.route) },
                    onNavigateToReports = { navController.navigate(Screen.Reports.route) },
                    onNavigateToSettings = { navController.navigate(Screen.Settings.route) },
                    onNavigateToSearch = { navController.navigate(Screen.GlobalSearch.route) }
                )
            }

            // Settings
            composable(Screen.Settings.route) {
                SettingsScreen(
                    viewModel = settingsViewModel,
                    onBackClick = { navController.popBackStack() },
                    onResetOnboarding = { navController.navigate(Screen.Onboarding.route) }
                )
            }

            // Global Search
            composable(Screen.GlobalSearch.route) {
                GlobalSearchScreen(
                    app = app,
                    onBackClick = { navController.popBackStack() },
                    onClientClick = { id -> navController.navigate(Screen.ClientDetail.createRoute(id)) },
                    onCaseClick = { id -> navController.navigate(Screen.CaseDetail.createRoute(id)) },
                    onTaskClick = { id -> navController.navigate(Screen.AddEditTask.createRoute(taskId = id)) },
                    onDocumentClick = { navController.navigate(Screen.Documents.route) }
                )
            }

            // Onboarding
            composable(Screen.Onboarding.route) {
                OnboardingScreen(
                    settingsViewModel = settingsViewModel,
                    onComplete = { navController.navigate(Screen.Dashboard.route) { popUpTo(0) } }
                )
            }
        }
    }
}
