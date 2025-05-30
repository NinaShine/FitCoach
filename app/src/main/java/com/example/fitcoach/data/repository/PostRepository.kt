package com.example.fitcoach.data.repository

import com.example.fitcoach.data.model.Post
import com.example.fitcoach.data.model.UserProfile
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.tasks.await

object PostRepository {
    private val db = FirebaseFirestore.getInstance()

    // ✅ Récupère tous les posts
    suspend fun getAllPosts(): List<Post> {
        val snapshot = db.collection("posts").get().await()
        return snapshot.documents.mapNotNull {
            it.toObject(Post::class.java)?.copy(id = it.id)
        }
    }

    // ✅ Liker un post (ajoute userId dans le champ "likes")
    suspend fun likePost(postId: String, userId: String) {
        db.collection("posts").document(postId)
            .update("likes", FieldValue.arrayUnion(userId))
            .await()
    }

    // ✅ Optionnel : unliker un post
    suspend fun unlikePost(postId: String, userId: String) {
        db.collection("posts").document(postId)
            .update("likes", FieldValue.arrayRemove(userId))
            .await()
    }

    // ✅ Récupérer le UserProfile de l’auteur d’un post
    suspend fun getUserProfilesForPosts(posts: List<Post>): Map<String, UserProfile> {
        val userIds = posts.map { it.userId }.distinct()
        val usersMap = mutableMapOf<String, UserProfile>()

        for (uid in userIds) {
            val doc = db.collection("users").document(uid).get().await()
            doc.toObject(UserProfile::class.java)?.let {
                usersMap[uid] = it.copy(uid = doc.id)
            }
        }
        return usersMap
    }
}
