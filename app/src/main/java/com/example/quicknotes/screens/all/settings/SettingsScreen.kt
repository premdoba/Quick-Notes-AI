package com.example.quicknotes.ui.screens

import android.content.Intent
import android.net.Uri
import androidx.activity.compose.BackHandler
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Info
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.quicknotes.R
import com.example.quicknotes.settings.SettingsViewModel
import com.example.quicknotes.ui.Routes
import com.example.quicknotes.viewmodel.StudyViewModel
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.firebase.auth.FirebaseAuth
import org.bouncycastle.crypto.params.Blake3Parameters.context

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingsScreen(
    navController: NavController,
    vm: SettingsViewModel,
    vm2: StudyViewModel
) {

    val configuration = LocalConfiguration.current
    val screenWidth = configuration.screenWidthDp

    var showClearDialog by remember { mutableStateOf(false) }

    val isTablet = screenWidth >= 600
    val context = LocalContext.current

    val googleSignInClient = remember {
        GoogleSignIn.getClient(
            context,
            GoogleSignInOptions.DEFAULT_SIGN_IN
        )
    }

    val paddingSize = if (isTablet) 32.dp else 16.dp

    val maxContentWidth = 720.dp

    val gradient = Brush.verticalGradient(
        listOf(
            MaterialTheme.colorScheme.background,
            MaterialTheme.colorScheme.surface,
            MaterialTheme.colorScheme.primary.copy(alpha = 0.12f)
        )
    )

    val educationLevels = listOf(
        "5th-8th",
        "9th-10th",
        "11th-12th",
        "Graduation",
        "Post Graduation"
    )

    val mcqLevels = listOf(
        "Easy",
        "Medium",
        "Hard"
    )

    val themeOptions = listOf(
        "Auto",
        "Light",
        "Dark"
    )

    val selectedEducationLevel by vm.education.collectAsState()

    val selectedMcqLevel by vm.mcq.collectAsState()

    val selectedTheme by vm.theme.collectAsState()

    if (showClearDialog) {
        AlertDialog(
            onDismissRequest = { showClearDialog = false },
            title = { Text("Logout?") },
            text = { Text("Are you sure you want to logout?") },
            confirmButton = {
                TextButton(onClick = {
                    FirebaseAuth.getInstance().signOut()
                    googleSignInClient.signOut()

                    navController.navigate(Routes.Login.route) {
                        popUpTo(0)
                        launchSingleTop = true
                    }

                    showClearDialog = false
                }) {
                    Text("Logout", color = MaterialTheme.colorScheme.error)
                }
            },
            dismissButton = {
                TextButton(onClick = { showClearDialog = false }) {
                    Text("Cancel")
                }
            }
        )
    }

    BackHandler {
        navController.navigate(Routes.Generate.route) {
            popUpTo(navController.graph.startDestinationId) {
                inclusive = false
            }
            launchSingleTop = true
        }
    }

    Scaffold(

        topBar = {

            TopAppBar(

                title = {

                    Column {

                        Text(
                            text = "Settings",
                            style = MaterialTheme.typography.titleLarge,
                            fontWeight = FontWeight.Bold
                        )

                        Text(
                            text = "Customize your QuickNotes experience",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f)
                        )
                    }
                },

                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.surface.copy(alpha = 0.95f)
                )
            )
        },

        bottomBar = {

            if (!isTablet) {

                BottomAppBar(
                    containerColor = MaterialTheme.colorScheme.surface.copy(alpha = 0.96f)
                ) {

                    NavigationBarItem(

                        selected = false,

                        onClick = {
                            navController.navigate(Routes.Generate.route)
                        },

                        icon = {
                            Icon(
                                painter = painterResource(R.drawable.outline_home_24),
                                contentDescription = "Home"
                            )
                        },

                        label = {
                            Text("Home")
                        },

                        colors = NavigationBarItemDefaults.colors(

                            selectedIconColor = MaterialTheme.colorScheme.onPrimary,
                            selectedTextColor = MaterialTheme.colorScheme.primary,

                            indicatorColor = MaterialTheme.colorScheme.primary,

                            unselectedIconColor = MaterialTheme.colorScheme.onSurfaceVariant,
                            unselectedTextColor = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    )

                    NavigationBarItem(

                        selected = false,

                        onClick = {
                            navController.navigate(Routes.Downloads.route)
                        },

                        icon = {
                            Icon(
                                painter = painterResource(R.drawable.outline_download_24),
                                contentDescription = "Downloads"
                            )
                        },

                        label = {
                            Text("Downloads")
                        },

                        colors = NavigationBarItemDefaults.colors(

                            selectedIconColor = MaterialTheme.colorScheme.onPrimary,
                            selectedTextColor = MaterialTheme.colorScheme.primary,

                            indicatorColor = MaterialTheme.colorScheme.primary,

                            unselectedIconColor = MaterialTheme.colorScheme.onSurfaceVariant,
                            unselectedTextColor = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    )

                    NavigationBarItem(
                        selected = false,
                        onClick = { navController.navigate(Routes.QuickTodo.route) },
                        icon = {
                            Icon(
                                painter = painterResource(R.drawable.baseline_checklist_24),
                                contentDescription = "QuickTodo"
                            )
                        },
                        label = { Text("Todo") },
                        colors = NavigationBarItemDefaults.colors(

                            selectedIconColor = MaterialTheme.colorScheme.onPrimary,
                            selectedTextColor = MaterialTheme.colorScheme.primary,

                            indicatorColor = MaterialTheme.colorScheme.primary,

                            unselectedIconColor = MaterialTheme.colorScheme.onSurfaceVariant,
                            unselectedTextColor = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    )

                    NavigationBarItem(

                        selected = true,

                        onClick = {},

                        icon = {
                            Icon(
                                painter = painterResource(R.drawable.outline_settings_24),
                                contentDescription = "Settings"
                            )
                        },

                        label = {
                            Text("Settings")
                        },

                        colors = NavigationBarItemDefaults.colors(

                            selectedIconColor = MaterialTheme.colorScheme.onPrimary,
                            selectedTextColor = MaterialTheme.colorScheme.primary,

                            indicatorColor = MaterialTheme.colorScheme.primary,

                            unselectedIconColor = MaterialTheme.colorScheme.onSurfaceVariant,
                            unselectedTextColor = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    )
                }
            }
        }

    ) { paddingValues ->

        Row(
            modifier = Modifier
                .fillMaxSize()
                .background(gradient)
                .padding(paddingValues)
        ) {

            if (isTablet) {

                NavigationRail(
                    containerColor = MaterialTheme.colorScheme.surface.copy(alpha = 0.95f)
                ) {

                    NavigationRailItem(

                        selected = false,

                        onClick = {
                            navController.navigate(Routes.Generate.route)
                        },

                        icon = {
                            Icon(
                                Icons.Default.Home,
                                contentDescription = "Home"
                            )
                        },

                        label = {
                            Text("Home")
                        },

                        colors = NavigationRailItemDefaults.colors(

                            selectedIconColor = MaterialTheme.colorScheme.onPrimary,
                            selectedTextColor = MaterialTheme.colorScheme.primary,

                            indicatorColor = MaterialTheme.colorScheme.primary,

                            unselectedIconColor = MaterialTheme.colorScheme.onSurfaceVariant,
                            unselectedTextColor = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    )

                    NavigationRailItem(

                        selected = false,

                        onClick = {
                            navController.navigate(Routes.Downloads.route)
                        },

                        icon = {
                            Icon(
                                painter = painterResource(R.drawable.baseline_history),
                                contentDescription = "History"
                            )
                        },

                        label = {
                            Text("History")
                        },

                        colors = NavigationRailItemDefaults.colors(

                            selectedIconColor = MaterialTheme.colorScheme.onPrimary,
                            selectedTextColor = MaterialTheme.colorScheme.primary,

                            indicatorColor = MaterialTheme.colorScheme.primary,

                            unselectedIconColor = MaterialTheme.colorScheme.onSurfaceVariant,
                            unselectedTextColor = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    )

                    NavigationRailItem(
                        selected = false,
                        onClick = { navController.navigate(Routes.QuickTodo.route) },
                        icon = {
                            Icon(
                                painter = painterResource(R.drawable.baseline_checklist_24),
                                contentDescription = "QuickTodo"
                            )
                        },
                        label = { Text("Todo") },
                        colors = NavigationRailItemDefaults.colors(

                            selectedIconColor = MaterialTheme.colorScheme.onPrimary,
                            selectedTextColor = MaterialTheme.colorScheme.primary,

                            indicatorColor = MaterialTheme.colorScheme.primary,

                            unselectedIconColor = MaterialTheme.colorScheme.onSurfaceVariant,
                            unselectedTextColor = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    )

                    NavigationRailItem(

                        selected = true,

                        onClick = {},

                        icon = {
                            Icon(
                                painter = painterResource(R.drawable.outline_settings_24),
                                contentDescription = "Settings"
                            )
                        },

                        label = {
                            Text("Settings")
                        },

                        colors = NavigationRailItemDefaults.colors(

                            selectedIconColor = MaterialTheme.colorScheme.onPrimary,
                            selectedTextColor = MaterialTheme.colorScheme.primary,

                            indicatorColor = MaterialTheme.colorScheme.primary,

                            unselectedIconColor = MaterialTheme.colorScheme.onSurfaceVariant,
                            unselectedTextColor = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    )
                }
            }

            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingSize)
                    .verticalScroll(rememberScrollState()),

                horizontalAlignment = Alignment.CenterHorizontally
            ) {

                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .widthIn(max = maxContentWidth)
                ) {

                    SettingsSectionTitle("Preferences")

                    SettingsDropdownItem(
                        title = "Default Education Level",
                        subtitle = "Used automatically while generating notes",

                        options = educationLevels,

                        selectedValue = selectedEducationLevel,

                        onSelected = {

                            vm.saveEducation(it)
                        }
                    )

                    Spacer(modifier = Modifier.height(18.dp))

                    SettingsDropdownItem(
                        title = "Default MCQ Difficulty",
                        subtitle = "Preferred difficulty for generated quizzes",

                        options = mcqLevels,

                        selectedValue = selectedMcqLevel,

                        onSelected = {

                            vm.saveMcq(it)
                        }
                    )

                    Spacer(modifier = Modifier.height(18.dp))

                    SettingsDropdownItem(
                        title = "App Theme",
                        subtitle = "Auto uses your device theme",

                        options = themeOptions,

                        selectedValue = selectedTheme,

                        onSelected = {

                            vm.saveTheme(it)
                        }
                    )

                    Spacer(modifier = Modifier.height(22.dp))

                    SettingsSectionTitle("Legal & Information")

                    SettingsItem(
                        icon = painterResource(R.drawable.outline_privacy_tip_24),
                        title = "Privacy Policy",
                        subtitle = "Learn how your data is handled",
                        onClick = {
                            val intent = Intent(
                                Intent.ACTION_VIEW,
                                Uri.parse("https://premdoba.github.io/Quick-Notes-AI/")
                            )
                            context.startActivity(intent)
                        }
                    )

                    SettingsItem(
                        icon = painterResource(R.drawable.outline_gavel_24),
                        title = "Terms & Conditions",
                        subtitle = "Rules and app usage policies",
                        onClick = {
                            navController.navigate(Routes.Terms.route)
                        }
                    )

                    SettingsItem(
                        icon = painterResource(R.drawable.outline_smart_toy_24),
                        title = "AI Disclaimer",
                        subtitle = "About AI-generated responses",
                        onClick = {
                            navController.navigate(Routes.AiDisclaimer.route)
                        }
                    )

                    Spacer(modifier = Modifier.height(22.dp))

                    SettingsSectionTitle("Support")

                    SettingsItem(
                        icon = painterResource(R.drawable.outline_mail_24),
                        title = "Contact Us",
                        subtitle = "Get support or send feedback",
                        onClick = {
                            navController.navigate(Routes.Contact.route)
                        }
                    )

                    SettingsItem(
                        icon = painterResource(R.drawable.outline_star_24),
                        title = "Rate App",
                        subtitle = "Support us on Play Store",
                        onClick = {

                        }
                    )

                    SettingsSectionTitle("Account")

                    val googleSignInClient = GoogleSignIn.getClient(
                        context,
                        GoogleSignInOptions.DEFAULT_SIGN_IN
                    )

                    SettingsItem(
                        icon = painterResource(R.drawable.baseline_logout_24),
                        title = "Logout",
                        subtitle = "Sign out from your account",

                        onClick = {
                            showClearDialog = true
                        }
                    )

                    Spacer(modifier = Modifier.height(22.dp))

                    Card(

                        modifier = Modifier.fillMaxWidth(),

                        shape = RoundedCornerShape(24.dp),

                        colors = CardDefaults.cardColors(
                            containerColor = MaterialTheme.colorScheme.surface.copy(alpha = 0.92f)
                        ),

                        border = BorderStroke(
                            1.dp,
                            MaterialTheme.colorScheme.outline.copy(alpha = 0.2f)
                        )
                    ) {

                        Column(
                            modifier = Modifier.padding(20.dp).fillMaxWidth(),

                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {

                            Icon(
                                imageVector = Icons.Default.Info,
                                contentDescription = null,
                                tint = MaterialTheme.colorScheme.primary,
                                modifier = Modifier.size(30.dp)
                            )

                            Spacer(modifier = Modifier.height(10.dp))

                            Text(
                                text = "QuickNotes AI",
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.Bold
                            )

                            Spacer(modifier = Modifier.height(4.dp))

                            Text(
                                text = "© 2026 Prem Doba. All rights reserved.",
                                style = MaterialTheme.typography.bodyMedium,
                                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f)
                            )

                            Spacer(modifier = Modifier.height(8.dp))

                            Text(
                                text = "Your intelligent study companion for notes, quizzes, and learning.",
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(90.dp))
                }
            }
        }
    }
}

@Composable
fun SettingsSectionTitle(title: String) {

    Text(
        text = title,
        style = MaterialTheme.typography.titleMedium,
        fontWeight = FontWeight.Bold,
        modifier = Modifier.padding(bottom = 10.dp)
    )
}

@Composable
fun SettingsItem(
    icon: Painter,
    title: String,
    subtitle: String,
    onClick: () -> Unit
) {

    Card(

        modifier = Modifier
            .fillMaxWidth()
            .padding(bottom = 12.dp)
            .clickable {
                onClick()
            },

        shape = RoundedCornerShape(22.dp),

        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface.copy(alpha = 0.92f)
        ),

        border = BorderStroke(
            1.dp,
            MaterialTheme.colorScheme.outline.copy(alpha = 0.2f)
        )
    ) {

        Row(
            modifier = Modifier.padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {

            Box(
                modifier = Modifier
                    .background(
                        MaterialTheme.colorScheme.primary.copy(alpha = 0.12f),
                        RoundedCornerShape(14.dp)
                    )
                    .padding(12.dp)
            ) {

                Icon(
                    painter = icon,
                    contentDescription = title,
                    tint = MaterialTheme.colorScheme.primary
                )
            }

            Spacer(modifier = Modifier.size(14.dp))

            Column(
                modifier = Modifier.weight(1f)
            ) {

                Text(
                    text = title,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.SemiBold
                )

                Spacer(modifier = Modifier.height(3.dp))

                Text(
                    text = subtitle,
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f)
                )
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingsDropdownItem(
    title: String,
    subtitle: String,
    options: List<String>,
    selectedValue: String,
    onSelected: (String) -> Unit
) {

    var expanded by remember {
        mutableStateOf(false)
    }

    Card(

        modifier = Modifier
            .fillMaxWidth()
            .padding(bottom = 12.dp),

        shape = RoundedCornerShape(22.dp),

        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface.copy(alpha = 0.92f)
        ),

        border = BorderStroke(
            1.dp,
            MaterialTheme.colorScheme.outline.copy(alpha = 0.2f)
        )
    ) {

        Column(
            modifier = Modifier.padding(16.dp)
        ) {

            Text(
                text = title,
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.SemiBold
            )

            Spacer(modifier = Modifier.height(4.dp))

            Text(
                text = subtitle,
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f)
            )

            Spacer(modifier = Modifier.height(12.dp))

            ExposedDropdownMenuBox(
                expanded = expanded,
                onExpandedChange = {
                    expanded = !expanded
                }
            ) {

                OutlinedTextField(
                    value = selectedValue,
                    onValueChange = {},
                    readOnly = true,

                    trailingIcon = {
                        ExposedDropdownMenuDefaults.TrailingIcon(
                            expanded = expanded
                        )
                    },

                    modifier = Modifier
                        .menuAnchor()
                        .fillMaxWidth(),

                    shape = RoundedCornerShape(16.dp),

                    colors = OutlinedTextFieldDefaults.colors(

                        focusedContainerColor = MaterialTheme.colorScheme.surface,
                        unfocusedContainerColor = MaterialTheme.colorScheme.surface,

                        focusedBorderColor = MaterialTheme.colorScheme.primary,
                        unfocusedBorderColor = MaterialTheme.colorScheme.outline,

                        focusedTextColor = MaterialTheme.colorScheme.onSurface,
                        unfocusedTextColor = MaterialTheme.colorScheme.onSurface,

                        cursorColor = MaterialTheme.colorScheme.primary
                    )
                )

                ExposedDropdownMenu(
                    expanded = expanded,

                    onDismissRequest = {
                        expanded = false
                    },

                    modifier = Modifier.background(
                        MaterialTheme.colorScheme.surface,
                        RoundedCornerShape(16.dp)
                    )
                ) {

                    options.forEach { option ->

                        DropdownMenuItem(

                            text = {

                                Text(
                                    text = option,
                                    color = MaterialTheme.colorScheme.onSurface
                                )
                            },

                            colors = MenuDefaults.itemColors(
                                textColor = MaterialTheme.colorScheme.onSurface
                            ),

                            onClick = {
                                onSelected(option)
                                expanded = false
                            }
                        )
                    }
                }
            }
        }
    }
}