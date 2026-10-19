package ca.gbc.comp3074.devtrack_mobile.ui.dashboard

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import ca.gbc.comp3074.devtrack_mobile.data.Task

class DashboardViewModel : ViewModel() {

    private val _tasks = MutableLiveData<List<Task>>().apply {
        value = listOf(
            Task(1, "COMP3074 Final Proposal", "Due: Tomorrow, 11:59 PM", "COMP3074", System.currentTimeMillis(), 3, false),
            Task(2, "COMP2129 Data Structures Lab 4", "Lab submission", "COMP2129", System.currentTimeMillis(), 2, false),
            Task(3, "MATH1105 Calculus Problem Set", "Review chapters 1-4", "MATH1105", System.currentTimeMillis(), 1, true)
        )
    }
    val tasks: LiveData<List<Task>> = _tasks

    private val _completionRate = MutableLiveData<Int>().apply {
        value = 50
    }
    val completionRate: LiveData<Int> = _completionRate

    fun toggleTaskCompletion(taskId: Int) {
        val currentList = _tasks.value.orEmpty().toMutableList()
        val index = currentList.indexOfFirst { it.taskId == taskId }
        if (index != -1) {
            val task = currentList[index]
            currentList[index] = task.copy(isCompleted = !task.isCompleted)
            _tasks.value = currentList

            // Recalculate progress
            val completedCount = currentList.count { it.isCompleted }
            val rate = if (currentList.isNotEmpty()) (completedCount * 100) / currentList.size else 0
            _completionRate.value = rate
        }
    }
}