    package com.example.quicknotes.ui.screens

    import android.app.Activity
    import android.widget.Toast
    import androidx.activity.compose.rememberLauncherForActivityResult
    import androidx.activity.result.contract.ActivityResultContracts
    import androidx.compose.foundation.BorderStroke
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
    import androidx.compose.ui.graphics.Color
    import androidx.compose.ui.platform.LocalContext
    import androidx.compose.ui.res.stringResource
    import androidx.compose.ui.text.font.FontWeight
    import androidx.compose.ui.text.input.PasswordVisualTransformation
    import androidx.compose.ui.unit.dp
    import androidx.navigation.NavController
    import com.example.quicknotes.R
    import com.example.quicknotes.ui.Routes
    import com.google.android.gms.auth.api.signin.*
    import com.google.android.gms.common.api.ApiException
    import com.google.firebase.auth.GoogleAuthProvider
    import com.google.firebase.auth.ktx.auth
    import com.google.firebase.ktx.Firebase

    @Composable
    fun LoginTestScreen(navController: NavController) {

        val context = LocalContext.current
        val activity = context as Activity
        val auth = Firebase.auth

        var isLoading by remember { mutableStateOf(false) }

        var email by remember {
            mutableStateOf("")
        }

        var password by remember {
            mutableStateOf("")
        }

        val webClientId = stringResource(R.string.default_web_client_id)

        val googleSignInClient = remember(webClientId) {

            val gso = GoogleSignInOptions.Builder(
                GoogleSignInOptions.DEFAULT_SIGN_IN
            )
                .requestIdToken(webClientId)
                .requestEmail()
                .build()

            GoogleSignIn.getClient(context, gso)
        }

        val launcher = rememberLauncherForActivityResult(
            contract = ActivityResultContracts.StartActivityForResult()
        ) { result ->

            val task = GoogleSignIn.getSignedInAccountFromIntent(result.data)

            try {

                val account = task.getResult(ApiException::class.java)

                val credential = GoogleAuthProvider.getCredential(
                    account.idToken,
                    null
                )

                auth.signInWithCredential(credential)
                    .addOnCompleteListener(activity) {

                        isLoading = false

                        if (it.isSuccessful) {

                            navController.navigate(Routes.Generate.route) {
                                popUpTo(Routes.Login.route) {
                                    inclusive = true
                                }
                            }

                        } else {

                            Toast.makeText(
                                context,
                                it.exception?.message ?: "Google Sign-In Failed",
                                Toast.LENGTH_LONG
                            ).show()
                        }
                    }

            } catch (e: Exception) {

                isLoading = false
                e.printStackTrace()

                Toast.makeText(
                    context,
                    e.message,
                    Toast.LENGTH_LONG
                ).show()
            }
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
                    .verticalScroll(rememberScrollState()),
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
                ),
                elevation = CardDefaults.cardElevation(10.dp)
            ) {

                Column(
                    modifier = Modifier.padding(28.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {

                    Text(
                        text = "QuickNotes AI",
                        style = MaterialTheme.typography.headlineMedium,
                        fontWeight = FontWeight.Bold
                    )

                    Spacer(modifier = Modifier.height(6.dp))

                    Text(
                        text = "Your AI-powered study companion",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
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
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth(),
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
                        singleLine = true,
                        visualTransformation = PasswordVisualTransformation(),
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(14.dp)
                    )

                    Spacer(modifier = Modifier.height(20.dp))

                    Button(
                        onClick = {

                            if (email.isBlank() || password.isBlank()) {

                                Toast.makeText(
                                    context,
                                    "Enter email and password",
                                    Toast.LENGTH_SHORT
                                ).show()

                                return@Button
                            }

                            isLoading = true

                            auth.signInWithEmailAndPassword(
                                email,
                                password
                            ).addOnCompleteListener {

                                isLoading = false

                                if (it.isSuccessful) {

                                    navController.navigate(Routes.Generate.route) {
                                        popUpTo(Routes.Login.route) {
                                            inclusive = true
                                        }
                                    }

                                } else {

                                    Toast.makeText(
                                        context,
                                        it.exception?.message ?: "Login Failed",
                                        Toast.LENGTH_LONG
                                    ).show()
                                }
                            }
                        },
                        modifier = Modifier.fillMaxWidth(),
                        enabled = !isLoading,
                        shape = RoundedCornerShape(14.dp)
                    ) {

                        Text("Sign In")
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    Text(
                        text = "OR",
                        style = MaterialTheme.typography.bodyMedium
                    )

                    Spacer(modifier = Modifier.height(16.dp))

                    OutlinedButton(
                        onClick = {

                            isLoading = true

                            val signInIntent =
                                googleSignInClient.signInIntent

                            launcher.launch(signInIntent)
                        },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(56.dp),
                        enabled = !isLoading,
                        shape = RoundedCornerShape(16.dp),

                        border = BorderStroke(
                            1.5.dp,
                            Color(0xFF00D9FF)
                        ),

                        colors = ButtonDefaults.outlinedButtonColors(
                            containerColor = MaterialTheme.colorScheme.surface
                        )
                    ) {

                        if (isLoading) {

                            CircularProgressIndicator(
                                modifier = Modifier.size(20.dp),
                                strokeWidth = 2.dp
                            )

                        } else {

                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.Center
                            ) {

                                Text(
                                    text = "G",
                                    style = MaterialTheme.typography.titleMedium,
                                    fontWeight = FontWeight.Bold,
                                    color = MaterialTheme.colorScheme.primary
                                )

                                Spacer(modifier = Modifier.width(12.dp))

                                Text(
                                    text = "Continue with Google",
                                    style = MaterialTheme.typography.bodyLarge,
                                    fontWeight = FontWeight.Medium
                                )
                            }
                        }
                    }
                    Spacer(modifier = Modifier.height(20.dp))

                    TextButton(
                        onClick = {
                            navController.navigate(Routes.Signup.route)
                        }
                    ) {

                        Text("Don't have an account? Sign Up")
                    }
                }
            }
        }
        }
    }