package com.example.wealthforge.data

import androidx.room.*
@Dao
interface CategoryBudgetDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertCategoryBudget(categoryBudget: CategoryBudget)

    @Delete
    suspend fun deleteCategoryBudget(categoryBudget: CategoryBudget)

    @Update
    suspend fun updateCategoryBudget(categoryBudget: CategoryBudget)

    @Query("SELECT * FROM categoryBudget WHERE user_id= :user_id AND category_name = :category_name AND year = :year AND month = :month LIMIT 1")
    suspend fun getCategoryBudget(user_id: Int,category_name: String, year: Int, month: String): CategoryBudget?

    @Query("SELECT * FROM categoryBudget WHERE user_id= :user_id AND year = :year AND month = :month LIMIT 1")
    suspend fun getCategoryBudgetByUser(user_id: Int, year: Int, month: String): CategoryBudget?

    @Query("SELECT * FROM categoryBudget WHERE user_id= :user_id AND year = :year AND month = :month")
    suspend fun getCategoryBudgetsByUser(user_id: Int, year: Int, month: String): List<CategoryBudget>

    @Query("SELECT COUNT(*) FROM categoryBudget WHERE user_id= :user_id AND year = :year AND month = :month LIMIT 1")
    suspend fun getCategoryBudgetCountByUser(user_id: Int, year: Int, month: String): Int

    @Query("SELECT SUM(amount) FROM categoryBudget WHERE user_id= :user_id AND year = :year AND month = :month LIMIT 1")
    suspend fun getTotalCategoryBudgetCountByUser(user_id: Int, year: Int, month: String): Double?

    @Query("SELECT COUNT(*) FROM categoryBudget WHERE user_id= :user_id AND category_name = :category_name AND year = :year AND month = :month")
    suspend fun checkCategoryBudgetExists(user_id: Int, category_name: String, year: Int, month: String): Int

    @Query("SELECT COUNT(*) FROM categoryBudget WHERE user_id = :userId AND category_name = :name AND year =:year AND month = :month")
    suspend fun categoryBudgetExistsForUser(userId: Int, name: String, year: Int, month: String): Int

    @Query("SELECT SUM(amount) FROM categoryBudget WHERE user_id = :userId AND category_name = :name AND year =:year AND month = :month")
    suspend fun categoryExpenseTotal(userId: Int, name: String, year: Int, month: String): Double

    // Delete category from budgets for a specific user
    @Query("DELETE FROM categoryBudget WHERE id = :id")
    suspend fun deleteCategoryBudgetById(id: Int)

    @Query("""
    SELECT SUM(cb.amount) 
    FROM categoryBudget cb
    INNER JOIN categories c 
        ON cb.user_id = c.user_id AND cb.category_name = c.category_name
    WHERE cb.user_id = :userId 
      AND cb.month = :month 
      AND cb.year = :year 
      AND c.type = :type
""")
    suspend fun getTotalCategoryBudgetAmountByType(
        userId: Int,
        month: String,
        year: Int,
        type: String // e.g., "Expense" or "Income"
    ): Double?


}
