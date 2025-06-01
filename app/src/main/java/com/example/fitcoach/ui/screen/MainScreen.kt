package com.example.fitcoach.ui.screen

import android.os.Build
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.fitcoach.R
import com.example.fitcoach.ui.screen.section_accueil.AccueilPageWithNavBar
import com.example.fitcoach.ui.screen.section_chatbot.ChatBotScreen
import com.example.fitcoach.ui.screen.section_music.MusicScreen
import com.example.fitcoach.ui.screen.section_music.MusicScreenWithNavBar
import com.example.fitcoach.ui.screen.section_profile.EditProfileScreen
import com.example.fitcoach.ui.screen.section_profile.ProfileScreen
import com.example.fitcoach.ui.screen.section_profile.StatsScreen
import com.example.fitcoach.ui.screen.section_tracking.TrackScreenWithPermission
import com.example.fitcoach.ui.screen.section_social.ChallengeScreen
import com.example.fitcoach.ui.screen.section_social.CreatePostScreen
import com.example.fitcoach.ui.screen.section_tracking.TrackScreen
import com.example.fitcoach.ui.screen.section_tracking.TrackScreenWithPermission
import com.example.fitcoach.ui.screen.section_tracking.WorkoutSummaryScreenWithNavBar
import com.example.fitcoach.ui.screen.section_social.LeaderboardScreen
import com.example.fitcoach.ui.screen.section_workout.CreateRoutineScreen
import com.example.fitcoach.ui.screen.section_workout.ExerciseDetailScreen
import com.example.fitcoach.ui.screen.section_workout.ExerciseListScreen
import com.example.fitcoach.ui.screen.section_workout.ExerciseListScreen2
import com.example.fitcoach.ui.screen.section_workout.ExploreRoutinesScreen
import com.example.fitcoach.ui.screen.section_workout.QuickWorkoutScreen
import com.example.fitcoach.ui.screen.section_workout.RoutineDetailScreen
import com.example.fitcoach.ui.screen.section_workout.WorkoutHelpScreen
import com.example.fitcoach.ui.screen.section_workout.WorkoutScreen
import com.example.fitcoach.viewmodel.CurrentlyPlayingViewModel
import com.example.fitcoach.viewmodel.UserOnboardingViewModel
import com.example.fitcoach.viewmodel.track_section.LiveTrackingViewModel
import com.example.fitcoach.viewmodel.track_section.SessionSummaryScreen
import com.example.fitcoach.viewmodel.track_section.StepCounterViewModel
import com.example.fitcoach.viewmodel.track_section.TrackingViewModel
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore



@RequiresApi(Build.VERSION_CODES.TIRAMISU)
@Composable
fun FitCoachApp(currentlyPlayingVm : CurrentlyPlayingViewModel, liveTrackingVm: LiveTrackingViewModel, initialRoute: String? = null) {
    val navController = rememberNavController()
    val isUserLoggedIn = FirebaseAuth.getInstance().currentUser != null
    //val defaultStart = "onboarding"
    val defaultStart = if (FirebaseAuth.getInstance().currentUser != null) "accueil" else "onboarding"
    val startDestination = initialRoute ?: defaultStart

    val viewModel: UserOnboardingViewModel = viewModel()
    val stepViewModel: StepCounterViewModel = viewModel()
    val trackViewModel: TrackingViewModel = viewModel()


    NavHost(navController = navController, startDestination = startDestination) {
        composable("onboarding") {
            FirstOnboardingScreen(
                onLoginClick = { navController.navigate("login") },
                onGetStartedClick = {
                    navController.navigate("secondOnboarding")
                    //onGetStartedClick = { navController.navigate("music") //  pour tester music screen (a enlever apres)

                }
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
                onLoginSuccess = { navController.navigate("accueil") },
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
                },
                onboardingViewModel = viewModel
            )
        }

        composable("question1") {
            QuestionOneScreen(navController = navController, viewModel)
        }
        composable("question2") {
            QuestionTwoScreen(
                navController = navController,
                viewModel)
        }
        composable("question3") {
            QuestionThreeScreen(
                navController = navController,
                viewModel)
        }
        composable("question4") {
            QuestionFourScreen(
                navController = navController,
                viewModel)
        }
        composable("question5") {
            QuestionFiveScreen(navController, viewModel)
            /*
            QuestionFiveScreen(
                navController = navController,
                onNextClick = { stepGoal ->
                    navController.navigate("createProfile")
                }
            )
             */
        }
        composable("createProfile") {
            CreateProfileScreen(navController, viewModel)
            /*
            CreateProfileScreen(
                navController = navController,
                onProfileCreated = { avatarUri, firstName, lastName ->
                    navController.navigate("onboarding1")
                }
            )

             */
        }
        composable("onboarding1") {
            OnboardingScreens(navController)
        }

        composable("music") {
            val accessToken = getSpotifyAccessToken(LocalContext.current).orEmpty()
            MusicScreen(navController = navController, accessToken = accessToken, currentlyPlayingVm = currentlyPlayingVm, steps = 0, calories = 0.0, distanceKm = 0.0)
        }
        composable ("musicWithNavBar"){
            MusicScreenWithNavBar(
                navController = navController,
                currentlyPlayingVm = currentlyPlayingVm,
                liveTrackingVm = liveTrackingVm
            )
        }

        composable("searchMusic"){
            SearchMusicScreen(navController = navController, currentlyPlayingVm = currentlyPlayingVm)
        }

        composable("accueil"){
            AccueilPageWithNavBar(navController = navController, liveTrackingVm = liveTrackingVm, currentlyPlayingVm = currentlyPlayingVm)
        }

        composable("forgotPassword") {
            ForgotPasswordScreen(navController = navController)
        }

        composable("spotifyProfile") {
            val accessToken = getSpotifyAccessToken(LocalContext.current).orEmpty()
            SpotifyProfileScreen(navController = navController, accessToken = accessToken)
        }

        composable("profile"){
            ProfileScreen(navController = navController, liveTrackingVm)
        }
        composable("workout") {
            WorkoutScreen(navController)
        }
        composable("workout_help") {
            WorkoutHelpScreen(navController = navController)
        }
        composable("quick_workout") {
            QuickWorkoutScreen(navController = navController)
        }
        composable("settings"){
            SettingsScreen(
                navController = navController,
                authViewModel = viewModel(),
                onLogout = {
                    navController.navigate("login") {
                        popUpTo("accueil") { inclusive = true }
                    }
                }
            )
        }

        composable("edit_profile"){
            EditProfileScreen(navController = navController)
        }

        composable("notification_settings"){
            val context = LocalContext.current
            NotificationSettingsScreen(
                navController = navController,
                initialValue = viewModel.answers.notificationsEnabled,
                onSave = { enabled ->
                    val uid = FirebaseAuth.getInstance().currentUser?.uid
                    if (uid != null) {
                        FirebaseFirestore.getInstance().collection("users")
                            .document(uid)
                            .update("notificationsEnabled", enabled)
                            .addOnSuccessListener {
                                navController.popBackStack()
                            }
                            .addOnFailureListener {
                                Toast.makeText(context, "Erreur Firestore : ${it.message}", Toast.LENGTH_SHORT).show()
                            }
                    }

                }
            )

        }

        composable("privacy_settings"){
            PrivacyScreen(navController = navController)
        }

        composable("exercise_list") {
            ExerciseListScreen(navController = navController)
        }

        composable("exercise_detail/{id}") { backStackEntry ->
            ExerciseDetailScreen(navController = navController, backStackEntry = backStackEntry)
        }
        composable("explore_routines") {
            ExploreRoutinesScreen(
                onBack = {
                    navController.popBackStack()
                }
            )
        }
        composable("create_routine") {
            CreateRoutineScreen(navController)
        }

        composable("exercise_list_2") {
            ExerciseListScreen2(navController)
        }
        composable("routine_detail/{id}") { backStackEntry ->
            val routineId = backStackEntry.arguments?.getString("id") ?: return@composable
            RoutineDetailScreen(routineId = routineId, navController = navController)
        }
        composable("challenges") {
            val uid = FirebaseAuth.getInstance().currentUser?.uid
            if (uid != null) {
                ChallengeScreen(currentUid = uid, navController = navController)
            } else {
                Text("Connexion requise")
            }
        }
        composable("leaderboard") {
            val currentUserId = FirebaseAuth.getInstance().currentUser?.uid
            if (currentUserId != null) {
                LeaderboardScreen(currentUserId, navController)
            }
        }




        composable("createPost") {
            CreatePostScreen(navController = navController)
        }


        composable("track") {
            TrackScreenWithPermission(
                navController,
                trackViewModel,
                stepViewModel
                )
        }

        composable("session_summary") {
            val session = trackViewModel.lastSessionData

            if (session != null) {
                SessionSummaryScreen(
                    navController = navController,
                    distanceKm = session.distanceKm,
                    durationMs = session.durationMs,
                    speedKmH = session.speedKmH,
                    calories = session.calories,
                    steps = session.steps,
                    activityType = session.activityType
                )
            } else {
                Text("Aucune session enregistrée")
            }
        }

        composable("workout_summary") {
            WorkoutSummaryScreenWithNavBar(
                navController = navController,
                liveTrackingVm = liveTrackingVm)
        }

        composable("chatbot") {
            ChatBotScreen(navController = navController)
        }

        composable("profile_stats"){
            StatsScreen(navController = navController, liveTrackingVm)
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
                    fontSize = 16.sp
                )
                Text(
                    text = "Log in",
                    color = Color(0xFFE76F51),
                    fontWeight = FontWeight.Bold,
                    fontSize = 16.sp,
                    modifier = Modifier.clickable { onLoginClick() }
                )
            }


            Spacer(modifier = Modifier.height(20.dp))

            Button(
                onClick = { onGetStartedClick() },
                shape = RoundedCornerShape(20.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = Color(0xFFE76F51)
                ),
                modifier = Modifier
                    .width(338.dp)
                    .height(68.dp)
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