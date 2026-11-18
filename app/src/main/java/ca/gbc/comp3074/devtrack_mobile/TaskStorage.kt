
package ca.gbc.comp3074.devtrack_mobile

import android.content.Context
import org.json.JSONArray
import org.json.JSONObject
import java.util.UUID

data class Task(
    val title: String,
    val description: String,
    val isCompleted: Boolean = false,
    val dueDate: String = "",
    val id: String = UUID.randomUUID().toString()
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

        var needsMigration = false

        for (i in 0 until jsonArray.length()) {
            val item = jsonArray.getJSONObject(i)

            val id = item.optString("id", "")

            if (id.isEmpty()) {
                needsMigration = true
            }

            tasks.add(
                Task(
                    id = id.ifEmpty { UUID.randomUUID().toString() },
                    title = item.getString("title"),
                    description = item.getString("description"),
                    isCompleted = item.optBoolean("isCompleted", false),
                    dueDate = item.optString("dueDate", "")
                )
            )
        }

        if (needsMigration) {
            saveTasks(context, tasks)
        }

        return tasks
    }

    fun addTask(context: Context, task: Task) {
        val tasks = getTasks(context)
        tasks.add(task)
        saveTasks(context, tasks)
    }

    fun getTask(context: Context, id: String): Task? {
        return getTasks(context).find { it.id == id }
    }

    fun updateTask(context: Context, updatedTask: Task) {
        val tasks = getTasks(context)

        val index = tasks.indexOfFirst {
            it.id == updatedTask.id
        }

        if (index != -1) {
            tasks[index] = updatedTask
            saveTasks(context, tasks)
        }
    }

    fun deleteTask(context: Context, id: String) {
        val tasks = getTasks(context)

        tasks.removeAll { it.id == id }

        saveTasks(context, tasks)
    }

    private fun saveTasks(context: Context, tasks: List<Task>) {
        val jsonArray = JSONArray()

        tasks.forEach { task ->
            val item = JSONObject()

            item.put("id", task.id)
            item.put("title", task.title)
            item.put("description", task.description)
            item.put("isCompleted", task.isCompleted)
            item.put("dueDate", task.dueDate)

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
