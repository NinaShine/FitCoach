package com.example.fitcoach.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.fitcoach.data.model.Comment
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

    private val _commentCounts = MutableStateFlow<Map<String, Int>>(emptyMap())
    val commentCounts: StateFlow<Map<String, Int>> = _commentCounts


    fun loadFeed(currentUid: String) {
        _currentUserId.value = currentUid

        viewModelScope.launch {
            try {
                // 1. Charger tous les posts
                val postList = PostRepository.getAllPosts()
                _posts.value = postList.sortedByDescending { it.timestamp }

                // 2. Charger les auteurs de chaque post
                val users = PostRepository.getUserProfilesForPosts(postList)
                _userProfiles.value = users

                // 3. Charger le nombre de commentaires pour chaque post
                val commentMap = mutableMapOf<String, Int>()
                for (post in postList) {
                    val count = PostRepository.getCommentCount(post.id)
                    commentMap[post.id] = count
                }
                _commentCounts.value = commentMap

            } catch (e: Exception) {
                e.printStackTrace()
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

    fun submitComment(postId: String, userId: String, text: String) {
        viewModelScope.launch {
            val comment = Comment(
                postId = postId,
                userId = userId,
                content = text,
                timestamp = System.currentTimeMillis()
            )

            try {
                PostRepository.addComment(postId, comment)

                // ✅ Met à jour uniquement le compteur du post concerné
                val updatedCount = PostRepository.getCommentCount(postId)
                _commentCounts.value = _commentCounts.value.toMutableMap().apply {
                    this[postId] = updatedCount
                }

            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }



}
