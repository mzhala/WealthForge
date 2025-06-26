package com.example.wealthforge

import UserViewModel
import com.example.wealthforge.data.CategoryTotal
import com.example.wealthforge.data.MonthlyTotal
import android.os.Build
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.Spinner
import android.widget.TextView
import androidx.annotation.RequiresApi
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import com.example.wealthforge.data.AppDatabase
import com.github.mikephil.charting.charts.BarChart
import com.github.mikephil.charting.data.BarData
import com.github.mikephil.charting.data.BarDataSet
import com.github.mikephil.charting.data.BarEntry
import com.github.mikephil.charting.formatter.IndexAxisValueFormatter
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.util.*

class SpendingChartFragment : Fragment() {

    private lateinit var db: AppDatabase
    private lateinit var userViewModel: UserViewModel

    private val months = listOf(
        "Jan", "Feb", "Mar", "Apr", "May", "Jun",
        "Jul", "Aug", "Sep", "Oct", "Nov", "Dec"
    )

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_spending_chart, container, false)
    }

    @RequiresApi(Build.VERSION_CODES.M)
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        (activity as? MainActivity)?.updateToolbarTitle("Spending Trends")

        db = AppDatabase.getDatabase(requireContext())
        userViewModel = (activity as MainActivity).userViewModel

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

        val reload: () -> Unit = {
            val userId = userViewModel.userId.value?.toIntOrNull()

            val startMonthIndex = startMonthSpinner.selectedItemPosition
            val endMonthIndex = endMonthSpinner.selectedItemPosition
            val startYear = startYearSpinner.selectedItem.toString().toInt()
            val endYear = endYearSpinner.selectedItem.toString().toInt()
            val summary = view.findViewById<TextView>(R.id.summary)
            if (userId != null) {
                loadTransactions(userId, startMonthIndex, startYear, endMonthIndex, endYear, summary)
            }
        }

        listOf(startMonthSpinner, startYearSpinner, endMonthSpinner, endYearSpinner).forEach {
            it.setOnItemSelectedListener { reload() }
        }

        reload()
    }

    private fun Spinner.setOnItemSelectedListener(onChange: () -> Unit) {
        this.setOnItemSelectedListener(object : android.widget.AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: android.widget.AdapterView<*>?, view: View?, position: Int, id: Long) {
                onChange()
            }

            override fun onNothingSelected(parent: android.widget.AdapterView<*>?) {}
        })
    }

    @RequiresApi(Build.VERSION_CODES.M)
    private fun loadTransactions(
        userId: Int,
        startMonthIndex: Int,
        startYear: Int,
        endMonthIndex: Int,
        endYear: Int,
        summary: TextView
    ) {
        lifecycleScope.launch {
            val categoryTotals = db.transactionsDao()
                .getCategorySpendingWithBudgetInRange(userId, startMonthIndex, startYear, endMonthIndex, endYear)

            withContext(Dispatchers.Main) {
                val barChart = view?.findViewById<BarChart>(R.id.categoriesBarChart) ?: return@withContext

                barChart.description.isEnabled = false
                barChart.setFitBars(true)
                barChart.setDrawValueAboveBar(true)
                barChart.axisRight.isEnabled = false

                val entriesSpent = ArrayList<BarEntry>()
                val entriesBudget = ArrayList<BarEntry>()
                val labels = ArrayList<String>()

                categoryTotals.forEachIndexed { index, it ->
                    entriesSpent.add(BarEntry(index.toFloat(), it.total.toFloat()))
                    entriesBudget.add(BarEntry(index.toFloat(), it.budget.toFloat()))
                    labels.add(it.category_name)
                }

                val spentSet = BarDataSet(entriesSpent, "Spent")
                spentSet.color = resources.getColor(R.color.teal_200, null)

                val budgetSet = BarDataSet(entriesBudget, "Budget")
                budgetSet.color = resources.getColor(R.color.green, null)

                val groupSpace = 0.2f
                val barSpace = 0.02f
                val barWidth = 0.4f

                val groupCount = labels.size

                val data = BarData(spentSet, budgetSet)
                data.barWidth = barWidth

                barChart.data = data

                val xAxis = barChart.xAxis
                xAxis.valueFormatter = IndexAxisValueFormatter(labels)
                xAxis.granularity = 1f
                xAxis.position = com.github.mikephil.charting.components.XAxis.XAxisPosition.BOTTOM
                xAxis.setCenterAxisLabels(true)

// Set axis min and max for grouped bars so they fit well
                xAxis.axisMinimum = 0f
                xAxis.axisMaximum = 0f + data.getGroupWidth(groupSpace, barSpace) * groupCount

// Group the bars starting at x=0f with groupSpace and barSpace
                barChart.groupBars(0f, groupSpace, barSpace)

                barChart.setFitBars(true)
                barChart.invalidate()

                // Load monthly totals after categories
                loadMonthlyTotals(userId, startMonthIndex, startYear, endMonthIndex, endYear)

                val budgetTotalRaw = db.categoryBudgetDao().getCategoryBudgetTotalInRange(userId, startMonthIndex, startYear, endMonthIndex, endYear)
                val spentTotalRaw = db.transactionsDao().getTransactionsBetweenTotal(userId, startMonthIndex, startYear, endMonthIndex, endYear)

                val budgetTotal = budgetTotalRaw ?: 0.0
                val spentTotal = spentTotalRaw ?: 0.0

                val summaryText = StringBuilder()

                if (budgetTotal != null && spentTotal != null) {
                    if (spentTotal < budgetTotal) {
                        val diff = budgetTotal - spentTotal
                        if (spentTotal < budgetTotal) {
                            val diff = budgetTotal - spentTotal
                            summaryText.append("\uD83D\uDE0A You set category budgets totaling R${"%.2f".format(budgetTotal)} and spent R${"%.2f".format(spentTotal)} — great job saving R${"%.2f".format(diff)} across your categories!")
                        } else if (spentTotal > budgetTotal) {
                            val diff = spentTotal - budgetTotal
                            summaryText.append("\uD83D\uDE25 Your spending exceeded the category budgets by R${"%.2f".format(diff)} (Spent: R${"%.2f".format(spentTotal)}, Budgeted: R${"%.2f".format(budgetTotal)}). Consider reviewing your categories to stay on track.")
                        } else {
                            summaryText.append("\uD83D\uDE0E You perfectly matched your category budgets with actual spending at R${"%.2f".format(budgetTotal)} — well done!")
                        }

                    }

                    // Add overspending category
                    val overspendingCategory = categoryTotals
                        .filter { it.budget > 0 && it.total > it.budget }
                        .maxByOrNull { it.total - it.budget }

                    if (overspendingCategory != null) {
                        val percentOver = ((overspendingCategory.total - overspendingCategory.budget) / overspendingCategory.budget) * 100
                        summaryText.append("\n⚠️ You overspent most in '${overspendingCategory.category_name}' - ${"%.1f".format(percentOver)}% over budget.")
                    }

                    summary.text = summaryText.toString()
                }

            }
        }
    }

    @RequiresApi(Build.VERSION_CODES.M)
    private fun loadMonthlyTotals(
        userId: Int,
        startMonthIndex: Int,
        startYear: Int,
        endMonthIndex: Int,
        endYear: Int
    ) {
        lifecycleScope.launch(Dispatchers.IO) {
            val monthlyTotals = db.budgetDao()
                .getMonthlyTotals(userId, startMonthIndex, startYear, endMonthIndex, endYear)

            withContext(Dispatchers.Main) {
                val barChart = view?.findViewById<BarChart>(R.id.monthlyBarChart) ?: return@withContext

                barChart.description.isEnabled = false
                barChart.setFitBars(true)
                barChart.setDrawValueAboveBar(true)
                barChart.axisRight.isEnabled = false

                val entriesSpent = ArrayList<BarEntry>()
                val entriesBudget = ArrayList<BarEntry>()
                val labels = ArrayList<String>()

                monthlyTotals.forEachIndexed { index, it ->
                    entriesSpent.add(BarEntry(index.toFloat(), it.spent.toFloat()))
                    entriesBudget.add(BarEntry(index.toFloat(), it.budget.toFloat()))
                    labels.add("${months[it.month_num]} ${it.year}")
                }

                val spentSet = BarDataSet(entriesSpent, "Spent")
                spentSet.color = resources.getColor(R.color.teal_700, null)

                val budgetSet = BarDataSet(entriesBudget, "Budget")
                budgetSet.color = resources.getColor(R.color.green, null)

                val groupSpace = 0.2f
                val barSpace = 0.02f
                val barWidth = 0.4f

                val groupCount = labels.size

                val data = BarData(spentSet, budgetSet)
                data.barWidth = barWidth

                barChart.data = data

                val xAxis = barChart.xAxis
                xAxis.valueFormatter = IndexAxisValueFormatter(labels)
                xAxis.granularity = 1f
                xAxis.position = com.github.mikephil.charting.components.XAxis.XAxisPosition.BOTTOM
                xAxis.setCenterAxisLabels(true)

// Set axis min and max for grouped bars so they fit well
                xAxis.axisMinimum = 0f
                xAxis.axisMaximum = 0f + data.getGroupWidth(groupSpace, barSpace) * groupCount

// Group the bars starting at x=0f with groupSpace and barSpace
                barChart.groupBars(0f, groupSpace, barSpace)

                barChart.setFitBars(true)
                barChart.invalidate()

            }
        }
    }


}
