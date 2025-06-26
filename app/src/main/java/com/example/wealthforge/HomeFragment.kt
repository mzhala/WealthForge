package com.example.wealthforge

import UserViewModel
import android.app.AlertDialog
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.wealthforge.data.AppDatabase
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class HomeFragment : Fragment() {

    private lateinit var db: AppDatabase
    private lateinit var recyclerView: RecyclerView
    private lateinit var userViewModel: UserViewModel


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

        showDailyTipDialog()
    }


    private fun showDailyTipDialog() {
        val tips = listOf(
            "Track your spending daily to build better habits!",
            "You're one step closer to your goals – keep going!",
            "Small savings today lead to big wins tomorrow.",
            "Budgeting is the first step toward financial freedom."
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


    private fun loadRecentTransactions(userId: Int, recyclerView: RecyclerView) {
        lifecycleScope.launch {
            val transactions = db.transactionsDao().getRecentTransactions(userId)
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
                        loadRecentTransactions(userId, recyclerView) // Reload the list
                    }
                }
            }
        }
    }
}
