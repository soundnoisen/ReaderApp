package com.example.core.ui.util

import android.Manifest
import android.content.pm.PackageManager
import android.os.Build
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalContext
import androidx.core.content.ContextCompat
import com.example.core.ui.R

@Composable
fun NotificationPermissionRequester(onPermissionDenied: (() -> Unit)? = null): () -> Unit {
    val context = LocalContext.current

    val launcher = rememberLauncherForActivityResult(contract = ActivityResultContracts.RequestPermission()) { granted ->
        if (!granted) {
            Toast.makeText(context, context.getString(R.string.msg_notifications_disabled), Toast.LENGTH_LONG).show()
            onPermissionDenied?.invoke()
        }
    }

    return {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            if (ContextCompat.checkSelfPermission(context, Manifest.permission.POST_NOTIFICATIONS) != PackageManager.PERMISSION_GRANTED) {
                launcher.launch(Manifest.permission.POST_NOTIFICATIONS)
            }
        }
    }
}
