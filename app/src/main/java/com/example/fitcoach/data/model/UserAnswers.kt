package com.example.fitcoach.data.model

data class UserAnswers(
    var goal: String = "",
    var height: Int = 0,
    var weight: Int = 0,
    var weightUnit: String = "",
    var gender: String = "",
    var birthDate: String = "",
    var trainingPlace: String = "",
    var stepGoal: Int = 0,
    var firstName: String = "",
    var lastName: String = "",
    var avatarUrl: String? = null,
    var username: String = ""
)

fun UserAnswers.toUserProfile(): UserProfile {
    return UserProfile(
        uid = "",
        firstName = firstName,
        lastName = lastName,
        avatarUrl = avatarUrl,
        goal = goal,
        height = height,
        weight = weight,
        weightUnit = weightUnit,
        gender = gender,
        birthDate = birthDate,
        trainingPlace = trainingPlace,
        stepGoal = stepGoal,
        username = username
    )
}
