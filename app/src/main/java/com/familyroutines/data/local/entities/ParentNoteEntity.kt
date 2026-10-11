package com.familyroutines.data.local.entities

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    tableName = "parent_notes",
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
data class ParentNoteEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val childId: Long,
    val author: String,    // "Mom" or "Dad"
    val category: String,  // "food", "sensory", "calm_down", "general"
    val rawText: String,
    val timestamp: Long = System.currentTimeMillis()
)
