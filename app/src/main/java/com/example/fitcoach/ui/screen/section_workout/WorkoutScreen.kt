package com.example.fitcoach.ui.screen.section_workout

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Description
import androidx.compose.material.icons.filled.NavigateNext
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import coil.compose.rememberAsyncImagePainter
import com.example.fitcoach.R
import com.example.fitcoach.viewmodel.WorkoutViewModel
import com.example.fitcoach.viewmodel.UserProfileViewModel

@Composable
fun WorkoutScreen(navController: NavController) {
    val viewModel: WorkoutViewModel = viewModel()
    val workouts by viewModel.savedWorkouts.collectAsState()
    val scrollState = rememberScrollState()

    val userViewModel: UserProfileViewModel = viewModel()
    val avatarUrl by userViewModel.avatarUrl

    LaunchedEffect(Unit) {
        userViewModel.fetchAvatar()
    }

    Box(modifier = Modifier.fillMaxSize()) {
        // Scrollable content
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(start = 16.dp, end = 16.dp, top = 16.dp, bottom = 80.dp) // bottom padding to avoid overlapping the fixed button
                .verticalScroll(scrollState)
        ) {
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

            Divider(
                color = Color.Black,
                thickness = 1.dp,
                modifier = Modifier.width(80.dp)
            )

            Spacer(modifier = Modifier.height(24.dp))
            Text("Quick Start", fontSize = 20.sp)

            Spacer(modifier = Modifier.height(12.dp))

            Button(
                onClick = {
                    navController.navigate("quick_workout")
                },
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFFBF2ED)),
                modifier = Modifier
                    .fillMaxWidth()
                    .height(60.dp),
                shape = RoundedCornerShape(12.dp)
            ) {
                Icon(Icons.Default.Add, contentDescription = null, tint = Color.Black)
                Spacer(modifier = Modifier.width(8.dp))
                Text("Start an Empty Workout", color = Color.Black)
            }

            Spacer(modifier = Modifier.height(24.dp))
            Text("Routines", fontSize = 20.sp)

            Spacer(modifier = Modifier.height(12.dp))
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                RoutineButton(
                    icon = Icons.Default.Description,
                    text = "New Routine",
                    modifier = Modifier.weight(1f),
                    onClick = { navController.navigate("create_routine") }
                )
                RoutineButton(
                    icon = Icons.Default.Search,
                    text = "Explore Routines",
                    modifier = Modifier.weight(1f),
                    onClick = { navController.navigate("explore_routines") }
                )
            }

            Spacer(modifier = Modifier.height(24.dp))

            Text("Mes entraînements enregistrés", fontSize = 18.sp)
            Spacer(modifier = Modifier.height(8.dp))
            workouts.forEach { workout ->
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 6.dp)
                        .clip(RoundedCornerShape(12.dp))
                        .background(Color(0xFFFBF2ED))
                        .clickable {
                            // Ajoute ici la navigation vers le détail du workout si tu as un écran
                        }
                        .padding(16.dp)
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text(workout.name, fontSize = 16.sp)
                        Spacer(modifier = Modifier.weight(1f))
                        Text("${workout.exercises.size} exercices", fontSize = 14.sp)
                    }
                }
            }
        }

        // Fixed bottom help button
        Box(
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .fillMaxWidth()
                .height(60.dp)
                .padding(horizontal = 16.dp, vertical = 16.dp)
                .clip(RoundedCornerShape(12.dp))
                .background(Color(0xFFFBF2ED))
                .clickable {
                    navController.navigate("workout_help")
                },
            contentAlignment = Alignment.CenterStart
        ) {
            Row(modifier = Modifier.padding(horizontal = 16.dp)) {
                Text("How to get started", fontSize = 16.sp)
                Spacer(modifier = Modifier.weight(1f))
                Icon(Icons.Default.NavigateNext, contentDescription = null)
            }
        }
    }
}

@Composable
fun RoutineButton(
    icon: ImageVector,
    text: String,
    modifier: Modifier = Modifier,
    onClick: () -> Unit
) {
    Box(
        modifier = modifier
            .height(100.dp)
            .clip(RoundedCornerShape(12.dp))
            .background(Color(0xFFEF8E79))
            .clickable { onClick() },
        contentAlignment = Alignment.Center
    ) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Icon(icon, contentDescription = null, tint = Color.White)
            Spacer(modifier = Modifier.height(8.dp))
            Text(text, color = Color.White)
        }
    }
}
