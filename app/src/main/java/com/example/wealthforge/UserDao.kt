package com.example.wealthforge.data
import androidx.room.*

@Dao
interface UserDao {

    // Add a new user to the database
    @Insert(onConflict = OnConflictStrategy.ABORT)
    suspend fun insertUser(user: User)

    // Get a user by username and password (used for login)
    @Query("SELECT * FROM users WHERE username = :username AND password = :password")
    suspend fun getUser(username: String, password: String): User?

    @Query("SELECT id FROM users WHERE username = :username AND password = :password")
    suspend fun getUserId(username: String, password: String): Int?

    @Query("SELECT username FROM users WHERE id = :userId")
    suspend fun getUsername(userId: Int): String?

    // Update an existing user
    @Update
    suspend fun updateUser(user: User)
}
