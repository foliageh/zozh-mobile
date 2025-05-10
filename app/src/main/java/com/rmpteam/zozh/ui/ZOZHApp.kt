package com.rmpteam.zozh.ui

import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.asPaddingValues
import androidx.compose.foundation.layout.calculateEndPadding
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeDrawing
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material3.DrawerValue
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalNavigationDrawer
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.rememberDrawerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.navigation.NavDestination.Companion.hasRoute
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.rmpteam.zozh.data.user.UserProfile
import com.rmpteam.zozh.ui.navigation.AppNavDrawerSheet
import com.rmpteam.zozh.ui.navigation.AppNavHost
import com.rmpteam.zozh.ui.navigation.Screen
import com.rmpteam.zozh.ui.navigation.ScreenInfo
import com.rmpteam.zozh.ui.navigation.userRepository
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ZOZHApp() {
    val navController = rememberNavController()
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = navBackStackEntry?.destination?.route ?: ""
    
    // Get repository from context
    val context = LocalContext.current
    val userRepository = context.userRepository
    
    // Check if user is authenticated and has a complete profile
    val currentUser by userRepository.getCurrentUser().collectAsState(initial = null)
    val isAuthenticated = currentUser != null
    val isProfileComplete = isAuthenticated && currentUser?.let { isProfileComplete(it) } ?: false
    
    // Determine if we're in an auth-related screen
    val isAuthScreen = currentRoute == "auth" || 
                       currentRoute == "login" || 
                       currentRoute == "register" || 
                       currentRoute == "profileSetup"
    
    // Only allow the drawer and main UI when authenticated and profile is complete
    val enableDrawer = isAuthenticated && isProfileComplete && !isAuthScreen
    
    val screenInfo = remember(navBackStackEntry) {
        Screen.screensInfo.entries.firstOrNull {
            navBackStackEntry?.destination?.hasRoute(it.key) == true
        }?.value ?: Screen.screensInfo[Screen.NutritionMain::class]!!
    }

    val coroutineScope = rememberCoroutineScope()
    val drawerState = rememberDrawerState(DrawerValue.Closed)

    ModalNavigationDrawer(
        drawerState = drawerState,
        drawerContent = {
            // Only provide drawer content if authenticated and profile complete
            if (enableDrawer) {
                AppNavDrawerSheet(
                    navController = navController,
                    drawerState = drawerState
                )
            }
        },
        // Disable drawer gestures if not authenticated or profile incomplete
        gesturesEnabled = enableDrawer
    ) {
        Scaffold(
            containerColor = MaterialTheme.colorScheme.background,
            topBar = {
                ZOZHTopBar(
                    screenInfo = screenInfo,
                    onOpenDrawer = { 
                        // Only allow opening drawer if authenticated and profile complete
                        if (enableDrawer) {
                            coroutineScope.launch { drawerState.open() }
                        }
                    },
                    onNavigateBack = { navController.navigateUp() },
                    enableMenu = enableDrawer
                )
            },
            floatingActionButton = {
                // Only show floating button if authenticated and profile complete
                if (enableDrawer && screenInfo.showFloatingButton) {
                    FloatingActionButton(
                        onClick = screenInfo.floatingButtonAction ?: {},
                        shape = MaterialTheme.shapes.medium,
                        modifier = Modifier.padding(end = WindowInsets.safeDrawing.asPaddingValues()
                            .calculateEndPadding(LocalLayoutDirection.current))
                    ) {
                        Icon(
                            imageVector = screenInfo.floatingButtonIcon!!,
                            contentDescription = screenInfo.floatingButtonDescription!!
                        )
                    }
                }
            },
            modifier = Modifier.fillMaxSize()
        ) { innerPadding ->
            AppNavHost(
                screenInfo = screenInfo,
                navController = navController,
                Modifier.padding(innerPadding)
            )
        }
    }
}

// Helper function to check if profile is complete
private fun isProfileComplete(user: UserProfile): Boolean {
    return user.weight != null && 
           user.height != null && 
           user.gender != null && 
           user.age != null && 
           user.goal != null
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ZOZHTopBar(
    screenInfo: ScreenInfo,
    onOpenDrawer: () -> Unit,
    onNavigateBack: () -> Unit,
    enableMenu: Boolean = true,
    modifier: Modifier = Modifier
) {
    TopAppBar(
        modifier = modifier,
        colors = TopAppBarDefaults.topAppBarColors(
            containerColor = MaterialTheme.colorScheme.surfaceVariant
        ),
        navigationIcon = {
            if (screenInfo.withBackButton) {
                IconButton(onClick = onNavigateBack) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Default.ArrowBack,
                        contentDescription = "Назад"
                    )
                }
            } else if (enableMenu) {
                // Only show menu button if enabled (authenticated)
                IconButton(onClick = onOpenDrawer) {
                    Icon(
                        imageVector = Icons.Default.Menu,
                        contentDescription = "Меню"
                    )
                }
            }
        },
        title = {
            Row(
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Text(
                    text = screenInfo.title,
                    style = MaterialTheme.typography.titleLarge
                )
            }
        },
    )
}