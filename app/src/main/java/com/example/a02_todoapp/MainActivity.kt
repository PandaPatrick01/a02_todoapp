package com.example.a02_todoapp

import android.content.Context
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
import com.example.a02_todoapp.data.local.WeeklyGoalViewModel
import com.example.a02_todoapp.ui.DayTaskScreen
import com.example.a02_todoapp.ui.EveningClosureScreen
import com.example.a02_todoapp.ui.FirstMoveScreen
import com.example.a02_todoapp.ui.FirstMoveSuccessScreen
import com.example.a02_todoapp.ui.MiddayResetScreen
import com.example.a02_todoapp.ui.MorningConfirmationScreen
import com.example.a02_todoapp.ui.MorningFocusScreen
import com.example.a02_todoapp.ui.MorningIntroScreen
import com.example.a02_todoapp.ui.PlanningLongTermScreen
import com.example.a02_todoapp.ui.PlanningScreen
import com.example.a02_todoapp.ui.PlanningWeeklyScreen
import com.example.a02_todoapp.ui.TomorrowSetupScreen
import com.example.a02_todoapp.ui.theme._02_ToDoAppTheme
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

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
    TODO_LIST,
    PLANNING_OVERVIEW, // Neuer Screen für Planung
    PLANNING_WEEKLY,   // Neuer Screen für Wochenplanung
    PLANNING_LONGTERM  // Neuer Screen für Langzeitziele
}

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        // SharedPreferences für "Erster Start am Tag" Logik
        val prefs = getSharedPreferences("app_prefs", Context.MODE_PRIVATE)
        val sdf = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
        val todayDate = sdf.format(Date())
        
        val lastRunDate = prefs.getString("last_run_date", "")
        val storedFocus = prefs.getString("selected_focus", "")
        
        // Prüfung: Ist das der erste Start heute?
        val isFirstRunToday = lastRunDate != todayDate

        // Start-Screen festlegen
        val startScreen = if (isFirstRunToday) {
            // Neuer Tag -> Alles von vorne
            // Wir speichern das neue Datum erst, wenn der User wirklich interagiert hat oder jetzt sofort.
            // Speichern wir es jetzt, damit beim nächsten Kill/Start heute der verkürzte Ablauf kommt.
            prefs.edit().putString("last_run_date", todayDate).apply()
            AppScreen.MORNING_INTRO
        } else {
            // Schon mal da gewesen -> Nur Spruch (Confirmation) zeigen, dann Tasks
            // Wir brauchen aber einen gespeicherten Fokus, sonst Fallback auf Intro
            if (!storedFocus.isNullOrEmpty()) {
                AppScreen.MORNING_CONFIRMATION
            } else {
                AppScreen.MORNING_INTRO
            }
        }

        setContent {
            _02_ToDoAppTheme {
                // ViewModel beziehen
                val vm: ToDoViewModel = viewModel()
                val weeklyVm: WeeklyGoalViewModel = viewModel()
                
                // State für den aktuellen Screen
                var currentScreen by remember { mutableStateOf(startScreen) }
                
                // State für die getroffene Fokus-Auswahl
                // Falls wir im "Short-Cut"-Modus sind, laden wir den gespeicherten Fokus
                var selectedFocus by remember { mutableStateOf(if (!isFirstRunToday) storedFocus ?: "" else "") }

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
                                        // Fokus speichern für spätere Starts am gleichen Tag
                                        prefs.edit().putString("selected_focus", focus).apply()
                                        
                                        currentScreen = AppScreen.MORNING_CONFIRMATION
                                    }
                                )
                            }
                            AppScreen.MORNING_CONFIRMATION -> {
                                MorningConfirmationScreen(
                                    selection = selectedFocus,
                                    onFinish = {
                                        // Weichenstellung:
                                        if (isFirstRunToday) {
                                            // Normaler Ablauf: Weiter zu First Move
                                            currentScreen = AppScreen.FIRST_MOVE
                                        } else {
                                            // Verkürzter Ablauf: Direkt zu den Tasks
                                            currentScreen = AppScreen.DAY_TASK_LIST
                                        }
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
                                    viewModel = vm,
                                    onAllTasksDone = {
                                        // Wenn alle Aufgaben erledigt sind, weiter zum Midday Reset
                                        currentScreen = AppScreen.MIDDAY_RESET
                                    },
                                    onPlanningClick = {
                                        // Navigation zum Planungs-Screen
                                        currentScreen = AppScreen.PLANNING_OVERVIEW
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
                            // --- NEUE SCREENS FÜR PLANUNG ---
                            AppScreen.PLANNING_OVERVIEW -> {
                                PlanningScreen(
                                    onWeeklyPlanningClick = { currentScreen = AppScreen.PLANNING_WEEKLY },
                                    onLongTermGoalsClick = { currentScreen = AppScreen.PLANNING_LONGTERM }
                                )
                            }
                            AppScreen.PLANNING_WEEKLY -> {
                                PlanningWeeklyScreen(
                                    weeklyViewModel = weeklyVm,
                                    onNewWeekClick = {
                                        // Hier könnte man zur Detailansicht navigieren oder bleiben
                                    }
                                )
                            }
                            AppScreen.PLANNING_LONGTERM -> {
                                PlanningLongTermScreen(
                                    onBack = { currentScreen = AppScreen.PLANNING_OVERVIEW }
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}
