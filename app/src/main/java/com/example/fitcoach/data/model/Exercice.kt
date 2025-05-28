package com.example.fitcoach.data.model

data class Exercise(
    val id: String,
    val name: String,
    val bodyPart: String,
    val equipment: String,
    val gifUrl: String,
    val target: String,
    val instructions: List<String> = emptyList()
)

// Modèle pour les instructions locales
data class ExerciseInstructions(
    val exerciseId: String,
    val instructions: List<String>
)