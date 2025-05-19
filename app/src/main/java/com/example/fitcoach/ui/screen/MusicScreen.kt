package com.example.fitcoach.ui.screen

import android.content.Intent
import android.util.Log
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.SkipNext
import androidx.compose.material.icons.filled.SkipPrevious
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.content.ContextCompat
import androidx.core.net.toUri
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import coil.compose.rememberAsyncImagePainter
import com.example.fitcoach.R
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import com.example.fitcoach.data.model.Song


// lien du tuto : https://developer.spotify.com/documentation/android/tutorials/getting-started
@Composable
fun MusicScreen(navController: NavController, accessToken: String) {
    val context = LocalContext.current
    var playlists by remember { mutableStateOf<List<SpotifyPlaylist>>(emptyList()) }
    val scope = rememberCoroutineScope()


    val clientId = "23b18fa0f82640f4a7c372678881a764"
    val redirectUri = "fitcoach://callback"
    val scopes = listOf(
        "user-read-private",
        "user-read-email",
        "streaming",
        "playlist-read-private"
    ).joinToString("%20")

    val authUrl = "https://accounts.spotify.com/authorize" +
            "?client_id=$clientId" +
            "&response_type=code" +
            "&redirect_uri=$redirectUri" +
            "&scope=$scopes"

    val currentSong = Song(
        title = "On verra",
        artist = "Nekfeu",
        imageRes = R.drawable.spotify_icon
    )

    LaunchedEffect(accessToken) {
        scope.launch(Dispatchers.IO) {
            playlists = fetchSpotifyPlaylists(accessToken)
        }
    }


    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.White)
            .padding(16.dp)
    ) {
        // Header
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text("fit’fy", fontWeight = FontWeight.Bold, fontSize = 22.sp, modifier = Modifier.align(Alignment.CenterVertically))
            Row(verticalAlignment = Alignment.CenterVertically) {
                Image(
                    painter = painterResource(id = R.drawable.robot_assistant),
                    contentDescription = null,
                    modifier = Modifier
                        .size(50.dp)
                        .clip(CircleShape)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Image(
                    painter = painterResource(id = R.drawable.july_photo_profile),
                    contentDescription = null,
                    contentScale = ContentScale.Crop,
                    modifier = Modifier
                        .size(45.dp)
                        .clip(CircleShape)
                        .clickable {
                            // TODO: Naviguer vers la page profil
                        }
                )
            }
        }

        Divider(
            color = Color.Black,
            thickness = 1.dp,
            modifier = Modifier.width(80.dp)
        )

        Spacer(modifier = Modifier.height(8.dp))
        Text("Music Assistant", fontSize = 20.sp, fontWeight = FontWeight.SemiBold)
        Text("Your rhythm, your energy", fontSize = 14.sp, color = Color.Gray)

        Spacer(modifier = Modifier.height(16.dp))

        Box(
            modifier = Modifier
                .fillMaxWidth()
                .background(
                    brush = Brush.horizontalGradient(listOf(Color(0xFFFFB47E), Color(0xFFFF8762))),
                    shape = RoundedCornerShape(20.dp)
                )
                .padding(16.dp)
        ) {
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Image(
                    painter = painterResource(id = currentSong.imageRes),
                    contentDescription = "Current song",
                    modifier = Modifier.size(64.dp)
                )
                Spacer(modifier = Modifier.height(8.dp))
                Text(currentSong.title, fontWeight = FontWeight.Bold, fontSize = 18.sp)
                Text(currentSong.artist, fontSize = 14.sp, color = Color.Black)

                Spacer(modifier = Modifier.height(16.dp))

                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(4.dp)
                        .background(Color.White.copy(alpha = 0.5f))
                ) {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth(0.4f)
                            .fillMaxHeight()
                            .background(Color.White)
                    )
                }

                Spacer(modifier = Modifier.height(16.dp))

                Row(horizontalArrangement = Arrangement.SpaceEvenly, modifier = Modifier.fillMaxWidth()) {
                    Icon(Icons.Default.SkipPrevious, contentDescription = null)
                    Icon(Icons.Default.PlayArrow, contentDescription = null, modifier = Modifier.size(48.dp))
                    Icon(Icons.Default.SkipNext, contentDescription = null)
                }
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            FilterChip("Training", selected = true)
            FilterChip("Chill", selected = false)
            FilterChip("Cardio", selected = false)
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Add a song
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.clickable(
                interactionSource = remember { MutableInteractionSource() },
                indication = null
            ) {
                Log.d("NAVIGATION", "Trying to navigate to searchMusic")
                navController.navigate("searchMusic")
            }
        ) {
            Icon(Icons.Default.Add, contentDescription = null, tint = Color(0xFFE86144))
            Spacer(modifier = Modifier.width(8.dp))
            Text("Add a song", color = Color(0xFFE86144))
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Login with Spotify button

        //if(getSpotifyAccessToken(context) == null){
            Button(
                onClick = {
                    Log.d("Spotify Music Screen", "Auth URL: $authUrl")

                    val intent = Intent(Intent.ACTION_VIEW, authUrl.toUri())
                    ContextCompat.startActivity(context, intent, null)

                    Log.d("Spotify Music Screen", "Auth URL: $authUrl")

                },
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFE86144)),
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("Login with Spotify", color = Color.White)
            }
        //}


        Spacer(modifier = Modifier.height(5.dp))

        // Playlist
        LazyColumn(verticalArrangement = Arrangement.spacedBy(8.dp)) {
            items(playlists) { playlist ->
                Card(
                    colors = CardDefaults.cardColors(containerColor = Color(0xFFFFF3E0)),
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(70.dp)
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(12.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Image(
                            painter = rememberAsyncImagePainter(playlist.imageUrl),
                            contentDescription = playlist.name,
                            modifier = Modifier
                                .size(56.dp)
                                .clip(RoundedCornerShape(12.dp)),
                            contentScale = ContentScale.Crop
                        )
                        Spacer(modifier = Modifier.width(12.dp))
                        Text(playlist.name, fontWeight = FontWeight.Bold, fontSize = 16.sp)                    }
                }
            }
        }

    }





}


@Composable
fun FilterChip(label: String, selected: Boolean) {
    Surface(
        color = if (selected) Color(0xFFE86144) else Color(0xFFFFF3E0),
        shape = RoundedCornerShape(20.dp)
    ) {
        Text(
            text = label,
            modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp),
            color = if (selected) Color.White else Color.Black
        )
    }
}

@Composable
fun MusicScreenWithNavBar(navController: NavController) {
    var currentRoute by remember { mutableStateOf("music") }

    Scaffold(
        bottomBar = {
            FitBottomBar(
                onItemSelected = { selectedRoute ->
                    currentRoute = selectedRoute
                },
                currentRoute = currentRoute,
                onCentralClick = {
                    // TODO : rediriger vers création séance
                }
            )
        }
    ) { padding ->
        Box(modifier = Modifier.padding(padding)) {
            when (currentRoute) {
                "home" -> AccueilScreen(navController = navController)
                "music" -> MusicScreen(
                    navController = navController,
                    accessToken = getSpotifyAccessToken(LocalContext.current).toString()
                )
                "workout" -> WorkoutScreen()
                "social" -> SocialScreen()
                else -> MusicScreen(
                    navController = navController,
                    accessToken = getSpotifyAccessToken(LocalContext.current).toString()
                )
            }
        }
    }
}



/*
@Preview
@Composable
fun MusicScreenPreview() {
    val navController = rememberNavController()
    val context = LocalContext.current
    val accessToken = getSpotifyAccessToken(context).orEmpty()

    MusicScreen(navController = navController, accessToken = accessToken)
}

 */


