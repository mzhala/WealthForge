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

    @Query("SELECT COUNT(*) FROM categoryBudget WHERE user_id= :user_id AND category_name = :category_name AND year = :year AND month = :month")
    suspend fun checkCategoryBudgetExists(user_id: Int, category_name: String, year: Int, month: String): Int
}
