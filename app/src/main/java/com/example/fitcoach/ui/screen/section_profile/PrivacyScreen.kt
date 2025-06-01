package com.example.fitcoach.ui.screen

import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.google.firebase.auth.FirebaseAuth

@Composable
fun PrivacyScreen(navController: NavController) {

    val auth = FirebaseAuth.getInstance()
    val context = LocalContext.current
    val currentUser = auth.currentUser

    var newEmail by remember { mutableStateOf("") }
    var newPassword by remember { mutableStateOf("") }
    var message by remember { mutableStateOf<String?>(null) }
    var passwordVisible by remember { mutableStateOf(false) }


    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFFFEF3ED))
            .padding(24.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            IconButton(onClick = { navController.popBackStack() }) {
                Icon(Icons.Default.ArrowBack, contentDescription = "Back")
            }
            Text(
                text = "Privacy",
                style = MaterialTheme.typography.titleLarge,
                modifier = Modifier.weight(1f),
                textAlign = TextAlign.Center
            )
            TextButton(onClick = {
                message = null
                currentUser?.let { user ->
                    if (newEmail.isNotBlank()) {
                        user.verifyBeforeUpdateEmail(newEmail)
                            .addOnSuccessListener {
                                message = "Email updated successfully"
                                Toast.makeText(context, "Email mis à jour", Toast.LENGTH_SHORT)
                                    .show()
                            }
                            .addOnFailureListener {
                                message = "Error updating email: ${it.message}"
                                Toast.makeText(
                                    context,
                                    "Erreur email : ${it.message}",
                                    Toast.LENGTH_LONG
                                ).show()
                            }
                    }

                    if (newPassword.isNotBlank()) {
                        user.updatePassword(newPassword)
                            .addOnSuccessListener {
                                message = "Password updated successfully"

                                Toast.makeText(
                                    context,
                                    "Mot de passe mis à jour",
                                    Toast.LENGTH_SHORT
                                )
                                    .show()
                            }
                            .addOnFailureListener {
                                message = "Error updating password: ${it.message}"

                                Toast.makeText(
                                    context,
                                    "Erreur mot de passe : ${it.message}",
                                    Toast.LENGTH_LONG
                                ).show()
                            }
                    }
                }
            }) {
                Text("Save", color = Color(0xFFE86144))
            }
        }

        Spacer(modifier = Modifier.height(24.dp))

        OutlinedTextField(
            value = newEmail,
            onValueChange = { newEmail = it },
            label = { Text("Change email address") },
            modifier = Modifier.fillMaxWidth(),
            singleLine = true
        )

        Spacer(modifier = Modifier.height(24.dp))

        OutlinedTextField(
            value = newPassword,
            onValueChange = { newPassword = it },
            label = { Text("Change password") },
            modifier = Modifier.fillMaxWidth(),
            singleLine = true,
            visualTransformation = if (passwordVisible) VisualTransformation.None else PasswordVisualTransformation(),
            trailingIcon = {
                IconButton(onClick = { passwordVisible = !passwordVisible }) {
                    Icon(
                        imageVector = if (passwordVisible) Icons.Default.Visibility else Icons.Default.VisibilityOff,
                        contentDescription = if (passwordVisible) "Hide password" else "Show password"
                    )
                }
            }
        )

        Spacer(modifier = Modifier.height(40.dp))



        Box(
            modifier = Modifier
                .fillMaxWidth(),
            contentAlignment = Alignment.Center
        ) {
            TextButton(onClick = {
                currentUser?.delete()
                    ?.addOnSuccessListener {
                        FirebaseAuth.getInstance().signOut()
                        navController.navigate("login") {
                            popUpTo("accueil") { inclusive = true }
                        }
                    }
                    ?.addOnFailureListener {
                        Toast.makeText(
                            context,
                            "Error deleting account: ${it.message}",
                            Toast.LENGTH_LONG
                        ).show()
                    }
            }) {
                Text("Delete Account", color = Color.Red)
            }

            message?.let {
                Spacer(modifier = Modifier.height(16.dp))
                Text(text = it, color = if ("successfully" in it) Color.Green else Color.Red)
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun PrivacyScreenPreview() {
    PrivacyScreen(navController = NavController(LocalContext.current))
}