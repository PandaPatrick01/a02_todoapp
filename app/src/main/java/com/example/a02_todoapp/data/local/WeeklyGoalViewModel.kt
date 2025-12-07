package com.example.a02_todoapp.data.local

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

// Platzhalter-Datenklasse für ein Wochenziel
data class WeeklyGoal(
    val id: Long = System.currentTimeMillis(),
    val title: String,
    val frequency: Int // z.B. 3 mal pro Woche
)

class WeeklyGoalViewModel(app: Application) : AndroidViewModel(app) {
    // Hier später echte DB-Anbindung
    private val _goals = MutableStateFlow<List<WeeklyGoal>>(emptyList())
    val goals: StateFlow<List<WeeklyGoal>> = _goals

    fun addGoal(title: String, frequency: Int) {
        val newGoal = WeeklyGoal(title = title, frequency = frequency)
        val currentList = _goals.value.toMutableList()
        currentList.add(newGoal)
        _goals.value = currentList
    }
}
