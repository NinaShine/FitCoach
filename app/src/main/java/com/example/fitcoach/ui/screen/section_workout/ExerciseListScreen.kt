package com.example.fitcoach.ui.screen.section_workout

import androidx.activity.ComponentActivity
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Info
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import coil.compose.rememberAsyncImagePainter
import com.example.fitcoach.viewmodel.ExerciseViewModel
import com.example.fitcoach.viewmodel.WorkoutViewModel
import com.example.fitcoach.data.model.WorkoutExercise
import com.example.fitcoach.data.model.Exercise
import androidx.navigation.NavController

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ExerciseListScreen(navController: NavController) {
    val exerciseViewModel: ExerciseViewModel = viewModel()

    // SOLUTION : Utiliser le même scope pour le WorkoutViewModel
    val workoutViewModel: WorkoutViewModel = androidx.lifecycle.viewmodel.compose.viewModel(
        viewModelStoreOwner = LocalContext.current as ComponentActivity
    )

    val exercises by exerciseViewModel.exercises.collectAsState()
    val workoutExercises by workoutViewModel.workoutExercises.collectAsState()

    var searchQuery by remember { mutableStateOf("") }
    var showAddDialog by remember { mutableStateOf(false) }
    var selectedExercise by remember { mutableStateOf<Exercise?>(null) }

    val filtered = if (searchQuery.isBlank()) exercises
    else exercises.filter {
        it.name.contains(searchQuery, ignoreCase = true)
    }

    // DEBUG: Observer les changements dans workoutExercises
    LaunchedEffect(workoutExercises) {
        println("DEBUG: Current workout exercises count: ${workoutExercises.size}")
        workoutExercises.forEach { exercise ->
            println("DEBUG: Exercise in workout: ${exercise.name} - ${exercise.series} series x ${exercise.repetitions} reps")
        }
    }

    // Dialog pour ajouter l'exercice avec séries et répétitions
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

                // DEBUG: Vérifier l'ajout
                println("DEBUG: About to add exercise: ${workoutExercise.name}")
                println("DEBUG: ViewModel instance: ${workoutViewModel.hashCode()}")

                workoutViewModel.addExercise(workoutExercise)

                // DEBUG: Vérifier après ajout
                println("DEBUG: Exercise added, current count: ${workoutViewModel.workoutExercises.value.size}")

                showAddDialog = false
                selectedExercise = null

                // Navigation avec debug
                println("DEBUG: Navigating back to quick_workout")
                navController.popBackStack()
            }
        )
    }

    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = {
                    Text("Add an Exercise", fontSize = 20.sp, fontWeight = FontWeight.SemiBold)
                },
                navigationIcon = {
                    TextButton(onClick = { navController.popBackStack() }) {
                        Text("Cancel", color = Color(0xFFE86144))
                    }
                },
                actions = {
                    TextButton(onClick = { /* To create selected list later */ }) {
                        Text("Create", color = Color(0xFFE86144))
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

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                FilterButton("All Equipment", Modifier.weight(1f))
                FilterButton("All Muscles", Modifier.weight(1f))
            }

            Spacer(modifier = Modifier.height(16.dp))
            Text("Popular exercises", fontWeight = FontWeight.SemiBold)

            LazyColumn {
                items(filtered) { ex ->
                    ExerciseItemCard(
                        name = ex.name,
                        gifUrl = ex.gifUrl,
                        target = ex.target,
                        bodyPart = ex.bodyPart,
                        onInfoClick = {
                            navController.navigate("exercise_detail/${ex.id}")
                        },
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

@Composable
fun AddExerciseDialog(
    exercise: Exercise,
    onDismiss: () -> Unit,
    onConfirm: (series: Int, repetitions: Int) -> Unit
) {
    var series by remember { mutableStateOf("3") }
    var repetitions by remember { mutableStateOf("12") }
    var seriesError by remember { mutableStateOf(false) }
    var repetitionsError by remember { mutableStateOf(false) }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Text(
                text = "Add ${exercise.name}",
                fontSize = 18.sp,
                fontWeight = FontWeight.SemiBold
            )
        },
        text = {
            Column {
                Text(
                    text = "Configure your exercise",
                    fontSize = 14.sp,
                    color = Color.Gray,
                    modifier = Modifier.padding(bottom = 16.dp)
                )

                OutlinedTextField(
                    value = series,
                    onValueChange = {
                        series = it
                        seriesError = it.toIntOrNull() == null || it.toIntOrNull()!! <= 0
                    },
                    label = { Text("Number of series") },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    isError = seriesError,
                    modifier = Modifier.fillMaxWidth(),
                    supportingText = {
                        if (seriesError) {
                            Text("Please enter a valid number")
                        }
                    }
                )

                Spacer(modifier = Modifier.height(8.dp))

                OutlinedTextField(
                    value = repetitions,
                    onValueChange = {
                        repetitions = it
                        repetitionsError = it.toIntOrNull() == null || it.toIntOrNull()!! <= 0
                    },
                    label = { Text("Number of repetitions") },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    isError = repetitionsError,
                    modifier = Modifier.fillMaxWidth(),
                    supportingText = {
                        if (repetitionsError) {
                            Text("Please enter a valid number")
                        }
                    }
                )
            }
        },
        confirmButton = {
            TextButton(
                onClick = {
                    val seriesInt = series.toIntOrNull()
                    val repetitionsInt = repetitions.toIntOrNull()

                    if (seriesInt != null && seriesInt > 0 && repetitionsInt != null && repetitionsInt > 0) {
                        // DEBUG: Ajouter des logs
                        println("DEBUG: Adding exercise ${exercise.name} with $seriesInt series and $repetitionsInt reps")
                        onConfirm(seriesInt, repetitionsInt)
                    } else {
                        seriesError = seriesInt == null || seriesInt <= 0
                        repetitionsError = repetitionsInt == null || repetitionsInt <= 0
                    }
                },
                colors = ButtonDefaults.textButtonColors(contentColor = Color(0xFFE86144))
            ) {
                Text("Add Exercise")
            }
        },
        dismissButton = {
            TextButton(
                onClick = onDismiss,
                colors = ButtonDefaults.textButtonColors(contentColor = Color.Gray)
            ) {
                Text("Cancel")
            }
        }
    )
}

@Composable
fun FilterButton(label: String, modifier: Modifier = Modifier) {
    Button(
        onClick = { /* TODO */ },
        shape = RoundedCornerShape(40.dp),
        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFFBF2ED)),
        modifier = modifier
    ) {
        Text(label, color = Color.Black)
    }
}

@Composable
fun ExerciseItemCard(
    name: String,
    gifUrl: String,
    target: String,
    bodyPart: String,
    onInfoClick: () -> Unit,
    onAddClick: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 6.dp),
        colors = CardDefaults.cardColors(containerColor = Color(0xFFFBF2ED))
    ) {
        Row(
            modifier = Modifier.padding(12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Image(
                painter = rememberAsyncImagePainter(gifUrl),
                contentDescription = name,
                modifier = Modifier
                    .size(50.dp)
                    .clip(CircleShape),
                contentScale = ContentScale.Crop
            )
            Spacer(modifier = Modifier.width(12.dp))
            Column(modifier = Modifier.weight(1f)) {
                Text(name, fontWeight = FontWeight.SemiBold)
                Text("$target • $bodyPart", fontSize = 14.sp, color = Color.Gray)
            }
            IconButton(onClick = onInfoClick) {
                Icon(Icons.Default.Info, contentDescription = "Info")
            }
            IconButton(onClick = onAddClick) {
                Icon(
                    imageVector = Icons.Default.Add,
                    contentDescription = "Add",
                    tint = Color(0xFFE86144)
                )
            }
        }
    }
}
