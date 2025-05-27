package com.example.fitcoach.data.repository

import android.annotation.SuppressLint
import com.example.fitcoach.data.model.UserProfile
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

object UserRepository {
    private val auth = FirebaseAuth.getInstance()
    @SuppressLint("StaticFieldLeak")
    private val db = FirebaseFirestore.getInstance()

    fun saveUserProfile(profile: UserProfile, onSuccess: () -> Unit, onFailure: (Exception) -> Unit) {
        val uid = auth.currentUser?.uid ?: return

        db.collection("users").document(uid)
            .set(profile.copy(uid = uid))
            .addOnSuccessListener { onSuccess() }
            .addOnFailureListener { onFailure(it) }
    }
}