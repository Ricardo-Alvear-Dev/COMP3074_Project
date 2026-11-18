
package ca.gbc.comp3074.devtrack_mobile

import android.app.DatePickerDialog
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import java.time.LocalDate

class AddTaskActivity : AppCompatActivity() {

    private var selectedDate = ""
    private var existingTask: Task? = null

    private lateinit var textSelectedDate: TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_add_task)

        val textPageTitle =
            findViewById<TextView>(R.id.textPageTitle)

        val editTaskTitle =
            findViewById<EditText>(R.id.editTaskTitle)

        val editTaskDescription =
            findViewById<EditText>(R.id.editTaskDescription)

        textSelectedDate = findViewById(R.id.textSelectedDate)

        val btnSelectDate =
            findViewById<Button>(R.id.btnSelectDate)

        val btnClearDate =
            findViewById<Button>(R.id.btnClearDate)

        val btnSaveTask =
            findViewById<Button>(R.id.btnSaveTask)

        val btnBack =
            findViewById<Button>(R.id.btnBackToDashboard)

        val taskId = intent.getStringExtra("taskId")

        // Load existing task when editing
        if (taskId != null) {
            existingTask = TaskStorage.getTask(this, taskId)

            if (existingTask == null) {
                Toast.makeText(
                    this,
                    "Task not found",
                    Toast.LENGTH_SHORT
                ).show()

                finish()
                return
            }

            existingTask?.let { task ->
                textPageTitle.text = "Edit Task"
                btnSaveTask.text = "UPDATE TASK"

                editTaskTitle.setText(task.title)
                editTaskDescription.setText(task.description)

                selectedDate = task.dueDate
                updateDateDisplay()
            }
        }

        btnSelectDate.setOnClickListener {
            showDatePicker()
        }

        btnClearDate.setOnClickListener {
            selectedDate = ""
            updateDateDisplay()
        }

        btnSaveTask.setOnClickListener {

            val title = editTaskTitle.text.toString().trim()
            val description =
                editTaskDescription.text.toString().trim()

            if (title.isEmpty()) {
                editTaskTitle.error = "Enter a task title"
                return@setOnClickListener
            }

            val task = existingTask

            if (task == null) {

                // Create new task
                val newTask = Task(
                    title = title,
                    description = description,
                    dueDate = selectedDate
                )

                TaskStorage.addTask(this, newTask)

                Toast.makeText(
                    this,
                    "Task saved successfully!",
                    Toast.LENGTH_SHORT
                ).show()

            } else {

                // Update existing task
                val updatedTask = task.copy(
                    title = title,
                    description = description,
                    dueDate = selectedDate
                )

                TaskStorage.updateTask(this, updatedTask)

                Toast.makeText(
                    this,
                    "Task updated successfully!",
                    Toast.LENGTH_SHORT
                ).show()
            }

            finish()
        }

        btnBack.setOnClickListener {
            finish()
        }
    }

    private fun showDatePicker() {

        val date = if (selectedDate.isNotEmpty()) {
            LocalDate.parse(selectedDate)
        } else {
            LocalDate.now()
        }

        val datePicker = DatePickerDialog(
            this,
            { _, year, month, day ->

                selectedDate = LocalDate.of(
                    year,
                    month + 1,
                    day
                ).toString()

                updateDateDisplay()
            },
            date.year,
            date.monthValue - 1,
            date.dayOfMonth
        )

        datePicker.show()
    }

    private fun updateDateDisplay() {
        textSelectedDate.text = if (selectedDate.isEmpty()) {
            "No due date selected"
        } else {
            selectedDate
        }
    }
}
