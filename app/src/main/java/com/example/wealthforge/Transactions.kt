package com.example.wealthforge.data

import androidx.room.*

@Entity(
    tableName = "transactions",
    foreignKeys = [
        ForeignKey(
            entity = User::class,
            parentColumns = ["id"],
            childColumns = ["user_id"],
            onDelete = ForeignKey.CASCADE
        )
    ],
    indices = [Index(value = ["user_id", "category_name, month, year"], unique = true)]
)
data class Transactions(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,

    @ColumnInfo(name = "user_id")
    val userId: Int,

    @ColumnInfo(name = "category_name")
    val categoryName: String,

    val amount: Double = 0.0,

    val description: String,
    val month: String,
    val monthIndex: Int,
    val year: Int,

    val iconResId: Int? // Path or URI to the profile picture, optional

)
