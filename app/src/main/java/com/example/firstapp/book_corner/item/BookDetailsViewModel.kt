package com.example.firstapp.book_corner.item

import android.app.Application
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.example.firstapp.book_corner.data.Book
import com.example.firstapp.book_corner.data.BookRepository
import com.example.firstapp.book_corner.data.local.BookDatabase
import com.example.firstapp.core.Result
import com.example.firstapp.core.TAG
import kotlinx.coroutines.launch

class BookDetailsViewModel(application: Application) : AndroidViewModel(application) {
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

    fun deleteItem(id: String) {
        viewModelScope.launch {
            Log.v(TAG, "delete Item wit id $id...");

            mutableFetching.value = true
            mutableException.value = null

            when (val result = itemRepository.delete(id)) {
                is Result.Success -> {
                    Log.d(TAG, "delete succeeded");
                }
                is Result.Error -> {
                    Log.w(TAG, "delete failed", result.exception);
                    mutableException.value = result.exception
                }
            }
            mutableCompleted.value = true
            mutableFetching.value = false
        }
    }
}
