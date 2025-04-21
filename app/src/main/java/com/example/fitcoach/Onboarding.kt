package com.example.fitcoach

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.*
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.*
import androidx.navigation.NavController
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.Alignment
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.layout.ContentScale
import androidx.compose.foundation.Image
import androidx.compose.runtime.saveable.rememberSaveable

@Composable
fun OnboardingScreens(navController: NavController) {
    val pages = listOf(
        OnboardingPage(R.drawable.onboarding5, "Welcome in Fit’ify", "Your smart coach for personalized and motivating workouts."),
        OnboardingPage(R.drawable.onboarding2, "", "Fit’ify tracks your performance live. You move, it picks up."),
        OnboardingPage(R.drawable.onboarding3, "", "Workout at home, outside or in a gym."),
        OnboardingPage(R.drawable.onboarding4, "", "Train with music that automatically adapts to your exercise, your pace, your location, and even the daylight!"),
        OnboardingPage(R.drawable.onboarding5, "", "Track your progress every day, take on challenges, share your achievements with your friends and climb the leaderboards!")
    )

    var currentPage by rememberSaveable { mutableStateOf(0) }

    Box(modifier = Modifier.fillMaxSize()) {
        Image(
            painter = painterResource(id = pages[currentPage].image),
            contentDescription = null,
            contentScale = ContentScale.Crop,
            modifier = Modifier.fillMaxSize()
        )

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(24.dp),
            verticalArrangement = Arrangement.SpaceBetween
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                // Barre de progression
                Row(modifier = Modifier.weight(1f), horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                    pages.forEachIndexed { index, _ ->
                        Box(
                            modifier = Modifier
                                .weight(1f)
                                .height(4.dp)
                                .background(if (index <= currentPage) Color(0xFFE86144) else Color.LightGray)
                        )
                    }
                }

                Spacer(modifier = Modifier.width(12.dp))

                Text(
                    "Skip",
                    modifier = Modifier
                        .clickable {
                            navController.navigate("home") {
                                popUpTo("onboardingFlow") { inclusive = true }
                            }
                        },
                    color = Color.White
                )
            }

            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier.fillMaxWidth()
            ) {
                if (pages[currentPage].title.isNotEmpty()) {
                    Text(
                        text = pages[currentPage].title,
                        fontSize = 32.sp,
                        color = Color.White,
                        textAlign = TextAlign.Center,
                        fontWeight = FontWeight.Bold
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                }

                Text(
                    text = pages[currentPage].description,
                    fontSize = 36.sp,
                    color = Color.White,
                    textAlign = TextAlign.Center,
                    fontWeight = FontWeight.Medium
                )
            }

            Button(
                onClick = {
                    if (currentPage < pages.lastIndex) {
                        currentPage++
                    } else {
                        navController.navigate("home") {
                            popUpTo("onboardingFlow") { inclusive = true }
                        }
                    }
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(60.dp),
                shape = RoundedCornerShape(16.dp),
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFE86144))
            ) {
                Text("Next", color = Color.White, fontSize = 18.sp)
            }
        }
    }
}

data class OnboardingPage(val image: Int, val title: String, val description: String)
