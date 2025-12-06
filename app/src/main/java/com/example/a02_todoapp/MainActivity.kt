package com.example.a02_todoapp

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.animation.Crossfade
import androidx.compose.animation.core.tween
import androidx.compose.material3.Surface
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.a02_todoapp.data.local.ToDoViewModel
import com.example.a02_todoapp.ui.DayTaskScreen
import com.example.a02_todoapp.ui.EveningClosureScreen
import com.example.a02_todoapp.ui.FirstMoveScreen
import com.example.a02_todoapp.ui.FirstMoveSuccessScreen
import com.example.a02_todoapp.ui.MiddayResetScreen
import com.example.a02_todoapp.ui.MorningConfirmationScreen
import com.example.a02_todoapp.ui.MorningFocusScreen
import com.example.a02_todoapp.ui.MorningIntroScreen
import com.example.a02_todoapp.ui.TomorrowSetupScreen
import com.example.a02_todoapp.ui.theme._02_ToDoAppTheme

// Enum für den einfachen Navigations-Flow
enum class AppScreen {
    MORNING_INTRO,
    MORNING_FOCUS,
    MORNING_CONFIRMATION,
    FIRST_MOVE,
    FIRST_MOVE_SUCCESS,
    DAY_TASK_LIST,
    MIDDAY_RESET,
    EVENING_CLOSURE,
    TOMORROW_SETUP,
    TODO_LIST
}

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            _02_ToDoAppTheme {
                // ViewModel beziehen
                val vm: ToDoViewModel = viewModel()
                
                // State für den aktuellen Screen
                var currentScreen by remember { mutableStateOf(AppScreen.MORNING_INTRO) }
                
                // State für die getroffene Fokus-Auswahl
                var selectedFocus by remember { mutableStateOf("") }

                Surface {
                    // Crossfade für weichen Übergang zwischen allen Screens
                    Crossfade(
                        targetState = currentScreen,
                        animationSpec = tween(durationMillis = 600), // 600ms Überblendung
                        label = "screen_transition"
                    ) { screen ->
                        when (screen) {
                            AppScreen.MORNING_INTRO -> {
                                MorningIntroScreen(
                                    onFinish = {
                                        currentScreen = AppScreen.MORNING_FOCUS
                                    }
                                )
                            }
                            AppScreen.MORNING_FOCUS -> {
                                MorningFocusScreen(
                                    onFocusSelected = { focus ->
                                        selectedFocus = focus
                                        currentScreen = AppScreen.MORNING_CONFIRMATION
                                    }
                                )
                            }
                            AppScreen.MORNING_CONFIRMATION -> {
                                MorningConfirmationScreen(
                                    selection = selectedFocus,
                                    onFinish = {
                                        currentScreen = AppScreen.FIRST_MOVE
                                    }
                                )
                            }
                            AppScreen.FIRST_MOVE -> {
                                FirstMoveScreen(
                                    onAllCompleted = {
                                        // Weiter zu First Move Success
                                        currentScreen = AppScreen.FIRST_MOVE_SUCCESS
                                    }
                                )
                            }
                            AppScreen.FIRST_MOVE_SUCCESS -> {
                                FirstMoveSuccessScreen(
                                    onFinish = {
                                        // Weiter zur Tages-Aufgabenliste
                                        currentScreen = AppScreen.DAY_TASK_LIST
                                    }
                                )
                            }
                            AppScreen.DAY_TASK_LIST -> {
                                DayTaskScreen(
                                    viewModel = vm, // ViewModel übergeben
                                    onAllTasksDone = {
                                        // Wenn alle Aufgaben erledigt sind, weiter zum Midday Reset
                                        currentScreen = AppScreen.MIDDAY_RESET
                                    }
                                )
                            }
                            AppScreen.MIDDAY_RESET -> {
                                MiddayResetScreen(
                                    onTimerStart = {
                                        currentScreen = AppScreen.EVENING_CLOSURE
                                    }
                                )
                            }
                            AppScreen.EVENING_CLOSURE -> {
                                EveningClosureScreen(
                                    onFinish = {
                                        currentScreen = AppScreen.TOMORROW_SETUP
                                    }
                                )
                            }
                            AppScreen.TOMORROW_SETUP -> {
                                TomorrowSetupScreen(
                                    onFinish = {
                                        currentScreen = AppScreen.TODO_LIST
                                    }
                                )
                            }
                            AppScreen.TODO_LIST -> {
                                ToDoScreen(vm)
                            }
                        }
                    }
                }
            }
        }
    }
}
