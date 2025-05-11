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

class CategoriesFragment : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_categories, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        (activity as? MainActivity)?.updateToolbarTitle("Categories")

        // type spinner
        val categorySpinner: Spinner = view.findViewById(R.id.categoryTypeSpinner)

        val categoryTypes = listOf("Expense", "Goal")
        categorySpinner.adapter = ArrayAdapter(requireContext(), android.R.layout.simple_spinner_dropdown_item, categoryTypes)
        categorySpinner.setSelection(0)

        // recycler view for displaying category budgets
        val recyclerView = view.findViewById<RecyclerView>(R.id.recyclerView)

        val categories = listOf(
            CategoryItem("Spotify", "27/03/2025", "R10 000.00", R.drawable.spotify),
            CategoryItem("Grocery", "28/03/2025", "R7 500.00", R.drawable.grocery),
            CategoryItem("Transport", "29/03/2025", "R1 200.00", R.drawable.transport)
        )

       
        recyclerView.layoutManager = LinearLayoutManager(context) // Set the layout manager
        recyclerView.adapter = CategoryAdapter(categories) // Set the adapter to bind data
    }
}
