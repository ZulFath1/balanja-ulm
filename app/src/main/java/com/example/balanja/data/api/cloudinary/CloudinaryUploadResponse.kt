package com.example.balanja.data.api.cloudinary

import com.google.gson.annotations.SerializedName

data class CloudinaryUploadResponse(
    @SerializedName("secure_url") val secureUrl: String,
    @SerializedName("public_id") val publicId: String,
    @SerializedName("format") val format: String,
    @SerializedName("bytes") val bytes: Long
)
