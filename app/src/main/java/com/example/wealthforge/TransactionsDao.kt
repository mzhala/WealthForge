package com.example.wealthforge.data

import androidx.room.*

@Dao
interface TransactionsDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertTransaction(transaction: Transactions)

    @Delete
    suspend fun deleteTransaction(transaction: Transactions)

    @Query("SELECT * FROM transactions WHERE user_id = :userId")
    suspend fun getAllTransactionsForUser(userId: Int): List<Transactions>

    @Query("""
        SELECT * FROM transactions
        WHERE user_id = :userId AND (
            (year > :startYear OR (year = :startYear AND monthIndex >= :startMonth)) AND
            (year < :endYear OR (year = :endYear AND monthIndex <= :endMonth))
        )
    """)
    suspend fun getTransactionsBetween(
        userId: Int,
        startMonth: Int,
        startYear: Int,
        endMonth: Int,
        endYear: Int
    ): List<Transactions>
}
