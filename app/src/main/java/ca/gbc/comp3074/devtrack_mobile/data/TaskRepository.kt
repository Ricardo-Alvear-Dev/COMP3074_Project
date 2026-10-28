package ca.gbc.comp3074.devtrack_mobile.data

class TaskRepository(private val taskDao: TaskDao) {

    suspend fun insert(task: Task) {
        taskDao.insertTask(task)
    }

    suspend fun delete(taskId: Int) {
        taskDao.deleteTaskById(taskId)
    }
}