package com.example.time_compassopsc7311_part1

import Task
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

class FilterTasksAdapter(private var filteredTaskList: List<Task>) : RecyclerView.Adapter<FilterTasksAdapter.TaskViewHolder>() {

    inner class TaskViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val taskName: TextView = itemView.findViewById(R.id.displayTaskName)
        val description : TextView = itemView.findViewById(R.id.displayDescription)
        val date : TextView = itemView.findViewById(R.id.displayDate)
        val startTime : TextView = itemView.findViewById(R.id.displayStartTime)
        val endTime : TextView = itemView.findViewById(R.id.displayEndTime)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TaskViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.task_item_layout, parent, false)
        return TaskViewHolder(view)
    }

    override fun onBindViewHolder(holder: TaskViewHolder, position: Int) {
        val task = filteredTaskList[position]
        holder.taskName.text = task.taskName
        holder.description.text = task.description
        holder.date.text = task.taskDate
        holder.startTime.text = task.startTime
        holder.endTime.text = task.endTime
        // Set your task icon here if needed
    }

    override fun getItemCount(): Int {
        return filteredTaskList.size
    }

    // Update the dataset with new list of filtered tasks
    fun updateFilteredTasks(newFilteredTaskList: List<Task>) {
        filteredTaskList = newFilteredTaskList
        notifyDataSetChanged()
    }
}