package com.example.fitcoach.ui.screen

import android.content.pm.PackageManager
import android.os.Build
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.annotation.RequiresApi
import androidx.compose.runtime.Composable
import androidx.navigation.NavController
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowRight
import androidx.compose.material.icons.automirrored.filled.Logout
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.core.content.ContextCompat
import androidx.navigation.compose.rememberNavController
import com.example.fitcoach.viewmodel.AuthViewModel
import com.google.firebase.messaging.FirebaseMessaging

@RequiresApi(Build.VERSION_CODES.TIRAMISU)
@Composable
fun NotificationSettingsScreen(
    navController: NavController,
    initialValue: Boolean = false,
    onSave: (Boolean) -> Unit
) {
    var notificationsEnabled by remember { mutableStateOf(initialValue) }
    val context = LocalContext.current

    val permission = android.Manifest.permission.POST_NOTIFICATIONS
    val permissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission()
    ) { isGranted ->
        notificationsEnabled = isGranted
        if (isGranted) {
            Toast.makeText(context, "Notifications activées", Toast.LENGTH_SHORT).show()
        } else {
            Toast.makeText(context, "Notifications refusées", Toast.LENGTH_SHORT).show()
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFFFEF3ED))
            .padding(24.dp)
    ) {
        // Header
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            IconButton(onClick = { navController.popBackStack() }) {
                Icon(Icons.Default.ArrowBack, contentDescription = "Back")
            }
            Text(
                text = "Notifications",
                style = MaterialTheme.typography.titleLarge,
                modifier = Modifier.weight(1f),
                textAlign = TextAlign.Center
            )
            TextButton(onClick = {
                onSave(notificationsEnabled)
                if (notificationsEnabled) {
                    FirebaseMessaging.getInstance().subscribeToTopic("general")
                } else {
                    FirebaseMessaging.getInstance().unsubscribeFromTopic("general")
                }
            }) {
                Text("Save", color = Color(0xFFE86144))
            }
        }

        Spacer(modifier = Modifier.height(24.dp))

        // Toggle notification
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 8.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text("Enable notifications", color = Color.Black)
            Switch(
                checked = notificationsEnabled,
                onCheckedChange = { isChecked ->
                    if (isChecked) {
                        if (ContextCompat.checkSelfPermission(context, permission) != PackageManager.PERMISSION_GRANTED) {
                            permissionLauncher.launch(permission)
                        } else {
                            notificationsEnabled = true
                        }
                    } else {
                        notificationsEnabled = false
                    }
                },
                colors = SwitchDefaults.colors(
                    checkedThumbColor = Color.White,
                    checkedTrackColor = Color(0xFFE86144)
                )
            )
        }

        HorizontalDivider(color = Color.Black)
    }
}
