package com.familyroutines.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.DeleteOutline
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.familyroutines.data.local.entities.ChoreEntity
import com.familyroutines.ui.theme.*

@Composable
fun ChoreItemCard(
    chore: ChoreEntity,
    onToggle: () -> Unit,
    onDelete: () -> Unit = {},
    isDarkTheme: Boolean = false,
    modifier: Modifier = Modifier
) {
    val isDone = chore.isCompleted

    val bgColor = if (isDone) {
        if (isDarkTheme) DarkSuccessBg else SuccessGreenBg
    } else {
        if (isDarkTheme) DarkCardBg else Color(0xFFF8FAFC)
    }

    val borderColor = if (isDone) {
        if (isDarkTheme) DarkSuccessBorder else SuccessGreen
    } else {
        if (isDarkTheme) DarkCardBorder else Color(0xFFCBD5E1)
    }

    val textColor = if (isDone) {
        if (isDarkTheme) DarkSuccessText else SuccessGreenText
    } else {
        if (isDarkTheme) DarkTextPrimary else Color(0xFF1E293B)
    }

    val emojiIcon = when (chore.iconCategory.lowercase()) {
        "bed" -> "🛏️"
        "toys" -> "🧸"
        "pet" -> "🐱"
        "dishes" -> "🍽️"
        "laundry" -> "🧺"
        "backpack" -> "🎒"
        "reading" -> "📚"
        else -> "⭐"
    }

    Row(
        modifier = modifier
            .fillMaxWidth()
            .background(bgColor, RoundedCornerShape(16.dp))
            .border(2.dp, borderColor, RoundedCornerShape(16.dp))
            .clickable { onToggle() }
            .padding(16.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Row(
            modifier = Modifier.weight(1f),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(14.dp)
        ) {
            // Visual Icon Badge
            Box(
                modifier = Modifier
                    .size(52.dp)
                    .background(
                        if (isDarkTheme) Color(0xFF0F172A) else Color(0xFFEFF6FF),
                        CircleShape
                    )
                    .border(
                        2.dp,
                        if (isDone) Color(0xFF10B981) else Color(0xFF93C5FD),
                        CircleShape
                    ),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = emojiIcon,
                    fontSize = 26.sp
                )
            }

            Column {
                Text(
                    text = chore.title,
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold,
                    color = textColor
                )
                Spacer(modifier = Modifier.height(4.dp))
                // Point reward pill
                Row(
                    modifier = Modifier
                        .background(
                            if (isDarkTheme) Color(0xFF451A03) else Color(0xFFFEF3C7),
                            RoundedCornerShape(8.dp)
                        )
                        .padding(horizontal = 8.dp, vertical = 2.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.Star,
                        contentDescription = "Reward",
                        tint = Color(0xFFF59E0B),
                        modifier = Modifier.size(14.dp)
                    )
                    Text(
                        text = "+${chore.pointValue} ⭐",
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Black,
                        color = if (isDarkTheme) Color(0xFFFDE68A) else Color(0xFFB45309)
                    )
                }
            }
        }

        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            // Delete Chore Button
            IconButton(
                onClick = onDelete,
                modifier = Modifier.size(36.dp)
            ) {
                Icon(
                    imageVector = Icons.Default.DeleteOutline,
                    contentDescription = "Delete chore",
                    tint = if (isDarkTheme) DarkTextSecondary else Color(0xFF94A3B8),
                    modifier = Modifier.size(22.dp)
                )
            }

            // Tactile Checkbox
            Box(
                modifier = Modifier
                    .size(46.dp)
                    .background(
                        if (isDone) (if (isDarkTheme) DarkSuccessBorder else SuccessGreen) else (if (isDarkTheme) Color(0xFF0F172A) else Color.White),
                        RoundedCornerShape(12.dp)
                    )
                    .border(
                        2.dp,
                        if (isDone) (if (isDarkTheme) DarkSuccessBorder else SuccessGreen) else (if (isDarkTheme) Color(0xFF64748B) else Color(0xFF94A3B8)),
                        RoundedCornerShape(12.dp)
                    ),
                contentAlignment = Alignment.Center
            ) {
                if (isDone) {
                    Text(
                        text = "✓",
                        color = Color.White,
                        fontSize = 26.sp,
                        fontWeight = FontWeight.Black
                    )
                }
            }
        }
    }
}
