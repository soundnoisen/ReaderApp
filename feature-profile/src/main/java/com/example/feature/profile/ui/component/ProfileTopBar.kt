package com.example.feature.profile.ui.component

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import com.example.core.ui.component.BaseHeader
import com.example.feature.profile.R

@Composable
fun ProfileTopBar(
    onEdit: () -> Unit,
    onLogout: () -> Unit,
    modifier: Modifier = Modifier
    ) {
    Column(modifier = modifier) {
        Row(
            Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            IconButton(onClick = onEdit) {
                Icon(painter = painterResource(R.drawable.ic_edit_profile),
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.onBackground
                )
            }
            BaseHeader(text = stringResource(R.string.title_profile))
            IconButton(
                onClick = onLogout
            ) {
                Icon(painter = painterResource(R.drawable.ic_exit_profile), contentDescription = null,
                    tint = MaterialTheme.colorScheme.onBackground
                )
            }
        }
    }
}