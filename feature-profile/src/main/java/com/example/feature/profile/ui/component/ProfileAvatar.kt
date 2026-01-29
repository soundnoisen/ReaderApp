package com.example.feature.profile.ui.component

import android.content.Context
import android.net.Uri
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import coil.request.ImageRequest
import com.example.feature.profile.R

@Composable
fun ProfilePhoto(
    context: Context,
    photoUri: Uri?,
    isDarkTheme: Boolean,
) {
    val placeholder = painterResource(
        if (isDarkTheme) R.drawable.ic_dark_avatar_placeholder
        else R.drawable.ic_light_avatar_placeholder
    )

    AsyncImage(
        model = ImageRequest.Builder(context)
            .data(photoUri)
            .setParameter("theme", isDarkTheme)
            .build(),
        contentDescription = null,
        modifier = Modifier
            .size(120.dp)
            .clip(CircleShape)
            .background(MaterialTheme.colorScheme.onSecondary.copy(0.1f)),
        contentScale = ContentScale.Crop,
        placeholder = placeholder,
        error = placeholder
    )
}
