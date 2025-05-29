package com.example.fitcoach.data.model

data class Workout(
    val name: String = "",
    val duration: Int = 0,
    val timestamp: Long = 0L,
    val exercises: List<Exercise> = emptyList()
)


