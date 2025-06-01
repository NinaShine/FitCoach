package com.example.fitcoach.viewmodel

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.fitcoach.data.model.CurrentlyPlaying
import com.example.fitcoach.data.model.TrackResult
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody
import org.json.JSONObject

class CurrentlyPlayingViewModel : ViewModel() {
    private val _track = MutableStateFlow<CurrentlyPlaying?>(null)
    val track: StateFlow<CurrentlyPlaying?> = _track
    private var manuallySet = false

    fun loadCurrentlyPlaying(accessToken: String) {
        if (manuallySet) return
        viewModelScope.launch(Dispatchers.IO) {
            val playing = fetchCurrentlyPlaying(accessToken)
            _track.value = playing
        }
    }

    private fun fetchCurrentlyPlaying(accessToken: String): CurrentlyPlaying? {
        val client = OkHttpClient()
        val req = Request.Builder()
            .url("https://api.spotify.com/v1/me/player/currently-playing")
            .addHeader("Authorization", "Bearer $accessToken")
            .build()

        client.newCall(req).execute().use { resp ->
            if (resp.isSuccessful && resp.code == 200) {
                val body = resp.body?.string() ?: return null
                val j = JSONObject(body)
                val item = j.getJSONObject("item")
                return CurrentlyPlaying(
                    title     = item.getString("name"),
                    artist    = item.getJSONArray("artists")
                        .getJSONObject(0)
                        .getString("name"),
                    imageUrl  = item.getJSONObject("album")
                        .getJSONArray("images")
                        .getJSONObject(0)
                        .getString("url"),
                    isPlaying = j.getBoolean("is_playing"),
                    progressMs  = j.getInt("progress_ms"),
                    durationMs  = item.getInt("duration_ms")
                )
            }
        }
        return null
    }

    fun setTrack(trackResult: TrackResult) {
        _track.value = CurrentlyPlaying(
            title = trackResult.name,
            artist = trackResult.artist,
            imageUrl = trackResult.imageUrl,
            isPlaying = true,
            progressMs = 0,
            durationMs = 0
        )
        manuallySet = true

    }

    fun updateCurrentlyPlaying(accessToken: String) {
        viewModelScope.launch(Dispatchers.IO) {
            val playing = fetchCurrentlyPlaying(accessToken)
            _track.value = playing
        }
    }

    fun pauseCurrentlyPlaying(accessToken: String) {
        viewModelScope.launch(Dispatchers.IO) {
            val client = OkHttpClient()
            val req = Request.Builder()
                .url("https://api.spotify.com/v1/me/player/pause")
                .post(RequestBody.create(null, ByteArray(0)))
                .addHeader("Authorization", "Bearer $accessToken")
                .build()
            client.newCall(req).execute().close()
            _track.value = _track.value?.copy(isPlaying = false)
        }
    }


    fun resumeCurrentlyPlaying(accessToken: String) {
        viewModelScope.launch(Dispatchers.IO) {
            val client = OkHttpClient()
            val req = Request.Builder()
                .url("https://api.spotify.com/v1/me/player/play")
                .post(RequestBody.create(null, ByteArray(0)))
                .addHeader("Authorization", "Bearer $accessToken")
                .build()
            val response = client.newCall(req).execute()
            Log.d("SpotifyResume", "Code réponse = ${response.code}")

            if (!response.isSuccessful) {
                Log.e("SpotifyResume", "Erreur de lecture : ${response.body?.string()}")
            }

            _track.value = _track.value?.copy(isPlaying = response.isSuccessful)
            response.close()
        }
    }

    fun skipNext(accessToken: String) {
        viewModelScope.launch(Dispatchers.IO) {
            val client = OkHttpClient()
            val req = Request.Builder()
                .url("https://api.spotify.com/v1/me/player/next")
                .post(RequestBody.create(null, ByteArray(0)))
                .addHeader("Authorization", "Bearer $accessToken")
                .build()
            client.newCall(req).execute().close()
            _track.value = _track.value?.copy(isPlaying = true)

        }
    }

    fun skipPrevious(accessToken: String) {
        viewModelScope.launch(Dispatchers.IO) {
            val client = OkHttpClient()
            val req = Request.Builder()
                .url("https://api.spotify.com/v1/me/player/previous")
                .post(RequestBody.create(null, ByteArray(0)))
                .addHeader("Authorization", "Bearer $accessToken")
                .build()
            client.newCall(req).execute().close()
            _track.value = _track.value?.copy(isPlaying = true)
        }
    }

    fun startProgressUpdater() {
        viewModelScope.launch {
            while (_track.value?.isPlaying == true) {
                delay(1000)
                _track.value = _track.value?.let {
                    it.copy(progressMs = it.progressMs + 1000)
                }
            }
        }
    }



}
