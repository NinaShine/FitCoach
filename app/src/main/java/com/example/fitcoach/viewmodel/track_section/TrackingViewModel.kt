package com.example.fitcoach.viewmodel.track_section

import android.annotation.SuppressLint
import android.app.Application
import android.content.pm.PackageManager
import android.location.Location
import android.os.Looper
import android.util.Log
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.tooling.preview.Preview
import androidx.core.content.ContextCompat
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.fitcoach.viewmodel.UserProfileViewModel
import com.google.android.gms.location.LocationCallback
import com.google.android.gms.location.LocationRequest
import com.google.android.gms.location.LocationResult
import com.google.android.gms.location.LocationServices
import com.google.android.gms.location.Priority
import com.google.android.gms.maps.model.LatLng
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.Job
import kotlinx.coroutines.NonCancellable.isActive
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

class TrackingViewModel(application: Application) : AndroidViewModel(application) {
    @SuppressLint("StaticFieldLeak")
    private val context = application.applicationContext
    private val fusedLocation = LocationServices.getFusedLocationProviderClient(context)

    var isTracking by mutableStateOf(false)
    var timeElapsed by mutableStateOf(0L)
    var distance by mutableStateOf(0.0)
    var speed by mutableStateOf(0.0)
    var calories by mutableStateOf(0.0)
    var userWeightKg = 75.0 // à personnaliser plus tard avec Firestore
    var selectedActivity by mutableStateOf("Walking")


    private var startTime = 0L
    private var lastLocation: Location? = null
    private var timerJob: Job? = null
    private var locationJob: Job? = null
    private var locationCallback: LocationCallback? = null


    private val MET = 8.0 // ou adapte selon le type d’activité

    data class SessionData(
        val distanceKm: Double,
        val durationMs: Long,
        val speedKmH: Double,
        val calories: Double,
        val steps: Int,
        val activityType: String
    )

    var lastSessionData by mutableStateOf<SessionData?>(null)
        private set

    var currentLatLng by mutableStateOf<LatLng?>(null)
        private set


    val pathPoints = mutableStateListOf<LatLng>()

    fun startLocationUpdates() {
        val locationRequest = LocationRequest.Builder(Priority.PRIORITY_HIGH_ACCURACY, 1000L).apply {
            setWaitForAccurateLocation(true)
            setMinUpdateIntervalMillis(1000)
            setMaxUpdateDelayMillis(2000)
        }.build()

        val permissionGranted = ContextCompat.checkSelfPermission(
            context,
            android.Manifest.permission.ACCESS_FINE_LOCATION
        ) == PackageManager.PERMISSION_GRANTED

        if (permissionGranted) {
            locationCallback = object : LocationCallback() {
                override fun onLocationResult(result: LocationResult) {
                    val newLocation = result.lastLocation ?: return
                    val newLatLng = LatLng(newLocation.latitude, newLocation.longitude)
                    currentLatLng = newLatLng

                    if (isTracking) {
                        if (lastLocation != null) {
                            val delta = lastLocation!!.distanceTo(newLocation)
                            distance += delta / 1000.0
                            val elapsedTime = (System.currentTimeMillis() - startTime) / 1000.0
                            speed = if (elapsedTime > 0) distance / (elapsedTime / 3600.0) else 0.0
                            pathPoints.add(newLatLng)
                        }
                        lastLocation = newLocation
                    }
                }
            }

            fusedLocation.requestLocationUpdates(
                locationRequest,
                locationCallback!!,
                Looper.getMainLooper()
            )
        }
    }

    fun startTracking() {
        isTracking = true
        startTime = System.currentTimeMillis()
        lastLocation = null
        distance = 0.0
        speed = 0.0
        timeElapsed = 0L
        pathPoints.clear()

        timerJob = viewModelScope.launch {
            while (isActive) {
                timeElapsed = System.currentTimeMillis() - startTime
                delay(1000)
                val minutes = timeElapsed.toDouble() / 60000.0
                if (distance > 0.05) {
                    calories = (minutes * MET * 3.5 * userWeightKg) / 200.0
                }
            }
        }
    }


    fun stopTracking() {
        isTracking = false
        timerJob?.cancel()
        /*
        locationJob?.cancel()
        locationCallback?.let {
            fusedLocation.removeLocationUpdates(it)
        }
        locationCallback = null

         */

    }

    fun formatTime(): String {
        val seconds = (timeElapsed / 1000) % 60
        val minutes = (timeElapsed / (1000 * 60)) % 60
        val hours = (timeElapsed / (1000 * 60 * 60))
        return String.format("%02d:%02d:%02d", hours, minutes, seconds)
    }

    fun saveSessionToFirestore(steps: Int, onSuccess: (() -> Unit)? = null) {
        val uid = FirebaseAuth.getInstance().currentUser?.uid ?: return
        val db = FirebaseFirestore.getInstance()

        val session = mapOf(
            "timestamp" to FieldValue.serverTimestamp(),  // plus lisible que millis
            "durationFormatted" to formatTime(),  // lisible directement
            "distanceKm" to "%.2f".format(distance),
            "speedKmH" to "%.2f".format(speed),
            "calories" to "%.0f".format(calories),
            "steps" to steps,
            "activityType" to selectedActivity

        )

        lastSessionData = SessionData(
            distanceKm = distance,
            durationMs = timeElapsed,
            speedKmH = speed,
            calories = calories,
            steps = steps,
            activityType = selectedActivity
        )

        db.collection("users")
            .document(uid)
            .collection("sessions")
            .add(session)
            .addOnSuccessListener {
                Log.d("Firestore", "Séance sauvegardée avec succès")
                onSuccess?.invoke()

            }
            .addOnFailureListener {
                Log.e("Firestore", "Erreur lors de la sauvegarde : ${it.message}")
            }
    }

    fun resetSession() {
        timeElapsed = 0L
        distance = 0.0
        speed = 0.0
        calories = 0.0
        lastLocation = null
    }


}

