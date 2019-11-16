package com.example.firstapp.auth.data

import com.example.firstapp.auth.data.remote.AuthApi
import com.example.firstapp.core.Api
import com.example.firstapp.core.Result

object AuthRepository {
    var user: User? = null
        private set

    val isLoggedIn: Boolean
        get() = user != null

    init {
        user = null
    }

    fun logout() {
        user = null
        Api.tokenInterceptor.token = null
    }

    suspend fun login(username: String, password: String): Result<TokenHolder> {
        val user = User(username, password)
        val result = AuthApi.login(user)

        if (result is Result.Success<TokenHolder>) {
            setLoggedInUser(user, result.data)
        }
        return result
    }

    private fun setLoggedInUser(user: User, tokenHolder: TokenHolder) {
        AuthRepository.user = user
        Api.tokenInterceptor.token = tokenHolder.token
    }
}
