package com.example.firstapp.auth.data.remote

import com.example.firstapp.auth.data.TokenHolder
import com.example.firstapp.auth.data.User
import com.example.firstapp.core.Api
import retrofit2.http.Body
import retrofit2.http.Headers
import retrofit2.http.POST
import com.example.firstapp.core.Result
import java.lang.Exception

object AuthApi {
    interface AuthService {
        @Headers("Content-Type: application/json")
        @POST("/api/auth/login")
        suspend fun login(@Body user: User): TokenHolder
    }

    private val authService: AuthService = Api.retrofit.create(AuthService::class.java)

    suspend fun login(user: User): Result<TokenHolder> {
        try {
            return Result.Success(authService.login(user))
        } catch(e: Exception) {
            return Result.Error(e)
        }
    }
}