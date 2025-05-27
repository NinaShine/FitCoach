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
    val stepGoal: Int = 0
)
