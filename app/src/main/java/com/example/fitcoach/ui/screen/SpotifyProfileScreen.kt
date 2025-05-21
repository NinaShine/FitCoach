package com.example.fitcoach.ui.screen

import android.util.Log
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import coil.compose.rememberAsyncImagePainter
import com.example.fitcoach.R
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import okhttp3.OkHttpClient
import okhttp3.Request
import org.json.JSONObject

data class SpotifyUser(val displayName: String, val email: String, val imageUrl: String?)
data class SpotifyPlaylist(val name: String, val id: String, val imageUrl: String?)

@Composable
fun SpotifyProfileScreen(navController: NavController, accessToken: String) {
    var user by remember { mutableStateOf<SpotifyUser?>(null) }
    var playlists by remember { mutableStateOf<List<SpotifyPlaylist>>(emptyList()) }
    val scope = rememberCoroutineScope()

    LaunchedEffect(accessToken) {
        scope.launch(Dispatchers.IO) {
            user = fetchSpotifyProfile(accessToken)
            playlists = fetchSpotifyPlaylists(accessToken)
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.White)
            .padding(16.dp)
    ) {
        user?.let {
            Row(verticalAlignment = Alignment.CenterVertically) {
                if (it.imageUrl != null) {
                    Image(
                        painter = rememberAsyncImagePainter(it.imageUrl),
                        contentDescription = "Profile Picture",
                        modifier = Modifier
                            .size(80.dp)
                            .clip(CircleShape),
                        contentScale = ContentScale.Crop
                    )
                } else {
                    Image(
                        painter = painterResource(id = R.drawable.july_photo_profile),
                        contentDescription = "Default",
                        modifier = Modifier
                            .size(80.dp)
                            .clip(CircleShape)
                    )
                }
                Spacer(modifier = Modifier.width(16.dp))
                Column {
                    Text(it.displayName, fontSize = 20.sp, fontWeight = FontWeight.Bold)
                    Text(it.email, fontSize = 14.sp, color = Color.Gray)
                }
            }
        }

        Spacer(modifier = Modifier.height(24.dp))

        Text("Your Playlists", fontSize = 18.sp, fontWeight = FontWeight.SemiBold)

        Spacer(modifier = Modifier.height(8.dp))

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
                        Text(playlist.name, fontWeight = FontWeight.Bold, fontSize = 16.sp)
                    }
                }
            }
        }
    }
}

suspend fun fetchSpotifyProfile(accessToken: String): SpotifyUser? {
    return try {
        val client = OkHttpClient()
        val request = Request.Builder()
            .url("https://api.spotify.com/v1/me")
            .addHeader("Authorization", "Bearer $accessToken")
            .build()

        client.newCall(request).execute().use { response ->
            if (response.isSuccessful) {
                val responseBody = response.body?.string()
                val json = JSONObject(responseBody!!)
                val name = json.getString("display_name")
                val email = json.getString("email")
                val images = json.getJSONArray("images")
                val imageUrl = if (images.length() > 0) images.getJSONObject(0).getString("url") else null

                SpotifyUser(name, email, imageUrl)
            } else {
                Log.e("SpotifyProfile", "Erreur profil: ${response.code}")
                null
            }
        }
    } catch (e: Exception) {
        Log.e("SpotifyProfile", "Exception: ${e.localizedMessage}")
        null
    }
}

suspend fun fetchSpotifyPlaylists(accessToken: String): List<SpotifyPlaylist> {
    return try {
        val client = OkHttpClient()
        val request = Request.Builder()
            .url("https://api.spotify.com/v1/me/playlists")
            .addHeader("Authorization", "Bearer $accessToken")
            .build()

        client.newCall(request).execute().use { response ->
            if (response.isSuccessful) {
                val responseBody = response.body?.string()
                val json = JSONObject(responseBody!!)
                val items = json.getJSONArray("items")
                val playlists = mutableListOf<SpotifyPlaylist>()

                for (i in 0 until items.length()) {
                    val playlist = items.getJSONObject(i)
                    val name = playlist.getString("name")
                    val id = playlist.getString("id")

                    val images = playlist.getJSONArray("images")
                    val imageUrl = if (images.length() > 0) {
                        images.getJSONObject(0).getString("url")
                    } else {
                        ""
                    }

                    playlists.add(SpotifyPlaylist(name, id, imageUrl))
                }
                playlists
            } else {
                Log.e("SpotifyPlaylist", "Erreur playlists: ${response.code}")
                emptyList()
            }
        }
    } catch (e: Exception) {
        Log.e("SpotifyPlaylist", "Exception: ${e.localizedMessage}")
        emptyList()
    }
}


@Preview
@Composable
fun SpotifyProfileScreenPreview() {
    SpotifyProfileScreen(navController = rememberNavController(), accessToken = getSpotifyAccessToken(LocalContext.current).toString())
}
