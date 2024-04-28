package com.example.time_compassopsc7311_part1

import Task
import TaskList
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

class TaskAdapter(private val taskList: List<Task>) : RecyclerView.Adapter<TaskAdapter.TaskViewHolder>() {


    var onItemClick : ((Task) -> Unit)? = null
    class TaskViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val taskName: TextView = itemView.findViewById(R.id.displayTaskName)
        val description : TextView = itemView.findViewById(R.id.displayDescription)
        val date : TextView = itemView.findViewById(R.id.displayDate)
        val startTime : TextView = itemView.findViewById(R.id.displayStartTime)
        val endTime : TextView = itemView.findViewById(R.id.displayEndTime)
        val taskIcon : ImageView = itemView.findViewById(R.id.displaytaskImage)
       // val categoryOption : Spinner = itemView.findViewById(R.id.categoryOption)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TaskViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.task_item_layout, parent, false)
        return TaskViewHolder(view)
    }

    override fun onBindViewHolder(holder: TaskViewHolder, position: Int) {
        val task = taskList[position]
        holder.taskName.setText(task.taskName)
        holder.description.setText(task.description)
        holder.date.setText(task.taskDate)
        holder.startTime.setText(task.startTime)
        holder.endTime.setText(task.endTime)
        holder.taskIcon.setImageURI(task.picture)

        holder.itemView.setOnClickListener{
            onItemClick?.invoke(task)
        }
        //holder.categoryName.setText(category.categoryName)

        //holder.categoryName.setBackgroundColor(colorChange(category.color).toColorInt())
        //val categoryColor = colorOptn.selectedItem.toString()
        // val categoryColor = itemView.findViewById(R.id.colorOption)
        // val categoryColorSelected = categoryColor.
        //holder.categoryName.setBackgroundColor(category.color.toColorInt())
        //holder.categoryColor.text = category.color
    }

    override fun getItemCount(): Int {
        return taskList.size
    }

}
