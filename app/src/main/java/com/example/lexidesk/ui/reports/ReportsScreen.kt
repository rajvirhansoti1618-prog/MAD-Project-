package com.example.lexidesk.ui.reports

import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Assignment
import androidx.compose.material.icons.filled.Description
import androidx.compose.material.icons.filled.Folder
import androidx.compose.material.icons.filled.People
import androidx.compose.material.icons.filled.PictureAsPdf
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.lexidesk.pdf.PdfGenerator
import com.example.lexidesk.ui.common.SummaryMetricCard
import com.example.lexidesk.ui.dashboard.DashboardViewModel
import com.example.lexidesk.ui.theme.AmberAccent
import com.example.lexidesk.ui.theme.BlueInfo
import com.example.lexidesk.ui.theme.GreenSuccess
import com.example.lexidesk.ui.theme.NavyPrimary
import com.example.lexidesk.ui.theme.RedUrgent
import com.example.lexidesk.ui.theme.TealSecondary

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ReportsScreen(
    dashboardViewModel: DashboardViewModel,
    onBackClick: () -> Unit
) {
    val context = LocalContext.current
    val state by dashboardViewModel.uiState.collectAsState()

    val totalCases = state.activeCasesCount // approximate or total
    // Use the pre-computed completionRate from DashboardViewModel (completedTasks / (pendingTasks + completedTasks), defaults to 0f when no tasks)
    val taskCompletionRate = state.completionRate

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Productivity & Case Analytics") },
                navigationIcon = {
                    IconButton(onClick = onBackClick) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                    }
                }
            )
        }
    ) { paddingValues ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .background(MaterialTheme.colorScheme.background),
            contentPadding = PaddingValues(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            item {
                Text(
                    text = "Executive Performance Overview",
                    style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold),
                    color = MaterialTheme.colorScheme.onBackground
                )
            }

            // Summary Metrics Grid
            item {
                Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        SummaryMetricCard(
                            title = "Active Clients",
                            value = state.clientCount.toString(),
                            icon = Icons.Default.People,
                            iconColor = NavyPrimary,
                            modifier = Modifier.weight(1f)
                        )
                        SummaryMetricCard(
                            title = "Active Matters",
                            value = state.activeCasesCount.toString(),
                            icon = Icons.Default.Folder,
                            iconColor = TealSecondary,
                            modifier = Modifier.weight(1f)
                        )
                    }

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        SummaryMetricCard(
                            title = "Pending Tasks",
                            value = state.pendingTasksCount.toString(),
                            icon = Icons.Default.Assignment,
                            iconColor = BlueInfo,
                            modifier = Modifier.weight(1f)
                        )
                        SummaryMetricCard(
                            title = "Overdue Deadlines",
                            value = state.overdueTasksCount.toString(),
                            icon = Icons.Default.Warning,
                            iconColor = if (state.overdueTasksCount > 0) RedUrgent else GreenSuccess,
                            modifier = Modifier.weight(1f)
                        )
                    }
                }
            }

            // Case Delivery Progress Bar Card
            item {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Text(
                            text = "Workload & Delivery Efficiency",
                            style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold)
                        )
                        Spacer(modifier = Modifier.height(10.dp))
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text("Completion Rate", style = MaterialTheme.typography.bodyMedium)
                            Text("${(taskCompletionRate * 100).toInt()}%", style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Bold), color = TealSecondary)
                        }
                        Spacer(modifier = Modifier.height(8.dp))
                        LinearProgressIndicator(
                            progress = { taskCompletionRate },
                            modifier = Modifier.fillMaxWidth().height(8.dp),
                            color = TealSecondary,
                            trackColor = MaterialTheme.colorScheme.surfaceVariant
                        )
                    }
                }
            }

            // Document Vault Summary Card
            item {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
                ) {
                    Row(
                        modifier = Modifier.padding(16.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(Icons.Default.Description, contentDescription = null, tint = NavyPrimary, modifier = Modifier.size(32.dp))
                        Spacer(modifier = Modifier.width(14.dp))
                        Column(modifier = Modifier.weight(1f)) {
                            Text("Document Vault Records", style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold))
                            Text("${state.documentCount} stored files and contracts", style = MaterialTheme.typography.bodyMedium, color = MaterialTheme.colorScheme.onSurfaceVariant)
                        }
                    }
                }
            }

            // Export Performance Report as PDF
            item {
                Button(
                    onClick = {
                        val reportContent = """
LEXIDESK EXECUTIVE PRODUCTIVITY REPORT
Generated: ${java.text.SimpleDateFormat("dd MMMM yyyy, hh:mm a", java.util.Locale.getDefault()).format(java.util.Date())}

1. EXECUTIVE SUMMARY
- Total Retained Clients: ${state.clientCount}
- Active Cases & Projects: ${state.activeCasesCount}
- Open Tasks: ${state.pendingTasksCount}
- Overdue Deadlines: ${state.overdueTasksCount}
- Documents in Vault: ${state.documentCount}

2. PRACTICE RECOMMENDATIONS
- Ensure all ${state.overdueTasksCount} overdue deadlines are reviewed and rescheduled.
- Proceed with pending draft deliveries to maintain high client satisfaction.

Generated automatically by LexiDesk Professional Suite.
                        """.trimIndent()

                        val file = PdfGenerator.generateDocumentPdf(
                            context = context,
                            title = "Executive Productivity Report",
                            content = reportContent,
                            firmName = state.userProfile.firmName.ifBlank { "LexiDesk Professional Services" }
                        )

                        if (file != null) {
                            PdfGenerator.sharePdf(context, file, "LexiDesk Productivity Report")
                        } else {
                            Toast.makeText(context, "Failed to create PDF report", Toast.LENGTH_SHORT).show()
                        }
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(50.dp),
                    shape = RoundedCornerShape(12.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = NavyPrimary)
                ) {
                    Icon(Icons.Default.PictureAsPdf, contentDescription = null)
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("Export Executive PDF Report")
                }
            }
        }
    }
}
