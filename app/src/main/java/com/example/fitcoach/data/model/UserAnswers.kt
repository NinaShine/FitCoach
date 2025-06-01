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
    var username: String = "",
    var bio: String? = null,
    var notificationsEnabled: Boolean = true,
    var fcmToken: String? = null,
    var friends: List<String> = emptyList(),
    var workouts: List<String> = emptyList(),
    var rewards: List<String> = emptyList(),
    var achievements: List<String> = emptyList(),
    var progress: List<Int> = emptyList()
) {
    companion object {
        fun fromUserProfile(profile: UserProfile): UserAnswers {
            return UserAnswers(
                goal = profile.goal ?: "",
                height = profile.height ?: 0,
                weight = profile.weight ?: 0,
                weightUnit = profile.weightUnit ?: "",
                gender = profile.gender ?: "",
                birthDate = profile.birthDate ?: "",
                trainingPlace = profile.trainingPlace ?: "",
                stepGoal = profile.stepGoal ?: 0,
                firstName = profile.firstName ?: "",
                lastName = profile.lastName ?: "",
                avatarUrl = profile.avatarUrl,
                username = profile.username ?: "",
                bio = profile.bio,
                notificationsEnabled = profile.notificationsEnabled ?: true,
                fcmToken = profile.fcmToken,
                friends = profile.friends ?: emptyList(),
                workouts = profile.workouts ?: emptyList(),
                rewards = profile.rewards ?: emptyList(),
                achievements = profile.achievements ?: emptyList(),
                progress = profile.progress ?: emptyList()
            )
        }
    }
}

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
        username = username,
        bio = bio,
        notificationsEnabled = notificationsEnabled,
        fcmToken = fcmToken,
        friends = friends.toMutableList(),
        workouts = workouts,
        rewards = rewards,
        achievements = achievements,
        progress = progress
    )
}
