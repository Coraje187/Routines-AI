package com.familyroutines.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import com.familyroutines.ui.theme.*

@Composable
fun AddChoreDialog(
    childName: String,
    isDarkTheme: Boolean,
    onDismiss: () -> Unit,
    onSave: (title: String, iconCategory: String, pointValue: Int) -> Unit
) {
    var title by remember { mutableStateOf("") }
    val categories = listOf(
        "bed" to "🛏️ Bed",
        "toys" to "🧸 Toys",
        "pet" to "🐱 Pet",
        "dishes" to "🍽️ Dishes",
        "laundry" to "🧺 Clothes",
        "reading" to "📚 Reading",
        "general" to "⭐ Other"
    )
    var selectedCategory by remember { mutableStateOf("toys") }
    var points by remember { mutableStateOf(10) }

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
                    text = "Add Chore for $childName",
                    fontSize = 22.sp,
                    fontWeight = FontWeight.Bold,
                    color = if (isDarkTheme) DarkTextPrimary else TextDark
                )

                OutlinedTextField(
                    value = title,
                    onValueChange = { title = it },
                    label = { Text("Chore Title (e.g. Put shoes away)") },
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp)
                )

                Text(
                    text = "Icon Category",
                    fontSize = 14.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = if (isDarkTheme) DarkTextSecondary else TextMuted
                )

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    categories.take(4).forEach { (cat, label) ->
                        FilterChip(
                            selected = selectedCategory == cat,
                            onClick = { selectedCategory = cat },
                            label = { Text(label) }
                        )
                    }
                }
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    categories.drop(4).forEach { (cat, label) ->
                        FilterChip(
                            selected = selectedCategory == cat,
                            onClick = { selectedCategory = cat },
                            label = { Text(label) }
                        )
                    }
                }

                Text(
                    text = "Star Points Reward: +$points ⭐",
                    fontSize = 14.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = Color(0xFFD97706)
                )

                Row(
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    listOf(5, 10, 15, 20).forEach { pt ->
                        FilterChip(
                            selected = points == pt,
                            onClick = { points = pt },
                            label = { Text("+$pt pts") }
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
                                onSave(title.trim(), selectedCategory, points)
                                onDismiss()
                            }
                        },
                        enabled = title.isNotBlank(),
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Text("Add Chore")
                    }
                }
            }
        }
    }
}
