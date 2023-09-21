package com.example.submission1.utils

import com.example.submission1.data.model.Story
import com.example.submission1.data.model.User
import com.example.submission1.data.source.remote.response.LoginResultResponse
import com.example.submission1.data.source.remote.response.StoryResponse
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

    fun createStoryQueryMap(): Map<String, String> = mapOf(
        "location" to "1"
    )

    fun loginResultResponseToUser(data: LoginResultResponse): User =
        User(userId = data.userId ?: "", name = data.name ?: "", token = data.token ?: "")

    fun storyResponseToStory(storyResponse: StoryResponse?): Story {
        return Story(
            id = storyResponse?.id ?: "",
            name = storyResponse?.name ?: "",
            description = storyResponse?.description ?: "",
            photoUrl = storyResponse?.photoUrl ?: "",
            createdAt = storyResponse?.createdAt ?: "",
            lat = storyResponse?.lat ?: 0.0,
            lon = storyResponse?.lon ?: 0.0,
        )
    }

    fun storyResponseToStory(listStoryResponse: List<StoryResponse?>?): List<Story> =
        listStoryResponse?.map { storyResponse ->
            storyResponseToStory(storyResponse)
        } ?: ArrayList()
}