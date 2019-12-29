package com.example.firstapp.book_corner.item

import android.app.Application
import android.util.Log
import androidx.lifecycle.*
import com.example.firstapp.book_corner.data.Book
import com.example.firstapp.book_corner.data.BookRepository
import com.example.firstapp.book_corner.data.BookToSave
import com.example.firstapp.book_corner.data.local.BookDatabase
import kotlinx.coroutines.launch
import com.example.firstapp.core.*;

class BookEditViewModel(application: Application) : AndroidViewModel(application) {
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

    fun saveOrUpdateItem(item: Book) {
        viewModelScope.launch {
            Log.v(TAG, "saveOrUpdateItem...");

            mutableFetching.value = true
            mutableException.value = null

            val result: Result<Book>
            result = if (item._id.isNotEmpty()) {
                itemRepository.update(item)
            } else {
                val itemToSave = BookToSave(item.title, item.author, item.gene, item.user)
                itemRepository.save(itemToSave)
            }
            when (result) {
                is Result.Success -> {
                    Log.d(TAG, "saveOrUpdateItem succeeded");
                }
                is Result.Error -> {
                    Log.w(TAG, "saveOrUpdateItem failed", result.exception);
                    mutableException.value = result.exception
                }
            }
            mutableCompleted.value = true
            mutableFetching.value = false
        }
    }
}
