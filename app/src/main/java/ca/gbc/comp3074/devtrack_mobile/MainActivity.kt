
package ca.gbc.comp3074.devtrack_mobile

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.ProgressBar
import android.widget.TextView
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.core.widget.doAfterTextChanged
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView

class MainActivity : AppCompatActivity() {

    private lateinit var taskAdapter: TaskAdapter
    private lateinit var editSearch: EditText
    private lateinit var progressBar: ProgressBar
    private lateinit var textProgressPercentage: TextView

    private var allTasks: List<Task> = emptyList()

    private val notificationPermissionLauncher =
        registerForActivityResult(
            ActivityResultContracts.RequestPermission()
        ) { isGranted ->

            if (isGranted) {
                NotificationScheduler.checkNow(this)
            }
        }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        NotificationHelper.createNotificationChannel(this)
        requestNotificationPermission()
        NotificationScheduler.scheduleDeadlineChecks(this)

        val recyclerViewTasks =
            findViewById<RecyclerView>(R.id.recyclerViewTasks)

        val btnAddTask =
            findViewById<Button>(R.id.btnAddTask)

        val btnOpenTimer =
            findViewById<Button>(R.id.btnOpenTimer)

        editSearch = findViewById(R.id.editSearch)

        progressBar = findViewById(R.id.progressBar)

        textProgressPercentage =
            findViewById(R.id.textProgressPercentage)

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

        editSearch.doAfterTextChanged { text ->
            filterTasks(text.toString())
        }

        loadTasks()
    }

    override fun onResume() {
        super.onResume()

        if (::taskAdapter.isInitialized) {
            loadTasks()
            NotificationScheduler.checkNow(this)
        }
    }

    private fun requestNotificationPermission() {

        if (Build.VERSION.SDK_INT >=
            Build.VERSION_CODES.TIRAMISU
        ) {

            if (ContextCompat.checkSelfPermission(
                    this,
                    Manifest.permission.POST_NOTIFICATIONS
                ) != PackageManager.PERMISSION_GRANTED
            ) {

                notificationPermissionLauncher.launch(
                    Manifest.permission.POST_NOTIFICATIONS
                )
            }
        }
    }

    private fun loadTasks() {

        allTasks = TaskStorage.getTasks(this).sortedWith(
            compareBy<Task> { it.isCompleted }
                .thenBy {
                    it.dueDate.ifEmpty { "9999-12-31" }
                }
        )

        filterTasks(editSearch.text.toString())

        updateProgress()
    }

    private fun filterTasks(query: String) {

        val searchQuery = query.trim()

        val filteredTasks = allTasks.filter { task ->

            task.title.contains(
                searchQuery,
                ignoreCase = true
            ) ||

                    task.description.contains(
                        searchQuery,
                        ignoreCase = true
                    )
        }

        taskAdapter.updateTasks(filteredTasks)
    }

    private fun updateProgress() {

        val totalTasks = allTasks.size

        val completedTasks = allTasks.count {
            it.isCompleted
        }

        val percentage = if (totalTasks == 0) {
            0
        } else {
            (completedTasks * 100) / totalTasks
        }

        progressBar.progress = percentage

        textProgressPercentage.text = "$percentage%"
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
