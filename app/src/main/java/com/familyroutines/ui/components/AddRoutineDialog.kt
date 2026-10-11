package com.familyroutines.ui.components

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import com.familyroutines.ui.theme.*

@Composable
fun AddRoutineDialog(
    childName: String,
    isDarkTheme: Boolean,
    onDismiss: () -> Unit,
    onSave: (title: String, timeBucket: String) -> Unit
) {
    var title by remember { mutableStateOf("") }
    val buckets = listOf("morning", "afternoon", "bedtime")
    var selectedBucket by remember { mutableStateOf("morning") }

    Dialog(onDismissRequest = onDismiss) {
        Card(
            shape = RoundedCornerShape(20.dp),
            colors = CardDefaults.cardColors(
                containerColor = if (isDarkTheme) DarkCardBg else Color.White
            ),
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            Column(
                modifier = Modifier
                    .padding(24.dp)
                    .fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                Text(
                    text = "Add Routine for $childName",
                    fontSize = 22.sp,
                    fontWeight = FontWeight.Bold,
                    color = if (isDarkTheme) DarkTextPrimary else TextDark
                )

                OutlinedTextField(
                    value = title,
                    onValueChange = { title = it },
                    label = { Text("Routine Title (e.g. Put shoes on)") },
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp)
                )

                Text(
                    text = "Time of Day",
                    fontSize = 14.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = if (isDarkTheme) DarkTextSecondary else TextMuted
                )

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    buckets.forEach { bucket ->
                        FilterChip(
                            selected = selectedBucket == bucket,
                            onClick = { selectedBucket = bucket },
                            label = { Text(bucket.replaceFirstChar { it.uppercase() }) }
                        )
                    }
                }

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.End
                ) {
                    TextButton(onClick = onDismiss) {
                        Text("Cancel")
                    }
                    Spacer(modifier = Modifier.width(8.dp))
                    Button(
                        onClick = {
                            if (title.isNotBlank()) {
                                onSave(title.trim(), selectedBucket)
                                onDismiss()
                            }
                        },
                        enabled = title.isNotBlank(),
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Text("Add Routine")
                    }
                }
            }
        }
    }
}
