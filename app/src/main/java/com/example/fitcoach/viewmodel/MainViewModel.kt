package com.example.fitcoach.viewmodel

import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel

class MainViewModel : ViewModel() {
    val selectedTab = mutableStateOf("accueil")
    val currentUserName = mutableStateOf("")

    fun updateUserName(name: String) {
        currentUserName.value = name
    }
}
