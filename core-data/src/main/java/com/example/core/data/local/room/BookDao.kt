package com.example.core.data.local.room

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

@Dao
interface BookDao {
    @Insert(onConflict = OnConflictStrategy.Companion.REPLACE)
    suspend fun insert(book: BookEntity)

    @Query("UPDATE books SET localFilePath = :localFilePath, coverPath = :coverPath, readingProgress = :readingProgress WHERE id = :bookId")
    suspend fun updateBook(
        bookId: String,
        localFilePath: String?,
        coverPath: String?,
        readingProgress: Float
    )

    @Query("SELECT * FROM books")
    fun getAllBooks(): Flow<List<BookEntity>>

    @Query("SELECT * FROM books")
    suspend fun getAllBooksOnce(): List<BookEntity>

    @Query("SELECT * FROM books WHERE id = :id")
    suspend fun getBookById(id: String): BookEntity

    @Query("DELETE FROM books WHERE id = :bookId")
    suspend fun deleteBookById(bookId: String)

    @Query("SELECT * FROM books WHERE localFilePath IS NOT NUll AND (title LIKE '%' || :query || '%' OR author LIKE '%' || :query || '%') ")
    fun searchDownloadedBooks(query: String): Flow<List<BookEntity>>

    @Query("UPDATE books SET readingProgress = :progress WHERE id = :bookId")
    suspend fun updateReadingProgress(bookId: String, progress: Float)

    @Query("DELETE FROM books")
    suspend fun clear()
}