package com.example.fitcoach.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.fitcoach.ui.screen.SpotifyPlaylist
import com.example.fitcoach.ui.screen.fetchSpotifyPlaylists
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class MusicViewModel : ViewModel() {

    private val _playlists = MutableStateFlow<List<SpotifyPlaylist>>(emptyList())
    val playlists: StateFlow<List<SpotifyPlaylist>> get() = _playlists

    fun loadPlaylists(accessToken: String) {
        viewModelScope.launch(Dispatchers.IO) {
            val result = fetchSpotifyPlaylists(accessToken)
            _playlists.value = result
        }
    }
}
