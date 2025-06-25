package com.example.wealthforge

import UserViewModel
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.fragment.app.activityViewModels
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.room.Room
import com.example.wealthforge.data.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class CategoriesFragment : Fragment() {

    private lateinit var db: AppDatabase
    private val userViewModel: UserViewModel by activityViewModels()

    private lateinit var recyclerView: RecyclerView
    private lateinit var categoryAdapter: CategoryAdapter

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View = inflater.inflate(R.layout.fragment_categories, container, false)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        (activity as? MainActivity)?.updateToolbarTitle("Categories")

        db = Room.databaseBuilder(
            requireContext(),
            AppDatabase::class.java, "wealthforge-db"
        ).build()

        val categorySpinner: Spinner = view.findViewById(R.id.categoryTypeSpinner)
        val categoryTypes = listOf("Expense", "Goal")
        categorySpinner.adapter = ArrayAdapter(
            requireContext(),
            android.R.layout.simple_spinner_dropdown_item,
            categoryTypes
        )
        categorySpinner.setSelection(0)

        recyclerView = view.findViewById(R.id.recyclerView)
        recyclerView.layoutManager = LinearLayoutManager(context)

        view.findViewById<Button>(R.id.addCategoryButton).setOnClickListener {
            addCategory(view)
        }

        loadCategories()
    }

    private fun addCategory(view: View) {
        val name = view.findViewById<EditText>(R.id.categoryName).text.toString().trim()
        val type = view.findViewById<Spinner>(R.id.categoryTypeSpinner).selectedItem.toString()
        val isRecurring = view.findViewById<CheckBox>(R.id.agreeCheckbox).isChecked
        val limitAmount = view.findViewById<EditText>(R.id.limitAmountInput).text.toString().toDoubleOrNull() ?: 0.0
        val iconResId = R.drawable.ic_categories
        val userId = userViewModel.userId.value?.toIntOrNull()

        if (userId == null) {
            Toast.makeText(requireContext(), "Invalid user ID", Toast.LENGTH_SHORT).show()
            return
        }

        lifecycleScope.launch {
            try {
                val userExists = db.userDao().getUsername(userId) != null
                if (!userExists) {
                    Toast.makeText(requireContext(), "User does not exist", Toast.LENGTH_SHORT).show()
                    return@launch
                }

                val exists = db.categoryDao().categoryExists(userId, name) > 0
                if (exists) {
                    withContext(Dispatchers.Main) {
                        Toast.makeText(requireContext(), "Category already exists", Toast.LENGTH_SHORT).show()
                    }
                    return@launch
                }

                val category = Category(
                    userId = userId,
                    categoryName = name,
                    type = type,
                    recurring = isRecurring,
                    amount = if (!isRecurring) 0.0 else limitAmount,
                    iconResId = iconResId
                )

                db.categoryDao().insertCategory(category)
                withContext(Dispatchers.Main) {
                    Toast.makeText(requireContext(), "Category Added", Toast.LENGTH_SHORT).show()
                    loadCategories()
                }
            } catch (e: Exception) {
                withContext(Dispatchers.Main) {
                    Toast.makeText(requireContext(), "Error: ${e.message}", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }


    // This function can be reused to refresh the RecyclerView anytime.
    fun loadCategories() {
        val userId = userViewModel.userId.value?.toIntOrNull()
        if (userId == null) {
            Toast.makeText(requireContext(), "Error fetching user ID", Toast.LENGTH_SHORT).show()
            return
        }

        lifecycleScope.launch {
            val savedCategories = db.categoryDao().getCategoriesByUser(userId)
            val categoryItems = savedCategories.map {
                CategoryItem(
                    id = it.id,
                    name = it.categoryName,
                    categoryType = it.type,
                    recurringAmount = "R${it.amount}",
                    iconResId = it.iconResId ?: R.drawable.ic_categories
                )
            }

            categoryAdapter = CategoryAdapter(categoryItems.toMutableList()) { item ->
                lifecycleScope.launch {
                    db.categoryDao().deleteCategoryById(item.id)
                    Toast.makeText(requireContext(), "Category deleted", Toast.LENGTH_SHORT).show()
                    loadCategories() // refresh after delete
                }
            }

            recyclerView.adapter = categoryAdapter
        }
    }
}
