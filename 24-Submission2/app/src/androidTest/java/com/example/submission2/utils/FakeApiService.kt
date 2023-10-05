package com.example.submission2.utils

import com.example.submission2.data.source.remote.response.DataResponse
import com.example.submission2.data.source.remote.response.StoryResponse
import com.example.submission2.data.source.remote.service.ApiService
import okhttp3.MultipartBody
import okhttp3.RequestBody

class FakeApiService : ApiService {
    override suspend fun register(requestBody: RequestBody): DataResponse = DataResponse()

    override suspend fun login(requestBody: RequestBody): DataResponse = DataResponse()

    override suspend fun story(
        token: String,
        file: MultipartBody.Part,
        params: Map<String, RequestBody>
    ): DataResponse = DataResponse()

    override suspend fun story(token: String, params: Map<String, String>): DataResponse {
        val items: MutableList<StoryResponse> = arrayListOf()
        for (i in 0..100) {
            val story = StoryResponse(
                id = i.toString(),
                name = "name $i",
                description = "description $i",
                photoUrl = "photoUrl $i",
                createdAt = "createdAt $i",
                lat = i.toDouble(),
                lon = i.toDouble()
            )
            items.add(story)
        }

        val page = params["page"]?.toInt() ?: 0
        val size = params["size"]?.toInt() ?: 0

        return DataResponse(
            listStory = items.subList((page - 1) * size, (page - 1) * size + size)
        )
    }

    override suspend fun story(token: String, id: Int): DataResponse = DataResponse()

    fun createStoryQueryMap(
        page: Int = 1,
        size: Int = 100,
        location: Int = 0
    ): Map<String, String> = mapOf(
        "page" to page.toString(),
        "size" to size.toString(),
        "location" to location.toString()
    )
}
