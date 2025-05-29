package com.example.fitcoach.data.model

import java.util.*

data class Routine(
    val id: String = UUID.randomUUID().toString(),
    val title: String,
    val exercises: List<WorkoutExercise>
)
