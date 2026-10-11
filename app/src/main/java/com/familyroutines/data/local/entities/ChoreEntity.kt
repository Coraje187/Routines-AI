package com.familyroutines.data.local.entities

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    tableName = "chores",
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
data class ChoreEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val childId: Long,
    val title: String,
    val iconCategory: String = "toys", // "bed", "toys", "pet", "dishes", "laundry", "backpack", "reading", "general"
    val pointValue: Int = 10,
    val isCompleted: Boolean = false,
    val orderIndex: Int = 0
)
