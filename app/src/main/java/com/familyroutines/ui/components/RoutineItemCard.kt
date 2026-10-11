package com.familyroutines.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.familyroutines.data.local.entities.RoutineItemEntity
import com.familyroutines.ui.theme.*

@Composable
fun RoutineItemCard(
    item: RoutineItemEntity,
    onToggle: () -> Unit,
    isDarkTheme: Boolean = false,
    modifier: Modifier = Modifier
) {
    val isDone = item.isCompleted

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

    val subTextColor = if (isDarkTheme) DarkTextSecondary else Color(0xFF64748B)

    val checkmarkBoxBg = if (isDone) {
        if (isDarkTheme) DarkSuccessBorder else SuccessGreen
    } else {
        if (isDarkTheme) Color(0xFF0F172A) else Color.White
    }

    val checkmarkBoxBorder = if (isDone) {
        if (isDarkTheme) DarkSuccessBorder else SuccessGreen
    } else {
        if (isDarkTheme) Color(0xFF64748B) else Color(0xFF94A3B8)
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
        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = item.title,
                fontSize = 22.sp,
                fontWeight = FontWeight.Bold,
                color = textColor
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = item.timeBucket.replaceFirstChar { it.uppercase() },
                fontSize = 14.sp,
                fontWeight = FontWeight.Medium,
                color = subTextColor
            )
        }

        Box(
            modifier = Modifier
                .size(48.dp)
                .background(checkmarkBoxBg, RoundedCornerShape(12.dp))
                .border(2.dp, checkmarkBoxBorder, RoundedCornerShape(12.dp)),
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
