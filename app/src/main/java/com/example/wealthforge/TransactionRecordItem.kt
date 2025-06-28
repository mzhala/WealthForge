package com.example.wealthforge

data class TransactionRecordItem(
    val id: Int,
    val name: String,
    val date: String,
    val amount: String,
    val iconResId: Int,
    val receiptUri: String?,
    val description: String,
    val subtext: String
)