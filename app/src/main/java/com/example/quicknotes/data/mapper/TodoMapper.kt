package com.example.quicknotes.data.mapper

import com.example.quicknotes.data.local.TodoEntity
import com.example.quicknotes.domain.model.Todo

fun TodoEntity.toDomain(): Todo {
    return Todo(
        id = id,
        title = title,
        completed = completed,
        reminderTime = reminderTime,
        createdAt = createdAt
    )
}

fun Todo.toEntity(): TodoEntity {
    return TodoEntity(
        id = id,
        title = title,
        completed = completed,
        reminderTime = reminderTime,
        createdAt = createdAt
    )
}