package com.example.submission2.data.source.remote.service

import com.example.submission2.data.source.remote.response.DataResponse
import okhttp3.MultipartBody
import okhttp3.RequestBody
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.Multipart
import retrofit2.http.POST
import retrofit2.http.Part
import retrofit2.http.PartMap
import retrofit2.http.Path
import retrofit2.http.QueryMap

interface ApiService {
    /**
     * Call Register API with POST Method
     *
     * Data:
     * - requestBody as RequestBody:
     *      - name: String
     *      - email: String
     *      - password: String
     */
    @POST("register")
    suspend fun register(
        @Body requestBody: RequestBody
    ): DataResponse

    /**
     * Call Login API with POST Method
     *
     * Data:
     * - requestBody as RequestBody:
     *      - email: String
     *      - password: String
     */
    @POST("login")
    suspend fun login(
        @Body requestBody: RequestBody
    ): DataResponse

    /**
     * Call Add Story API with POST Method
     *
     * Headers:
     * - Content-Type: multipart/form-data
     * - Authorization: Bearer token
     *
     * Data:
     * - file as MultipartBody.Part (max 1 MB)
     * - params as Map<String, RequestBody>:
     *      - description: String
     *      - lat: Float (optional)
     *      - lon: Float (optional)
     */
    @Multipart
    @POST("stories")
    @JvmSuppressWildcards
    suspend fun story(
        @Header("Authorization") token: String,
        @Part file: MultipartBody.Part,
        @PartMap params: Map<String, RequestBody>,
    ): DataResponse

    /**
     * Call Get All Story API with GET Method
     *
     * Headers:
     * - Authorization: Bearer token
     *
     * Data:
     * - params as Map<String, String>:
     *      - location: Int (1 | 0; default=0; optional)
     *      - page: Int (optional)
     *      - size: Int (optional)
     */
    @GET("stories")
    suspend fun story(
        @Header("Authorization") token: String,
        @QueryMap params: Map<String, String>
    ): DataResponse

    /**
     * Call Get Detail Story API with GET Method
     *
     * Headers:
     * - Authorization: Bearer token
     *
     * Data:
     * - id: Int
     */
    @GET("stories/{id}")
    suspend fun story(
        @Header("Authorization") token: String,
        @Path("id") id: Int
    ): DataResponse
}