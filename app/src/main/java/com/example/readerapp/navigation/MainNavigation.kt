package com.example.readerapp.navigation

import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.compose.rememberNavController

@Composable
fun MainNavigation(
    isDarkTheme: Boolean,
    onThemeChange: (Boolean) -> Unit,
    rootNavController: NavHostController
) {
    val bottomNavController = rememberNavController()

    Scaffold(
        bottomBar = {
            BottomNavigationBar(navController = bottomNavController)
        }, content = { padding ->
            NavHostContainer(
                navController = bottomNavController,
                rootNavController = rootNavController,
                padding = padding,
                isDarkTheme = isDarkTheme,
                onThemeChange = onThemeChange)
        }
    )
}