package com.example.core.domain.model

import android.net.Uri

data class User(
    val uid: String = "",
    val email: String? = "",
    val displayName: String? = "",
    val photoUrl: Uri? = null
)