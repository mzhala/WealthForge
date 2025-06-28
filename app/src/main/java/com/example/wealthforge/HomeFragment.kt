package com.example.wealthforge

import UserViewModel
import android.app.AlertDialog
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.wealthforge.data.AppDatabase
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.util.Calendar
import java.text.DecimalFormat
import java.text.DecimalFormatSymbols
import java.util.Locale

class HomeFragment : Fragment() {

    private lateinit var db: AppDatabase
    private lateinit var recyclerView: RecyclerView
    private lateinit var userViewModel: UserViewModel
    private var dailyTipShown = false


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_home, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        view.post {
            (activity as? MainActivity)?.updateToolbarTitle("Welcome back")
        }

        db = AppDatabase.getDatabase(requireContext())
        userViewModel = (activity as MainActivity).userViewModel
        recyclerView = view.findViewById(R.id.recyclerView)

        recyclerView.layoutManager = LinearLayoutManager(context)

        userViewModel.userId.observe(viewLifecycleOwner) { userIdStr ->
            val userId = userIdStr?.toIntOrNull()
            if (userId != null) {
                loadRecentTransactions(userId, recyclerView)
            }
        }

        val monthNames = listOf("Jan", "Feb", "Mar", "Apr", "May", "Jun",
            "Jul", "Aug", "Sep", "Oct", "Nov", "Dec")

        userViewModel.userId.observe(viewLifecycleOwner) { userIdStr ->
            val userId = userIdStr?.toIntOrNull()
            if (userId != null) {
                loadRecentTransactions(userId, recyclerView)
                showCurrentMonthBudget(userId)
            }
        }


        showDailyTipDialog()
    }


    private fun showDailyTipDialog() {
        if (dailyTipShown) return
        dailyTipShown = true

        val tips = listOf(
            "Track your spending daily to build better habits!",
            "You're one step closer to your goals – keep going!",
            "Small savings today lead to big wins tomorrow.",
            "Budgeting is the first step toward financial freedom.",
            "Review your budget regularly to stay on track.",
            "Avoid impulse purchases by waiting 24 hours before buying.",
            "Set clear financial goals to motivate your saving.",
            "Use cash envelopes for better control over spending.",
            "Automate your savings to build your emergency fund.",
            "Cut down on small daily expenses; they add up quickly.",
            "Compare prices before making big purchases.",
            "Keep track of subscriptions and cancel what you don’t use.",
            "Plan meals ahead to save money on groceries.",
            "Use apps to monitor your credit score and finances."
        )

        val randomTip = tips.random()

        AlertDialog.Builder(requireContext())
            .setTitle("💡 Daily Tip")
            .setMessage(randomTip)
            .setPositiveButton("Got it!") { dialog, _ ->
                dialog.dismiss()
            }
            .show()
    }


    private fun showCurrentMonthBudget(userId: Int) {
        val textView = view?.findViewById<TextView>(R.id.budgetAmount) ?: return
        val textView1 = view?.findViewById<TextView>(R.id.expenseAmount) ?: return
        val textView2 = view?.findViewById<TextView>(R.id.goalAmount) ?: return
        val textView3 = view?.findViewById<TextView>(R.id.expenseAmountBudget) ?: return
        val textView4 = view?.findViewById<TextView>(R.id.goalAmountBudget) ?: return

        val calendar = Calendar.getInstance()
        val currentMonth = calendar.get(Calendar.MONTH) // 0-based
        val currentYear = calendar.get(Calendar.YEAR)
        val monthName = listOf("Jan", "Feb", "Mar", "Apr", "May", "Jun",
            "Jul", "Aug", "Sep", "Oct", "Nov", "Dec")[currentMonth]

        lifecycleScope.launch {
            val amount = db.budgetDao().getBudgetAmountForUserAndMonthYear(userId, monthName, currentYear)
            val expenseAmount = db.transactionsDao().getTransactionSumByTypeForMonthYear(userId, monthName, currentYear, "Expense")
            val goalAmount  = db.transactionsDao().getTransactionSumByTypeForMonthYear(userId, monthName, currentYear, "Goal")
            val expenseAmountBudget = db.categoryBudgetDao().getCategoryBudgetTotalByType(userId, monthName, currentYear, "Expense")
            val goalAmountBudget = db.categoryBudgetDao().getCategoryBudgetTotalByType(userId, monthName, currentYear, "Goal")

            withContext(Dispatchers.Main) {
                if (amount != null) {
                    val formatted =  formatAmountWithSpace(amount)
                    textView.text = formatted
                } else {
                    textView.text = "R0"
                }

                if (expenseAmount != null) {
                    val formatted = formatAmountWithSpace(expenseAmount)
                    textView1.text = formatted
                } else {
                    textView1.text = "R0"
                }

                if (goalAmount != null) {
                    val formatted = formatAmountWithSpace(goalAmount)
                    textView2.text = formatted
                } else {
                    textView2.text = "R0"
                }

                if (expenseAmountBudget != null) {
                    val formatted = formatAmountWithSpace(expenseAmountBudget)
                    textView3.text = formatted
                } else {
                    textView3.text = "R0"
                }

                if (goalAmountBudget != null) {
                    val formatted = formatAmountWithSpace(goalAmountBudget)
                    textView4.text = formatted
                } else {
                    textView4.text = "R0"
                }
            }
        }
    }

    fun formatAmountWithSpace(amount: Number): String {
        val symbols = DecimalFormatSymbols(Locale.getDefault()).apply {
            groupingSeparator = ' '  // space as thousands separator
            decimalSeparator = '.'   // decimal separator, not needed if no decimals
        }
        val pattern = "#,##0"  // no decimals
        val decimalFormat = DecimalFormat(pattern, symbols)
        return "R${decimalFormat.format(amount)}"
    }

    private fun loadRecentTransactions(userId: Int, recyclerView: RecyclerView) {
        lifecycleScope.launch {
            val transactions = db.transactionsDao().getRecentTransactions(userId)
            val items = transactions.map {
                TransactionRecordItem(
                    id = it.id,
                    name = it.categoryName,
                    date = "${it.day} ${it.month}",
                    amount = "R${"%.2f".format(it.amount)}",
                    iconResId = it.iconResId ?: R.drawable.ic_categories,
                    receiptUri = it.receipt,
                    description = it.description,
                    subtext = "${it.day} ${it.month} ${it.year} ${it.description}"
                )
            }.toMutableList()

            withContext(Dispatchers.Main) {
                recyclerView.adapter = TransactionRecordAdapter(requireContext(), items) { item ->
                    lifecycleScope.launch {
                        db.transactionsDao().deleteTransactionById(item.id)
                        loadRecentTransactions(userId, recyclerView) // Reload the list
                    }
                }
            }
        }
    }


}
