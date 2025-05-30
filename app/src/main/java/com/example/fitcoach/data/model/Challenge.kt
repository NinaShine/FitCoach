package com.example.fitcoach.data.model

data class Challenge(
    val id: String = "",
    val userId: String = "",
    val title: String = "",
    val description: String = "",
    val rewardPoints: Int = 0,
    val durationDays: Int = 0,
    val timestamp: Long = 0L,
    val participants: List<String> = emptyList()
)
