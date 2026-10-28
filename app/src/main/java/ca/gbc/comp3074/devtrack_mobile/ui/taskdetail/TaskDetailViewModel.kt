package ca.gbc.comp3074.devtrack_mobile.ui.taskdetail

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import ca.gbc.comp3074.devtrack_mobile.data.AppDatabase
import ca.gbc.comp3074.devtrack_mobile.data.Task
import ca.gbc.comp3074.devtrack_mobile.data.TaskRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class TaskDetailViewModel(application: Application) : AndroidViewModel(application) {

    private val repository: TaskRepository

    init {
        val taskDao = AppDatabase.getDatabase(application).taskDao()
        repository = TaskRepository(taskDao)
    }

    fun saveTask(title: String, description: String, courseCode: String, priority: Int) {
        viewModelScope.launch(Dispatchers.IO) {
            val newTask = Task(
                title = title,
                description = description,
                courseCode = courseCode,
                dueDate = System.currentTimeMillis() + 86400000, // Default: Tomorrow
                priority = priority,
                isCompleted = false
            )
            repository.insert(newTask)
        }
    }

    fun deleteTask(taskId: Int) {
        viewModelScope.launch(Dispatchers.IO) {
            repository.delete(taskId)
        }
    }
}