package com.example.fitcoach.viewmodel

import android.util.Log
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.GoogleAuthProvider
import com.google.firebase.auth.ktx.userProfileChangeRequest
import com.google.firebase.firestore.FirebaseFirestore

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

    fun signUp(email: String, password: String, username: String,  onboardingViewModel: UserOnboardingViewModel) {
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
                            onboardingViewModel.answers.username = username
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

    private fun saveTokenToFirestore(token: String) {
        val uid = FirebaseAuth.getInstance().currentUser?.uid ?: return
        val db = FirebaseFirestore.getInstance()

        db.collection("users").document(uid)
            .update("fcmToken", token)
            .addOnSuccessListener {
                Log.d("FCM", "Token FCM enregistré dans Firestore.")
            }
            .addOnFailureListener {
                Log.e("FCM", "Échec d'enregistrement du token FCM : ${it.message}")
            }
    }

    fun signInWithGoogle(idToken: String) {
        val credential = GoogleAuthProvider.getCredential(idToken, null)
        FirebaseAuth.getInstance().signInWithCredential(credential)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    authSuccess.value = true
                    errorMessage.value = null
                } else {
                    errorMessage.value = task.exception?.message
                }
            }
    }



}
