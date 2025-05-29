package com.example.fitcoach.ui.screen.section_workout

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.fitcoach.data.model.Exercise
import com.example.fitcoach.data.model.WorkoutExercise
import com.example.fitcoach.viewmodel.CreateRoutineViewModel
import androidx.activity.ComponentActivity
import androidx.navigation.NavController
import com.example.fitcoach.viewmodel.ExerciseViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ExerciseListScreen2(navController: NavController) {
    val exerciseViewModel: ExerciseViewModel = viewModel()
    val routineViewModel: CreateRoutineViewModel = viewModel(
        viewModelStoreOwner = LocalContext.current as ComponentActivity
    )

    val exercises by exerciseViewModel.exercises.collectAsState()
    val routineExercises by routineViewModel.exercises.collectAsState()

    var searchQuery by remember { mutableStateOf("") }
    var showAddDialog by remember { mutableStateOf(false) }
    var selectedExercise by remember { mutableStateOf<Exercise?>(null) }

    val filtered = if (searchQuery.isBlank()) exercises
    else exercises.filter { it.name.contains(searchQuery, ignoreCase = true) }

    if (showAddDialog && selectedExercise != null) {
        AddExerciseDialog(
            exercise = selectedExercise!!,
            onDismiss = {
                showAddDialog = false
                selectedExercise = null
            },
            onConfirm = { series, repetitions ->
                val workoutExercise = WorkoutExercise(
                    id = selectedExercise!!.id,
                    name = selectedExercise!!.name,
                    bodyPart = selectedExercise!!.bodyPart,
                    equipment = selectedExercise!!.equipment,
                    gifUrl = selectedExercise!!.gifUrl,
                    target = selectedExercise!!.target,
                    series = series,
                    repetitions = repetitions,
                    instructions = selectedExercise!!.instructions
                )
                routineViewModel.addExercise(workoutExercise)
                showAddDialog = false
                selectedExercise = null
                navController.popBackStack()
            }
        )
    }

    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = { Text("Add an Exercise", fontSize = 20.sp, fontWeight = FontWeight.SemiBold) },
                navigationIcon = {
                    TextButton(onClick = { navController.popBackStack() }) {
                        Text("Cancel", color = Color(0xFFE86144))
                    }
                }
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(horizontal = 16.dp)
        ) {
            OutlinedTextField(
                value = searchQuery,
                onValueChange = { searchQuery = it },
                placeholder = { Text("Search") },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 12.dp),
                shape = RoundedCornerShape(30.dp),
                colors = OutlinedTextFieldDefaults.colors(
                    unfocusedContainerColor = Color(0xFFFBF2ED),
                    focusedContainerColor = Color(0xFFFBF2ED)
                )
            )

            LazyColumn {
                items(filtered) { ex ->
                    ExerciseItemCard(
                        name = ex.name,
                        gifUrl = ex.gifUrl,
                        target = ex.target,
                        bodyPart = ex.bodyPart,
                        onInfoClick = {},
                        onAddClick = {
                            selectedExercise = ex
                            showAddDialog = true
                        }
                    )
                }
            }
        }
    }
}

