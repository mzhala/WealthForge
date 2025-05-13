package com.example.wealthforge

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.wealthforge.R

class CategoryBudgetAdapter(private val items: MutableList<CategoryBudgetItem>, private val onDelete: (CategoryBudgetItem) -> Unit) :
    RecyclerView.Adapter<CategoryBudgetAdapter.ViewHolder>() {

    class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val imageView: ImageView = view.findViewById(R.id.imageView)
        val categoryName: TextView = view.findViewById(R.id.categoryName)
        val transactionAmount: TextView = view.findViewById(R.id.transactionAmount)
        val deleteButton: ImageView = view.findViewById(R.id.deleteButton)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.template_category_budget, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = items[position]
        holder.imageView.setImageResource(item.iconResId)
        holder.categoryName.text = item.name
        holder.transactionAmount.text = item.amount


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
