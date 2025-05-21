package com.example.fitcoach.data.model

data class CurrentlyPlaying(
    val title: String,
    val artist: String,
    val imageUrl: String,
    val isPlaying: Boolean,
    val progressMs: Int,
    val durationMs: Int
)