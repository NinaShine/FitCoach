package com.example.fitcoach.ui.screen.section_social

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ChatBubbleOutline
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import coil.compose.rememberAsyncImagePainter
import com.example.fitcoach.R
import com.example.fitcoach.data.model.Post
import com.example.fitcoach.data.model.UserProfile
import com.example.fitcoach.viewmodel.FeedViewModel

@Composable
fun FeedScreen(currentUid: String, navController: NavController) {
    val vm: FeedViewModel = viewModel()
    val posts by vm.posts.collectAsState()
    val users by vm.userProfiles.collectAsState()
    val commentCounts by vm.commentCounts.collectAsState()

    LaunchedEffect(Unit) {
        vm.loadFeed(currentUid)
    }

    Column {
        TopBar(
            user = users[currentUid],
            navController = navController,
            onProfileClick = { navController.navigate("profile") },
            onChallengeClick = { navController.navigate("challenges") }
        )

        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(12.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            items(posts) { post ->
                val author = users[post.userId]
                val commentCount = commentCounts[post.id] ?: 0

                PostCard(
                    post = post,
                    user = author,
                    currentUserId = currentUid,
                    commentCount = commentCount,
                    onLikeClick = { vm.likePost(post.id) },
                    onFollowClick = { vm.followUser(post.userId) },
                    onCommentSubmit = { text ->
                        vm.submitComment(post.id, currentUid, text)
                    }
                )
            }
        }
    }
}

@Composable
fun TopBar(
    user: UserProfile?,
    onProfileClick: () -> Unit,
    navController: NavController,
    onChallengeClick: () -> Unit
) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .padding(12.dp)
    ) {
        // 🏷️ Titre centré
        Text(
            text = "Feed",
            fontWeight = FontWeight.Bold,
            fontSize = 20.sp,
            modifier = Modifier.align(Alignment.Center)
        )

        // 🔘 Boutons à droite (emoji + avatar)
        Row(
            modifier = Modifier.align(Alignment.CenterEnd),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                "🏆",
                fontSize = 22.sp,
                modifier = Modifier
                    .padding(end = 12.dp)
                    .clickable { onChallengeClick() }
            )
            Image(
                painter = painterResource(id = R.drawable.july_photo_profile),
                contentDescription = null,
                contentScale = ContentScale.Crop,
                modifier = Modifier
                    .size(45.dp)
                    .clip(CircleShape)
                    .clickable {
                        navController.navigate("profile")
                    }
            )
        }

        // 🔙 Optionnel : "Discover" à gauche
        Text(
            "Discover",
            color = Color.Gray,
            modifier = Modifier.align(Alignment.CenterStart)
        )
    }
}

@Composable
fun CommentDialog(
    showDialog: Boolean,
    onDismiss: () -> Unit,
    onSubmit: (String) -> Unit
) {
    var text by remember { mutableStateOf("") }

    if (showDialog) {
        AlertDialog(
            onDismissRequest = { onDismiss() },
            title = { Text("Ajouter un commentaire") },
            text = {
                TextField(
                    value = text,
                    onValueChange = { text = it },
                    placeholder = { Text("Écris ton commentaire ici...") }
                )
            },
            confirmButton = {
                TextButton(onClick = {
                    onSubmit(text)
                    text = ""
                    onDismiss()
                }) {
                    Text("Publier")
                }
            },
            dismissButton = {
                TextButton(onClick = onDismiss) {
                    Text("Annuler")
                }
            }
        )
    }
}


@Composable
fun PostCard(
    post: Post,
    user: UserProfile?,
    currentUserId: String,
    commentCount: Int,
    onLikeClick: () -> Unit,
    onFollowClick: () -> Unit,
    onCommentSubmit: (String) -> Unit
) {
    var showCommentDialog by remember { mutableStateOf(false) }
    var liked by remember { mutableStateOf(post.likes.contains(currentUserId)) }
    var likeCount by remember { mutableStateOf(post.likes.size) }

    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = Color(0xFFFFEAE0))
    ) {
        Column(modifier = Modifier.padding(16.dp)) {

            // 🔼 Top section with avatar, name/date, and follow button
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Image(
                        painter = rememberAsyncImagePainter(user?.avatarUrl ?: "https://placehold.co/40x40"),
                        contentDescription = null,
                        modifier = Modifier
                            .size(40.dp)
                            .clip(CircleShape)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Column {
                        Text("${user?.firstName ?: "User"} ${user?.lastName ?: ""}", fontWeight = FontWeight.Bold)
                        Text("20 April at 11:45", fontSize = 12.sp, color = Color.Gray)
                    }
                }

                if (currentUserId != post.userId && user?.friends?.contains(currentUserId) != true) {
                    Button(
                        onClick = onFollowClick,
                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFFF9800)), // Orange
                        contentPadding = PaddingValues(horizontal = 12.dp, vertical = 4.dp),
                        modifier = Modifier.height(36.dp)
                    ) {
                        Text("Follow", color = Color.White, fontSize = 14.sp)
                    }
                }
            }

            Spacer(modifier = Modifier.height(8.dp))
            Text(post.content)

            if (post.imageUrl.isNotBlank()) {
                Spacer(modifier = Modifier.height(8.dp))
                Image(
                    painter = rememberAsyncImagePainter(post.imageUrl),
                    contentDescription = null,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(200.dp)
                        .clip(RoundedCornerShape(12.dp)),
                    contentScale = ContentScale.Crop
                )
            }

            Spacer(modifier = Modifier.height(12.dp))

            Row(verticalAlignment = Alignment.CenterVertically) {
                IconButton(onClick = {
                    if (!liked) {
                        liked = true
                        likeCount++
                        onLikeClick()
                    }
                }) {
                    Icon(
                        imageVector = Icons.Default.Favorite,
                        contentDescription = "Like",
                        tint = if (liked) Color.Red else Color.Gray
                    )
                }
                Text("$likeCount likes")

                Spacer(modifier = Modifier.width(16.dp))

                IconButton(onClick = { showCommentDialog = true }) {
                    Icon(
                        imageVector = Icons.Default.ChatBubbleOutline,
                        contentDescription = "Comments",
                        tint = Color.Gray
                    )
                }
                Text("$commentCount comments")
            }

            // 💬 Comment dialog
            CommentDialog(
                showDialog = showCommentDialog,
                onDismiss = { showCommentDialog = false },
                onSubmit = { commentText ->
                    onCommentSubmit(commentText)
                }
            )
        }
    }
}
