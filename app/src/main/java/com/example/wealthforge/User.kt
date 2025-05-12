package com.example.wealthforge.data

import androidx.room.Entity
import androidx.room.PrimaryKey
import androidx.room.ColumnInfo
import androidx.room.Index

@Entity(
    tableName = "users",
    indices = [Index(value = ["username"], unique = true)] // Enforce unique username
)
data class User(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0, // Auto-incrementing primary key

    val username: String, // Must be unique and not null

    val password: String, // Required field, cannot be null

    val name: String?, // First name is optional

    val surname: String?, // Last name is also optional

    val profilePicture: String? // Path or URI to the profile picture, optional
)
