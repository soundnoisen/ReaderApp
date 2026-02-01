package com.example.feature.upload.ui.component

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.airbnb.lottie.compose.LottieAnimation
import com.airbnb.lottie.compose.LottieCompositionSpec
import com.airbnb.lottie.compose.animateLottieCompositionAsState
import com.airbnb.lottie.compose.rememberLottieComposition
import com.example.feature.upload.R

@Composable
fun UploadSuccessAnimation(isDarkTheme: Boolean, onAnimationFinished: () -> Unit) {
    val animationId = if (isDarkTheme) R.raw.dark_success else R.raw.light_success
    val composition by rememberLottieComposition(LottieCompositionSpec.RawRes(animationId))
    val animationState = animateLottieCompositionAsState(
        composition = composition,
        iterations = 1,
        isPlaying = true
    )

    LaunchedEffect(animationState.isPlaying, animationState.progress) {
        if (!animationState.isPlaying && animationState.progress == 1f) {
            onAnimationFinished()
        }
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.Transparent)
            .padding(bottom = 32.dp),
        contentAlignment = Alignment.Center
    ) {
        LottieAnimation(
            composition = composition,
            progress = { animationState.progress },
            modifier = Modifier.size(80.dp)
        )
    }
}