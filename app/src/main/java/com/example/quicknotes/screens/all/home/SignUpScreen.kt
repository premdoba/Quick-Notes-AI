package com.example.quicknotes.ui.screens

import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.quicknotes.ui.Routes
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase

@Composable
fun SignupScreen(navController: NavController) {

    val context = LocalContext.current
    val auth = Firebase.auth

    var email by remember {
        mutableStateOf("")
    }

    var password by remember {
        mutableStateOf("")
    }

    var confirmPassword by remember {
        mutableStateOf("")
    }

    var isLoading by remember {
        mutableStateOf(false)
    }

    val gradient = Brush.verticalGradient(
        listOf(
            MaterialTheme.colorScheme.background,
            MaterialTheme.colorScheme.surface,
            MaterialTheme.colorScheme.primary.copy(alpha = 0.25f)
        )
    )

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(gradient)
    ) {

        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(vertical = 24.dp),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {

        Card(
            modifier = Modifier
                .padding(24.dp)
                .fillMaxWidth()
                .widthIn(max = 420.dp),
            shape = RoundedCornerShape(28.dp),
            colors = CardDefaults.cardColors(
                containerColor = MaterialTheme.colorScheme.surface
            )
        ) {

            Column(
                modifier = Modifier.padding(28.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {

                Text(
                    text = "Create Account",
                    style = MaterialTheme.typography.headlineMedium,
                    fontWeight = FontWeight.Bold
                )

                Spacer(modifier = Modifier.height(28.dp))

                OutlinedTextField(
                    value = email,
                    onValueChange = {
                        email = it
                    },
                    label = {
                        Text("Email")
                    },
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true,
                    shape = RoundedCornerShape(14.dp)
                )

                Spacer(modifier = Modifier.height(14.dp))

                OutlinedTextField(
                    value = password,
                    onValueChange = {
                        password = it
                    },
                    label = {
                        Text("Password")
                    },
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true,
                    visualTransformation = PasswordVisualTransformation(),
                    shape = RoundedCornerShape(14.dp)
                )

                Spacer(modifier = Modifier.height(14.dp))

                OutlinedTextField(
                    value = confirmPassword,
                    onValueChange = {
                        confirmPassword = it
                    },
                    label = {
                        Text("Confirm Password")
                    },
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true,
                    visualTransformation = PasswordVisualTransformation(),
                    shape = RoundedCornerShape(14.dp)
                )

                Spacer(modifier = Modifier.height(22.dp))

                Button(
                    onClick = {

                        when {

                            email.isBlank() -> {

                                Toast.makeText(
                                    context,
                                    "Email cannot be empty",
                                    Toast.LENGTH_LONG
                                ).show()

                                return@Button
                            }

                            password.isBlank() -> {

                                Toast.makeText(
                                    context,
                                    "Password cannot be empty",
                                    Toast.LENGTH_LONG
                                ).show()

                                return@Button
                            }

                            confirmPassword.isBlank() -> {

                                Toast.makeText(
                                    context,
                                    "Confirm Password cannot be empty",
                                    Toast.LENGTH_LONG
                                ).show()

                                return@Button
                            }

                            password != confirmPassword -> {

                                Toast.makeText(
                                    context,
                                    "Passwords do not match",
                                    Toast.LENGTH_LONG
                                ).show()

                                return@Button
                            }
                        }

                        isLoading = true

                        auth.createUserWithEmailAndPassword(
                            email,
                            password
                        ).addOnCompleteListener {

                            isLoading = false

                            if (it.isSuccessful) {

                                Toast.makeText(
                                    context,
                                    "Account Created",
                                    Toast.LENGTH_SHORT
                                ).show()

                                navController.navigate(Routes.Generate.route) {
                                    popUpTo(Routes.Login.route) {
                                        inclusive = true
                                    }
                                }

                            } else {

                                Toast.makeText(
                                    context,
                                    it.exception?.message ?: "Signup Failed",
                                    Toast.LENGTH_LONG
                                ).show()
                            }
                        }
                    },
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(14.dp),
                    enabled = !isLoading
                ) {

                    if (isLoading) {

                        CircularProgressIndicator(
                            modifier = Modifier.size(18.dp),
                            strokeWidth = 2.dp
                        )

                    } else {

                        Text("Create Account")
                    }
                }

                Spacer(modifier = Modifier.height(18.dp))

                TextButton(
                    onClick = {
                        navController.popBackStack()
                    }
                ) {

                    Text("Already have an account? Sign In")
                }
            }
        }
    }
    }
}