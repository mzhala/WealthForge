package com.example.wealthforge.data

import androidx.room.*
@Entity(
    tableName = "categoryBudget",
    foreignKeys = [
        ForeignKey(
            entity = User::class,
            parentColumns = ["id"],
            childColumns = ["user_id"],
            onDelete = ForeignKey.CASCADE
        )
    ],
    indices = [Index(value = ["user_id", "category_name", "year", "month"], unique = true)]
)
data class CategoryBudget(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,

    @ColumnInfo(name = "user_id")
    val userId: Int,

    val category_name: String,

    val year: Int,
    val month: String,
    val amount: Int,
    val iconResId: Int? // Path or URI to the profile picture, optional

)
