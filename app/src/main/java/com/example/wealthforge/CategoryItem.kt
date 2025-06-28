package com.example.wealthforge

import android.app.appsearch.AppSearchSchema.BooleanPropertyConfig

data class CategoryItem(
    val id: Int=0,
    val name: String,
    val categoryType: String,
    val recurringAmount: String,
    val iconResId: Int,
    val recurring: Boolean
)
