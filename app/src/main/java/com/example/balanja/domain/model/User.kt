package com.example.balanja.domain.model

data class User(
    val id: String = "",
    val name: String = "",
    val email: String = "",
    val role: String = "",
    val reviewCount: Int = 0,
    val createdAt: Long = 0L
)
