package com.example.fitcoach.viewmodel

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.fitcoach.data.model.Comment
import com.example.fitcoach.data.model.Post
import com.example.fitcoach.data.model.UserProfile
import com.example.fitcoach.data.repository.PostRepository
import com.example.fitcoach.data.repository.UserRepository
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import java.time.Instant

class FeedViewModel : ViewModel() {

    private val _posts = MutableStateFlow<List<Post>>(emptyList())
    val posts: StateFlow<List<Post>> = _posts

    private val _userProfiles = MutableStateFlow<Map<String, UserProfile>>(emptyMap())
    val userProfiles: StateFlow<Map<String, UserProfile>> = _userProfiles


    private val _currentUserId = MutableStateFlow<String?>(null)
    val currentUserId: StateFlow<String?> = _currentUserId

    private val _commentCounts = MutableStateFlow<Map<String, Int>>(emptyMap())
    val commentCounts: StateFlow<Map<String, Int>> = _commentCounts

    private val db = FirebaseFirestore.getInstance()


    fun loadFeed(currentUid: String) {
        _currentUserId.value = currentUid

        viewModelScope.launch {
            try {
                val postList = PostRepository.getAllPosts()
                _posts.value = postList.sortedByDescending { it.timestamp }

                val users = PostRepository.getUserProfilesForPosts(postList)
                _userProfiles.value = users

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

                val updatedCount = PostRepository.getCommentCount(postId)
                _commentCounts.value = _commentCounts.value.toMutableMap().apply {
                    this[postId] = updatedCount
                }

            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }
    fun loadFriends(currentUserId: String, onLoaded: (List<String>) -> Unit) {
        db.collection("users")
            .document(currentUserId)
            .collection("friends")
            .get()
            .addOnSuccessListener { result ->
                val friendIds = result.documents.mapNotNull { it.id }
                onLoaded(friendIds)
            }
            .addOnFailureListener { e ->
                e.printStackTrace()
                onLoaded(emptyList())
            }
    }
    fun updateCurrentUserFriends(friends: List<String>) {
        val updatedProfiles = _userProfiles.value.toMutableMap()
        val currentUser = updatedProfiles[_currentUserId.value] ?: return
        updatedProfiles[_currentUserId.value!!] = currentUser.copy(friends = friends as MutableList<String>)
        _userProfiles.value = updatedProfiles
    }

    private val _currentUserFriends = MutableStateFlow<Set<String>>(emptySet())
    val currentUserFriends: StateFlow<Set<String>> = _currentUserFriends.asStateFlow()

    fun loadCurrentUserFriends(currentUserId: String) {
        db.collection("users")
            .document(currentUserId)
            .collection("friends")
            .addSnapshotListener { snapshot, error ->
                if (error != null) {
                    println("Erreur lors du chargement des amis : ${error.message}")
                    return@addSnapshotListener
                }

                val friendIds = snapshot?.documents?.map { it.id }?.toSet() ?: emptySet()
                _currentUserFriends.value = friendIds
            }
    }

    @RequiresApi(Build.VERSION_CODES.O)
    fun followUser(targetUserId: String, currentUserId: String) {
        val friendRef = db.collection("users")
            .document(currentUserId)
            .collection("friends")
            .document(targetUserId)

        val friendData = mapOf(
            "followedAt" to Instant.now().toString(),
            "userId" to targetUserId
        )

        friendRef.set(friendData)
            .addOnSuccessListener {
                println("Utilisateur $targetUserId ajouté aux amis de $currentUserId")

                val currentFriends = _currentUserFriends.value.toMutableSet()
                currentFriends.add(targetUserId)
                _currentUserFriends.value = currentFriends
            }
            .addOnFailureListener { e ->
                println(" Erreur lors de l'ajout d'un ami : ${e.message}")
            }
    }
}







