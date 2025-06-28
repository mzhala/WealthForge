package com.example.wealthforge

data class BudgetChartEntry(
    val month: String,
    val spent: Double,
    val mainBudget: Double,
    val categoryBudgetTotal: Double
)
