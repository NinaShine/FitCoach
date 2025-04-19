package com.example.fitcoach

data class UserProfile(
    val username: String = "",
    val email: String = "",
    val createdAt: com.google.firebase.Timestamp? = null
)
