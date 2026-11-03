
package ca.gbc.comp3074.devtrack_mobile

import android.content.Intent
import android.os.Bundle
import android.widget.Button
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

        val btnAddTask = findViewById<Button>(R.id.btnAddTask)
        val btnOpenTimer = findViewById<Button>(R.id.btnOpenTimer)

        taskAdapter = TaskAdapter { task, isChecked ->
            TaskStorage.updateTask(this, task, isChecked)
            loadTasks()
        }

        recyclerViewTasks.layoutManager = LinearLayoutManager(this)
        recyclerViewTasks.adapter = taskAdapter

        btnAddTask.setOnClickListener {
            startActivity(Intent(this, AddTaskActivity::class.java))
        }

        btnOpenTimer.setOnClickListener {
            startActivity(Intent(this, TimerActivity::class.java))
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
        taskAdapter.updateTasks(tasks)
    }
}
