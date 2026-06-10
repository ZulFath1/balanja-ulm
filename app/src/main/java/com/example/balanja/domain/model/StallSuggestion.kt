package com.example.balanja.domain.model

data class StallSuggestion(
    val id: String = "",
    val name: String = "",
    val locationDescription: String = "",
    val submittedBy: String = "",
    val submittedByName: String = "",
    val status: String = "pending",
    val createdAt: Long = 0L
)
