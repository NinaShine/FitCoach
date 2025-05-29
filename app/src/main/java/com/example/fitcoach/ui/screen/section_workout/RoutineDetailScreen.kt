package com.example.fitcoach.ui.screen

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import coil.compose.rememberAsyncImagePainter
import com.example.fitcoach.repository.RoutineRepository
import com.example.fitcoach.data.model.WorkoutExercise

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RoutineDetailScreen(routineId: String, navController: NavController) {
    val routine = RoutineRepository.getRoutineById(routineId)

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(routine?.title ?: "Routine", fontSize = 20.sp)
                },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(
                            painter = painterResource(id = com.example.fitcoach.R.drawable.ic_back), // ou utilisez Icons.Default.ArrowBack
                            contentDescription = "Back"
                        )
                    }
                }
            )
        }
    ) { padding ->
        if (routine == null) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(padding),
                contentAlignment = Alignment.Center
            ) {
                Text("Routine not found.")
            }
        } else {
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(padding)
                    .padding(horizontal = 16.dp)
            ) {
                items(routine.exercises) { ex ->
                    ExerciseDetailCard(ex)
                }
            }
        }
    }
}

@Composable
fun ExerciseDetailCard(ex: WorkoutExercise) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = Color(0xFFFBF2ED))
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Image(
                painter = rememberAsyncImagePainter(ex.gifUrl),
                contentDescription = ex.name,
                modifier = Modifier
                    .size(60.dp)
                    .clip(CircleShape),
                contentScale = ContentScale.Crop
            )

            Spacer(modifier = Modifier.width(12.dp))

            Column {
                Text(ex.name, fontSize = 16.sp)
                Text("Target: ${ex.target} - ${ex.bodyPart}", fontSize = 14.sp, color = Color.Gray)
                Text("Series: ${ex.series}, Reps: ${ex.repetitions}, Kg: ${ex.kg ?: "-"}", fontSize = 13.sp)
            }
        }
    }
}
