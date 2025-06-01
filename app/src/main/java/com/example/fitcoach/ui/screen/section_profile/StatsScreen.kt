package com.example.fitcoach.ui.screen.section_profile

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.DirectionsWalk
import androidx.compose.material.icons.filled.DirectionsWalk
import androidx.compose.material.icons.filled.GolfCourse
import androidx.compose.material.icons.filled.Place
import androidx.compose.material.icons.filled.Timer
import androidx.compose.material.icons.filled.Whatshot
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import coil.compose.rememberAsyncImagePainter
import com.example.fitcoach.R
import com.example.fitcoach.viewmodel.UserProfileViewModel
import com.example.fitcoach.viewmodel.track_section.LastSessionViewModel
import com.example.fitcoach.viewmodel.track_section.LiveTrackingViewModel

@Composable
fun StatsScreen(navController: NavController,liveTrackingVm: LiveTrackingViewModel) {
    val viewModel: UserProfileViewModel = viewModel()
    val lastSessionViewModel: LastSessionViewModel = viewModel()
    val firstname by viewModel.fN
    val lastname by viewModel.lN
    val avatarUrl by viewModel.avatarUrl
    val goal by viewModel.stepGoal
    val workoutGoal by viewModel.workoutGoal


    val calories = liveTrackingVm.calories
    val steps = liveTrackingVm.steps
    val distance = liveTrackingVm.distanceKm

    val recentSessions = lastSessionViewModel.recentSessions


    LaunchedEffect(Unit) {
        viewModel.fetchFirstName()
        viewModel.fetchLastName()
        viewModel.fetchAvatar()
        viewModel.fetchStepGoal()
        viewModel.fetchWorkoutGoal()
        liveTrackingVm.startListening()
        lastSessionViewModel.fetchRecentSessions()
    }

    val totalDurationMin = recentSessions.sumOf {
        val durationStr = it["durationFormatted"] as? String ?: "00:00:00"
        val parts = durationStr.split(":").mapNotNull { s -> s.toIntOrNull() }
        val (h, m, s) = when (parts.size) {
            3 -> Triple(parts[0], parts[1], parts[2])
            2 -> Triple(0, parts[0], parts[1])
            else -> Triple(0, 0, 0)
        }
        h*60+m+s/60.0
    }

    var selectedTab by remember { mutableStateOf("daily") }



    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFFFEF3ED))
            .padding(16.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 8.dp, top = 10.dp, start = 10.dp, end = 10.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text("$firstname $lastname" ?: "Chargement...", style = MaterialTheme.typography.headlineSmall)

            Spacer(modifier = Modifier.width(16.dp))

            Image(
                painter = rememberAsyncImagePainter(avatarUrl),
                contentDescription = "Profile picture",
                contentScale = ContentScale.Crop,
                modifier = Modifier
                    .size(70.dp)
                    .clip(CircleShape)
            )
        }

        Spacer(Modifier.height(16.dp))

        Row(horizontalArrangement = Arrangement.spacedBy(16.dp)) {
            Button(
                onClick = { selectedTab = "daily" },
                colors = ButtonDefaults.buttonColors(
                    containerColor = if (selectedTab == "daily") Color(0xFFE86144) else Color.White
                )
            ) {
                Text("Daily", color = if (selectedTab == "daily") Color.White else Color.Black)
            }
            Button(
                onClick = { selectedTab = "weekly" },
                colors = ButtonDefaults.buttonColors(
                    containerColor = if (selectedTab == "weekly") Color(0xFFE86144) else Color.White
                )
            ) {
                Text("Weekly", color = if (selectedTab == "weekly") Color.White else Color.Black)
            }
            Button(
                onClick = { selectedTab = "monthly" },
                colors = ButtonDefaults.buttonColors(
                    containerColor = if (selectedTab == "monthly") Color(0xFFF5B01A) else Color.White
                )
            ) {
                Text("Monthly", color = if (selectedTab == "monthly") Color.White else Color.Black)
            }
        }

        Spacer(Modifier.height(24.dp))

        when (selectedTab) {
            "daily" -> {
                Spacer(Modifier.height(24.dp))
                Text("STATISTICS", color = Color(0xFFE86144))
                Spacer(Modifier.height(12.dp))
                StatsGrid(calories, steps, distance, totalDurationMin)

                Spacer(Modifier.height(24.dp))
                Text("REWARDS", color = Color(0xFFE86144))
                Spacer(Modifier.height(12.dp))
                RewardCard("🔥", "Reach a 7 day streak", 0.4f)
                Spacer(modifier = Modifier.height(12.dp))
                RewardCard("🏋️", "Complete 3 workouts", 0.7f)
                Spacer(modifier = Modifier.height(12.dp))
                RewardCard("🏃", "Reach 10 000 steps in one day", 0.6f)

                Button(
                    onClick = {
                        navController.popBackStack()
                    },
                    modifier = Modifier.fillMaxWidth(),
                    colors = ButtonDefaults.buttonColors(containerColor = Color.White)
                ) {
                    Text("Return", color = Color.Black)
                }
            }

            "weekly" -> {
                WeeklyStatsSection(goal ?: 0, workoutGoal ?: 0, navController)
            }

            "monthly" -> {
                MonthlyStatsSection(navController)
            }
        }

    }
}

@Composable
fun StatsGrid(calories: Double, steps: Int, distance: Double, totalDurationMin: Double) {
    Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
        Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
            StatCard(
                modifier = Modifier.weight(1f),
                icon = Icons.Default.Whatshot,
                value = "${calories.toInt()}",
                label = "Calories"
            )
            StatCard(
                modifier = Modifier.weight(1f),
                icon = Icons.Default.Timer,
                value = "${totalDurationMin.toInt()}",
                label = "Duration"
            )
        }

        Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
            StatCard(
                modifier = Modifier.weight(1f),
                icon = Icons.Default.DirectionsWalk,
                value = "$steps",
                label = "Steps"
            )
            StatCard(
                modifier = Modifier.weight(1f),
                icon = Icons.Default.Place,
                value = "%.1f km".format(distance),
                label = "Distance"
            )
        }
    }
}

@Composable
fun StatsGridMonthly() {
    Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
        Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
            StatCardMonthly(
                modifier = Modifier
                    .weight(1f),
                icon = Icons.Default.Whatshot,
                value = "38 Day Streak",
                label = ""
            )
            StatCardMonthly(
                modifier = Modifier
                    .weight(1f),
                icon = Icons.Default.GolfCourse,
                value = "16 rewards received",
                label = ""
            )
        }
    }
}


@Composable
fun StatCard(modifier: Modifier = Modifier, icon: ImageVector, value: String, label: String) {
    Card(
        modifier = modifier
            .height(100.dp),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White)
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center,
            modifier = Modifier.fillMaxSize()
        ) {
            Icon(icon, contentDescription = null, tint = Color(0xFFE86144))
            Text(value, fontWeight = FontWeight.Bold)
            Text(label)
        }
    }
}

@Composable
fun StatCardMonthly(modifier: Modifier = Modifier, icon: ImageVector, value: String, label: String) {
    Card(
        modifier = modifier
            .height(160.dp),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White)
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center,
            modifier = Modifier.fillMaxSize()
        ) {
            Icon(icon, contentDescription = null, tint = Color(0xFFF5B01A))
            Text(value, fontWeight = FontWeight.Bold)
            Text(label)
        }
    }
}


@Composable
fun RewardCard(emoji: String, label: String, progress: Float) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .height(80.dp),
        colors = CardDefaults.cardColors(containerColor = Color(0xFFFFD7C2)),
        shape = RoundedCornerShape(16.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier.size(32.dp),
                contentAlignment = Alignment.Center
            ) {
                Text(emoji, fontSize = 24.sp)
            }

            Spacer(Modifier.width(12.dp))

            Column(
                verticalArrangement = Arrangement.Center,
                modifier = Modifier.weight(1f)
            ) {
                Text(label)
                Spacer(modifier = Modifier.height(6.dp))
                LinearProgressIndicator(
                    progress = { progress },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(6.dp)
                        .clip(RoundedCornerShape(4.dp)),
                    color = Color(0xFFE86144),
                    trackColor = Color.White
                )
            }
        }
    }
}


@Composable
fun WeeklyStatsSection(stepGoal: Int, workoutGoal: Int, navController: NavController) {
    Column(verticalArrangement = Arrangement.spacedBy(16.dp)) {

        Image(
            painter = painterResource(id = R.drawable.weekly),
            contentDescription = "Weight Chart",
            modifier = Modifier
                .fillMaxWidth()
                .height(200.dp)
                .clip(RoundedCornerShape(16.dp)),
            contentScale = ContentScale.FillWidth
        )

        Spacer(Modifier.height(16.dp))

        Text("WORKOUTS", fontWeight = FontWeight.SemiBold)
        Text("Goal: $workoutGoal workouts/day", fontSize = 12.sp, color = Color.Gray)
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(100.dp)
                .background(Color(0xFFFFE8DD), RoundedCornerShape(16.dp))
                .padding(16.dp)
        ) {
            Row(
                horizontalArrangement = Arrangement.SpaceBetween,
                modifier = Modifier.fillMaxWidth()
            ) {
                repeat(7) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Box(
                            modifier = Modifier
                                .width(12.dp)
                                .height((20..60).random().dp)
                                .background(Color(0xFFE86144), RoundedCornerShape(4.dp))
                        )
                        Text("MTWTFSS"[it].toString())
                    }
                }
            }
        }

        Text("Steps", fontWeight = FontWeight.SemiBold)
        Text("Goal: $stepGoal steps/day", fontSize = 12.sp, color = Color.Gray)
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(130.dp)
                .background(Color(0xFFFFE8DD), RoundedCornerShape(16.dp))
                .padding(16.dp)
        ) {
            Row(
                horizontalArrangement = Arrangement.SpaceBetween,
                modifier = Modifier.fillMaxWidth()
            ) {
                repeat(7) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Box(
                            modifier = Modifier
                                .width(12.dp)
                                .height((30..90).random().dp)
                                .background(Color(0xFFFFB4A2), RoundedCornerShape(4.dp))
                        )
                        Text("MTWTFSS"[it].toString())
                    }
                }
            }
        }

    }

    Button(
        onClick = {
            navController.popBackStack()
        },
        modifier = Modifier.fillMaxWidth(),
        colors = ButtonDefaults.buttonColors(containerColor = Color.White)
    ) {
        Text("Return", color = Color.Black)
    }
}


@Composable
fun MonthlyStatsSection(navController: NavController) {
    Column {
        Text("Monthly Progress", style = MaterialTheme.typography.titleMedium, color = Color(0xFFF5B01A))
        Spacer(Modifier.height(12.dp))

        Image(
            painter = painterResource(id = R.drawable.monthly),
            contentDescription = "Weight Chart",
            modifier = Modifier
                .fillMaxWidth()
                .height(230.dp)
                .clip(RoundedCornerShape(16.dp)),
            contentScale = ContentScale.FillWidth
        )

        Spacer(Modifier.height(16.dp))

        StatsGridMonthly()

        Spacer(Modifier.height(16.dp))


        Button(
            onClick = {
                navController.popBackStack()
            },
            modifier = Modifier.fillMaxWidth(),
            colors = ButtonDefaults.buttonColors(containerColor = Color.White)
        ) {
            Text("Return", color = Color.Black)
        }

    }
}





