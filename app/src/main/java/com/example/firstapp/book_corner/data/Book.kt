package com.example.firstapp.book_corner.data

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "books")
data class Book(
    @PrimaryKey @ColumnInfo(name = "_id") val _id: String,
    @ColumnInfo(name = "title") var title: String,
    @ColumnInfo(name = "author") var author: String,
    @ColumnInfo(name = "gene") var gene: String,
    @ColumnInfo(name = "user") var user: String
)
