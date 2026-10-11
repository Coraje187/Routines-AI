package com.familyroutines.data.local.entities

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "family_photos")
data class FamilyPhotoEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val photoUri: String,
    val caption: String = "",
    val timestamp: Long = System.currentTimeMillis()
)
