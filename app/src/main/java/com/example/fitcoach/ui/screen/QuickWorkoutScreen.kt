package com.example.fitcoach.ui.screen

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccessTime
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.fitcoach.R
import kotlinx.coroutines.delay

@Composable
fun QuickWorkoutScreen(navController: NavController) {
    var durationSeconds by remember { mutableStateOf(0) }
    var isRunning by remember { mutableStateOf(false) }
    var volume by remember { mutableStateOf(0) }
    var series by remember { mutableStateOf(0) }

    // Chrono logic
    LaunchedEffect(isRunning) {
        while (isRunning) {
            delay(1000L)
            durationSeconds++
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        // Header
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text("fit’fy", fontSize = 24.sp, fontWeight = FontWeight.Bold)
            Button(
                onClick = { isRunning = false },
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFEF8E79)),
                shape = RoundedCornerShape(8.dp)
            ) {
                Icon(Icons.Default.AccessTime, contentDescription = null)
                Spacer(modifier = Modifier.width(4.dp))
                Text("End workout", color = Color.White)
            }
        }

        Spacer(modifier = Modifier.height(24.dp))

        // Stats Row
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceAround
        ) {
            WorkoutStat("Duration", formatTime(durationSeconds))
            WorkoutStat("Volume", "$volume kg")
            WorkoutStat("Series", "$series")
        }

        Spacer(modifier = Modifier.height(32.dp))

        // Dumbbell Start
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier
                .fillMaxWidth()
                .clickable { isRunning = true } // Start chrono
        ) {
            Icon(
                painter = painterResource(R.drawable.dumbell),
                contentDescription = "Start",
                modifier = Modifier.size(70.dp),
                tint = Color(0xFFE86144)
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text("Start", fontSize = 18.sp, fontWeight = FontWeight.SemiBold, color = Color(0xFFE86144))
        }

        Spacer(modifier = Modifier.height(12.dp))
        Text(
            text = "Add an exercise to start your workout",
            fontSize = 14.sp,
            modifier = Modifier.align(Alignment.CenterHorizontally)
        )

        Spacer(modifier = Modifier.height(24.dp))

        // Add Exercise Button
        Button(
            onClick = { navController.navigate("exercise_list") },
            modifier = Modifier
                .fillMaxWidth()
                .height(60.dp),
            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFFBF2ED)),
            shape = RoundedCornerShape(16.dp)
        ) {
            Icon(Icons.Default.Add, contentDescription = null, tint = Color.Black)
            Spacer(modifier = Modifier.width(8.dp))
            Text("Add an Exercise", color = Color.Black)
        }

        Spacer(modifier = Modifier.height(12.dp))

        // Settings + Abandon (uniform style)
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Button(
                onClick = { /* TODO settings */ },
                modifier = Modifier.weight(1f),
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFFBF2ED)),
                shape = RoundedCornerShape(16.dp)
            ) {
                Text("Settings", color = Color.Black)
            }

            Button(
                onClick = {
                    // Show confirm dialog here
                    navController.navigate("confirm_abandon")
                },
                modifier = Modifier.weight(1f),
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFFBF2ED)),
                shape = RoundedCornerShape(16.dp)
            ) {
                Text("Abandon Training", color = Color(0xFFE86144))
            }
        }

        Spacer(modifier = Modifier.height(32.dp))

        // BOT
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .clickable { navController.navigate("chatbot") },
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            TextBotMessage("You're the GOAT")
            TextBotMessage("Keep up the hard work !")
            TextBotMessage("Tap me if you need help !", highlight = true)
        }
    }
}

@Composable
fun WorkoutStat(title: String, value: String) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Text(title, fontSize = 14.sp)
        Spacer(modifier = Modifier.height(4.dp))
        Text(value, fontSize = 18.sp, fontWeight = FontWeight.Bold)
    }
}

@Composable
fun TextBotMessage(text: String, highlight: Boolean = false) {
    Box(
        modifier = Modifier
            .padding(4.dp)
            .background(
                color = if (highlight) Color(0xFFEF8E79) else Color(0xFFFFD7C2),
                shape = RoundedCornerShape(16.dp)
            )
            .padding(horizontal = 12.dp, vertical = 8.dp)
    ) {
        Text(text, color = Color.Black, fontSize = 14.sp)
    }
}

fun formatTime(totalSeconds: Int): String {
    val minutes = totalSeconds / 60
    val seconds = totalSeconds % 60
    return "${minutes}min ${seconds}s"
}
