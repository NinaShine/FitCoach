package com.example.fitcoach.ui.screen

import android.content.Context
import android.util.Log
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.FavoriteBorder
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import coil.compose.rememberAsyncImagePainter
import kotlinx.coroutines.launch
import okhttp3.*
import org.json.JSONObject
import java.io.IOException
import com.example.fitcoach.data.model.TrackResult
import com.example.fitcoach.viewmodel.CurrentlyPlayingViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SearchMusicScreen(navController: NavController, currentlyPlayingVm : CurrentlyPlayingViewModel) {
    var searchText by remember { mutableStateOf("") }
    var searchResults by remember { mutableStateOf(listOf<TrackResult>()) }
    val scope = rememberCoroutineScope()
    val context = LocalContext.current

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Search Music") },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(
                            imageVector = Icons.Default.ArrowBack,
                            contentDescription = "Back"
                        )
                    }
                }
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(Color.White)
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // Header
            Text("Music Assistant", fontSize = 22.sp, fontWeight = FontWeight.Bold)
            Text("Your rhythm, your energy", fontSize = 14.sp, color = Color.Gray)

            Spacer(modifier = Modifier.height(30.dp))

            // Barre de recherche
            OutlinedTextField(
                value = searchText,
                onValueChange = { searchText = it },
                leadingIcon = { Icon(Icons.Default.Search, contentDescription = "Search") },
                placeholder = { Text("Search") },
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(MaterialTheme.shapes.medium),
                singleLine = true,
                keyboardOptions = KeyboardOptions.Default.copy(
                    imeAction = ImeAction.Search,
                    keyboardType = KeyboardType.Text
                ),
                keyboardActions = KeyboardActions(
                    onSearch = {
                        if (searchText.isNotBlank()) {
                            scope.launch {
                                searchMusicOnSpotify(context, searchText) { results ->
                                    searchResults = results
                                }
                            }
                        }
                    }
                )
            )

            Spacer(modifier = Modifier.height(40.dp))

            if (searchResults.isEmpty()) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text("Find music", fontSize = 20.sp, fontWeight = FontWeight.Bold)
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        "Add it to the playlist with\nthe icon",
                        textAlign = TextAlign.Center,
                        color = Color.Gray,
                        fontSize = 14.sp
                    )
                    Spacer(modifier = Modifier.height(16.dp))
                    Icon(
                        Icons.Default.Favorite,
                        contentDescription = null,
                        tint = Color.Red,
                        modifier = Modifier.size(32.dp)
                    )
                }
            } else {
                Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                    searchResults.forEach { track ->
                        TrackItem(track = track, onClick = {
                            currentlyPlayingVm.setTrack(track)
                            navController.navigate("musicWithNavBar")
                            //navController.popBackStack() // retourne à MusicScreen
                        })
                    }
                }
            }
        }
    }
}

@Composable
fun TrackItem(track: TrackResult,  onClick: () -> Unit) {
    var isLiked by remember { mutableStateOf(false) }

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .background(Color(0xFFFFF3E0), shape = RoundedCornerShape(12.dp))
            .padding(12.dp)
            .clickable { onClick() },
        verticalAlignment = Alignment.CenterVertically
    ) {
        Image(
            painter = rememberAsyncImagePainter(track.imageUrl),
            contentDescription = track.name,
            contentScale = ContentScale.Crop,
            modifier = Modifier
                .size(56.dp)
                .clip(RoundedCornerShape(12.dp))
        )

        Spacer(modifier = Modifier.width(12.dp))

        Column(modifier = Modifier.weight(1f)) {
            Text(track.name, fontWeight = FontWeight.Bold, fontSize = 16.sp)
            Text(track.artist, fontSize = 12.sp, color = Color.Gray)
        }

        Spacer(modifier = Modifier.width(8.dp))

        Icon(
            imageVector = if (isLiked) Icons.Default.Favorite else Icons.Default.FavoriteBorder,
            contentDescription = "Like",
            tint = if (isLiked) Color.Red else Color.Gray,
            modifier = Modifier
                .size(24.dp)
                .clickable {
                    isLiked = !isLiked
                    // TODO : Ajouter ici l'action pour insérer dans une playlist (pour apres)
                }
        )
    }
}

/**
 * Fait une recherche dans l'API Spotify
 */
fun searchMusicOnSpotify(context: Context, query: String, onResult: (List<TrackResult>) -> Unit) {
    //val accessToken = MainActivity.SpotifySession.accessToken

    //val accessToken = "BQBUViNysPLVDDWKqrNQ8TdEsjvcFpRsVcQRZK-0QYQDMMZmSJCFcxqBTifXio4dLAbnA6yEsbQQpfTAVROyttH3DoPicUXNef7-imEuaC8EdTnYjU0jml-whyUUgd-1bj3muTx5bh4oXGTqUCiFARfLhr6HosMAYEABQIuaQBkMqjpMUBnasS6-z2oAGwMZSBWAD4Qhf9abHchH964yhTAv3fMexg_NP9UKt95rF9QUV8A4xhSgzvExkERBYiRA04TEV2-Xi1fr"
    val accessToken = getSpotifyAccessToken(context)

    if (accessToken == null) {
        Log.e("SpotifySearch", "No Access Token available")
        return
    }

    val client = OkHttpClient()
    val url = "https://api.spotify.com/v1/search?q=${query.replace(" ", "%20")}&type=track&limit=10"

    val request = Request.Builder()
        .url(url)
        .addHeader("Authorization", "Bearer $accessToken")
        .build()

    client.newCall(request).enqueue(object : Callback {
        override fun onFailure(call: Call, e: IOException) {
            Log.e("SpotifySearch", "Erreur réseau: ${e.message}")
        }

        override fun onResponse(call: Call, response: Response) {
            if (response.isSuccessful) {
                val body = response.body?.string()
                val results = mutableListOf<TrackResult>()

                if (body != null) {
                    val json = JSONObject(body)
                    val tracks = json.getJSONObject("tracks").getJSONArray("items")

                    for (i in 0 until tracks.length()) {
                        val track = tracks.getJSONObject(i)
                        val name = track.getString("name")
                        val artist = track.getJSONArray("artists").getJSONObject(0).getString("name")
                        val imageUrl = track.getJSONObject("album")
                            .getJSONArray("images")
                            .getJSONObject(0)
                            .getString("url")

                        results.add(TrackResult(name, artist, imageUrl))
                    }
                }

                onResult(results)
            } else {
                Log.e("SpotifySearch", "Erreur API: ${response.code}")
            }
        }
    })
}

fun getSpotifyAccessToken(context: Context): String? {
    val prefs = context.getSharedPreferences("SpotifyPrefs", Context.MODE_PRIVATE)
    return prefs.getString("access_token", null)
}


@Preview
@Composable
fun SearchMusicScreenPreview() {
    SearchMusicScreen(navController = NavController(LocalContext.current), currentlyPlayingVm = CurrentlyPlayingViewModel())
}


