package com.example.fitcoach

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.EmojiEvents
import androidx.compose.material.icons.filled.KeyboardArrowRight
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.ShowChart
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.google.firebase.auth.FirebaseAuth

fun getCurrentUsername(): String {
    return FirebaseAuth.getInstance().currentUser?.displayName ?: "Utilisateur"
}


@Composable
fun ProfileScreen(username: String = getCurrentUsername()) {
    //val user = FirebaseAuth.getInstance().currentUser
    //val username = user?.displayName ?: "Utilisateur"
    val age = 28
    Text(username, fontSize = 20.sp, fontWeight = FontWeight.Bold)


    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFFFDF4EF))
            .padding(16.dp)
    ) {
        // Photo + nom
        Row(
            modifier = Modifier
                .padding(bottom = 8.dp, top = 10.dp, start = 10.dp),
            verticalAlignment = Alignment.CenterVertically) {
            Image(
                painter = painterResource(id = R.drawable.july_photo_profile),
                contentDescription = "Profile picture",
                contentScale = ContentScale.Crop,
                modifier = Modifier
                    .size(90.dp)
                    .clip(CircleShape)
            )
            Spacer(modifier = Modifier.width(16.dp))
            Column {
                Text(username, fontSize = 20.sp, fontWeight = FontWeight.Bold)
                Text("$age years old", fontSize = 14.sp, color = Color.Gray)
            }
        }

        Spacer(modifier = Modifier.height(24.dp))
        Text("Your Progress", fontSize = 17.sp)

        Spacer(modifier = Modifier.height(8.dp))

        Row(
            modifier = Modifier
                .align(Alignment.CenterHorizontally),
            horizontalArrangement = Arrangement.spacedBy(25.dp)) {
            repeat(3) {
                Icon(
                    painter = painterResource(id = R.drawable.ic_check_circle),
                    contentDescription = null,
                    tint = Color(0xFFE86144),
                    modifier = Modifier.size(32.dp)
                )
            }
            Text("27", modifier = Modifier
                .align(Alignment.CenterVertically))
            Text("28", modifier = Modifier.align(Alignment.CenterVertically))
            Text("29", modifier = Modifier.align(Alignment.CenterVertically))
            Text("30", modifier = Modifier.align(Alignment.CenterVertically))

        }

        Spacer(modifier = Modifier.height(12.dp))

        // Progress Stats
        Row(
            modifier = Modifier
                .width(320.dp)
                .clip(RoundedCornerShape(16.dp))
                .background(Color.White)
                .align(Alignment.CenterHorizontally)
                .padding(16.dp),
            horizontalArrangement = Arrangement.SpaceEvenly
        ) {
            StatBlock("16", "Workouts")
            StatBlock("10540", "KCAL")
            StatBlock("49", "Minutes")
        }

        Spacer(modifier = Modifier.height(24.dp))

        ProfileOptionItem("Friends", Icons.Default.Person)
        ProfileOptionItem("Reward Collection", Icons.Default.EmojiEvents)
        ProfileOptionItem("Statistics", Icons.Default.ShowChart)
        ProfileOptionItem("Settings", Icons.Default.Settings)
    }
}

@Composable
fun StatBlock(value: String, label: String) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Text(value, fontWeight = FontWeight.Bold)
        Text(label, fontSize = 12.sp, color = Color.Gray)
    }
}

@Composable
fun ProfileOptionItem(title: String, icon: ImageVector) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .height(80.dp)
            .padding(vertical = 6.dp)
            .clip(RoundedCornerShape(16.dp))
            .background(Color.White)
            .padding(horizontal = 16.dp, vertical = 12.dp)
            .clickable {
                // TODO: Naviguer vers .....
            },
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(icon, contentDescription = null, tint = Color(0xFFE86144), modifier = Modifier.size(32.dp))
        Spacer(modifier = Modifier.width(12.dp))
        Text(title, modifier = Modifier.weight(1f), fontSize = 18.sp)
        Icon(Icons.Default.KeyboardArrowRight, contentDescription = null, tint = Color.Gray)
    }
}

@Preview(showBackground = true)
@Composable
fun ProfileScreenPreview() {
    ProfileScreen(username = "Emma Smith")
}


