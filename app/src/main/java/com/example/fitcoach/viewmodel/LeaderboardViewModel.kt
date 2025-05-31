package com.example.fitcoach.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.fitcoach.data.model.UserLeaderboard
import com.example.fitcoach.data.repository.UserRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class LeaderboardViewModel : ViewModel() {
    private val _users = MutableStateFlow<List<UserLeaderboard>>(emptyList())
    val users: StateFlow<List<UserLeaderboard>> = _users

    fun loadLeaderboard() {
        viewModelScope.launch {
            try {
                val result = UserRepository.getAllUserLeaderboard()
                _users.value = result
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }
}
