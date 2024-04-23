package com.example.time_compassopsc7311_part1

import Category
import android.graphics.Color
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import androidx.core.graphics.toColorInt
import androidx.recyclerview.widget.RecyclerView

class CategoryAdapter(private val categoryList: List<Category>) : RecyclerView.Adapter<CategoryAdapter.CategoryViewHolder>() {

    class CategoryViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        // Define the views inside the layout
        val categoryName: Button = itemView.findViewById(R.id.categoryBtn)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CategoryViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.category_item_layout, parent, false)
        return CategoryViewHolder(view)
    }

    override fun onBindViewHolder(holder: CategoryViewHolder, position: Int) {
        val category = categoryList[position]
        holder.categoryName.setText(category.categoryName)

        holder.categoryName.setBackgroundColor(colorChange(category.color).toColorInt())
        //val categoryColor = colorOptn.selectedItem.toString()
       // val categoryColor = itemView.findViewById(R.id.colorOption)
       // val categoryColorSelected = categoryColor.
        //holder.categoryName.setBackgroundColor(category.color.toColorInt())
        //holder.categoryColor.text = category.color
    }

    override fun getItemCount(): Int {
        return categoryList.size
    }
    private fun colorChange(x:String): String {
         var colourName = ""
        if (x.equals("Dark Blue")){
            colourName = "#393E46"
        }else if (x.equals("Light Blue")){
            colourName ="#00ADB5"
        }else if (x.equals("Grey")){
            colourName ="#EEEEEE"
        }else if (x.equals("Orange")){
            colourName ="#F8B400"
        }else if (x.equals("Purple")){
            colourName ="#7209B7"
        }else if (x.equals("Black")){
            colourName ="#FF000000"
        }else if (x.equals("White")){
            colourName ="#FFFFFFFF"
        }else if(x.equals("Select Color")){
            colourName ="#D9D9D9"
        }
        return colourName
    }
}