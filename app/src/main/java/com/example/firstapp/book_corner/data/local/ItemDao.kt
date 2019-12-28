package com.example.firstapp.book_corner.data.local

import androidx.lifecycle.LiveData
import androidx.room.*
import com.example.firstapp.book_corner.data.Book

@Dao
interface ItemDao {
    @Query("SELECT * from books")
    fun getAll(): LiveData<List<Book>>

    @Query("SELECT * FROM books WHERE _id=:id ")
    fun getById(id: String): LiveData<Book>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(book: Book)

    @Update(onConflict = OnConflictStrategy.REPLACE)
    suspend fun update(book: Book)

    @Query("DELETE FROM books WHERE _id=:id")
    suspend fun delete(id: String)

    @Query("DELETE FROM books")
    suspend fun deleteAll()
}