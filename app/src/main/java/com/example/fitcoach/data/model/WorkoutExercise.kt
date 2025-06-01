package com.example.fitcoach.data.model

data class WorkoutExercise(
    val id: String,
    val name: String,
    val bodyPart: String,
    val equipment: String,
    val gifUrl: String,
    val target: String,
    val series: Int = 0,
    val repetitions: Int = 0,
    val instructions: List<String> = emptyList(),
    val kg: Int? = null,
    val weight: Int? = null,
    val restTime: Int? = null,
    val isCompleted: Boolean = false,
    val completedSeries: Int = 0
)