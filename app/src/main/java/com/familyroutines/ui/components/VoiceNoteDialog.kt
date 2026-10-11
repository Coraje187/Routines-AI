package com.familyroutines.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Mic
import androidx.compose.material.icons.filled.Stop
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

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun VoiceNoteDialog(
    childName: String,
    isListening: Boolean,
    recognizedText: String,
    isDarkTheme: Boolean = false,
    onStartListening: () -> Unit,
    onStopListening: () -> Unit,
    onSaveNote: (author: String, category: String, text: String) -> Unit,
    onDismiss: () -> Unit
) {
    var author by remember { mutableStateOf("Mom") }
    var category by remember { mutableStateOf("food") }
    var noteText by remember { mutableStateOf(recognizedText) }

    LaunchedEffect(recognizedText) {
        if (recognizedText.isNotBlank()) {
            noteText = recognizedText
        }
    }

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
                    text = "Record Note for $childName",
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold,
                    color = if (isDarkTheme) DarkTextPrimary else Color(0xFF0F172A)
                )

                // Author selection: Mom / Dad
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    listOf("Mom", "Dad").forEach { name ->
                        FilterChip(
                            selected = author == name,
                            onClick = { author = name },
                            label = { Text(name) }
                        )
                    }
                }

                // Category selection
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    listOf("food", "sensory", "calm_down", "general").forEach { cat ->
                        FilterChip(
                            selected = category == cat,
                            onClick = { category = cat },
                            label = {
                                Text(
                                    cat.replace("_", " ")
                                        .replaceFirstChar { it.uppercase() }
                                )
                            }
                        )
                    }
                }

                // Text field
                OutlinedTextField(
                    value = noteText,
                    onValueChange = { noteText = it },
                    label = { Text("Note details or speech transcript") },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(130.dp),
                    shape = RoundedCornerShape(12.dp)
                )

                // Voice Recording control
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    Button(
                        onClick = {
                            if (isListening) onStopListening() else onStartListening()
                        },
                        colors = ButtonDefaults.buttonColors(
                            containerColor = if (isListening) Color(0xFFEF4444) else Color(0xFF2563EB)
                        ),
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Icon(
                            imageVector = if (isListening) Icons.Default.Stop else Icons.Default.Mic,
                            contentDescription = if (isListening) "Stop Recording" else "Speak Note"
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(if (isListening) "Listening..." else "Record Voice")
                    }

                    if (isListening) {
                        Text(
                            text = "Speaking now...",
                            color = Color(0xFFEF4444),
                            fontSize = 14.sp,
                            fontWeight = FontWeight.SemiBold
                        )
                    }
                }

                // Actions: Cancel & Save
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.End,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    TextButton(onClick = onDismiss) {
                        Text("Cancel")
                    }
                    Spacer(modifier = Modifier.width(8.dp))
                    Button(
                        onClick = {
                            if (noteText.isNotBlank()) {
                                onSaveNote(author, category, noteText.trim())
                                onDismiss()
                            }
                        },
                        enabled = noteText.isNotBlank(),
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Text("Save Note")
                    }
                }
            }
        }
    }
}
