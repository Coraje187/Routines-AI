package com.familyroutines.data.local

import android.content.Context
import android.content.SharedPreferences

class UserPreferences(context: Context) {
    private val prefs: SharedPreferences =
        context.getSharedPreferences("family_routines_user_prefs", Context.MODE_PRIVATE)

    fun isDarkTheme(): Boolean = prefs.getBoolean("pref_dark_theme", false)

    fun setDarkTheme(isDark: Boolean) {
        prefs.edit().putBoolean("pref_dark_theme", isDark).apply()
    }
}
