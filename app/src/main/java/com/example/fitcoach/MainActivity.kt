package com.example.fitcoach

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.google.firebase.FirebaseApp
import com.google.firebase.auth.FirebaseAuth

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        FirebaseApp.initializeApp(this)
        setContent {
            FitCoachApp()
        }
    }
}

@Composable
fun FitCoachApp() {
    val navController = rememberNavController()
    val isUserLoggedIn = FirebaseAuth.getInstance().currentUser != null
    val startDestination = if (isUserLoggedIn) "home" else "onboarding"

    NavHost(navController = navController, startDestination = startDestination) {
        composable("onboarding") {
            FirstOnboardingScreen(
                onLoginClick = { navController.navigate("login") },
                onGetStartedClick = { navController.navigate("secondOnboarding") } // 👈 nouvelle page
            )
        }
        composable("secondOnboarding") {
            SecondOnboardingScreen(
                onSignUpClick = { navController.navigate("register") },
                onSignInClick = { navController.navigate("login") }
            )
        }
        composable("login") {
            LoginScreen(
                navController = navController,
                onLoginSuccess = { navController.navigate("question1") },
                onGoToRegister = { navController.navigate("register") }
            )
        }
        composable("register") {
            RegisterScreen(
                navController = navController,
                onRegisterSuccess = {
                    navController.navigate("question1") {
                        popUpTo("register") { inclusive = true }
                    }
                }
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

        composable("question1") {
            QuestionOneScreen(
                navController = navController,
                onNextClick = { selectedGoal ->
                    // Tu peux l'enregistrer ici plus tard
                    navController.navigate("question2") // à créer ensuite
                }
            )
        }
        composable("question2") {
            QuestionTwoScreen(
                navController = navController,
                onNextClick = { height, weight, unit ->
                    // TODO: enregistrer si besoin dans Firestore ou ViewModel

                    navController.navigate("question3")
                }
            )
        }
        composable("question3") {
            QuestionThreeScreen(
                navController = navController,
                onNextClick = { gender, birthdate ->
                    // TODO: enregistrer si besoin dans Firestore ou ViewModel
                    navController.navigate("question4") // ou "question4" plus tard
                }
            )
        }
        composable("question4") {
            QuestionFourScreen(
                navController = navController,
                onNextClick = { response ->
                    // TODO: enregistrer si besoin dans Firestore ou ViewModel
                    navController.navigate("question5")
                }
            )
        }
        composable("question5") {
            QuestionFiveScreen(
                navController = navController,
                onNextClick = { stepGoal ->
                    // TODO : enregistrer la valeur si besoin
                    navController.navigate("createProfile")
                }
            )
        }
        composable("createProfile") {
            CreateProfileScreen(
                navController = navController,
                onProfileCreated = { avatarUri, firstName, lastName ->
                    // TODO : tu peux sauvegarder ces valeurs dans un ViewModel ou Firestore
                    navController.navigate("home")
                }
            )
        }










    }
}

@Composable
fun FirstOnboardingScreen(
    onLoginClick: () -> Unit,
    onGetStartedClick: () -> Unit
) {
    Box(
        modifier = Modifier
            .fillMaxSize()
    ) {
        Image(
            painter = painterResource(id = R.drawable.page1),
            contentDescription = "Workout Background",
            contentScale = ContentScale.Crop,
            modifier = Modifier.fillMaxSize()
        )

        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(
                    Brush.verticalGradient(
                        colors = listOf(Color.Transparent, Color.Black),
                        startY = 500f
                    )
                )
        )

        Column(
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = buildAnnotatedString {
                    append("Take your ")
                    withStyle(style = SpanStyle(color = Color(0xFFE76F51), fontWeight = FontWeight.Bold)) {
                        append("workout\n")
                    }
                    append("routines to a higher level.")
                },
                fontSize = 26.sp,
                color = Color.White,
                textAlign = TextAlign.Center,
                lineHeight = 36.sp
            )

            Spacer(modifier = Modifier.height(16.dp))

            Row {
                Text(
                    text = "Already have an account? ",
                    color = Color.White,
                    fontSize = 16.sp // ✅ augmenté
                )
                Text(
                    text = "Log in",
                    color = Color(0xFFE76F51),
                    fontWeight = FontWeight.Bold,
                    fontSize = 16.sp, // ✅ augmenté aussi
                    modifier = Modifier.clickable { onLoginClick() }
                )
            }


            Spacer(modifier = Modifier.height(20.dp))

            Button(
                onClick = { onGetStartedClick() },
                shape = RoundedCornerShape(20.dp), // ✅ corner radius Figma
                colors = ButtonDefaults.buttonColors(
                    containerColor = Color(0xFFE76F51)
                ),
                modifier = Modifier
                    .width(338.dp) // ✅ largeur Figma
                    .height(68.dp) // ✅ hauteur Figma
            ) {
                Text(
                    text = "Get Started",
                    color = Color.White,
                    fontSize = 16.sp
                )
            }

        }
    }
}
@Composable
fun SecondOnboardingScreen(
    onSignUpClick: () -> Unit,
    onSignInClick: () -> Unit
) {
    val imageList = listOf(
        R.drawable.img1,
        R.drawable.img2,
        R.drawable.img3,
        R.drawable.img4,
        R.drawable.img5,
        R.drawable.img6,
        R.drawable.img7,
        R.drawable.img8,
        R.drawable.img9
    )

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.White)
    ) {
        // ✅ Partie haute - grille agrandie
        Box(modifier = Modifier.height(420.dp)) {
            LazyVerticalGrid(
                columns = GridCells.Fixed(3),
                modifier = Modifier
                    .fillMaxSize(),
                contentPadding = PaddingValues(4.dp),
                verticalArrangement = Arrangement.spacedBy(4.dp),
                horizontalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                items(imageList.size) { index ->
                    Image(
                        painter = painterResource(id = imageList[index]),
                        contentDescription = null,
                        contentScale = ContentScale.Crop,
                        modifier = Modifier
                            .aspectRatio(1f)
                            .clip(RoundedCornerShape(8.dp))
                    )
                }
            }

            // ✅ Dégradé fondu
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(80.dp)
                    .align(Alignment.BottomCenter)
                    .background(
                        Brush.verticalGradient(
                            colors = listOf(Color.Transparent, Color.White)
                        )
                    )
            )
        }

        // ✅ Partie centrale - texte et bouton
        Box(
            modifier = Modifier
                .weight(1f)
                .fillMaxWidth(),
            contentAlignment = Alignment.Center
        ) {
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Text(
                    text = "Welcome to Sporter.",
                    fontSize = 32.sp,
                    fontWeight = FontWeight.Bold,
                    fontFamily = FontFamily.Default,
                    textAlign = TextAlign.Center,
                    color = Color.Black
                )

                Spacer(modifier = Modifier.height(8.dp))

                Text(
                    text = "One best app for all things fitness",
                    fontSize = 20.sp,
                    color = Color.Gray,
                    fontFamily = FontFamily.Default,
                    textAlign = TextAlign.Center
                )

                Spacer(modifier = Modifier.height(24.dp))

                Button(
                    onClick = { onSignUpClick() },
                    shape = RoundedCornerShape(20.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = Color(0xFFE76F51)
                    ),
                    modifier = Modifier
                        .width(338.dp)
                        .height(68.dp)
                ) {
                    Text("Sign Up", color = Color.White, fontSize = 16.sp)
                }
            }
        }

        // ✅ Partie basse - Sign In aligné tout en bas
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 16.dp),
            horizontalArrangement = Arrangement.Center
        ) {
            Text("Already have account? ")
            Text(
                "Sign In",
                color = Color(0xFFE76F51),
                fontWeight = FontWeight.Bold,
                modifier = Modifier.clickable { onSignInClick() }
            )
        }
    }
}



