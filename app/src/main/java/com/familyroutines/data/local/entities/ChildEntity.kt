package com.familyroutines.data.local.entities

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "children")
data class ChildEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val name: String,
    val colorHex: String = "#3B82F6",
    val avatarUri: String? = null,
    val stars: Int = 0
)
