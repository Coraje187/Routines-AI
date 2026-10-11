package com.familyroutines

import android.app.Application
import com.familyroutines.ai.FamilyMemoryEngine
import com.familyroutines.data.local.FamilyDatabase
import com.familyroutines.data.repository.FamilyRepository

class FamilyApplication : Application() {

    val database by lazy { FamilyDatabase.getDatabase(this) }
    val repository by lazy { FamilyRepository(database.familyDao()) }
    val memoryEngine by lazy { FamilyMemoryEngine(this) }
    val weatherService by lazy { com.familyroutines.data.weather.WeatherService(this) }
    val userPreferences by lazy { com.familyroutines.data.local.UserPreferences(this) }

    override fun onCreate() {
        super.onCreate()
    }
}
