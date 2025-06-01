package com.example.fitcoach.ui.screen.section_profile

import android.net.Uri
import android.util.Log
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LocalTextStyle
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import coil.compose.rememberAsyncImagePainter
import com.example.fitcoach.viewmodel.UserOnboardingViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EditProfileScreen(
    navController: NavController,
) {
    val viewModel: UserOnboardingViewModel = viewModel()

    val context = LocalContext.current
    val userData = viewModel.answers
    Log.d("UserData", userData.toString())

    var avatarUri by remember { mutableStateOf<Uri?>(null) }
    var firstName by remember { mutableStateOf("") }
    var lastName by remember { mutableStateOf("") }
    var bio by remember { mutableStateOf("") }
    var gender by remember { mutableStateOf("") }
    var birthDate by remember { mutableStateOf("") }
    var selectedDate by remember { mutableStateOf(Triple(1, "January", 2000)) }
    var selectedGender by remember { mutableStateOf<String?>(null) }


    var showGenderPicker by remember { mutableStateOf(false) }
    var showDatePicker by remember { mutableStateOf(false) }

    val months = listOf(
        "January", "February", "March", "April", "May", "June",
        "July", "August", "September", "October", "November", "December"
    )

    val launcher = rememberLauncherForActivityResult(ActivityResultContracts.GetContent()) {
        avatarUri = it
    }

    LaunchedEffect(Unit) {
        viewModel.loadFromFirestore(
            onSuccess = {
                firstName = viewModel.answers.firstName
                lastName = viewModel.answers.lastName
                bio = viewModel.answers.bio ?: ""
                gender = viewModel.answers.gender
                birthDate = viewModel.answers.birthDate
            }
        )
    }


    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFFFEF3ED))
            .padding(28.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // Header
        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
            IconButton(onClick = { navController.popBackStack() }) {
                Icon(Icons.Default.ArrowBack, contentDescription = null)
            }
            Text("Personal Information", style = MaterialTheme.typography.titleMedium)
            TextButton(onClick = {
                val formattedDate = "${selectedDate.first} ${selectedDate.second} ${selectedDate.third}"
                viewModel.answers.firstName = firstName
                viewModel.answers.lastName = lastName
                viewModel.answers.bio = bio
                viewModel.answers.gender = selectedGender ?: gender
                viewModel.answers.birthDate = formattedDate
                viewModel.answers.avatarUrl = avatarUri?.toString() ?: userData.avatarUrl

                viewModel.updateFirestoreProfile(
                    onSuccess = {
                        Toast.makeText(context, "Profil mis à jour !", Toast.LENGTH_SHORT).show()
                        navController.popBackStack()
                    },
                    onFailure = {
                        Toast.makeText(context, "Erreur: ${it.message}", Toast.LENGTH_LONG).show()
                    }
                )
            }) {
                Text("Save", color = Color(0xFFE86144))
            }
        }

        Spacer(modifier = Modifier.height(24.dp))

        Box(
            modifier = Modifier
                .size(130.dp)
                .clip(CircleShape)
                .border(2.dp, Color.Black, CircleShape)
        ) {
            if (avatarUri != null) {
                Image(painter = rememberAsyncImagePainter(avatarUri), contentDescription = null, modifier = Modifier.fillMaxSize(), contentScale = ContentScale.Crop)
            } else {
                Image(painter = rememberAsyncImagePainter(userData.avatarUrl), contentDescription = null, modifier = Modifier.fillMaxSize(), contentScale = ContentScale.Crop)
            }
        }
        Spacer(modifier = Modifier.height(16.dp))
        Text("Edit Photo", color = Color(0xFFE86144), modifier = Modifier.clickable {
            launcher.launch("image/*")
        })

        Spacer(modifier = Modifier.height(24.dp))

        Text("Public profile data", color = Color.Gray ,
            modifier = Modifier.
                align(Alignment.Start)
        )
        Spacer(modifier = Modifier.height(8.dp))

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 8.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "First Name",
                modifier = Modifier.weight(1f),
                color = Color.Black
            )

            Box(
                modifier = Modifier
                    .weight(2f)
            ) {
                if (firstName.isEmpty()) {
                    Text(
                        text = firstName,
                        color = Color.Gray.copy(alpha = 0.5f)
                    )
                }

                BasicTextField(
                    value = firstName,
                    onValueChange = { firstName = it },
                    singleLine = true,
                    textStyle = LocalTextStyle.current.copy(color = Color.Black),
                    modifier = Modifier.fillMaxWidth()

                )
            }
        }

        Spacer(modifier = Modifier.height(8.dp))

        HorizontalDivider(color = Color.Gray)

        Spacer(modifier = Modifier.height(8.dp))

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 8.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "Last Name",
                modifier = Modifier.weight(1f),
                color = Color.Black
            )

            Box(
                modifier = Modifier
                    .weight(2f)
            ) {
                if (lastName.isEmpty()) {
                    Text(
                        text = lastName,
                        color = Color.Gray.copy(alpha = 0.5f)
                    )
                }
                BasicTextField(
                    value = lastName,
                    onValueChange = { lastName = it },
                    singleLine = true,
                    textStyle = LocalTextStyle.current.copy(color = Color.Black),
                    modifier = Modifier.fillMaxWidth()
                )
            }

        }

        Spacer(modifier = Modifier.height(8.dp))

        HorizontalDivider(color = Color.Gray)


        Spacer(modifier = Modifier.height(8.dp))

        val bioMaxLength = 150

        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 8.dp)
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 4.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.Top
            ) {
                Text(
                    text = "Bio",
                    modifier = Modifier.weight(1f),
                    color = Color.Black
                )

                Box(modifier = Modifier.weight(2f)) {
                    if (bio.isEmpty()) {
                        Text(
                            text = "Describe yourself",
                            color = Color.Gray.copy(alpha = 0.5f)
                        )
                    } else {
                        Text(
                            text = bio,
                            color = Color(0xFFE86144),
                            modifier = Modifier.clickable { }
                        )
                    }
                    BasicTextField(
                        value = bio,
                        onValueChange = {
                            if (it.length <= bioMaxLength) {
                                bio = it
                            }
                        },
                        textStyle = LocalTextStyle.current.copy(color = Color.Black),
                        modifier = Modifier
                            .fillMaxWidth()
                            .heightIn(min = 80.dp)
                    )
                }
            }

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(end = 8.dp),
                horizontalArrangement = Arrangement.End
            ) {
                Text(
                    text = "${bio.length} / $bioMaxLength",
                    style = MaterialTheme.typography.bodySmall,
                    color = Color.Gray
                )
            }

            HorizontalDivider(color = Color.Gray)

        }




        Spacer(modifier = Modifier.height(24.dp))
        Text("Private data", color = Color.Gray,
            modifier = Modifier
                .align(Alignment.Start)
        )
        Spacer(modifier = Modifier.height(10.dp))

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 8.dp)
                .clickable { showGenderPicker = true },
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text("Gender")
            Text(text = selectedGender ?: gender, color = Color(0xFFE86144))

        }
        Spacer(modifier = Modifier.height(8.dp))

        HorizontalDivider(color = Color.Gray)
        Spacer(modifier = Modifier.height(8.dp))

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 8.dp)
                .clickable { showDatePicker = true },
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text("Birthday")
            Text(
                text = if (birthDate.isNotEmpty()) birthDate else "${selectedDate.first} ${selectedDate.second} ${selectedDate.third}",
                color = Color(0xFFE86144)
            )

        }
        Spacer(modifier = Modifier.height(8.dp))

        HorizontalDivider(color = Color.Gray)
    }

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

    if (showDatePicker) {
        ModalBottomSheet(onDismissRequest = { showDatePicker = false }) {
            val days = 1..31
            val years = 1970..2025

            Column (
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp)
            )  {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 16.dp),
                    horizontalArrangement = Arrangement.SpaceEvenly,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    LazyColumn(
                        modifier = Modifier
                            .weight(1f)
                            .height(200.dp)
                        ,
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        items(days.toList()) {
                            Text(
                                text = it.toString(),
                                fontSize = 22.sp,
                                modifier = Modifier
                                    .padding(8.dp)
                                    .clickable {
                                        selectedDate = selectedDate.copy(first = it)
                                    },
                                color = if (it == selectedDate.first) Color.Black else Color.Gray
                            )
                        }
                    }

                    LazyColumn(
                        modifier = Modifier
                            .weight(1f)
                            .height(200.dp)
                        ,
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        items(months) {
                            Text(
                                text = it,
                                fontSize = 22.sp,
                                modifier = Modifier
                                    .padding(8.dp)
                                    .clickable {
                                        selectedDate = selectedDate.copy(second = it)
                                    },
                                color = if (it == selectedDate.second) Color.Black else Color.Gray
                            )
                        }
                    }

                    LazyColumn(
                        modifier = Modifier
                            .weight(1f)
                            .height(200.dp)
                        ,
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        items(years.toList()) {
                            Text(
                                text = it.toString(),
                                fontSize = 22.sp,
                                modifier = Modifier
                                    .padding(8.dp)
                                    .clickable {
                                        selectedDate = selectedDate.copy(third = it)
                                    },
                                color = if (it == selectedDate.third) Color.Black else Color.Gray
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceEvenly
                ) {
                    TextButton(
                        onClick = { showDatePicker = false }
                    ) {
                        Text("Annuler", color = Color.Gray)
                    }

                    Button(
                        onClick = {
                            birthDate = "${selectedDate.first} ${selectedDate.second} ${selectedDate.third}"
                            showDatePicker = false
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFE86144))
                    ) {
                        Text("OK", color = Color.White)
                    }
                }
            }
        }
    }

}

@Preview(showBackground = true)
@Composable
fun EditProfileScreenPreviw(){
    EditProfileScreen(navController = NavController(LocalContext.current))
}
