package com.example.fitcoach.ui.screen.section_tracking

import android.Manifest
import androidx.annotation.RequiresPermission
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import com.google.android.gms.location.LocationServices
import com.google.android.gms.maps.model.CameraPosition
import com.google.android.gms.maps.model.LatLng
import com.google.maps.android.compose.GoogleMap
import com.google.maps.android.compose.Marker
import com.google.maps.android.compose.MarkerState
import com.google.maps.android.compose.rememberCameraPositionState
import kotlinx.coroutines.tasks.await

@RequiresPermission(Manifest.permission.ACCESS_FINE_LOCATION)
@Composable
fun TrackingMapScreen() {
    val context = LocalContext.current
    val fusedLocationClient = remember { LocationServices.getFusedLocationProviderClient(context) }
    val cameraPositionState = rememberCameraPositionState()
    var currentLocation by remember { mutableStateOf<LatLng?>(null) }

    LaunchedEffect(Unit) {
        try {
            val location = fusedLocationClient.lastLocation.await()
            location?.let {
                val latLng = LatLng(it.latitude, it.longitude)
                currentLocation = latLng
                cameraPositionState.position = CameraPosition.fromLatLngZoom(latLng, 15f)
            }
        } catch (e: SecurityException) {
            e.printStackTrace()
        }
    }

    Box(Modifier.fillMaxSize()) {
        GoogleMap(
            modifier = Modifier.fillMaxSize(),
            cameraPositionState = cameraPositionState
        ) {
            currentLocation?.let {
                Marker(
                    state = MarkerState(position = it),
                    title = "Vous êtes ici"
                )
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun TrackingMapScreenPreview(){
    Box(Modifier.fillMaxSize())
}
