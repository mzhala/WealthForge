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

    @Query("SELECT * FROM categoryBudget WHERE category_id = :categoryId AND year = :year AND month = :month LIMIT 1")
    suspend fun getCategoryBudget(categoryId: Int, year: Int, month: String): CategoryBudget?

    @Query("SELECT COUNT(*) FROM categoryBudget WHERE category_id = :categoryId AND year = :year AND month = :month")
    suspend fun checkCategoryBudgetExists(categoryId: Int, year: Int, month: String): Int
}
