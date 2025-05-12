package com.example.wealthforge

import UserViewModel
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.NumberPicker
import android.widget.Spinner
import java.util.*
import android.widget.EditText
import android.widget.Toast
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.room.Room
import com.example.wealthforge.data.AppDatabase
import com.example.wealthforge.data.Budget
import com.example.wealthforge.data.BudgetDao
import com.example.wealthforge.data.Category
import com.example.wealthforge.data.UserDao
import kotlinx.coroutines.launch

class BudgetFragment : Fragment() {

    private lateinit var db: AppDatabase
    private val userViewModel: UserViewModel by activityViewModels() // Access UserViewModel shared across activity
    private lateinit var budgetDao: BudgetDao

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_budget, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Optional: update toolbar title if needed
        (activity as? MainActivity)?.updateToolbarTitle("Budget")

        // db and Signed in user id
        db = Room.databaseBuilder(
            requireContext(),
            AppDatabase::class.java, "wealthforge-db"
        ).build()
        val userId = userViewModel.userId.value?.toIntOrNull()

        //
        val monthSpinner: Spinner = view.findViewById(R.id.monthSpinner)
        val yearSpinner: Spinner = view.findViewById(R.id.yearSpinner)
        val categorySpinner: Spinner = view.findViewById(R.id.categorySpinner)
        val budgetAmountInput: EditText = view.findViewById(R.id.budgetAmountInput)
        val enteredValue_budgetAmountInput = budgetAmountInput.text.toString().toIntOrNull()
        val limitAmountInput: EditText = view.findViewById(R.id.limitAmountInput)
        val enteredValue_limitAmountInput = budgetAmountInput.text.toString().toIntOrNull()

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


        val updateBudgetAmount: () -> Unit = {
            val userId = userViewModel.userId.value?.toIntOrNull()
            val selectedMonth = monthSpinner.selectedItem?.toString()
            val selectedYear = yearSpinner.selectedItem?.toString()?.toIntOrNull()

            if (userId != null && selectedMonth != null && selectedYear != null) {
                lifecycleScope.launch {
                    val budget = db.budgetDao()
                        .getBudgetForUserAndMonthYear(userId, selectedMonth, selectedYear)
                    val amount = budget?.amount?.toString() ?: ""
                    budgetAmountInput.setText(amount)
                }
            }
        }

        // Set listeners to update budget when selection changes
        monthSpinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>, view: View?, position: Int, id: Long) {
                updateBudgetAmount()
            }

            override fun onNothingSelected(parent: AdapterView<*>) {}
        }

        yearSpinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>, view: View?, position: Int, id: Long) {
                updateBudgetAmount()
            }

            override fun onNothingSelected(parent: AdapterView<*>) {}
        }
        val setBudgetAmountButton = view.findViewById<Button>(R.id.setBudgetAmountButton)
        // set budget amount button
        setBudgetAmountButton.setOnClickListener {
            val context = requireContext()
            val budgetAmount = view.findViewById<EditText>(R.id.budgetAmountInput).text.toString().toIntOrNull() ?: 0
            val month = view.findViewById<Spinner>(R.id.monthSpinner).selectedItem.toString()
            val year = view.findViewById<Spinner>(R.id.yearSpinner).selectedItem.toString().toIntOrNull() ?: 0
            val userId = userViewModel.userId.value?.toIntOrNull()

            if (userId == null) {
                Toast.makeText(context, "Invalid user ID", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            if (budgetAmount <= 0) {
                Toast.makeText(context, "Enter Budget Amount", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            lifecycleScope.launch {
                try {
                    val userExists = db.userDao().getUsername(userId) != null
                    if (!userExists) {
                        Toast.makeText(context, "User does not exist", Toast.LENGTH_SHORT).show()
                        return@launch
                    }

                    val budget = Budget(userId = userId, month = month, year = year, amount = budgetAmount)

                    if (db.budgetDao().checkBudgetExists(userId, month, year) == 0) {
                        db.budgetDao().insertBudget(budget)
                        Toast.makeText(context, "Budget Added", Toast.LENGTH_SHORT).show()
                    } else {
                        db.budgetDao().updateBudget(budget)
                        Toast.makeText(context, "Budget Updated", Toast.LENGTH_SHORT).show()
                    }
                } catch (e: Exception) {
                    Toast.makeText(context, "Error: ${e.message}", Toast.LENGTH_SHORT).show()
                }
            }
        }





        val categories = listOf("Rent", "Groceries", "Transport", "Entertainment")
        categorySpinner.adapter = ArrayAdapter(requireContext(), android.R.layout.simple_spinner_dropdown_item, categories)
        categorySpinner.setSelection(0)

        // recycle view displaying categories added on budget
        val recyclerView = view.findViewById<RecyclerView>(R.id.recyclerView)

        val budgetCategories = listOf(
            CategoryBudgetItem("Spotify", "R500", R.drawable.spotify),
            CategoryBudgetItem("Grocery", "R850", R.drawable.grocery),
            CategoryBudgetItem("Transport", "R8400",  R.drawable.transport),
            CategoryBudgetItem("Spotify", "R500", R.drawable.spotify),
            CategoryBudgetItem("Grocery", "R850", R.drawable.grocery),
            CategoryBudgetItem("Transport", "R8400",  R.drawable.transport),
            CategoryBudgetItem("Spotify", "R500", R.drawable.spotify),
            CategoryBudgetItem("Grocery", "R850", R.drawable.grocery),
            CategoryBudgetItem("Transport", "R8400",  R.drawable.transport),
            CategoryBudgetItem("Spotify", "R500", R.drawable.spotify),
            CategoryBudgetItem("Grocery", "R850", R.drawable.grocery),
            CategoryBudgetItem("Transport", "R8400",  R.drawable.transport)
        )

        recyclerView.layoutManager = LinearLayoutManager(context) // Set the layout manager
        recyclerView.adapter = CategoryBudgetAdapter(budgetCategories) // Set the adapter to bind data

    }

}
