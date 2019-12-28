package com.example.firstapp.book_corner.item

import android.app.Application
import android.util.Log
import androidx.lifecycle.*
import com.example.firstapp.book_corner.data.Book
import com.example.firstapp.book_corner.data.BookRepository
import com.example.firstapp.book_corner.data.local.BookDatabase
import com.example.firstapp.core.TAG

class BookDetailsViewModel (application: Application) : AndroidViewModel(application) {
    private val mutableFetching = MutableLiveData<Boolean>().apply { value = false }
    private val mutableCompleted = MutableLiveData<Boolean>().apply { value = false }
    private val mutableException = MutableLiveData<Exception>().apply { value = null }

    val fetching: LiveData<Boolean> = mutableFetching
    val fetchingError: LiveData<Exception> = mutableException
    val completed: LiveData<Boolean> = mutableCompleted

    val itemRepository: BookRepository

    init {
        val itemDao = BookDatabase.getDatabase(application, viewModelScope).itemDao()
        itemRepository = BookRepository(itemDao)
    }

    fun getItemById(itemId: String): LiveData<Book> {
        Log.v(TAG, "getItemById...")
        return itemRepository.getById(itemId)
    }
}
