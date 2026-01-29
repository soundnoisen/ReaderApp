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

        if (!networkChecker.isNetworkAvailable()) {
            emitError(UpdateProfileError.Network, canRetry = true)
            return
        }

        viewModelScope.launch {
            _state.update { it.copy(isLoading = true) }
            try {
                updateProfile.invoke(name, uri).let { result ->
                    when (result) {
                        is UpdateProfileResult.Error -> emitError(result.error)
                        is UpdateProfileResult.Success -> {
                            _state.update { current ->
                                var updated = current
                                if (!name.isNullOrBlank()) {
                                    updated = updated.copy(name = name)
                                }
                                result.uri?.let { updated = updated.copy(photoUri = it) }
                                updated
                            }
                            _effect.emit(ProfileEffect.UploadProfileSuccessToast)
                        }
                    }
                }
            } finally {
                _state.update { it.copy(isLoading = false, isEditVisible = false, editName = null, editPhotoUri = null) }
            }
        }
    }


    private fun loadProfile() {
        observeProfile()
            .catch { e ->
                emitError(UpdateProfileError.Unknow(e.message.toString()))
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
        viewModelScope.launch {
            _effect.emit(ProfileEffect.ThemeChange(isDarkTheme))
        }
    }

    private fun logoutDismissed() {
        _state.update { it.copy(isLogoutDialogVisible = false) }
    }

    private fun logoutConfirmed() {
        viewModelScope.launch {
            logoutProfile.invoke()
            _state.update { it.copy(isLogoutDialogVisible = false) }
            _effect.emit(ProfileEffect.NavigateToLogin)
        }
    }

    private fun editDismissed() {
        if (!_state.value.isLoading) {
            _state.update { it.copy(isEditVisible = false, editName = null, editPhotoUri = null) }
        }
    }

    private fun logoutClicked() {
        _state.update { it.copy(isLogoutDialogVisible = !it.isLogoutDialogVisible) }
    }

    private fun editClicked() {
        _state.update { it.copy(isEditVisible = !it.isEditVisible) }
    }

    private fun photoSelected(uri: Uri) {
        _state.update { it.copy(editPhotoUri = uri) }
    }

    private fun selectPhotoClicked() {
        viewModelScope.launch {
            _effect.emit(ProfileEffect.OpenFilePicker)
        }
    }

    private fun nameChanged(name: String) {
        _state.update { it.copy(editName = name) }
    }

    private fun emitError(error: UpdateProfileError, canRetry: Boolean = false) {
        viewModelScope.launch {
            _effect.emit(ProfileEffect.ShowError(error, canRetry))
        }
    }
}