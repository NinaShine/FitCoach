package com.example.fitcoach.viewmodel

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.fitcoach.data.model.Song
import com.example.fitcoach.ui.screen.SpotifyPlaylist
import com.example.fitcoach.ui.screen.fetchSpotifyPlaylists
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class MusicViewModel : ViewModel() {

    private val _playlists = MutableStateFlow<List<SpotifyPlaylist>>(emptyList())
    val playlists: StateFlow<List<SpotifyPlaylist>> get() = _playlists

    private val _favorites = MutableStateFlow<List<Song>>(emptyList())
    val favorites: StateFlow<List<Song>> = _favorites

    fun loadPlaylists(accessToken: String) {
        viewModelScope.launch(Dispatchers.IO) {
            val result = fetchSpotifyPlaylists(accessToken)
            _playlists.value = result
        }
    }

    fun loadFavorites() {
        val uid = FirebaseAuth.getInstance().currentUser?.uid ?: return
        val db = FirebaseFirestore.getInstance()

        db.collection("users").document(uid)
            .collection("favorites")
            .orderBy("timestamp", Query.Direction.DESCENDING)
            .get()
            .addOnSuccessListener { result ->
                val favs = result.map {
                    Song(
                        title = it.getString("name") ?: "",
                        artist = it.getString("artist") ?: "",
                        imageRes = 0, // on utilisera imageUrl dans l'UI
                        imageUrl = it.getString("imageUrl") ?: ""
                    )
                }
                _favorites.value = favs
            }
    }

    private val _favoriteTracks = MutableStateFlow<List<Song>>(emptyList())
    val favoriteTracks: StateFlow<List<Song>> = _favoriteTracks

    fun loadFavoriteTracks() {
        val uid = FirebaseAuth.getInstance().currentUser?.uid ?: return
        val db = FirebaseFirestore.getInstance()

        db.collection("users")
            .document(uid)
            .collection("favorites")
            .get()
            .addOnSuccessListener { result ->
                val songs = result.mapNotNull { doc ->
                    val data = doc.data
                    Song(
                        title = data["title"] as? String ?: "",
                        artist = data["artist"] as? String ?: "",
                        imageRes = 0,
                        imageUrl = data["imageUrl"] as? String ?: ""
                    )
                }
                _favoriteTracks.value = songs
            }
            .addOnFailureListener { e ->
                Log.e("Firestore", "Erreur récupération favoris: ${e.message}")
            }
    }


}
