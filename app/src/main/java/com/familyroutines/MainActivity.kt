package com.familyroutines

import android.Manifest
import android.content.pm.PackageManager
import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.compose.setContent
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Surface
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.core.content.ContextCompat
import androidx.core.view.WindowCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.WindowInsetsControllerCompat
import com.familyroutines.ui.components.*
import com.familyroutines.ui.screens.KioskDashboardScreen
import com.familyroutines.ui.theme.FamilyRoutinesTheme
import com.familyroutines.ui.viewmodel.KioskViewModel
import com.familyroutines.ui.viewmodel.KioskViewModelFactory
import com.familyroutines.voice.ParentVoiceListener

class MainActivity : ComponentActivity() {

    private var voiceListener: ParentVoiceListener? = null
    private var isListeningState = mutableStateOf(false)
    private var recognizedTextState = mutableStateOf("")

    private val requestPermissionLauncher = registerForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { isGranted: Boolean ->
        if (isGranted) {
            setupVoiceListener()
        } else {
            Toast.makeText(this, "Microphone permission is needed for voice notes.", Toast.LENGTH_SHORT).show()
        }
    }

    private val viewModel: KioskViewModel by viewModels {
        val app = application as FamilyApplication
        KioskViewModelFactory(app.repository, app.memoryEngine, app.weatherService, app.userPreferences)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        WindowCompat.setDecorFitsSystemWindows(window, false)
        checkAudioPermissionAndSetup()

        setContent {
            val isDarkTheme by viewModel.isDarkTheme.collectAsState()

            FamilyRoutinesTheme(darkTheme = isDarkTheme) {
                val children by viewModel.children.collectAsState()
                val selectedChild by viewModel.selectedChild.collectAsState()
                val selectedTab by viewModel.selectedTab.collectAsState()
                val routines by viewModel.routines.collectAsState()
                val chores by viewModel.chores.collectAsState()
                val calendarEvents by viewModel.calendarEvents.collectAsState()
                val selectedCalendarDate by viewModel.selectedCalendarDate.collectAsState()
                val assistantResponse by viewModel.assistantResponse.collectAsState()
                val isModelLoaded by viewModel.isModelLoaded.collectAsState()

                val weather by viewModel.weatherInfo.collectAsState()
                val showWeatherDialog by viewModel.showWeatherDialog.collectAsState()
                val familyPhotos by viewModel.familyPhotos.collectAsState()
                val rightPaneMode by viewModel.rightPaneMode.collectAsState()
                val editingKidForPhoto by viewModel.editingKidForPhoto.collectAsState()

                val showVoiceDialog by viewModel.showVoiceDialog.collectAsState()
                val showAddKidDialog by viewModel.showAddKidDialog.collectAsState()
                val showAddChoreDialog by viewModel.showAddChoreDialog.collectAsState()
                val showAddRoutineDialog by viewModel.showAddRoutineDialog.collectAsState()
                val showAddEventDialog by viewModel.showAddEventDialog.collectAsState()

                val isListening by isListeningState
                val recognizedText by recognizedTextState

                // Kid Photo Picker Launcher
                val kidPhotoPickerLauncher = rememberLauncherForActivityResult(
                    contract = ActivityResultContracts.GetContent()
                ) { uri ->
                    uri?.let { sourceUri ->
                        val targetChild = editingKidForPhoto ?: return@let
                        try {
                            val file = java.io.File(filesDir, "child_${targetChild.id}_avatar_${System.currentTimeMillis()}.jpg")
                            contentResolver.openInputStream(sourceUri)?.use { input ->
                                file.outputStream().use { output -> input.copyTo(output) }
                            }
                            viewModel.updateChildAvatar(targetChild, file.absolutePath)
                            Toast.makeText(this@MainActivity, "Photo updated for ${targetChild.name}!", Toast.LENGTH_SHORT).show()
                        } catch (e: Exception) {
                            Toast.makeText(this@MainActivity, "Failed to load photo: ${e.message}", Toast.LENGTH_SHORT).show()
                        }
                    }
                }

                // Family Photo Picker Launcher
                val familyPhotoPickerLauncher = rememberLauncherForActivityResult(
                    contract = ActivityResultContracts.GetContent()
                ) { uri ->
                    uri?.let { sourceUri ->
                        try {
                            val file = java.io.File(filesDir, "family_photo_${System.currentTimeMillis()}.jpg")
                            contentResolver.openInputStream(sourceUri)?.use { input ->
                                file.outputStream().use { output -> input.copyTo(output) }
                            }
                            viewModel.addFamilyPhoto(file.absolutePath, "Family Memory ❤️")
                            Toast.makeText(this@MainActivity, "Family photo added!", Toast.LENGTH_SHORT).show()
                        } catch (e: Exception) {
                            Toast.makeText(this@MainActivity, "Failed to save photo: ${e.message}", Toast.LENGTH_SHORT).show()
                        }
                    }
                }

                Surface(modifier = Modifier.fillMaxSize()) {
                    KioskDashboardScreen(
                        children = children,
                        selectedChild = selectedChild,
                        selectedTab = selectedTab,
                        routines = routines,
                        chores = chores,
                        calendarEvents = calendarEvents,
                        selectedCalendarDate = selectedCalendarDate,
                        assistantResponse = assistantResponse,
                        isModelLoaded = isModelLoaded,
                        isDarkTheme = isDarkTheme,
                        weather = weather,
                        familyPhotos = familyPhotos,
                        rightPaneMode = rightPaneMode,
                        onSelectChild = { viewModel.selectChild(it) },
                        onSelectTab = { viewModel.selectTab(it) },
                        onAddKid = { viewModel.openAddKidDialog() },
                        onDeleteChild = { viewModel.deleteChild(it) },
                        onEditKidPhoto = { viewModel.openEditKidPhotoDialog(it) },
                        onWeatherClick = { viewModel.openWeatherDialog() },
                        onSetRightPaneMode = { viewModel.setRightPaneMode(it) },
                        onAddFamilyPhoto = { familyPhotoPickerLauncher.launch("image/*") },
                        onDeleteFamilyPhoto = { viewModel.deleteFamilyPhoto(it) },
                        onUpdateFamilyPhotoCaption = { photo, caption ->
                            viewModel.updateFamilyPhotoCaption(photo, caption)
                        },
                        onAddChore = { viewModel.openAddChoreDialog() },
                        onAddRoutine = { viewModel.openAddRoutineDialog() },
                        onSelectCalendarDate = { viewModel.setSelectedCalendarDate(it) },
                        onAddEvent = { preselectedDate -> viewModel.openAddEventDialog(preselectedDate) },
                        onDeleteEvent = { viewModel.deleteCalendarEvent(it) },
                        onToggleDarkTheme = { viewModel.toggleDarkTheme() },
                        onToggleRoutine = { viewModel.toggleRoutine(it) },
                        onDeleteRoutine = { viewModel.deleteRoutine(it) },
                        onToggleChore = { viewModel.toggleChore(it) },
                        onDeleteChore = { viewModel.deleteChore(it) },
                        onAskQuery = { viewModel.askQuery(it) },
                        onRecordVoiceNote = {
                            recognizedTextState.value = ""
                            viewModel.openVoiceDialog()
                        }
                    )

                    // Add Kid Dialog
                    if (showAddKidDialog) {
                        AddKidDialog(
                            isDarkTheme = isDarkTheme,
                            onDismiss = { viewModel.closeAddKidDialog() },
                            onSave = { name, colorHex, avatarUri ->
                                viewModel.addKid(name, colorHex, avatarUri)
                            }
                        )
                    }

                    // Edit Kid Photo Dialog
                    if (editingKidForPhoto != null) {
                        val child = editingKidForPhoto!!
                        EditKidPhotoDialog(
                            child = child,
                            isDarkTheme = isDarkTheme,
                            onPickFromGallery = {
                                kidPhotoPickerLauncher.launch("image/*")
                            },
                            onSelectPresetAvatar = { emoji ->
                                viewModel.updateChildAvatar(child, "preset:$emoji")
                            },
                            onRemovePhoto = {
                                viewModel.updateChildAvatar(child, null)
                            },
                            onDismiss = { viewModel.closeEditKidPhotoDialog() }
                        )
                    }

                    // Weather & Location Dialog
                    if (showWeatherDialog) {
                        WeatherLocationDialog(
                            currentWeather = weather,
                            isDarkTheme = isDarkTheme,
                            onDismiss = { viewModel.closeWeatherDialog() },
                            onSaveLocation = { city, useFahrenheit ->
                                viewModel.updateWeatherLocation(city, useFahrenheit)
                            }
                        )
                    }

                    // Add Chore Dialog
                    if (showAddChoreDialog) {
                        AddChoreDialog(
                            childName = selectedChild?.name ?: "Kid",
                            isDarkTheme = isDarkTheme,
                            onDismiss = { viewModel.closeAddChoreDialog() },
                            onSave = { title, iconCategory, points ->
                                viewModel.addChore(title, iconCategory, points)
                            }
                        )
                    }

                    // Add Routine Dialog
                    if (showAddRoutineDialog) {
                        AddRoutineDialog(
                            childName = selectedChild?.name ?: "Kid",
                            isDarkTheme = isDarkTheme,
                            onDismiss = { viewModel.closeAddRoutineDialog() },
                            onSave = { title, timeBucket ->
                                viewModel.addRoutine(title, timeBucket)
                            }
                        )
                    }

                    // Add Calendar Event Dialog
                    if (showAddEventDialog) {
                        AddEventDialog(
                            children = children,
                            initialDate = selectedCalendarDate,
                            isDarkTheme = isDarkTheme,
                            onDismiss = { viewModel.closeAddEventDialog() },
                            onSave = { childId, title, timeSlot, category, dateString, colorHex ->
                                viewModel.addCalendarEvent(childId, title, timeSlot, category, dateString, colorHex)
                            }
                        )
                    }

                    // Voice Note Dialog
                    if (showVoiceDialog) {
                        VoiceNoteDialog(
                            childName = selectedChild?.name ?: "Child",
                            isListening = isListening,
                            recognizedText = recognizedText,
                            isDarkTheme = isDarkTheme,
                            onStartListening = { startVoiceInput() },
                            onStopListening = { stopVoiceInput() },
                            onSaveNote = { author, category, text ->
                                viewModel.saveNote(author, category, text)
                            },
                            onDismiss = {
                                stopVoiceInput()
                                viewModel.closeVoiceDialog()
                            }
                        )
                    }
                }
            }
        }
    }

    override fun onWindowFocusChanged(hasFocus: Boolean) {
        super.onWindowFocusChanged(hasFocus)
        if (hasFocus) {
            enableKioskImmersiveMode()
        }
    }

    private fun checkAudioPermissionAndSetup() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.RECORD_AUDIO) == PackageManager.PERMISSION_GRANTED) {
            setupVoiceListener()
        } else {
            requestPermissionLauncher.launch(Manifest.permission.RECORD_AUDIO)
        }
    }

    private fun setupVoiceListener() {
        voiceListener = ParentVoiceListener(
            context = this,
            onResult = { text ->
                recognizedTextState.value = text
                isListeningState.value = false
            },
            onError = { err ->
                isListeningState.value = false
                Toast.makeText(this, err, Toast.LENGTH_SHORT).show()
            }
        )
    }

    private fun startVoiceInput() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.RECORD_AUDIO) == PackageManager.PERMISSION_GRANTED) {
            isListeningState.value = true
            voiceListener?.startListening()
        } else {
            requestPermissionLauncher.launch(Manifest.permission.RECORD_AUDIO)
        }
    }

    private fun stopVoiceInput() {
        if (isListeningState.value) {
            isListeningState.value = false
            voiceListener?.stopListening()
        }
    }

    private fun enableKioskImmersiveMode() {
        val windowInsetsController = WindowCompat.getInsetsController(window, window.decorView)
        windowInsetsController.systemBarsBehavior =
            WindowInsetsControllerCompat.BEHAVIOR_SHOW_TRANSIENT_BARS_BY_SWIPE
        windowInsetsController.hide(WindowInsetsCompat.Type.systemBars())
    }

    override fun onDestroy() {
        super.onDestroy()
        voiceListener?.destroy()
    }
}
