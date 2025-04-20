package com.example.fitcoach

import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.tooling.preview.Preview
import androidx.navigation.compose.rememberNavController

@Preview(showBackground = true, name = "🏁 Onboarding Screen")
@Composable
fun FirstOnboardingScreenPreview() {
    MaterialTheme {
        FirstOnboardingScreen(
            onLoginClick = {},
            onGetStartedClick = {}
        )
    }
}

@Preview(showBackground = true, name = "🔐 Login Screen")
@Composable
fun LoginScreenPreview() {
    val fakeNavController = rememberNavController()

    MaterialTheme {
        LoginScreen(
            navController = fakeNavController,
            onLoginSuccess = {},
            onGoToRegister = {}
        )
    }
}



