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
        b.monthIndex as month_num,
        COALESCE(SUM(b.amount), 0) as budget,
        COALESCE(SUM(t.amount), 0) as spent
    FROM budget b
    LEFT JOIN transactions t
      ON b.user_id = t.user_id
      AND b.year = t.year
      AND b.monthIndex = t.monthIndex
    WHERE b.user_id = :userId
      AND (
        (b.year > :startYear OR (b.year = :startYear AND b.monthIndex >= :startMonth))
        AND
        (b.year < :endYear OR (b.year = :endYear AND b.monthIndex <= :endMonth))
      )
    GROUP BY b.year, b.monthIndex
    ORDER BY b.year, b.monthIndex
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
