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
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.KeyboardArrowUp
import androidx.compose.material.icons.filled.Remove
import androidx.compose.ui.Alignment
import androidx.compose.ui.res.painterResource

import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.ui.platform.LocalContext
import androidx.compose.foundation.Image
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.ui.unit.dp
import androidx.compose.ui.layout.ContentScale
import coil.compose.rememberAsyncImagePainter


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
                    fontSize = 20.sp,
                    modifier = Modifier.align(Alignment.Center),
                    style = MaterialTheme.typography.bodyMedium
                )
            }

            Spacer(modifier = Modifier.height(8.dp))

            Text("Tell us about yourself", fontSize = 24.sp, fontWeight = FontWeight.Medium,  modifier = Modifier.padding(bottom = 24.dp))

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
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun QuestionThreeScreen(
    navController: NavController,
    onNextClick: (String, Triple<Int, String, Int>) -> Unit
) {
    var selectedGender by remember { mutableStateOf<String?>(null) }
    var showGenderPicker by remember { mutableStateOf(false) }
    var showDatePicker by remember { mutableStateOf(false) }
    var selectedDate by remember { mutableStateOf(Triple(1, "January", 2000)) }

    val months = listOf(
        "January", "February", "March", "April", "May", "June",
        "July", "August", "September", "October", "November", "December"
    )

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 24.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // Header
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            IconButton(onClick = {
                navController.navigate("onboarding") {
                    popUpTo("question3") { inclusive = true }
                }
            }) {
                Icon(Icons.Default.Close, contentDescription = "Close", tint = Color.Black)
            }
            Spacer(modifier = Modifier.weight(1f))
            Text(
                "Let's get to know you",
                fontSize = 20.sp,
                modifier = Modifier.align(Alignment.CenterVertically),
                style = MaterialTheme.typography.bodyMedium
            )
            Spacer(modifier = Modifier.weight(1f))
        }

        Spacer(modifier = Modifier.height(16.dp))

        Text(
            "Give your coach some final details",
            fontSize = 24.sp,
            fontWeight = FontWeight.Medium,
            textAlign = TextAlign.Center,
            modifier = Modifier
                .padding(bottom = 24.dp)
                .align(Alignment.CenterHorizontally)
        )
        Text(
            "This information helps your coach design a training journey",
            color = Color.Gray,
            fontSize = 14.sp,
            textAlign = TextAlign.Center
        )

        Spacer(modifier = Modifier.height(32.dp))

        // Gender picker trigger
        Box(
            modifier = Modifier
                .width(338.dp)
                .height(66.dp)
                .clip(RoundedCornerShape(20.dp))
                .background(Color(0xFFFBF2ED))
                .clickable { showGenderPicker = true }
                .padding(horizontal = 24.dp),
            contentAlignment = Alignment.CenterStart
        ) {
            Text(text = selectedGender ?: "Select gender", fontSize = 16.sp)
        }

        Spacer(modifier = Modifier.height(12.dp))

        // Date of birth
        Box(
            modifier = Modifier
                .width(338.dp)
                .height(66.dp)
                .clip(RoundedCornerShape(20.dp))
                .border(1.dp, Color(0xFFE86144), RoundedCornerShape(20.dp))
                .clickable { showDatePicker = true }
                .padding(horizontal = 24.dp),
            contentAlignment = Alignment.CenterStart
        ) {
            Text("${selectedDate.first} ${selectedDate.second} ${selectedDate.third}")
        }

        Spacer(modifier = Modifier.weight(1f))

        Button(
            onClick = {
                selectedGender?.let {
                    onNextClick(it, selectedDate)
                }
            },
            enabled = selectedGender != null,
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

    // Gender bottom sheet
    if (showGenderPicker) {
        ModalBottomSheet(
            onDismissRequest = { showGenderPicker = false }
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 24.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text("Select Gender", fontSize = 20.sp, fontWeight = FontWeight.Bold)

                Spacer(modifier = Modifier.height(16.dp))

                listOf("Male", "Female", "Other").forEach { gender ->
                    Text(
                        text = gender,
                        fontSize = 22.sp,
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable {
                                selectedGender = gender
                                showGenderPicker = false
                            }
                            .padding(vertical = 16.dp, horizontal = 24.dp),
                        textAlign = TextAlign.Start
                    )
                }
            }
        }
    }

    // Date picker bottom sheet
    if (showDatePicker) {
        ModalBottomSheet(onDismissRequest = { showDatePicker = false }) {
            val days = 1..31
            val years = 1970..2025
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 16.dp),
                horizontalArrangement = Arrangement.SpaceEvenly,
                verticalAlignment = Alignment.CenterVertically
            ) {
                LazyColumn(modifier = Modifier.weight(1f), horizontalAlignment = Alignment.CenterHorizontally) {
                    items(days.toList()) {
                        Text(
                            text = it.toString(),
                            fontSize = 22.sp,
                            modifier = Modifier
                                .padding(8.dp)
                                .clickable {
                                    selectedDate = selectedDate.copy(first = it)
                                    showDatePicker = false
                                },
                            color = if (it == selectedDate.first) Color.Black else Color.Gray
                        )
                    }
                }

                LazyColumn(modifier = Modifier.weight(1f), horizontalAlignment = Alignment.CenterHorizontally) {
                    items(months) {
                        Text(
                            text = it,
                            fontSize = 22.sp,
                            modifier = Modifier
                                .padding(8.dp)
                                .clickable {
                                    selectedDate = selectedDate.copy(second = it)
                                    showDatePicker = false
                                },
                            color = if (it == selectedDate.second) Color.Black else Color.Gray
                        )
                    }
                }

                LazyColumn(modifier = Modifier.weight(1f), horizontalAlignment = Alignment.CenterHorizontally) {
                    items(years.toList()) {
                        Text(
                            text = it.toString(),
                            fontSize = 22.sp,
                            modifier = Modifier
                                .padding(8.dp)
                                .clickable {
                                    selectedDate = selectedDate.copy(third = it)
                                    showDatePicker = false
                                },
                            color = if (it == selectedDate.third) Color.Black else Color.Gray
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun QuestionFourScreen(
    navController: NavController,
    onNextClick: (String) -> Unit // on passe la réponse sélectionnée
) {
    val options = listOf(
        "At home", "In the gym", "Outdoors", "Never mind"
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
                            popUpTo("question4") { inclusive = true }
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
                text = "Where do you prefer to train?",
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

@Composable
fun QuestionFiveScreen(
    navController: NavController,
    onNextClick: (Int) -> Unit
) {
    val options = listOf(
        Triple("8 000 Steps", "Mov. 7 Days", Color.Gray),
        Triple("8 500 Steps", "Realistic", Color(0xFF6FCF97)),
        Triple("10 000 Steps", "Stimulating", Color(0xFFF2994A)),
        Triple("13 000 Steps", "Ambitious", Color(0xFFEB5757))
    )

    var selectedOption by remember { mutableStateOf<String?>(null) }
    var isCustomExpanded by remember { mutableStateOf(false) }
    var customGoal by remember { mutableStateOf(5500) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 24.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // 🔹 Header
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 16.dp)
        ) {
            IconButton(
                onClick = {
                    navController.navigate("home") {
                        popUpTo("question5") { inclusive = true }
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

        // 🔹 Question
        Text(
            text = "What is your daily step goal?",
            fontSize = 24.sp,
            fontWeight = FontWeight.Medium,
            textAlign = TextAlign.Center,
            modifier = Modifier
                .padding(bottom = 24.dp)
                .align(Alignment.CenterHorizontally)
        )

        // 🔹 Standard Options
        options.forEach { (label, tag, color) ->
            val isSelected = selectedOption == label
            Row(
                modifier = Modifier
                    .width(338.dp)
                    .height(66.dp)
                    .padding(vertical = 6.dp)
                    .clip(RoundedCornerShape(20.dp))
                    .background(if (isSelected) Color(0xFFFFA491) else Color(0xFFFBF2ED))
                    .clickable {
                        selectedOption = label
                        isCustomExpanded = false
                    }
                    .padding(horizontal = 16.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(text = label, color = Color.Black, fontSize = 16.sp)
                Text(text = tag, color = color, fontSize = 14.sp)
            }
        }

        // 🔹 Custom Goal Cell
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 6.dp)
                .clip(RoundedCornerShape(20.dp))
                .background(Color(0xFFFBF2ED))
                .clickable {
                    selectedOption = "custom"
                    isCustomExpanded = !isCustomExpanded
                }
                .padding(horizontal = 16.dp, vertical = 12.dp)
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween,
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("Custom goal", fontSize = 16.sp)
                Icon(
                    imageVector = if (isCustomExpanded) Icons.Default.KeyboardArrowUp else Icons.Default.KeyboardArrowDown,
                    contentDescription = null
                )
            }

            if (isCustomExpanded) {
                Spacer(modifier = Modifier.height(12.dp))

                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(Color(0xFFFFA491), RoundedCornerShape(16.dp))
                        .padding(vertical = 16.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        IconButton(onClick = {
                            if (customGoal > 1000) customGoal -= 500
                        }) {
                            Icon(Icons.Default.Remove, contentDescription = "Minus")
                        }

                        Spacer(modifier = Modifier.width(12.dp))

                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Text(
                                text = "%,d".format(customGoal),
                                fontSize = 20.sp,
                                fontWeight = FontWeight.Medium
                            )
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(
                                    painter = painterResource(id = R.drawable.steps),
                                    contentDescription = "Steps icon",
                                    modifier = Modifier.size(20.dp)
                                )
                                Spacer(modifier = Modifier.width(4.dp))
                                Text("steps", fontSize = 14.sp)
                            }
                        }

                        Spacer(modifier = Modifier.width(12.dp))

                        IconButton(onClick = {
                            if (customGoal < 30000) customGoal += 500
                        }) {
                            Icon(Icons.Default.Add, contentDescription = "Plus")
                        }
                    }
                }
            }
        }

        Spacer(modifier = Modifier.weight(1f))

        // 🔹 Button Next
        Button(
            onClick = {
                val value = if (selectedOption == "custom") customGoal else selectedOption?.filter { it.isDigit() }?.replace(" ", "")?.toIntOrNull() ?: 0
                onNextClick(value)
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

@Composable
fun CreateProfileScreen(
    navController: NavController,
    onProfileCreated: (Uri?, String, String) -> Unit // Pour transmettre les infos
) {
    val context = LocalContext.current
    var avatarUri by remember { mutableStateOf<Uri?>(null) }
    var firstName by remember { mutableStateOf("") }
    var lastName by remember { mutableStateOf("") }

    val launcher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri: Uri? ->
        avatarUri = uri
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 24.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Spacer(modifier = Modifier.height(24.dp))

        // ❌ Titre
        Text("Create a profile", style = MaterialTheme.typography.headlineMedium)

        Spacer(modifier = Modifier.height(24.dp))

        // ✅ Avatar
        Box(
            modifier = Modifier
                .size(130.dp)
                .clip(CircleShape)
                .border(2.dp, Color.Black, CircleShape),
            contentAlignment = Alignment.Center
        ) {
            if (avatarUri != null) {
                Image(
                    painter = rememberAsyncImagePainter(avatarUri),
                    contentDescription = "Avatar",
                    modifier = Modifier.fillMaxSize(),
                    contentScale = ContentScale.Crop
                )
            } else {
                Image(
                    painter = painterResource(id = R.drawable.default_avatar),
                    contentDescription = "Default Avatar",
                    modifier = Modifier.fillMaxSize(),
                    contentScale = ContentScale.Crop
                )
            }
        }

        Spacer(modifier = Modifier.height(8.dp))

        // ✅ Bouton "Change avatar"
        Text(
            text = "Change avatar",
            color = Color(0xFFE86144),
            modifier = Modifier.clickable {
                launcher.launch("image/*")
            }
        )

        Spacer(modifier = Modifier.height(24.dp))

        // ✅ Inputs
        Column(
            modifier = Modifier
                .width(338.dp)
                .background(Color(0xFFFBF2ED), shape = RoundedCornerShape(12.dp))
                .padding(16.dp)
        ) {
            OutlinedTextField(
                value = firstName,
                onValueChange = { firstName = it },
                placeholder = { Text("First Name", color = Color.Gray) },
                colors = OutlinedTextFieldDefaults.colors(
                    unfocusedBorderColor = Color.Transparent,
                    focusedBorderColor = Color.Transparent
                ),
                modifier = Modifier.fillMaxWidth()
            )
            OutlinedTextField(
                value = lastName,
                onValueChange = { lastName = it },
                placeholder = { Text("Last name", color = Color.Gray) },
                colors = OutlinedTextFieldDefaults.colors(
                    unfocusedBorderColor = Color.Transparent,
                    focusedBorderColor = Color.Transparent
                ),
                modifier = Modifier.fillMaxWidth()
            )
        }

        Spacer(modifier = Modifier.weight(1f))

        // ✅ Bouton Next
        Button(
            onClick = {
                onProfileCreated(avatarUri, firstName, lastName)
                navController.navigate("home")
            },
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


