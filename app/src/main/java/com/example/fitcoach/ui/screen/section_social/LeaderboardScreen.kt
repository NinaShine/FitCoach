package com.example.fitcoach.ui.screen.section_social

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import coil.compose.rememberAsyncImagePainter
import com.example.fitcoach.R
import com.example.fitcoach.viewmodel.LeaderboardViewModel

@Composable
fun LeaderboardScreen(currentUserId: String, navController: NavController) {
    val vm: LeaderboardViewModel = viewModel()
    val users by vm.users.collectAsState()

    LaunchedEffect(Unit) {
        vm.loadLeaderboard()
    }

    Column(modifier = Modifier.fillMaxSize().padding(16.dp)) {
        // Header
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            IconButton(onClick = { navController.popBackStack() }) {
                Icon(Icons.Default.ArrowBack, contentDescription = "Back")
            }

            Text("LeaderBoard", fontSize = 20.sp, fontWeight = FontWeight.Bold)

            Image(
                painter = painterResource(R.drawable.july_photo_profile),
                contentDescription = null,
                modifier = Modifier
                    .size(40.dp)
                    .clip(CircleShape)
            )
        }

        Spacer(modifier = Modifier.height(12.dp))

        users.forEachIndexed { index, user ->
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 4.dp),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = Color(0xFFFFEAE0))
            ) {
                Row(
                    modifier = Modifier
                        .padding(16.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Image(
                            painter = rememberAsyncImagePainter(user.avatarUrl),
                            contentDescription = null,
                            modifier = Modifier
                                .size(40.dp)
                                .clip(CircleShape)
                        )

                        Spacer(modifier = Modifier.width(12.dp))

                        Text(user.fullName, fontWeight = FontWeight.Medium)
                    }

                    Row(verticalAlignment = Alignment.CenterVertically) {
                        if (index == 0) Text("🥇", fontSize = 18.sp)
                        else if (index == 1) Text("🥈", fontSize = 18.sp)
                        else if (index == 2) Text("🥉", fontSize = 18.sp)
                        Spacer(modifier = Modifier.width(6.dp))
                        Text("${user.points} Pts", fontWeight = FontWeight.Bold)
                    }
                }
            }
        }
    }
}
