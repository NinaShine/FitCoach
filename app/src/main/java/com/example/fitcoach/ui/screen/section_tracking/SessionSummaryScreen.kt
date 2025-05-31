package com.example.fitcoach.viewmodel.track_section

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
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

    Column(
        Modifier
            .fillMaxSize()
            .padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text("Résumé de séance", fontSize = 24.sp, fontWeight = FontWeight.Bold)

        Spacer(modifier = Modifier.height(16.dp))
        Text("Activité : $activityType")
        Text("Distance : %.2f km".format(distanceKm))
        Text("Durée : $time")
        Text("Vitesse moyenne : %.2f km/h".format(speedKmH))
        Text("Calories : %.0f kcal".format(calories))
        Text("Pas : $steps")

        Spacer(modifier = Modifier.height(24.dp))
        Button(onClick = { navController.popBackStack("accueil", inclusive = false) }) {
            Text("Retour à l'accueil")
        }
    }
}
