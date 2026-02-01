package com.example.feature.profile.ui

import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
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
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import com.example.core.ui.ThemeViewModel
import com.example.core.ui.component.BaseAlertDialog
import com.example.core.ui.component.BaseSnackBar
import com.example.feature.profile.R
import com.example.feature.profile.ui.component.ProfileEditBottomSheet
import com.example.feature.profile.ui.component.ProfileEmail
import com.example.feature.profile.ui.component.ProfileName
import com.example.feature.profile.ui.component.ProfilePhoto
import com.example.feature.profile.ui.component.ProfileTopBar
import com.example.feature.profile.ui.mapper.toUiText


@Composable
fun ProfileScreen(
    navigateToLogin: () -> Unit,
    isDarkTheme: Boolean,
    onThemeChange: (Boolean) -> Unit,
    viewModel: ProfileViewModel = hiltViewModel()
) {
    val state by viewModel.state.collectAsState()
    val snackBarHostState = remember { SnackbarHostState() }
    val context = LocalContext.current

    val fileLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()) { uri ->
        uri?.let {
            viewModel.handleIntent(ProfileIntent.PhotoSelected(it))
        }
    }

    LaunchedEffect(Unit) {
        viewModel.effect.collect { effect ->
            when(effect) {
                is ProfileEffect.NavigateToLogin -> navigateToLogin()
                is ProfileEffect.OpenFilePicker -> fileLauncher.launch("image/*")
                is ProfileEffect.ShowError -> snackBarHostState.showSnackbar(effect.error.toUiText(context.resources))
                is ProfileEffect.ThemeChange ->onThemeChange(effect.isDarkTheme)
                is ProfileEffect.UploadProfileSuccessToast -> Toast.makeText(context, context.resources.getString(R.string.msg_update_success), Toast.LENGTH_SHORT).show()
            }
        }
    }

    Scaffold(
        snackbarHost = {
            BaseSnackBar(
                snackBarHostState = snackBarHostState,
            )
        },
        topBar = {
            ProfileTopBar(
                onEdit = { viewModel.handleIntent(ProfileIntent.EditClicked)},
                onLogout = { viewModel.handleIntent(ProfileIntent.LogoutClicked) },
                modifier = Modifier.padding(top = 18.dp)
            )
        },
        containerColor = MaterialTheme.colorScheme.background
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues),
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            Spacer(Modifier.height(20.dp))
            ProfilePhoto(context, state.photoUri, isDarkTheme)
            Spacer(Modifier.height(16.dp))
            ProfileName(state.name.takeIf { !it.isNullOrBlank() } ?: state.email.orEmpty())
            Spacer(Modifier.height(4.dp))
            ProfileEmail(state.email.orEmpty())

            if (state.isEditVisible) {
                ProfileEditBottomSheet(
                    context = context,
                    isLoading = state.isLoading,
                    photoUrl = state.editPhotoUri ?: state.photoUri,
                    name = state.editName ?: state.name.orEmpty(),
                    onPhotoChange = { viewModel.handleIntent(ProfileIntent.SelectPhotoClicked) },
                    onNameChange = { viewModel.handleIntent(ProfileIntent.NameChanged(it)) },
                    isDarkTheme = isDarkTheme,
                    onThemeChanged = { viewModel.handleIntent(ProfileIntent.ThemeChanged(it)) },
                    onSave = { viewModel.handleIntent(ProfileIntent.SaveClicked) },
                    onDismiss = { viewModel.handleIntent(ProfileIntent.EditDismissed) },
                )
            }

            if (state.isLogoutDialogVisible) {
                BaseAlertDialog(
                    dismissText = stringResource(R.string.action_cancel),
                    confirmText = stringResource(R.string.action_logout),
                    onDismiss = { viewModel.handleIntent(ProfileIntent.LogoutDismissed) },
                    onConfirm = { viewModel.handleIntent(ProfileIntent.LogoutConfirmed) },
                    title = stringResource(R.string.label_logout),
                    description = stringResource(R.string.msg_logout_confirmation)
                )
            }
        }
    }
}






