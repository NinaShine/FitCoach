package com.example.fitcoach.data.repository

import android.annotation.SuppressLint
import com.example.fitcoach.data.model.UserLeaderboard
import com.example.fitcoach.data.model.UserProfile
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.tasks.await

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

    fun updateUserProfile(
        updates: Map<String, Any?>,
        onSuccess: () -> Unit,
        onFailure: (Exception) -> Unit
    ) {
        val uid = auth.currentUser?.uid
        if (uid == null) {
            onFailure(Exception("Utilisateur non connecté"))
            return
        }

        db.collection("users").document(uid)
            .update(updates)
            .addOnSuccessListener { onSuccess() }
            .addOnFailureListener { onFailure(it) }
    }


    suspend fun getAllUserLeaderboard(): List<UserLeaderboard> {
        val snapshot = FirebaseFirestore.getInstance()
            .collection("users")
            .get()
            .await()

        return snapshot.documents.mapNotNull { doc ->
            val firstName = doc.getString("firstName") ?: ""
            val lastName = doc.getString("lastName") ?: ""
            val fullName = "$firstName $lastName"
            val avatarUrl = doc.getString("profileImageUrl") ?: ""
            val points = doc.getLong("points")?.toInt() ?: 0

            UserLeaderboard(
                uid = doc.id,
                fullName = fullName,
                avatarUrl = avatarUrl,
                points = points
            )
        }.sortedByDescending { it.points }
    }

}

