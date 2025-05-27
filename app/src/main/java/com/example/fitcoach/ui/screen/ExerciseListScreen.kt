package com.example.fitcoach.ui.screen

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import coil.compose.rememberAsyncImagePainter
import com.example.fitcoach.viewmodel.ExerciseViewModel
import androidx.navigation.NavController

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ExerciseListScreen(navController: NavController) {
    val vm: ExerciseViewModel = viewModel()
    val exercises by vm.exercises.collectAsState()
    var searchQuery by remember { mutableStateOf("") }

    val filtered = if (searchQuery.isBlank()) exercises
    else exercises.filter {
        it.name.contains(searchQuery, ignoreCase = true)
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
        Column(modifier = Modifier
            .fillMaxSize()
            .padding(padding)
            .padding(horizontal = 16.dp)) {

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
                    ExerciseItemCard(ex.name, ex.gifUrl, ex.target, onInfoClick = {
                        navController.navigate("exercise_detail/${ex.id}")
                    })
                }
            }
        }
    }
}

@Composable
fun FilterButton(label: String, modifier: Modifier = Modifier) {
    Button(
        onClick = { /* TODO */ },
        shape = RoundedCornerShape(40.dp),
        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFFBF2ED)),
        //modifier = Modifier.weight(1f)
    ) {
        Text(label, color = Color.Black)
    }
}

@Composable
fun ExerciseItemCard(
    name: String,
    gifUrl: String,
    target: String,
    onInfoClick: () -> Unit
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
                Text(target)
            }
            IconButton(onClick = onInfoClick) {
                Icon(Icons.Default.Info, contentDescription = "Info")
            }
            IconButton(onClick = { /* TODO: Add exercise */ }) {
                Icon(Icons.Default.Add, contentDescription = "Add")
            }
        }
    }
}
