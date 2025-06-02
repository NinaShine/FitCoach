package com.example.fitcoach.ui.screen.section_social

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import coil.compose.rememberAsyncImagePainter
import com.example.fitcoach.data.model.Challenge
import com.example.fitcoach.viewmodel.ChallengeViewModel
import com.example.fitcoach.viewmodel.UserProfileViewModel

@Composable
fun ChallengeScreen(currentUid: String, navController: NavController) {
    val vm: ChallengeViewModel = viewModel()
    val challenges by vm.challenges.collectAsState()
    val viewModel: UserProfileViewModel = viewModel()
    val avatarUrl by viewModel.avatarUrl


    LaunchedEffect(Unit) {
        vm.loadChallenges()
        viewModel.fetchAvatar()

    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            IconButton(onClick = { navController.popBackStack() }) {
                Icon(Icons.Default.ArrowBack, contentDescription = "Retour")
            }

            Text(
                text = "Challenges",
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold
            )

            Row(verticalAlignment = Alignment.CenterVertically) {
                IconButton(onClick = {
                    navController.navigate("leaderboard")
                }) {
                    Text("🏆", fontSize = 22.sp)
                }
                Spacer(modifier = Modifier.width(8.dp))
                Image(
                    painter = rememberAsyncImagePainter(avatarUrl),
                    contentDescription = null,
                    modifier = Modifier
                        .size(36.dp)
                        .clip(CircleShape)
                )
            }
        }

        Spacer(modifier = Modifier.height(12.dp))

        LazyColumn(verticalArrangement = Arrangement.spacedBy(12.dp)) {
            items(challenges) { challenge ->
                Card(
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = Color(0xFFFFEAE0)),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(Modifier.padding(16.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Image(
                                    painter = rememberAsyncImagePainter("https://placehold.co/40x40"),
                                    contentDescription = null,
                                    modifier = Modifier
                                        .size(36.dp)
                                        .clip(CircleShape)
                                )
                                Spacer(modifier = Modifier.width(8.dp))
                                Column {
                                    Text("Laura Meier", fontWeight = FontWeight.Bold)
                                    Text("3h", fontSize = 12.sp, color = Color.Gray)
                                }
                            }
                            Text("${challenge.rewardPoints} Pts", fontWeight = FontWeight.Bold)
                        }

                        Spacer(Modifier.height(12.dp))
                        Text("Join my challenge!", fontWeight = FontWeight.Bold)

                        Card(
                            colors = CardDefaults.cardColors(containerColor = Color(0xFFFF7043)),
                            shape = RoundedCornerShape(12.dp),
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(top = 8.dp)
                        ) {
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(12.dp),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Column {
                                    Text(
                                        text = challenge.title,
                                        fontWeight = FontWeight.Bold,
                                        color = Color.White
                                    )
                                    Text(
                                        text = getTimeRemaining(challenge),
                                        color = Color.White.copy(alpha = 0.8f)
                                    )
                                }

                                Button(
                                    onClick = {
                                        vm.joinChallenge(challenge.id, currentUid)
                                    },
                                    enabled = !challenge.participants.contains(currentUid),
                                    colors = ButtonDefaults.buttonColors(containerColor = Color.White)
                                ) {
                                    Text("Join", color = Color(0xFFFF7043))
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}


fun getTimeRemaining(challenge: Challenge): String {
    val now = System.currentTimeMillis()
    val endTime = challenge.timestamp + challenge.durationDays * 24 * 60 * 60 * 1000
    val daysLeft = ((endTime - now) / (1000 * 60 * 60 * 24)).coerceAtLeast(0)
    return "Ends in $daysLeft days"
}
