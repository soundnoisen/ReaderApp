package com.example.feature.profile.ui

import android.net.Uri

data class ProfileState(
    val name: String? = null,
    val email: String? = "",
    val photoUri: Uri? = null,
    val editName: String? = null,
    val editPhotoUri: Uri? = null,
    val isLoading: Boolean = false,
    val isEditVisible: Boolean = false,
    val isLogoutDialogVisible: Boolean = false
)