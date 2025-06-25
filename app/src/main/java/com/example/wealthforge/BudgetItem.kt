package com.example.wealthforge

data class BudgetItem(
    val id: Int,
    val categoryName: String,
    val budgetAmount: String,
    val iconResId: Int = R.drawable.ic_categories
)
