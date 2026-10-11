package com.familyroutines.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material3.Icon
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
fun FirstThenBoard(
    firstItem: RoutineItemEntity?,
    thenItem: RoutineItemEntity?,
    isDarkTheme: Boolean = false,
    modifier: Modifier = Modifier
) {
    val bg = if (isDarkTheme) DarkAmberBg else Color(0xFFFEF3C7)
    val border = if (isDarkTheme) DarkAmberBorder else Color(0xFFF59E0B)
    val labelColor = if (isDarkTheme) DarkAmberText else Color(0xFFB45309)
    val textColor = if (isDarkTheme) DarkTextPrimary else Color(0xFF1E293B)
    val iconColor = if (isDarkTheme) Color(0xFFF59E0B) else Color(0xFFD97706)

    Row(
        modifier = modifier
            .fillMaxWidth()
            .background(bg, RoundedCornerShape(16.dp))
            .border(2.dp, border, RoundedCornerShape(16.dp))
            .padding(16.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        // FIRST Column
        Column(
            modifier = Modifier.weight(1f)
        ) {
            Text(
                text = "FIRST",
                fontSize = 14.sp,
                fontWeight = FontWeight.Black,
                color = labelColor
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = firstItem?.title ?: "All routines completed!",
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold,
                color = textColor
            )
        }

        Icon(
            imageVector = Icons.AutoMirrored.Filled.ArrowForward,
            contentDescription = "Then",
            tint = iconColor,
            modifier = Modifier.padding(horizontal = 12.dp)
        )

        // THEN Column
        Column(
            modifier = Modifier.weight(1f)
        ) {
            Text(
                text = "THEN",
                fontSize = 14.sp,
                fontWeight = FontWeight.Black,
                color = labelColor
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = thenItem?.title ?: "Free Play / Relaxation",
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold,
                color = textColor
            )
        }
    }
}
