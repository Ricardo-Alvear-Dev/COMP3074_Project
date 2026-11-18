
package ca.gbc.comp3074.devtrack_mobile

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.CheckBox
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

class TaskAdapter(
    private val onTaskChecked: (Task, Boolean) -> Unit,
    private val onTaskEdit: (Task) -> Unit,
    private val onTaskDelete: (Task) -> Unit
) : RecyclerView.Adapter<TaskAdapter.TaskViewHolder>() {

    private val tasks = mutableListOf<Task>()

    class TaskViewHolder(itemView: View) :
        RecyclerView.ViewHolder(itemView) {

        val checkboxTask: CheckBox =
            itemView.findViewById(R.id.checkboxTask)

        val textTaskTitle: TextView =
            itemView.findViewById(R.id.textTaskTitle)

        val textTaskDetails: TextView =
            itemView.findViewById(R.id.textTaskDetails)

        val textTaskDueDate: TextView =
            itemView.findViewById(R.id.textTaskDueDate)

        val btnEditTask: Button =
            itemView.findViewById(R.id.btnEditTask)

        val btnDeleteTask: Button =
            itemView.findViewById(R.id.btnDeleteTask)
    }

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): TaskViewHolder {

        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_task, parent, false)

        return TaskViewHolder(view)
    }

    override fun onBindViewHolder(
        holder: TaskViewHolder,
        position: Int
    ) {
        val task = tasks[position]

        holder.textTaskTitle.text = task.title
        holder.textTaskDetails.text = task.description

        holder.textTaskDueDate.text =
            if (task.dueDate.isEmpty()) {
                "Due: No deadline"
            } else {
                "Due: ${task.dueDate}"
            }

        holder.checkboxTask.setOnCheckedChangeListener(null)
        holder.checkboxTask.isChecked = task.isCompleted

        holder.checkboxTask.setOnCheckedChangeListener { _, isChecked ->
            onTaskChecked(task, isChecked)
        }

        holder.btnEditTask.setOnClickListener {
            onTaskEdit(task)
        }

        holder.btnDeleteTask.setOnClickListener {
            onTaskDelete(task)
        }
    }

    override fun getItemCount(): Int = tasks.size

    fun updateTasks(newTasks: List<Task>) {
        tasks.clear()
        tasks.addAll(newTasks)
        notifyDataSetChanged()
    }
}
