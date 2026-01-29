package com.example.feature.profile.ui.component

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.sp

@Composable
fun ProfileName(name: String) {
    Text(
        text = name,
        fontSize = 18.sp,
        color = MaterialTheme.colorScheme.onBackground,
        textAlign = TextAlign.Center,
        fontWeight = FontWeight.Medium
    )
}