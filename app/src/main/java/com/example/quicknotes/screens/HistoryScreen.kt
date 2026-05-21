package com.example.quicknotes.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Home
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
import com.example.quicknotes.viewmodel.StudyViewModel
import com.example.quicknotes.ui.Routes
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HistoryScreen(navController: NavController, vm: StudyViewModel) {

    val history by vm.historyList.collectAsState(initial = emptyList())

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

    var showClearDialog by remember { mutableStateOf(false) }

    val snackbarHostState = remember { SnackbarHostState() }
    val scope = rememberCoroutineScope()

    if (showClearDialog) {
        AlertDialog(
            onDismissRequest = { showClearDialog = false },
            title = { Text("Clear History?") },
            text = { Text("Are you sure you want to delete all history items?") },
            confirmButton = {
                TextButton(onClick = {
                    vm.clearHistory()
                    showClearDialog = false
                }) {
                    Text("Delete All", color = MaterialTheme.colorScheme.error)
                }
            },
            dismissButton = {
                TextButton(onClick = { showClearDialog = false }) {
                    Text("Cancel")
                }
            }
        )
    }

    Scaffold(
        snackbarHost = { SnackbarHost(hostState = snackbarHostState) },

        topBar = {
            TopAppBar(
                title = {
                    Text(
                        "History",
                        fontWeight = FontWeight.Bold
                    )
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
                BottomAppBar(containerColor = MaterialTheme.colorScheme.surface) {

                    NavigationBarItem(
                        selected = false,
                        onClick = { navController.navigate(Routes.Generate.route) },
                        icon = { Icon(Icons.Default.Home, contentDescription = "Home") },
                        label = { Text("Home") }
                    )

                    NavigationBarItem(
                        selected = true,
                        onClick = {},
                        icon = {
                            Icon(
                                painter = painterResource(R.drawable.baseline_history),
                                contentDescription = "History"
                            )
                        },
                        label = { Text("History") }
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
                        onClick = { navController.navigate(Routes.Generate.route) },
                        icon = { Icon(Icons.Default.Home, contentDescription = "Home") },
                        label = { Text("Home") }
                    )

                    NavigationRailItem(
                        selected = true,
                        onClick = {},
                        icon = {
                            Icon(
                                painter = painterResource(R.drawable.baseline_history),
                                contentDescription = "History"
                            )
                        },
                        label = { Text("History") }
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

                    if (history.isNotEmpty()) {
                        Text(
                            text = "Tip: Swipe left to delete an item.",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.7f),
                            modifier = Modifier.padding(bottom = 10.dp)
                        )
                    }

                    if (history.isEmpty()) {
                        Text(
                            text = "No history found yet.",
                            style = MaterialTheme.typography.bodyLarge
                        )
                    } else {

                        LazyColumn {
                            items(history, key = { it.id }) { item ->

                                val dismissState = rememberSwipeToDismissBoxState(
                                    confirmValueChange = { value ->
                                        if (value == SwipeToDismissBoxValue.EndToStart) {

                                            vm.deleteHistoryItem(item.id)

                                            scope.launch {
                                                val result = snackbarHostState.showSnackbar(
                                                    message = "Deleted",
                                                    actionLabel = "UNDO",
                                                    duration = SnackbarDuration.Short
                                                )

                                                if (result == SnackbarResult.ActionPerformed) {
                                                    vm.insertHistoryItem(item)
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
                                                imageVector = Icons.Default.Delete,
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
                                                    Routes.HistoryDetail.createRoute(item.id)
                                                )
                                            },
                                        shape = RoundedCornerShape(20.dp),
                                        colors = CardDefaults.cardColors(
                                            containerColor = MaterialTheme.colorScheme.surface
                                        ),
                                        elevation = CardDefaults.cardElevation(8.dp)
                                    ) {
                                        Column(modifier = Modifier.padding(16.dp)) {

                                            Text(
                                                text = item.topic,
                                                style = MaterialTheme.typography.titleLarge,
                                                fontWeight = FontWeight.Bold
                                            )

                                            Spacer(modifier = Modifier.height(4.dp))

                                            Text(
                                                text = "Level: ${item.educationLevel}",
                                                style = MaterialTheme.typography.bodyMedium,
                                                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.8f)
                                            )

                                            Spacer(modifier = Modifier.height(6.dp))

                                            Text(
                                                text = formatDate(item.createdAt),
                                                style = MaterialTheme.typography.bodySmall,
                                                color = MaterialTheme.colorScheme.secondary
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

fun formatDate(time: Long): String {
    val sdf = SimpleDateFormat("dd MMM yyyy, hh:mm a", Locale.getDefault())
    return sdf.format(Date(time))
}