package com.example.wealthforge

import UserViewModel
import android.os.Bundle
import android.view.*
import android.widget.*
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.room.Room
import com.example.wealthforge.data.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.util.*

class BudgetFragment : Fragment() {

    private lateinit var db: AppDatabase
    private val userViewModel: UserViewModel by activityViewModels()

    private lateinit var monthSpinner: Spinner
    private lateinit var yearSpinner: Spinner
    private lateinit var categorySpinner: Spinner
    private lateinit var budgetAmountInput: EditText
    private lateinit var categoryBudgetAmountInput: EditText
    private lateinit var recyclerView: RecyclerView
    private lateinit var categoryBudgetAdapter: CategoryBudgetAdapter
    private lateinit var typeSpinner: Spinner
    private lateinit var recurringCheckbox: CheckBox

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View = inflater.inflate(R.layout.fragment_budget, container, false)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        (activity as? MainActivity)?.updateToolbarTitle("Budget")

        db = Room.databaseBuilder(requireContext(), AppDatabase::class.java, "wealthforge-db").build()

        initViews(view)
        setupSpinners()
        setupListeners()
        loadCategoryNames()
        loadBudgetItems()


    }

    private fun initViews(view: View) {
        monthSpinner = view.findViewById(R.id.monthSpinner)
        yearSpinner = view.findViewById(R.id.yearSpinner)
        categorySpinner = view.findViewById(R.id.categorySpinner)
        budgetAmountInput = view.findViewById(R.id.budgetAmountInput)
        categoryBudgetAmountInput = view.findViewById(R.id.categoryBudgetAmount)

        recyclerView = view.findViewById(R.id.recyclerView)
        recyclerView.layoutManager = LinearLayoutManager(context)


    }

    private fun setupSpinners() {
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
    }

    private fun setupListeners() {
        monthSpinner.onItemSelectedListener = spinnerChangeListener
        yearSpinner.onItemSelectedListener = spinnerChangeListener

        view?.findViewById<Button>(R.id.setBudgetAmountButton)?.setOnClickListener {
            setBudgetAmount()
        }

        view?.findViewById<Button>(R.id.addCategoryToBudgetButton)?.setOnClickListener {
            addCategoryBudget()
        }
    }

    private val spinnerChangeListener = object : AdapterView.OnItemSelectedListener {
        override fun onItemSelected(parent: AdapterView<*>, view: View?, position: Int, id: Long) {
            updateBudgetAmount()
            loadBudgetItems()
        }
        override fun onNothingSelected(parent: AdapterView<*>) {}
    }

    private fun updateBudgetAmount() {
        val userId = userViewModel.userId.value?.toIntOrNull() ?: return
        val month = monthSpinner.selectedItem?.toString() ?: return
        val year = yearSpinner.selectedItem?.toString()?.toIntOrNull() ?: return

        lifecycleScope.launch {
            val budget = db.budgetDao().getBudgetForUserAndMonthYear(userId, month, year)
            budgetAmountInput.setText(budget?.amount?.toString() ?: "")
        }
    }

    private fun setBudgetAmount() {
        val context = requireContext()
        val userId = userViewModel.userId.value?.toIntOrNull()
        val amount = budgetAmountInput.text.toString().toIntOrNull() ?: 0
        val month = monthSpinner.selectedItem.toString()
        val year = yearSpinner.selectedItem.toString().toIntOrNull() ?: 0

        if (userId == null || amount <= 0) {
            Toast.makeText(context, "Please enter valid budget info", Toast.LENGTH_SHORT).show()
            return
        }

        val monthIndex = when (month) {
            "Jan" -> 0
            "Feb" -> 1
            "Mar" -> 2
            "Apr" -> 3
            "May" -> 4
            "Jun" -> 5
            "Jul" -> 6
            "Aug" -> 7
            "Sep" -> 8
            "Oct" -> 9
            "Nov" -> 10
            "Dec" -> 11
            else -> 0
        }

        lifecycleScope.launch {
            try {
                if (db.userDao().getUsername(userId) == null) {
                    Toast.makeText(context, "User does not exist", Toast.LENGTH_SHORT).show()
                    return@launch
                }

                val budget = Budget(
                    userId = userId,
                    month = month,
                    year = year,
                    amount = amount,
                    monthIndex = monthIndex
                )

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

    private fun addCategoryBudget() {
        val context = requireContext()
        val userId = userViewModel.userId.value?.toIntOrNull()
        val amount = categoryBudgetAmountInput.text.toString().toDoubleOrNull() ?: 0.0
        val category_name = categorySpinner.selectedItem?.toString() ?: return
        val month = monthSpinner.selectedItem.toString()
        val year = yearSpinner.selectedItem.toString().toIntOrNull() ?: 0

        if (userId == null || amount <= 0) {
            Toast.makeText(context, "Please enter category budget info", Toast.LENGTH_SHORT).show()
            return
        }

        val monthIndex = when (month) {
            "Jan" -> 0
            "Feb" -> 1
            "Mar" -> 2
            "Apr" -> 3
            "May" -> 4
            "Jun" -> 5
            "Jul" -> 6
            "Aug" -> 7
            "Sep" -> 8
            "Oct" -> 9
            "Nov" -> 10
            "Dec" -> 11
            else -> 0
        }


        lifecycleScope.launch {
            try {
                if (db.userDao().getUsername(userId) == null) {
                    Toast.makeText(context, "User does not exist", Toast.LENGTH_SHORT).show()
                    return@launch
                }

                val existing = db.categoryBudgetDao()
                    .getCategoryBudget(userId, category_name, year, month)

                val iconId = db.categoryDao().getCategoryIcon(userId, category_name)
                val categoryBudget = if (existing != null) {
                    existing.copy(amount = amount, monthIndex = monthIndex) // update the amount, preserve id
                } else {
                    CategoryBudget(
                        userId = userId,
                        category_name = category_name,
                        year = year,
                        month = month,
                        amount = amount,
                        iconResId = iconId,
                        monthIndex = monthIndex,
                    )
                }


                if (db.categoryBudgetDao().checkCategoryBudgetExists(userId, category_name, year, month) == 0) {
                    db.categoryBudgetDao().insertCategoryBudget(categoryBudget)
                    withContext(Dispatchers.Main) {
                        Toast.makeText(context, "Category Budget Added", Toast.LENGTH_SHORT).show()
                        loadBudgetItems()
                    }
                } else {
                    db.categoryBudgetDao().updateCategoryBudget(categoryBudget)
                    withContext(Dispatchers.Main) {
                        Toast.makeText(context, "Category Budget Updated", Toast.LENGTH_SHORT).show()
                        loadBudgetItems()
                    }
                }


                loadBudgetItems()

            } catch (e: Exception) {
                Toast.makeText(context, "Error: ${e.message}", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun loadCategoryNames() {
        val userId = userViewModel.userId.value?.toIntOrNull() ?: return

        lifecycleScope.launch {
            val categories = db.categoryDao().getAllCategoryNamesByUser(userId)
            val adapter = ArrayAdapter(
                requireContext(),
                android.R.layout.simple_spinner_dropdown_item,
                categories
            )
            categorySpinner.adapter = adapter
        }
    }

    private fun loadBudgetItems() {
        val userId = userViewModel.userId.value?.toIntOrNull() ?: return
        val month = view?.findViewById<Spinner>(R.id.monthSpinner)?.selectedItem?.toString() ?: return
        val year = view?.findViewById<Spinner>(R.id.yearSpinner)?.selectedItem?.toString()?.toIntOrNull() ?: return
        val recyclerView = view?.findViewById<RecyclerView>(R.id.recyclerView) ?: return
        val totalTextView = view?.findViewById<TextView>(R.id.categories_total)

        lifecycleScope.launch {
            val budgets = db.categoryBudgetDao().getCategoryBudgetsByUser(userId, year, month)

            val items = budgets.map {
                BudgetItem(
                    id = it.id,
                    categoryName = it.category_name,
                    budgetAmount = "R${it.amount}",
                    iconResId = it.iconResId ?: R.drawable.ic_categories
                )
            }.toMutableList()

            recyclerView.adapter = BudgetAdapter(items) { item ->
                lifecycleScope.launch {
                    db.categoryBudgetDao().deleteCategoryBudgetById(item.id)
                    Toast.makeText(requireContext(), "${item.categoryName} deleted", Toast.LENGTH_SHORT).show()
                    loadBudgetItems() // refresh
                }
            }
            val total: Double? = db.categoryBudgetDao().getTotalCategoryBudgetCountByUser(userId, year, month)
            totalTextView?.text = "Total: R${"%.2f".format(total ?: 0.0)}"


        }
    }


    }
