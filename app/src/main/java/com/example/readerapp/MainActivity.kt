package com.example.readerapp

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import com.example.core.ui.SplashViewModel
import com.example.core.ui.ThemeViewModel
import com.example.core.ui.splash.SplashScreen
import com.example.core.ui.theme.ReaderAppTheme
import com.example.readerapp.navigation.AppNavHost
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : ComponentActivity() {

    private val themeViewModel: ThemeViewModel by viewModels()
    private val splashViewModel: SplashViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            val isDarkTheme by themeViewModel.isDarkTheme.collectAsState()
            val showSplash by splashViewModel.showSplash.collectAsState()
            ReaderAppTheme(darkTheme = isDarkTheme) {
                if (showSplash) {
                    SplashScreen { splashViewModel.finishSplash() }
                } else {
                    AppNavHost()
                }
            }
        }
    }
}


