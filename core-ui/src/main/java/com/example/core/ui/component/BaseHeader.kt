package com.example.core.ui.component

import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.sp

@Composable
fun BaseHeader(text: String, modifier: Modifier = Modifier){
    Text(
        modifier = modifier,
        text = text,
        fontSize = 20.sp,
        textAlign = TextAlign.Center,
        fontWeight = FontWeight.SemiBold
    )
}