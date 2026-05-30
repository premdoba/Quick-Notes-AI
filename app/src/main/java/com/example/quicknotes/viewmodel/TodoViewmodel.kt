package com.example.quicknotes.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.quicknotes.data.AppDatabase
import com.example.quicknotes.data.repository.TodoRepositoryImpl
import com.example.quicknotes.domain.model.Todo
import com.example.quicknotes.domain.repository.TodoRepository
import com.example.quicknotes.notification.ReminderScheduler
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class TodoViewModel(
    application: Application
) : AndroidViewModel(application) {

    private val dao =
        AppDatabase.getDatabase(application).todoDao()

    private val repo: TodoRepository = TodoRepositoryImpl(dao)

    val todos = repo.getAllTodos().stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = emptyList()
    )

    fun insert(todo: Todo) {
        viewModelScope.launch {
            val insertedId = repo.insertTodo(todo)
            todo.reminderTime?.let { reminderTime ->
                ReminderScheduler.scheduleReminder(
                    context = getApplication<Application>(),
                    taskId = insertedId.toInt(),
                    title = todo.title,
                    timeInMillis = reminderTime
                )
            }
        }
    }

    fun delete(todo: Todo) {
        viewModelScope.launch {
            repo.deleteTodo(todo)
        }
    }

    fun toggleTodo(todo: Todo) {
        viewModelScope.launch {
            repo.updateTodo(
                todo.copy(completed = !todo.completed)
            )
        }
    }

    fun isNotEmpty(): Boolean {
        return todos.value.isNotEmpty()
    }

    fun clearAll() {
        viewModelScope.launch {
            repo.clearAllTodos()
        }
    }
}