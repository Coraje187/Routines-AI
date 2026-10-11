package com.familyroutines.data.local

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.sqlite.db.SupportSQLiteDatabase
import com.familyroutines.data.local.entities.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

@Database(
    entities = [
        ChildEntity::class,
        RoutineItemEntity::class,
        ChoreEntity::class,
        CalendarEventEntity::class,
        ParentNoteEntity::class
    ],
    version = 3,
    exportSchema = false
)
abstract class FamilyDatabase : RoomDatabase() {

    abstract fun familyDao(): FamilyDao

    companion object {
        @Volatile
        private var INSTANCE: FamilyDatabase? = null

        fun getDatabase(context: Context, scope: CoroutineScope = CoroutineScope(Dispatchers.IO)): FamilyDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    FamilyDatabase::class.java,
                    "family_routines_database"
                )
                    .fallbackToDestructiveMigration()
                    .addCallback(DatabaseCallback(scope))
                    .build()
                INSTANCE = instance
                instance
            }
        }

        private class DatabaseCallback(
            private val scope: CoroutineScope
        ) : RoomDatabase.Callback() {
            override fun onCreate(db: SupportSQLiteDatabase) {
                super.onCreate(db)
                INSTANCE?.let { database ->
                    scope.launch {
                        seedDatabase(database.familyDao())
                    }
                }
            }

            private suspend fun seedDatabase(dao: FamilyDao) {
                // Seed Child 1: Leo
                val leoId = dao.insertChild(
                    ChildEntity(
                        name = "Leo",
                        colorHex = "#3B82F6",
                        stars = 35
                    )
                )

                // Seed Child 2: Maya
                val mayaId = dao.insertChild(
                    ChildEntity(
                        name = "Maya",
                        colorHex = "#EC4899",
                        stars = 50
                    )
                )

                // --- Leo's Routines ---
                dao.insertRoutine(RoutineItemEntity(childId = leoId, title = "Put on comfortable socks", timeBucket = "morning", orderIndex = 0))
                dao.insertRoutine(RoutineItemEntity(childId = leoId, title = "Warm bath with dim lights", timeBucket = "bedtime", orderIndex = 1))
                dao.insertRoutine(RoutineItemEntity(childId = leoId, title = "Eat breakfast (oatmeal)", timeBucket = "morning", orderIndex = 2))
                dao.insertRoutine(RoutineItemEntity(childId = leoId, title = "Weighted blanket on bed", timeBucket = "bedtime", orderIndex = 3))
                dao.insertRoutine(RoutineItemEntity(childId = leoId, title = "Brush teeth (bubblegum paste)", timeBucket = "morning", orderIndex = 4))
                dao.insertRoutine(RoutineItemEntity(childId = leoId, title = "Pack sensory backpack", timeBucket = "morning", orderIndex = 5))

                // --- Leo's Chores (with visual categories and point values) ---
                dao.insertChore(ChoreEntity(childId = leoId, title = "Make bed with smooth sheets", iconCategory = "bed", pointValue = 10, orderIndex = 0))
                dao.insertChore(ChoreEntity(childId = leoId, title = "Put toys in color bins", iconCategory = "toys", pointValue = 15, orderIndex = 1))
                dao.insertChore(ChoreEntity(childId = leoId, title = "Feed the cat dry food", iconCategory = "pet", pointValue = 10, orderIndex = 2))
                dao.insertChore(ChoreEntity(childId = leoId, title = "Put shoes on rack", iconCategory = "general", pointValue = 5, orderIndex = 3))
                dao.insertChore(ChoreEntity(childId = leoId, title = "Put dirty clothes in basket", iconCategory = "laundry", pointValue = 10, orderIndex = 4))

                // --- Maya's Chores ---
                dao.insertChore(ChoreEntity(childId = mayaId, title = "Make bed & arrange pillows", iconCategory = "bed", pointValue = 10, orderIndex = 0))
                dao.insertChore(ChoreEntity(childId = mayaId, title = "Wipe down kitchen table", iconCategory = "dishes", pointValue = 15, orderIndex = 1))
                dao.insertChore(ChoreEntity(childId = mayaId, title = "20 minutes reading time", iconCategory = "reading", pointValue = 20, orderIndex = 2))
                dao.insertChore(ChoreEntity(childId = mayaId, title = "Water the indoor plants", iconCategory = "general", pointValue = 10, orderIndex = 3))

                // --- Family Calendar Events (Skylight style, across October 2026) ---
                dao.insertCalendarEvent(CalendarEventEntity(childId = null, title = "School Bus Pick-up", timeSlot = "7:45 AM", category = "school", dayLabel = "Today", dateString = "2026-10-10", colorHex = "#F59E0B"))
                dao.insertCalendarEvent(CalendarEventEntity(childId = leoId, title = "Speech & Language Therapy", timeSlot = "10:30 AM - 11:15 AM", category = "therapy", dayLabel = "Today", dateString = "2026-10-10", colorHex = "#3B82F6"))
                dao.insertCalendarEvent(CalendarEventEntity(childId = null, title = "Family Dinner & Quiet Time", timeSlot = "6:00 PM - 7:00 PM", category = "family", dayLabel = "Today", dateString = "2026-10-10", colorHex = "#10B981"))
                dao.insertCalendarEvent(CalendarEventEntity(childId = leoId, title = "Sensory Gym / OT", timeSlot = "2:00 PM - 3:00 PM", category = "therapy", dayLabel = "Sunday", dateString = "2026-10-11", colorHex = "#3B82F6"))
                dao.insertCalendarEvent(CalendarEventEntity(childId = mayaId, title = "Swim Lessons", timeSlot = "4:30 PM - 5:15 PM", category = "activity", dayLabel = "Sunday", dateString = "2026-10-11", colorHex = "#EC4899"))
                dao.insertCalendarEvent(CalendarEventEntity(childId = leoId, title = "Pediatrician Checkup", timeSlot = "9:30 AM - 10:15 AM", category = "medical", dayLabel = "Wednesday", dateString = "2026-10-14", colorHex = "#EF4444"))
                dao.insertCalendarEvent(CalendarEventEntity(childId = null, title = "Park & Playground Outing", timeSlot = "10:00 AM - 12:00 PM", category = "activity", dayLabel = "Saturday", dateString = "2026-10-17", colorHex = "#8B5CF6"))
                dao.insertCalendarEvent(CalendarEventEntity(childId = mayaId, title = "Soccer Practice", timeSlot = "4:00 PM - 5:30 PM", category = "activity", dayLabel = "Tuesday", dateString = "2026-10-20", colorHex = "#EC4899"))
                dao.insertCalendarEvent(CalendarEventEntity(childId = leoId, title = "Occupational Therapy", timeSlot = "11:00 AM - 12:00 PM", category = "therapy", dayLabel = "Friday", dateString = "2026-10-23", colorHex = "#3B82F6"))
                dao.insertCalendarEvent(CalendarEventEntity(childId = null, title = "Halloween Costume Parade", timeSlot = "5:30 PM - 7:30 PM", category = "family", dayLabel = "Saturday", dateString = "2026-10-31", colorHex = "#F97316"))

                // --- Leo's Parent Notes ---
                dao.insertNote(ParentNoteEntity(childId = leoId, author = "Mom", category = "food", rawText = "Leo only eats oats if cinnamon is stirred in thoroughly. NEVER sprinkle cinnamon on top or he will refuse it."))
                dao.insertNote(ParentNoteEntity(childId = leoId, author = "Mom", category = "sensory", rawText = "Shirt tags must be cut off completely flat against the seam. Scratchy necklines cause distress."))
                dao.insertNote(ParentNoteEntity(childId = leoId, author = "Dad", category = "calm_down", rawText = "If Leo starts covering ears from noise, hand him green noise-cancelling headphones and wait 3 minutes in the quiet corner before speaking."))
            }
        }
    }
}
