package com.example.quicknotes.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.quicknotes.R
import com.example.quicknotes.ui.Routes
import com.example.quicknotes.viewmodel.StudyViewModel
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun McqsScreen(
    navController: NavController,
    vm: StudyViewModel
) {

    val quizHistory by vm.quizHistoryList.collectAsState(initial = emptyList())

    val configuration = LocalConfiguration.current
    val screenWidth = configuration.screenWidthDp
    val isTablet = screenWidth >= 600
    val paddingSize = if (isTablet) 32.dp else 14.dp
    val maxContentWidth = 720.dp

    val gradient = Brush.verticalGradient(
        listOf(
            MaterialTheme.colorScheme.background,
            MaterialTheme.colorScheme.surface,
            MaterialTheme.colorScheme.primary.copy(alpha = 0.25f)
        )
    )

    val snackbarHostState = remember { SnackbarHostState() }
    val scope = rememberCoroutineScope()

    var showClearDialog by remember { mutableStateOf(false) }

    if (showClearDialog) {

        AlertDialog(

            onDismissRequest = {
                showClearDialog = false
            },

            title = {
                Text(
                    text = "Delete All Quizzes"
                )
            },

            text = {
                Text(
                    text = "Are you sure you want to delete all quizzes?"
                )
            },

            confirmButton = {

                TextButton(

                    onClick = {

                        vm.clearQuizHistory()

                        showClearDialog = false

                        scope.launch {

                            snackbarHostState.showSnackbar(
                                message = "All quizzes deleted"
                            )
                        }
                    }
                ) {

                    Text(
                        text = "Delete",
                        color = MaterialTheme.colorScheme.error
                    )
                }
            },

            dismissButton = {

                TextButton(

                    onClick = {
                        showClearDialog = false
                    }
                ) {

                    Text("Cancel")
                }
            }
        )
    }

    Scaffold(

        snackbarHost = {
            SnackbarHost(hostState = snackbarHostState)
        },

        topBar = {

            TopAppBar(
                title = {
                    Text(
                        "MCQs",
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
                },
                actions = {
                    IconButton(onClick = { showClearDialog = true }) {
                        Icon(
                            painter = painterResource(R.drawable.outline_delete_sweep_24),
                            contentDescription = "Clear History",
                            tint = MaterialTheme.colorScheme.error
                        )
                    }
                }
            )
        },

        bottomBar = {

            if (!isTablet) {

                BottomAppBar(
                    containerColor = MaterialTheme.colorScheme.surface
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
                        selected = true,
                        onClick = {},
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
                        selected = false,
                        onClick = {
                            navController.navigate(Routes.Settings.route)
                        },
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
                    containerColor = MaterialTheme.colorScheme.surface
                ) {

                    NavigationRailItem(
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
                                painter = painterResource(R.drawable.outline_download_24),
                                contentDescription = "Downloads"
                            )
                        },
                        label = {
                            Text("Downloads")
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
                        selected = false,
                        onClick = {
                            navController.navigate(Routes.Settings.route)
                        },
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
                    .padding(paddingSize),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {

                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .widthIn(max = maxContentWidth)
                ) {

                    if (quizHistory.isNotEmpty()) {

                        Text(
                            text = "Tip: Swipe left to delete a quiz.",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.7f),
                            modifier = Modifier.padding(bottom = 10.dp)
                        )
                    }

                    if (quizHistory.isEmpty()) {

                        Box(
                            modifier = Modifier.fillMaxSize(),
                            contentAlignment = Alignment.Center
                        ) {

                            Text(
                                text = "No downloaded quizzes found.",
                                style = MaterialTheme.typography.bodyLarge
                            )
                        }

                    } else {

                        LazyColumn {

                            items(
                                items = quizHistory,
                                key = { item -> item.id }
                            ) { item ->

                                val dismissState =
                                    rememberSwipeToDismissBoxState(

                                        confirmValueChange = { value ->

                                            if (value ==
                                                SwipeToDismissBoxValue.EndToStart
                                            ) {

                                                vm.deleteQuiz(item.id)

                                                scope.launch {

                                                    val result =
                                                        snackbarHostState.showSnackbar(
                                                            message = "Quiz deleted",
                                                            actionLabel = "UNDO",
                                                            duration = SnackbarDuration.Short
                                                        )

                                                    if (
                                                        result ==
                                                        SnackbarResult.ActionPerformed
                                                    ) {

                                                        vm.saveQuiz(item)
                                                    }
                                                }

                                                true

                                            } else {
                                                false
                                            }
                                        }
                                    )

                                SwipeToDismissBox(
                                    state = dismissState,
                                    enableDismissFromStartToEnd = false,

                                    backgroundContent = {

                                        Box(
                                            modifier = Modifier
                                                .fillMaxSize()
                                                .padding(bottom = 12.dp)
                                                .background(
                                                    MaterialTheme.colorScheme.error,
                                                    shape = RoundedCornerShape(20.dp)
                                                )
                                                .padding(end = 20.dp),

                                            contentAlignment = Alignment.CenterEnd
                                        ) {

                                            Icon(
                                                painter =
                                                    painterResource(
                                                        R.drawable.baseline_delete_24
                                                    ),
                                                contentDescription = "Delete",
                                                tint = MaterialTheme.colorScheme.onError
                                            )
                                        }
                                    }
                                ) {

                                    Card(
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .padding(bottom = 12.dp)
                                            .clickable {

                                                navController.navigate(
                                                    "mcq_detail/${item.id}"
                                                )
                                            },

                                        shape = RoundedCornerShape(20.dp),

                                        colors = CardDefaults.cardColors(
                                            containerColor =
                                                MaterialTheme.colorScheme.surface
                                        ),

                                        elevation = CardDefaults.cardElevation(8.dp)
                                    ) {

                                        Column(
                                            modifier = Modifier.padding(16.dp)
                                        ) {

                                            Text(
                                                text = item.title,
                                                style =
                                                    MaterialTheme.typography.titleLarge,

                                                fontWeight = FontWeight.Bold
                                            )

                                            Spacer(
                                                modifier = Modifier.height(8.dp)
                                            )

                                            Text(
                                                text =
                                                    "Score: ${item.score}/${item.totalQuestions}",

                                                style =
                                                    MaterialTheme.typography.bodyLarge
                                            )

                                            Spacer(
                                                modifier = Modifier.height(6.dp)
                                            )

                                            Text(
                                                text = formatDate(item.date),

                                                style =
                                                    MaterialTheme.typography.bodySmall,

                                                color =
                                                    MaterialTheme.colorScheme.primary
                                            )
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}