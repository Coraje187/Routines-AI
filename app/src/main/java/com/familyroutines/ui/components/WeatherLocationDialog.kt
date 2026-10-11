package com.familyroutines.ui.components

import androidx.compose.foundation.layout.*
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
import com.familyroutines.data.weather.WeatherInfo
import com.familyroutines.ui.theme.*

@Composable
fun WeatherLocationDialog(
    currentWeather: WeatherInfo,
    isDarkTheme: Boolean,
    onDismiss: () -> Unit,
    onSaveLocation: (city: String, useFahrenheit: Boolean) -> Unit
) {
    var cityName by remember { mutableStateOf(currentWeather.location) }
    var useFahrenheit by remember { mutableStateOf(currentWeather.unit == "°F") }

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
                    text = "🌤️ Local Weather & Area",
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold,
                    color = if (isDarkTheme) DarkTextPrimary else TextDark
                )

                Text(
                    text = "Enter your city or area to display live weather on your family dashboard:",
                    fontSize = 14.sp,
                    color = if (isDarkTheme) DarkTextSecondary else TextMuted
                )

                OutlinedTextField(
                    value = cityName,
                    onValueChange = { cityName = it },
                    label = { Text("City, State or Country") },
                    placeholder = { Text("e.g. Austin, TX or Chicago, IL") },
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp)
                )

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text(
                        text = "Temperature Unit:",
                        fontSize = 15.sp,
                        fontWeight = FontWeight.Medium,
                        color = if (isDarkTheme) DarkTextPrimary else TextDark
                    )

                    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        FilterChip(
                            selected = useFahrenheit,
                            onClick = { useFahrenheit = true },
                            label = { Text("°F (Fahrenheit)") }
                        )
                        FilterChip(
                            selected = !useFahrenheit,
                            onClick = { useFahrenheit = false },
                            label = { Text("°C (Celsius)") }
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
                            if (cityName.isNotBlank()) {
                                onSaveLocation(cityName.trim(), useFahrenheit)
                                onDismiss()
                            }
                        },
                        shape = RoundedCornerShape(12.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = PrimaryBlue)
                    ) {
                        Text("Update Weather")
                    }
                }
            }
        }
    }
}
