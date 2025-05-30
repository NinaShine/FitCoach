package com.example.fitcoach.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.fitcoach.data.model.Post
import com.example.fitcoach.data.model.UserProfile
import com.example.fitcoach.data.repository.PostRepository
import com.example.fitcoach.data.repository.UserRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class FeedViewModel : ViewModel() {

    private val _posts = MutableStateFlow<List<Post>>(emptyList())
    val posts: StateFlow<List<Post>> = _posts

    private val _userProfiles = MutableStateFlow<Map<String, UserProfile>>(emptyMap())
    val userProfiles: StateFlow<Map<String, UserProfile>> = _userProfiles

    private val _currentUserId = MutableStateFlow<String?>(null)
    val currentUserId: StateFlow<String?> = _currentUserId

    fun loadFeed(currentUid: String) {
        _currentUserId.value = currentUid

        viewModelScope.launch {
            try {
                // 1. Charger tous les posts
                val postList = PostRepository.getAllPosts()
                _posts.value = postList.sortedByDescending { it.timestamp }

                // 2. Charger les auteurs des posts
                val users = PostRepository.getUserProfilesForPosts(postList)
                _userProfiles.value = users
            } catch (e: Exception) {
                e.printStackTrace()
                // TODO : ajouter gestion d'erreur si besoin
            }
        }
    }

    fun likePost(postId: String) {
        val uid = _currentUserId.value ?: return

        viewModelScope.launch {
            try {
                PostRepository.likePost(postId, uid)
                refreshPosts()
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    fun followUser(targetUserId: String) {
        viewModelScope.launch {
            try {
                UserRepository.followUser(
                    targetUserId,
                    onSuccess = { /* Optionnel : afficher message */ },
                    onFailure = { it.printStackTrace() }
                )
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    private fun refreshPosts() {
        val uid = _currentUserId.value ?: return
        loadFeed(uid)
    }
}
