package com.example.wealthforge.data

import androidx.room.*

@Dao
interface BudgetDao {

    // 1. Insert a new budget
    @Insert(onConflict = OnConflictStrategy.ABORT)
    suspend fun insertBudget(budget: Budget)

    // 2. Select a specific budget by user, month, and year
    @Query("SELECT * FROM budget WHERE user_id = :userId AND month = :month AND year = :year LIMIT 1")
    suspend fun getBudgetForUserAndMonthYear(userId: Int, month: String, year: Int): Budget?

    @Query("SELECT COUNT(*) FROM budget WHERE user_id = :userId AND month = :month AND year = :year LIMIT 1")
    suspend fun checkBudgetExists(userId: Int, month: String, year: Int): Int

    @Query("SELECT amount FROM budget WHERE user_id = :userId AND month = :month AND year = :year LIMIT 1")
    suspend fun getBudgetAmountForUserAndMonthYear(userId: Int, month: String, year: Int): Int?

    // 3. Get all budgets for a specific user, sorted by year then month
    @Query("SELECT * FROM budget WHERE user_id = :userId ORDER BY year DESC, month DESC")
    suspend fun getAllBudgetsForUser(userId: Int): List<Budget>

    // 4. Update only the amount of a specific budget
    @Query("UPDATE budget SET amount = :newAmount WHERE id = :budgetId")
    suspend fun updateBudgetAmount(budgetId: Int, newAmount: Int)

    @Query("UPDATE budget SET amount = :newAmount WHERE user_id = :userId AND month = :month AND year = :year")
    suspend fun updateBudgetAmountByMonthYear(userId: Int, month: String, year: Int, newAmount: Int)

    // 5. Update all fields of a budget using the entity
    @Update
    suspend fun updateBudget(budget: Budget)

    // 6. Delete a budget entry
    @Delete
    suspend fun deleteBudget(budget: Budget)

    // 7. Delete all budgets for a specific user
    @Query("DELETE FROM budget WHERE user_id = :userId")
    suspend fun deleteAllBudgetsForUser(userId: Int)

    @Query("""
    SELECT
        b.year,
        CASE b.month
            WHEN 'Jan' THEN 0
            WHEN 'Feb' THEN 1
            WHEN 'Mar' THEN 2
            WHEN 'Apr' THEN 3
            WHEN 'May' THEN 4
            WHEN 'Jun' THEN 5
            WHEN 'Jul' THEN 6
            WHEN 'Aug' THEN 7
            WHEN 'Sep' THEN 8
            WHEN 'Oct' THEN 9
            WHEN 'Nov' THEN 10
            WHEN 'Dec' THEN 11
        END as month_num,
        COALESCE(SUM(b.amount), 0) as budget,
        COALESCE(SUM(t.amount), 0) as spent
    FROM budget b
    LEFT JOIN transactions t
      ON b.user_id = t.user_id
      AND b.year = t.year
      AND (
        CASE b.month
            WHEN 'Jan' THEN 0
            WHEN 'Feb' THEN 1
            WHEN 'Mar' THEN 2
            WHEN 'Apr' THEN 3
            WHEN 'May' THEN 4
            WHEN 'Jun' THEN 5
            WHEN 'Jul' THEN 6
            WHEN 'Aug' THEN 7
            WHEN 'Sep' THEN 8
            WHEN 'Oct' THEN 9
            WHEN 'Nov' THEN 10
            WHEN 'Dec' THEN 11
        END = t.monthIndex
      )
    WHERE b.user_id = :userId
      AND (
        (b.year > :startYear OR (b.year = :startYear AND month_num >= :startMonth))
        AND
        (b.year < :endYear OR (b.year = :endYear AND month_num <= :endMonth))
      )
    GROUP BY b.year, month_num
    ORDER BY b.year, month_num
""")
    fun getMonthlyTotals(
        userId: Int,
        startMonth: Int,
        startYear: Int,
        endMonth: Int,
        endYear: Int
    ): List<MonthlyTotal>


    @Query("SELECT DISTINCT month, year FROM budget WHERE user_id = :userId")
    fun getDistinctMonthYear(userId: Int): List<DistinctMonthYear>




}
