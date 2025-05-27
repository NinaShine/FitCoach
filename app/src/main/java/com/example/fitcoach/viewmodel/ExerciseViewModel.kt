package com.example.fitcoach.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.fitcoach.data.model.Exercise
import com.example.fitcoach.data.remote.RetrofitInstance
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class ExerciseViewModel : ViewModel() {

    private val _exercises = MutableStateFlow<List<Exercise>>(emptyList())
    val exercises: StateFlow<List<Exercise>> = _exercises

    init {
        fetchExercises()
    }

    private fun fetchExercises() {
        viewModelScope.launch {
            try {
                val response = RetrofitInstance.api.getAllExercises()
                _exercises.value = response
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }
}
