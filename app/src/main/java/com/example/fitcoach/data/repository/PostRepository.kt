package com.example.fitcoach.data.repository

import com.example.fitcoach.data.model.Comment
import com.example.fitcoach.data.model.Post
import com.example.fitcoach.data.model.UserProfile
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.tasks.await

object PostRepository {
    private val db = FirebaseFirestore.getInstance()

    suspend fun getAllPosts(): List<Post> {
        val snapshot = db.collection("posts").get().await()
        return snapshot.documents.mapNotNull {
            it.toObject(Post::class.java)?.copy(id = it.id)
        }
    }


    suspend fun likePost(postId: String, userId: String) {
        db.collection("posts").document(postId)
            .update("likes", FieldValue.arrayUnion(userId))
            .await()
    }

    suspend fun unlikePost(postId: String, userId: String) {
        db.collection("posts").document(postId)
            .update("likes", FieldValue.arrayRemove(userId))
            .await()
    }

    suspend fun getUserProfilesForPosts(posts: List<Post>): Map<String, UserProfile> {
        val db = FirebaseFirestore.getInstance()
        val userIds = posts.map { it.userId }.toSet()
        val profiles = mutableMapOf<String, UserProfile>()

        for (uid in userIds) {
            val doc = db.collection("users").document(uid).get().await()
            val profile = doc.toObject(UserProfile::class.java)?.copy(uid = uid)

            val friendsSnapshot = db.collection("users")
                .document(uid)
                .collection("friends")
                .get()
                .await()

            val friendsList = friendsSnapshot.documents.map { it.id }

            if (profile != null) {
                profiles[uid] = profile.copy(friends = friendsList as MutableList<String>)
            }
        }

        return profiles
    }


    suspend fun addComment(postId: String, comment: Comment) {
        val db = FirebaseFirestore.getInstance()
        db.collection("posts")
            .document(postId)
            .collection("comments")
            .add(comment)
            .await()
    }

    suspend fun getCommentCount(postId: String): Int {
        val snapshot = FirebaseFirestore.getInstance()
            .collection("posts")
            .document(postId)
            .collection("comments")
            .get()
            .await()

        return snapshot.size()
    }


}
