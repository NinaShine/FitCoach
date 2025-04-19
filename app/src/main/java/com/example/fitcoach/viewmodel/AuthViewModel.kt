package com.example.fitcoach.viewmodel

import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import com.google.firebase.auth.FirebaseAuth

class AuthViewModel : ViewModel() {
    private val auth: FirebaseAuth = FirebaseAuth.getInstance()

    val isUserAuthenticated = mutableStateOf(auth.currentUser != null)
    val errorMessage = mutableStateOf<String?>(null)

    fun signIn(email: String, password: String, onSuccess: () -> Unit) {
        auth.signInWithEmailAndPassword(email, password)
            .addOnSuccessListener { onSuccess() }
            .addOnFailureListener { errorMessage.value = it.message }
    }

    fun signUp(email: String, password: String, onSuccess: () -> Unit) {
        auth.createUserWithEmailAndPassword(email, password)
            .addOnSuccessListener { onSuccess() }
            .addOnFailureListener { errorMessage.value = it.message }
    }

    fun signOut() {
        auth.signOut()
        isUserAuthenticated.value = false
    }
}
