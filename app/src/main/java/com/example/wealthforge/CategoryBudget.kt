package com.example.wealthforge.data

import androidx.room.*
@Entity(
    tableName = "categoryBudget",
    foreignKeys = [
        ForeignKey(
            entity =Category::class,
            parentColumns = ["id"],
            childColumns = ["category_id"],
            onDelete = ForeignKey.CASCADE
        )
    ],
    indices = [Index(value = ["category_id", "year", "month"], unique = true)]
)
data class CategoryBudget(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,

    @ColumnInfo(name = "category_id")
    val categoryId: Int,

    val year: Int,
    val month: String,
    val amount: Int
)
