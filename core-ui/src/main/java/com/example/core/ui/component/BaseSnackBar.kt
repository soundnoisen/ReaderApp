package com.example.core.ui.component

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Snackbar
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.key
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.core.ui.R

@Composable
fun BaseSnackBar(
    snackBarHostState: SnackbarHostState,
    modifier: Modifier = Modifier,
    canRetry: Boolean = false,
    onRetry: (() -> Unit)? = null
) {
    Box(
        modifier = modifier.fillMaxSize(),
        contentAlignment = Alignment.TopCenter
    ) {
        SnackbarHost(
            hostState = snackBarHostState
        ) { data ->
            key(data.visuals.message) {
                AnimatedVisibility(
                    visible = true,
                ) {
                    Snackbar(
                        modifier = Modifier
                            .padding(top = 20.dp, start = 16.dp, end = 16.dp)
                            .clip(RoundedCornerShape(10.dp)),
                        containerColor =  MaterialTheme.colorScheme.errorContainer,
                        action = {
                            if (canRetry && onRetry != null) {
                                IconButton(onClick = onRetry) {
                                    Icon(
                                        painter = painterResource(id = R.drawable.ic_replay),
                                        contentDescription = null,
                                        modifier = Modifier.size(24.dp),
                                        tint = MaterialTheme.colorScheme.onErrorContainer
                                    )
                                }
                            }
                        },
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            Icon(
                                painter = painterResource(id = R.drawable.ic_warning),
                                contentDescription = null,
                                modifier = Modifier.size(24.dp),
                                tint =  MaterialTheme.colorScheme.onErrorContainer
                            )
                            Text(
                                text = data.visuals.message,
                                fontSize = 16.sp,
                                fontWeight = FontWeight.Medium,
                                color =  MaterialTheme.colorScheme.onErrorContainer
                            )
                        }
                    }
                }
            }
        }
    }
}


