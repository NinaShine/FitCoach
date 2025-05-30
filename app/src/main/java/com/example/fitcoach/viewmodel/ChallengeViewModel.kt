package com.example.fitcoach.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.fitcoach.data.model.Challenge
import com.example.fitcoach.data.repository.ChallengeRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class ChallengeViewModel : ViewModel() {
    private val _challenges = MutableStateFlow<List<Challenge>>(emptyList())
    val challenges: StateFlow<List<Challenge>> = _challenges

    fun loadChallenges() {
        viewModelScope.launch {
            _challenges.value = ChallengeRepository.getAllChallenges()
        }
    }

    fun joinChallenge(challengeId: String, userId: String) {
        viewModelScope.launch {
            ChallengeRepository.joinChallenge(challengeId, userId)
            loadChallenges() // refresh UI
        }
    }
}
