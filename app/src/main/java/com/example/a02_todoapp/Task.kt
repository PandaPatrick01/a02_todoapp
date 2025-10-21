package com.example.a02_todoapp


data class Task(
    val id: Long,
    val title: String,
    val done: Boolean = false,
    val category: Category = Category.OTHER
)