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
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.familyroutines.data.local.entities.CalendarEventEntity
import com.familyroutines.data.local.entities.ChildEntity
import com.familyroutines.ui.theme.*
import java.time.LocalDate
import java.time.YearMonth
import java.time.format.DateTimeFormatter
import java.time.format.TextStyle
import java.util.Locale

@Composable
fun MonthCalendarView(
    events: List<CalendarEventEntity>,
    children: List<ChildEntity>,
    selectedDateString: String,
    isDarkTheme: Boolean = false,
    onSelectDate: (String) -> Unit,
    onAddEvent: (preselectedDate: String) -> Unit,
    onDeleteEvent: (CalendarEventEntity) -> Unit,
    modifier: Modifier = Modifier
) {
    // Current viewed YearMonth (default October 2026)
    var currentYearMonth by remember {
        val initial = try {
            YearMonth.parse(selectedDateString.substring(0, 7))
        } catch (e: Exception) {
            YearMonth.of(2026, 10)
        }
        mutableStateOf(initial)
    }

    val today = LocalDate.of(2026, 10, 10)
    val todayString = today.format(DateTimeFormatter.ISO_LOCAL_DATE)

    val selectedDate = try {
        LocalDate.parse(selectedDateString)
    } catch (e: Exception) {
        today
    }

    // Days calculation for current month grid
    val firstDayOfMonth = currentYearMonth.atDay(1)
    val daysInMonth = currentYearMonth.lengthOfMonth()
    // DayOfWeek: 1 = Monday, 7 = Sunday. For Sunday-first calendar: Sunday is 0, Monday is 1, etc.
    val firstDayOffset = firstDayOfMonth.dayOfWeek.value % 7

    // Events on currently selected day
    val selectedDayEvents = events.filter { it.dateString == selectedDateString }

    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(2.dp),
        verticalArrangement = Arrangement.spacedBy(10.dp)
    ) {
        // --- CALENDAR HEADER: Month Navigator & Action Buttons ---
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Month navigation controls
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                IconButton(
                    onClick = { currentYearMonth = currentYearMonth.minusMonths(1) },
                    modifier = Modifier
                        .size(36.dp)
                        .background(
                            if (isDarkTheme) DarkAssistantInner else Color(0xFFF1F5F9),
                            RoundedCornerShape(8.dp)
                        )
                ) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                        contentDescription = "Previous Month",
                        tint = if (isDarkTheme) DarkTextPrimary else TextDark,
                        modifier = Modifier.size(20.dp)
                    )
                }

                Text(
                    text = "${currentYearMonth.month.getDisplayName(TextStyle.FULL, Locale.getDefault())} ${currentYearMonth.year}",
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Black,
                    color = if (isDarkTheme) DarkTextPrimary else TextDark
                )

                IconButton(
                    onClick = { currentYearMonth = currentYearMonth.plusMonths(1) },
                    modifier = Modifier
                        .size(36.dp)
                        .background(
                            if (isDarkTheme) DarkAssistantInner else Color(0xFFF1F5F9),
                            RoundedCornerShape(8.dp)
                        )
                ) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.ArrowForward,
                        contentDescription = "Next Month",
                        tint = if (isDarkTheme) DarkTextPrimary else TextDark,
                        modifier = Modifier.size(20.dp)
                    )
                }

                // "Today" shortcut chip
                OutlinedButton(
                    onClick = {
                        currentYearMonth = YearMonth.of(today.year, today.month)
                        onSelectDate(todayString)
                    },
                    shape = RoundedCornerShape(10.dp),
                    contentPadding = PaddingValues(horizontal = 12.dp, vertical = 4.dp),
                    modifier = Modifier.height(36.dp)
                ) {
                    Text(
                        text = "Today",
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Bold,
                        color = PrimaryBlue
                    )
                }
            }

            // Right: Add Event Button
            Button(
                onClick = { onAddEvent(selectedDateString) },
                colors = ButtonDefaults.buttonColors(containerColor = PrimaryBlue),
                shape = RoundedCornerShape(12.dp),
                contentPadding = PaddingValues(horizontal = 14.dp, vertical = 6.dp)
            ) {
                Icon(Icons.Default.Add, contentDescription = null, modifier = Modifier.size(18.dp))
                Spacer(modifier = Modifier.width(4.dp))
                Text("Add Event", fontSize = 14.sp, fontWeight = FontWeight.Bold)
            }
        }

        // --- WEEKDAY LABELS (Sun, Mon, Tue, Wed, Thu, Fri, Sat) ---
        val weekdays = listOf("SUN", "MON", "TUE", "WED", "THU", "FRI", "SAT")
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            weekdays.forEach { dayName ->
                Text(
                    text = dayName,
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Bold,
                    color = if (dayName == "SUN" || dayName == "SAT") Color(0xFFEF4444) else (if (isDarkTheme) DarkTextSecondary else TextMuted),
                    textAlign = TextAlign.Center,
                    modifier = Modifier.weight(1f)
                )
            }
        }

        // --- MONTH CALENDAR GRID ---
        val totalCells = firstDayOffset + daysInMonth
        val rows = (totalCells + 6) / 7

        Column(
            modifier = Modifier
                .fillMaxWidth()
                .background(
                    if (isDarkTheme) Color(0xFF0F172A) else Color(0xFFF8FAFC),
                    RoundedCornerShape(16.dp)
                )
                .border(
                    1.dp,
                    if (isDarkTheme) DarkCardBorder else Color(0xFFE2E8F0),
                    RoundedCornerShape(16.dp)
                )
                .padding(6.dp),
            verticalArrangement = Arrangement.spacedBy(4.dp)
        ) {
            for (rowIndex in 0 until rows) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    for (colIndex in 0..6) {
                        val cellIndex = rowIndex * 7 + colIndex
                        val dayNumber = cellIndex - firstDayOffset + 1

                        if (dayNumber in 1..daysInMonth) {
                            val cellDate = currentYearMonth.atDay(dayNumber)
                            val cellDateStr = cellDate.format(DateTimeFormatter.ISO_LOCAL_DATE)
                            val isCellToday = cellDateStr == todayString
                            val isCellSelected = cellDateStr == selectedDateString

                            // Events on this specific date
                            val cellEvents = events.filter { it.dateString == cellDateStr }

                            Box(
                                modifier = Modifier
                                    .weight(1f)
                                    .height(58.dp)
                                    .clip(RoundedCornerShape(10.dp))
                                    .background(
                                        when {
                                            isCellSelected -> if (isDarkTheme) Color(0xFF1E3A8A) else Color(0xFFDBEAFE)
                                            isCellToday -> if (isDarkTheme) Color(0xFF1E293B) else Color(0xFFEFF6FF)
                                            else -> if (isDarkTheme) DarkCardBg else Color.White
                                        }
                                    )
                                    .border(
                                        width = when {
                                            isCellSelected -> 2.dp
                                            isCellToday -> 2.dp
                                            else -> 1.dp
                                        },
                                        color = when {
                                            isCellSelected -> PrimaryBlue
                                            isCellToday -> Color(0xFF3B82F6)
                                            else -> if (isDarkTheme) DarkCardBorder.copy(alpha = 0.5f) else Color(0xFFE2E8F0)
                                        },
                                        shape = RoundedCornerShape(10.dp)
                                    )
                                    .clickable { onSelectDate(cellDateStr) }
                                    .padding(4.dp)
                            ) {
                                Column(modifier = Modifier.fillMaxSize()) {
                                    // Day Number Row
                                    Row(
                                        modifier = Modifier.fillMaxWidth(),
                                        horizontalArrangement = Arrangement.SpaceBetween,
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        Text(
                                            text = "$dayNumber",
                                            fontSize = 12.sp,
                                            fontWeight = if (isCellToday || isCellSelected) FontWeight.Black else FontWeight.SemiBold,
                                            color = when {
                                                isCellSelected -> if (isDarkTheme) Color.White else PrimaryBlue
                                                isCellToday -> PrimaryBlue
                                                else -> if (isDarkTheme) DarkTextPrimary else TextDark
                                            }
                                        )

                                        if (isCellToday) {
                                            Box(
                                                modifier = Modifier
                                                    .background(PrimaryBlue, RoundedCornerShape(4.dp))
                                                    .padding(horizontal = 4.dp, vertical = 1.dp)
                                            ) {
                                                Text(
                                                    text = "TODAY",
                                                    fontSize = 8.sp,
                                                    fontWeight = FontWeight.Bold,
                                                    color = Color.White
                                                )
                                            }
                                        }
                                    }

                                    Spacer(modifier = Modifier.height(2.dp))

                                    // Compact Event Badges
                                    if (cellEvents.isNotEmpty()) {
                                        cellEvents.take(2).forEach { ev ->
                                            val evColor = try {
                                                Color(android.graphics.Color.parseColor(ev.colorHex))
                                            } catch (e: Exception) {
                                                PrimaryBlue
                                            }

                                            Row(
                                                modifier = Modifier
                                                    .fillMaxWidth()
                                                    .padding(vertical = 1.dp)
                                                    .background(evColor.copy(alpha = 0.18f), RoundedCornerShape(3.dp))
                                                    .padding(horizontal = 2.dp, vertical = 1.dp),
                                                verticalAlignment = Alignment.CenterVertically
                                            ) {
                                                Box(
                                                    modifier = Modifier
                                                        .size(5.dp)
                                                        .background(evColor, CircleShape)
                                                )
                                                Spacer(modifier = Modifier.width(3.dp))
                                                Text(
                                                    text = ev.title,
                                                    fontSize = 9.sp,
                                                    fontWeight = FontWeight.Bold,
                                                    color = if (isDarkTheme) Color.White else evColor,
                                                    maxLines = 1,
                                                    overflow = TextOverflow.Ellipsis
                                                )
                                            }
                                        }

                                        if (cellEvents.size > 2) {
                                            Text(
                                                text = "+${cellEvents.size - 2} more",
                                                fontSize = 8.sp,
                                                fontWeight = FontWeight.Bold,
                                                color = if (isDarkTheme) DarkTextSecondary else TextMuted
                                            )
                                        }
                                    }
                                }
                            }
                        } else {
                            // Blank cell outside month bounds
                            Box(
                                modifier = Modifier
                                    .weight(1f)
                                    .height(58.dp)
                            )
                        }
                    }
                }
            }
        }

        // --- SELECTED DAY AGENDA DRAWER ---
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            val dateLabel = try {
                "${selectedDate.dayOfWeek.getDisplayName(TextStyle.FULL, Locale.getDefault())}, ${selectedDate.month.getDisplayName(TextStyle.FULL, Locale.getDefault())} ${selectedDate.dayOfMonth}"
            } catch (e: Exception) {
                selectedDateString
            }

            Text(
                text = "Events for $dateLabel (${selectedDayEvents.size})",
                fontSize = 16.sp,
                fontWeight = FontWeight.Bold,
                color = if (isDarkTheme) DarkTextPrimary else TextDark
            )

            TextButton(
                onClick = { onAddEvent(selectedDateString) }
            ) {
                Icon(Icons.Default.AddCircleOutline, contentDescription = null, modifier = Modifier.size(16.dp))
                Spacer(modifier = Modifier.width(4.dp))
                Text("Schedule on this day", fontSize = 13.sp)
            }
        }

        if (selectedDayEvents.isEmpty()) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f)
                    .background(
                        if (isDarkTheme) DarkCardBg else Color(0xFFF8FAFC),
                        RoundedCornerShape(12.dp)
                    )
                    .border(
                        1.dp,
                        if (isDarkTheme) DarkCardBorder else Color(0xFFE2E8F0),
                        RoundedCornerShape(12.dp)
                    )
                    .padding(16.dp),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = "No events scheduled for this date. Tap '+ Schedule on this day' to add one!",
                    fontSize = 14.sp,
                    color = if (isDarkTheme) DarkTextSecondary else TextMuted,
                    textAlign = TextAlign.Center
                )
            }
        } else {
            LazyColumn(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                items(selectedDayEvents, key = { it.id }) { event ->
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
                                if (isDarkTheme) DarkCardBg else Color.White,
                                RoundedCornerShape(12.dp)
                            )
                            .border(
                                1.5.dp,
                                if (isDarkTheme) DarkCardBorder else Color(0xFFE2E8F0),
                                RoundedCornerShape(12.dp)
                            )
                            .padding(12.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(12.dp),
                            modifier = Modifier.weight(1f)
                        ) {
                            // Left accent indicator bar
                            Box(
                                modifier = Modifier
                                    .width(5.dp)
                                    .height(36.dp)
                                    .background(eventColor, RoundedCornerShape(3.dp))
                            )

                            Column {
                                Text(
                                    text = event.title,
                                    fontSize = 16.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = if (isDarkTheme) DarkTextPrimary else TextDark
                                )
                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.Schedule,
                                        contentDescription = "Time",
                                        tint = if (isDarkTheme) DarkTextSecondary else TextMuted,
                                        modifier = Modifier.size(14.dp)
                                    )
                                    Text(
                                        text = event.timeSlot,
                                        fontSize = 13.sp,
                                        color = if (isDarkTheme) DarkTextSecondary else TextMuted
                                    )
                                }
                            }
                        }

                        // Child or Family tag pill + Delete button
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            Box(
                                modifier = Modifier
                                    .background(eventColor.copy(alpha = 0.15f), RoundedCornerShape(8.dp))
                                    .border(1.dp, eventColor, RoundedCornerShape(8.dp))
                                    .padding(horizontal = 10.dp, vertical = 4.dp)
                            ) {
                                Text(
                                    text = child?.name ?: "Family",
                                    fontSize = 12.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = eventColor
                                )
                            }

                            IconButton(
                                onClick = { onDeleteEvent(event) },
                                modifier = Modifier.size(28.dp)
                            ) {
                                Icon(
                                    imageVector = Icons.Default.DeleteOutline,
                                    contentDescription = "Delete event",
                                    tint = if (isDarkTheme) DarkTextSecondary else Color(0xFF94A3B8),
                                    modifier = Modifier.size(18.dp)
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}
