package com.example.fitcoach.viewmodel.track_section

import android.util.Log
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query

class RecentWorkoutsViewModel : ViewModel() {
    var recentSessions by mutableStateOf<List<Map<String, Any>>>(emptyList())
        private set

    fun fetchRecentSessions() {
        val uid = FirebaseAuth.getInstance().currentUser?.uid ?: return
        FirebaseFirestore.getInstance()
            .collection("users")
            .document(uid)
            .collection("sessions")
            .orderBy("timestamp", Query.Direction.DESCENDING)
            .limit(5)
            .get()
            .addOnSuccessListener { result ->
                recentSessions = result.documents.mapNotNull { it.data }
            }
            .addOnFailureListener {
                Log.e("Firestore", "Erreur récupération sessions : ${it.message}")
            }
    }
}
