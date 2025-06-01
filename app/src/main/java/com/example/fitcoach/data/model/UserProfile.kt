package com.example.fitcoach.data.model

data class UserProfile(
    val uid: String = "",
    val username: String = "",
    val firstName: String = "",
    val lastName: String = "",
    val avatarUrl: String? = null,
    val goal: String = "",
    val height: Int = 0,
    val weight: Int = 0,
    val weightUnit: String = "",
    val gender: String = "",
    val birthDate: String = "",
    val trainingPlace: String = "",
    val stepGoal: Int = 0,
    val bio: String? = null,
    val notificationsEnabled: Boolean = true,
    val fcmToken : String? = null,
    var friends: MutableList<String> = mutableListOf(),
    val workouts: List<String> = emptyList(),
    val rewards: List<String> = emptyList(),
    val achievements: List<String> = emptyList(),
    val progress: List<Int> = emptyList(),
    val points: Int = 0,
    val location: String = "", // ← Exemple : "maison", "salle", "extérieur"
    val fitnessGoal: String = "", // ← perte de poids, prise de masse…
    val level: String = "" // débutant, intermédiaire…


)
