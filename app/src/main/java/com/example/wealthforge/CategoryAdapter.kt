package com.example.wealthforge

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.wealthforge.R

class CategoryAdapter(private val items: MutableList<CategoryItem>, private val onDelete: (CategoryItem) -> Unit) :
    RecyclerView.Adapter<CategoryAdapter.ViewHolder>() {

    class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val imageView: ImageView = view.findViewById(R.id.imageView)
        val categoryName: TextView = view.findViewById(R.id.categoryName)
        val categoryType: TextView = view.findViewById(R.id.categoryType)
        val recurringAmount: TextView = view.findViewById(R.id.recurringAmount)
        val deleteButton: ImageView = view.findViewById(R.id.deleteButton)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.template_category, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = items[position]
        holder.imageView.setImageResource(item.iconResId)
        holder.categoryName.text = item.name
        holder.categoryType.text = item.categoryType
        holder.recurringAmount.text = item.recurringAmount

        // Handle delete button click
        holder.deleteButton.setOnClickListener {
            onDelete(item)  // Invoke the delete action provided by the fragment
            // Optionally remove the item from the list and notify the adapter
            val index = items.indexOf(item)
            if (index != -1) {
                items.removeAt(index)
                notifyItemRemoved(index)
            }

        }
    }

    override fun getItemCount(): Int = items.size


}

