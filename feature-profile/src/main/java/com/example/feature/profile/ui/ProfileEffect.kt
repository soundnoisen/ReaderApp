package com.example.feature.profile.ui

import com.example.core.domain.model.profile.UpdateProfileError

sealed class ProfileEffect {
    object OpenFilePicker: ProfileEffect()
    object NavigateToLogin: ProfileEffect()
    data class ShowError(val error: UpdateProfileError, val canRetry: Boolean = false): ProfileEffect()
    data class ThemeChange(val isDarkTheme: Boolean): ProfileEffect()
    object UploadProfileSuccess: ProfileEffect()
}