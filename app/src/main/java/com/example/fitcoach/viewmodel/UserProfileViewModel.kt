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

    // 🆕 Champs pour la personnalisation
    var location = mutableStateOf<String?>(null)       // Ex: "maison", "salle"
    var fitnessGoal = mutableStateOf<String?>(null)    // Ex: "perte de poids"
    var level = mutableStateOf<String?>(null)          // Ex: "débutant"

    private val uid = FirebaseAuth.getInstance().currentUser?.uid
    private val db = FirebaseFirestore.getInstance()

    fun fetchUsername() {
        fetchField("username") { username.value = it ?: "Utilisateur" }
    }

    fun fetchFirstName() {
        fetchField("firstName") { fN.value = it ?: "First Name" }
    }

    fun fetchLastName() {
        fetchField("lastName") { lN.value = it ?: "Last Name" }
    }

    fun fetchBio() {
        fetchField("bio") { bio.value = it ?: "Bio" }
    }

    fun fetchGender() {
        fetchField("gender") { gender.value = it ?: "Genre" }
    }

    fun fetchBirthDateRaw() {
        fetchField("birthDate") { birthDate.value = it ?: "Birth Date" }
    }

    @RequiresApi(Build.VERSION_CODES.O)
    fun fetchAge() {
        fetchField("birthDate") { birthDateStr ->
            try {
                val formatter = DateTimeFormatter.ofPattern("d MMMM yyyy", Locale.ENGLISH)
                val birthDateLocal = LocalDate.parse(birthDateStr, formatter)
                val ageYears = Period.between(birthDateLocal, LocalDate.now()).years
                birthDate.value = "$ageYears ans"
            } catch (e: Exception) {
                birthDate.value = "Format invalide"
            }
        }
    }

    // 🔁 Méthodes pour les nouveaux champs de personnalisation
    fun fetchLocation() {
        fetchField("location") { location.value = it ?: "maison" }
    }

    fun fetchFitnessGoal() {
        fetchField("fitnessGoal") { fitnessGoal.value = it ?: "forme" }
    }

    fun fetchLevel() {
        fetchField("level") { level.value = it ?: "débutant" }
    }

    // ✅ Fonction générique pour factoriser les appels Firestore
    private fun fetchField(field: String, onResult: (String?) -> Unit) {
        if (uid == null) return
        db.collection("users").document(uid)
            .get()
            .addOnSuccessListener { doc ->
                onResult(doc.getString(field))
            }
            .addOnFailureListener {
                onResult("Erreur")
            }
    }
}
