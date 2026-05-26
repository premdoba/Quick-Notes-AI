package com.example.quicknotes.data.local

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "todo_table")
data class TodoEntity(

    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,

    val title: String,

    val completed: Boolean = false,

    val reminderTime: Long? = null,

    val createdAt: Long = System.currentTimeMillis()
)