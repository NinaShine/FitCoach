package com.example.fitcoach.ui.screen.section_profile

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.snapshots.SnapshotStateList
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import coil.compose.AsyncImage
import com.example.fitcoach.data.model.UserProfile
import com.example.fitcoach.viewmodel.UserProfileViewModel

@Composable
fun FriendsScreen(navController: NavController) {
    val viewModel: UserProfileViewModel = viewModel()
    val friends = viewModel.friends

    LaunchedEffect(Unit) {
        viewModel.fetchFriends()
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFFFDF4EF))
            .padding(16.dp)
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            IconButton(onClick = { navController.popBackStack() }) {
                Icon(
                    imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                    contentDescription = "Back"
                )
            }
            Text(
                "Friends",
                fontSize = 22.sp,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.padding(start = 8.dp)
            )
        }

        Spacer(modifier = Modifier.height(12.dp))

        LazyVerticalGrid(
            columns = GridCells.Fixed(2),
            contentPadding = PaddingValues(8.dp),
            horizontalArrangement = Arrangement.spacedBy(12.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp),
            modifier = Modifier.fillMaxSize()
        ) {
            items(friends) { friend: UserProfile ->
                FriendCard(friend)
            }
        }
    }
}


@Composable
fun FriendCard(user: UserProfile) {
    Card(
        shape = RoundedCornerShape(16.dp),
        modifier = Modifier
            .fillMaxWidth()
            .aspectRatio(0.75f),
        elevation = CardDefaults.cardElevation(4.dp)
    ) {
        Box {
            AsyncImage(
                model = user.avatarUrl,
                contentDescription = user.username,
                contentScale = ContentScale.Crop,
                modifier = Modifier.fillMaxSize()
            )

            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(
                        Brush.verticalGradient(
                            colors = listOf(Color.Transparent, Color.Black.copy(alpha = 0.7f))
                        )
                    )
            )

            Column(
                modifier = Modifier
                    .align(Alignment.BottomStart)
                    .padding(12.dp)
            ) {
                Text(
                    text = user.firstName ?: "Unknown",
                    color = Color.White,
                    fontWeight = FontWeight.Bold,
                    fontSize = 16.sp
                )

                if (!user.bio.isNullOrBlank()) {
                    Text(
                        text = user.bio!!,
                        color = Color.White,
                        fontSize = 12.sp,
                        maxLines = 2
                    )
                }

                Spacer(modifier = Modifier.height(4.dp))

                Row(horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                    user.workouts.take(2).forEach {
                        AssistChip(
                            onClick = {},
                            label = { Text(it, fontSize = 10.sp) },
                            colors = AssistChipDefaults.assistChipColors(
                                containerColor = Color.White.copy(alpha = 0.15f),
                                labelColor = Color.White
                            )
                        )
                    }
                }
            }
        }
    }
}
