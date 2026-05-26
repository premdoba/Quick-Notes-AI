package com.example.quicknotes.ui.screens

import android.app.Activity
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.unit.dp
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.navigation.NavController
import com.google.android.gms.auth.api.signin.*
import com.google.android.gms.common.api.ApiException
import com.google.firebase.auth.*
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import com.example.quicknotes.R
import com.example.quicknotes.ui.Routes

@Composable
fun LoginTestScreen(navController: NavController) {

    val context = LocalContext.current
    val activity = context as Activity
    val auth = Firebase.auth

    var user by remember { mutableStateOf(auth.currentUser) }
    var isLoading by remember { mutableStateOf(false) }

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
                        user = auth.currentUser

                        navController.navigate(Routes.Generate.route) {
                            popUpTo(Routes.Login.route) { inclusive = true }
                        }
                    }
                }

        } catch (e: Exception) {
            isLoading = false
            e.printStackTrace()
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
            .background(gradient),
        contentAlignment = Alignment.Center
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
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSurface
                )

                Spacer(modifier = Modifier.height(6.dp))

                Text(
                    text = "Your AI-powered study companion",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )

                Spacer(modifier = Modifier.height(28.dp))

                Button(
                    onClick = {
                        isLoading = true
                        val signInIntent = googleSignInClient.signInIntent
                        launcher.launch(signInIntent)
                    },
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(14.dp),
                    enabled = !isLoading,
                    colors = ButtonDefaults.buttonColors(
                        containerColor = MaterialTheme.colorScheme.primary
                    )
                ) {

                    if (isLoading) {

                        Row(
                            verticalAlignment = Alignment.CenterVertically
                        ) {

                            CircularProgressIndicator(
                                modifier = Modifier.size(18.dp),
                                strokeWidth = 2.dp,
                                color = MaterialTheme.colorScheme.onPrimary
                            )

                            Spacer(modifier = Modifier.width(10.dp))

                            Text("Signing in...")
                        }

                    } else {
                        Text("Continue with Google")
                    }
                }
            }
        }
    }
}