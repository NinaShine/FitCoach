package com.example.fitcoach

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import okhttp3.*
import org.json.JSONObject
import java.io.IOException
import androidx.core.content.edit
import com.example.fitcoach.ui.screen.fetchSpotifyPlaylists
import com.example.fitcoach.ui.screen.fetchSpotifyProfile

class SpotifyAuthActivity : ComponentActivity() {
    private val redirectUri = "fitcoach://callback"
    //private val backendUrl = "https://fitcoachspotify.vercel.app/"
    private val backendUrl = "https://fitcoachspotify.vercel.app/?provider=spotify"


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
                //exchangeCodeForAccessToken(code)
                getAccessTokenFromBackend(this, code) { accessToken ->
                    Log.d("SpotifyAuth", "AccessToken récupéré via backend : $accessToken")

                    saveAccessTokenLocally(accessToken)

                    runOnUiThread {
                        val intent = Intent(this, MainActivity::class.java).apply {
                            putExtra("navigateTo", "musicWithNavBar")
                        }
                        startActivity(intent)
                        finish()
                    }
                }
            } else {
                Log.e("SpotifyAuth", "Aucun code reçu dans le callback URI")
            }
        }

    }

    private fun getAccessTokenFromBackend(context: Context, code: String, onSuccess: (String) -> Unit) {
        val client = OkHttpClient()
        val requestUrl = "$backendUrl&code=$code"
        Log.d("SpotifyBack", "backend récupéré: $requestUrl")


        val request = Request.Builder()
            //.url(backendUrl)
            .url(requestUrl)
            .get()
            .build()

        client.newCall(request).enqueue(object : Callback {
            override fun onFailure(call: Call, e: IOException) {
                Log.e("SpotifyAuth", "❌ Erreur réseau vers backend Vercel : ${e.message}")
            }

            override fun onResponse(call: Call, response: Response) {
                if (response.isSuccessful) {
                    val json = JSONObject(response.body?.string() ?: "")
                    val accessToken = json.getString("access_token")
                    Log.d("SpotifyAuth", "✅ Token reçu via backend : $accessToken")

                    onSuccess(accessToken)
                } else {
                    Log.e("SpotifyAuth", "❌ Réponse backend invalide : ${response.code}")
                }
            }
        })
    }
/*
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

                    saveAccessTokenLocally(accessToken)


                    runOnUiThread {
                        val intent =
                            Intent(this@SpotifyAuthActivity, MainActivity::class.java).apply {
                                putExtra("navigateTo", "musicWithNavBar")
                            }
                        //intent.putExtra("accessToken", accessToken)
                        startActivity(intent)
                        finish()
                    }

                } else {
                    Log.e("SpotifyAuth", "Erreur de réponse: ${response.message}")
                }
            }
        })


    }

 */

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
    /*
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

     */

    fun fetchSpotifyPlaylists(accessToken: String) {
        val client = OkHttpClient()

        val request = Request.Builder()
            .url("https://api.spotify.com/v1/me/playlists")
            .addHeader("Authorization", "Bearer $accessToken")
            .build()

        client.newCall(request).enqueue(object : Callback {
            override fun onFailure(call: Call, e: IOException) {
                Log.e("SpotifyPlaylist", "❌ Erreur réseau : ${e.message}")
            }

            override fun onResponse(call: Call, response: Response) {
                if (response.isSuccessful) {
                    val responseBody = response.body?.string()
                    Log.d("SpotifyPlaylist", "✅ Réponse : $responseBody")

                    responseBody?.let {
                        val json = JSONObject(it)
                        val items = json.getJSONArray("items")

                        for (i in 0 until items.length()) {
                            val playlist = items.getJSONObject(i)
                            val name = playlist.getString("name")
                            val id = playlist.getString("id")

                            Log.d("SpotifyPlaylist", "🎵 Playlist : $name (ID: $id)")
                        }
                    }
                } else {
                    Log.e("SpotifyPlaylist", "❌ Erreur API Spotify : ${response.code}")
                }
            }
        })
    }



}
