package com.example.feature_auth.ui.login.component

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
fun LoginForm(
    email: String,
    emailError: String? = null,
    password: String,
    passwordError: String? = null,
    enabled: Boolean = true,
    onEmailChange: (String) -> Unit,
    onPasswordChange: (String) -> Unit,
) {
    Column(verticalArrangement = Arrangement.spacedBy(16.dp)) {
        BaseTextField(
            value = email,
            enabled = enabled,
            onValueChange = onEmailChange,
            placeholder = stringResource(R.string.placeholder_email)
        )
        BaseErrorText(
            visibility = !emailError.isNullOrEmpty(),
            text = emailError.orEmpty()
        )
        BasePasswordTextField(
            value = password,
            enabled = enabled,
            onValueChange = onPasswordChange,
            placeholder = stringResource(R.string.placeholder_password)
        )
        BaseErrorText(
            visibility = !passwordError.isNullOrEmpty(),
            text = passwordError.orEmpty()
        )
    }
}
