package com.example.fitcoach.viewmodel.track_section

import android.util.Log
import androidx.lifecycle.ViewModel
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.snapshots.SnapshotStateList


class LastSessionViewModel : ViewModel() {
    private val _lastSession = mutableStateOf<Map<String, Any>?>(null)
    val lastSession: State<Map<String, Any>?> = _lastSession

    private val _recentSessions = mutableStateListOf<Map<String, Any>>()
    val recentSessions: SnapshotStateList<Map<String, Any>> = _recentSessions


    fun fetchLastSession() {
        val uid = FirebaseAuth.getInstance().currentUser?.uid ?: return
        val db = FirebaseFirestore.getInstance()

        db.collection("users")
            .document(uid)
            .collection("sessions")
            .orderBy("timestamp", Query.Direction.DESCENDING)
            .limit(1)
            .get()
            .addOnSuccessListener { result ->
                _lastSession.value = result.documents.firstOrNull()?.data
            }
            .addOnFailureListener {
                Log.e("Firestore", "Erreur récupération session : ${it.message}")
            }
    }

    fun fetchRecentSessions() {
        val uid = FirebaseAuth.getInstance().currentUser?.uid ?: return
        val db = FirebaseFirestore.getInstance()

        db.collection("users")
            .document(uid)
            .collection("sessions")
            .orderBy("timestamp", Query.Direction.DESCENDING)
            .get()
            .addOnSuccessListener { result ->
                _recentSessions.clear()
                _recentSessions.addAll(result.documents.mapNotNull { it.data })
            }
            .addOnFailureListener {
                Log.e("Firestore", "Erreur récupération sessions : \${it.message}")
            }
    }
}
