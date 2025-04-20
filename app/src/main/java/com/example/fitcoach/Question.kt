package com.example.fitcoach

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.*
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.*
import androidx.navigation.NavController
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.foundation.border
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items




@Composable
fun QuestionOneScreen(
    navController: NavController,
    onNextClick: (String) -> Unit // on passe la réponse sélectionnée
) {
    val options = listOf(
        "Muscle gain", "Endurance", "Weight loss", "Well-being", "Other"
    )
    var selectedOption by remember { mutableStateOf<String?>(null) }

    Box(modifier = Modifier.fillMaxSize()) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 24.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // ✅ Header
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 16.dp)
            ) {
                IconButton(
                    onClick = {
                        navController.navigate("home") {
                            popUpTo("question1") { inclusive = true }
                        }
                    },
                    modifier = Modifier.align(Alignment.CenterStart)
                ) {
                    Icon(Icons.Default.Close, contentDescription = "Close", tint = Color.Gray)
                }

                Text(
                    "Let's get to know you",
                    fontSize = 20.sp,
                    modifier = Modifier.align(Alignment.Center),
                    style = MaterialTheme.typography.bodyMedium
                )
            }

            Spacer(modifier = Modifier.height(12.dp))

            // ✅ Question
            Text(
                text = "What are your main goals?",
                fontSize = 24.sp,
                fontWeight = FontWeight.Medium,
                modifier = Modifier.padding(bottom = 24.dp)
            )

            // ✅ Options
            options.forEach { option ->
                val isSelected = selectedOption == option
                Row(
                    modifier = Modifier
                        .width(338.dp)
                        .height(68.dp)
                        .padding(vertical = 6.dp)
                        .clip(RoundedCornerShape(20.dp))
                        .background(
                            if (isSelected) Color(0xFFFFA491) else Color(0xFFFBF2ED)
                        )
                        .clickable { selectedOption = option }
                        .padding(horizontal = 16.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text(
                        text = option,
                        color = Color.Black,
                        fontSize = 16.sp
                    )
                    if (isSelected) {
                        Icon(
                            imageVector = Icons.Default.CheckCircle,
                            contentDescription = "Selected",
                            tint = Color(0xFFE86144)
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.weight(1f))

            // ✅ Button Next
            Button(
                onClick = {
                    selectedOption?.let { onNextClick(it) }
                },
                enabled = selectedOption != null,
                modifier = Modifier
                    .width(338.dp)
                    .height(66.dp)
                    .padding(bottom = 16.dp),
                shape = RoundedCornerShape(20.dp),
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFE86144))
            ) {
                Text("Next", color = Color.White, fontSize = 16.sp)
            }
        }
    }
}
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun QuestionTwoScreen(
    navController: NavController,
    onNextClick: (height: Int, weight: Int, unit: String) -> Unit
) {
    var height by remember { mutableStateOf(183) }
    var weight by remember { mutableStateOf(75) }
    var unit by remember { mutableStateOf("kg") }

    var showHeightPicker by remember { mutableStateOf(false) }
    var showWeightPicker by remember { mutableStateOf(false) }

    Box(modifier = Modifier.fillMaxSize()) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 24.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // ✅ Header
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 16.dp)
            ) {
                IconButton(
                    onClick = { navController.popBackStack() },
                    modifier = Modifier.align(Alignment.CenterStart)
                ) {
                    Icon(Icons.Default.Close, contentDescription = "Close", tint = Color.Gray)
                }

                Text(
                    "Let's get to know you",
                    modifier = Modifier.align(Alignment.Center),
                    style = MaterialTheme.typography.bodyMedium
                )
            }

            Spacer(modifier = Modifier.height(8.dp))

            Text("Tell us about yourself", fontSize = 24.sp, fontWeight = FontWeight.Medium)
            Text(
                "We’d like the following information to provide\nmore accurate results",
                textAlign = TextAlign.Center,
                color = Color.Gray,
                modifier = Modifier.padding(vertical = 12.dp)
            )

            Spacer(modifier = Modifier.height(16.dp))

            // ✅ Height cell
            Box(
                modifier = Modifier
                    .width(338.dp)
                    .height(66.dp)
                    .clip(RoundedCornerShape(20.dp))
                    .background(Color(0xFFFBF2ED))
                    .clickable { showHeightPicker = true },
                contentAlignment = Alignment.CenterStart
            ) {
                Text(
                    "${height} cm",
                    modifier = Modifier.padding(start = 20.dp),
                    fontSize = 18.sp
                )
            }

            Spacer(modifier = Modifier.height(12.dp))

            // ✅ Weight cell
            Box(
                modifier = Modifier
                    .width(338.dp)
                    .height(66.dp)
                    .clip(RoundedCornerShape(20.dp))
                    .border(
                        width = 1.dp,
                        color = Color(0xFFE86144),
                        shape = RoundedCornerShape(20.dp)
                    )
                    .clickable { showWeightPicker = true },
                contentAlignment = Alignment.CenterStart
            ) {
                Text(
                    "$weight $unit",
                    modifier = Modifier.padding(start = 20.dp),
                    fontSize = 18.sp
                )
            }

            Spacer(modifier = Modifier.weight(1f))

            // ✅ Button Next
            Button(
                onClick = { onNextClick(height, weight, unit) },
                modifier = Modifier
                    .width(338.dp)
                    .height(66.dp)
                    .padding(bottom = 16.dp),
                shape = RoundedCornerShape(20.dp),
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFE86144))
            ) {
                Text("Next", color = Color.White)
            }
        }

        // ✅ Height Picker Bottom Sheet
        if (showHeightPicker) {
            ModalBottomSheet(
                onDismissRequest = { showHeightPicker = false },
                containerColor = Color.White // optionnel, pour forcer fond blanc
            ) {
                PickerSheet(
                    title = "Select Height",
                    valueRange = 140..220,
                    unit = "cm",
                    current = height,
                    onNext = {
                        height = it
                        showHeightPicker = false
                    }
                )
            }
        }


        // ✅ Weight Picker Bottom Sheet
        if (showWeightPicker) {
            ModalBottomSheet(
                onDismissRequest = { showWeightPicker = false }
            ) {
                PickerSheet(
                    title = "Select Weight",
                    valueRange = 40..200,
                    unit = unit,
                    current = weight,
                    showUnitToggle = true,
                    currentUnit = unit,
                    onUnitChange = { unit = it },
                    onNext = {
                        weight = it
                        showWeightPicker = false
                    }
                )
            }
        }

    }
}

@Composable
fun PickerSheet(
    title: String,
    valueRange: IntRange,
    unit: String,
    current: Int,
    showUnitToggle: Boolean = false,
    currentUnit: String = "kg",
    onUnitChange: (String) -> Unit = {},
    onNext: (Int) -> Unit
) {
    var selected by remember { mutableStateOf(current) }

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .background(Color.White)
            .padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Row(
            Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            IconButton(onClick = { onNext(current) }) {
                Icon(Icons.Default.Close, contentDescription = "Close", tint = Color.Gray)
            }
            Text(title, fontSize = 18.sp, fontWeight = FontWeight.Medium)
            TextButton(onClick = { onNext(selected) }) {
                Text("Next", color = Color(0xFFE86144))
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // ✅ Valeurs
        LazyColumn(horizontalAlignment = Alignment.CenterHorizontally) {
            items(valueRange.toList()) {
                Text(
                    "$it $unit",
                    fontSize = if (it == selected) 22.sp else 16.sp,
                    color = if (it == selected) Color.Black else Color.Gray,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 8.dp)
                        .clickable { selected = it }
                        .align(Alignment.CenterHorizontally)
                )
            }
        }

        // ✅ Switch kg/lb
        if (showUnitToggle) {
            Spacer(modifier = Modifier.height(8.dp))
            Row(horizontalArrangement = Arrangement.Center) {
                listOf("kg", "lb").forEach {
                    Text(
                        text = it,
                        modifier = Modifier
                            .padding(horizontal = 12.dp)
                            .clickable { onUnitChange(it) },
                        color = if (it == currentUnit) Color.Black else Color.Gray
                    )
                }
            }
        }
    }
}


