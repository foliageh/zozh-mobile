package com.rmpteam.zozh.ui.navigation

import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Dining
import androidx.compose.material.icons.rounded.NightsStay
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
    val icon: ImageVector
)
val navItems = mapOf(
    Screen.NutritionSection to NavItemInfo(id = 0, title = "Питание", icon = Icons.Rounded.Dining),
    Screen.SleepSection to NavItemInfo(id = 1, title = "Сон", icon = Icons.Rounded.NightsStay),
)

@Composable
fun AppNavDrawerSheet(
    modifier: Modifier = Modifier,
    navController: NavController,
    drawerState: DrawerState
) {
    val coroutineScope = rememberCoroutineScope()
    var currentNavItemId by rememberSaveable { mutableIntStateOf(0) }

    ModalDrawerSheet {
        Text(
            text = "ZOZH",
            style = MaterialTheme.typography.headlineSmall,
            modifier = Modifier.padding(16.dp)
        )
        HorizontalDivider(modifier = Modifier.padding(bottom = 12.dp))
        navItems.forEach { (screen, navItemInfo) ->
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
                        navController.navigate(screen)
                    }
                },
                modifier = Modifier.padding(NavigationDrawerItemDefaults.ItemPadding)
            )
        }
    }
}