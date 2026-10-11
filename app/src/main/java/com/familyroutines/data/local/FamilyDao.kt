package com.familyroutines.data.local

import androidx.room.*
import com.familyroutines.data.local.entities.*
import kotlinx.coroutines.flow.Flow

@Dao
interface FamilyDao {
    // --- Children ---
    @Query("SELECT * FROM children ORDER BY name ASC")
    fun getAllChildren(): Flow<List<ChildEntity>>

    @Query("DELETE FROM children WHERE id NOT IN (SELECT MIN(id) FROM children GROUP BY name)")
    suspend fun deduplicateChildren()

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertChild(child: ChildEntity): Long

    @Update
    suspend fun updateChild(child: ChildEntity)

    @Delete
    suspend fun deleteChild(child: ChildEntity)

    // --- Routines ---
    @Query("SELECT * FROM routine_items WHERE childId = :childId ORDER BY orderIndex ASC")
    fun getRoutinesForChild(childId: Long): Flow<List<RoutineItemEntity>>

    @Update
    suspend fun updateRoutine(item: RoutineItemEntity)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertRoutine(item: RoutineItemEntity): Long

    @Delete
    suspend fun deleteRoutine(item: RoutineItemEntity)

    // --- Chores ---
    @Query("SELECT * FROM chores WHERE childId = :childId ORDER BY orderIndex ASC")
    fun getChoresForChild(childId: Long): Flow<List<ChoreEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertChore(chore: ChoreEntity): Long

    @Update
    suspend fun updateChore(chore: ChoreEntity)

    @Delete
    suspend fun deleteChore(chore: ChoreEntity)

    // --- Calendar Events ---
    @Query("SELECT * FROM calendar_events ORDER BY id ASC")
    fun getAllCalendarEvents(): Flow<List<CalendarEventEntity>>

    @Query("SELECT * FROM calendar_events WHERE childId = :childId OR childId IS NULL ORDER BY id ASC")
    fun getCalendarEventsForChild(childId: Long): Flow<List<CalendarEventEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertCalendarEvent(event: CalendarEventEntity): Long

    @Delete
    suspend fun deleteCalendarEvent(event: CalendarEventEntity)

    // --- Parent Notes ---
    @Query("SELECT * FROM parent_notes WHERE childId = :childId ORDER BY timestamp DESC")
    suspend fun getNotesForChild(childId: Long): List<ParentNoteEntity>

    @Query("SELECT * FROM parent_notes WHERE childId = :childId ORDER BY timestamp DESC")
    fun getNotesForChildFlow(childId: Long): Flow<List<ParentNoteEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertNote(note: ParentNoteEntity): Long

    @Delete
    suspend fun deleteNote(note: ParentNoteEntity)
}
