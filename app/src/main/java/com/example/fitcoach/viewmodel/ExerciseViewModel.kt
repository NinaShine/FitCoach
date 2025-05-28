package com.example.fitcoach.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.fitcoach.data.model.Exercise
import com.example.fitcoach.data.remote.RetrofitInstance
import com.example.fitcoach.data.repository.InstructionsRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class ExerciseViewModel(application: Application) : AndroidViewModel(application) {

    private val _exercises = MutableStateFlow<List<Exercise>>(emptyList())
    val exercises: StateFlow<List<Exercise>> = _exercises

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading

    private val instructionsRepository = InstructionsRepository(application)

    init {
        fetchExercises()
    }

    private fun fetchExercises() {
        viewModelScope.launch {
            _isLoading.value = true
            try {
                val response = RetrofitInstance.api.getAllExercises()

                // Enrichir chaque exercice avec ses instructions
                val exercisesWithInstructions = response.map { exercise ->
                    exercise.copy(
                        instructions = instructionsRepository.getInstructions(exercise.id)
                    )
                }

                _exercises.value = exercisesWithInstructions
            } catch (e: Exception) {
                e.printStackTrace()
                _exercises.value = emptyList()
            } finally {
                _isLoading.value = false
            }
        }
    }

    fun getExerciseById(id: String): Exercise? {
        return _exercises.value.find { it.id == id }
    }
}