package com.example.fitcoach.ui.screen.section_tracking

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.FavoriteBorder
import androidx.compose.material.icons.filled.History
import androidx.compose.material.icons.filled.Timer
import androidx.compose.material.icons.filled.Whatshot
import androidx.compose.material3.Divider
import androidx.compose.material3.Icon
import androidx.compose.material3.Scaffold
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
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.example.fitcoach.R
import com.example.fitcoach.ui.screen.section_music.MusicScreen
import com.example.fitcoach.ui.screen.getSpotifyAccessToken
import com.example.fitcoach.ui.screen.section_accueil.AccueilScreen
import com.example.fitcoach.ui.screen.section_accueil.FitBottomBar
import com.example.fitcoach.ui.screen.section_social.FeedScreen
import com.example.fitcoach.ui.screen.section_workout.WorkoutScreen
import com.example.fitcoach.viewmodel.CurrentlyPlayingViewModel
import com.example.fitcoach.viewmodel.track_section.LiveTrackingViewModel
import com.example.fitcoach.viewmodel.track_section.RecentWorkoutsViewModel
import com.google.firebase.Timestamp
import com.google.firebase.auth.FirebaseAuth
import java.text.SimpleDateFormat
import java.util.Locale

@Composable
fun WorkoutSummaryScreen(navController: NavController, steps: Int, calories: Double, distanceKm: Double) {
    val viewModel: RecentWorkoutsViewModel = viewModel()
    val sessions = viewModel.recentSessions

    LaunchedEffect(Unit) {
        viewModel.fetchRecentSessions()
    }


    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
            .background(Color.White),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        item {
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
                    )
                    Spacer(modifier = Modifier.width(8.dp))
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
            }

            Divider(
                color = Color.Black,
                thickness = 1.dp,
                modifier = Modifier.width(80.dp)
            )

            Spacer(modifier = Modifier.height(8.dp))
            Text("Track Workout", fontSize = 24.sp, fontWeight = FontWeight.SemiBold)
            Spacer(modifier = Modifier.height(16.dp))
        }

        item {

            // 🟠 STATIC WIDGETS
            Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                Box(
                    modifier = Modifier
                        .weight(1f)
                        .background(Color(0xFFE86144), RoundedCornerShape(12.dp))
                        .padding(12.dp)
                ) {
                    Column {
                        Text("History", fontWeight = FontWeight.Bold, color = Color.White)
                        Icon(Icons.Default.History, contentDescription = null, tint = Color.White)
                    }
                }

                Box(
                    modifier = Modifier
                        .weight(1f)
                        .background(Color(0xFFFFE2D3), RoundedCornerShape(12.dp))
                        .padding(12.dp)
                ) {
                    Column {
                        Text("Duration", fontWeight = FontWeight.Bold, color = Color(0xFFE86144))
                        Text("30 mins")
                        Icon(
                            Icons.Default.Timer,
                            contentDescription = null,
                            tint = Color(0xFFE86144)
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                Box(
                    modifier = Modifier
                        .weight(1f)
                        .background(Color(0xFFFFE7DE), RoundedCornerShape(12.dp))
                        .padding(12.dp)
                ) {
                    Column {
                        Text("Live Tracking", fontWeight = FontWeight.Bold)
                        Icon(
                            Icons.Default.FavoriteBorder,
                            contentDescription = null,
                            tint = Color(0xFFE86144)
                        )
                    }
                }

                Box(
                    modifier = Modifier
                        .weight(1f)
                        .background(Color(0xFFFFC9C1), RoundedCornerShape(12.dp))
                        .padding(12.dp)
                ) {
                    Column {
                        Text("Calories", fontWeight = FontWeight.Bold)
                        Text("426 Kcal")
                        Icon(
                            Icons.Default.Whatshot,
                            contentDescription = null,
                            tint = Color(0xFFE86144)
                        )
                    }
                }
            }
        }

        item {
            Spacer(modifier = Modifier.height(16.dp))
            Text("Recent Workouts", fontSize = 18.sp, fontWeight = FontWeight.Medium)

            Spacer(modifier = Modifier.height(12.dp))
        }

        // 🟠 DYNAMIC LIST
        itemsIndexed(sessions) {_,  session ->
            WorkoutCard(session)
            Spacer(modifier = Modifier.height(8.dp))
        }

    }
}


@Composable
fun WorkoutCard(session: Map<String, Any>) {
    val type = session["activityType"] as? String ?: "Unknown"
    val duration = session["durationFormatted"] as? String ?: "--:--"
    val calories = session["calories"] as? String ?: "--"
    val timestamp = session["timestamp"] as? Timestamp

    val formattedDate = timestamp?.toDate()?.let {
        val formatter = SimpleDateFormat("MMMM dd, yyyy - hh:mm a", Locale.ENGLISH)
        formatter.format(it)
    } ?: "Date inconnue"

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .background(Color(0xFFFBF2ED), shape = RoundedCornerShape(12.dp))
            .padding(12.dp)
    ) {
        Column {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(type, fontSize = 17.sp, fontWeight = FontWeight.SemiBold, color = Color(0xFFEF8E79))
                Row {
                    Icon(Icons.Default.Timer, contentDescription = null, tint = Color(0xFFEF8E79))
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(duration, fontSize = 14.sp)
                    Spacer(modifier = Modifier.width(12.dp))
                    Icon(Icons.Default.Whatshot, contentDescription = null, tint = Color(0xFFE86144))
                    Spacer(modifier = Modifier.width(4.dp))
                    Text("$calories Kcal", fontSize = 14.sp)
                }
            }
            Spacer(modifier = Modifier.height(4.dp))
            Text(formattedDate, fontSize = 13.sp, color = Color.White,
                modifier = Modifier
                    .background(Color(0xFFEF8E79), shape = RoundedCornerShape(6.dp))
                    .padding(4.dp)
            )
        }
    }
}

@Composable
fun WorkoutSummaryScreenWithNavBar(
    navController: NavController,
    liveTrackingVm: LiveTrackingViewModel
) {
    var currentRoute by remember { mutableStateOf("workout_summary") }

    val steps = liveTrackingVm.steps
    val calories = liveTrackingVm.calories
    val distance = liveTrackingVm.distanceKm

    Scaffold(
        bottomBar = {
            FitBottomBar(
                onItemSelected = { selectedRoute ->
                    currentRoute = selectedRoute
                },
                currentRoute = currentRoute,
                onCentralClick = {
                    currentRoute = "createPost"
                }
            )
        }
    ) { padding ->
        Box(modifier = Modifier.padding(12.dp)) {
            if (currentRoute == "workout_summary") {
                WorkoutSummaryScreen(
                    navController = navController,
                    steps = steps,
                    calories = calories,
                    distanceKm = distance
                )
            } else if (currentRoute == "home") {
                AccueilScreen(
                    navController = navController,
                    steps = steps,
                    calories = calories,
                    distanceKm = distance
                )
            } else if (currentRoute == "music") {
                MusicScreen(
                    navController = navController,
                    accessToken = getSpotifyAccessToken(LocalContext.current).orEmpty(),
                    currentlyPlayingVm = CurrentlyPlayingViewModel(),
                    steps = steps,
                    calories = calories,
                    distanceKm = distance

                )
            } else if (currentRoute == "social") {
                val currentUserId = FirebaseAuth.getInstance().currentUser?.uid
                if (currentUserId != null) {
                    FeedScreen(
                        currentUid = currentUserId,
                        navController = navController
                    )
                } else {
                    Text("Connexion requise")
                }
            } else if (currentRoute == "workout") {
                WorkoutScreen(navController = navController)
            }
        }
    }
}
/*
@Preview(showBackground = true)
@Composable
fun WorkoutSummaryPreviewWithNav() {
    WorkoutSummaryScreenWithNavBar(navController = NavController(LocalContext.current), liveTrackingVm = )
}

 */

