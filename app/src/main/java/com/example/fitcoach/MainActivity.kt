package com.example.fitcoach

import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.annotation.RequiresApi
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.fitcoach.ui.screen.FitCoachApp
import com.example.fitcoach.viewmodel.CurrentlyPlayingViewModel
import com.example.fitcoach.viewmodel.track_section.LiveTrackingViewModel
import com.google.firebase.FirebaseApp
import com.google.firebase.messaging.FirebaseMessaging

class MainActivity : ComponentActivity() {
    private val currentlyPlayingVm = CurrentlyPlayingViewModel()
    @RequiresApi(Build.VERSION_CODES.TIRAMISU)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        FirebaseApp.initializeApp(this)

        if (ContextCompat.checkSelfPermission(
                this,
                android.Manifest.permission.ACTIVITY_RECOGNITION
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            ActivityCompat.requestPermissions(
                this,
                arrayOf(android.Manifest.permission.ACTIVITY_RECOGNITION),
                1001
            )
        }


        FirebaseMessaging.getInstance().token.addOnCompleteListener { task ->
            if (task.isSuccessful) {
                val token = task.result
                Log.d("FCM Token", "Device token: $token")
                // TODO : Enregistrer ce token sur Firestore si besoin
            } else {
                Log.e("FCM Token", "Failed to get token", task.exception)
            }
        }

        //handleSpotifyRedirect(intent)

        val navigateTo = intent.getStringExtra("navigateTo")

        setContent {
            val liveTrackingVm: LiveTrackingViewModel = viewModel()

            LaunchedEffect(Unit) {
                Log.d("MainActivity", "Initialisation du comptage de pas...")
                liveTrackingVm.startListening()
            }
            FitCoachApp(currentlyPlayingVm = currentlyPlayingVm,
                liveTrackingVm = liveTrackingVm,
                initialRoute = navigateTo)
        }
    }
    /*
    override fun onNewIntent(intent: Intent?) {
        super.onNewIntent(intent)
        handleSpotifyRedirect(intent)
    }

    private fun handleSpotifyRedirect(intent: Intent?) {
        intent?.data?.let { uri ->
            if (uri.toString().startsWith("fitcoach://callback")) {
                Log.d("Spotify", "Spotify callback URL: $uri")

                val fragment = uri.fragment // après le "#"
                val params = fragment?.split("&")?.associate {
                    val (key, value) = it.split("=")
                    key to value
                }

                val accessToken = params?.get("access_token")
                accessToken?.let {
                    Log.d("Spotify", "AccessToken = $accessToken")
                }
            }
        }
    }

     */


}





