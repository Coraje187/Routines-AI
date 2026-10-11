package com.familyroutines.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CalendarToday
import androidx.compose.material.icons.filled.Schedule
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.familyroutines.data.local.entities.CalendarEventEntity
import com.familyroutines.data.local.entities.ChildEntity
import com.familyroutines.ui.theme.*

@Composable
fun FamilyCalendarView(
    events: List<CalendarEventEntity>,
    children: List<ChildEntity>,
    isDarkTheme: Boolean = false,
    modifier: Modifier = Modifier
) {
    var selectedDayFilter by remember { mutableStateOf("All") }

    val days = listOf("All", "Today", "Tomorrow", "Saturday")
    val filteredEvents = if (selectedDayFilter == "All") {
        events
    } else {
        events.filter { it.dayLabel.equals(selectedDayFilter, ignoreCase = true) }
    }

    Column(
        modifier = modifier.fillMaxSize(),
        verticalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        // Day filter bar
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            days.forEach { day ->
                FilterChip(
                    selected = selectedDayFilter == day,
                    onClick = { selectedDayFilter = day },
                    label = { Text(day, fontWeight = FontWeight.Bold) },
                    colors = FilterChipDefaults.filterChipColors(
                        selectedContainerColor = PrimaryBlue,
                        selectedLabelColor = Color.White
                    )
                )
            }
        }

        // Timeline list
        if (filteredEvents.isEmpty()) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(32.dp),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = "No events scheduled for $selectedDayFilter.",
                    fontSize = 18.sp,
                    color = if (isDarkTheme) DarkTextSecondary else TextMuted
                )
            }
        } else {
            LazyColumn(
                verticalArrangement = Arrangement.spacedBy(12.dp),
                modifier = Modifier.fillMaxSize()
            ) {
                items(filteredEvents, key = { it.id }) { event ->
                    val child = children.find { it.id == event.childId }
                    val eventColor = try {
                        Color(android.graphics.Color.parseColor(event.colorHex))
                    } catch (e: Exception) {
                        PrimaryBlue
                    }

                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .background(
                                if (isDarkTheme) DarkCardBg else Color(0xFFF8FAFC),
                                RoundedCornerShape(16.dp)
                            )
                            .border(
                                2.dp,
                                if (isDarkTheme) DarkCardBorder else Color(0xFFE2E8F0),
                                RoundedCornerShape(16.dp)
                            )
                            .padding(16.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(14.dp),
                            modifier = Modifier.weight(1f)
                        ) {
                            // Left Accent bar
                            Box(
                                modifier = Modifier
                                    .width(6.dp)
                                    .height(48.dp)
                                    .background(eventColor, RoundedCornerShape(4.dp))
                            )

                            Column {
                                Text(
                                    text = event.title,
                                    fontSize = 20.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = if (isDarkTheme) DarkTextPrimary else TextDark
                                )
                                Spacer(modifier = Modifier.height(4.dp))
                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.Schedule,
                                        contentDescription = "Time",
                                        tint = if (isDarkTheme) DarkTextSecondary else TextMuted,
                                        modifier = Modifier.size(16.dp)
                                    )
                                    Text(
                                        text = "${event.dayLabel} • ${event.timeSlot}",
                                        fontSize = 14.sp,
                                        fontWeight = FontWeight.Medium,
                                        color = if (isDarkTheme) DarkTextSecondary else TextMuted
                                    )
                                }
                            }
                        }

                        // Child or Family tag pill
                        Box(
                            modifier = Modifier
                                .background(
                                    eventColor.copy(alpha = 0.15f),
                                    RoundedCornerShape(12.dp)
                                )
                                .border(1.dp, eventColor, RoundedCornerShape(12.dp))
                                .padding(horizontal = 12.dp, vertical = 6.dp)
                        ) {
                            Text(
                                text = child?.name ?: "Family",
                                fontSize = 14.sp,
                                fontWeight = FontWeight.Bold,
                                color = eventColor
                            )
                        }
                    }
                }
            }
        }
    }
}
