
package ca.gbc.comp3074.devtrack_mobile

import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity

class AddTaskActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_add_task)

        val editTaskTitle = findViewById<EditText>(R.id.editTaskTitle)
        val editTaskDescription =
            findViewById<EditText>(R.id.editTaskDescription)

        val btnSaveTask = findViewById<Button>(R.id.btnSaveTask)
        val btnBack = findViewById<Button>(R.id.btnBackToDashboard)

        btnSaveTask.setOnClickListener {
            val title = editTaskTitle.text.toString().trim()
            val description = editTaskDescription.text.toString().trim()

            if (title.isEmpty()) {
                editTaskTitle.error = "Enter a task title"
                return@setOnClickListener
            }

            val task = Task(
                title = title,
                description = description
            )

            TaskStorage.addTask(this, task)

            Toast.makeText(
                this,
                "Task saved successfully!",
                Toast.LENGTH_SHORT
            ).show()

            finish()
        }

        btnBack.setOnClickListener {
            finish()
        }
    }
}
