package com.example.core.data.local.room

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "books")
data class BookEntity(
    @PrimaryKey
    val id: String,
    val title: String,
    val author: String,
    val fileUrl: String,
    val localFilePath: String?,
    val coverPath: String?,
    val readingProgress: Float = 0f
)