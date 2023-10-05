package com.example.submission2.utils

import com.example.submission2.data.model.Story
import com.example.submission2.data.model.User
import com.example.submission2.data.source.local.entity.StoryEntity
import com.example.submission2.data.source.remote.response.ErrorResponse
import com.example.submission2.data.source.remote.response.LoginResultResponse
import com.example.submission2.data.source.remote.response.StoryResponse
import com.google.android.gms.maps.model.LatLng
import com.google.gson.Gson
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.MultipartBody
import okhttp3.RequestBody
import okhttp3.RequestBody.Companion.asRequestBody
import okhttp3.RequestBody.Companion.toRequestBody
import retrofit2.HttpException
import java.io.File

object Mapping {
    fun getErrorApiResponse(e: HttpException): ErrorResponse {
        val errorBody = e.response()?.errorBody()?.string()
        return Gson().fromJson(errorBody, ErrorResponse::class.java)
    }

    fun createFileMultipartBody(imageFile: File): MultipartBody.Part {
        val requestImageFile = imageFile.asRequestBody("image/jpeg".toMediaType())
        return MultipartBody.Part.createFormData(
            "photo",
            imageFile.name,
            requestImageFile
        )
    }

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

    fun createStoryQueryMap(
        page: Int = 1,
        size: Int = 100,
        location: Int = 0
    ): Map<String, String> = mapOf(
        "page" to page.toString(),
        "size" to size.toString(),
        "location" to location.toString()
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

    fun storyResponseToStoryEntity(storyResponse: StoryResponse?): StoryEntity {
        return StoryEntity(
            id = storyResponse?.id ?: "",
            name = storyResponse?.name,
            description = storyResponse?.description,
            photoUrl = storyResponse?.photoUrl,
            createdAt = storyResponse?.createdAt,
            lat = storyResponse?.lat,
            lon = storyResponse?.lon,
        )
    }

    fun storyResponseToStoryEntity(listStoryResponse: List<StoryResponse?>?): List<StoryEntity> =
        listStoryResponse?.map { storyResponse ->
            storyResponseToStoryEntity(storyResponse)
        } ?: ArrayList()

    fun storyEntityToStory(storyEntity: StoryEntity?): Story {
        return Story(
            id = storyEntity?.id ?: "",
            name = storyEntity?.name ?: "",
            description = storyEntity?.description ?: "",
            photoUrl = storyEntity?.photoUrl ?: "",
            createdAt = storyEntity?.createdAt ?: "",
            lat = storyEntity?.lat ?: 0.0,
            lon = storyEntity?.lon ?: 0.0,
        )
    }

    fun storyEntityToStory(listStoryEntity: List<StoryEntity?>?): List<Story> =
        listStoryEntity?.map { storyEntity ->
            storyEntityToStory(storyEntity)
        } ?: ArrayList()

    fun createUploadMapRequestBody(
        desc: String,
        location: LatLng?
    ): Map<String, RequestBody> {
        val descRequestBody = desc.toRequestBody("text/plain".toMediaType())
        return if (location != null) {
            val latRequestBody =
                location.latitude.toString().toRequestBody("text/plain".toMediaType())
            val lonRequestBody =
                location.longitude.toString().toRequestBody("text/plain".toMediaType())
            mapOf(
                "description" to descRequestBody,
                "lat" to latRequestBody,
                "lon" to lonRequestBody,
            )
        } else mapOf(
            "description" to descRequestBody,
        )
    }
}