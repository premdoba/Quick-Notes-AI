package com.example.quicknotes.ui.screens

import TodoViewModel
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.example.quicknotes.data.local.TodoEntity
import com.example.quicknotes.R
import com.example.quicknotes.ui.Routes
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun QuickTodoScreen(
    navController: NavController,
    vm: TodoViewModel = viewModel()
) {

    val todos by vm.todos.collectAsState(initial = emptyList())

    var fabExpanded by remember { mutableStateOf(false) }
    var showDialog by remember { mutableStateOf(false) }
    var showClearDialog by remember { mutableStateOf(false) }
    var title by remember { mutableStateOf("") }

    val scope = rememberCoroutineScope()

    val configuration = LocalConfiguration.current
    val isTablet = configuration.screenWidthDp >= 600
    val paddingSize = if (isTablet) 32.dp else 14.dp
    val maxContentWidth = 720.dp

    var selectedReminderTime by remember {
        mutableStateOf<Long?>(null)
    }

    val gradient = Brush.verticalGradient(
        listOf(
            MaterialTheme.colorScheme.background,
            MaterialTheme.colorScheme.surface,
            MaterialTheme.colorScheme.primary.copy(alpha = 0.12f)
        )
    )

    if (showClearDialog) {
        AlertDialog(
            onDismissRequest = { showClearDialog = false },
            title = { Text("Delete All Tasks") },
            text = { Text("Are you sure you want to delete all tasks?") },
            confirmButton = {
                TextButton(onClick = {
                    vm.clearAll()
                    showClearDialog = false
                }) {
                    Text("Delete", color = MaterialTheme.colorScheme.error)
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

        topBar = {
            TopAppBar(
                title = {
                    Column {
                        Text("QuickTasks", fontWeight = FontWeight.Bold)
                        Text(
                            "Organize your day, focus better",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
                        )
                    }
                },
                actions = {
                    IconButton(onClick = { showClearDialog = true }) {
                        Icon(
                            painter = painterResource(R.drawable.outline_delete_sweep_24),
                            contentDescription = "Clear",
                            tint = MaterialTheme.colorScheme.error
                        )
                    }
                }
            )
        },

        floatingActionButton = {
            Column(
                horizontalAlignment = Alignment.End,
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {

                AnimatedVisibility(fabExpanded) {
                    MiniFabButton("New Task", R.drawable.baseline_add_24) {
                        showDialog = true
                        fabExpanded = false
                    }
                }

                FloatingActionButton(
                    onClick = { showDialog = true },
                    containerColor = MaterialTheme.colorScheme.primary
                ) {
                    Icon(
                        painter = painterResource(R.drawable.baseline_add_24),
                        contentDescription = "Add"
                    )
                }
            }
        },

        bottomBar = {
            if (!isTablet) {
                BottomAppBar {

                    NavigationBarItem(
                        selected = false,
                        onClick = { navController.navigate(Routes.Generate.route) },
                        icon = { Icon(painterResource(R.drawable.outline_home_24), null) },
                        label = { Text("Home") },
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
                        onClick = { navController.navigate(Routes.Downloads.route) },
                        icon = { Icon(painterResource(R.drawable.outline_download_24), null) },
                        label = { Text("Downloads") },
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
                        onClick = { },
                        icon = { Icon(painterResource(R.drawable.baseline_checklist_24), null) },
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
                        onClick = { navController.navigate(Routes.Settings.route) },
                        icon = { Icon(painterResource(R.drawable.outline_settings_24), null) },
                        label = { Text("Settings") },
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

    ) { padding ->

        Row(
            modifier = Modifier
                .fillMaxSize()
                .background(gradient)
                .padding(padding)
        ) {

            if (isTablet) {
                NavigationRail(
                    containerColor = MaterialTheme.colorScheme.surface
                ) {

                    NavigationRailItem(
                        selected = false,
                        onClick = { navController.navigate(Routes.Generate.route) },
                        icon = { Icon(painterResource(R.drawable.outline_home_24), null) },
                        label = { Text("Home") },
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
                        onClick = { navController.navigate(Routes.Downloads.route) },
                        icon = { Icon(painterResource(R.drawable.outline_download_24), null) },
                        label = { Text("Downloads") },
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
                        onClick = { },
                        icon = { Icon(painterResource(R.drawable.baseline_checklist_24), null) },
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
                        onClick = { navController.navigate(Routes.Settings.route) },
                        icon = { Icon(painterResource(R.drawable.outline_settings_24), null) },
                        label = { Text("Settings") },
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
                    .widthIn(max = maxContentWidth),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {

                if (todos.isNotEmpty()) {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(bottom = 8.dp),
                        contentAlignment = Alignment.TopCenter
                    ) {
                        Text(
                            "Tip: Swipe left to delete a task.",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
                        )
                    }
                }

                if (todos.isEmpty()) {
                    EmptyTodoState()
                } else {

                    LazyColumn(
                        verticalArrangement = Arrangement.spacedBy(10.dp),
                        modifier = Modifier.fillMaxSize()
                    ) {

                        items(todos, key = { it.id }) { todo ->

                            val dismissState = rememberSwipeToDismissBoxState(
                                confirmValueChange = {
                                    if (it == SwipeToDismissBoxValue.EndToStart) {
                                        vm.delete(todo)
                                        true
                                    } else false
                                }
                            )

                            SwipeToDismissBox(
                                state = dismissState,
                                enableDismissFromStartToEnd = false,
                                backgroundContent = {
                                    Box(
                                        modifier = Modifier
                                            .fillMaxSize()
                                            .clip(RoundedCornerShape(18.dp))
                                            .background(MaterialTheme.colorScheme.error),
                                        contentAlignment = Alignment.CenterEnd
                                    ) {
                                        Icon(
                                            painter = painterResource(R.drawable.baseline_delete_24),
                                            contentDescription = null,
                                            tint = MaterialTheme.colorScheme.onError,
                                            modifier = Modifier.padding(end = 16.dp)
                                        )
                                    }
                                }
                            ) {

                                Box(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .clip(RoundedCornerShape(18.dp))
                                ) {
                                    TodoCard(
                                        todo = todo,
                                        onToggle = { vm.toggleTodo(todo) }
                                    )
                                }

                            }
                        }
                    }
                }
            }

            if (showDialog) {
                AlertDialog(
                    onDismissRequest = { showDialog = false },
                    title = { Text("Add New Task") },
                    text = {

                        Column {

                            OutlinedTextField(
                                value = title,
                                onValueChange = { title = it },
                                modifier = Modifier.fillMaxWidth(),
                                singleLine = true,
                                label = {
                                    Text("Task title")
                                }
                            )

                            Spacer(modifier = Modifier.height(16.dp))

                            OutlinedButton(
                                onClick = {

                                    val calendar = java.util.Calendar.getInstance()

                                    android.app.DatePickerDialog(
                                        navController.context,
                                        { _, year, month, day ->

                                            android.app.TimePickerDialog(
                                                navController.context,
                                                { _, hour, minute ->

                                                    calendar.set(
                                                        year,
                                                        month,
                                                        day,
                                                        hour,
                                                        minute
                                                    )

                                                    selectedReminderTime =
                                                        calendar.timeInMillis

                                                },
                                                calendar.get(java.util.Calendar.HOUR_OF_DAY),
                                                calendar.get(java.util.Calendar.MINUTE),
                                                false
                                            ).show()

                                        },
                                        calendar.get(java.util.Calendar.YEAR),
                                        calendar.get(java.util.Calendar.MONTH),
                                        calendar.get(java.util.Calendar.DAY_OF_MONTH)
                                    ).show()

                                },
                                modifier = Modifier.fillMaxWidth(),
                                border = BorderStroke(
                                    1.5.dp,
                                    Color(0xFF00D9FF)
                                ),

                                colors = ButtonDefaults.outlinedButtonColors(
                                    containerColor = MaterialTheme.colorScheme.surface
                                )
                            ) {

                                Text(
                                    if (selectedReminderTime != null)
                                        "Reminder Added"
                                    else
                                        "Set Reminder"
                                )
                            }

                            if (selectedReminderTime != null) {

                                Spacer(modifier = Modifier.height(12.dp))

                                Text(
                                    text = java.text.SimpleDateFormat(
                                        "dd MMM yyyy, hh:mm a",
                                        java.util.Locale.getDefault()
                                    ).format(java.util.Date(selectedReminderTime!!)),
                                    style = MaterialTheme.typography.bodyMedium
                                )
                            }
                        }
                    },
                    confirmButton = {

                        Button(
                            onClick = {

                                if (title.isNotBlank()) {

                                    val todo = TodoEntity(
                                        title = title,
                                        reminderTime = selectedReminderTime
                                    )

                                    vm.insert(todo)

                                    title = ""
                                    selectedReminderTime = null
                                    showDialog = false
                                }

                            }
                        ) {
                            Text("Add")
                        }
                    },
                    dismissButton = {

                        TextButton(
                            onClick = {
                                showDialog = false
                            }
                        ) {
                            Text("Cancel")
                        }
                    }
                )
            }
        }
    }
}

@Composable
fun TodoCard(
    todo: TodoEntity,
    onToggle: () -> Unit
) {

    Card(
        modifier = Modifier.fillMaxWidth()
            .clickable(onClick = {onToggle()}),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface
        ),
        elevation = CardDefaults.cardElevation(6.dp)
    ) {

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {

            Column(modifier = Modifier.weight(1f)) {

                Text(
                    text = todo.title,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = if (todo.completed)
                        MaterialTheme.colorScheme.primary
                    else
                        MaterialTheme.colorScheme.onSurface
                )

                Text(
                    text = if (todo.completed) "Completed" else "Pending",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
                )
            }

            Icon(
                painter = painterResource(R.drawable.baseline_check_24),
                contentDescription = "Toggle",
                tint = if (todo.completed)
                    MaterialTheme.colorScheme.primary
                else
                    MaterialTheme.colorScheme.onSurface
            )
        }
    }
}

@Composable
fun EmptyTodoState() {

    Column(
        modifier = Modifier.fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text(
            text = "Tap + to add your first task",
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.5f)
        )
    }
}