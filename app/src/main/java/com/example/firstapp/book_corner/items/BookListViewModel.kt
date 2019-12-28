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

    val itemRepository: BookRepository

    init {
        val itemDao = BookDatabase.getDatabase(application, viewModelScope).itemDao()
        itemRepository = BookRepository(itemDao)
        items = itemRepository.items
    }

    fun refresh() {
        viewModelScope.launch {
            Log.v(TAG, "refresh...");
            mutableLoading.value = true
            mutableException.value = null
            when (val result = itemRepository.refresh()) {
                is Result.Success -> {
                    Log.d(TAG, "refresh succeeded");
                }
                is Result.Error -> {
                    Log.w(TAG, "refresh failed", result.exception);
                    mutableException.value = result.exception
                }
            }
            mutableLoading.value = false
        }
    }
}