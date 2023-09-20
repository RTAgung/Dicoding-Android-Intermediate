package com.example.submission1.utils

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
}