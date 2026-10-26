package com.example.lexidesk.ui.calendar

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowLeft
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowRight
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.CalendarMonth
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Event
import androidx.compose.material.icons.filled.Gavel
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.FilterChip
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.lexidesk.ui.common.TimePickerModalDialog
import com.example.lexidesk.ui.theme.AmberAccent
import com.example.lexidesk.ui.theme.BlueInfo
import com.example.lexidesk.ui.theme.GreenSuccess
import com.example.lexidesk.ui.theme.NavyPrimary
import com.example.lexidesk.ui.theme.RedUrgent
import com.example.lexidesk.ui.theme.TealSecondary
import com.example.lexidesk.utils.DateUtils
import java.text.DateFormatSymbols
import java.util.Calendar

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CalendarScreen(
    viewModel: CalendarViewModel
) {
    val state by viewModel.uiState.collectAsState()
    var showAddEventDialog by remember { mutableStateOf(false) }

    val monthNames = DateFormatSymbols().months
    val currentMonthName = monthNames[state.selectedMonth]

    if (showAddEventDialog) {
        var eventTitle by remember { mutableStateOf("") }
        var eventDescription by remember { mutableStateOf("") }
        var eventType by remember { mutableStateOf("Client Meeting") }
        var minutesBefore by remember { mutableIntStateOf(30) }
        var eventTimeFormatted by remember { mutableStateOf("10:00 AM") }
        var showTimePicker by remember { mutableStateOf(false) }

        val eventTypes = listOf("Court Hearing", "Client Meeting", "Tax Filing Deadline", "Document Submission", "Custom")

        if (showTimePicker) {
            TimePickerModalDialog(
                onTimeSelected = { eventTimeFormatted = it },
                onDismiss = { showTimePicker = false }
            )
        }

        AlertDialog(
            onDismissRequest = { showAddEventDialog = false },
            shape = RoundedCornerShape(24.dp),
            title = { Text("Schedule Event / Reminder", style = MaterialTheme.typography.titleLarge) },
            text = {
                Column(
                    modifier = Modifier.verticalScroll(rememberScrollState()),
                    verticalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    OutlinedTextField(
                        value = eventTitle,
                        onValueChange = { eventTitle = it },
                        label = { Text("Event Title *") },
                        modifier = Modifier.fillMaxWidth(),
                        singleLine = true
                    )

                    var typeExpanded by remember { mutableStateOf(false) }
                    ExposedDropdownMenuBox(
                        expanded = typeExpanded,
                        onExpandedChange = { typeExpanded = it }
                    ) {
                        OutlinedTextField(
                            value = eventType,
                            onValueChange = {},
                            readOnly = true,
                            label = { Text("Event Category") },
                            trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = typeExpanded) },
                            modifier = Modifier.fillMaxWidth().menuAnchor()
                        )
                        ExposedDropdownMenu(
                            expanded = typeExpanded,
                            onDismissRequest = { typeExpanded = false }
                        ) {
                            eventTypes.forEach { t ->
                                DropdownMenuItem(
                                    text = { Text(t) },
                                    onClick = {
                                        eventType = t
                                        typeExpanded = false
                                    }
                                )
                            }
                        }
                    }

                    Box(modifier = Modifier.fillMaxWidth().clickable { showTimePicker = true }) {
                        OutlinedTextField(
                            value = eventTimeFormatted,
                            onValueChange = {},
                            readOnly = true,
                            label = { Text("Scheduled Time") },
                            modifier = Modifier.fillMaxWidth(),
                            enabled = false
                        )
                    }

                    Text("Reminder Notification", style = MaterialTheme.typography.labelMedium)
                    Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                        listOf(0 to "At time", 15 to "15m", 30 to "30m", 60 to "1h", 1440 to "1d").forEach { (min, label) ->
                            FilterChip(
                                selected = minutesBefore == min,
                                onClick = { minutesBefore = min },
                                label = { Text(label, fontSize = 11.sp) }
                            )
                        }
                    }

                    OutlinedTextField(
                        value = eventDescription,
                        onValueChange = { eventDescription = it },
                        label = { Text("Notes / Venue (Optional)") },
                        modifier = Modifier.fillMaxWidth()
                    )
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        if (eventTitle.isNotBlank()) {
                            viewModel.addAppointment(
                                title = eventTitle,
                                description = eventDescription,
                                type = eventType,
                                timestamp = state.selectedDateMillis,
                                minutesBefore = minutesBefore
                            )
                            showAddEventDialog = false
                        }
                    }
                ) {
                    Text("Schedule")
                }
            },
            dismissButton = {
                OutlinedButton(onClick = { showAddEventDialog = false }) {
                    Text("Cancel")
                }
            }
        )
    }

    Scaffold { paddingValues ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .background(MaterialTheme.colorScheme.background),
            contentPadding = PaddingValues(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Month Header & Navigation
            item {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Text(
                            text = "$currentMonthName ${state.selectedYear}",
                            style = MaterialTheme.typography.headlineMedium.copy(fontWeight = FontWeight.Bold),
                            color = MaterialTheme.colorScheme.onBackground
                        )
                        Text(
                            text = "Schedule & Deadlines",
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }

                    Row {
                        IconButton(onClick = { viewModel.onMonthChanged(-1) }) {
                            Icon(Icons.AutoMirrored.Filled.KeyboardArrowLeft, contentDescription = "Previous Month")
                        }
                        IconButton(onClick = { viewModel.onMonthChanged(1) }) {
                            Icon(Icons.AutoMirrored.Filled.KeyboardArrowRight, contentDescription = "Next Month")
                        }
                    }
                }
            }

            // Month Grid Card
            item {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                    elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
                ) {
                    Column(modifier = Modifier.padding(14.dp)) {
                        // Weekday headers
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceAround
                        ) {
                            listOf("Sun", "Mon", "Tue", "Wed", "Thu", "Fri", "Sat").forEach { day ->
                                Text(
                                    text = day,
                                    modifier = Modifier.width(36.dp),
                                    textAlign = TextAlign.Center,
                                    style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.SemiBold),
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }
                        }

                        Spacer(modifier = Modifier.height(10.dp))

                        // Days grid
                        val cal = Calendar.getInstance().apply {
                            set(Calendar.YEAR, state.selectedYear)
                            set(Calendar.MONTH, state.selectedMonth)
                            set(Calendar.DAY_OF_MONTH, 1)
                        }
                        val firstDayOfWeek = cal.get(Calendar.DAY_OF_WEEK) - 1 // 0 for Sunday
                        val daysInMonth = cal.getActualMaximum(Calendar.DAY_OF_MONTH)

                        val totalSlots = ((firstDayOfWeek + daysInMonth + 6) / 7) * 7

                        for (week in 0 until (totalSlots / 7)) {
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(vertical = 4.dp),
                                horizontalArrangement = Arrangement.SpaceAround
                            ) {
                                for (dayOfWeek in 0..6) {
                                    val slotIndex = week * 7 + dayOfWeek
                                    val dayNumber = slotIndex - firstDayOfWeek + 1

                                    if (dayNumber in 1..daysInMonth) {
                                        val isSelected = state.selectedDay == dayNumber
                                        val hasEvents = state.allEventsInMonth.any { event ->
                                            val eCal = Calendar.getInstance().apply { timeInMillis = event.timestamp }
                                            eCal.get(Calendar.YEAR) == state.selectedYear &&
                                                    eCal.get(Calendar.MONTH) == state.selectedMonth &&
                                                    eCal.get(Calendar.DAY_OF_MONTH) == dayNumber
                                        }

                                        Box(
                                            modifier = Modifier
                                                .size(36.dp)
                                                .clip(CircleShape)
                                                .background(
                                                    if (isSelected) MaterialTheme.colorScheme.primary else Color.Transparent
                                                )
                                                .clickable {
                                                    viewModel.onDateSelected(state.selectedYear, state.selectedMonth, dayNumber)
                                                },
                                            contentAlignment = Alignment.Center
                                        ) {
                                            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                                Text(
                                                    text = dayNumber.toString(),
                                                    style = MaterialTheme.typography.bodyMedium.copy(
                                                        fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal
                                                    ),
                                                    color = if (isSelected) Color.White else MaterialTheme.colorScheme.onSurface
                                                )
                                                if (hasEvents && !isSelected) {
                                                    Box(
                                                        modifier = Modifier
                                                            .size(4.dp)
                                                            .clip(CircleShape)
                                                            .background(TealSecondary)
                                                    )
                                                }
                                            }
                                        }
                                    } else {
                                        Spacer(modifier = Modifier.size(36.dp))
                                    }
                                }
                            }
                        }
                    }
                }
            }

            // Agenda for Selected Date
            item {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Text(
                            text = DateUtils.formatDate(state.selectedDateMillis),
                            style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold),
                            color = MaterialTheme.colorScheme.onSurface
                        )
                        Text(
                            text = "${state.eventsForSelectedDate.size} event(s) scheduled",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }

                    Button(
                        onClick = { showAddEventDialog = true },
                        shape = RoundedCornerShape(10.dp)
                    ) {
                        Icon(Icons.Default.Add, contentDescription = null, modifier = Modifier.size(16.dp))
                        Spacer(modifier = Modifier.width(4.dp))
                        Text("+ Event", fontSize = 12.sp)
                    }
                }
            }

            if (state.eventsForSelectedDate.isEmpty()) {
                item {
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(12.dp),
                        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
                    ) {
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(20.dp),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Text(
                                text = "No appointments or deadlines on this date.",
                                style = MaterialTheme.typography.bodyMedium,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                            Spacer(modifier = Modifier.height(8.dp))
                            TextButton(onClick = { showAddEventDialog = true }) {
                                Icon(Icons.Default.Add, contentDescription = null, modifier = Modifier.size(16.dp))
                                Spacer(modifier = Modifier.width(4.dp))
                                Text("Schedule on this date", fontSize = 13.sp)
                            }
                        }
                    }
                }
            } else {
                items(state.eventsForSelectedDate) { event ->
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(12.dp),
                        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(14.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(38.dp)
                                    .clip(CircleShape)
                                    .background(
                                        when {
                                            event.type.contains("Hearing", ignoreCase = true) -> RedUrgent.copy(alpha = 0.15f)
                                            event.type.contains("Meeting", ignoreCase = true) -> TealSecondary.copy(alpha = 0.15f)
                                            else -> AmberAccent.copy(alpha = 0.15f)
                                        }
                                    ),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(
                                    imageVector = if (event.type.contains("Hearing")) Icons.Default.Gavel else Icons.Default.Event,
                                    contentDescription = null,
                                    tint = when {
                                        event.type.contains("Hearing", ignoreCase = true) -> RedUrgent
                                        event.type.contains("Meeting", ignoreCase = true) -> TealSecondary
                                        else -> AmberAccent
                                    },
                                    modifier = Modifier.size(20.dp)
                                )
                            }

                            Spacer(modifier = Modifier.width(12.dp))

                            Column(modifier = Modifier.weight(1f)) {
                                Text(
                                    text = event.title,
                                    style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.SemiBold),
                                    color = MaterialTheme.colorScheme.onSurface
                                )
                                Text(
                                    text = "${event.type} • ${DateUtils.formatTime(event.timestamp)}",
                                    style = MaterialTheme.typography.bodySmall,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }

                            IconButton(onClick = { viewModel.deleteReminder(event.id) }) {
                                Icon(Icons.Default.Delete, contentDescription = "Delete", tint = MaterialTheme.colorScheme.outline, modifier = Modifier.size(18.dp))
                            }
                        }
                    }
                }
            }
        }
    }
}
