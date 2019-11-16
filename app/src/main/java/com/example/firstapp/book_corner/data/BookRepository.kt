package com.example.firstapp.book_corner.data

import com.example.firstapp.auth.data.AuthRepository
import com.example.firstapp.book_corner.data.remote.BookApi


object BookRepository {
    private var cachedItems: MutableList<Book>? = null;

    suspend fun loadAll(): List<Book> {
        if (cachedItems != null) {
            return cachedItems as List<Book>;
        }
        cachedItems = mutableListOf()

        var pagedItems = BookApi.service.find();

        if(AuthRepository.isLoggedIn){
            val user = AuthRepository.user!!.username;
            pagedItems = BookApi.service.findByUser(user);
        }
        cachedItems?.addAll(pagedItems)
        return cachedItems as List<Book>
    }

    suspend fun load(itemId: String): Book {
        val item = cachedItems?.find { it._id == itemId }
        if (item != null) {
            return item
        }
        return BookApi.service.read(itemId)
    }

    suspend fun save(item: Book): Book {
        val createdItem = BookApi.service.create(item)
        cachedItems?.add(createdItem)
        return createdItem
    }

    suspend fun update(item: Book): Book {
        val updatedItem = BookApi.service.update(item._id, item)
        val index = cachedItems?.indexOfFirst { it._id == item._id }
        if (index != null) {
            cachedItems?.set(index, updatedItem)
        }
        return updatedItem
    }

}