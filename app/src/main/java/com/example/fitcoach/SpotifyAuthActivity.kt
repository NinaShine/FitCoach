package com.example.fitcoach

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import com.example.fitcoach.MainActivity.SpotifySession
import okhttp3.*
import org.json.JSONObject
import java.io.IOException
import androidx.core.content.edit

class SpotifyAuthActivity : ComponentActivity() {

    private val clientId = "23b18fa0f82640f4a7c372678881a764"          // 🔥 remplace ici
    private val clientSecret = "09824133c068416dbaa40cd7bac9c99f"  // 🔥 remplace ici
    private val redirectUri = "fitcoach://callback"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        handleSpotifyRedirect(intent)
    }

    override fun onNewIntent(intent: Intent?) {
        super.onNewIntent(intent)
        handleSpotifyRedirect(intent)
    }

    private fun handleSpotifyRedirect(intent: Intent?) {
        val uri: Uri? = intent?.data
        Log.d("SpotifyAuth", "handleSpotifyRedirect appelé avec URI = $uri")

        if (uri != null && uri.toString().startsWith(redirectUri)) {
            val code = uri.getQueryParameter("code")
            Log.d("SpotifyAuth", "Code récupéré: $code")

            if (code != null) {
                exchangeCodeForAccessToken(code)
            } else {
                Log.e("SpotifyAuth", "Pas de code dans l'URI")
            }
        }
    }

    private fun exchangeCodeForAccessToken(code: String) {
        val client = OkHttpClient()

        val requestBody = FormBody.Builder()
            .add("grant_type", "authorization_code")
            .add("code", code)
            .add("redirect_uri", redirectUri)
            .add("client_id", clientId)
            .add("client_secret", clientSecret)
            .build()

        val request = Request.Builder()
            .url("https://accounts.spotify.com/api/token")
            .post(requestBody)
            .build()

        client.newCall(request).enqueue(object : Callback {
            override fun onFailure(call: Call, e: IOException) {
                Log.e("SpotifyAuth", "Erreur réseau : ${e.message}")
            }

            override fun onResponse(call: Call, response: Response) {
                if (response.isSuccessful) {
                    val responseBody = response.body?.string()
                    Log.d("SpotifyAuth", "Réponse brute: $responseBody")

                    val json = JSONObject(responseBody ?: "")
                    val accessToken = json.getString("access_token")

                    Log.d("SpotifyAuth", "AccessToken récupéré : $accessToken")



                    fetchSpotifyProfile(accessToken)
                    fetchSpotifyPlaylists(accessToken)

                    //SpotifySession.accessToken = accessToken

                    saveAccessTokenLocally(accessToken)


                    runOnUiThread {
                        val intent = Intent(this@SpotifyAuthActivity, SpotifyMusicActivity::class.java)
                        //intent.putExtra("accessToken", accessToken)
                        startActivity(intent)
                        finish() // on ferme SpotifyAuthActivity
                    }


                    // ➡️ Ici tu peux sauvegarder le token dans SharedPreferences par exemple !
                } else {
                    Log.e("SpotifyAuth", "Erreur de réponse: ${response.message}")
                }
            }
        })


    }

    private fun saveAccessTokenLocally(token: String) {
        val prefs = getSharedPreferences("SpotifyPrefs", Context.MODE_PRIVATE)
        prefs.edit() { putString("access_token", token) }
        Log.d("SpotifyAuth", "✅ Token sauvegardé dans SharedPreferences")
    }


    fun fetchSpotifyProfile(accessToken: String) {
        val client = OkHttpClient()

        val request = Request.Builder()
            .url("https://api.spotify.com/v1/me")
            .addHeader("Authorization", "Bearer $accessToken")
            .build()

        client.newCall(request).execute().use { response ->
            if (response.isSuccessful) {
                val responseBody = response.body?.string()
                Log.d("SpotifyProfile", "Réponse : $responseBody")

                responseBody?.let {
                    val json = JSONObject(it)
                    val displayName = json.getString("display_name")
                    val email = json.getString("email")
                    val id = json.getString("id")

                    Log.d("SpotifyProfile", "Nom : $displayName, Email : $email, ID : $id")
                }
            } else {
                Log.e("SpotifyProfile", "Erreur : ${response.code}")
            }
        }
    }
    fun fetchSpotifyPlaylists(accessToken: String) {
        val client = OkHttpClient()

        val request = Request.Builder()
            .url("https://api.spotify.com/v1/me/playlists")
            .addHeader("Authorization", "Bearer $accessToken")
            .build()

        client.newCall(request).execute().use { response ->
            if (response.isSuccessful) {
                val responseBody = response.body?.string()
                Log.d("SpotifyPlaylist", "Réponse : $responseBody")

                responseBody?.let {
                    val json = JSONObject(it)
                    val items = json.getJSONArray("items")

                    for (i in 0 until items.length()) {
                        val playlist = items.getJSONObject(i)
                        val name = playlist.getString("name")
                        val id = playlist.getString("id")

                        Log.d("SpotifyPlaylist", "Playlist : $name (ID: $id)")
                    }
                }
            } else {
                Log.e("SpotifyPlaylist", "Erreur : ${response.code}")
            }
        }
    }


}
