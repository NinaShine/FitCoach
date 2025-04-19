package com.example.fitcoach

import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.google.firebase.auth.FirebaseAuth

@Composable
fun FitCoachApp() {
    val navController = rememberNavController()
    val isUserLoggedIn = FirebaseAuth.getInstance().currentUser != null

    // Démarrage conditionnel selon l'état de connexion
    val startDestination = if (isUserLoggedIn) "home" else "onboarding"

    NavHost(navController = navController, startDestination = "onboarding") {
        composable("onboarding") {
            FirstOnboardingScreen(
                onLoginClick = { navController.navigate("login") },
                onGetStartedClick = { navController.navigate("register") }
            )
        }
        composable("login") {
            LoginScreen(
                onLoginSuccess = { navController.navigate("home") },
                onGoToRegister = { navController.navigate("register") }
            )
        }
        composable("register") {
            RegisterScreen(
                navController = navController,
                onRegisterSuccess = { navController.navigate("home") },
            )
        }
        composable("home") {
            HomeScreen(
                onLogout = {
                    FirebaseAuth.getInstance().signOut()
                    navController.navigate("onboarding") {
                        popUpTo("home") { inclusive = true }
                    }
                }
            )
        }
    }
}

