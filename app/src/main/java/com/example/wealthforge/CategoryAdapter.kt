package com.example.wealthforge

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.Space
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

class CategoryAdapter(
    private val items: MutableList<CategoryItem>,
    private val onDelete: (CategoryItem) -> Unit
) : RecyclerView.Adapter<CategoryAdapter.ViewHolder>() {

    class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val imageView: ImageView = view.findViewById(R.id.imageView)
        val categoryName: TextView = view.findViewById(R.id.categoryName)
        val categoryType: TextView = view.findViewById(R.id.categoryType)
        val recurringAmount: TextView = view.findViewById(R.id.recurringAmount)
        val deleteButton: ImageView = view.findViewById(R.id.deleteButton)
        val recurringIcon: ImageView = view.findViewById(R.id.recurring) // Make sure the ID matches your XML
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

        // Show/hide the recurring icon
        if (item.recurring) {
            holder.recurringIcon.visibility = View.VISIBLE
            holder.recurringAmount.visibility = View.VISIBLE
        } else {
            holder.recurringIcon.visibility = View.GONE
            holder.recurringAmount.visibility = View.GONE
        }

        holder.deleteButton.setOnClickListener {
            onDelete(item)
            val index = items.indexOf(item)
            if (index != -1) {
                items.removeAt(index)
                notifyItemRemoved(index)
            }
        }
    }

    override fun getItemCount(): Int = items.size
}
