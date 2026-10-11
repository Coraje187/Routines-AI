package com.familyroutines.data.local.entities

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    tableName = "routine_items",
    foreignKeys = [
        ForeignKey(
            entity = ChildEntity::class,
            parentColumns = ["id"],
            childColumns = ["childId"],
            onDelete = ForeignKey.CASCADE
        )
    ],
    indices = [Index("childId")]
)
data class RoutineItemEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val childId: Long,
    val title: String,
    val localImageUri: String? = null,
    val timeBucket: String, // "morning", "afternoon", "bedtime"
    val orderIndex: Int = 0,
    val isCompleted: Boolean = false,
    val isFirstThenTarget: Boolean = false
)
