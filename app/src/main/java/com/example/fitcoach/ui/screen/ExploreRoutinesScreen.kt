package com.example.fitcoach.ui.screen

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.RoundedCornerShape
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
import com.example.fitcoach.data.model.YouTubeVideo

@Composable
fun ExploreRoutinesScreen(
    viewModel: ExploreViewModel = viewModel(),
    onBack: () -> Unit
) {
    val videos by viewModel.videos.collectAsState()
    val levelOptions = listOf("Beginner", "Intermediate", "Advanced")
    val objectiveOptions = listOf("Fat Loss", "Muscle Gain", "Endurance")
    val equipmentOptions = listOf("Dumbbells", "Bodyweight", "Full Gym")

    var selectedLevel by remember { mutableStateOf<String?>(null) }
    var selectedObjective by remember { mutableStateOf<String?>(null) }
    var selectedEquipment by remember { mutableStateOf<String?>(null) }

    var showLevelMenu by remember { mutableStateOf(false) }
    var showObjectiveMenu by remember { mutableStateOf(false) }
    var showEquipmentMenu by remember { mutableStateOf(false) }

    var showAll by remember { mutableStateOf(false) }

    val listState = rememberLazyListState()

    Column(modifier = Modifier.fillMaxSize()) {
        // Top Bar
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

        // Filters title
        Text("Programs", fontSize = 18.sp, modifier = Modifier.padding(start = 16.dp, bottom = 8.dp))

        // Filter Buttons
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            FilterButtonWithIcon("Filters", null, { /* TODO */ }, Icons.Default.FilterList)
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

        // Scrollable Videos
        Box(modifier = Modifier.weight(1f)) {
            LazyColumn(
                state = listState,
                modifier = Modifier
                    .fillMaxSize()
                    .padding(16.dp)
            ) {
                val displayedVideos = if (showAll) videos else videos.take(3)
                items(displayedVideos) { video ->
                    VideoCard(video)
                }
            }
        }

        // Show all programs button
        Button(
            onClick = { showAll = true },
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 8.dp)
        ) {
            Text("Show all programs")
        }

        // Routine buttons (Home, Gym, Outside, etc.)
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            listOf("Home", "Gym", "Outside").forEach {
                FilterButton(it)
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
fun FilterButton(label: String) {
    Box(
        modifier = Modifier
            .clip(RoundedCornerShape(10.dp))
            .background(Color(0xFFFBF2ED))
            .padding(horizontal = 12.dp, vertical = 8.dp),
        contentAlignment = Alignment.Center
    ) {
        Text(label)
    }
}

@Composable
fun VideoCard(video: YouTubeVideo) {
    Card(
        shape = RoundedCornerShape(12.dp),
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp)
            .background(Color(0xFFFFAA7C))
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
                Text("3 routines", fontSize = 12.sp, color = Color.Gray)
            }
        }
    }
}
