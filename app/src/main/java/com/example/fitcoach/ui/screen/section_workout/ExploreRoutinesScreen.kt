package com.example.fitcoach.ui.screen.section_workout

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material.icons.filled.FilterList
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import coil.compose.AsyncImage
import com.example.fitcoach.viewmodel.ExploreViewModel
import com.example.fitcoach.viewmodel.UserProfileViewModel
import com.example.fitcoach.data.model.YouTubeVideo

@Composable
fun CategoryButton(
    text: String,
    icon: String,
    isSelected: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val backgroundColor = if (isSelected) Color(0xFFFAC9A8) else Color(0xFFFBF2ED)

    Card(
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = backgroundColor),
        modifier = modifier
            .height(60.dp)
            .clickable { onClick() }
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(12.dp),
            contentAlignment = Alignment.Center
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.Center
            ) {
                Text(
                    text = text,
                    fontSize = 14.sp,
                    color = Color.Black,
                    textAlign = TextAlign.Center,
                    modifier = Modifier.weight(1f, fill = false)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = icon,
                    fontSize = 20.sp
                )
            }
        }
    }
}

@Composable
fun ExploreRoutinesScreen(
    viewModel: ExploreViewModel = viewModel(),
    profileViewModel: UserProfileViewModel = viewModel(),
    onBack: () -> Unit
) {
    val videos by viewModel.videos.collectAsState()

    LaunchedEffect(Unit) {
        profileViewModel.fetchLocation()
        profileViewModel.fetchLevel()
        profileViewModel.fetchFitnessGoal()
    }

    val location = profileViewModel.location.value
    val level = profileViewModel.level.value
    val goal = profileViewModel.fitnessGoal.value

    val defaultQuery = when (location?.lowercase()) {
        "maison" -> "home workout"
        "salle" -> "gym workout"
        else -> "fitness routine"
    }

    LaunchedEffect(location) {
        viewModel.searchVideos(defaultQuery)
    }

    val levelOptions = listOf("Beginner", "Intermediate", "Advanced")
    val objectiveOptions = listOf("Fat Loss", "Muscle Gain", "Endurance")
    val equipmentOptions = listOf("Dumbbells", "Bodyweight", "Full Gym")
    val durationOptions = listOf("< 10 min", "10-20 min", "> 20 min")

    var selectedLevel by remember { mutableStateOf<String?>(null) }
    var selectedObjective by remember { mutableStateOf<String?>(null) }
    var selectedEquipment by remember { mutableStateOf<String?>(null) }
    var selectedDuration by remember { mutableStateOf<String?>(null) }
    var selectedCategory by remember { mutableStateOf<String?>(null) }

    var showLevelMenu by remember { mutableStateOf(false) }
    var showObjectiveMenu by remember { mutableStateOf(false) }
    var showEquipmentMenu by remember { mutableStateOf(false) }
    var showFilterDialog by remember { mutableStateOf(false) }

    var showAll by remember { mutableStateOf(false) }

    val listState = rememberLazyListState()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(Icons.Default.ArrowBack, contentDescription = null, modifier = Modifier.clickable { onBack() })
            Spacer(modifier = Modifier.weight(1f))
            Text("Explore", fontSize = 20.sp, textAlign = TextAlign.Center)
            Spacer(modifier = Modifier.weight(1f))
            Spacer(modifier = Modifier.width(24.dp))
        }

        Text("Programs", fontSize = 18.sp, modifier = Modifier.padding(start = 16.dp, bottom = 8.dp))

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            FilterButtonWithIcon("Filters", null, { showFilterDialog = true }, Icons.Default.FilterList)
            FilterButtonWithIcon("Level", selectedLevel, { showLevelMenu = true }, Icons.Default.ArrowDropDown)
            FilterButtonWithIcon("Objective", selectedObjective, { showObjectiveMenu = true }, Icons.Default.ArrowDropDown)
            FilterButtonWithIcon("Equipment", selectedEquipment, { showEquipmentMenu = true }, Icons.Default.ArrowDropDown)
        }

        DropdownMenu(expanded = showLevelMenu, onDismissRequest = { showLevelMenu = false }) {
            levelOptions.forEach {
                DropdownMenuItem(text = { Text(it) }, onClick = {
                    selectedLevel = it
                    showLevelMenu = false
                    viewModel.searchVideos(it)
                })
            }
        }

        DropdownMenu(expanded = showObjectiveMenu, onDismissRequest = { showObjectiveMenu = false }) {
            objectiveOptions.forEach {
                DropdownMenuItem(text = { Text(it) }, onClick = {
                    selectedObjective = it
                    showObjectiveMenu = false
                    viewModel.searchVideos(it)
                })
            }
        }

        DropdownMenu(expanded = showEquipmentMenu, onDismissRequest = { showEquipmentMenu = false }) {
            equipmentOptions.forEach {
                DropdownMenuItem(text = { Text(it) }, onClick = {
                    selectedEquipment = it
                    showEquipmentMenu = false
                    viewModel.searchVideos(it)
                })
            }
        }

        if (showFilterDialog) {
            AlertDialog(
                onDismissRequest = { showFilterDialog = false },
                title = { Text("More Filters") },
                text = {
                    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                        Text("Duration")
                        durationOptions.forEach { option ->
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clickable {
                                        selectedDuration = option
                                    }
                                    .padding(4.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                RadioButton(
                                    selected = selectedDuration == option,
                                    onClick = { selectedDuration = option }
                                )
                                Text(option)
                            }
                        }
                    }
                },
                confirmButton = {
                    TextButton(onClick = {
                        showFilterDialog = false
                        val combinedQuery = listOfNotNull(
                            selectedLevel,
                            selectedObjective,
                            selectedEquipment,
                            selectedDuration
                        ).joinToString(" ")
                        viewModel.searchVideos(combinedQuery)
                    }) {
                        Text("Apply")
                    }
                },
                dismissButton = {
                    TextButton(onClick = { showFilterDialog = false }) {
                        Text("Cancel")
                    }
                }
            )
        }

        Box(
            modifier = Modifier
                .height(290.dp)
                .padding(horizontal = 16.dp, vertical = 8.dp)
        ) {
            if (showAll) {
                LazyColumn(
                    state = listState,
                    modifier = Modifier.fillMaxSize()
                ) {
                    items(videos) { video ->
                        VideoCard(video)
                    }
                }
            } else {
                Column(modifier = Modifier.fillMaxSize()) {
                    videos.take(3).forEach { video ->
                        VideoCard(video)
                    }
                }
            }
        }

        if (!showAll && videos.size > 3) {
            Button(
                onClick = { showAll = true },
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFFBF2ED)),
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 4.dp)
            ) {
                Text("Show all programs", color = Color.Black)
            }
        }

        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Text("Routines", fontSize = 18.sp, modifier = Modifier.padding(bottom = 8.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                CategoryButton("At home", "🏠", selectedCategory == "At home", {
                    selectedCategory = "At home"
                    viewModel.searchVideos("home workout")
                }, Modifier.weight(1f))

                CategoryButton("Journey", "💼", selectedCategory == "Journey", {
                    selectedCategory = "Journey"
                    viewModel.searchVideos("travel workout")
                }, Modifier.weight(1f))
            }

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                CategoryButton("Dumbbells\nOnly", "🏋️", selectedCategory == "Dumbbells\nOnly", {
                    selectedCategory = "Dumbbells\nOnly"
                    viewModel.searchVideos("dumbbell workout")
                }, Modifier.weight(1f))

                CategoryButton("Gym", "💪", selectedCategory == "Gym", {
                    selectedCategory = "Gym"
                    viewModel.searchVideos("gym workout")
                }, Modifier.weight(1f))
            }

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                CategoryButton("Cardio and\nFitness", "❤️", selectedCategory == "Cardio and\nFitness", {
                    selectedCategory = "Cardio and\nFitness"
                    viewModel.searchVideos("cardio fitness")
                }, Modifier.weight(1f))

                CategoryButton("Stretching", "🧘‍♀️", selectedCategory == "Stretching", {
                    selectedCategory = "Stretching"
                    viewModel.searchVideos("stretching workout")
                }, Modifier.weight(1f))
            }
        }
    }
}

@Composable
fun FilterButtonWithIcon(label: String, selected: String?, onClick: () -> Unit, icon: androidx.compose.ui.graphics.vector.ImageVector) {
    Row(
        modifier = Modifier
            .width(113.dp)
            .height(34.dp)
            .clip(RoundedCornerShape(10.dp))
            .background(Color(0xFFFBF2ED))
            .clickable { onClick() }
            .padding(horizontal = 8.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(text = selected ?: label, fontSize = 14.sp)
        Icon(icon, contentDescription = null, modifier = Modifier.size(16.dp))
    }
}

@Composable
fun VideoCard(video: YouTubeVideo) {
    Card(
        shape = RoundedCornerShape(8.dp),
        colors = CardDefaults.cardColors(containerColor = Color(0xFFFFD8C2)),
        modifier = Modifier
            .height(90.dp)
            .fillMaxWidth()
            .padding(vertical = 4.dp)
    ) {
        Row(modifier = Modifier.padding(8.dp)) {
            AsyncImage(
                model = video.snippet.thumbnails.medium.url,
                contentDescription = null,
                modifier = Modifier
                    .size(100.dp)
                    .clip(RoundedCornerShape(8.dp))
            )
            Spacer(modifier = Modifier.width(12.dp))
            Column(modifier = Modifier.align(Alignment.CenterVertically)) {
                Text(video.snippet.title, maxLines = 2)
            }
        }
    }
}
