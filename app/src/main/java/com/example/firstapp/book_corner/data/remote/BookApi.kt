package com.example.firstapp.book_corner.data.remote

import com.example.firstapp.book_corner.data.Book
import com.example.firstapp.book_corner.data.BookToSave
import com.example.firstapp.core.Api
import retrofit2.http.*


object BookApi {
    interface Service {
        @GET("/api/book")
        suspend fun find(): List<Book>

        @GET("/api/book/user/{user}")
        suspend fun findByUser(@Path("user") user: String): List<Book>

        @GET("/api/book/{_id}")
        suspend fun read(@Path("_id") itemId: String): Book;

        @Headers("Content-Type: application/json")
        @POST("/api/book")
        suspend fun create(@Body item: BookToSave): Book

        @Headers("Content-Type: application/json")
        @PUT("/api/book")
        suspend fun update(@Body item: Book): Book

        @DELETE("/api/book/{id}")
        suspend fun delete(@Path("id") id: String): Boolean
    }

    val service: Service = Api.retrofit.create(
        Service::class.java)
}