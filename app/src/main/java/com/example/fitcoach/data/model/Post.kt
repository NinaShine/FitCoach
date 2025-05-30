package com.example.fitcoach.data.model

data class Post(
    val id: String = "",
    val userId: String = "",
    val content: String = "",
    val imageUrl: String = "",
    val likes: List<String> = emptyList(), // UIDs qui ont liké
    val timestamp: Long = 0
)
