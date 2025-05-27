package com.example.fitcoach.viewmodel

import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

class UserProfileViewModel : ViewModel() {
    var username = mutableStateOf<String?>(null)

    fun fetchUsername() {
        val uid = FirebaseAuth.getInstance().currentUser?.uid ?: return
        FirebaseFirestore.getInstance()
            .collection("users")
            .document(uid)
            .get()
            .addOnSuccessListener { doc ->
                username.value = doc.getString("username") ?: "Utilisateur"
            }
            .addOnFailureListener {
                username.value = "Erreur"
            }
    }
}
