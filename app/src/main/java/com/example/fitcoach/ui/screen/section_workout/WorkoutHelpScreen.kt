package com.example.fitcoach.ui.screen

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController

@Composable
fun WorkoutHelpScreen(navController: NavController) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 16.dp, vertical = 8.dp)
    ) {
        // Top bar
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 8.dp)
        ) {
            Icon(
                imageVector = Icons.Default.ArrowBack,
                contentDescription = "Back",
                modifier = Modifier
                    .size(28.dp)
                    .clickable { navController.navigate("workout") }
            )
            Spacer(modifier = Modifier.width(12.dp))
            Text(
                text = "How to get started",
                fontSize = 20.sp,
                fontWeight = FontWeight.SemiBold
            )
        }

        Spacer(modifier = Modifier.height(12.dp))

        // Section: First step
        Text(
            "First step",
            fontSize = 18.sp,
            fontWeight = FontWeight.SemiBold,
            color = Color(0xFFE86144)
        )
        Spacer(modifier = Modifier.height(8.dp))
        Text("Starting a workout is easy! Simply tap “Start Empty Workout” and start tracking your sets.")
        Spacer(modifier = Modifier.height(8.dp))
        Text("Add an exercise to your workout, enter the weight and reps for each set, then easily delete it once you're done.")
        Spacer(modifier = Modifier.height(8.dp))
        Text("If you have a workout routine that you do often, you can create a “routine.”")
        Spacer(modifier = Modifier.height(8.dp))
        Text("Routines are reusable and save you time by allowing you to plan your workout before going to the gym.")

        Spacer(modifier = Modifier.height(20.dp))

        // Section: Creating routines
        Text(
            "Creating routines",
            fontSize = 18.sp,
            fontWeight = FontWeight.SemiBold,
            color = Color(0xFFE86144)
        )
        Spacer(modifier = Modifier.height(8.dp))
        Text("Simply select the exercises you want to chain into your routine and add the reps and weight to your sets.")
        Spacer(modifier = Modifier.height(8.dp))
        Text("Save your new routine and use it the next time you workout.")
    }
}
