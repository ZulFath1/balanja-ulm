package com.example.balanja.data.api.cloudinary

import okhttp3.RequestBody
import retrofit2.http.Body
import retrofit2.http.POST

interface CloudinaryApiService {
    @POST("image/upload")
    suspend fun uploadImage(@Body body: RequestBody): CloudinaryUploadResponse
}
