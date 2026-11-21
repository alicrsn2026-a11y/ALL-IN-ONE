package com.allinone.app.navigation

import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.Folder
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.SmartToy
import androidx.compose.material.icons.filled.TravelExplore
import androidx.compose.material.icons.filled.VideoLibrary
import androidx.compose.material.icons.filled.DocumentScanner
import androidx.compose.material.icons.filled.Checklist
import androidx.compose.material3.DrawerValue
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.ModalDrawerSheet
import androidx.compose.material3.ModalNavigationDrawer
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationDrawerItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.rememberDrawerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.unit.dp
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.allinone.app.data.AppContainer
import com.allinone.app.ui.ai.AiScreen
import com.allinone.app.ui.email.EmailScreen
import com.allinone.app.ui.files.FilesScreen
import com.allinone.app.ui.productivity.ProductivityScreen
import com.allinone.app.ui.research.ResearchScreen
import com.allinone.app.ui.scanner.ScannerScreen
import com.allinone.app.ui.settings.SettingsScreen
import com.allinone.app.ui.space.InfiniteSpaceScreen
import com.allinone.app.ui.video.VideoScreen
import kotlinx.coroutines.launch

data class Destination(
    val route: String,
    val label: String,
    val icon: ImageVector
)

object Routes {
    const val SPACE = "space"
    const val AI = "ai"
    const val RESEARCH = "research"
    const val EMAIL = "email"
    const val SCANNER = "scanner"
    const val VIDEO = "video"
    const val PRODUCTIVITY = "productivity"
    const val FILES = "files"
    const val SETTINGS = "settings"
}

val allDestinations = listOf(
    Destination(Routes.SPACE, "Spațiu", Icons.Filled.Home),
    Destination(Routes.AI, "Asistent", Icons.Filled.SmartToy),
    Destination(Routes.RESEARCH, "Cercetare", Icons.Filled.TravelExplore),
    Destination(Routes.EMAIL, "Email", Icons.Filled.Email),
    Destination(Routes.SCANNER, "Scanner", Icons.Filled.DocumentScanner),
    Destination(Routes.VIDEO, "Video", Icons.Filled.VideoLibrary),
    Destination(Routes.PRODUCTIVITY, "Productivitate", Icons.Filled.Checklist),
    Destination(Routes.FILES, "Fișiere", Icons.Filled.Folder),
    Destination(Routes.SETTINGS, "Setări", Icons.Filled.Settings)
)

// Destinations shown in the bottom bar (keep to 5 for a phone).
val bottomDestinations = listOf(
    allDestinations[0], // Spațiu
    allDestinations[1], // Asistent
    allDestinations[6], // Productivitate
    allDestinations[4], // Scanner
    allDestinations[8]  // Setări
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AllInOneNavHost(container: AppContainer) {
    val navController = rememberNavController()
    val drawerState = rememberDrawerState(initialValue = DrawerValue.Closed)
    val scope = rememberCoroutineScope()
    var currentRoute by remember { mutableStateOf(Routes.SPACE) }

    ModalNavigationDrawer(
        drawerState = drawerState,
        drawerContent = {
            ModalDrawerSheet {
                Text(
                    "ALL-IN-ONE",
                    modifier = Modifier.padding(20.dp),
                    style = androidx.compose.material3.MaterialTheme.typography.titleLarge
                )
                allDestinations.forEach { dest ->
                    NavigationDrawerItem(
                        label = { Text(dest.label) },
                        selected = currentRoute == dest.route,
                        icon = { Icon(dest.icon, contentDescription = null) },
                        onClick = {
                            navController.navigate(dest.route) {
                                popUpTo(Routes.SPACE) { saveState = true }
                                launchSingleTop = true
                                restoreState = true
                            }
                            currentRoute = dest.route
                            scope.launch { drawerState.close() }
                        }
                    )
                }
            }
        }
    ) {
        Scaffold(
            topBar = {
                TopAppBar(
                    title = { Text(allDestinations.firstOrNull { it.route == currentRoute }?.label ?: "") },
                    navigationIcon = {
                        IconButton(onClick = { scope.launch { drawerState.open() } }) {
                            Icon(Icons.Filled.Menu, contentDescription = "Meniu")
                        }
                    }
                )
            },
            bottomBar = {
                NavigationBar {
                    bottomDestinations.forEach { dest ->
                        NavigationBarItem(
                            selected = currentRoute == dest.route,
                            onClick = {
                                navController.navigate(dest.route) {
                                    popUpTo(Routes.SPACE) { saveState = true }
                                    launchSingleTop = true
                                    restoreState = true
                                }
                                currentRoute = dest.route
                            },
                            icon = { Icon(dest.icon, contentDescription = null) },
                            label = { Text(dest.label) }
                        )
                    }
                }
            }
        ) { innerPadding ->
            NavHost(
                navController = navController,
                startDestination = Routes.SPACE,
                modifier = Modifier.padding(innerPadding)
            ) {
                composable(Routes.SPACE) { InfiniteSpaceScreen(container) }
                composable(Routes.AI) { AiScreen(container) }
                composable(Routes.RESEARCH) { ResearchScreen(container) }
                composable(Routes.EMAIL) { EmailScreen(container) }
                composable(Routes.SCANNER) { ScannerScreen(container) }
                composable(Routes.VIDEO) { VideoScreen(container) }
                composable(Routes.PRODUCTIVITY) { ProductivityScreen(container) }
                composable(Routes.FILES) { FilesScreen(container) }
                composable(Routes.SETTINGS) { SettingsScreen(container) }
            }
        }
    }
}
