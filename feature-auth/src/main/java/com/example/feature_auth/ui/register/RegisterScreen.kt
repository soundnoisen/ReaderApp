package com.example.feature_auth.ui.register

import android.widget.Toast
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
import com.example.feature_auth.ui.mapper.toUiText
import com.example.feature_auth.ui.register.component.RegisterFooter
import com.example.feature_auth.ui.register.component.RegisterForm

@Composable
fun RegisterScreen(
    viewModel: RegisterViewModel = hiltViewModel(),
    onNavigateToLogin: () -> Unit
) {
    val context = LocalContext.current
    val state by viewModel.state.collectAsState()
    var currentEffect by remember { mutableStateOf<RegisterEffect?>(null) }
    val snackBarHostState = remember { SnackbarHostState() }

    LaunchedEffect(Unit) {
        viewModel.effect.collect { effect ->
            currentEffect = effect
            when(effect) {
                is RegisterEffect.NavigateToLogin -> {
                    Toast.makeText(context, context.resources.getString(R.string.msg_register_success), Toast.LENGTH_SHORT).show()
                    onNavigateToLogin()
                }
                is RegisterEffect.ShowError -> {
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
                snackBarHostState  = snackBarHostState,
                canRetry = currentEffect?.let { it is RegisterEffect.ShowError && it.canRetry } ?: false,
                onRetry = { viewModel.handleIntent(RegisterIntent.Registration) },
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

            BaseHeader(text = stringResource(R.string.title_register))

            Spacer(modifier = Modifier.height(160.dp))

            RegisterForm(
                email = state.email,
                password = state.password,
                confirmPassword = state.passwordRepeat,
                enabled = !state.isLoading,
                onEmailChange = {
                    viewModel.handleIntent(RegisterIntent.EmailChanged(it))
                },
                onPasswordChange = {
                    viewModel.handleIntent(RegisterIntent.PasswordChanged(it))
                },
                onPasswordConfirmChange = {
                    viewModel.handleIntent(RegisterIntent.PasswordRepeatChanged(it))
                },
            )

            Spacer(modifier = Modifier.height(24.dp))

            BaseButton(
                text = stringResource(R.string.action_register),
                enabled = !state.isLoading
            ) {
                viewModel.handleIntent(RegisterIntent.Registration)
            }

            Spacer(modifier = Modifier.weight(1f))
            RegisterFooter { onNavigateToLogin() }
        }
    }
}