package com.example.fitcoach

import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import com.example.fitcoach.ui.screen.FitCoachApp
import com.example.fitcoach.viewmodel.MainViewModel
import com.google.firebase.FirebaseApp

class MainActivity : ComponentActivity() {
    private val maintViewModel = MainViewModel()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        FirebaseApp.initializeApp(this)

        //handleSpotifyRedirect(intent)

        val navigateTo = intent.getStringExtra("navigateTo")

        setContent {
            FitCoachApp(maintViewModel, initialRoute = navigateTo)
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





