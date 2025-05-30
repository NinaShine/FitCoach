package com.example.fitcoach.ui.screen.section_social

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.example.fitcoach.data.model.Challenge
import com.example.fitcoach.viewmodel.ChallengeViewModel

@Composable
fun ChallengeScreen(currentUid: String, navController: NavController) {
    val vm: ChallengeViewModel = viewModel()
    val challenges by vm.challenges.collectAsState()

    LaunchedEffect(Unit) {
        vm.loadChallenges()
    }

    Column(Modifier.fillMaxSize().padding(16.dp)) {
        Text("Challenges", style = MaterialTheme.typography.titleLarge)

        LazyColumn(verticalArrangement = Arrangement.spacedBy(12.dp)) {
            items(challenges) { challenge ->
                Card(
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = Color(0xFFFFEAE0)),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(Modifier.padding(16.dp)) {
                        Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                            Text("${challenge.rewardPoints} Pts", fontWeight = FontWeight.Bold)
                            Text("${getTimeRemaining(challenge)}")
                        }

                        Spacer(Modifier.height(8.dp))
                        Text("Join my challenge!", fontWeight = FontWeight.Bold)
                        Text(challenge.title)

                        Spacer(Modifier.height(12.dp))
                        Button(
                            onClick = {
                                vm.joinChallenge(challenge.id, currentUid)
                            },
                            enabled = !challenge.participants.contains(currentUid),
                            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFFF7043))
                        ) {
                            Text("Join")
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
