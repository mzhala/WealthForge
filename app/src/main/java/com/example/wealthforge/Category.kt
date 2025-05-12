package com.example.wealthforge.data

import androidx.room.*

@Entity(
    tableName = "categories",
    foreignKeys = [
        ForeignKey(
            entity = User::class,
            parentColumns = ["id"],
            childColumns = ["user_id"],
            onDelete = ForeignKey.CASCADE
        )
    ],
    indices = [Index(value = ["user_id"]), Index(value = ["category_name"], unique = true)]
)
data class Category(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,

    @ColumnInfo(name = "user_id")
    val userId: Int,

    @ColumnInfo(name = "category_name")
    val categoryName: String,

    val type: String = "Expense",

    val recurring: Boolean = false,

    val amount: Double = 0.0,

    val iconResId: Int? // Path or URI to the profile picture, optional

)
