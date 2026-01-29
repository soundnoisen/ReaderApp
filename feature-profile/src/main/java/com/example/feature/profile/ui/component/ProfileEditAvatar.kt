package com.example.feature.profile.ui.component

import android.content.Context
import android.net.Uri
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import coil.request.ImageRequest
import com.example.feature.profile.R

@Composable
fun ProfileEditPhoto(
    photoUrl: Uri?,
    isDarkTheme: Boolean,
    onClick: () -> Unit,
    context: Context
) {
    val placeholder = painterResource(
        if (isDarkTheme) R.drawable.ic_dark_avatar_placeholder
        else R.drawable.ic_light_avatar_placeholder
    )

    Box(
        contentAlignment = Alignment.Center
    ) {
        AsyncImage(
            model = ImageRequest.Builder(context)
                .data(photoUrl)
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
        IconButton(
            onClick = onClick,
            modifier = Modifier
                .align(Alignment.BottomEnd)
                .offset(y = (-6).dp)
                .background(
                    color = MaterialTheme.colorScheme.primary,
                    shape = CircleShape
                )
        ) {
            Icon(
                painter = painterResource(R.drawable.ic_select_avatar),
                contentDescription = null,
                modifier = Modifier.size(28.dp),
                tint = MaterialTheme.colorScheme.onPrimary
            )
        }

    }
}