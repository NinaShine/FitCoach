package com.example.fitcoach.viewmodel

import androidx.lifecycle.ViewModel
import com.example.fitcoach.data.model.UserAnswers
import com.example.fitcoach.data.model.toUserProfile
import com.example.fitcoach.data.repository.UserRepository



class UserOnboardingViewModel : ViewModel() {
    var answers = UserAnswers()

    fun saveToFirestore(
        onSuccess: () -> Unit,
        onFailure: (Exception) -> Unit
    ) {
        UserRepository.saveUserProfile(
            profile = answers.toUserProfile(),
            onSuccess = onSuccess,
            onFailure = onFailure
        )
    }
}
