package com.rmpteam.zozh.ui.navigation

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
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
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

    // Verify that user is authenticated and profile is set up
    val currentUser = userRepository.getCurrentUser()
    val isAuthenticated = currentUser != null
    val isProfileComplete = isAuthenticated && 
                          currentUser!!.weight != null && 
                          currentUser.height != null &&
                          currentUser.gender != null && 
                          currentUser.age != null && 
                          currentUser.goal != null

    // If not authenticated or profile not complete, don't show drawer content
    if (!isAuthenticated || !isProfileComplete) {
        return
    }

    ModalDrawerSheet {
        Text(
            text = "ZOZH",
            style = MaterialTheme.typography.headlineSmall,
            modifier = Modifier.padding(16.dp)
        )
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
                        }
                    }
                },
                modifier = Modifier.padding(NavigationDrawerItemDefaults.ItemPadding)
            )
        }
    }
}