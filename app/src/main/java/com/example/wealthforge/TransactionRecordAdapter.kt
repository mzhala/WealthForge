package com.example.wealthforge

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.wealthforge.R

class TransactionRecordAdapter(private val items: List<TransactionRecordItem>) :
    RecyclerView.Adapter<TransactionRecordAdapter.ViewHolder>() {


    class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val imageView: ImageView = view.findViewById(R.id.imageView)
        val categoryName: TextView = view.findViewById(R.id.categoryName)
        val transactionDateAndDescription: TextView = view.findViewById(R.id.transactionDateAndDescription)
        val transactionAmount: TextView = view.findViewById(R.id.transactionAmount)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.template_transaction_v1, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = items[position]
        holder.imageView.setImageResource(item.iconResId)
        holder.categoryName.text = item.name
        holder.transactionDateAndDescription.text = item.date
        holder.transactionAmount.text = item.amount
    }

    override fun getItemCount(): Int = items.size
}
