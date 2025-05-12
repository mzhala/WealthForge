package com.example.wealthforge.data

import androidx.room.*

@Dao
interface CategoryDao {

    @Insert(onConflict = OnConflictStrategy.ABORT)
    suspend fun insertCategory(category: Category)


    @Query("SELECT * FROM categories WHERE user_id = :user_id")
    suspend fun getCategoriesByUser(user_id: Int): List<Category>

    @Query("SELECT * FROM categories")
    suspend fun getCategories(): List<Category>
    @Update
    suspend fun updateCategory(category: Category)

    @Delete
    suspend fun deleteCategory(category: Category)

    @Query("DELETE FROM categories WHERE id = :id")
    suspend fun deleteCategoryById(id: Int)

    @Query("SELECT COUNT(*) FROM categories WHERE category_name = :name AND user_id = :user_id")
    suspend fun categoryExists(name: String, user_id: Int): Int

    @Query("SELECT COUNT(*) FROM categories WHERE user_id = :userId AND category_name = :name")
    suspend fun categoryExistsForUser(userId: Int, name: String): Int

    @Query("SELECT COUNT(*) FROM categories WHERE user_id = :user_id")
    suspend fun categoryCountByUserId(user_id: Int): Int

    @Query("SELECT COUNT(*) FROM categories")
    suspend fun categoryCount(): Int

}
