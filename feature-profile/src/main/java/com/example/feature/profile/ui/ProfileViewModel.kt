package com.example.feature.profile.ui

import android.net.Uri
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.core.domain.model.profile.UpdateProfileError
import com.example.core.domain.model.profile.UpdateProfileResult
import com.example.core.domain.repository.NetworkCheckerRepository
import com.example.feature.profile.domain.usecase.LogoutProfileUseCase
import com.example.feature.profile.domain.usecase.ObserveProfileUseCase
import com.example.feature.profile.domain.usecase.UpdateProfileUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject


@HiltViewModel
class ProfileViewModel  @Inject constructor(
    private val networkChecker: NetworkCheckerRepository,
    private val observeProfile: ObserveProfileUseCase,
    private val updateProfile: UpdateProfileUseCase,
    private val logoutProfile: LogoutProfileUseCase,
): ViewModel() {

    private val _state = MutableStateFlow(ProfileState())
    val state: StateFlow<ProfileState> = _state.asStateFlow()

    private val _effect = MutableSharedFlow<ProfileEffect>()
    val effect: SharedFlow<ProfileEffect> = _effect.asSharedFlow()

    private var uploadJob: Job? = null

    init {
        loadProfile()
    }

    fun handleIntent(intent: ProfileIntent) {
        when(intent) {
            is ProfileIntent.PhotoSelected -> photoSelected(intent.uri)
            is ProfileIntent.EditClicked -> editClicked()
            is ProfileIntent.EditDismissed -> editDismissed()
            is ProfileIntent.LogoutClicked -> logoutClicked()
            is ProfileIntent.LogoutConfirmed -> logoutConfirmed()
            is ProfileIntent.LogoutDismissed -> logoutDismissed()
            is ProfileIntent.NameChanged -> nameChanged(intent.name)
            is ProfileIntent.SaveClicked -> saveClicked()
            is ProfileIntent.SelectPhotoClicked -> selectPhotoClicked()
            is ProfileIntent.ThemeChanged -> themeChanged(intent.isDarkTheme)
        }
    }

    private fun saveClicked() {
        val name = _state.value.editName
        val uri = _state.value.editPhotoUri

        if (name == null && uri == null)
            return emitError(UpdateProfileError.NoChanges)

        if (!networkChecker.isNetworkAvailable())
            return emitError(UpdateProfileError.Network, canRetry = true)

        uploadJob?.cancel()
        uploadJob = viewModelScope.launch {
            _state.update { it.copy(isLoading = true) }
            try {
                handleUpdateResult(updateProfile(name, uri), name)
            } finally {
                _state.update { it.copy(isLoading = false) }
            }
        }
    }

    private fun handleUpdateResult(result: UpdateProfileResult, name: String?) {
        when (result) {
            is UpdateProfileResult.Error -> emitError(result.error)
            is UpdateProfileResult.Success -> {
                applyProfileChanges(name, result.uri)
                sendEffect(ProfileEffect.UploadProfileSuccess)
            }
        }
    }

    private fun applyProfileChanges(name: String?, uri: Uri?) {
        _state.update {
            it.copy(
                name = name ?: it.name,
                photoUri = uri ?: it.photoUri,
                editName = null,
                editPhotoUri = null,
                isEditVisible = false
            )
        }
    }

    private fun loadProfile() {
        observeProfile()
            .catch { e ->
                emitError(UpdateProfileError.Unknown(e.message.toString()))
            }
            .onEach { user ->
                user?.let {
                    _state.update { current ->
                        current.copy(
                            name = it.displayName,
                            email = it.email,
                            photoUri = it.photoUrl
                        )
                    }
                }
            }
            .launchIn(viewModelScope)
    }

    private fun themeChanged(isDarkTheme: Boolean) {
        sendEffect(ProfileEffect.ThemeChange(isDarkTheme))
    }

    private fun logoutDismissed() {
        _state.update { it.copy(isLogoutDialogVisible = false) }
    }

    private fun logoutConfirmed() {
        viewModelScope.launch {
            logoutProfile()
            _state.update { it.copy(isLogoutDialogVisible = false) }
            sendEffect(ProfileEffect.NavigateToLogin)
        }
    }

    private fun editDismissed() {
        if (!_state.value.isLoading) {
            _state.update { it.copy(isEditVisible = false, editName = null, editPhotoUri = null) }
        }
    }

    private fun logoutClicked() {
        _state.update { it.copy(isLogoutDialogVisible = true) }
    }

    private fun editClicked() {
        _state.update { it.copy(isEditVisible = true) }
    }

    private fun photoSelected(uri: Uri) {
        _state.update { it.copy(editPhotoUri = uri) }
    }

    private fun selectPhotoClicked() {
        sendEffect(ProfileEffect.OpenFilePicker)
    }

    private fun nameChanged(name: String) {
        _state.update { it.copy(editName = name) }
    }

    private fun emitError(error: UpdateProfileError, canRetry: Boolean = false) {
        sendEffect(ProfileEffect.ShowError(error, canRetry))
    }

    private fun sendEffect(effect: ProfileEffect) {
        viewModelScope.launch {
            _effect.emit(effect)
        }
    }
}