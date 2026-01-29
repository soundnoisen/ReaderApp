package com.example.feature_auth.ui.login.component

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.width
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.feature_auth.R

@Composable
fun LoginFooter(
    onClick: () -> Unit
) {
    Row(horizontalArrangement = Arrangement.Center) {
        Text(
            text = stringResource(R.string.label_no_account),
            fontSize = 16.sp,
            color = MaterialTheme.colorScheme.onBackground
        )
        Spacer(modifier = Modifier.width(4.dp))
        Text(
            modifier = Modifier.clickable { onClick() },
            text = stringResource(R.string.link_register),
            fontSize = 16.sp,
            color = MaterialTheme.colorScheme.primary
        )
    }
}
