package com.familyroutines.data.local.entities

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    tableName = "calendar_events",
    foreignKeys = [
        ForeignKey(
            entity = ChildEntity::class,
            parentColumns = ["id"],
            childColumns = ["childId"],
            onDelete = ForeignKey.SET_NULL
        )
    ],
    indices = [Index("childId")]
)
data class CalendarEventEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val childId: Long? = null, // null means whole family event
    val title: String,
    val timeSlot: String, // e.g. "8:30 AM - 9:15 AM"
    val category: String = "routine", // "therapy", "school", "activity", "medical", "family"
    val dayLabel: String = "Today", // "Today", "Tomorrow", "Wednesday", etc.
    val dateString: String = "2026-10-10", // ISO YYYY-MM-DD
    val colorHex: String = "#3B82F6"
)
