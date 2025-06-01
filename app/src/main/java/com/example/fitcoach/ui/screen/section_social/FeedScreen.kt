package com.example.fitcoach.ui.screen.section_social

import android.os.Build
import androidx.annotation.RequiresApi
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
import com.example.fitcoach.viewmodel.UserProfileViewModel

@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun FeedScreen(currentUid: String, navController: NavController) {
    val vm: FeedViewModel = viewModel()
    val posts by vm.posts.collectAsState()
    val users by vm.userProfiles.collectAsState()
    val commentCounts by vm.commentCounts.collectAsState()
    val currentUserFriends by vm.currentUserFriends.collectAsState() // ✅ Nouvelle StateFlow

    LaunchedEffect(Unit) {
        vm.loadFeed(currentUid)
        vm.loadCurrentUserFriends(currentUid) // ✅ Charger les amis de l'utilisateur courant
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.White)
            .padding(8.dp)
    ) {
        TopBar(
            user = users[currentUid],
            navController = navController,
            onProfileClick = { navController.navigate("profile") },
            onChallengeClick = { navController.navigate("challenges") }
        )
        HorizontalDivider(
            color = Color.Black,
            thickness = 1.dp,
            modifier = Modifier
                .padding(start = 8.dp)
                .width(80.dp)
        )

        Spacer(modifier = Modifier.height(8.dp))

        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(12.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            items(posts) { post ->
                val author = users[post.userId]
                val commentCount = commentCounts[post.id] ?: 0
                val isFollowing = currentUserFriends.contains(post.userId) // ✅ Vérification correcte

                PostCard(
                    post = post,
                    user = author,
                    currentUserId = currentUid,
                    commentCount = commentCount,
                    isFollowing = isFollowing, // ✅ Passer le statut de suivi
                    onLikeClick = { vm.likePost(post.id) },
                    onFollowClick = {
                        vm.followUser(post.userId, currentUid) // ✅ Appel correct
                    },
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
    val userViewModel: UserProfileViewModel = viewModel()

    val avatarUrl by userViewModel.avatarUrl

    LaunchedEffect(Unit) {
        userViewModel.fetchAvatar()
    }
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .padding(6.dp)
    ) {
        // 🏷️ Titre centré
        Text(
            text = "Feed",
            fontWeight = FontWeight.Bold,
            fontSize = 20.sp,
            modifier = Modifier.align(Alignment.Center)
        )


        // Header
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text("fit’fy", fontWeight = FontWeight.Bold, fontSize = 22.sp, modifier = Modifier.align(Alignment.CenterVertically))
            Row(verticalAlignment = Alignment.CenterVertically) {
                Image(
                    painter = painterResource(id = R.drawable.robot_assistant),
                    contentDescription = null,
                    modifier = Modifier
                        .size(50.dp)
                        .clip(CircleShape)
                        .clickable {
                            navController.navigate("chatbot")
                        }
                )
                Spacer(modifier = Modifier.width(8.dp))
                Image(
                    painter = rememberAsyncImagePainter(avatarUrl),
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
        }

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
    onCommentSubmit: (String) -> Unit,
    isFollowing: Boolean // ✅ Nouveau paramètre pour savoir si on suit déjà
) {
    var showCommentDialog by remember { mutableStateOf(false) }
    var liked by remember { mutableStateOf(post.likes.contains(currentUserId)) }
    var likeCount by remember { mutableStateOf(post.likes.size) }
    var isCurrentlyFollowing by remember { mutableStateOf(isFollowing) }

    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = Color(0xFFFFEAE0))
    ) {
        Column(modifier = Modifier.padding(16.dp)) {

            // 🔼 En-tête avec avatar, nom, bouton follow/amis
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

                // ✅ Logique corrigée pour le bouton Follow
                if (currentUserId != post.userId) {
                    if (isCurrentlyFollowing) {
                        // Si on suit déjà, on affiche "Amis" en gris et désactivé
                        Button(
                            onClick = { /* déjà amis */ },
                            colors = ButtonDefaults.buttonColors(containerColor = Color.Gray),
                            contentPadding = PaddingValues(horizontal = 12.dp, vertical = 4.dp),
                            modifier = Modifier.height(36.dp),
                            enabled = false
                        ) {
                            Text("Amis", color = Color.White, fontSize = 14.sp)
                        }
                    } else {
                        // Si on ne suit pas encore, bouton Follow actif
                        Button(
                            onClick = {
                                isCurrentlyFollowing = true // ✅ Mise à jour immédiate de l'UI
                                onFollowClick() // ✅ Appel correct du callback
                            },
                            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFFF9800)),
                            contentPadding = PaddingValues(horizontal = 12.dp, vertical = 4.dp),
                            modifier = Modifier.height(36.dp)
                        ) {
                            Text("Follow", color = Color.White, fontSize = 14.sp)
                        }
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

            CommentDialog(
                showDialog = showCommentDialog,
                onDismiss = { showCommentDialog = false },
                onSubmit = { commentText -> onCommentSubmit(commentText) }
            )
        }
    }
}
