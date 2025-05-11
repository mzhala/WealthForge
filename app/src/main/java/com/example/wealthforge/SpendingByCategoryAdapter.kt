package com.example.wealthforge

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.wealthforge.R

class SpendingByCategoryAdapter(private val items: List<SpendingByCategoryItem>) :
    RecyclerView.Adapter<SpendingByCategoryAdapter.ViewHolder>() {

    class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val imageView: ImageView = view.findViewById(R.id.imageView)
        val categoryName: TextView = view.findViewById(R.id.categoryName)
        val categoryBudgetAmount: TextView = view.findViewById(R.id.categoryBudgetAmount)
        val categorySpentAmount: TextView = view.findViewById(R.id.categorySpentAmount)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.template_spending_by_category, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = items[position]
        holder.imageView.setImageResource(item.iconResId)
        holder.categoryName.text = item.name
        holder.categoryBudgetAmount.text = item.budgetAmount
        holder.categorySpentAmount.text = item.spentAmount
    }

    override fun getItemCount(): Int = items.size
}
