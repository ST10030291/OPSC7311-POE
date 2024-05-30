package com.example.time_compassopsc7311_part1

import Points
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.squareup.picasso.Picasso

class GameAdapter (private val pointsList: List<Points>) : RecyclerView.Adapter<GameAdapter.GameViewHolder>() {

    class GameViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        //declare all variables here
        val userPosition: TextView = itemView.findViewById(R.id.displayPosition)
        val userName : TextView = itemView.findViewById(R.id.displayUser)
        val userPoints : TextView = itemView.findViewById(R.id.displayPoints)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): GameViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.user_item, parent, false)
        return GameViewHolder(view)
    }

    override fun onBindViewHolder(holder: GameViewHolder, position: Int) {
        //this is where you can set your values
        val point = pointsList[position]
        //holder.userPosition.setText(pointsList[position].toString())
        holder.userName.setText(point.userName)
        holder.userPoints.setText(point.userPoints)
        //holder.date.setText(task.taskDate)
        //holder.startTime.setText(task.startTime)
        //holder.endTime.setText(task.endTime)
    }



    override fun getItemCount(): Int {
        return pointsList.size
    }

}