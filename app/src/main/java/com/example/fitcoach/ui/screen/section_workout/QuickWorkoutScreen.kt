package com.example.fitcoach.ui.screen

import androidx.activity.ComponentActivity
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccessTime
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.fitcoach.R
import com.example.fitcoach.viewmodel.WorkoutViewModel
import kotlinx.coroutines.delay
import coil.compose.AsyncImage

@Composable
fun QuickWorkoutScreen(navController: NavController) {
    val workoutViewModel: WorkoutViewModel = androidx.lifecycle.viewmodel.compose.viewModel(
        viewModelStoreOwner = LocalContext.current as ComponentActivity
    )
    val workoutExercises by workoutViewModel.workoutExercises.collectAsState()
    val scrollState = rememberScrollState()


    var durationSeconds by remember { mutableStateOf(0) }
    var isRunning by remember { mutableStateOf(false) }
    var volume by remember { mutableStateOf(0) }

    val totalSeries = workoutViewModel.getTotalSeries()
    var showConfirmDialog by remember { mutableStateOf(false) }

    // Calculer le volume total (poids * répétitions * séries)
    val totalVolume = workoutExercises.sumOf { exercise ->
        (exercise.weight ?: 0) * exercise.repetitions * exercise.series
    }

    LaunchedEffect(isRunning) {
        while (isRunning) {
            delay(1000L)
            durationSeconds++
        }
    }

    // Mettre à jour le volume quand les exercices changent
    LaunchedEffect(workoutExercises) {
        volume = totalVolume
    }

    if (showConfirmDialog) {
        AlertDialog(
            onDismissRequest = { showConfirmDialog = false },
            title = { Text("Confirm Exit") },
            text = { Text("Are you sure you want to abandon your training session?") },
            confirmButton = {
                TextButton(
                    onClick = {
                        showConfirmDialog = false
                        isRunning = false
                        workoutViewModel.clearWorkout()
                        navController.navigate("workout") {
                            popUpTo("quick_workout") { inclusive = true }
                        }
                    }
                ) {
                    Text("Yes", color = Color.Red)
                }
            },
            dismissButton = {
                TextButton(onClick = { showConfirmDialog = false }) {
                    Text("Cancel")
                }
            }
        )
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(scrollState)
            .padding(16.dp)
    ) {
        // Header avec titre et bouton fin d'entraînement
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text("fit'fy", fontSize = 24.sp, fontWeight = FontWeight.Bold)
            Button(
                onClick = {
                    if (isRunning) {
                        isRunning = false
                    }
                    // TODO: Sauvegarder l'entraînement
                },
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFEF8E79)),
                shape = RoundedCornerShape(8.dp)
            ) {
                Icon(Icons.Default.AccessTime, contentDescription = null)
                Spacer(modifier = Modifier.width(4.dp))
                Text("End workout", color = Color.White)
            }
        }

        Spacer(modifier = Modifier.height(24.dp))

        // Statistiques de l'entraînement
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceAround
        ) {
            WorkoutStat("Duration", formatTime(durationSeconds))
            WorkoutStat("Volume", "$volume kg")
            WorkoutStat("Series", "$totalSeries")
        }

        Spacer(modifier = Modifier.height(32.dp))

        // Bouton Start/Pause
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier
                .fillMaxWidth()
                .clickable {
                    if (workoutExercises.isNotEmpty()) {
                        isRunning = !isRunning
                    }
                }
        ) {
            Icon(
                painter = painterResource(R.drawable.dumbell),
                contentDescription = if (isRunning) "Pause" else "Start",
                modifier = Modifier.size(70.dp),
                tint = if (workoutExercises.isEmpty()) Color.Gray else Color(0xFFE86144)
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = if (isRunning) "Pause" else "Start",
                fontSize = 18.sp,
                fontWeight = FontWeight.SemiBold,
                color = if (workoutExercises.isEmpty()) Color.Gray else Color(0xFFE86144)
            )
        }

        Spacer(modifier = Modifier.height(24.dp))

        // Liste des exercices ajoutés
        if (workoutExercises.isNotEmpty()) {
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(containerColor = Color(0xFFFBF2ED)),
                shape = RoundedCornerShape(16.dp)
            ) {
                Column(
                    modifier = Modifier.padding(16.dp)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "Your workout exercises (${workoutExercises.size})",
                            fontSize = 16.sp,
                            fontWeight = FontWeight.SemiBold
                        )
                        Text(
                            text = "Total: $totalSeries series",
                            fontSize = 14.sp,
                            color = Color(0xFFE86144),
                            fontWeight = FontWeight.Medium
                        )
                    }

                    Spacer(modifier = Modifier.height(12.dp))

                    LazyColumn(
                        modifier = Modifier.heightIn(max = 400.dp),
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        items(workoutExercises) { exercise ->
                            WorkoutExerciseCard(
                                exercise = exercise,
                                onRemove = { workoutViewModel.removeExercise(exercise.id) },
                                onEdit = {
                                    // TODO: Implémenter l'édition des séries/reps
                                }
                            )
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))
        } else {
            // Message quand aucun exercice n'est ajouté
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(containerColor = Color(0xFFFBF2ED)),
                shape = RoundedCornerShape(16.dp)
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(32.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Icon(
                        painter = painterResource(R.drawable.dumbell),
                        contentDescription = null,
                        modifier = Modifier.size(48.dp),
                        tint = Color.Gray
                    )
                    Spacer(modifier = Modifier.height(16.dp))
                    Text(
                        text = "No exercises yet",
                        fontSize = 18.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = Color.Gray
                    )
                    Text(
                        text = "Add an exercise to start your workout",
                        fontSize = 14.sp,
                        color = Color.Gray
                    )
                }
            }
            Spacer(modifier = Modifier.height(16.dp))
        }

        // Bouton Ajouter un exercice
        Button(
            onClick = { navController.navigate("exercise_list") },
            modifier = Modifier
                .fillMaxWidth()
                .height(60.dp),
            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFE86144)),
            shape = RoundedCornerShape(16.dp)
        ) {
            Icon(Icons.Default.Add, contentDescription = null, tint = Color.White)
            Spacer(modifier = Modifier.width(8.dp))
            Text(
                text = if (workoutExercises.isEmpty()) "Add your first exercise" else "Add another exercise",
                color = Color.White,
                fontWeight = FontWeight.SemiBold
            )
        }

        Spacer(modifier = Modifier.height(12.dp))

        // Boutons Settings et Abandon
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Button(
                onClick = { /* TODO: Settings */ },
                modifier = Modifier.weight(1f),
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFFBF2ED)),
                shape = RoundedCornerShape(16.dp)
            ) {
                Text("Settings", color = Color.Black)
            }

            Button(
                onClick = { showConfirmDialog = true },
                modifier = Modifier.weight(1f),
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFFBF2ED)),
                shape = RoundedCornerShape(16.dp)
            ) {
                Text("Abandon Training", color = Color(0xFFE86144))
            }
        }

        Spacer(modifier = Modifier.height(32.dp))

        // Bot assistant
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 24.dp),
            contentAlignment = Alignment.Center
        ) {
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                TextBotMessage(
                    if (workoutExercises.isEmpty()) "Add some exercises to get started!"
                    else "You're the GOAT"
                )

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceEvenly
                ) {
                    if (workoutExercises.isNotEmpty()) {
                        TextBotMessage("Keep up the hard work!")
                    }
                    TextBotMessage("Tap me if you need help!", highlight = true)
                }
            }

            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Spacer(modifier = Modifier.height(60.dp))
                Box(
                    modifier = Modifier
                        .size(80.dp)
                        .background(color = Color(0xFFFBF2ED), shape = RoundedCornerShape(40.dp))
                        .clickable { navController.navigate("chatbot") },
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        painter = painterResource(R.drawable.robot_assistant),
                        contentDescription = "Bot",
                        modifier = Modifier.size(48.dp),
                        tint = Color.Unspecified
                    )
                }
            }
        }
    }
}

// Composant amélioré pour afficher les exercices
@Composable
fun WorkoutExerciseCard(
    exercise: com.example.fitcoach.data.model.WorkoutExercise,
    onRemove: () -> Unit,
    onEdit: () -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        shape = RoundedCornerShape(12.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Row(
            modifier = Modifier.padding(12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            AsyncImage(
                model = exercise.gifUrl,
                contentDescription = exercise.name,
                modifier = Modifier
                    .size(60.dp)
                    .clip(CircleShape)
            )

            Spacer(modifier = Modifier.width(12.dp))

            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = exercise.name,
                    fontWeight = FontWeight.SemiBold,
                    fontSize = 16.sp
                )
                Text(
                    text = "${exercise.target} • ${exercise.bodyPart}",
                    fontSize = 13.sp,
                    color = Color.Gray
                )
                Spacer(modifier = Modifier.height(4.dp))
                Row(
                    horizontalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    Text(
                        text = "${exercise.series} sets",
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Medium,
                        color = Color(0xFFE86144)
                    )
                    Text(
                        text = "${exercise.repetitions} reps",
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Medium,
                        color = Color(0xFFE86144)
                    )
                    if (exercise.weight != null && exercise.weight > 0) {
                        Text(
                            text = "${exercise.weight} kg",
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Medium,
                            color = Color(0xFFE86144)
                        )
                    }
                }
            }

            Row {
                IconButton(onClick = onEdit) {
                    Icon(
                        Icons.Default.Edit,
                        contentDescription = "Edit",
                        tint = Color.Gray
                    )
                }
                IconButton(onClick = onRemove) {
                    Icon(
                        Icons.Default.Delete,
                        contentDescription = "Remove",
                        tint = Color(0xFFE86144)
                    )
                }
            }
        }
    }
}

@Composable
fun WorkoutStat(title: String, value: String) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Text(title, fontSize = 14.sp, color = Color.Gray)
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