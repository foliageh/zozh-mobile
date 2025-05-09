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
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.navigation.NavDestination.Companion.hasRoute
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.rmpteam.zozh.ui.navigation.AppNavDrawerSheet
import com.rmpteam.zozh.ui.navigation.AppNavHost
import com.rmpteam.zozh.ui.navigation.Screen
import com.rmpteam.zozh.ui.navigation.ScreenInfo
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ZOZHApp() {
    val navController = rememberNavController()
    val navBackStackEntry by navController.currentBackStackEntryAsState()
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
            AppNavDrawerSheet(
                navController = navController,
                drawerState = drawerState
            )
        }
    ) {
        Scaffold(
            //modifier = Modifier.nestedScroll(TopAppBarDefaults.enterAlwaysScrollBehavior().nestedScrollConnection),
            containerColor = MaterialTheme.colorScheme.background,
            topBar = {
                ZOZHTopBar(
                    screenInfo = screenInfo,
                    onOpenDrawer = { coroutineScope.launch { drawerState.open() } },
                    onNavigateBack = { navController.navigateUp() }
                )
            },
            floatingActionButton = {
                if (screenInfo.showFloatingButton) {
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

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ZOZHTopBar(
    screenInfo: ScreenInfo,
    onOpenDrawer: () -> Unit,
    onNavigateBack: () -> Unit,
    modifier: Modifier = Modifier
) {
    TopAppBar(
        modifier = modifier,
        colors = TopAppBarDefaults.topAppBarColors(
            containerColor = MaterialTheme.colorScheme.surfaceVariant
        ),
        //scrollBehavior = TopAppBarDefaults.enterAlwaysScrollBehavior(),
        navigationIcon = {
            if (screenInfo.withBackButton) {
                IconButton(onClick = onNavigateBack) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Default.ArrowBack,
                        contentDescription = "Назад"
                    )
                }
            } else {
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