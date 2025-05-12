package com.example.wealthforge

import UserViewModel
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.Spinner
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.lifecycle.lifecycleScope
import kotlinx.coroutines.launch
import android.widget.Toast
import androidx.activity.viewModels
import androidx.room.Room
import android.widget.Button
import android.widget.CheckBox
import android.widget.EditText
import android.widget.TextView
import androidx.fragment.app.activityViewModels
import com.example.wealthforge.data.Category
import com.example.wealthforge.data.AppDatabase
import com.example.wealthforge.data.CategoryDao
import com.example.wealthforge.data.UserDao

class CategoriesFragment : Fragment() {

    private lateinit var db: AppDatabase
    private val userViewModel: UserViewModel by activityViewModels() // Access UserViewModel shared across activity
    //private lateinit var usernameTextView: TextView
    private lateinit var userDao: UserDao
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_categories, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        (activity as? MainActivity)?.updateToolbarTitle("Categories")

        db = Room.databaseBuilder(
            requireContext(),
            AppDatabase::class.java, "wealthforge-db"
        ).build()
        val userId = userViewModel.userId.value?.toIntOrNull()
        // type spinner
        val categorySpinner: Spinner = view.findViewById(R.id.categoryTypeSpinner)

        val categoryTypes = listOf("Expense", "Goal")
        categorySpinner.adapter = ArrayAdapter(
            requireContext(),
            android.R.layout.simple_spinner_dropdown_item,
            categoryTypes
        )
        categorySpinner.setSelection(0)

        val debugging = view.findViewById<TextView>(R.id.debugging)
        debugging.text = userId.toString()
        // Retrieve the username in a coroutine
       /* if (userId != null) {
            viewLifecycleOwner.lifecycleScope.launch {
                try {
                    val username = db.userDao().getUsername(userId)
                    usernameTextView.text = username
                } catch (e: Exception) {
                    Toast.makeText(requireContext(), "Error fetching username", Toast.LENGTH_SHORT).show()
                }
            }
        } else {
            Toast.makeText(requireContext(), "User ID is null", Toast.LENGTH_SHORT).show()
        }*/

        val addCategoryButton = view.findViewById<Button>(R.id.addCategoryButton)

        addCategoryButton.setOnClickListener {
            val name = view.findViewById<EditText>(R.id.categoryName).text.toString()
            val type = view.findViewById<Spinner>(R.id.categoryTypeSpinner).selectedItem.toString()
            val isRecurring = view.findViewById<CheckBox>(R.id.agreeCheckbox).isChecked
            val limitAmountText = view.findViewById<EditText>(R.id.limitAmountInput).text.toString()
            val limitAmount = limitAmountText.toDoubleOrNull() ?: 0.0
            val iconResId = R.drawable.ic_categories // set default or use the uploaded one

            // Get userId from ViewModel
            val userId = userViewModel.userId.value?.toIntOrNull()

            if (name.isNotBlank() && userId != null) {
                val category = Category(
                    userId = userId,
                    categoryName = name,
                    type = type,
                    recurring = isRecurring,
                    amount = limitAmount,
                    iconResId = iconResId
                )

                // insert into DB on background thread
                lifecycleScope.launch {
                    db.categoryDao().insertCategory(category)
                    Toast.makeText(requireContext(), "Category Added", Toast.LENGTH_SHORT).show()
                }
            } else {
                Toast.makeText(requireContext(), "Please enter category name and valid user ID", Toast.LENGTH_SHORT)
                    .show()
            }
        }

        // recycler view for displaying category budgets
        val recyclerView = view.findViewById<RecyclerView>(R.id.recyclerView)
        recyclerView.layoutManager = LinearLayoutManager(context)

        lifecycleScope.launch {
            val userId = userViewModel.userId.value?.toIntOrNull()

            if (userId != null) {
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
                recyclerView.adapter = CategoryAdapter(categoryItems.toMutableList()) { item ->
                    // This lambda receives the id to delete
                    lifecycleScope.launch {
                        db.categoryDao().deleteCategoryById(item.id)
                        Toast.makeText(requireContext(), "Category deleted", Toast.LENGTH_SHORT).show()
                    }
                }
            } else {
                Toast.makeText(requireContext(), "Error fetching user ID", Toast.LENGTH_SHORT).show()
            }
        }
    }
}
