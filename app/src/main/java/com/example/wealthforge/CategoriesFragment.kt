package com.example.wealthforge

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
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

        // Update toolbar title
        (activity as? MainActivity)?.updateToolbarTitle("Categories")

        // Set up static data
        val staticCategories = listOf(
            CategoryItem(1, "Groceries", "Expense", "R500", R.drawable.ic_categories),
            CategoryItem(2, "Fitness Goal", "Goal", "R1000", R.drawable.ic_categories),
            CategoryItem(3, "Entertainment", "Expense", "R300", R.drawable.ic_categories)
        )

        // Setup RecyclerView
        val recyclerView = view.findViewById<RecyclerView>(R.id.recyclerView)
        recyclerView.layoutManager = LinearLayoutManager(context)
        recyclerView.adapter = CategoryAdapter(staticCategories.toMutableList()) {
            // No delete logic for static demo
        }
    }
}
