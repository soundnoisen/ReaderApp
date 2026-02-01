package com.example.readerapp.navigation

import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import com.example.feature.book.reader.ui.BookReaderScreen
import com.example.feature.books.ui.BooksScreen
import com.example.feature.profile.ui.ProfileScreen
import com.example.feature.upload.ui.UploadScreen

@Composable
fun NavHostContainer(
    navController: NavHostController,
    rootNavController: NavHostController,
    padding: PaddingValues,
    isDarkTheme: Boolean,
    onThemeChange: (Boolean) -> Unit,
) {
    NavHost(
        navController = navController,
        startDestination = "books",
        modifier = Modifier.padding(paddingValues = padding),
        builder = {
            composable("books") {
                BooksScreen(
                    navigateToBook = { bookId ->
                        navController.navigate("reader/$bookId")
                    }
                )
            }
            composable("upload") {
                UploadScreen(
                    isDarkTheme = isDarkTheme
                )
            }
            composable(
                route = "reader/{bookId}",
                arguments = listOf(navArgument("bookId") { type = NavType.StringType })
            ) {
                BookReaderScreen(
                    isDarkTheme = isDarkTheme,
                    onThemeChange = onThemeChange,
                    navigateBack = {
                        navController.popBackStack()
                    }
                )
            }
            composable("profile") {
                ProfileScreen(
                    isDarkTheme = isDarkTheme,
                    onThemeChange = onThemeChange,
                    navigateToLogin = {
                        rootNavController.navigate("login") {
                            popUpTo("main") { inclusive = true }
                        }
                    }
                )
            }
        }
    )
}
