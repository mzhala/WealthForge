package com.example.wealthforge

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.Spinner
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import java.util.*

class BudgetFragment : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_budget, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Update toolbar title
        (activity as? MainActivity)?.updateToolbarTitle("Budget")

        // Month and Year Spinners
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

        // Static RecyclerView Data
        val recyclerView = view.findViewById<RecyclerView>(R.id.recyclerView)
        recyclerView.layoutManager = LinearLayoutManager(context)

        val sampleData = listOf(
            CategoryBudgetItem(1, "Spotify", "R500", R.drawable.spotify),
            CategoryBudgetItem(2, "Grocery", "R850", R.drawable.grocery),
            CategoryBudgetItem(3, "Transport", "R8400", R.drawable.transport)
        )

        recyclerView.adapter = CategoryBudgetAdapter(sampleData.toMutableList()) { item ->
            // Just show a toast or do nothing for now
        }
    }
}
