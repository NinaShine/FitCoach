package com.example.fitcoach.data.repository

import com.example.fitcoach.data.model.Challenge
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.tasks.await

object ChallengeRepository {
    private val db = FirebaseFirestore.getInstance()

    suspend fun getAllChallenges(): List<Challenge> {
        val snapshot = db.collection("challenges").get().await()
        return snapshot.documents.mapNotNull {
            it.toObject(Challenge::class.java)?.copy(id = it.id)
        }
    }

    suspend fun joinChallenge(challengeId: String, userId: String) {
        db.collection("challenges").document(challengeId)
            .update("participants", com.google.firebase.firestore.FieldValue.arrayUnion(userId))
            .await()
    }
}
