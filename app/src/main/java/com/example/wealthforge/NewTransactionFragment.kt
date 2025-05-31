package com.example.wealthforge

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.Spinner
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import java.util.*

class NewTransactionFragment : Fragment() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        // No use of param1/param2, so omitted retrieval
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_new_transaction, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Set toolbar title
        (activity as? MainActivity)?.updateToolbarTitle("New Transaction")

        // Category spinner setup
        val categorySpinner: Spinner = view.findViewById(R.id.categorySpinner)
        val categories = listOf("Rent", "Groceries", "Transport", "Entertainment")
        categorySpinner.adapter = ArrayAdapter(requireContext(), android.R.layout.simple_spinner_dropdown_item, categories)
        categorySpinner.setSelection(0)

        // Month and year spinners
        val monthSpinner: Spinner = view.findViewById(R.id.monthSpinner)
        val yearSpinner: Spinner = view.findViewById(R.id.yearSpinner)

        val months = listOf("Jan", "Feb", "Mar", "Apr", "May", "Jun",
            "Jul", "Aug", "Sep", "Oct", "Nov", "Dec")
        val current = Calendar.getInstance()
        val currentMonth = current.get(Calendar.MONTH)
        val currentYear = current.get(Calendar.YEAR)
        val years = (currentYear..(currentYear + 5)).map { it.toString() }

        monthSpinner.adapter = ArrayAdapter(requireContext(), android.R.layout.simple_spinner_dropdown_item, months)
        yearSpinner.adapter = ArrayAdapter(requireContext(), android.R.layout.simple_spinner_dropdown_item, years)

        monthSpinner.setSelection(currentMonth)
        yearSpinner.setSelection(0)

        // RecyclerView for recent transactions
        val recyclerView = view.findViewById<RecyclerView>(R.id.recyclerView)
        val transactionRecords = listOf(
            TransactionRecordItem("Groceries", "29/05/2025 study snack", "R100", R.drawable.ic_groceries),
            TransactionRecordItem("Groceries", "21/05/2025 Weekly groceries", "R535", R.drawable.ic_groceries),
            TransactionRecordItem("Transport", "21/05/2025", "R600.00", R.drawable.ic_transport),
            TransactionRecordItem("Emergency Fund", "15/03/2025", "R500.00", R.drawable.ic_emergency_stop)
        )
        recyclerView.layoutManager = LinearLayoutManager(context)
        recyclerView.adapter = TransactionRecordAdapter(transactionRecords)
    }

    companion object {
        @JvmStatic
        fun newInstance(param1: String, param2: String) =
            NewTransactionFragment().apply {
                arguments = Bundle().apply {
                    putString("param1", param1)
                    putString("param2", param2)
                }
            }
    }
}
