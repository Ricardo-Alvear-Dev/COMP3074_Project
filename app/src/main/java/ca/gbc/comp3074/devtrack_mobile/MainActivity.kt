
package ca.gbc.comp3074.devtrack_mobile

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView

class MainActivity : AppCompatActivity() {

    private lateinit var taskAdapter: TaskAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val recyclerViewTasks =
            findViewById<RecyclerView>(R.id.recyclerViewTasks)

        val btnAddTask =
            findViewById<Button>(R.id.btnAddTask)

        val btnOpenTimer =
            findViewById<Button>(R.id.btnOpenTimer)

        taskAdapter = TaskAdapter(

            onTaskChecked = { task, isChecked ->

                val updatedTask = task.copy(
                    isCompleted = isChecked
                )

                TaskStorage.updateTask(this, updatedTask)
                loadTasks()
            },

            onTaskEdit = { task ->

                val intent = Intent(
                    this,
                    AddTaskActivity::class.java
                )

                intent.putExtra("taskId", task.id)

                startActivity(intent)
            },

            onTaskDelete = { task ->

                showDeleteConfirmation(task)
            }
        )

        recyclerViewTasks.layoutManager =
            LinearLayoutManager(this)

        recyclerViewTasks.adapter = taskAdapter

        btnAddTask.setOnClickListener {

            startActivity(
                Intent(this, AddTaskActivity::class.java)
            )
        }

        btnOpenTimer.setOnClickListener {

            startActivity(
                Intent(this, TimerActivity::class.java)
            )
        }

        loadTasks()
    }

    override fun onResume() {
        super.onResume()

        if (::taskAdapter.isInitialized) {
            loadTasks()
        }
    }

    private fun loadTasks() {

        val tasks = TaskStorage.getTasks(this)

        val sortedTasks = tasks.sortedWith(
            compareBy<Task> { it.isCompleted }
                .thenBy {
                    it.dueDate.ifEmpty { "9999-12-31" }
                }
        )

        taskAdapter.updateTasks(sortedTasks)
    }

    private fun showDeleteConfirmation(task: Task) {

        AlertDialog.Builder(this)
            .setTitle("Delete Task")
            .setMessage(
                "Are you sure you want to delete \"${task.title}\"?"
            )
            .setPositiveButton("Delete") { _, _ ->

                TaskStorage.deleteTask(this, task.id)

                loadTasks()

                Toast.makeText(
                    this,
                    "Task deleted successfully!",
                    Toast.LENGTH_SHORT
                ).show()
            }
            .setNegativeButton("Cancel", null)
            .show()
    }
}
