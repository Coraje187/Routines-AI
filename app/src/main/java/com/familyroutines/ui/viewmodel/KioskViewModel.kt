package com.familyroutines.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.familyroutines.ai.FamilyMemoryEngine
import com.familyroutines.data.local.entities.*
import com.familyroutines.data.repository.FamilyRepository
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

@OptIn(ExperimentalCoroutinesApi::class)
class KioskViewModel(
    private val repository: FamilyRepository,
    private val memoryEngine: FamilyMemoryEngine
) : ViewModel() {

    val children: StateFlow<List<ChildEntity>> = repository.allChildren
        .map { list -> list.distinctBy { it.name } }
        .stateIn(viewModelScope, SharingStarted.Eagerly, emptyList())

    private val _selectedChildId = MutableStateFlow<Long?>(null)
    val selectedChildId: StateFlow<Long?> = _selectedChildId.asStateFlow()

    private val _isDarkTheme = MutableStateFlow(false)
    val isDarkTheme: StateFlow<Boolean> = _isDarkTheme.asStateFlow()

    private val _selectedTab = MutableStateFlow("routines") // "routines", "chores", "calendar"
    val selectedTab: StateFlow<String> = _selectedTab.asStateFlow()

    // Dialog state flags
    private val _showAddKidDialog = MutableStateFlow(false)
    val showAddKidDialog: StateFlow<Boolean> = _showAddKidDialog.asStateFlow()

    private val _showAddChoreDialog = MutableStateFlow(false)
    val showAddChoreDialog: StateFlow<Boolean> = _showAddChoreDialog.asStateFlow()

    private val _showAddRoutineDialog = MutableStateFlow(false)
    val showAddRoutineDialog: StateFlow<Boolean> = _showAddRoutineDialog.asStateFlow()

    private val _showAddEventDialog = MutableStateFlow(false)
    val showAddEventDialog: StateFlow<Boolean> = _showAddEventDialog.asStateFlow()

    private val _selectedCalendarDate = MutableStateFlow("2026-10-10")
    val selectedCalendarDate: StateFlow<String> = _selectedCalendarDate.asStateFlow()

    private val _showVoiceDialog = MutableStateFlow(false)
    val showVoiceDialog: StateFlow<Boolean> = _showVoiceDialog.asStateFlow()

    init {
        viewModelScope.launch {
            repository.deduplicateChildren()
            children.collect { list ->
                if (list.isEmpty()) {
                    seedDatabaseV2()
                } else if (_selectedChildId.value == null) {
                    _selectedChildId.value = list.first().id
                }
            }
        }

        viewModelScope.launch {
            val loaded = memoryEngine.initialize()
            _isModelLoaded.value = loaded
        }
    }

    private suspend fun seedDatabaseV2() {
        val leoId = repository.insertChild(ChildEntity(name = "Leo", colorHex = "#3B82F6", stars = 35))
        val mayaId = repository.insertChild(ChildEntity(name = "Maya", colorHex = "#EC4899", stars = 50))
        _selectedChildId.value = leoId

        // Leo routines
        repository.insertRoutine(RoutineItemEntity(childId = leoId, title = "Put on comfortable socks", timeBucket = "morning", orderIndex = 0))
        repository.insertRoutine(RoutineItemEntity(childId = leoId, title = "Warm bath with dim lights", timeBucket = "bedtime", orderIndex = 1))
        repository.insertRoutine(RoutineItemEntity(childId = leoId, title = "Eat breakfast (oatmeal)", timeBucket = "morning", orderIndex = 2))
        repository.insertRoutine(RoutineItemEntity(childId = leoId, title = "Weighted blanket on bed", timeBucket = "bedtime", orderIndex = 3))
        repository.insertRoutine(RoutineItemEntity(childId = leoId, title = "Brush teeth (bubblegum paste)", timeBucket = "morning", orderIndex = 4))
        repository.insertRoutine(RoutineItemEntity(childId = leoId, title = "Pack sensory backpack", timeBucket = "morning", orderIndex = 5))

        // Leo chores
        repository.insertChore(ChoreEntity(childId = leoId, title = "Make bed with smooth sheets", iconCategory = "bed", pointValue = 10, orderIndex = 0))
        repository.insertChore(ChoreEntity(childId = leoId, title = "Put toys in color bins", iconCategory = "toys", pointValue = 15, orderIndex = 1))
        repository.insertChore(ChoreEntity(childId = leoId, title = "Feed the cat dry food", iconCategory = "pet", pointValue = 10, orderIndex = 2))
        repository.insertChore(ChoreEntity(childId = leoId, title = "Put shoes on rack", iconCategory = "general", pointValue = 5, orderIndex = 3))
        repository.insertChore(ChoreEntity(childId = leoId, title = "Put dirty clothes in basket", iconCategory = "laundry", pointValue = 10, orderIndex = 4))

        // Maya chores
        repository.insertChore(ChoreEntity(childId = mayaId, title = "Make bed & arrange pillows", iconCategory = "bed", pointValue = 10, orderIndex = 0))
        repository.insertChore(ChoreEntity(childId = mayaId, title = "Wipe down kitchen table", iconCategory = "dishes", pointValue = 15, orderIndex = 1))
        repository.insertChore(ChoreEntity(childId = mayaId, title = "20 minutes reading time", iconCategory = "reading", pointValue = 20, orderIndex = 2))
        repository.insertChore(ChoreEntity(childId = mayaId, title = "Water the indoor plants", iconCategory = "general", pointValue = 10, orderIndex = 3))

        // Calendar events spread across October 2026
        repository.insertCalendarEvent(CalendarEventEntity(childId = null, title = "School Bus Pick-up", timeSlot = "7:45 AM", category = "school", dayLabel = "Today", dateString = "2026-10-10", colorHex = "#F59E0B"))
        repository.insertCalendarEvent(CalendarEventEntity(childId = leoId, title = "Speech & Language Therapy", timeSlot = "10:30 AM - 11:15 AM", category = "therapy", dayLabel = "Today", dateString = "2026-10-10", colorHex = "#3B82F6"))
        repository.insertCalendarEvent(CalendarEventEntity(childId = null, title = "Family Dinner & Movie", timeSlot = "6:00 PM - 7:00 PM", category = "family", dayLabel = "Today", dateString = "2026-10-10", colorHex = "#10B981"))
        repository.insertCalendarEvent(CalendarEventEntity(childId = leoId, title = "Sensory Gym / OT", timeSlot = "2:00 PM - 3:00 PM", category = "therapy", dayLabel = "Sunday", dateString = "2026-10-11", colorHex = "#3B82F6"))
        repository.insertCalendarEvent(CalendarEventEntity(childId = mayaId, title = "Swim Lessons", timeSlot = "4:30 PM - 5:15 PM", category = "activity", dayLabel = "Sunday", dateString = "2026-10-11", colorHex = "#EC4899"))
        repository.insertCalendarEvent(CalendarEventEntity(childId = leoId, title = "Pediatrician Checkup", timeSlot = "9:30 AM - 10:15 AM", category = "medical", dayLabel = "Wednesday", dateString = "2026-10-14", colorHex = "#EF4444"))
        repository.insertCalendarEvent(CalendarEventEntity(childId = null, title = "Park & Playground Outing", timeSlot = "10:00 AM - 12:00 PM", category = "activity", dayLabel = "Saturday", dateString = "2026-10-17", colorHex = "#8B5CF6"))
        repository.insertCalendarEvent(CalendarEventEntity(childId = mayaId, title = "Soccer Practice", timeSlot = "4:00 PM - 5:30 PM", category = "activity", dayLabel = "Tuesday", dateString = "2026-10-20", colorHex = "#EC4899"))
        repository.insertCalendarEvent(CalendarEventEntity(childId = leoId, title = "Occupational Therapy", timeSlot = "11:00 AM - 12:00 PM", category = "therapy", dayLabel = "Friday", dateString = "2026-10-23", colorHex = "#3B82F6"))
        repository.insertCalendarEvent(CalendarEventEntity(childId = null, title = "Halloween Costume Parade", timeSlot = "5:30 PM - 7:30 PM", category = "family", dayLabel = "Saturday", dateString = "2026-10-31", colorHex = "#F97316"))

        // Notes
        repository.insertNote(ParentNoteEntity(childId = leoId, author = "Mom", category = "food", rawText = "Leo only eats oats if cinnamon is stirred in thoroughly. NEVER sprinkle cinnamon on top or he will refuse it."))
        repository.insertNote(ParentNoteEntity(childId = leoId, author = "Mom", category = "sensory", rawText = "Shirt tags must be cut off completely flat against the seam. Scratchy necklines cause distress."))
        repository.insertNote(ParentNoteEntity(childId = leoId, author = "Dad", category = "calm_down", rawText = "If Leo starts covering ears from noise, hand him green noise-cancelling headphones and wait 3 minutes in the quiet corner before speaking."))
    }

    fun toggleDarkTheme() {
        _isDarkTheme.value = !_isDarkTheme.value
    }

    fun selectTab(tab: String) {
        _selectedTab.value = tab
    }

    fun selectChild(childId: Long) {
        _selectedChildId.value = childId
    }

    // Kid Management
    fun openAddKidDialog() { _showAddKidDialog.value = true }
    fun closeAddKidDialog() { _showAddKidDialog.value = false }
    fun addKid(name: String, colorHex: String) {
        viewModelScope.launch {
            val id = repository.insertChild(ChildEntity(name = name, colorHex = colorHex, stars = 0))
            _selectedChildId.value = id
        }
    }

    // Chore Management
    fun openAddChoreDialog() { _showAddChoreDialog.value = true }
    fun closeAddChoreDialog() { _showAddChoreDialog.value = false }
    fun addChore(title: String, iconCategory: String, pointValue: Int) {
        val childId = _selectedChildId.value ?: return
        viewModelScope.launch {
            repository.insertChore(
                ChoreEntity(
                    childId = childId,
                    title = title,
                    iconCategory = iconCategory,
                    pointValue = pointValue
                )
            )
        }
    }

    // Routine Management
    fun openAddRoutineDialog() { _showAddRoutineDialog.value = true }
    fun closeAddRoutineDialog() { _showAddRoutineDialog.value = false }
    fun addRoutine(title: String, timeBucket: String) {
        val childId = _selectedChildId.value ?: return
        viewModelScope.launch {
            repository.insertRoutine(
                RoutineItemEntity(
                    childId = childId,
                    title = title,
                    timeBucket = timeBucket
                )
            )
        }
    }

    // Calendar Event Management
    fun setSelectedCalendarDate(date: String) {
        _selectedCalendarDate.value = date
    }

    fun openAddEventDialog(preselectedDate: String = "") {
        if (preselectedDate.isNotBlank()) {
            _selectedCalendarDate.value = preselectedDate
        }
        _showAddEventDialog.value = true
    }

    fun closeAddEventDialog() {
        _showAddEventDialog.value = false
    }

    fun addCalendarEvent(
        childId: Long?,
        title: String,
        timeSlot: String,
        category: String,
        dateString: String,
        colorHex: String
    ) {
        viewModelScope.launch {
            repository.insertCalendarEvent(
                CalendarEventEntity(
                    childId = childId,
                    title = title,
                    timeSlot = timeSlot,
                    category = category,
                    dayLabel = dateString,
                    dateString = dateString,
                    colorHex = colorHex
                )
            )
        }
    }

    fun deleteCalendarEvent(event: CalendarEventEntity) {
        viewModelScope.launch {
            repository.deleteCalendarEvent(event)
        }
    }

    val selectedChild: StateFlow<ChildEntity?> = combine(children, _selectedChildId) { list, id ->
        list.find { it.id == id } ?: list.firstOrNull()
    }.stateIn(viewModelScope, SharingStarted.Eagerly, null)

    val routines: StateFlow<List<RoutineItemEntity>> = _selectedChildId
        .flatMapLatest { id ->
            if (id != null) repository.getRoutinesForChild(id) else flowOf(emptyList())
        }
        .stateIn(viewModelScope, SharingStarted.Eagerly, emptyList())

    val chores: StateFlow<List<ChoreEntity>> = _selectedChildId
        .flatMapLatest { id ->
            if (id != null) repository.getChoresForChild(id) else flowOf(emptyList())
        }
        .stateIn(viewModelScope, SharingStarted.Eagerly, emptyList())

    val calendarEvents: StateFlow<List<CalendarEventEntity>> = repository.allCalendarEvents
        .stateIn(viewModelScope, SharingStarted.Eagerly, emptyList())

    val notes: StateFlow<List<ParentNoteEntity>> = _selectedChildId
        .flatMapLatest { id ->
            if (id != null) repository.getNotesForChildFlow(id) else flowOf(emptyList())
        }
        .stateIn(viewModelScope, SharingStarted.Eagerly, emptyList())

    private val _assistantResponse = MutableStateFlow("")
    val assistantResponse: StateFlow<String> = _assistantResponse.asStateFlow()

    private val _isModelLoaded = MutableStateFlow(false)
    val isModelLoaded: StateFlow<Boolean> = _isModelLoaded.asStateFlow()

    fun toggleRoutine(item: RoutineItemEntity) {
        viewModelScope.launch {
            val willBeCompleted = !item.isCompleted
            repository.updateRoutine(item.copy(isCompleted = willBeCompleted))

            // Award 5 stars on completion, or deduct 5 stars on uncompleting
            selectedChild.value?.let { child ->
                val delta = if (willBeCompleted) 5 else -5
                val newStars = (child.stars + delta).coerceAtLeast(0)
                repository.updateChild(child.copy(stars = newStars))
            }
        }
    }

    fun toggleChore(chore: ChoreEntity) {
        viewModelScope.launch {
            val willBeCompleted = !chore.isCompleted
            repository.updateChore(chore.copy(isCompleted = willBeCompleted))

            // Award / deduct points for chores
            selectedChild.value?.let { child ->
                val delta = if (willBeCompleted) chore.pointValue else -chore.pointValue
                val newStars = (child.stars + delta).coerceAtLeast(0)
                repository.updateChild(child.copy(stars = newStars))
            }
        }
    }

    fun askQuery(query: String) {
        val childId = _selectedChildId.value ?: return
        viewModelScope.launch {
            val currentChild = selectedChild.value?.name ?: "Leo"
            val currentNotes = repository.getNotesForChild(childId)
            _assistantResponse.value = "Consulting Routines A.I Brain..."
            val response = memoryEngine.askAssistant(currentChild, currentNotes, query)
            _assistantResponse.value = response
        }
    }

    fun openVoiceDialog() {
        _showVoiceDialog.value = true
    }

    fun closeVoiceDialog() {
        _showVoiceDialog.value = false
    }

    fun saveNote(author: String, category: String, text: String) {
        val childId = _selectedChildId.value ?: return
        viewModelScope.launch {
            repository.insertNote(
                ParentNoteEntity(
                    childId = childId,
                    author = author,
                    category = category,
                    rawText = text
                )
            )
        }
    }

    override fun onCleared() {
        super.onCleared()
        memoryEngine.close()
    }
}

class KioskViewModelFactory(
    private val repository: FamilyRepository,
    private val memoryEngine: FamilyMemoryEngine
) : ViewModelProvider.Factory {
    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(KioskViewModel::class.java)) {
            return KioskViewModel(repository, memoryEngine) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}
