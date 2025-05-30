package com.example.fitcoach.data.repository

import android.annotation.SuppressLint
import com.example.fitcoach.data.model.UserProfile
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FieldValue
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

    // ✅ Méthode pour FOLLOW (ajouter un uid dans friends)
    fun followUser(
        targetUserId: String,
        onSuccess: () -> Unit,
        onFailure: (Exception) -> Unit
    ) {
        val currentUserId = auth.currentUser?.uid
        if (currentUserId == null) {
            onFailure(Exception("Utilisateur non connecté"))
            return
        }

        db.collection("users").document(currentUserId)
            .update("friends", FieldValue.arrayUnion(targetUserId))
            .addOnSuccessListener { onSuccess() }
            .addOnFailureListener { onFailure(it) }
    }

    // ✅ (optionnel) pour récupérer un profil
    fun getUserProfile(
        userId: String,
        onSuccess: (UserProfile) -> Unit,
        onFailure: (Exception) -> Unit
    ) {
        db.collection("users").document(userId)
            .get()
            .addOnSuccessListener { doc ->
                val user = doc.toObject(UserProfile::class.java)
                if (user != null) {
                    onSuccess(user.copy(uid = doc.id))
                } else {
                    onFailure(Exception("Utilisateur introuvable"))
                }
            }
            .addOnFailureListener { onFailure(it) }
    }
}
