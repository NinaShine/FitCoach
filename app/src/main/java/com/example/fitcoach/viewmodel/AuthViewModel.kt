package com.example.fitcoach.viewmodel

import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.userProfileChangeRequest

class AuthViewModel : ViewModel() {

    private val auth = FirebaseAuth.getInstance()

    val isUserAuthenticated = mutableStateOf(auth.currentUser != null)
    val errorMessage = mutableStateOf<String?>(null)
    val authSuccess = mutableStateOf(false)

    fun signIn(email: String, password: String) {
        errorMessage.value = null
        auth.signInWithEmailAndPassword(email, password)
            .addOnSuccessListener {
                isUserAuthenticated.value = true
                authSuccess.value = true
            }
            .addOnFailureListener {
                errorMessage.value = it.message
                authSuccess.value = false
            }
    }

    fun signUp(email: String, password: String, username: String) {
        errorMessage.value = null
        auth.createUserWithEmailAndPassword(email, password)
            .addOnSuccessListener {
                val user = auth.currentUser
                val profileUpdates = userProfileChangeRequest {
                    displayName = username
                }
                user?.updateProfile(profileUpdates)
                    ?.addOnCompleteListener { updateTask ->
                        if (updateTask.isSuccessful) {
                            isUserAuthenticated.value = true
                            authSuccess.value = true
                        } else {
                            errorMessage.value = "Compte créé, mais impossible d'ajouter le nom."
                            authSuccess.value = false
                        }
                    }
            }
            .addOnFailureListener {
                errorMessage.value = it.message
                authSuccess.value = false
            }
    }

    fun signOut() {
        auth.signOut()
        isUserAuthenticated.value = false
        authSuccess.value = false
    }

    fun resetPassword(email: String, onSuccess: () -> Unit) {
        errorMessage.value = null
        auth.sendPasswordResetEmail(email)
            .addOnSuccessListener {
                onSuccess()
            }
            .addOnFailureListener {
                errorMessage.value = it.message
            }
    }

}
