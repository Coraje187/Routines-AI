package com.familyroutines.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.DarkMode
import androidx.compose.material.icons.filled.LightMode
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.familyroutines.data.local.entities.ChildEntity
import com.familyroutines.ui.theme.*

@Composable
fun KidProfileBar(
    children: List<ChildEntity>,
    selectedChildId: Long?,
    isDarkTheme: Boolean,
    onSelectChild: (Long) -> Unit,
    onAddKid: () -> Unit,
    onDeleteChild: (ChildEntity) -> Unit = {},
    onToggleDarkTheme: () -> Unit,
    modifier: Modifier = Modifier
) {
    var childToDelete by remember { mutableStateOf<ChildEntity?>(null) }

    Row(
        modifier = modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        // Left side: Genesis OS Branding + Kid Chips and Add Kid Button
        Row(
            horizontalArrangement = Arrangement.spacedBy(10.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // --- Genesis OS Logo & Routines AI Branding Badge ---
            Row(
                modifier = Modifier
                    .background(
                        Color(0xFF090D16),
                        RoundedCornerShape(20.dp)
                    )
                    .border(
                        1.5.dp,
                        Color(0xFF00F0FF),
                        RoundedCornerShape(20.dp)
                    )
                    .padding(horizontal = 12.dp, vertical = 7.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                // Neon Genesis G Node
                Box(
                    modifier = Modifier
                        .size(24.dp)
                        .background(Color(0xFF00F0FF), CircleShape),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "G",
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Black,
                        color = Color(0xFF090D16)
                    )
                }
                Text(
                    text = "Routines AI",
                    fontSize = 15.sp,
                    fontWeight = FontWeight.Black,
                    color = Color(0xFF00F0FF)
                )
            }

            children.forEach { child ->
                val isSelected = child.id == selectedChildId
                val childColor = try {
                    Color(android.graphics.Color.parseColor(child.colorHex))
                } catch (e: Exception) {
                    PrimaryBlue
                }

                Row(
                    modifier = Modifier
                        .background(
                            if (isSelected) childColor else if (isDarkTheme) Color(0xFF1E293B) else Color(0xFFF1F5F9),
                            RoundedCornerShape(20.dp)
                        )
                        .border(
                            2.dp,
                            if (isSelected) childColor else if (isDarkTheme) Color(0xFF334155) else Color(0xFFCBD5E1),
                            RoundedCornerShape(20.dp)
                        )
                        .clickable { onSelectChild(child.id) }
                        .padding(horizontal = 14.dp, vertical = 8.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    // Avatar dot / initials
                    Box(
                        modifier = Modifier
                            .size(28.dp)
                            .background(if (isSelected) Color.White.copy(alpha = 0.25f) else childColor, CircleShape),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = child.name.take(1).uppercase(),
                            color = Color.White,
                            fontWeight = FontWeight.Bold,
                            fontSize = 14.sp
                        )
                    }

                    // Kid Name
                    Text(
                        text = child.name,
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold,
                        color = if (isSelected) Color.White else if (isDarkTheme) DarkTextPrimary else TextDark
                    )

                    // Star Points Pill
                    Row(
                        modifier = Modifier
                            .background(
                                if (isSelected) Color.Black.copy(alpha = 0.2f) else if (isDarkTheme) Color(0xFF0F172A) else Color.White,
                                RoundedCornerShape(12.dp)
                            )
                            .padding(horizontal = 8.dp, vertical = 3.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(4.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Star,
                            contentDescription = "Stars",
                            tint = Color(0xFFF59E0B),
                            modifier = Modifier.size(16.dp)
                        )
                        Text(
                            text = "${child.stars} pts",
                            fontSize = 13.sp,
                            fontWeight = FontWeight.Black,
                            color = if (isSelected) Color.White else Color(0xFFD97706)
                        )
                    }

                    // Remove Child Button (shown on selected profile)
                    if (isSelected) {
                        Box(
                            modifier = Modifier
                                .size(22.dp)
                                .background(Color.Black.copy(alpha = 0.25f), CircleShape)
                                .clickable { childToDelete = child },
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.Default.Close,
                                contentDescription = "Remove ${child.name}",
                                tint = Color.White,
                                modifier = Modifier.size(14.dp)
                            )
                        }
                    }
                }
            }

            // + Add Kid Pill
            Row(
                modifier = Modifier
                    .background(
                        if (isDarkTheme) Color(0xFF1E293B) else Color.White,
                        RoundedCornerShape(20.dp)
                    )
                    .border(
                        1.dp,
                        if (isDarkTheme) Color(0xFF475569) else Color(0xFFCBD5E1),
                        RoundedCornerShape(20.dp)
                    )
                    .clickable { onAddKid() }
                    .padding(horizontal = 12.dp, vertical = 8.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                Icon(
                    imageVector = Icons.Default.Add,
                    contentDescription = "Add Kid",
                    tint = if (isDarkTheme) DarkTextSecondary else TextMuted,
                    modifier = Modifier.size(18.dp)
                )
                Text(
                    text = "Add Kid",
                    fontSize = 14.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = if (isDarkTheme) DarkTextSecondary else TextMuted
                )
            }
        }

        // Right side: Sensory Dark Mode Toggle Button
        IconButton(
            onClick = onToggleDarkTheme,
            modifier = Modifier
                .background(
                    if (isDarkTheme) Color(0xFF334155) else Color(0xFFE2E8F0),
                    RoundedCornerShape(12.dp)
                )
        ) {
            Icon(
                imageVector = if (isDarkTheme) Icons.Default.LightMode else Icons.Default.DarkMode,
                contentDescription = if (isDarkTheme) "Switch to Light Mode" else "Switch to Sensory Dim Mode",
                tint = if (isDarkTheme) Color(0xFFFDE68A) else Color(0xFF475569)
            )
        }
    }

    // Confirmation Dialog for Removing a Child
    if (childToDelete != null) {
        val target = childToDelete!!
        AlertDialog(
            onDismissRequest = { childToDelete = null },
            title = {
                Text(
                    text = "Remove ${target.name}?",
                    fontWeight = FontWeight.Bold,
                    fontSize = 20.sp
                )
            },
            text = {
                Text(
                    text = "Are you sure you want to remove ${target.name}? This will delete all of ${target.name}'s routines, chores, and notes from Routines AI.",
                    fontSize = 15.sp,
                    lineHeight = 22.sp
                )
            },
            confirmButton = {
                Button(
                    onClick = {
                        onDeleteChild(target)
                        childToDelete = null
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFEF4444)),
                    shape = RoundedCornerShape(10.dp)
                ) {
                    Text("Remove Child", color = Color.White, fontWeight = FontWeight.Bold)
                }
            },
            dismissButton = {
                TextButton(onClick = { childToDelete = null }) {
                    Text("Cancel", fontSize = 15.sp)
                }
            },
            shape = RoundedCornerShape(20.dp)
        )
    }
}
