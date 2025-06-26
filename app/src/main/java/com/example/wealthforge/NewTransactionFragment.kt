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
import android.app.Activity
import android.content.Intent
import android.net.Uri
import androidx.activity.result.contract.ActivityResultContracts

class NewTransactionFragment : Fragment() {

    private var receiptUri: Uri? = null

    private lateinit var db: AppDatabase
    private val userViewModel: UserViewModel by activityViewModels()

    private val pickImageLauncher = registerForActivityResult(
        ActivityResultContracts.GetContent()
    ) { uri: Uri? ->
        if (uri != null) {
            receiptUri = uri
            Toast.makeText(requireContext(), "Receipt image selected", Toast.LENGTH_SHORT).show()
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        return inflater.inflate(R.layout.fragment_new_transaction, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        (activity as? MainActivity)?.updateToolbarTitle("New Transaction")

        db = Room.databaseBuilder(
            requireContext(),
            AppDatabase::class.java,
            "wealthforge-db"
        ).build()

        val categorySpinner: Spinner = view.findViewById(R.id.categorySpinner)
        val monthSpinner: Spinner = view.findViewById(R.id.monthSpinner)
        val yearSpinner: Spinner = view.findViewById(R.id.yearSpinner)
        val amountInput: EditText = view.findViewById(R.id.AmountInput)
        val descriptionInput: EditText = view.findViewById(R.id.description)
        val addTransactionButton: Button = view.findViewById(R.id.addTransactionButton)
        val recyclerView: RecyclerView = view.findViewById(R.id.recyclerView)

        val current = Calendar.getInstance()
        val currentMonth = current.get(Calendar.MONTH)
        val currentYear = current.get(Calendar.YEAR)
        val months = listOf("Jan", "Feb", "Mar", "Apr", "May", "Jun", "Jul", "Aug", "Sep", "Oct", "Nov", "Dec")
        val years = (currentYear..(currentYear + 5)).map { it.toString() }
        val daySpinner: Spinner = view.findViewById(R.id.daySpinner)

        monthSpinner.adapter = ArrayAdapter(requireContext(), android.R.layout.simple_spinner_dropdown_item, months)
        yearSpinner.adapter = ArrayAdapter(requireContext(), android.R.layout.simple_spinner_dropdown_item, years)
        monthSpinner.setSelection(currentMonth)
        yearSpinner.setSelection(0)

        // Set default days for current month/year
        fun updateDaysSpinner(monthIndex: Int, year: Int) {
            val calendar = Calendar.getInstance()
            calendar.set(Calendar.MONTH, monthIndex)
            calendar.set(Calendar.YEAR, year)
            val daysInMonth = calendar.getActualMaximum(Calendar.DAY_OF_MONTH)

            val days = (1..daysInMonth).map { it.toString() }
            daySpinner.adapter = ArrayAdapter(requireContext(), android.R.layout.simple_spinner_dropdown_item, days)
        }

        // Initial set
        updateDaysSpinner(currentMonth, currentYear)

        // Update days when month/year changes
        monthSpinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                val year = yearSpinner.selectedItem.toString().toIntOrNull() ?: currentYear
                updateDaysSpinner(position, year)
            }

            override fun onNothingSelected(parent: AdapterView<*>?) {}


        }

        yearSpinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                val monthIndex = monthSpinner.selectedItemPosition
                val year = years[position].toIntOrNull() ?: currentYear
                updateDaysSpinner(monthIndex, year)
            }

            override fun onNothingSelected(parent: AdapterView<*>?) {}
        }

        recyclerView.layoutManager = LinearLayoutManager(requireContext())

        val userId = userViewModel.userId.value?.toIntOrNull()
        if (userId == null) {
            Toast.makeText(requireContext(), "User not found", Toast.LENGTH_SHORT).show()
            return
        }

        lifecycleScope.launch {
            val categoryNames = db.categoryDao().getAllCategoryNamesByUser(userId)
            val adapter = ArrayAdapter(requireContext(), android.R.layout.simple_spinner_dropdown_item, categoryNames)
            withContext(Dispatchers.Main) {
                categorySpinner.adapter = adapter
            }

            loadRecentTransactions(userId, recyclerView)
        }

        val pickReceiptButton: Button = view.findViewById(R.id.pickReceiptButton)
        pickReceiptButton.setOnClickListener {
            // Open gallery for images only
            pickImageLauncher.launch("image/*")
        }

        addTransactionButton.setOnClickListener {
            val category = categorySpinner.selectedItem?.toString() ?: return@setOnClickListener
            val amount = amountInput.text.toString().toDoubleOrNull()
            val description = descriptionInput.text.toString().trim()
            val month = monthSpinner.selectedItem.toString()
            val year = yearSpinner.selectedItem.toString().toIntOrNull()
            val monthIndex = monthSpinner.selectedItemPosition
            val day = daySpinner.selectedItem.toString().toInt()

            if (amount == null || year == null) {
                Toast.makeText(requireContext(), "Please fill in all required fields correctly", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            lifecycleScope.launch {
                try {
                    val iconResId = db.categoryDao().getCategoryIcon(userId, category)
                    val receiptPath = receiptUri?.toString() // store URI as string or handle differently

                    val transaction = Transactions(
                        userId = userId,
                        categoryName = category,
                        amount = amount,
                        description = description,
                        month = month,
                        year = year,
                        monthIndex = monthIndex,
                        day = day,
                        iconResId = iconResId,
                        receipt = receiptPath
                    )

                    db.transactionsDao().insertTransaction(transaction)

                    withContext(Dispatchers.Main) {
                        Toast.makeText(requireContext(), "Transaction added", Toast.LENGTH_SHORT).show()
                        amountInput.text.clear()
                        descriptionInput.text.clear()
                        receiptUri = null  // reset after use
                        loadRecentTransactions(userId, recyclerView)
                    }

                } catch (e: Exception) {
                    withContext(Dispatchers.Main) {
                        Toast.makeText(requireContext(), "Error: ${e.message}", Toast.LENGTH_LONG).show()
                    }
                }
            }
        }




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
                    iconResId = it.iconResId ?: R.drawable.ic_categories,
                    receiptUri = it.receipt
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
