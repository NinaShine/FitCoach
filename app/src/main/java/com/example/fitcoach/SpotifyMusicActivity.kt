package com.example.fitcoach

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.ui.platform.LocalContext
import androidx.navigation.compose.rememberNavController

class SpotifyMusicActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            val context = LocalContext.current
            val accessToken = getSpotifyAccessToken(context) ?: ""
            MusicScreen(navController = rememberNavController(), accessToken = accessToken)
        }
    }
}
