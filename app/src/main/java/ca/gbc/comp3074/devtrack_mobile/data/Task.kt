package ca.gbc.comp3074.devtrack_mobile.data

data class Task(
    val taskId: Int,
    val title: String,
    val description: String,
    val courseCode: String,
    val dueDate: Long,
    val priority: Int,
    var isCompleted: Boolean
)