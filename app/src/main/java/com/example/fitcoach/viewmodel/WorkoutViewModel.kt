package com.example.fitcoach.viewmodel

import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import com.example.fitcoach.data.model.WorkoutExercise

class WorkoutViewModel : ViewModel() {

    private val _workoutExercises = MutableStateFlow<List<WorkoutExercise>>(emptyList())
    val workoutExercises: StateFlow<List<WorkoutExercise>> = _workoutExercises.asStateFlow()

    private val _isWorkoutActive = MutableStateFlow(false)
    val isWorkoutActive: StateFlow<Boolean> = _isWorkoutActive.asStateFlow()

    private val _workoutDuration = MutableStateFlow(0)
    val workoutDuration: StateFlow<Int> = _workoutDuration.asStateFlow()

    fun addExercise(exercise: WorkoutExercise) {
        val currentList = _workoutExercises.value.toMutableList()

        // Vérifier si l'exercice existe déjà
        val existingIndex = currentList.indexOfFirst { it.id == exercise.id }

        if (existingIndex != -1) {
            // Mettre à jour l'exercice existant
            currentList[existingIndex] = exercise
        } else {
            // Ajouter le nouvel exercice
            currentList.add(exercise)
        }

        _workoutExercises.value = currentList
    }

    fun removeExercise(exerciseId: String) {
        val currentList = _workoutExercises.value.toMutableList()
        currentList.removeAll { it.id == exerciseId }
        _workoutExercises.value = currentList
    }

    fun updateExercise(exerciseId: String, series: Int, repetitions: Int, weight: Int? = null) {
        val currentList = _workoutExercises.value.toMutableList()
        val index = currentList.indexOfFirst { it.id == exerciseId }

        if (index != -1) {
            val updatedExercise = currentList[index].copy(
                series = series,
                repetitions = repetitions,
                weight = weight
            )
            currentList[index] = updatedExercise
            _workoutExercises.value = currentList
        }
    }

    fun markSeriesCompleted(exerciseId: String) {
        val currentList = _workoutExercises.value.toMutableList()
        val index = currentList.indexOfFirst { it.id == exerciseId }

        if (index != -1) {
            val exercise = currentList[index]
            val newCompletedSeries = minOf(exercise.completedSeries + 1, exercise.series)
            val updatedExercise = exercise.copy(
                completedSeries = newCompletedSeries,
                isCompleted = newCompletedSeries == exercise.series
            )
            currentList[index] = updatedExercise
            _workoutExercises.value = currentList
        }
    }

    fun getTotalSeries(): Int {
        return _workoutExercises.value.sumOf { it.series }
    }

    fun getTotalVolume(): Int {
        return _workoutExercises.value.sumOf { exercise ->
            (exercise.weight ?: 0) * exercise.repetitions * exercise.series
        }
    }

    fun getCompletedSeries(): Int {
        return _workoutExercises.value.sumOf { it.completedSeries }
    }

    fun getProgressPercentage(): Float {
        val total = getTotalSeries()
        return if (total > 0) {
            getCompletedSeries().toFloat() / total.toFloat()
        } else {
            0f
        }
    }

    fun clearWorkout() {
        _workoutExercises.value = emptyList()
        _isWorkoutActive.value = false
        _workoutDuration.value = 0
    }

    fun startWorkout() {
        _isWorkoutActive.value = true
    }

    fun pauseWorkout() {
        _isWorkoutActive.value = false
    }

    fun endWorkout() {
        _isWorkoutActive.value = false
        // Ici vous pourriez sauvegarder l'entraînement dans une base de données
    }

    fun updateDuration(seconds: Int) {
        _workoutDuration.value = seconds
    }

    fun isWorkoutEmpty(): Boolean {
        return _workoutExercises.value.isEmpty()
    }

    fun getExerciseById(id: String): WorkoutExercise? {
        return _workoutExercises.value.find { it.id == id }
    }
}