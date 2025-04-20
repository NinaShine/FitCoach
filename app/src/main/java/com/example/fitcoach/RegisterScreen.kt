package com.example.fitcoach

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material3.Button
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.userProfileChangeRequest
import com.google.firebase.firestore.FirebaseFirestore

@Composable
fun RegisterScreen(
    navController: NavController,
    onRegisterSuccess: () -> Unit
) {
    var username by remember { mutableStateOf("") }
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var error by remember { mutableStateOf<String?>(null) }
    var passwordVisible by remember { mutableStateOf(false) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 24.dp, vertical = 16.dp),
        verticalArrangement = Arrangement.Top,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {

        // ✅ Croix en haut à gauche
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 8.dp)
        ) {
            IconButton(
                onClick = {
                    navController.navigate("onboarding") {
                        popUpTo("register") { inclusive = true }
                    }
                },
                modifier = Modifier.align(Alignment.CenterStart)
            ) {
                Icon(
                    imageVector = Icons.Default.Close, // Remplace par icône de croix si dispo
                    contentDescription = "Close",
                    tint = Color.Gray
                )
            }
        }

        Spacer(modifier = Modifier.height(8.dp))

        Text(
            text = "Create an account",
            style = MaterialTheme.typography.headlineMedium
        )

        Spacer(modifier = Modifier.height(24.dp))

        // ✅ Input - Username
        Box(
            modifier = Modifier
                .width(338.dp)
                .height(68.dp)
        ) {
            OutlinedTextField(
                value = username,
                onValueChange = { username = it },
                label = { Text("Username") },
                leadingIcon = { Icon(Icons.Default.Person, contentDescription = null) },
                modifier = Modifier
                    .fillMaxSize(), // 👈 remplir la box 338x68
                shape = RoundedCornerShape(20.dp),
                colors = OutlinedTextFieldDefaults.colors(
                    unfocusedContainerColor = Color(0xFFFAEDE6),
                    unfocusedBorderColor = Color(0xFFFAEDE6),
                    focusedBorderColor = Color(0xFFE86144),
                    focusedLabelColor = Color(0xFFE86144),
                )
            )
        }


        Spacer(modifier = Modifier.height(12.dp))

        // ✅ Input - Email
        Box(
            modifier = Modifier
                .width(338.dp)
                .height(68.dp)
        ) {
            OutlinedTextField(
                value = email,
                onValueChange = { email = it },
                label = { Text("Email") },
                leadingIcon = { Icon(Icons.Default.Email, contentDescription = null) },
                modifier = Modifier.fillMaxSize(), // 👈 très important
                shape = RoundedCornerShape(20.dp),
                colors = OutlinedTextFieldDefaults.colors(
                    unfocusedContainerColor = Color(0xFFFAEDE6),
                    unfocusedBorderColor = Color(0xFFFAEDE6),
                    focusedBorderColor = Color(0xFFE86144),
                    focusedLabelColor = Color(0xFFE86144),
                )
            )
        }

        Spacer(modifier = Modifier.height(12.dp))


        Box(
            modifier = Modifier
                .width(338.dp)
                .height(68.dp)
        ) {
            OutlinedTextField(
                value = password,
                onValueChange = { password = it },
                label = { Text("Password") },
                leadingIcon = { Icon(Icons.Default.Lock, contentDescription = null) },
                trailingIcon = {
                    IconButton(onClick = { passwordVisible = !passwordVisible }) {
                        Icon(
                            imageVector = if (passwordVisible) Icons.Default.Visibility else Icons.Default.VisibilityOff,
                            contentDescription = if (passwordVisible) "Hide password" else "Show password"
                        )
                    }
                },
                visualTransformation = if (passwordVisible) VisualTransformation.None else PasswordVisualTransformation(),
                modifier = Modifier.fillMaxSize(), // 👈 important ici aussi
                shape = RoundedCornerShape(20.dp),
                colors = OutlinedTextFieldDefaults.colors(
                    unfocusedContainerColor = Color(0xFFFAEDE6),
                    unfocusedBorderColor = Color(0xFFFAEDE6),
                    focusedBorderColor = Color(0xFFE86144),
                    focusedLabelColor = Color(0xFFE86144),
                )
            )
        }


        Spacer(modifier = Modifier.height(24.dp))

        // ✅ Bouton "Sign Up"
        Button(
            onClick = {
                FirebaseAuth.getInstance()
                    .createUserWithEmailAndPassword(email, password)
                    .addOnCompleteListener { task ->
                        if (task.isSuccessful) {
                            val user = FirebaseAuth.getInstance().currentUser
                            val profileUpdates = userProfileChangeRequest {
                                displayName = username
                            }
                            user?.updateProfile(profileUpdates)
                                ?.addOnCompleteListener { updateTask ->
                                    if (updateTask.isSuccessful) {
                                        onRegisterSuccess()
                                    } else {
                                        error = "Compte créé, mais impossible d'ajouter le nom."
                                    }
                                }
                        } else {
                            error = task.exception?.message ?: "Erreur lors de l'inscription"
                        }
                    }
            },
            modifier = Modifier
                .width(338.dp)
                .height(68.dp),
            shape = RoundedCornerShape(20.dp),
            colors = androidx.compose.material3.ButtonDefaults.buttonColors(
                containerColor = Color(0xFFE86144)
            )
        ) {
            Text("Sign Up", color = Color.White, fontSize = MaterialTheme.typography.bodyLarge.fontSize)
        }

        Spacer(modifier = Modifier.height(8.dp))

        // ✅ Lien Sign In en bas
        Row(
            modifier = Modifier.align(Alignment.CenterHorizontally),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "Already have an account?",
                color = Color.Black,
                style = MaterialTheme.typography.bodyMedium
            )
            TextButton(
                onClick = {
                    navController.navigate("login") {
                        popUpTo("register") { inclusive = true }
                    }
                },
                contentPadding = PaddingValues(4.dp)
            ) {
                Text(
                    text = "Sign In",
                    color = Color(0xFFE86144), // ✅ Orange Figma
                    style = MaterialTheme.typography.bodyMedium
                )
            }
        }

        error?.let {
            Spacer(modifier = Modifier.height(8.dp))
            Text(it, color = Color.Red)
        }
    }
}

