package com.example.submission1.utils

import com.example.submission1.data.model.User
import com.example.submission1.data.source.remote.response.LoginResult
import com.google.gson.Gson
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.RequestBody
import okhttp3.RequestBody.Companion.toRequestBody

object Mapping {
    fun createRegisterRequestBody(name: String, email: String, password: String): RequestBody {
        return Gson()
            .toJson(
                mapOf(
                    "name" to name,
                    "email" to email,
                    "password" to password
                )
            )
            .toRequestBody("application/json; charset=utf-8".toMediaType())
    }

    fun createLoginRequestBody(email: String, password: String): RequestBody {
        return Gson()
            .toJson(
                mapOf(
                    "email" to email,
                    "password" to password
                )
            )
            .toRequestBody("application/json; charset=utf-8".toMediaType())
    }

    fun loginResultToUser(data: LoginResult): User =
        User(userId = data.userId ?: "", name = data.name ?: "", token = data.token ?: "")
}