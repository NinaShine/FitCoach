package com.example.fitcoach.data.repository

import com.example.fitcoach.data.model.Workout
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.tasks.await

class WorkoutRepository(private val firestore: FirebaseFirestore) {

    suspend fun addWorkout(userId: String, workout: Workout): Boolean {
        return try {
            firestore.collection("users")
                .document(userId)
                .collection("workouts")
                .add(workout)
                .await()
            true
        } catch (e: Exception) {
            false
        }
    }

    suspend fun getWorkouts(userId: String): List<Workout> {
        return try {
            val snapshot = firestore.collection("users")
                .document(userId)
                .collection("workouts")
                .get()
                .await()
            snapshot.toObjects(Workout::class.java)
        } catch (e: Exception) {
            emptyList()
        }
    }

    fun addWorkoutCallback(userId: String, workout: Workout, onComplete: (Boolean) -> Unit) {
        firestore.collection("users")
            .document(userId)
            .collection("workouts")
            .add(workout)
            .addOnSuccessListener { onComplete(true) }
            .addOnFailureListener { onComplete(false) }
    }

    fun getWorkoutsCallback(userId: String, onResult: (List<Workout>) -> Unit) {
        firestore.collection("users")
            .document(userId)
            .collection("workouts")
            .get()
            .addOnSuccessListener { snapshot ->
                val list = snapshot.toObjects(Workout::class.java)
                onResult(list)
            }
            .addOnFailureListener {
                onResult(emptyList())
            }
    }
}