package com.example.wealthforge

import com.example.wealthforge.data.CategoryTotal

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

class SpendingByCategoryFragment : Fragment() {

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
        return inflater.inflate(R.layout.fragment_spending_by_category, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        (activity as? MainActivity)?.updateToolbarTitle("Spending by Category")

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
            val totalBudgetTextView = view.findViewById<TextView>(R.id.categoryBudgetAmountTotal)
            val totalSpentTextView = view.findViewById<TextView>(R.id.categorySpentAmountTotal)

            if (userId != null) {
                loadTransactions(userId, startMonthIndex, startYear, endMonthIndex, endYear, recyclerView, totalBudgetTextView, totalSpentTextView)
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

    private fun loadTransactions(
        userId: Int,
        startMonthIndex: Int,
        startYear: Int,
        endMonthIndex: Int,
        endYear: Int,
        recyclerView: RecyclerView,
        totalBudgetTextView: TextView,
        totalSpentTextView: TextView

    ) {
        lifecycleScope.launch {
            val categoryTotals = db.transactionsDao().getCategorySpendingWithBudgetInRange(userId, startMonthIndex, startYear, endMonthIndex, endYear)
            val budgetTotal = db.categoryBudgetDao().getCategoryBudgetTotalInRange(userId, startMonthIndex, startYear, endMonthIndex, endYear)
            val spentTotal = db.transactionsDao().getTransactionsBetweenTotal(userId, startMonthIndex, startYear, endMonthIndex, endYear)

            val monthMap = mapOf(
                "Jan" to 0, "Feb" to 1, "Mar" to 2, "Apr" to 3,
                "May" to 4, "Jun" to 5, "Jul" to 6, "Aug" to 7,
                "Sep" to 8, "Oct" to 9, "Nov" to 10, "Dec" to 11
            )

            val items = categoryTotals.map {
                SpendingByCategoryItem(
                    name = it.category_name,
                    budgetAmount = "R" + "%,d".format((it.budget ?: 0.0).toInt()).replace(',', ' '),
                    spentAmount = "R" + "%,d".format((it.total ?: 0.0).toInt()).replace(',', ' '),
                    iconResId = R.drawable.ic_categories
                )
            }

            withContext(Dispatchers.Main) {
                recyclerView.adapter = SpendingByCategoryAdapter(items)

                val formattedBudget = if (budgetTotal != null) {
                    "R" + "%,d".format(budgetTotal.toInt()) // Assumes Double or Int
                } else {
                    "R0"
                }

                val formattedSpent = if (spentTotal != null) {
                    "R" + "%,d".format(spentTotal.toInt()) // Assumes Double or Int
                } else {
                    "R0"
                }

                totalBudgetTextView.text = formattedBudget
                totalSpentTextView.text = formattedSpent
            }

        }
    }

}
