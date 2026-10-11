package com.familyroutines.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Send
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.CalendarMonth
import androidx.compose.material.icons.filled.CleaningServices
import androidx.compose.material.icons.filled.Mic
import androidx.compose.material.icons.filled.Schedule
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.familyroutines.data.local.entities.*
import com.familyroutines.ui.components.*
import com.familyroutines.ui.theme.*

@Composable
fun KioskDashboardScreen(
    children: List<ChildEntity>,
    selectedChild: ChildEntity?,
    selectedTab: String,
    routines: List<RoutineItemEntity>,
    chores: List<ChoreEntity>,
    calendarEvents: List<CalendarEventEntity>,
    selectedCalendarDate: String,
    assistantResponse: String,
    isModelLoaded: Boolean,
    isDarkTheme: Boolean,
    onSelectChild: (Long) -> Unit,
    onSelectTab: (String) -> Unit,
    onAddKid: () -> Unit,
    onDeleteChild: (ChildEntity) -> Unit = {},
    onAddChore: () -> Unit,
    onAddRoutine: () -> Unit,
    onSelectCalendarDate: (String) -> Unit,
    onAddEvent: (String) -> Unit,
    onDeleteEvent: (CalendarEventEntity) -> Unit,
    onToggleDarkTheme: () -> Unit,
    onToggleRoutine: (RoutineItemEntity) -> Unit,
    onDeleteRoutine: (RoutineItemEntity) -> Unit = {},
    onToggleChore: (ChoreEntity) -> Unit,
    onDeleteChore: (ChoreEntity) -> Unit = {},
    onAskQuery: (String) -> Unit,
    onRecordVoiceNote: () -> Unit
) {
    var queryInput by remember { mutableStateOf("") }

    val uncompletedRoutines = routines.filter { !it.isCompleted }
    val firstTask = uncompletedRoutines.firstOrNull()
    val thenTask = uncompletedRoutines.drop(1).firstOrNull()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(if (isDarkTheme) DarkBackground else BackgroundSlate)
            .padding(16.dp)
    ) {
        // TOP HEADER: Child Profile Selector Chips & Sensory Dark Theme Toggle
        KidProfileBar(
            children = children,
            selectedChildId = selectedChild?.id,
            isDarkTheme = isDarkTheme,
            onSelectChild = onSelectChild,
            onAddKid = onAddKid,
            onDeleteChild = onDeleteChild,
            onToggleDarkTheme = onToggleDarkTheme,
            modifier = Modifier.padding(bottom = 12.dp)
        )

        Row(
            modifier = Modifier
                .fillMaxSize(),
            horizontalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // --- LEFT PANE: MAIN KIOSK VIEW (Routines / Chores / Calendar) ---
            Card(
                modifier = Modifier
                    .weight(1.35f)
                    .fillMaxHeight(),
                colors = CardDefaults.cardColors(
                    containerColor = if (isDarkTheme) DarkCardBg else CardWhite
                ),
                shape = RoundedCornerShape(24.dp)
            ) {
                Column(
                    modifier = Modifier
                        .padding(20.dp)
                        .fillMaxSize()
                ) {
                    // Navigation Tabs
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .background(
                                if (isDarkTheme) Color(0xFF0F172A) else Color(0xFFF1F5F9),
                                RoundedCornerShape(16.dp)
                            )
                            .padding(4.dp),
                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        val tabs = listOf(
                            Triple("routines", "📋 Routines", Icons.Default.Schedule),
                            Triple("chores", "🧹 Chores & Stars", Icons.Default.CleaningServices),
                            Triple("calendar", "📅 Wall Calendar", Icons.Default.CalendarMonth)
                        )

                        tabs.forEach { (tabKey, title, icon) ->
                            val isSelected = selectedTab == tabKey
                            Box(
                                modifier = Modifier
                                    .weight(1f)
                                    .background(
                                        if (isSelected) (if (isDarkTheme) Color(0xFF2563EB) else PrimaryBlue) else Color.Transparent,
                                        RoundedCornerShape(12.dp)
                                    )
                                    .clickable { onSelectTab(tabKey) }
                                    .padding(vertical = 10.dp),
                                contentAlignment = Alignment.Center
                            ) {
                                Text(
                                    text = title,
                                    fontSize = 15.sp,
                                    fontWeight = if (isSelected) FontWeight.Black else FontWeight.SemiBold,
                                    color = if (isSelected) Color.White else if (isDarkTheme) DarkTextSecondary else TextMuted
                                )
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(14.dp))

                    // Tab Contents
                    when (selectedTab) {
                        "routines" -> {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(
                                    text = "${selectedChild?.name ?: "Kid"}'s Routines",
                                    fontSize = 20.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = if (isDarkTheme) DarkTextPrimary else TextDark
                                )
                                Button(
                                    onClick = onAddRoutine,
                                    shape = RoundedCornerShape(12.dp),
                                    colors = ButtonDefaults.buttonColors(containerColor = PrimaryBlue)
                                ) {
                                    Icon(Icons.Default.Add, contentDescription = "Add Routine", modifier = Modifier.size(16.dp))
                                    Spacer(modifier = Modifier.width(4.dp))
                                    Text("Add Routine")
                                }
                            }

                            Spacer(modifier = Modifier.height(10.dp))

                            // First-Then Banner
                            FirstThenBoard(
                                firstItem = firstTask,
                                thenItem = thenTask,
                                isDarkTheme = isDarkTheme,
                                modifier = Modifier.padding(bottom = 14.dp)
                            )

                            LazyColumn(
                                verticalArrangement = Arrangement.spacedBy(12.dp),
                                modifier = Modifier.fillMaxSize()
                            ) {
                                items(routines, key = { it.id }) { item ->
                                    RoutineItemCard(
                                        item = item,
                                        onToggle = { onToggleRoutine(item) },
                                        onDelete = { onDeleteRoutine(item) },
                                        isDarkTheme = isDarkTheme
                                    )
                                }
                            }
                        }

                        "chores" -> {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(
                                    text = "${selectedChild?.name ?: "Kid"}'s Chores",
                                    fontSize = 20.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = if (isDarkTheme) DarkTextPrimary else TextDark
                                )
                                Button(
                                    onClick = onAddChore,
                                    shape = RoundedCornerShape(12.dp),
                                    colors = ButtonDefaults.buttonColors(containerColor = PrimaryBlue)
                                ) {
                                    Icon(Icons.Default.Add, contentDescription = "Add Chore", modifier = Modifier.size(16.dp))
                                    Spacer(modifier = Modifier.width(4.dp))
                                    Text("Add Chore")
                                }
                            }

                            Spacer(modifier = Modifier.height(10.dp))

                            LazyColumn(
                                verticalArrangement = Arrangement.spacedBy(12.dp),
                                modifier = Modifier.fillMaxSize()
                            ) {
                                items(chores, key = { it.id }) { chore ->
                                    ChoreItemCard(
                                        chore = chore,
                                        onToggle = { onToggleChore(chore) },
                                        onDelete = { onDeleteChore(chore) },
                                        isDarkTheme = isDarkTheme
                                    )
                                }
                            }
                        }

                        "calendar" -> {
                            MonthCalendarView(
                                events = calendarEvents,
                                children = children,
                                selectedDateString = selectedCalendarDate,
                                isDarkTheme = isDarkTheme,
                                onSelectDate = onSelectCalendarDate,
                                onAddEvent = onAddEvent,
                                onDeleteEvent = onDeleteEvent,
                                modifier = Modifier.fillMaxSize()
                            )
                        }
                    }
                }
            }

            // --- RIGHT PANE: ROUTINES A.I BRAIN & VOICE MEMORY ENGINE ---
            Card(
                modifier = Modifier
                    .weight(0.95f)
                    .fillMaxHeight(),
                colors = CardDefaults.cardColors(
                    containerColor = if (isDarkTheme) DarkAssistantCard else PrimaryBlueBg
                ),
                shape = RoundedCornerShape(24.dp)
            ) {
                Column(
                    modifier = Modifier
                        .padding(20.dp)
                        .fillMaxSize()
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column {
                            Text(
                                text = "Routines A.I Brain",
                                fontSize = 22.sp,
                                fontWeight = FontWeight.Bold,
                                color = if (isDarkTheme) Color(0xFF93C5FD) else PrimaryBlueText
                            )
                            Text(
                                text = if (isModelLoaded) "Gemma 2B Ready (GPU)" else "Keyword Index Mode",
                                fontSize = 12.sp,
                                color = if (isModelLoaded) Color(0xFF4ADE80) else Color(0xFFFBBF24)
                            )
                        }
                        IconButton(
                            onClick = onRecordVoiceNote,
                            modifier = Modifier.background(PrimaryBlue, RoundedCornerShape(12.dp))
                        ) {
                            Icon(Icons.Default.Mic, contentDescription = "Record Note", tint = Color.White)
                        }
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .fillMaxWidth()
                            .background(
                                if (isDarkTheme) DarkAssistantInner else Color.White,
                                RoundedCornerShape(16.dp)
                            )
                            .padding(16.dp)
                    ) {
                        Text(
                            text = if (assistantResponse.isBlank()) {
                                "Ask how to prep meals, sensory quirks, calm-down routines, or rules for ${selectedChild?.name ?: "kids"}..."
                            } else {
                                assistantResponse
                            },
                            fontSize = 16.sp,
                            color = if (assistantResponse.isBlank()) {
                                if (isDarkTheme) DarkTextSecondary else Color.Gray
                            } else {
                                if (isDarkTheme) DarkTextPrimary else TextDark
                            },
                            lineHeight = 22.sp
                        )
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        OutlinedTextField(
                            value = queryInput,
                            onValueChange = { queryInput = it },
                            modifier = Modifier.weight(1f),
                            placeholder = {
                                Text(
                                    "Ask about ${selectedChild?.name ?: "kid"}...",
                                    color = if (isDarkTheme) DarkTextSecondary else Color.Gray
                                )
                            },
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedTextColor = if (isDarkTheme) DarkTextPrimary else TextDark,
                                unfocusedTextColor = if (isDarkTheme) DarkTextPrimary else TextDark,
                                focusedContainerColor = if (isDarkTheme) DarkAssistantInner else Color.White,
                                unfocusedContainerColor = if (isDarkTheme) DarkAssistantInner else Color.White,
                            ),
                            shape = RoundedCornerShape(12.dp)
                        )
                        Button(
                            onClick = {
                                if (queryInput.isNotBlank()) {
                                    onAskQuery(queryInput)
                                    queryInput = ""
                                }
                            },
                            colors = ButtonDefaults.buttonColors(containerColor = PrimaryBlue),
                            shape = RoundedCornerShape(12.dp)
                        ) {
                            Icon(
                                imageVector = Icons.AutoMirrored.Filled.Send,
                                contentDescription = "Submit Query"
                            )
                        }
                    }
                }
            }
        }
    }
}
