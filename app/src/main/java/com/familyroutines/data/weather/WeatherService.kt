package com.familyroutines.data.weather

import android.content.Context
import android.content.SharedPreferences
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.json.JSONObject
import java.net.HttpURLConnection
import java.net.URL
import java.net.URLEncoder

data class WeatherInfo(
    val temperature: Int = 72,
    val unit: String = "°F",
    val condition: String = "Partly Cloudy",
    val location: String = "Home",
    val iconEmoji: String = "⛅",
    val high: Int = 78,
    val low: Int = 60
)

class WeatherService(context: Context) {
    private val prefs: SharedPreferences =
        context.getSharedPreferences("weather_prefs", Context.MODE_PRIVATE)

    fun getSavedLocation(): String = prefs.getString("location_name", "Austin, TX") ?: "Austin, TX"
    fun isFahrenheit(): Boolean = prefs.getBoolean("is_fahrenheit", true)

    fun saveLocation(location: String, isFahrenheit: Boolean = true) {
        prefs.edit()
            .putString("location_name", location)
            .putBoolean("is_fahrenheit", isFahrenheit)
            .apply()
    }

    suspend fun fetchWeather(cityName: String = getSavedLocation(), useFahrenheit: Boolean = isFahrenheit()): WeatherInfo {
        return withContext(Dispatchers.IO) {
            try {
                // 1. Geocode City Name
                val encodedCity = URLEncoder.encode(cityName, "UTF-8")
                val geoUrl = URL("https://geocoding-api.open-meteo.com/v1/search?name=$encodedCity&count=1&language=en&format=json")
                val geoConn = (geoUrl.openConnection() as HttpURLConnection).apply {
                    connectTimeout = 4000
                    readTimeout = 4000
                    requestMethod = "GET"
                }

                if (geoConn.responseCode != 200) {
                    return@withContext fallbackWeather(cityName, useFahrenheit)
                }

                val geoJson = geoConn.inputStream.bufferedReader().use { it.readText() }
                val geoObj = JSONObject(geoJson)
                val results = geoObj.optJSONArray("results")
                if (results == null || results.length() == 0) {
                    return@withContext fallbackWeather(cityName, useFahrenheit)
                }

                val firstLoc = results.getJSONObject(0)
                val lat = firstLoc.getDouble("latitude")
                val lon = firstLoc.getDouble("longitude")
                val resolvedName = firstLoc.optString("name", cityName)
                val admin1 = firstLoc.optString("admin1", "")
                val displayName = if (admin1.isNotBlank()) "$resolvedName, $admin1" else resolvedName

                // 2. Query Forecast
                val tempUnitParam = if (useFahrenheit) "&temperature_unit=fahrenheit" else ""
                val weatherUrl = URL("https://api.open-meteo.com/v1/forecast?latitude=$lat&longitude=$lon&current=temperature_2m,weather_code&daily=temperature_2m_max,temperature_2m_min&timezone=auto$tempUnitParam")
                val weatherConn = (weatherUrl.openConnection() as HttpURLConnection).apply {
                    connectTimeout = 4000
                    readTimeout = 4000
                    requestMethod = "GET"
                }

                if (weatherConn.responseCode != 200) {
                    return@withContext fallbackWeather(displayName, useFahrenheit)
                }

                val weatherJson = weatherConn.inputStream.bufferedReader().use { it.readText() }
                val weatherObj = JSONObject(weatherJson)
                val current = weatherObj.getJSONObject("current")
                val temp = current.getDouble("temperature_2m").toInt()
                val code = current.optInt("weather_code", 0)

                val daily = weatherObj.optJSONObject("daily")
                val high = daily?.optJSONArray("temperature_2m_max")?.optDouble(0, temp + 5.0)?.toInt() ?: (temp + 5)
                val low = daily?.optJSONArray("temperature_2m_min")?.optDouble(0, temp - 8.0)?.toInt() ?: (temp - 8)

                val (conditionName, emoji) = parseWmoCode(code)

                WeatherInfo(
                    temperature = temp,
                    unit = if (useFahrenheit) "°F" else "°C",
                    condition = conditionName,
                    location = displayName,
                    iconEmoji = emoji,
                    high = high,
                    low = low
                )
            } catch (e: Exception) {
                fallbackWeather(cityName, useFahrenheit)
            }
        }
    }

    private fun fallbackWeather(location: String, useFahrenheit: Boolean): WeatherInfo {
        return WeatherInfo(
            temperature = if (useFahrenheit) 72 else 22,
            unit = if (useFahrenheit) "°F" else "°C",
            condition = "Partly Cloudy",
            location = location,
            iconEmoji = "⛅",
            high = if (useFahrenheit) 78 else 25,
            low = if (useFahrenheit) 62 else 17
        )
    }

    private fun parseWmoCode(code: Int): Pair<String, String> {
        return when (code) {
            0 -> "Clear Sky" to "☀️"
            1, 2 -> "Partly Cloudy" to "⛅"
            3 -> "Overcast" to "☁️"
            45, 48 -> "Foggy" to "🌫️"
            51, 53, 55, 61, 63 -> "Showers" to "🌧️"
            65, 80, 81, 82 -> "Heavy Rain" to "🌧️"
            71, 73, 75, 77, 85, 86 -> "Snow" to "❄️"
            95, 96, 99 -> "Thunderstorm" to "⛈️"
            else -> "Fair" to "🌤️"
        }
    }
}
