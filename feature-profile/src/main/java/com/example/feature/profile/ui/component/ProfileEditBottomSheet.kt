package com.example.feature.profile.ui.component

import android.content.Context
import android.net.Uri
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.example.core.ui.component.BaseButton
import com.example.core.ui.component.BaseTextField
import com.example.core.ui.component.ThemeSetting
import com.example.feature.profile.R

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProfileEditBottomSheet(
    photoUrl: Uri?,
    name: String,
    onPhotoChange: () -> Unit,
    onNameChange: (String) -> Unit,
    isDarkTheme: Boolean,
    onThemeChanged: (Boolean) -> Unit,
    onSave: () -> Unit,
    onDismiss: () -> Unit,
    isLoading: Boolean,
    context: Context
) {
    ModalBottomSheet(
        onDismissRequest = { if (!isLoading) { onDismiss() } },
        containerColor = MaterialTheme.colorScheme.background,

    ) {
        Column(modifier = Modifier.padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            ProfileEditPhoto(
                photoUrl = photoUrl,
                isDarkTheme = isDarkTheme,
                onClick = onPhotoChange,
                context = context
            )
            Spacer(Modifier.height(16.dp))
            BaseTextField(
                value = name,
                onValueChange = onNameChange,
                placeholder = stringResource(R.string.placeholder_name),
            )
            Spacer(Modifier.height(16.dp))
            ThemeSetting(
                isDarkTheme = isDarkTheme,
                onChange = onThemeChanged
            )
            Spacer(Modifier.height(16.dp))
            BaseButton(
                text = stringResource(R.string.action_save),
                onClick = onSave,
                enabled = !isLoading
            )
        }
    }
}
