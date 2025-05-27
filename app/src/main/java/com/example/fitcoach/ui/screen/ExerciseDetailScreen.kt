package com.example.fitcoach.ui.screen

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import coil.compose.rememberAsyncImagePainter
import androidx.navigation.NavController
import androidx.navigation.NavBackStackEntry
import com.example.fitcoach.viewmodel.ExerciseViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ExerciseDetailScreen(
    navController: NavController,
    backStackEntry: NavBackStackEntry,
    viewModel: ExerciseViewModel = viewModel()
) {
    val id = backStackEntry.arguments?.getString("id")
    val exercise = viewModel.exercises.collectAsState().value.find { it.id == id }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(exercise?.name ?: "", fontSize = 18.sp) },
                navigationIcon = {
                    TextButton(onClick = { navController.popBackStack() }) {
                        Text("Back", color = Color(0xFFE86144))
                    }
                }
            )
        }
    ) { padding ->
        Column(modifier = Modifier
            .fillMaxSize()
            .padding(padding)
            .padding(16.dp)) {

            exercise?.let {
                Image(
                    painter = rememberAsyncImagePainter(it.gifUrl),
                    contentDescription = it.name,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(300.dp)
                        .padding(8.dp)
                )

                Spacer(modifier = Modifier.height(8.dp))

                Button(
                    onClick = { /* instructions expandable */ },
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFFBF2ED))
                ) {
                    Text("Instructions", color = Color.Black)
                }

                Spacer(modifier = Modifier.height(8.dp))

                Text(
                    text = """
                        1. Grab a pair of dumbbells and stand up straight with your feet in a comfortable position and your shoulders back.
                        2. Have your wrists pointed forward and your arms extended.
                        3. Take a breath and press both dumbbells down simultaneously. Lift the weights until your wrists are slightly higher than your elbows.
                        4. Extend your arms slowly and exhale as you lower.
                    """.trimIndent(),
                    lineHeight = 20.sp
                )

                Spacer(modifier = Modifier.height(24.dp))

                Button(
                    onClick = { /* add this exercise */ },
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(16.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFE86144))
                ) {
                    Text("Add an exercise", color = Color.White)
                }
            }
        }
    }
}
