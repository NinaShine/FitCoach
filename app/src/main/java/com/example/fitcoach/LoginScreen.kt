package com.example.fitcoach

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
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
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import com.google.firebase.auth.FirebaseAuth
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Alarm
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material3.*
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController


@Composable
fun LoginScreen(
    navController: NavController,
    onLoginSuccess: () -> Unit,
    onGoToRegister: () -> Unit
) {
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var error by remember { mutableStateOf<String?>(null) }
    var passwordVisible by remember { mutableStateOf(false) }

    Box(modifier = Modifier.fillMaxSize()) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 24.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // ✅ Croix en haut
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 8.dp)
            ) {
                IconButton(
                    onClick = {
                        navController.navigate("onboarding") {
                            popUpTo("login") { inclusive = true }
                        }
                    },
                    modifier = Modifier.align(Alignment.CenterStart)
                ) {
                    Icon(
                        imageVector = Icons.Default.Close,
                        contentDescription = "Close",
                        tint = Color.Gray
                    )
                }

                Text(
                    text = "Sign In",
                    style = MaterialTheme.typography.headlineMedium,
                    modifier = Modifier.align(Alignment.Center)
                )
            }

            Text(
                text = "Let's sign in to your sporter account",
                fontSize = 20.sp,
                color = Color.Gray,
                style = MaterialTheme.typography.bodyMedium
            )

            Spacer(modifier = Modifier.height(24.dp))

            // ✅ Boutons sociaux
            Button(
                onClick = { /* Sign in with Apple */ },
                modifier = Modifier
                    .width(338.dp)
                    .height(68.dp),
                shape = RoundedCornerShape(16.dp),
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFF5F5F5))
            ) {
                Icon(
                    painter = painterResource(id = R.drawable.apple),
                    contentDescription = "Apple icon",
                    tint = Color.Unspecified,
                    modifier = Modifier.size(24.dp)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text("Sign in with Apple", color = Color.Black)
            }

            Spacer(modifier = Modifier.height(12.dp))

            Button(
                onClick = { /* Sign in with Google */ },
                modifier = Modifier
                    .width(338.dp)
                    .height(68.dp),
                shape = RoundedCornerShape(16.dp),
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFF5F5F5))
            ) {
                Icon(
                    painter = painterResource(id = R.drawable.gmail),
                    contentDescription = "gmail icon",
                    tint = Color.Unspecified,
                    modifier = Modifier.size(24.dp)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text("Sign in with Google", color = Color.Black)
            }

            Spacer(modifier = Modifier.height(20.dp))

            Row(verticalAlignment = Alignment.CenterVertically) {
                Divider(modifier = Modifier.weight(1f))
                Text("  Or sign in with email  ", color = Color.Gray)
                Divider(modifier = Modifier.weight(1f))
            }

            Spacer(modifier = Modifier.height(16.dp))

            Box(modifier = Modifier.width(338.dp).height(68.dp)) {
                OutlinedTextField(
                    value = email,
                    onValueChange = { email = it },
                    label = { Text("Email") },
                    leadingIcon = { Icon(Icons.Default.Email, contentDescription = null) },
                    modifier = Modifier.fillMaxSize(),
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

            Box(modifier = Modifier.width(338.dp).height(68.dp)) {
                OutlinedTextField(
                    value = password,
                    onValueChange = { password = it },
                    label = { Text("Password") },
                    leadingIcon = { Icon(Icons.Default.Lock, contentDescription = null) },
                    trailingIcon = {
                        IconButton(onClick = { passwordVisible = !passwordVisible }) {
                            Icon(
                                imageVector = if (passwordVisible) Icons.Default.Visibility else Icons.Default.VisibilityOff,
                                contentDescription = null
                            )
                        }
                    },
                    visualTransformation = if (passwordVisible) VisualTransformation.None else PasswordVisualTransformation(),
                    modifier = Modifier.fillMaxSize(),
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

            Button(
                onClick = {
                    FirebaseAuth.getInstance()
                        .signInWithEmailAndPassword(email, password)
                        .addOnCompleteListener { task ->
                            if (task.isSuccessful) {
                                onLoginSuccess()
                            } else {
                                error = task.exception?.message ?: "Erreur inconnue"
                            }
                        }
                },
                modifier = Modifier.width(338.dp).height(68.dp),
                shape = RoundedCornerShape(20.dp),
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFE86144))
            ) {
                Text("Sign In", color = Color.White)
            }

            Spacer(modifier = Modifier.height(12.dp))

            TextButton(onClick = { /* Forgot Password */ }) {
                Text("Forgot Password?", color = Color.Gray)
            }

            error?.let {
                Spacer(modifier = Modifier.height(8.dp))
                Text(it, color = Color.Red)
            }
        }

        // ✅ Lien en bas fixé
        Row(
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .padding(bottom = 16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text("Don't have account? ")
            Text(
                text = "Sign Up",
                color = Color(0xFFE86144),
                modifier = Modifier.clickable { onGoToRegister() }
            )
        }
    }
}
