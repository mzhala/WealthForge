package com.example.wealthforge

import UserViewModel
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.Spinner
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.wealthforge.data.AppDatabase
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.util.*

class TransactionHistoryFragment : Fragment() {

    private lateinit var db: AppDatabase
    private lateinit var recyclerView: RecyclerView
    private lateinit var userViewModel: UserViewModel

    private val months = listOf(
        "Jan", "Feb", "Mar", "Apr", "May", "Jun",
        "Jul", "Aug", "Sep", "Oct", "Nov", "Dec"
    )

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_transaction_history, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        (activity as? MainActivity)?.updateToolbarTitle("Transaction History")

        db = AppDatabase.getDatabase(requireContext())
        userViewModel = (activity as MainActivity).userViewModel
        recyclerView = view.findViewById(R.id.recyclerView)
        recyclerView.layoutManager = LinearLayoutManager(context)

        val startMonthSpinner: Spinner = view.findViewById(R.id.startMonthSpinner)
        val startYearSpinner: Spinner = view.findViewById(R.id.startYearSpinner)
        val endMonthSpinner: Spinner = view.findViewById(R.id.endMonthSpinner)
        val endYearSpinner: Spinner = view.findViewById(R.id.endYearSpinner)

        val current = Calendar.getInstance()
        val currentMonth = current.get(Calendar.MONTH)
        val currentYear = current.get(Calendar.YEAR)
        val years = (currentYear..(currentYear + 5)).map { it.toString() }

        startMonthSpinner.adapter = ArrayAdapter(requireContext(), android.R.layout.simple_spinner_dropdown_item, months)
        endMonthSpinner.adapter = ArrayAdapter(requireContext(), android.R.layout.simple_spinner_dropdown_item, months)
        startYearSpinner.adapter = ArrayAdapter(requireContext(), android.R.layout.simple_spinner_dropdown_item, years)
        endYearSpinner.adapter = ArrayAdapter(requireContext(), android.R.layout.simple_spinner_dropdown_item, years)

        startMonthSpinner.setSelection(0)
        endMonthSpinner.setSelection(currentMonth)
        startYearSpinner.setSelection(0)
        endYearSpinner.setSelection(0)

        // Load transactions when spinners change
        val reload: () -> Unit = {
            val userId = userViewModel.userId.value?.toIntOrNull()

            val startMonthIndex = startMonthSpinner.selectedItemPosition
            val endMonthIndex = endMonthSpinner.selectedItemPosition
            val startYear = startYearSpinner.selectedItem.toString().toInt()
            val endYear = endYearSpinner.selectedItem.toString().toInt()
            val totalTextView = view.findViewById<TextView>(R.id.transactionAmountTotal)


            if (userId != null) {
                loadTransactions(userId, startMonthIndex, startYear, endMonthIndex, endYear, recyclerView, totalTextView)
            }
        }

        listOf(startMonthSpinner, startYearSpinner, endMonthSpinner, endYearSpinner).forEach {
            it.setOnItemSelectedListener { reload() }
        }

        reload()
    }

    private fun Spinner.setOnItemSelectedListener(onChange: () -> Unit) {
        this.setOnItemSelectedListener(object : android.widget.AdapterView.OnItemSelectedListener {
            override fun onItemSelected(
                parent: android.widget.AdapterView<*>?,
                view: View?,
                position: Int,
                id: Long
            ) {
                onChange()
            }

            override fun onNothingSelected(parent: android.widget.AdapterView<*>?) {}
        })
    }

    private fun loadTransactions(userId: Int,
                                       startMonthIndex: Int,
                                       startYear: Int,
                                       endMonthIndex: Int,
                                       endYear: Int,
                                        recyclerView: RecyclerView,
                                        totalTextView: TextView
    ) {
        lifecycleScope.launch {
            val transactions = db.transactionsDao().getTransactionsBetween(userId, startMonthIndex, startYear, endMonthIndex, endYear)
            val total = db.transactionsDao().getTransactionsBetweenTotal(userId, startMonthIndex, startYear, endMonthIndex, endYear)

            val items = transactions.map {
                TransactionRecordItem(
                    id = it.id,
                    name = it.categoryName,
                    date = "${it.day} ${it.month} ${it.year} ${it.description}",
                    amount = "R${"%.2f".format(it.amount)}",
                    iconResId = it.iconResId ?: R.drawable.ic_categories
                )
            }.toMutableList()

            withContext(Dispatchers.Main) {
                recyclerView.adapter = TransactionRecordAdapter(items) { item ->
                    lifecycleScope.launch {
                        db.transactionsDao().deleteTransactionById(item.id)
                        loadTransactions(userId, startMonthIndex, startYear, endMonthIndex, endYear, recyclerView, totalTextView) // Reload the list
                    }
                }
            }

            val formattedTotal = if (total != null) {
                "R" + "%,d".format(total.toInt()) // Assumes Double or Int
            } else {
                "R0"
            }

            totalTextView.text = formattedTotal
        }
    }
}
