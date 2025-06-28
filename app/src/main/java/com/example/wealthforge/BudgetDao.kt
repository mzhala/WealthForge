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
        b.monthIndex AS month_num,
        b.amount AS budget,
        COALESCE(SUM(t.amount), 0) AS spent,
        COALESCE(cb.total_category_budget, 0) AS category_budget_total
    FROM (
        SELECT user_id, year, monthIndex, SUM(amount) AS amount
        FROM budget
        WHERE user_id = :userId
          AND (
            (year > :startYear OR (year = :startYear AND monthIndex >= :startMonth))
            AND
            (year < :endYear OR (year = :endYear AND monthIndex <= :endMonth))
          )
        GROUP BY user_id, year, monthIndex
    ) b
    LEFT JOIN transactions t
      ON b.user_id = t.user_id
      AND b.year = t.year
      AND b.monthIndex = t.monthIndex

    LEFT JOIN (
        SELECT user_id, year, monthIndex, SUM(amount) AS total_category_budget
        FROM categorybudget
        WHERE user_id = :userId
        GROUP BY user_id, year, monthIndex
    ) cb
      ON b.user_id = cb.user_id
      AND b.year = cb.year
      AND b.monthIndex = cb.monthIndex

    GROUP BY b.year, b.monthIndex, b.amount, cb.total_category_budget
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
