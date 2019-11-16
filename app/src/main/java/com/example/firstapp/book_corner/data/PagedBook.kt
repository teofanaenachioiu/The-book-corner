package com.example.firstapp.book_corner.data

import com.example.firstapp.book_corner.data.Book

data class PagedItems(
    val page: Int,
    val items: List<Book>,
    val more: Boolean) {}
