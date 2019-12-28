package com.example.firstapp.book_corner.chart

import android.app.Application
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.viewModelScope
import com.example.firstapp.book_corner.data.Book
import com.example.firstapp.book_corner.data.BookRepository
import com.example.firstapp.book_corner.data.local.BookDatabase
import com.example.firstapp.core.TAG
import com.github.mikephil.charting.data.PieEntry

class ChartViewModel(application: Application) : AndroidViewModel(application) {
    val items: LiveData<List<Book>> =
        BookDatabase.getDatabase(application, viewModelScope).itemDao().getAll()

    fun getPieEntries(booksgene: Array<String>): List<PieEntry> {
        val entries = ArrayList<PieEntry>()

        val books: List<Book>?
        books = items.value
        if (books != null) {
            val sizee = books.size
            Log.v(TAG, "Size of books gene list $sizee")
            for (gene in booksgene) {
                val value = books.count { b -> b.gene == gene }
                if (value != 0) {
                    entries.add(PieEntry(value.toFloat(), gene))
                }
            }
        }
        return entries
    }
}
