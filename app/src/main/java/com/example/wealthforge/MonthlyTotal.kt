package com.example.wealthforge.data

data class MonthlyTotal(
    val year: Int,
    val month_num: Int,
    val budget: Double,
    val spent: Double
)
