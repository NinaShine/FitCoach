package com.example.fitcoach

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.ui.platform.LocalContext
import androidx.navigation.compose.rememberNavController
import com.example.fitcoach.ui.screen.SpotifyProfileScreen
import com.example.fitcoach.ui.screen.getSpotifyAccessToken

class SpotifyProfileActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        //val accessToken = intent.getStringExtra("accessToken") ?: ""
        setContent {
            val context = LocalContext.current
            val accessToken = getSpotifyAccessToken(context) ?: ""
            SpotifyProfileScreen(navController = rememberNavController(), accessToken = accessToken)
        }
    }
}
