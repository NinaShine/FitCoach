package com.example.fitcoach.viewmodel.track_section

import android.annotation.SuppressLint
import android.app.Application
import android.content.Context
import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import android.util.Log
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.AndroidViewModel
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import androidx.core.content.edit
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

class LiveTrackingViewModel(application: Application) : AndroidViewModel(application) {

    @SuppressLint("StaticFieldLeak")
    private val context = application.applicationContext
    private val prefs = context.getSharedPreferences("step_prefs", Context.MODE_PRIVATE)

    private var sensorManager: SensorManager = context.getSystemService(Context.SENSOR_SERVICE) as SensorManager
    private var stepSensor: Sensor? = null
    private var isSimulatingSteps = false // quand capteur de pas n'existe pas mdrr


    var steps by mutableStateOf(0)
    var calories by mutableStateOf(0.0)
    var distanceKm by mutableStateOf(0.0)

    var weightKg = 75.0
    private val averageStepLengthMeters = 0.78

    private val stepListener = object : SensorEventListener {
        override fun onSensorChanged(event: SensorEvent) {
            if (event.sensor.type == Sensor.TYPE_STEP_COUNTER) {
                val totalSteps = event.values[0].toInt()
                Log.d("LiveTracking", "Steps: $totalSteps")

                val today = getCurrentDate()
                val savedDate = prefs.getString("step_date", null)
                val savedInitial = prefs.getInt("initial_steps", -1)

                if (savedDate == null || savedDate != today || savedInitial == -1) {
                    // Nouvelle journée ou première fois : on sauvegarde les valeurs
                    prefs.edit() {
                        putString("step_date", today)
                            .putInt("initial_steps", totalSteps)
                    }

                    steps = 0
                } else {
                    steps = totalSteps - savedInitial
                }

                // Calcul des calories et distance
                calories = steps * 0.04 * (weightKg / 70.0)
                distanceKm = (steps * averageStepLengthMeters) / 1000.0
            }
        }

        override fun onAccuracyChanged(sensor: Sensor?, accuracy: Int) {
            Log.d("LiveTracking", "Accuracy changed: $accuracy")
        }
    }

    private fun simulateStepsIfNeeded() {
        if (!isSimulatingSteps) {
            isSimulatingSteps = true
            viewModelScope.launch {
                while (isSimulatingSteps) {
                    delay(2000) // toutes les 2 secondes
                    steps += 1
                    calories = steps * 0.04 * (weightKg / 70.0)
                    distanceKm = (steps * averageStepLengthMeters) / 1000.0
                    //Log.d("LiveTracking", "Pas simulé : $steps")
                }
            }
        }
    }


    fun startListening() {
        stepSensor = sensorManager.getDefaultSensor(Sensor.TYPE_STEP_COUNTER)
        if (stepSensor == null) {
            Log.e("LiveTracking", "Aucun capteur de pas trouvé sur l’appareil.")
            simulateStepsIfNeeded()
        } else {
            Log.d("LiveTracking", "Capteur de pas détecté, enregistrement du listener.")
            sensorManager.registerListener(stepListener, stepSensor, SensorManager.SENSOR_DELAY_UI)
        }
    }


    fun stopListening() {
        sensorManager.unregisterListener(stepListener)
        isSimulatingSteps = false
    }

    private fun getCurrentDate(): String {
        val sdf = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
        return sdf.format(Date())
    }
}

