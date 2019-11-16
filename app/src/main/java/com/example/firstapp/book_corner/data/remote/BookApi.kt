package com.example.firstapp.book_corner.data.remote

import com.example.firstapp.book_corner.data.PagedItems
import com.example.firstapp.book_corner.data.Book
import com.example.firstapp.core.Api
import com.google.gson.GsonBuilder
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
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
        suspend fun create(@Body item: Book): Book

        @Headers("Content-Type: application/json")
        @PUT("/api/book/{_id}")
        suspend fun update(@Path("_id") itemId: String, @Body item: Book): Book
    }

    val service: Service = Api.retrofit.create(
        Service::class.java)
}