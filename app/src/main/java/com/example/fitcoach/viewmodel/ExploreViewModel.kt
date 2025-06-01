package com.example.fitcoach.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.fitcoach.data.model.YouTubeVideo
import com.example.fitcoach.data.remote.RetrofitInstance
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class ExploreViewModel : ViewModel() {

    private val _videos = MutableStateFlow<List<YouTubeVideo>>(emptyList())
    val videos: StateFlow<List<YouTubeVideo>> = _videos

    private val pamelaChannelId = "UChVRfsT_ASBZk10o0An7Ucg"

    init {
        searchVideos("abs") // Exemple de filtre initial
    }

    fun searchVideos(query: String = "") {
        viewModelScope.launch {
            try {
                val response = RetrofitInstance.youtubeApi.searchVideosByChannelAndQuery(
                    part = "snippet",
                    channelId = pamelaChannelId,
                    query = query,
                    apiKey = "AIzaSyDtQWHQobT6-zR0e4udK8WbRmKJ1XX2ANg"
                )
                _videos.value = response.items
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }
}

