import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.quicknotes.data.AppDatabase
import com.example.quicknotes.data.local.TodoEntity
import com.example.quicknotes.notifications.ReminderScheduler
import com.example.quicknotes.repository.TodoRepository
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class TodoViewModel(
    application: Application
) : AndroidViewModel(application) {

    private val dao =
        AppDatabase.getDatabase(application).todoDao()

    private val repo = TodoRepository(dao)

    val todos = repo.allTodos.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = emptyList()
    )

    fun insert(todo: TodoEntity) {

        viewModelScope.launch {

            val insertedId = repo.insert(todo)

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

    fun delete(todo: TodoEntity) {
        viewModelScope.launch {
            repo.delete(todo)
        }
    }

    fun toggleTodo(todo: TodoEntity) {
        viewModelScope.launch {
            repo.update(
                todo.copy(completed = !todo.completed)
            )
        }
    }

    fun isNotEmpty(): Boolean {
        return todos.value.isNotEmpty()
    }

    fun clearAll() {
        viewModelScope.launch {
            repo.clearAll()
        }
    }
}