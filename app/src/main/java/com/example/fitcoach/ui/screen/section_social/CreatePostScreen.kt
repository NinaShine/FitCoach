package com.example.fitcoach.ui.screen.section_social

import android.net.Uri
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Image
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import coil.compose.rememberAsyncImagePainter
import com.example.fitcoach.data.model.Post
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp
import kotlinx.coroutines.tasks.await

import kotlinx.coroutines.launch
import java.util.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CreatePostScreen(navController: NavController) {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()
    val uid = FirebaseAuth.getInstance().currentUser?.uid

    var textState by remember { mutableStateOf(TextFieldValue("")) }
    var selectedImageUri by remember { mutableStateOf<Uri?>(null) }
    var isLoading by remember { mutableStateOf(false) }

    val imagePickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri: Uri? -> selectedImageUri = uri }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Nouveau post") },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Retour")
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = Color.White)
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .padding(padding)
                .padding(16.dp)
                .fillMaxSize()
                .background(Color.White)
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Image(
                    painter = rememberAsyncImagePainter("https://placehold.co/40x40"), // Optionnel: avatar de l’utilisateur
                    contentDescription = null,
                    modifier = Modifier
                        .size(40.dp)
                        .clip(CircleShape)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text("Quoi de neuf ?", fontWeight = FontWeight.Bold, fontSize = 18.sp)
            }

            Spacer(modifier = Modifier.height(12.dp))

            OutlinedTextField(
                value = textState,
                onValueChange = { textState = it },
                placeholder = { Text("Exprime-toi...", color = Color.Gray) },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(180.dp),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = Color(0xFFFF5722),
                    unfocusedBorderColor = Color.LightGray
                )
            )

            Spacer(modifier = Modifier.height(12.dp))

            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                IconButton(onClick = { imagePickerLauncher.launch("image/*") }) {
                    Icon(Icons.Default.Image, contentDescription = "Ajouter une image", tint = Color(0xFFFF5722))
                }

                Text("Ajouter une image", color = Color(0xFFFF5722))
            }

            selectedImageUri?.let { uri ->
                Spacer(modifier = Modifier.height(12.dp))
                Image(
                    painter = rememberAsyncImagePainter(uri),
                    contentDescription = null,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(200.dp)
                        .clip(RoundedCornerShape(12.dp)),
                    contentScale = ContentScale.Crop
                )
            }

            Spacer(modifier = Modifier.weight(1f))

            Button(
                onClick = {
                    if (uid == null) {
                        Toast.makeText(context, "Non connecté", Toast.LENGTH_SHORT).show()
                        return@Button
                    }

                    isLoading = true
                    scope.launch {
                        try {
                            val imageUrl = selectedImageUri?.let { uri ->
                                val fileName = UUID.randomUUID().toString()
                                val ref = FirebaseStorage.getInstance().reference.child("post_images/$fileName.jpg")
                                ref.putFile(uri).await()
                                ref.downloadUrl.await().toString()
                            } ?: ""

                            val post = Post(
                                userId = uid,
                                content = textState.text,
                                imageUrl = imageUrl,
                                timestamp = System.currentTimeMillis()
                            )

                            FirebaseFirestore.getInstance()
                                .collection("posts")
                                .add(post)
                                .addOnSuccessListener {
                                    Toast.makeText(context, "Publié", Toast.LENGTH_SHORT).show()
                                    //navController.popBackStack()
                                }
                                .addOnFailureListener {
                                    Toast.makeText(context, "Erreur: ${it.message}", Toast.LENGTH_SHORT).show()
                                }
                        } catch (e: Exception) {
                            Toast.makeText(context, "Erreur: ${e.message}", Toast.LENGTH_SHORT).show()
                        } finally {
                            isLoading = false
                        }
                    }
                },
                modifier = Modifier.fillMaxWidth(),
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFFF5722))
            ) {
                Text(if (isLoading) "Publication..." else "Publier", color = Color.White)
            }
        }
    }
}

