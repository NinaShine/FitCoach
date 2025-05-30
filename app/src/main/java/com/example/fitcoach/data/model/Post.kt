package com.example.fitcoach.data.model

data class Post(
    val id: String = "",
    val userId: String = "", //lui
    val content: String = "",//lui
    val imageUrl: String = "",//lui
    val likes: List<String> = emptyList(), // UIDs qui ont liké   lui
    val timestamp: Long = 0,//lui
    val comments: List<Comment> = emptyList() // lui
)
