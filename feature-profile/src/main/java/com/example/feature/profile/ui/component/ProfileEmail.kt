package com.example.feature.profile.ui.component

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.sp

@Composable
fun ProfileEmail(email: String) {
    Text(
        text = email,
        fontSize = 16.sp,
        color = MaterialTheme.colorScheme.onSecondary,
        textAlign = TextAlign.Center
    )
}