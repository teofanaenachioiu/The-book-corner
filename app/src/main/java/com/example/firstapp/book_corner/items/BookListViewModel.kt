package com.example.firstapp.book_corner.items

import android.app.Application
import android.util.Log
import androidx.lifecycle.*
import com.example.firstapp.book_corner.data.Book
import com.example.firstapp.book_corner.data.BookRepository
import com.example.firstapp.book_corner.data.local.BookDatabase
import kotlinx.coroutines.launch
import com.example.firstapp.core.*;

class BookListViewModel(application: Application) : AndroidViewModel(application) {

    private val mutableLoading = MutableLiveData<Boolean>().apply { value = false }
    private val mutableException = MutableLiveData<Exception>().apply { value = null }

    val items: LiveData<List<Book>>
    val loading: LiveData<Boolean> = mutableLoading
    val loadingError: LiveData<Exception> = mutableException

    val bookRepository: BookRepository

    // we init the list items using items from repository
    init {
        val itemDao = BookDatabase.getDatabase(application, viewModelScope).itemDao()
        bookRepository = BookRepository(itemDao)
        items = bookRepository.items
    }

    // we call this function when we want to refresh items from list
    fun refresh() {
        viewModelScope.launch {

            Log.v(TAG, "refresh items from list...");

            mutableLoading.value = true
            mutableException.value = null

            when (val result = bookRepository.refresh()) {
                is Result.Success -> {
                    Log.d(TAG, "refresh items succeeded");
                }
                is Result.Error -> {
                    Log.w(TAG, "refresh items failed", result.exception);
                    mutableException.value = result.exception
                }
            }
            mutableLoading.value = false
        }
    }
}