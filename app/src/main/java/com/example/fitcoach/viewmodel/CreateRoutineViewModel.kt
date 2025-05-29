package com.example.fitcoach.viewmodel

import androidx.lifecycle.ViewModel
import com.example.fitcoach.data.model.Routine
import com.example.fitcoach.data.model.WorkoutExercise
import com.example.fitcoach.repository.RoutineRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

class CreateRoutineViewModel : ViewModel() {
    private val _exercises = MutableStateFlow<List<WorkoutExercise>>(emptyList())
    val exercises: StateFlow<List<WorkoutExercise>> = _exercises

    fun addExercise(exercise: WorkoutExercise) {
        _exercises.value = _exercises.value + exercise
    }

    fun clearExercises() {
        _exercises.value = emptyList()
    }

    fun saveRoutine(title: String) {
        val routine = Routine(
            title = title,
            exercises = _exercises.value
        )
        RoutineRepository.addRoutine(routine)
        clearExercises()
    }

    fun removeExercise(exercise: WorkoutExercise) {
        _exercises.value = _exercises.value.filterNot { it.id == exercise.id }
    }


}
