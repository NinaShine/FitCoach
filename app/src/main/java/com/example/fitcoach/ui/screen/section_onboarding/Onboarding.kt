package com.example.fitcoach.ui.screen

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
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
import com.example.fitcoach.R
import com.example.fitcoach.data.model.OnboardingPage

@Composable
fun OnboardingScreens(navController: NavController) {
    val pages = listOf(
        OnboardingPage(R.drawable.onboarding1, "Welcome in Fit’ify", "Your smart coach for personalized and motivating workouts."),
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
                .padding(start = 24.dp, end = 24.dp, top = 24.dp, bottom = 48.dp),
            verticalArrangement = Arrangement.SpaceBetween
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
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
                            navController.navigate("accueil") {
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

            Box(
                modifier = Modifier.fillMaxWidth(),
                contentAlignment = Alignment.BottomEnd
            ) {
                IconButton(
                    onClick = {
                        if (currentPage < pages.lastIndex) {
                            currentPage++
                        } else {
                            navController.navigate("accueil") {
                                popUpTo("onboardingFlow") { inclusive = true }
                            }
                        }
                    },
                    modifier = Modifier.size(60.dp)
                ) {
                    Icon(
                        painter = painterResource(id = R.drawable.next),
                        contentDescription = "Next",
                        tint = Color(0xFFE86144),
                        modifier = Modifier.size(48.dp)
                    )
                }
            }
        }
    }
}
