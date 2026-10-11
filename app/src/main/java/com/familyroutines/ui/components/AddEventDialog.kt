package com.familyroutines.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CalendarMonth
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Schedule
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import com.familyroutines.data.local.entities.ChildEntity
import com.familyroutines.ui.theme.*
import java.time.LocalDate
import java.time.format.DateTimeFormatter

@Composable
fun AddEventDialog(
    children: List<ChildEntity>,
    initialDate: String,
    isDarkTheme: Boolean = false,
    onDismiss: () -> Unit,
    onSave: (childId: Long?, title: String, timeSlot: String, category: String, dateString: String, colorHex: String) -> Unit
) {
    var title by remember { mutableStateOf("") }
    var selectedChildId by remember { mutableStateOf<Long?>(null) } // null = Entire Family
    var timeSlot by remember { mutableStateOf("9:00 AM - 10:00 AM") }
    var selectedCategory by remember { mutableStateOf("family") }
    var eventDate by remember { mutableStateOf(if (initialDate.isNotBlank()) initialDate else "2026-10-10") }

    val categories = listOf(
        Pair("family", "🏠 Family"),
        Pair("school", "🏫 School"),
        Pair("therapy", "🧩 Therapy"),
        Pair("activity", "⚽ Activity"),
        Pair("medical", "🏥 Medical")
    )

    val commonTimeSlots = listOf(
        "All Day",
        "8:00 AM - 9:00 AM",
        "9:30 AM - 10:30 AM",
        "1:00 PM - 2:00 PM",
        "3:30 PM - 4:30 PM",
        "6:00 PM - 7:00 PM"
    )

    Dialog(onDismissRequest = onDismiss) {
        Card(
            shape = RoundedCornerShape(24.dp),
            colors = CardDefaults.cardColors(
                containerColor = if (isDarkTheme) DarkCardBg else Color.White
            ),
            modifier = Modifier
                .width(580.dp)
                .wrapContentHeight()
                .padding(8.dp)
        ) {
            Column(
                modifier = Modifier
                    .padding(24.dp)
                    .fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                // Header
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.CalendarMonth,
                            contentDescription = null,
                            tint = PrimaryBlue,
                            modifier = Modifier.size(28.dp)
                        )
                        Text(
                            text = "Schedule Calendar Event",
                            fontSize = 22.sp,
                            fontWeight = FontWeight.Bold,
                            color = if (isDarkTheme) DarkTextPrimary else TextDark
                        )
                    }
                    IconButton(onClick = onDismiss) {
                        Icon(
                            imageVector = Icons.Default.Close,
                            contentDescription = "Close",
                            tint = if (isDarkTheme) DarkTextSecondary else TextMuted
                        )
                    }
                }

                // Title Field
                OutlinedTextField(
                    value = title,
                    onValueChange = { title = it },
                    label = { Text("Event Title") },
                    placeholder = { Text("e.g. Speech Therapy, Soccer Practice, Dentist") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp)
                )

                // Date Field / Quick Picker
                Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                    Text(
                        text = "Date (YYYY-MM-DD):",
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Bold,
                        color = if (isDarkTheme) DarkTextPrimary else TextDark
                    )
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        OutlinedTextField(
                            value = eventDate,
                            onValueChange = { eventDate = it },
                            modifier = Modifier.weight(1f),
                            singleLine = true,
                            shape = RoundedCornerShape(12.dp)
                        )
                        Button(
                            onClick = {
                                eventDate = "2026-10-10"
                            },
                            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF64748B)),
                            shape = RoundedCornerShape(12.dp)
                        ) {
                            Text("Today")
                        }
                        Button(
                            onClick = {
                                try {
                                    val parsed = LocalDate.parse(eventDate)
                                    eventDate = parsed.plusDays(1).format(DateTimeFormatter.ISO_LOCAL_DATE)
                                } catch (e: Exception) {
                                    eventDate = "2026-10-11"
                                }
                            },
                            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF64748B)),
                            shape = RoundedCornerShape(12.dp)
                        ) {
                            Text("+1 Day")
                        }
                    }
                }

                // Assigned Person / Family
                Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                    Text(
                        text = "Assigned To:",
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Bold,
                        color = if (isDarkTheme) DarkTextPrimary else TextDark
                    )
                    Row(
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        // Whole family chip
                        val isFamily = selectedChildId == null
                        Box(
                            modifier = Modifier
                                .background(
                                    if (isFamily) Color(0xFF8B5CF6) else if (isDarkTheme) DarkAssistantInner else Color(0xFFF1F5F9),
                                    RoundedCornerShape(12.dp)
                                )
                                .clickable { selectedChildId = null }
                                .padding(horizontal = 14.dp, vertical = 8.dp)
                        ) {
                            Text(
                                text = "👨‍👩‍👧‍👦 Whole Family",
                                fontWeight = FontWeight.Bold,
                                color = if (isFamily) Color.White else if (isDarkTheme) DarkTextPrimary else TextDark
                            )
                        }

                        children.forEach { child ->
                            val isSelected = selectedChildId == child.id
                            val kidColor = try {
                                Color(android.graphics.Color.parseColor(child.colorHex))
                            } catch (e: Exception) {
                                PrimaryBlue
                            }

                            Box(
                                modifier = Modifier
                                    .background(
                                        if (isSelected) kidColor else if (isDarkTheme) DarkAssistantInner else Color(0xFFF1F5F9),
                                        RoundedCornerShape(12.dp)
                                    )
                                    .clickable { selectedChildId = child.id }
                                    .padding(horizontal = 14.dp, vertical = 8.dp)
                            ) {
                                Text(
                                    text = child.name,
                                    fontWeight = FontWeight.Bold,
                                    color = if (isSelected) Color.White else if (isDarkTheme) DarkTextPrimary else TextDark
                                )
                            }
                        }
                    }
                }

                // Time Slot
                Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                    Text(
                        text = "Time Slot:",
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Bold,
                        color = if (isDarkTheme) DarkTextPrimary else TextDark
                    )
                    OutlinedTextField(
                        value = timeSlot,
                        onValueChange = { timeSlot = it },
                        modifier = Modifier.fillMaxWidth(),
                        singleLine = true,
                        leadingIcon = {
                            Icon(Icons.Default.Schedule, contentDescription = null, tint = PrimaryBlue)
                        },
                        shape = RoundedCornerShape(12.dp)
                    )
                    LazyRow(
                        horizontalArrangement = Arrangement.spacedBy(6.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        items(commonTimeSlots) { slot ->
                            FilterChip(
                                selected = timeSlot == slot,
                                onClick = { timeSlot = slot },
                                label = { Text(slot, fontSize = 12.sp) }
                            )
                        }
                    }
                }

                // Category Selector
                Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                    Text(
                        text = "Category:",
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Bold,
                        color = if (isDarkTheme) DarkTextPrimary else TextDark
                    )
                    Row(
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        categories.forEach { (key, label) ->
                            FilterChip(
                                selected = selectedCategory == key,
                                onClick = { selectedCategory = key },
                                label = { Text(label, fontSize = 13.sp, fontWeight = FontWeight.Bold) }
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(6.dp))

                // Actions
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.End,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    TextButton(onClick = onDismiss) {
                        Text("Cancel", fontSize = 16.sp, color = if (isDarkTheme) DarkTextSecondary else TextMuted)
                    }
                    Spacer(modifier = Modifier.width(8.dp))
                    Button(
                        onClick = {
                            if (title.isNotBlank()) {
                                val selectedChild = children.find { it.id == selectedChildId }
                                val eventColorHex = selectedChild?.colorHex ?: when (selectedCategory) {
                                    "therapy" -> "#3B82F6"
                                    "school" -> "#F59E0B"
                                    "activity" -> "#EC4899"
                                    "medical" -> "#EF4444"
                                    else -> "#8B5CF6"
                                }
                                onSave(
                                    selectedChildId,
                                    title.trim(),
                                    timeSlot.trim(),
                                    selectedCategory,
                                    eventDate.trim(),
                                    eventColorHex
                                )
                                onDismiss()
                            }
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = PrimaryBlue),
                        shape = RoundedCornerShape(12.dp),
                        enabled = title.isNotBlank()
                    ) {
                        Text("Save Event", fontSize = 16.sp, fontWeight = FontWeight.Bold)
                    }
                }
            }
        }
    }
}
