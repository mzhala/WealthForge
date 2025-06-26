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
    WHERE user_id = :userId 
      AND (
        year > :startYear 
        OR (year = :startYear AND monthIndex >= :startMonth)
      )
      AND (
        year < :endYear 
        OR (year = :endYear AND monthIndex <= :endMonth)
      )
    ORDER BY year desc, monthIndex desc, day desc
""")
    suspend fun getTransactionsBetween(
        userId: Int,
        startMonth: Int,
        startYear: Int,
        endMonth: Int,
        endYear: Int
    ): List<Transactions>

    @Query("""
    SELECT SUM(amount) FROM transactions
    WHERE user_id = :userId 
      AND (
        year > :startYear 
        OR (year = :startYear AND monthIndex >= :startMonth)
      )
      AND (
        year < :endYear 
        OR (year = :endYear AND monthIndex <= :endMonth)
      )
    ORDER BY year desc, monthIndex desc, day desc
""")
    suspend fun getTransactionsBetweenTotal(
        userId: Int,
        startMonth: Int,
        startYear: Int,
        endMonth: Int,
        endYear: Int
    ): Double?



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
    b.category_name AS category_name,
    COALESCE(SUM(t.amount), 0.0) AS total,
    b.total_budget AS budget
FROM (
    SELECT category_name, user_id, SUM(amount) AS total_budget
    FROM categoryBudget
    WHERE user_id = :userId
      AND (
          (year > :startYear OR (year = :startYear AND monthIndex >= :startMonthIndex)) AND
          (year < :endYear OR (year = :endYear AND monthIndex <= :endMonthIndex))
      )
    GROUP BY category_name, user_id
) AS b
LEFT JOIN transactions t
    ON b.category_name = t.category_name
    AND b.user_id = t.user_id
    AND (
        (t.year > :startYear OR (t.year = :startYear AND t.monthIndex >= :startMonthIndex)) AND
        (t.year < :endYear OR (t.year = :endYear AND t.monthIndex <= :endMonthIndex))
    )
GROUP BY b.category_name, b.total_budget
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
