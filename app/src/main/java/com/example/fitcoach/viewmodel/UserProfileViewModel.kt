package com.example.fitcoach.viewmodel

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import com.example.fitcoach.data.model.UserProfile
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
    var stepGoal = mutableStateOf<Int?>(null)
    var workoutGoal = mutableStateOf<Int?>(null)

    var location = mutableStateOf<String?>(null)
    var fitnessGoal = mutableStateOf<String?>(null)
    var level = mutableStateOf<String?>(null)

    private val uid = FirebaseAuth.getInstance().currentUser?.uid
    private val db = FirebaseFirestore.getInstance()
    var weight = mutableStateOf<Double?>(null)
    var avatarUrl = mutableStateOf<String?>(null)

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
    val friends = mutableStateListOf<UserProfile>()

    fun fetchFriends() {
        val currentUserId = FirebaseAuth.getInstance().currentUser?.uid ?: return
        val db = FirebaseFirestore.getInstance()

        db.collection("users")
            .document(currentUserId)
            .collection("friends")
            .get()
            .addOnSuccessListener { result ->
                friends.clear()
                for (doc in result.documents) {
                    val friendId = doc.id
                    db.collection("users").document(friendId).get()
                        .addOnSuccessListener { userDoc ->
                            val profile = userDoc.toObject(UserProfile::class.java)
                            profile?.let { friends.add(it) }
                        }
                }
            }
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

    fun fetchLocation() {
        fetchField("location") { location.value = it ?: "maison" }
    }

    fun fetchFitnessGoal() {
        fetchField("fitnessGoal") { fitnessGoal.value = it ?: "forme" }
    }

    fun fetchLevel() {
        fetchField("level") { level.value = it ?: "débutant" }
    }

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

    fun fetchStepGoal(){
        val uid = FirebaseAuth.getInstance().currentUser?.uid ?: return
        FirebaseFirestore.getInstance()
            .collection("users")
            .document(uid)
            .get()
            .addOnSuccessListener { doc ->
                stepGoal.value = doc.getLong("stepGoal")?.toInt() ?: 0
            }
            .addOnFailureListener {
                stepGoal.value = 0
            }
    }

    fun fetchWorkoutGoal(){
        val uid = FirebaseAuth.getInstance().currentUser?.uid ?: return
        FirebaseFirestore.getInstance()
            .collection("users")
            .document(uid)
            .get()
            .addOnSuccessListener { doc ->
                workoutGoal.value = doc.getLong("workoutGoal")?.toInt() ?: 0
            }
            .addOnFailureListener {
                workoutGoal.value = 0
            }
    }

}
