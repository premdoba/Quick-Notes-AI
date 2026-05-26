package com.example.quicknotes.ui.screens

import android.content.Intent
import android.net.Uri
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.quicknotes.R
import org.bouncycastle.crypto.params.Blake3Parameters.context

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ContactUsScreen(
    navController: NavController
) {

    val gradient = Brush.verticalGradient(
        listOf(
            MaterialTheme.colorScheme.background,
            MaterialTheme.colorScheme.surface,
            MaterialTheme.colorScheme.primary.copy(alpha = 0.15f)
        )
    )
    val context = LocalContext.current

    Scaffold(

        topBar = {

            TopAppBar(

                title = {
                    Text(
                        text = "Contact Us",
                        fontWeight = FontWeight.Bold
                    )
                },

                navigationIcon = {

                    IconButton(
                        onClick = {
                            navController.popBackStack()
                        }
                    ) {

                        Icon(
                            painter = painterResource(R.drawable.baseline_arrow_back_ios_24),
                            contentDescription = "Back"
                        )
                    }
                }
            )
        }

    ) { padding ->

        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(gradient)
                .padding(padding)
                .padding(16.dp)
                .verticalScroll(rememberScrollState()),

            horizontalAlignment = Alignment.CenterHorizontally
        ) {

            Card(
                modifier = Modifier.fillMaxWidth(),

                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.surface
                )
            ) {

                Column(
                    modifier = Modifier.padding(18.dp)
                ) {

                    Text(
                        text = "Get In Touch",
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.Bold
                    )

                    Spacer(modifier = Modifier.height(14.dp))

                    Text(
                        text =
                            "For support, suggestions, bug reports, or feedback regarding QuickNotes AI, contact us using the details below."
                    )

                    Spacer(modifier = Modifier.height(18.dp))

                    Text(
                        text = "Developer",
                        fontWeight = FontWeight.Bold
                    )

                    Text(
                        text = "Prem Mahesh Doba"
                    )

                    Spacer(modifier = Modifier.height(10.dp))

                    Button(

                        onClick = {

                            val intent = Intent(
                                Intent.ACTION_SENDTO
                            ).apply {

                                data = Uri.parse(
                                    "mailto:premdoba4@gmail.com"
                                )

                                putExtra(
                                    Intent.EXTRA_SUBJECT,
                                    "QuickNotes Support"
                                )
                            }

                            context.startActivity(intent)
                        },

                        modifier = Modifier.fillMaxWidth()

                    ) {

                        Text(
                            text = "Contact Developer"
                        )
                    }

                    Spacer(modifier = Modifier.height(20.dp))

                    Text(
                        text =
                            "We appreciate your feedback and support.",
                        color = MaterialTheme.colorScheme.primary
                    )
                }
            }

            Spacer(modifier = Modifier.height(40.dp))
        }
    }
}