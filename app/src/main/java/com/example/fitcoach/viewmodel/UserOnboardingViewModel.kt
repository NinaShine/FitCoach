package com.example.fitcoach.viewmodel

import androidx.lifecycle.ViewModel
import com.example.fitcoach.data.model.UserAnswers
import com.example.fitcoach.data.model.UserProfile
import com.example.fitcoach.data.model.toUserProfile
import com.example.fitcoach.data.repository.UserRepository
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore


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

    fun updateFirestoreProfile(
        onSuccess: () -> Unit,
        onFailure: (Exception) -> Unit
    ) {
        val updates = mapOf(
            "firstName" to answers.firstName,
            "lastName" to answers.lastName,
            "bio" to answers.bio,
            "gender" to answers.gender,
            "birthDate" to answers.birthDate,
            "avatarUrl" to answers.avatarUrl
        )

        UserRepository.updateUserProfile(
            updates = updates,
            onSuccess = onSuccess,
            onFailure = onFailure
        )
    }

    fun loadFromFirestore(
        onSuccess: () -> Unit = {},
        onFailure: (Exception) -> Unit = {}
    ) {
        val uid = FirebaseAuth.getInstance().currentUser?.uid ?: return

        FirebaseFirestore.getInstance()
            .collection("users")
            .document(uid)
            .get()
            .addOnSuccessListener { document ->
                document.toObject(UserProfile::class.java)?.let { profile ->
                    answers = UserAnswers.fromUserProfile(profile)
                    onSuccess()
                }
            }
            .addOnFailureListener { onFailure(it) }
    }



}
