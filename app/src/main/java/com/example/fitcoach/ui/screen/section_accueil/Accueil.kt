package com.example.fitcoach.ui.screen.section_accueil

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.FitnessCenter
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.MusicNote
import androidx.compose.material.icons.filled.Public
import androidx.compose.material.icons.filled.Whatshot
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import coil.compose.rememberAsyncImagePainter
import com.example.fitcoach.R
import com.example.fitcoach.ui.screen.section_music.MusicScreen
import com.example.fitcoach.ui.screen.getSpotifyAccessToken
import com.example.fitcoach.ui.screen.section_social.CreatePostScreen
import com.example.fitcoach.ui.screen.section_social.FeedScreen
import com.example.fitcoach.ui.screen.section_workout.WorkoutScreen
import com.example.fitcoach.viewmodel.CurrentlyPlayingViewModel
import com.example.fitcoach.viewmodel.UserProfileViewModel
import com.example.fitcoach.viewmodel.track_section.LastSessionViewModel
import com.example.fitcoach.viewmodel.track_section.LiveTrackingViewModel
import com.example.fitcoach.viewmodel.track_section.TrackingViewModel
import com.google.firebase.auth.FirebaseAuth


data class Recommendation(val title: String, val duration: String, val imageRes: Int)

val recommendations = listOf(
    Recommendation("Abdo", "45 min", R.drawable.exercice_abs),
    Recommendation("Cardio", "30 min", R.drawable.ex_cardio),
    Recommendation("Yoga", "60 min", R.drawable.ex_yoga),
    Recommendation("Abdo", "45 min", R.drawable.ex2_abs)
)

data class BottomNavItem(
    val label: String,
    val icon: ImageVector,
    val route: String
)

val bottomNavItems = listOf(
    BottomNavItem("Home", Icons.Default.Home, "home"),
    BottomNavItem("Workout", Icons.Default.FitnessCenter, "workout"),
    BottomNavItem("Music", Icons.Default.MusicNote, "music"),
    BottomNavItem("Social", Icons.Default.Public, "social")
)



@Composable
fun AccueilScreen(navController: NavController, steps: Int, calories: Double, distanceKm: Double) {
    val user = FirebaseAuth.getInstance().currentUser
    val viewModel: UserProfileViewModel = viewModel()
    val username by viewModel.username
    val avatarUrl by viewModel.avatarUrl

    LaunchedEffect(Unit) {
        viewModel.fetchUsername()
        viewModel.fetchAvatar()
    }

    val lastSessionViewModel: LastSessionViewModel = viewModel()
    val lastSession = lastSessionViewModel.lastSession.value

    LaunchedEffect(Unit) {
        lastSessionViewModel.fetchLastSession()
    }

    val trackingViewModel: TrackingViewModel = viewModel()

    LaunchedEffect(Unit) {
        trackingViewModel.startLocationUpdates()
    }

    val liveTrackingVm: LiveTrackingViewModel = viewModel()

    LaunchedEffect(Unit) {
        liveTrackingVm.startListening()
    }
/*
    val steps = liveTrackingVm.steps
    val calories = liveTrackingVm.calories
    val distanceKm = liveTrackingVm.distanceKm

 */



    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
            .background(Color.White),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        item {
            // Header
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text("fit’fy", fontWeight = FontWeight.Bold, fontSize = 22.sp, modifier = Modifier.align(Alignment.CenterVertically))
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Image(
                        painter = painterResource(id = R.drawable.robot_assistant),
                        contentDescription = null,
                        modifier = Modifier
                            .size(50.dp)
                            .clip(CircleShape)
                            .clickable {
                                navController.navigate("chatbot")
                            }
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Image(
                        //painter = painterResource(id = R.drawable.july_photo_profile),
                        // afficher avatarUrl
                        painter = rememberAsyncImagePainter(avatarUrl),
                        contentDescription = null,
                        contentScale = ContentScale.Crop,
                        modifier = Modifier
                            .size(45.dp)
                            .clip(CircleShape)
                            .clickable {
                                navController.navigate("profile")
                            }
                    )
                }
            }

            Divider(
                color = Color.Black,
                thickness = 1.dp,
                modifier = Modifier.width(80.dp)
            )

            Spacer(modifier = Modifier.height(8.dp))
            Text("Hey, ${username ?: "Chargement..."} !", fontSize = 25.sp)

            Spacer(modifier = Modifier.height(16.dp))
        }

        item {
            // Grid 2x2
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Column(
                    modifier = Modifier
                        .weight(1f),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    WidgetCard(
                        title = "Track Distance",
                        content = {
                            Image(
                                painter = painterResource(id = R.drawable.map_provisoire),
                                contentDescription = null,
                                modifier = Modifier
                                    .height(100.dp)
                                    .fillMaxWidth()
                                    .clip(RoundedCornerShape(10.dp)),
                                contentScale = ContentScale.Crop
                            )
                            Spacer(modifier = Modifier.height(8.dp))
                            if (lastSession != null) {
                                val time = lastSession["durationFormatted"] as? String ?: "Inconnu"
                                val distance = lastSession["distanceKm"] as? String ?: "0.0"
                                val type = lastSession["activityType"] as? String ?: "Unknown" // facultatif

                                Text("Latest: $type", fontSize = 12.sp, color = Color(0xFFE86144))
                                Text("Time: $time", fontSize = 12.sp, textAlign = TextAlign.Center)
                                Text("Distance: $distance km", fontSize = 12.sp, textAlign = TextAlign.Center)
                            } else {
                                Text("No data", fontSize = 12.sp, color = Color.Gray)
                            }
                            /*
                            Text("Latest: Walking", fontSize = 12.sp, color = Color(0xFFE86144))
                            Text("Time: 10 min", fontSize = 12.sp, textAlign = TextAlign.Center)
                            Text("Distance: 2 km", fontSize = 12.sp, textAlign = TextAlign.Center)

                             */
                        },
                        modifier = Modifier
                            .align(Alignment.CenterHorizontally)
                            .fillMaxWidth()
                            .height(200.dp)
                            .clickable {
                                navController.navigate("track")
                            }
                    )

                    WidgetCard(
                        title = "Track Workout",
                        content = {
                            Text("❤️", fontSize = 28.sp, modifier = Modifier.align(Alignment.CenterHorizontally))
                        },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(110.dp)
                            .clickable {
                                navController.navigate("workout_summary")
                            }
                    )
                }

                Column(
                    modifier = Modifier
                        .weight(1f),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    WidgetCard(
                        title = "Music",
                        icon = Icons.Default.MusicNote,
                        content = {
                            Text("Jul - Wesh Alors", fontSize = 14.sp, fontWeight = FontWeight.Medium)
                        },
                        gradient = Brush.horizontalGradient(listOf(Color(0xFFFFB47E), Color(0xFFFF8762))),
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(100.dp)
                            .clickable {
                                // Naviguer vers la page musique
                            }
                    )

                    WidgetCalories(
                        title = "Calories",
                        icon = Icons.Default.Whatshot,
                        content = {
                            Spacer(modifier = Modifier.height(8.dp))

                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.SpaceEvenly
                            ) {
                                /*CalorieProgress(current = 357, goal = 500)

                                Text(
                                    "  357 kcal",
                                    fontSize = 16.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = Color.Black
                                )

                                 */
                                CalorieProgress(current = calories.toInt(), goal = 500)

                                Text(
                                    "  ${calories.toInt()} kcal",
                                    fontSize = 16.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = Color.Black
                                )
                            }

                            Spacer(modifier = Modifier.height(12.dp))

                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(65.dp)
                                    .padding(horizontal = 1.dp),
                            ) {
                                Column(
                                    modifier = Modifier
                                        .weight(1f)
                                        .align(Alignment.CenterVertically)
                                ) {
                                    Text(" Steps ", fontSize = 17.sp, color = Color(0xFFE86144))
                                    Spacer(modifier = Modifier.height(12.dp))
                                    //Text(" 2285", fontSize = 16.sp, textAlign = TextAlign.Center)
                                    Text("$steps", fontSize = 16.sp, textAlign = TextAlign.Center)
                                }

                                Spacer(modifier = Modifier.width(12.dp))

                                Column {
                                    DividerVertical()
                                }

                                Spacer(modifier = Modifier.width(8.dp))

                                Column(
                                    modifier = Modifier.align(Alignment.CenterVertically)
                                ) {
                                    Text("Distance ", fontSize = 17.sp, color = Color(0xFFE86144))
                                    Spacer(modifier = Modifier.height(12.dp))
                                    //Text("2.5 km", fontSize = 16.sp, textAlign = TextAlign.Center)
                                    Text("%.2f km".format(distanceKm), fontSize = 16.sp, textAlign = TextAlign.Center)

                                }
                            }
                        },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(210.dp)
                    )
                }
            }
        }

        item {
            Spacer(modifier = Modifier.height(16.dp))
            Text("Recommendation", fontWeight = FontWeight.SemiBold)
            Spacer(modifier = Modifier.height(8.dp))
        }

        items(recommendations) { item ->
            RecommendationItem(item)
            Spacer(modifier = Modifier.height(8.dp))
        }
    }
}


@Composable
fun WidgetCard(
    title: String,
    modifier: Modifier = Modifier,
    icon: ImageVector? = null,
    content: @Composable ColumnScope.() -> Unit,
    gradient: Brush = Brush.verticalGradient(listOf(Color(0xFFFFF3E0), Color(0xFFFBF2ED)))
) {
    Column(
        modifier = modifier
            .height(140.dp)
            .background(brush = gradient, shape = RoundedCornerShape(16.dp))
            .padding(12.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.fillMaxWidth()
        ) {
            Text(
                title,
                textAlign = TextAlign.Center,
                fontSize = 16.sp,
                color = Color.Black,
                modifier = Modifier.weight(1f))
            icon?.let {
                Icon(imageVector = it, contentDescription = null, tint = Color(0xFFE86144))
            }
        }
        Spacer(modifier = Modifier.height(8.dp))
        content()
    }
}

@Composable
fun WidgetCalories(
    title: String,
    modifier: Modifier = Modifier,
    icon: ImageVector? = null,
    content: @Composable ColumnScope.() -> Unit,
    gradient: Brush = Brush.verticalGradient(listOf(Color(0xFFFFF3E0), Color(0xFFFBF2ED)))
) {
    Column(
        modifier = modifier
            .height(140.dp)
            .background(brush = gradient, shape = RoundedCornerShape(16.dp))
            .padding(12.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.fillMaxWidth()
        ) {
            Text(
                title,
                fontSize = 16.sp,
                color = Color(0xFFE86144),
                modifier = Modifier.weight(1f))
            icon?.let {
                Icon(imageVector = it, contentDescription = null, tint = Color(0xFFE86144))
            }
        }
        Spacer(modifier = Modifier.height(8.dp))
        content()
    }
}

@Composable
fun RecommendationItem(item: Recommendation) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .height(80.dp)
            .background(Color(0xFFFFF3E0), shape = RoundedCornerShape(12.dp))
            .padding(9.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Image(
            painter = painterResource(id = item.imageRes),
            contentDescription = item.title,
            contentScale = ContentScale.Crop,
            modifier = Modifier
                .size(100.dp)
                .clip(RoundedCornerShape(10.dp))
        )
        Spacer(modifier = Modifier.width(12.dp))
        Column {
            Text(item.title, fontSize = 16.sp, color = Color(0xFFE86144))
            Text("Duration : ${item.duration}", fontSize = 14.sp)
        }
    }
}

@Composable
fun FitBottomBar(
    onItemSelected: (String) -> Unit,
    currentRoute: String,
    onCentralClick: () -> Unit
) {
    Box {
        /* ---------- Barre de navigation ---------- */
        NavigationBar(
            containerColor = Color.White,
            tonalElevation = 8.dp,
            modifier = Modifier
                .fillMaxWidth()
                .height(65.dp)
                .align(Alignment.BottomCenter)
        ) {
            bottomNavItems.take(2).forEach {
                NavigationBarItem(
                    selected = currentRoute == it.route,
                    onClick = { onItemSelected(it.route) },
                    icon = {
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Icon(it.icon, contentDescription = it.label)
                            Text(it.label, fontSize = 10.sp)
                        }
                    },
                    modifier = Modifier.padding(top = 8.dp),
                    label = null
                )
            }


            Spacer(Modifier.weight(0.8f))

            bottomNavItems.drop(2).forEach {
                NavigationBarItem(
                    selected = currentRoute == it.route,
                    onClick = { onItemSelected(it.route) },
                    icon = {
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Icon(it.icon, contentDescription = it.label)
                            Text(it.label, fontSize = 10.sp)
                        }
                    },
                    modifier = Modifier.padding(top = 8.dp),
                    label = null,
                )
            }
        }

        /* ---------- Bouton central flottant + ---------- */
        Box(
            modifier = Modifier
                .size(56.dp)
                .align(Alignment.TopCenter)
                .offset(y = (-18).dp)
                .clip(CircleShape)
                .background(Color(0xFFE86144))
                .clickable { onCentralClick() },
            contentAlignment = Alignment.Center
        ) {
            Icon(Icons.Default.Add, contentDescription = "Add", tint = Color.White)
        }
    }
}


@Composable
fun CalorieProgress(current: Int, goal: Int) {
    val progress = current.toFloat() / goal.toFloat()
    Box(contentAlignment = Alignment.Center, modifier = Modifier.size(70.dp)) {
        CircularProgressIndicator(
            progress = progress.coerceIn(0f, 1f) ,
            strokeWidth = 9.dp,
            color = Color(0xFFE86144),
            trackColor = Color(0xFFFFD7C2),
            modifier = Modifier.fillMaxSize()
        )
        Text("${(progress * 100).toInt()}%", fontSize = 12.sp, fontWeight = FontWeight.Bold)
    }
}

@Composable
fun DividerVertical(
    modifier: Modifier = Modifier,
    color: Color = Color(0xFFE86144),
    thickness: Dp = 2.dp,
) {
    Divider(
        modifier = modifier
            .fillMaxHeight()
            .width(thickness),
        color = color,
        thickness = thickness
    )
}



@Composable
fun AccueilPageWithNavBar(navController: NavController, liveTrackingVm: LiveTrackingViewModel) {
    var currentRoute by remember { mutableStateOf("home") }

    val steps = liveTrackingVm.steps
    val calories = liveTrackingVm.calories
    val distance = liveTrackingVm.distanceKm

    Scaffold(
        bottomBar = {
            FitBottomBar(
                onItemSelected = { selectedRoute ->
                    currentRoute = selectedRoute
                },
                currentRoute = currentRoute,
                onCentralClick = {
                    currentRoute = "createPost"
                }

            )
        }
    ) { padding ->
        Box(modifier = Modifier.padding(12.dp)) {
            val currentUserId = FirebaseAuth.getInstance().currentUser?.uid

            when (currentRoute) {
                "home" -> AccueilScreen(
                    navController = navController,
                    steps = steps,
                    calories = calories,
                    distanceKm = distance)
                "music" -> MusicScreen(
                    navController = navController,
                    accessToken = getSpotifyAccessToken(LocalContext.current).toString(),
                    currentlyPlayingVm = CurrentlyPlayingViewModel(),
                    steps = steps,
                    calories = calories,
                    distanceKm = distance
                )
                "workout" -> WorkoutScreen(navController)
                "createPost" -> CreatePostScreen(navController = navController)
                "social" -> {
                    if (currentUserId != null) {
                        FeedScreen(
                            currentUid = currentUserId,
                            navController = navController
                        )
                    } else {
                        Text("Connexion requise")
                    }
                }
                else -> AccueilScreen(
                    navController = navController,
                    steps = steps,
                    calories = calories,
                    distanceKm = distance)

            }

        }
    }
}

