package com.example.fitcoach.ui.screen.section_profile

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.EmojiEvents
import androidx.compose.material.icons.filled.KeyboardArrowRight
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.ShowChart
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
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
import com.google.firebase.Timestamp
import java.time.LocalDate
import java.time.ZoneId


@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun ProfileScreen(navController: NavController, liveTrackingVm: LiveTrackingViewModel) {
    val viewModel: UserProfileViewModel = viewModel()
    val lastSessionViewModel: LastSessionViewModel = viewModel()
    val username by viewModel.username
    val age by viewModel.birthDate
    val avatarUrl by viewModel.avatarUrl

    val recentSessions = lastSessionViewModel.recentSessions

    val today = remember { LocalDate.now() }
    val todaySessions = recentSessions.filter {
        val timestamp = it["timestamp"] as? Timestamp
        timestamp?.toDate()?.toInstant()?.atZone(ZoneId.systemDefault())?.toLocalDate() == today
    }

    val totalDurationMin = todaySessions.sumOf {
        val durationStr = it["durationFormatted"] as? String ?: "00:00:00"
        val parts = durationStr.split(":").mapNotNull { s -> s.toIntOrNull() }
        val (h, m, s) = when (parts.size) {
            3 -> Triple(parts[0], parts[1], parts[2])
            2 -> Triple(0, parts[0], parts[1])
            else -> Triple(0, 0, 0)
        }
        h * 60 + m + s / 60.0
    }

    val workoutCount = todaySessions.size


    val calories = liveTrackingVm.calories


    LaunchedEffect(Unit) {
        viewModel.fetchUsername()
        viewModel.fetchAge()
        viewModel.fetchAvatar()
        lastSessionViewModel.fetchRecentSessions()
        liveTrackingVm.startListening()
    }

    val scrollState = rememberScrollState()

    val daysToDisplay = (-3..3).map { today.plusDays(it.toLong()) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(scrollState)
            .background(Color(0xFFFDF4EF))
            .padding(28.dp)
    ) {
        // Profile info + Close button
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 8.dp, top = 10.dp, start = 10.dp, end = 10.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Image(
                    painter = rememberAsyncImagePainter(avatarUrl),
                    contentDescription = "Profile picture",
                    contentScale = ContentScale.Crop,
                    modifier = Modifier
                        .size(90.dp)
                        .clip(CircleShape)
                )
                Spacer(modifier = Modifier.width(16.dp))
                Column {
                    Text(username ?: "Chargement...", fontSize = 20.sp, fontWeight = FontWeight.Bold)
                    Text("$age years old", fontSize = 14.sp, color = Color.Gray)
                }
            }

            Box(
                modifier = Modifier
                    .size(36.dp)
                    .background(
                        color = Color(0xFFE86144).copy(alpha = 0.1f),
                        shape = CircleShape
                    )
                    .clickable { navController.popBackStack() },
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    painter = painterResource(id = R.drawable.close),
                    contentDescription = null,
                    tint = Color(0xFFE86144),
                    modifier = Modifier.size(20.dp)
                )
            }
        }

        Spacer(modifier = Modifier.height(24.dp))
        Text("Your Progress", fontSize = 17.sp)

        Spacer(modifier = Modifier.height(8.dp))

        Row(
            modifier = Modifier.align(Alignment.CenterHorizontally),
            horizontalArrangement = Arrangement.spacedBy(25.dp)
        ) {
            daysToDisplay.forEach { day ->
                val isToday = day == today
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    modifier = if (isToday) Modifier
                        .size(40.dp)
                        .clip(CircleShape)
                        .background(Color(0xFFE86144))
                        .padding(4.dp) else Modifier
                ) {
                    Text(day.dayOfMonth.toString(), color = if (isToday) Color.White else Color.Black)
                    Text(day.dayOfWeek.name.first().toString(), fontSize = 12.sp, color = if (isToday) Color.White else Color.Gray)
                }
            }
        }

        Spacer(modifier = Modifier.height(12.dp))

        // Progress Stats
        Row(
            modifier = Modifier
                .width(320.dp)
                .clip(RoundedCornerShape(16.dp))
                .background(Color.White)
                .align(Alignment.CenterHorizontally)
                .padding(16.dp),
            horizontalArrangement = Arrangement.SpaceEvenly
        ) {
            StatBlock("$workoutCount", "Workouts")
            StatBlockCal(calories, "CAL")
            StatBlock("${totalDurationMin.toInt()}", "Minutes")
        }

        Spacer(modifier = Modifier.height(24.dp))

        ProfileOptionItem("Friends", Icons.Default.Person) { /* navigate to friends */ }
        ProfileOptionItem("Reward Collection", Icons.Default.EmojiEvents) { /* navigate to rewards */ }
        ProfileOptionItem("Statistics", Icons.Default.ShowChart) {
            navController.navigate("profile_stats")
        }
        ProfileOptionItem("Settings", Icons.Default.Settings) {
            navController.navigate("settings")
        }
    }
}

@Composable
fun StatBlock(value: String, label: String) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Text(value, fontWeight = FontWeight.Bold)
        Text(label, fontSize = 12.sp, color = Color.Gray)
    }
}

@Composable
fun StatBlockCal(value: Double, label: String) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Text("${value.toInt()}", fontWeight = FontWeight.Bold)
        Text(label, fontSize = 12.sp, color = Color.Gray)
    }
}

@Composable
fun ProfileOptionItem(title: String, icon: ImageVector, onClick: () -> Unit) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .height(80.dp)
            .padding(vertical = 6.dp)
            .clip(RoundedCornerShape(16.dp))
            .background(Color.White)
            .padding(horizontal = 16.dp, vertical = 12.dp)
            .clickable {
                onClick()
            },
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(icon, contentDescription = null, tint = Color(0xFFE86144), modifier = Modifier.size(32.dp))
        Spacer(modifier = Modifier.width(12.dp))
        Text(title, modifier = Modifier.weight(1f), fontSize = 18.sp)
        Icon(Icons.Default.KeyboardArrowRight, contentDescription = null, tint = Color.Gray)
    }
}
/*
@RequiresApi(Build.VERSION_CODES.O)
@Preview(showBackground = true)
@Composable
fun ProfileScreenPreview() {
    ProfileScreen(navController = NavController(LocalContext.current))
}

 */




