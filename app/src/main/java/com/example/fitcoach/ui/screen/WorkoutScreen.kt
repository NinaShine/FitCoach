package com.example.fitcoach.ui.screen

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Description
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.fitcoach.R

@Composable
fun WorkoutScreen(navController: NavController) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        // Top Bar
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text("fit’fy", fontSize = 24.sp, color = Color.Black)
            Row {
                Image(
                    painter = painterResource(R.drawable.robot_assistant),
                    contentDescription = null,
                    modifier = Modifier.size(40.dp)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Image(
                    painter = painterResource(R.drawable.july_photo_profile),
                    contentDescription = null,
                    modifier = Modifier
                        .size(40.dp)
                        .clip(CircleShape),
                    contentScale = ContentScale.Crop
                )
            }
        }

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
                onClick = { /* Action pour "New Routine" */ }
            )
            RoutineButton(
                icon = Icons.Default.Search,
                text = "Explore Routines",
                modifier = Modifier.weight(1f),
                onClick = { navController.navigate("explore_routines") }
            )
        }


        Spacer(modifier = Modifier.weight(1f))

        // Bottom Help Button
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(60.dp)
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
                Icon(Icons.Default.Add, contentDescription = null)
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
