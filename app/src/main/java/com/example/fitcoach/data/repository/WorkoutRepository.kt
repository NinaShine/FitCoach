package com.example.fitcoach.data.repository

import com.example.fitcoach.data.model.Workout
import com.google.firebase.firestore.FirebaseFirestore

class WorkoutRepository(private val firestore: FirebaseFirestore) {

    fun addWorkout(userId: String, workout: Workout, onComplete: (Boolean) -> Unit) {
        firestore.collection("users")
            .document(userId)
            .collection("workouts")
            .add(workout)
            .addOnSuccessListener { onComplete(true) }
            .addOnFailureListener { onComplete(false) }
    }

    fun getWorkouts(userId: String, onResult: (List<Workout>) -> Unit) {
        firestore.collection("users")
            .document(userId)
            .collection("workouts")
            .get()
            .addOnSuccessListener { snapshot ->
                val list = snapshot.toObjects(Workout::class.java)
                onResult(list)
            }
    }
}
