package com.example.wealthforge.data
import com.example.wealthforge.data.CategoryTotal

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

    @Query("SELECT * FROM transactions WHERE user_id = :userId ORDER BY year DESC, monthIndex DESC, day DESC")
    suspend fun getRecentTransactions(userId: Int): List<Transactions>

    @Query("""
    SELECT category_name, SUM(amount) AS total
    FROM transactions
    WHERE user_id = :userId AND 
          (year > :startYear OR (year = :startYear AND monthIndex >= :startMonthIndex))
          AND
          (year < :endYear OR (year = :endYear AND monthIndex <= :endMonthIndex))
    GROUP BY category_name
    ORDER BY total DESC
""")
    suspend fun getCategoryTotalsInRange(
        userId: Int,
        startMonthIndex: Int,
        startYear: Int,
        endMonthIndex: Int,
        endYear: Int
    ): List<CategoryTotal>

    @Query("""
    SELECT 
        t.category_name AS category_name,
        SUM(t.amount) AS total,
        COALESCE(cb.amount, 0.0) AS budget
    FROM transactions t
    LEFT JOIN categoryBudget cb 
        ON t.category_name = cb.category_name AND t.user_id = cb.user_id
    WHERE t.user_id = :userId AND (
        (t.year > :startYear OR (t.year = :startYear AND t.monthIndex >= :startMonthIndex)) AND
        (t.year < :endYear OR (t.year = :endYear AND t.monthIndex <= :endMonthIndex))
    )
    GROUP BY t.category_name
    ORDER BY total DESC
""")
    suspend fun getCategorySpendingWithBudgetInRange(
        userId: Int,
        startMonthIndex: Int,
        startYear: Int,
        endMonthIndex: Int,
        endYear: Int
    ): List<CategorySpending>

    @Query("SELECT DISTINCT month, year FROM transactions WHERE user_id = :userId")
    fun getDistinctMonthYear(userId: Int): List<DistinctMonthYear>

    @Query("DELETE FROM transactions WHERE id = :transactionId")
    suspend fun deleteTransactionById(transactionId: Int)

}
