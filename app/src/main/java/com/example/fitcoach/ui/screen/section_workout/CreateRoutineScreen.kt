package com.example.fitcoach.ui.screen.section_workout

import androidx.activity.ComponentActivity
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import coil.compose.rememberAsyncImagePainter
import com.example.fitcoach.R
import com.example.fitcoach.viewmodel.CreateRoutineViewModel

@Composable
fun CreateRoutineScreen(navController: NavController) {
    val viewModel: CreateRoutineViewModel = viewModel(
        viewModelStoreOwner = LocalContext.current as ComponentActivity
    )
    val exercises by viewModel.exercises.collectAsState()
    var routineTitle by remember { mutableStateOf(TextFieldValue("")) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "Cancel",
                color = Color(0xFFEF8E79),
                modifier = Modifier.clickable { navController.popBackStack() }
            )
            Text("Create a Routine", fontSize = 18.sp)
            Button(
                onClick = {
                    viewModel.saveRoutine(routineTitle.text)
                    navController.popBackStack()
                },
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFEF8E79))
            ) {
                Text("Save")
            }
        }

        Spacer(modifier = Modifier.height(24.dp))

        TextField(
            value = routineTitle,
            onValueChange = { routineTitle = it },
            placeholder = { Text("Routine title") },
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(12.dp))
                .background(Color(0xFFFBF2ED)),
            colors = TextFieldDefaults.colors(
                focusedContainerColor = Color.Transparent,
                unfocusedContainerColor = Color.Transparent,
                focusedIndicatorColor = Color.Transparent,
                unfocusedIndicatorColor = Color.Transparent
            )
        )

        Spacer(modifier = Modifier.height(24.dp))

        Button(
            onClick = { navController.navigate("exercise_list_2") },
            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFFBF2ED)),
            modifier = Modifier
                .fillMaxWidth()
                .height(60.dp),
            shape = RoundedCornerShape(12.dp)
        ) {
            Icon(Icons.Default.Add, contentDescription = null, tint = Color.Black)
            Spacer(modifier = Modifier.width(8.dp))
            Text("Add an Exercise", color = Color.Black)
        }

        Spacer(modifier = Modifier.height(24.dp))

        if (exercises.isNotEmpty()) {
            Text("Exercises in this routine:", fontSize = 16.sp, modifier = Modifier.padding(bottom = 8.dp))
            LazyColumn {
                items(exercises) { ex ->
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 4.dp),
                        colors = CardDefaults.cardColors(containerColor = Color(0xFFFBF2ED))
                    ) {
                        Row(
                            modifier = Modifier.padding(12.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Image(
                                painter = rememberAsyncImagePainter(ex.gifUrl),
                                contentDescription = ex.name,
                                modifier = Modifier
                                    .size(50.dp)
                                    .clip(CircleShape)
                            )
                            Spacer(modifier = Modifier.width(12.dp))
                            Column(modifier = Modifier.weight(1f)) {
                                Text(ex.name, fontSize = 16.sp)
                                Text("${ex.series} x ${ex.repetitions}", fontSize = 12.sp, color = Color.Gray)
                            }
                            IconButton(onClick = { viewModel.removeExercise(ex) }) {
                                Icon(Icons.Default.Delete, contentDescription = "Delete")
                            }
                        }
                    }
                }
            }
        } else {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier.fillMaxWidth()
            ) {
                Image(
                    painter = painterResource(R.drawable.dumbell),
                    contentDescription = null,
                    modifier = Modifier.size(64.dp)
                )
                Spacer(modifier = Modifier.height(12.dp))
                Text("Start by adding an exercise to your routine.", fontSize = 16.sp)
            }
        }
    }
}
