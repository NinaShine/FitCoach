package com.example.fitcoach.viewmodel

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import java.time.LocalDate
import java.time.Period
import java.time.format.DateTimeFormatter
import java.util.Locale

class UserProfileViewModel : ViewModel() {
    var username = mutableStateOf<String?>(null)
    var birthDate = mutableStateOf<String?>(null)
    var fN = mutableStateOf<String?>(null)
    var lN = mutableStateOf<String?>(null)
    var bio = mutableStateOf<String?>(null)
    var gender = mutableStateOf<String?>(null)
    var weight = mutableStateOf<Double?>(null)
    var avatarUrl = mutableStateOf<String?>(null)

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

    @RequiresApi(Build.VERSION_CODES.O)
    fun fetchAge() {
        val uid = FirebaseAuth.getInstance().currentUser?.uid ?: return
        FirebaseFirestore.getInstance()
            .collection("users")
            .document(uid)
            .get()
            .addOnSuccessListener { doc ->
                val birthDateStr = doc.getString("birthDate")
                if (birthDateStr != null) {
                    try {
                        val formatter = DateTimeFormatter.ofPattern("d MMMM yyyy", Locale.ENGLISH)
                        val birthDateLocal = LocalDate.parse(birthDateStr, formatter)
                        val today = LocalDate.now()
                        val ageYears = Period.between(birthDateLocal, today).years
                        birthDate.value = "$ageYears ans"
                    } catch (e: Exception) {
                        birthDate.value = "Format invalide"
                    }
                } else {
                    birthDate.value = "Non renseignée"
                }
            }
            .addOnFailureListener {
                birthDate.value = "Erreur"
            }
    }

    fun fetchFirstName() {
        val uid = FirebaseAuth.getInstance().currentUser?.uid ?: return
        FirebaseFirestore.getInstance()
            .collection("users")
            .document(uid)
            .get()
            .addOnSuccessListener { doc ->
                fN.value = doc.getString("firstname") ?: "First Name"
            }
            .addOnFailureListener {
                username.value = "Erreur"
            }

    }

    fun fetchLastName(){
        val uid = FirebaseAuth.getInstance().currentUser?.uid ?: return
        FirebaseFirestore.getInstance()
            .collection("users")
            .document(uid)
            .get()
            .addOnSuccessListener { doc ->
                lN.value = doc.getString("lastname") ?: "Last Name"
            }
            .addOnFailureListener {
                lN.value = "Erreur"
            }
    }

    fun fetchBio(){
        val uid = FirebaseAuth.getInstance().currentUser?.uid ?: return
        FirebaseFirestore.getInstance()
            .collection("users")
            .document(uid)
            .get()
            .addOnSuccessListener { doc ->
                bio.value = doc.getString("bio") ?: "Bio"
            }
            .addOnFailureListener {
                bio.value = "Erreur"
            }
    }

    fun fetchGender(){
        val uid = FirebaseAuth.getInstance().currentUser?.uid ?: return
        FirebaseFirestore.getInstance()
            .collection("users")
            .document(uid)
            .get()
            .addOnSuccessListener { doc ->
                gender.value = doc.getString("gender") ?: "Gender"
            }
            .addOnFailureListener {
                gender.value = "Erreur"
            }
    }

    fun fetchBirthDate(){
        val uid = FirebaseAuth.getInstance().currentUser?.uid ?: return
        FirebaseFirestore.getInstance()
            .collection("users")
            .document(uid)
            .get()
            .addOnSuccessListener { doc ->
                birthDate.value = doc.getString("birthDate") ?: "Birth Date"
            }
            .addOnFailureListener {
                birthDate.value = "Erreur"
            }
    }

    fun fetchUserWeight(){
        val uid = FirebaseAuth.getInstance().currentUser?.uid ?: return
        FirebaseFirestore.getInstance()
            .collection("users")
            .document(uid)
            .get()
            .addOnSuccessListener { doc ->
                weight.value = doc.getDouble("weight") ?: 0.0
            }
            .addOnFailureListener {
                weight.value = 0.0
            }
    }

    fun fetchAvatar(){
        val uid = FirebaseAuth.getInstance().currentUser?.uid ?: return
        FirebaseFirestore.getInstance()
            .collection("users")
            .document(uid)
            .get()
            .addOnSuccessListener { doc ->
                avatarUrl.value = doc.getString("avatarUrl")
            }
            .addOnFailureListener {
                avatarUrl.value = null
            }
    }

}
