package com.example.a02_todoapp.data

import com.example.a02_todoapp.Task
import com.example.a02_todoapp.Category
import com.example.a02_todoapp.data.local.TaskDao
import com.example.a02_todoapp.data.local.toDomain
import com.example.a02_todoapp.data.local.toEntity
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class TaskRepository(private val dao: TaskDao) {

    val tasks: Flow<List<Task>> =
        dao.observeAll().map { list -> list.map { it.toDomain() } }

    suspend fun add(title: String, category: Category) {
        dao.insert(Task(
            id = 0L,
            title = title.trim(),
            done = false,
            category = category
        ).toEntity())
    }

    suspend fun toggle(task: Task) {
        dao.update(task.copy(done = !task.done).toEntity())
    }

    suspend fun delete(id: Long) {
        dao.deleteById(id)
    }
}
