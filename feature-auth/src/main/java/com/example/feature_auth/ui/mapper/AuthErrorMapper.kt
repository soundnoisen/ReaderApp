package com.example.feature_auth.ui.mapper

import android.content.res.Resources
import com.example.core.domain.model.auth.AuthError
import com.example.feature_auth.R

fun AuthError.toUiText(resources: Resources): String =
    when (this) {
        AuthError.InvalidCredentials -> resources.getString(R.string.error_invalid_credentials)
        AuthError.Network -> resources.getString(R.string.error_no_network)
        AuthError.Unknown -> resources.getString(R.string.error_unknown_error)
        AuthError.NotValidEmail -> resources.getString(R.string.error_invalid_email)
        AuthError.NotValidPassword -> resources.getString(R.string.error_invalid_password)
        AuthError.DifferentPasswords -> resources.getString(R.string.error_different_passwords)
        AuthError.UserAlreadyExist -> resources.getString(R.string.error_user_already_exists)
    }