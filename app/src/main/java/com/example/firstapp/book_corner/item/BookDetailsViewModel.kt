package com.example.firstapp.book_corner.item

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.firstapp.book_corner.data.Book
import com.example.firstapp.book_corner.data.BookRepository
import com.example.firstapp.core.TAG
import kotlinx.coroutines.launch

class BookDetailsViewModel : ViewModel() {
    private val mutableItem = MutableLiveData<Book>().apply { value =
        Book("", "", "", "", "")
    }
    private val mutableFetching = MutableLiveData<Boolean>().apply { value = false }
    private val mutableCompleted = MutableLiveData<Boolean>().apply { value = false }
    private val mutableException = MutableLiveData<Exception>().apply { value = null }

    val book: LiveData<Book> = mutableItem
    val fetching: LiveData<Boolean> = mutableFetching
    val fetchingError: LiveData<Exception> = mutableException
    val completed: LiveData<Boolean> = mutableCompleted

    fun loadItem(itemId: String) {
        viewModelScope.launch {
            Log.v(TAG, "loadItem...");
            mutableFetching.value = true
            mutableException.value = null
            try {
                mutableItem.value = BookRepository.load(itemId)
                Log.d(TAG, "loadItem succeeded");
                mutableFetching.value = false
            } catch (e: Exception) {
                Log.w(TAG, "loadItem failed", e);
                mutableException.value = e
                mutableFetching.value = false
            }
        }
    }
}
