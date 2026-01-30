package com.example.feature_auth.ui.login

import android.annotation.SuppressLint
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHostState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import com.example.core.ui.component.BaseButton
import com.example.core.ui.component.BaseHeader
import com.example.core.ui.component.BaseSnackBar
import com.example.feature_auth.R
import com.example.feature_auth.ui.login.component.GoogleSignInButton
import com.example.feature_auth.ui.login.component.LoginFooter
import com.example.feature_auth.ui.login.component.LoginForm
import com.example.feature_auth.ui.login.component.OrDivider
import com.example.feature_auth.ui.mapper.toUiText

@SuppressLint("LocalContextResourcesRead")
@Composable
fun LoginScreen(
    viewModel: LoginViewModel = hiltViewModel(),
    onNavigateToMain: () -> Unit,
    onNavigateToRegister: () -> Unit
) {
    val state by viewModel.state.collectAsState()
    var currentEffect by remember { mutableStateOf<LoginEffect?>(null) }
    val snackBarHostState = remember { SnackbarHostState() }
    val context = LocalContext.current

    LaunchedEffect(Unit) {
        viewModel.effect.collect { effect ->
            currentEffect = effect
            when (effect) {
                is LoginEffect.NavigateToMain -> onNavigateToMain()
                is LoginEffect.ShowError -> {
                    snackBarHostState.showSnackbar(
                        message = effect.error.toUiText(context.resources)
                    )
                }
            }
        }
    }

    Scaffold(
        snackbarHost = {
            BaseSnackBar(
                snackBarHostState = snackBarHostState,
                canRetry = currentEffect?.let { it is LoginEffect.ShowError && it.canRetry } ?: false,
                onRetry = { viewModel.handleIntent(LoginIntent.Login) },
                modifier = Modifier.padding(top = 50.dp)
            )
        }
    ) { paddingValues ->

        Column(
            Modifier
                .fillMaxSize()
                .background(MaterialTheme.colorScheme.background)
                .padding(paddingValues)
                .padding(horizontal = 16.dp)
                .padding(top = 30.dp, bottom = 60.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {

            BaseHeader(text = stringResource(R.string.title_login))

            Spacer(modifier = Modifier.height(160.dp))

            LoginForm(
                email = state.email,
                password = state.password,
                enabled = !state.isLoading,
                onEmailChange = {
                    viewModel.handleIntent(LoginIntent.EmailChanged(it))
                },
                onPasswordChange = {
                    viewModel.handleIntent(LoginIntent.PasswordChanged(it))
                }
            )

            Spacer(modifier = Modifier.height(24.dp))

            BaseButton(
                text = stringResource(R.string.action_login),
                enabled = !state.isLoading
            ) {
                viewModel.handleIntent(LoginIntent.Login)
            }
            Spacer(modifier = Modifier.height(16.dp))
            OrDivider()
            Spacer(modifier = Modifier.height(8.dp))
            GoogleSignInButton(
                loading = !state.isGoogleLoading,
                onClick = { viewModel.handleIntent(LoginIntent.GoogleLogin) }
            )
            Spacer(modifier = Modifier.weight(1f))
            LoginFooter { onNavigateToRegister() }
        }
    }
}