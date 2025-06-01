package com.example.fitcoach.ui.screen.section_tracking

import android.app.Application
import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.DirectionsRun
import androidx.compose.material.icons.filled.DirectionsRun
import androidx.compose.material.icons.filled.MusicNote
import androidx.compose.material.icons.filled.Pause
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.runtime.snapshotFlow
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import androidx.room.util.TableInfo
import com.example.fitcoach.viewmodel.UserProfileViewModel
import com.example.fitcoach.viewmodel.track_section.StepCounterViewModel
import com.example.fitcoach.viewmodel.track_section.TrackingViewModel
import com.google.android.gms.location.LocationServices
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.maps.android.compose.GoogleMap
import com.google.maps.android.compose.Marker
import com.google.maps.android.compose.MarkerState
import com.google.maps.android.compose.rememberCameraPositionState
import com.google.android.gms.maps.model.CameraPosition
import com.google.android.gms.maps.model.LatLng
import com.google.maps.android.compose.MapProperties
import com.google.maps.android.compose.MapUiSettings
import com.google.maps.android.compose.Polyline
import kotlinx.coroutines.tasks.await

@RequiresApi(Build.VERSION_CODES.Q)
@Composable
fun TrackScreen(
    navController: NavController,
    viewModel: TrackingViewModel = viewModel(),
    stepViewModel: StepCounterViewModel = viewModel(),
    profileViewModel: UserProfileViewModel = viewModel()
) {
    var showDialog by remember { mutableStateOf(false) }
    val steps = stepViewModel.stepsToday

    LaunchedEffect(Unit) {
        stepViewModel.startListening()
    }

    DisposableEffect(Unit) {
        onDispose {
            stepViewModel.stopListening()
        }
    }

    val cameraPositionState = rememberCameraPositionState()


    LaunchedEffect(Unit) {
        snapshotFlow { viewModel.currentLatLng }
            .collect { latLng ->
                latLng?.let {
                    cameraPositionState.animate(CameraUpdateFactory.newLatLng(it))
                }
            }
    }


    val poids = profileViewModel.weight.value

    LaunchedEffect(Unit) {
        profileViewModel.fetchUserWeight()
    }

    LaunchedEffect(poids) {
        poids?.let {
            viewModel.userWeightKg = it
        }
    }

    var expanded by remember { mutableStateOf(false) }
    val options = listOf("Walking", "Jogging", "Running", "Biking")






    val distance = viewModel.distance
    val speed = viewModel.speed
    val time = viewModel.formatTime()
    val isTracking = viewModel.isTracking

    if (showDialog) {
        AlertDialog(
            onDismissRequest = { showDialog = false },
            title = { Text("Enregistrer la séance ?") },
            text = { Text("Souhaitez-vous enregistrer cette session d'entraînement ?") },
            confirmButton = {
                // enregistrer puis remettre tout à zero
                TextButton(onClick = {
                    viewModel.saveSessionToFirestore(
                        steps = steps,
                        onSuccess = {
                            showDialog = false
                            navController.navigate("session_summary") {
                                launchSingleTop = true
                            }
                        }
                    )
                    viewModel.resetSession()

                }) {
                    Text("Oui")
                }
            },
            dismissButton = {
                TextButton(onClick = {
                    viewModel.resetSession()
                    showDialog = false
                }) {
                    Text("Non")
                }
            }
        )
    }


    Column(Modifier.fillMaxSize()) {
        // MAP
        Box(
            modifier = Modifier
                //.height(650.dp)
                .weight(3f)
                .fillMaxWidth()
        ) {
            GoogleMap(
                modifier = Modifier.fillMaxSize(),
                cameraPositionState = cameraPositionState,
                properties = MapProperties(
                    isMyLocationEnabled = true
                ),
                uiSettings = MapUiSettings(
                    myLocationButtonEnabled = true
                )
            ) {
                if (viewModel.pathPoints.size > 1) {
                    Polyline(
                        points = viewModel.pathPoints,
                        color = Color.Blue,
                        width = 5f
                    )
                }
            }

        }

        // INFOS EN BAS
        Box(
            modifier = Modifier
                .weight(1f)
                .fillMaxWidth()
                .background(Color(0xFF0A185F))
                .padding(16.dp)
        ) {
            Column(horizontalAlignment = Alignment.CenterHorizontally, modifier = Modifier.fillMaxWidth()) {

                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(start = 150.dp)

                ) {

                    Column (
                        horizontalAlignment = Alignment.CenterHorizontally

                    ) {
                        Text("Distance", color = Color.White, fontSize = 16.sp)
                        Text(text = String.format("%.2f", distance), color = Color.White, fontSize = 36.sp, fontWeight = FontWeight.Bold)
                        Text("km", color = Color.White, fontSize = 14.sp)

                    }

                    Box(
                        modifier = Modifier
                            .align(Alignment.CenterVertically)
                            .padding(start = 16.dp)
                    ) {
                        IconButton(onClick = { expanded = true }) {
                            Icon(Icons.AutoMirrored.Filled.DirectionsRun, contentDescription = "Select activity", tint = Color.White, modifier = Modifier.size(32.dp))
                        }

                        DropdownMenu(expanded = expanded, onDismissRequest = { expanded = false }) {
                            options.forEach { option ->
                                DropdownMenuItem(
                                    text = { Text(option) },
                                    onClick = {
                                        viewModel.selectedActivity = option
                                        expanded = false
                                    }
                                )
                            }
                        }
                    }

                }

                Spacer(modifier = Modifier.height(12.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceAround,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally,
                        modifier = Modifier
                            .padding(end = 40.dp)
                    ) {
                        Text("Time", color = Color.White, fontSize = 14.sp)
                        Text(text = time, color = Color.White, fontSize = 16.sp)
                        Text("hh/min/seconds", color = Color.White, fontSize = 10.sp)
                    }

                    // Bouton Play
                    IconButton(onClick = {
                        if (isTracking){
                            viewModel.stopTracking()
                            showDialog = true
                        } else {
                            viewModel.startTracking()
                        }

                    }) {
                        Icon(
                            imageVector = if (isTracking) Icons.Default.Pause else Icons.Default.PlayArrow,
                            contentDescription = "Start",
                            modifier = Modifier
                                .align(Alignment.CenterVertically)
                                .size(48.dp)
                                .background(Color.White, CircleShape)
                                .padding(8.dp),
                            tint = Color(0xFF0A185F)
                        )
                    }

                    // Icône musique
                    IconButton(onClick = { /* aller à music */ }) {
                        Icon(
                            imageVector = Icons.Default.MusicNote,
                            contentDescription = "Music",
                            tint = Color.White,
                            modifier = Modifier.size(32.dp)
                        )
                    }

                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text("Speed", color = Color.White, fontSize = 14.sp)
                        Text(text = String.format("%.2f", speed), color = Color.White, fontSize = 16.sp)
                        Text("km/h", color = Color.White, fontSize = 10.sp)
                    }
                }
                Spacer(modifier = Modifier.height(12.dp))


                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ){

                    Column(horizontalAlignment = Alignment.CenterHorizontally
                    ){
                        Text("Steps: $steps", color = Color.White, fontSize = 16.sp)

                    }

                    //Spacer(modifier = Modifier.height(12.dp))

                    Column(horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text(
                            "Calories: ${String.format("%.2f", viewModel.calories)}",
                            color = Color.White,
                            fontSize = 16.sp
                        )
                    }
                }
            }



        }


    }
}

@RequiresApi(Build.VERSION_CODES.Q)
@Composable
fun TrackScreenWithPermission(navController : NavController, trackingVm : TrackingViewModel = viewModel(), stepVm : StepCounterViewModel = viewModel()) {
    var permissionGranted by remember { mutableStateOf(false) }
    val profileVm: UserProfileViewModel = viewModel()

    if (permissionGranted) {
        LaunchedEffect(permissionGranted) {
            if (permissionGranted) {
                trackingVm.startLocationUpdates() // démarre le GPS dès qu’on entre dans l’écran
            }
        }

        TrackScreen(
            navController = navController,
            viewModel = trackingVm,
            stepViewModel = stepVm,
            profileViewModel = profileVm
        )
    } else {
        RequestLocationPermission {
            permissionGranted = true
        }
    }
}


@RequiresApi(Build.VERSION_CODES.Q)
@Preview(showBackground = true)
@Composable
fun TrackScreenPreview() {
    TrackScreenWithPermission(navController = rememberNavController())
}
