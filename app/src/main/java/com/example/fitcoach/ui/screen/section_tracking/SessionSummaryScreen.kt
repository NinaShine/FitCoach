package com.example.fitcoach.viewmodel.track_section

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
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
fun SessionSummaryScreen(
    navController: NavController,
    distanceKm: Double,
    durationMs: Long,
    speedKmH: Double,
    calories: Double,
    steps: Int,
    activityType: String
) {
    val time = String.format(
        "%02d:%02d:%02d",
        (durationMs / (1000 * 60 * 60)),
        (durationMs / (1000 * 60)) % 60,
        (durationMs / 1000) % 60
    )

    val orange = Color(0xFFE86144)

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp)
            .background(Color(0xFFFDF6F4)),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = "Résumé de séance",
            style = MaterialTheme.typography.headlineMedium,
            fontWeight = FontWeight.Bold,
            color = orange
        )

        Spacer(modifier = Modifier.height(24.dp))

        Card(
            modifier = Modifier
                .fillMaxWidth(),
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(containerColor = Color.White),
            elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
        ) {
            Column(
                modifier = Modifier
                    .padding(20.dp),
                horizontalAlignment = Alignment.Start
            ) {
                SummaryItem(label = "Activité", value = activityType)
                SummaryItem(label = "Distance", value = "%.2f km".format(distanceKm))
                SummaryItem(label = "Durée", value = time)
                SummaryItem(label = "Vitesse moyenne", value = "%.2f km/h".format(speedKmH))
                SummaryItem(label = "Calories", value = "%.0f kcal".format(calories))
                SummaryItem(label = "Pas", value = "$steps")
            }
        }

        Spacer(modifier = Modifier.height(32.dp))

        Button(
            onClick = {
                navController.popBackStack("accueil", inclusive = false)
            },
            modifier = Modifier
                .fillMaxWidth()
                .height(56.dp),
            shape = RoundedCornerShape(20.dp),
            colors = ButtonDefaults.buttonColors(containerColor = orange)
        ) {
            Text("Retour à l'accueil", color = Color.White)
        }
    }
}

@Composable
private fun SummaryItem(label: String, value: String) {
    Column(modifier = Modifier.padding(vertical = 8.dp)) {
        Text(text = label, fontSize = 14.sp, color = Color.Gray)
        Text(
            text = value,
            fontSize = 18.sp,
            fontWeight = FontWeight.Medium,
            color = Color.Black
        )
    }
}
