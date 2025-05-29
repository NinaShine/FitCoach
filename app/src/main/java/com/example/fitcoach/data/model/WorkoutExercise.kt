package com.example.fitcoach.data.model

data class WorkoutExercise(
    val id: String,
    val name: String,
    val bodyPart: String,
    val equipment: String,
    val gifUrl: String,
    val target: String,
    val series: Int = 0,         // Valeur par défaut = 0
    val repetitions: Int = 0,    // Valeur par défaut = 0
    val instructions: List<String> = emptyList(),
    val kg: Int? = null,
    val weight: Int? = null, // Poids optionnel pour calculer le volume
    val restTime: Int? = null, // Temps de repos optionnel en secondes
    val isCompleted: Boolean = false, // Pour marquer si l'exercice est terminé
    val completedSeries: Int = 0 // Nombre de séries terminées
)