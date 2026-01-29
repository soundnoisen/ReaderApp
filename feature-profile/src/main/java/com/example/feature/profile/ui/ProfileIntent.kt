package com.example.feature.profile.ui

import android.net.Uri

sealed class ProfileIntent {
    object EditClicked : ProfileIntent()
    object EditDismissed : ProfileIntent()
    object SelectPhotoClicked : ProfileIntent()
    data class PhotoSelected(val uri: Uri) : ProfileIntent()
    data class NameChanged(val name: String) : ProfileIntent()
    data class ThemeChanged(val isDarkTheme: Boolean): ProfileIntent()
    object SaveClicked : ProfileIntent()
    object LogoutClicked : ProfileIntent()
    object LogoutDismissed : ProfileIntent()
    object LogoutConfirmed : ProfileIntent()
}