package com.example.a02_todoapp.data.local

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.example.a02_todoapp.Category
import com.example.a02_todoapp.Task

@Entity(tableName = "tasks")
data class TaskEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0L,
    val title: String,
    val done: Boolean,
    val category: String // Category.name
)

fun TaskEntity.toDomain() = Task(id, title, done, Category.valueOf(category))
fun Task.toEntity() = TaskEntity(
    id = if (id == 0L) 0L else id,
    title = title,
    done = done,
    category = category.name
)