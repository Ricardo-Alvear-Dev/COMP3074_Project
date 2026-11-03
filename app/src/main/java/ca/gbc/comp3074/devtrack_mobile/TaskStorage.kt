
package ca.gbc.comp3074.devtrack_mobile

import android.content.Context
import org.json.JSONArray
import org.json.JSONObject

data class Task(
    val title: String,
    val description: String,
    val isCompleted: Boolean = false
)

object TaskStorage {

    private const val PREFS_NAME = "DevTrackTasks"
    private const val TASKS_KEY = "tasks"

    fun getTasks(context: Context): MutableList<Task> {
        val preferences = context.getSharedPreferences(
            PREFS_NAME,
            Context.MODE_PRIVATE
        )

        val savedTasks = preferences.getString(TASKS_KEY, "[]")
        val jsonArray = JSONArray(savedTasks)

        val tasks = mutableListOf<Task>()

        for (i in 0 until jsonArray.length()) {
            val item = jsonArray.getJSONObject(i)

            tasks.add(
                Task(
                    title = item.getString("title"),
                    description = item.getString("description"),
                    isCompleted = item.optBoolean("isCompleted", false)
                )
            )
        }

        return tasks
    }

    fun addTask(context: Context, task: Task) {
        val tasks = getTasks(context)
        tasks.add(task)
        saveTasks(context, tasks)
    }

    fun updateTask(
        context: Context,
        updatedTask: Task,
        isCompleted: Boolean
    ) {
        val tasks = getTasks(context)

        val index = tasks.indexOf(updatedTask)

        if (index != -1) {
            tasks[index] = updatedTask.copy(isCompleted = isCompleted)
            saveTasks(context, tasks)
        }
    }

    private fun saveTasks(context: Context, tasks: List<Task>) {
        val jsonArray = JSONArray()

        tasks.forEach { task ->
            val item = JSONObject()
            item.put("title", task.title)
            item.put("description", task.description)
            item.put("isCompleted", task.isCompleted)
            jsonArray.put(item)
        }

        context.getSharedPreferences(
            PREFS_NAME,
            Context.MODE_PRIVATE
        ).edit()
            .putString(TASKS_KEY, jsonArray.toString())
            .apply()
    }
}
