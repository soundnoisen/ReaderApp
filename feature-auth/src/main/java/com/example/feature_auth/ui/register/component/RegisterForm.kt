package com.example.feature_auth.ui.register.component

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.runtime.Composable
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.example.core.ui.component.BaseErrorText
import com.example.core.ui.component.BasePasswordTextField
import com.example.core.ui.component.BaseTextField
import com.example.feature_auth.R

@Composable
fun RegisterForm(
    email: String,
    emailError: String? = null,
    password: String,
    passwordError: String? = null,
    passwordConfirm: String,
    passwordConfirmError: String? = null,
    enabled:  Boolean,
    onEmailChange: (String) -> Unit,
    onPasswordChange: (String) -> Unit,
    onPasswordConfirmChange: (String) -> Unit
) {
    Column(verticalArrangement = Arrangement.spacedBy(16.dp)) {
        BaseTextField(
            value = email,
            onValueChange = onEmailChange,
            enabled = enabled,
            placeholder = stringResource(R.string.placeholder_email)
        )
        BaseErrorText(
            visibility = !emailError.isNullOrEmpty(),
            text = emailError.orEmpty()
        )
        BasePasswordTextField(
            value = password,
            onValueChange = onPasswordChange,
            enabled = enabled,
            placeholder = stringResource(R.string.placeholder_password)
        )
        BaseErrorText(
            visibility = !passwordError.isNullOrEmpty(),
            text = passwordError.orEmpty()
        )
        BasePasswordTextField(
            value = passwordConfirm,
            onValueChange = onPasswordConfirmChange,
            enabled = enabled,
            placeholder = stringResource(R.string.placeholder_confirm_password)
        )
        BaseErrorText(
            visibility = !passwordConfirmError.isNullOrEmpty(),
            text = passwordConfirmError.orEmpty()
        )
    }
}