package com.example.core.ui.splash

import android.view.animation.OvershootInterpolator
import androidx.compose.animation.core.animate
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.scale
import androidx.compose.ui.res.painterResource
import com.example.core.ui.R
import kotlinx.coroutines.delay

@Composable
fun SplashScreen(onAnimationFinished: () -> Unit) {
    var scale by remember { mutableFloatStateOf(0.5f) }

    LaunchedEffect(Unit) {
        animate(
            initialValue = 0.5f,
            targetValue = 1.3f,
            animationSpec = tween(
                durationMillis = 600,
                easing = { OvershootInterpolator().getInterpolation(it) }
            )
        ) { value, _ ->
            scale = value
        }
        animate(
            initialValue = 1.3f,
            targetValue = 1f,
            animationSpec = tween(300)
        ) { value, _ ->
            scale = value
        }
        delay(100)
        onAnimationFinished()
    }
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background),
        contentAlignment = Alignment.Center
    ) {
        Icon(
            painter = painterResource(R.drawable.ic_logo),
            contentDescription = "Logo",
            modifier = Modifier.scale(scale),
            tint = MaterialTheme.colorScheme.primary
        )
    }
}