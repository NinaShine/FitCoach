package com.example.fitcoach.viewmodel

import androidx.lifecycle.ViewModel
import com.example.fitcoach.data.model.Workout
import com.example.fitcoach.data.repository.WorkoutRepository
import com.google.firebase.firestore.FirebaseFirestore

class WorkoutViewModel : ViewModel() {
    private val workoutRepository = WorkoutRepository(FirebaseFirestore.getInstance())
    fun addWorkout(userId: String, workout: Workout) {
        workoutRepository.addWorkout(userId, workout, onComplete = { isSuccess ->
            if (isSuccess) {
                // L'ajout du travail a réussi
            } else {
                // L'ajout du travail a échoué
            }
            })
    }
    fun getWorkouts(userId: String, onResult: (List<Workout>) -> Unit) {
        workoutRepository.getWorkouts(userId, onResult)
    }

}
