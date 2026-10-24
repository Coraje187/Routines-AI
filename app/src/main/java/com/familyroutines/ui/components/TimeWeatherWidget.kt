package com.familyroutines.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.familyroutines.data.weather.WeatherInfo
import com.familyroutines.ui.theme.*
import kotlinx.coroutines.delay
import java.text.SimpleDateFormat
import java.util.*

@Composable
fun TimeWeatherWidget(
    weather: WeatherInfo,
    isDarkTheme: Boolean,
    onWeatherClick: () -> Unit,
    isCompact: Boolean = false,
    modifier: Modifier = Modifier
) {
    var currentTime by remember { mutableStateOf(getCurrentTimeString()) }
    var currentDate by remember { mutableStateOf(getCurrentDateString()) }

    LaunchedEffect(Unit) {
        while (true) {
            currentTime = getCurrentTimeString()
            currentDate = getCurrentDateString()
            delay(1000)
        }
    }

    Row(
        modifier = modifier,
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(if (isCompact) 8.dp else 14.dp)
    ) {
        // Clock & Date display
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(if (isCompact) 4.dp else 8.dp)
        ) {
            Text(
                text = currentTime,
                fontSize = if (isCompact) 14.sp else 18.sp,
                fontWeight = FontWeight.Black,
                color = if (isDarkTheme) Color(0xFFF8FAFC) else Color(0xFF0F172A)
            )
            if (!isCompact) {
                Text(
                    text = "•",
                    fontSize = 14.sp,
                    color = if (isDarkTheme) DarkTextSecondary else TextMuted
                )
                Text(
                    text = currentDate,
                    fontSize = 14.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = if (isDarkTheme) DarkTextSecondary else TextMuted
                )
            }
        }

        // Weather Badge
        Row(
            modifier = Modifier
                .background(
                    if (isDarkTheme) Color(0xFF1E293B) else Color.White,
                    RoundedCornerShape(if (isCompact) 14.dp else 18.dp)
                )
                .border(
                    1.dp,
                    if (isDarkTheme) Color(0xFF334155) else Color(0xFFE2E8F0),
                    RoundedCornerShape(if (isCompact) 14.dp else 18.dp)
                )
                .clickable { onWeatherClick() }
                .padding(horizontal = if (isCompact) 8.dp else 12.dp, vertical = if (isCompact) 4.dp else 6.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(4.dp)
        ) {
            Text(
                text = weather.iconEmoji,
                fontSize = if (isCompact) 14.sp else 16.sp
            )
            Text(
                text = "${weather.temperature}${weather.unit}",
                fontSize = if (isCompact) 12.sp else 14.sp,
                fontWeight = FontWeight.Bold,
                color = if (isDarkTheme) DarkTextPrimary else TextDark
            )
            if (!isCompact) {
                Text(
                    text = weather.location,
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Medium,
                    color = if (isDarkTheme) DarkTextSecondary else TextMuted
                )
            }
        }
    }
}

private fun getCurrentTimeString(): String {
    val sdf = SimpleDateFormat("h:mm a", Locale.getDefault())
    return sdf.format(Date())
}

private fun getCurrentDateString(): String {
    val sdf = SimpleDateFormat("EEE, MMM d", Locale.getDefault())
    return sdf.format(Date())
}
