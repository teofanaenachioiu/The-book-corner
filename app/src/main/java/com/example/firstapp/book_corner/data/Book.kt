package com.example.firstapp.book_corner.data

data class Book(
    val _id: String,
    var title: String,
    var author: String,
    var gene: String,
    var user: String
) {
    override fun toString(): String = title
}
