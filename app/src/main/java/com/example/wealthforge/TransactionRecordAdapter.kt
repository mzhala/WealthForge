package com.example.wealthforge

import android.app.AlertDialog
import android.content.Context
import android.graphics.ImageDecoder
import android.net.Uri
import android.os.Build
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.annotation.RequiresApi
import androidx.recyclerview.widget.RecyclerView

class TransactionRecordAdapter(
    private val context: Context,
    private val items: MutableList<TransactionRecordItem>,
    private val onDeleteClick: (TransactionRecordItem) -> Unit
) : RecyclerView.Adapter<TransactionRecordAdapter.ViewHolder>() {


    class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val imageView: ImageView = view.findViewById(R.id.imageView)
        val categoryName: TextView = view.findViewById(R.id.categoryName)
        val transactionDateAndDescription: TextView = view.findViewById(R.id.transactionDateAndDescription)
        val transactionAmount: TextView = view.findViewById(R.id.transactionAmount)
        val deleteButton: ImageView = view.findViewById(R.id.deleteButton)
        val viewTransactionButton: ImageView = view.findViewById(R.id.viewTransactionButton)
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
        holder.transactionDateAndDescription.text = item.subtext
        holder.transactionAmount.text = item.amount

        holder.deleteButton.setOnClickListener {
            onDeleteClick(item)
        }
        holder.viewTransactionButton.setOnClickListener {
            showTransactionDetailsDialog(context, item)
        }
    }

    override fun getItemCount(): Int = items.size

    @RequiresApi(Build.VERSION_CODES.P)
    private fun showTransactionDetailsDialog(context: Context, item: TransactionRecordItem) {
        val dialogView = LayoutInflater.from(context).inflate(R.layout.dialog_transaction_detail, null)

        val category = dialogView.findViewById<TextView>(R.id.detailCategory)
        val amount = dialogView.findViewById<TextView>(R.id.detailAmount)
        val date = dialogView.findViewById<TextView>(R.id.detailDate)
        val description = dialogView.findViewById<TextView>(R.id.detailDescription)
        val receiptImage = dialogView.findViewById<ImageView>(R.id.detailReceipt)

        category.text = "Category: ${item.name}"
        amount.text = "Amount: ${item.amount}"
        date.text = "Date: ${item.date}"
        description.text = "Description: ${item.description ?: "N/A"}"

        if (!item.receiptUri.isNullOrBlank()) {
            try {
                val uri = Uri.parse(item.receiptUri)
                val source = ImageDecoder.createSource(context.contentResolver, uri)
                val bitmap = ImageDecoder.decodeBitmap(source)
                receiptImage.setImageBitmap(bitmap)
            } catch (e: Exception) {
                e.printStackTrace()
                receiptImage.setImageResource(R.drawable.ic_no_receipt)
            }
        } else {
            // Fallback image if receiptUri is null or blank
            receiptImage.setImageResource(R.drawable.ic_no_receipt)
        }


        AlertDialog.Builder(context)
            .setTitle("Transaction Details")
            .setView(dialogView)
            .setPositiveButton("Close", null)
            .show()
    }

}
