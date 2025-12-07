package com.example.a02_todoapp.data.local


import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.a02_todoapp.Category
import com.example.a02_todoapp.Task
import com.example.a02_todoapp.data.TaskRepository
import com.example.a02_todoapp.data.local.AppDatabase
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue

class ToDoViewModel(app: Application) : AndroidViewModel(app) {

    private val repo = TaskRepository(AppDatabase.get(app).taskDao())

    // UI-State (Compose-reaktiv)
    var input by mutableStateOf("")
        private set

    var selectedCategory by mutableStateOf(Category.OTHER)
        private set

    // Liste aus der DB (Flow -> StateFlow)
    val tasks: StateFlow<List<Task>> =
        repo.tasks.stateIn(viewModelScope, SharingStarted.Lazily, emptyList())

    init {
        // Testdaten neu befüllen
        viewModelScope.launch(Dispatchers.IO) {
            val currentTasks = repo.tasks.first()
            
            // Alles löschen für sauberen Test-Start
            currentTasks.forEach { task ->
                repo.delete(task.id)
            }

            // 10 neue Aufgaben quer durch alle Kategorien
            val testTasks = listOf(
                "Morgen-Jogging (30 min)" to Category.PHYSICAL,
                "E-Mails sortieren" to Category.WORK,
                "Einkaufen für die Woche" to Category.PERSONAL,
                "Kapitel 4 lernen" to Category.STUDY,
                "Rechnung überweisen" to Category.OTHER,
                "Yoga Session" to Category.PHYSICAL,
                "Meeting Protokoll schreiben" to Category.WORK,
                "Mama anrufen" to Category.PERSONAL,
                "Vokabeln wiederholen" to Category.STUDY,
                "Pflanzen gießen" to Category.OTHER
            )
            testTasks.forEach { (title, cat) ->
                repo.add(title, cat)
            }
        }
    }

    // 🔹 DIE Input-Funktion:
    fun onInputChange(v: String) {
        input = v
    }

    fun onCategoryChange(c: Category) {
        selectedCategory = c
    }

    fun add() {
        val t = input.trim()
        if (t.isEmpty()) return
        viewModelScope.launch(Dispatchers.IO) {
            repo.add(t, selectedCategory)
        }
        // Feld nach Add leeren
        input = ""
    }

    fun toggle(id: Long) {
        tasks.value.firstOrNull { it.id == id }?.let { task ->
            viewModelScope.launch(Dispatchers.IO) { repo.toggle(task) }
        }
    }

    fun delete(id: Long) {
        viewModelScope.launch(Dispatchers.IO) { repo.delete(id) }
    }
}
