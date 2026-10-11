package com.familyroutines.data.repository

import com.familyroutines.data.local.FamilyDao
import com.familyroutines.data.local.entities.*
import kotlinx.coroutines.flow.Flow

class FamilyRepository(private val familyDao: FamilyDao) {

    // Children
    val allChildren: Flow<List<ChildEntity>> = familyDao.getAllChildren()

    suspend fun deduplicateChildren() = familyDao.deduplicateChildren()

    suspend fun insertChild(child: ChildEntity): Long = familyDao.insertChild(child)

    suspend fun updateChild(child: ChildEntity) = familyDao.updateChild(child)

    // Routines
    fun getRoutinesForChild(childId: Long): Flow<List<RoutineItemEntity>> =
        familyDao.getRoutinesForChild(childId)

    suspend fun updateRoutine(item: RoutineItemEntity) = familyDao.updateRoutine(item)

    suspend fun insertRoutine(item: RoutineItemEntity): Long = familyDao.insertRoutine(item)

    suspend fun deleteRoutine(item: RoutineItemEntity) = familyDao.deleteRoutine(item)

    // Chores
    fun getChoresForChild(childId: Long): Flow<List<ChoreEntity>> =
        familyDao.getChoresForChild(childId)

    suspend fun updateChore(chore: ChoreEntity) = familyDao.updateChore(chore)

    suspend fun insertChore(chore: ChoreEntity): Long = familyDao.insertChore(chore)

    suspend fun deleteChore(chore: ChoreEntity) = familyDao.deleteChore(chore)

    // Calendar
    val allCalendarEvents: Flow<List<CalendarEventEntity>> = familyDao.getAllCalendarEvents()

    fun getCalendarEventsForChild(childId: Long): Flow<List<CalendarEventEntity>> =
        familyDao.getCalendarEventsForChild(childId)

    suspend fun insertCalendarEvent(event: CalendarEventEntity): Long =
        familyDao.insertCalendarEvent(event)

    suspend fun deleteCalendarEvent(event: CalendarEventEntity) =
        familyDao.deleteCalendarEvent(event)

    // Parent Notes
    suspend fun getNotesForChild(childId: Long): List<ParentNoteEntity> =
        familyDao.getNotesForChild(childId)

    fun getNotesForChildFlow(childId: Long): Flow<List<ParentNoteEntity>> =
        familyDao.getNotesForChildFlow(childId)

    suspend fun insertNote(note: ParentNoteEntity): Long = familyDao.insertNote(note)

    suspend fun deleteNote(note: ParentNoteEntity) = familyDao.deleteNote(note)
}
