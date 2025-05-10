package com.rmpteam.zozh.ui.navigation

import android.content.Context
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Dining
import androidx.compose.material.icons.rounded.Outlet
import androidx.compose.material.icons.rounded.Settings
import androidx.compose.material3.DrawerState
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalDrawerSheet
import androidx.compose.material3.NavigationDrawerItem
import androidx.compose.material3.NavigationDrawerItemDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import kotlinx.coroutines.launch

data class NavItemInfo(
    val id: Int,
    val title: String,
    val icon: ImageVector,
    val screenRoute: Screen
)

val navItems = listOf(
    NavItemInfo(id = 0, title = "Питание", icon = Icons.Rounded.Dining, screenRoute = Screen.Nutrition),
    NavItemInfo(id = 1, title = "Другое", icon = Icons.Rounded.Outlet, screenRoute = Screen.Other),
    NavItemInfo(id = 2, title = "Настройки", icon = Icons.Rounded.Settings, screenRoute = Screen.Settings)
)

@Composable
fun AppNavDrawerSheet(
    modifier: Modifier = Modifier,
    navController: NavController,
    drawerState: DrawerState
) {
    val coroutineScope = rememberCoroutineScope()
    var currentNavItemId by rememberSaveable { mutableIntStateOf(0) }

    // Get the user repository from context
    val context = LocalContext.current
    val userRepository = context.userRepository
    
    // Verify that user is authenticated and profile is set up
    val currentUser by userRepository.getCurrentUser().collectAsState(initial = null)
    val isAuthenticated = currentUser != null
    val isProfileComplete = isAuthenticated && 
                           currentUser?.weight != null && 
                           currentUser?.height != null &&
                           currentUser?.gender != null && 
                           currentUser?.age != null && 
                           currentUser?.goal != null

    // If not authenticated or profile not complete, don't show drawer content
    if (!isAuthenticated || !isProfileComplete) {
        return
    }

    // Update navigation based on current destination
    val currentDestinationRoute = navController.currentBackStackEntry?.destination?.route
    LaunchedEffect(currentDestinationRoute) {
        val newIndex = navItems.indexOfFirst { 
            when (it.screenRoute) {
                is Screen.Nutrition -> currentDestinationRoute == "Nutrition" || 
                                      currentDestinationRoute?.startsWith("NutritionMain") == true ||
                                      currentDestinationRoute?.startsWith("NutritionRecord") == true
                is Screen.Other -> currentDestinationRoute == "Other"
                is Screen.Settings -> currentDestinationRoute == "Settings"
                else -> false
            }
        }
        if (newIndex >= 0) {
            currentNavItemId = newIndex
        }
    }

    ModalDrawerSheet {
        Text(
            text = "ZOZH",
            style = MaterialTheme.typography.headlineSmall,
            modifier = Modifier.padding(16.dp)
        )
        
        // Show username if available
        currentUser?.let { user ->
            Text(
                text = "Привет, ${user.username}!",
                style = MaterialTheme.typography.bodyMedium,
                modifier = Modifier.padding(horizontal = 16.dp, vertical = 4.dp)
            )
        }
        
        HorizontalDivider(modifier = Modifier.padding(bottom = 12.dp))
        
        navItems.forEach { navItemInfo ->
            NavigationDrawerItem(
                icon = {
                    Icon(
                        imageVector = navItemInfo.icon,
                        contentDescription = navItemInfo.title,
                        modifier = Modifier.size(24.dp)
                    )
                },
                label = {
                    Text(
                        text = navItemInfo.title,
                        style = MaterialTheme.typography.titleMedium
                    )
                },
                selected = currentNavItemId == navItemInfo.id,
                onClick = {
                    coroutineScope.launch { drawerState.close() }
                    
                    if (currentNavItemId != navItemInfo.id) {
                        currentNavItemId = navItemInfo.id
                        
                        // Navigate using Screen objects
                        navController.navigate(navItemInfo.screenRoute) {
                            // Single top prevents multiple copies of the same destination
                            launchSingleTop = true
                            // Pop up to the first destination in the current nav graph
                            popUpTo(navController.graph.startDestinationId) {
                                saveState = true
                            }
                            // Avoid recreating content when reselecting the same destination
                            restoreState = true
                        }
                    }
                },
                modifier = Modifier.padding(NavigationDrawerItemDefaults.ItemPadding)
            )
        }
    }
}