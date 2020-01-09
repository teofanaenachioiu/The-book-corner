package com.example.firstapp.book_corner.data

import android.util.Log
import androidx.lifecycle.LiveData
import com.example.firstapp.auth.data.AuthRepository
import com.example.firstapp.core.*;
import com.example.firstapp.book_corner.data.local.ItemDao
import com.example.firstapp.book_corner.data.remote.BookApi


class BookRepository(private val itemDao: ItemDao) {
    var items = itemDao.getAll()

//    init {
//        println("S-a initializat repoul")
//        WebSocketProvider.initRepo(this)
//    }

    suspend fun refresh(): Result<Boolean> {
        try {
            if (AuthRepository.isLoggedIn) {
                Log.v(TAG, "User logged in")
                Log.v(TAG, "Get all items from server")
                val items = BookApi.service.find()
                Log.v(TAG, "Done with loading items from server")
                itemDao.deleteAll();
                for (item in items) {
                    itemDao.insert(item)
                }
            }
            items = itemDao.getAll();
            return Result.Success(true)
        } catch (e: Exception) {
            return Result.Error(e)
        }
    }


    fun getById(itemId: String): LiveData<Book> {
        return itemDao.getById(itemId)
    }

    suspend fun save(item: BookToSave): Result<Book> {
        return try {
            val createdItem = BookApi.service.create(item)
            itemDao.insert(createdItem)
            Result.Success(createdItem)
        } catch (e: Exception) {
            Log.v(TAG, "Eroare la save")
            Result.Error(e)
        }
    }

    suspend fun update(item: Book): Result<Book> {
        return try {
            val updatedItem = BookApi.service.update(item)
            itemDao.update(updatedItem)
            Result.Success(updatedItem)
        } catch (e: Exception) {
            Result.Error(e)
        }
    }

    suspend fun delete(id: String): Result<Boolean> {
        return try {
            val isDelete = BookApi.service.delete(id)

            if (isDelete) {
                itemDao.delete(id);
                Result.Success(true)
            } else {
                Result.Success(false)
            }

        } catch (e: Exception) {
            Result.Error(e)
        }
    }

    suspend fun refreshLocally(books: List<Book>) {
        itemDao.deleteAll()
        for (book in books) {
            itemDao.insert(book)
        }
    }

}