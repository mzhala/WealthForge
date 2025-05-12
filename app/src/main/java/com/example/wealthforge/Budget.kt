package com.example.wealthforge.data

import androidx.room.*

@Entity(
    tableName = "budget",
    foreignKeys = [
        ForeignKey(
            entity = User::class,
            parentColumns = ["id"],
            childColumns = ["user_id"],
            onDelete = ForeignKey.CASCADE
        )
    ],
    indices = [Index(value = ["user_id", "month", "year"], unique = true)]  // Ensure unique month-year combination
)
data class Budget(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,

    @ColumnInfo(name = "user_id")
    val userId: Int,

    @ColumnInfo(name = "month")
    val month: String,  // 1 = January, 2 = February, etc.

    @ColumnInfo(name = "year")
    val year: Int,

    @ColumnInfo(name = "amount")
    val amount: Int
)
