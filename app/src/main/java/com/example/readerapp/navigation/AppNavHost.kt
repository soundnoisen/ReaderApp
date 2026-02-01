package com.example.readerapp.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.feature_auth.ui.login.LoginScreen
import com.example.feature_auth.ui.register.RegisterScreen

@Composable
fun AppNavHost(
    isDarkTheme: Boolean,
    onThemeChange: (Boolean) -> Unit,
    navController: NavHostController = rememberNavController()
) {
    NavHost(
        navController,
        startDestination = "login",
    ) {
        composable("login") {
            LoginScreen(
                onNavigateToRegister = { navController.navigate("register") },
                onNavigateToMain = {
                    navController.navigate("main") {
                        popUpTo("login") { inclusive = true }
                    }
                }
            )
        }
        composable("register") {
            RegisterScreen(
                onNavigateToLogin = { navController.popBackStack() }
            )
        }
        composable("main") {
            MainNavigation(
                isDarkTheme = isDarkTheme,
                onThemeChange = onThemeChange,
                rootNavController = navController,
            )
        }
    }
}