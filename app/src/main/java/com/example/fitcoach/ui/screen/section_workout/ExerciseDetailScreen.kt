package com.example.fitcoach.ui.screen.section_workout

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.KeyboardArrowUp
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
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
    var isInstructionsExpanded by remember { mutableStateOf(false) }

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
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            exercise?.let { ex ->
                item {
                    Image(
                        painter = rememberAsyncImagePainter(ex.gifUrl),
                        contentDescription = ex.name,
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(300.dp)
                    )
                }

                item {
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        colors = CardDefaults.cardColors(containerColor = Color(0xFFFBF2ED))
                    ) {
                        Column(modifier = Modifier.padding(16.dp)) {
                            Text(
                                text = "Informations",
                                fontSize = 18.sp,
                                fontWeight = FontWeight.SemiBold
                            )
                            Spacer(modifier = Modifier.height(8.dp))
                            Text("Partie du corps: ${ex.bodyPart}")
                            Text("Équipement: ${ex.equipment}")
                            Text("Muscle ciblé: ${ex.target}")
                        }
                    }
                }

                item {
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        colors = CardDefaults.cardColors(containerColor = Color(0xFFFBF2ED))
                    ) {
                        Column(modifier = Modifier.padding(16.dp)) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Text(
                                    text = "Instructions",
                                    fontSize = 18.sp,
                                    fontWeight = FontWeight.SemiBold
                                )
                                IconButton(
                                    onClick = { isInstructionsExpanded = !isInstructionsExpanded }
                                ) {
                                    Icon(
                                        imageVector = if (isInstructionsExpanded)
                                            Icons.Default.KeyboardArrowUp
                                        else
                                            Icons.Default.KeyboardArrowDown,
                                        contentDescription = if (isInstructionsExpanded)
                                            "Réduire"
                                        else
                                            "Développer"
                                    )
                                }
                            }

                            if (isInstructionsExpanded) {
                                Spacer(modifier = Modifier.height(8.dp))
                                ex.instructions.forEachIndexed { index, instruction ->
                                    Row(
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .padding(vertical = 4.dp)
                                    ) {
                                        Text(
                                            text = "${index + 1}. ",
                                            fontWeight = FontWeight.Medium,
                                            color = Color(0xFFE86144)
                                        )
                                        Text(
                                            text = instruction,
                                            modifier = Modifier.weight(1f),
                                            lineHeight = 20.sp
                                        )
                                    }
                                }
                            }
                        }
                    }
                }

            }
        }
    }
}