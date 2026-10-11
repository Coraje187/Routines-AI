package com.familyroutines

import android.Manifest
import android.content.pm.PackageManager
import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
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
        KioskViewModelFactory(app.repository, app.memoryEngine)
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

                val showVoiceDialog by viewModel.showVoiceDialog.collectAsState()
                val showAddKidDialog by viewModel.showAddKidDialog.collectAsState()
                val showAddChoreDialog by viewModel.showAddChoreDialog.collectAsState()
                val showAddRoutineDialog by viewModel.showAddRoutineDialog.collectAsState()
                val showAddEventDialog by viewModel.showAddEventDialog.collectAsState()

                val isListening by isListeningState
                val recognizedText by recognizedTextState

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
                        onSelectChild = { viewModel.selectChild(it) },
                        onSelectTab = { viewModel.selectTab(it) },
                        onAddKid = { viewModel.openAddKidDialog() },
                        onAddChore = { viewModel.openAddChoreDialog() },
                        onAddRoutine = { viewModel.openAddRoutineDialog() },
                        onSelectCalendarDate = { viewModel.setSelectedCalendarDate(it) },
                        onAddEvent = { preselectedDate -> viewModel.openAddEventDialog(preselectedDate) },
                        onDeleteEvent = { viewModel.deleteCalendarEvent(it) },
                        onToggleDarkTheme = { viewModel.toggleDarkTheme() },
                        onToggleRoutine = { viewModel.toggleRoutine(it) },
                        onToggleChore = { viewModel.toggleChore(it) },
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
                            onSave = { name, colorHex ->
                                viewModel.addKid(name, colorHex)
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
