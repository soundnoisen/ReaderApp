package com.example.core.data.util

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import androidx.core.app.NotificationCompat
import com.example.core.data.R

object NotificationUtils {

    const val CHANNEL_ID = "book_transfer_channel"
    const val CHANNEL_NAME = "Book Transfers"

    fun createChannel(context: Context) {
        val manager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        if (manager.getNotificationChannel(CHANNEL_ID) == null) {
            manager.createNotificationChannel(
                NotificationChannel(
                    CHANNEL_ID,
                    CHANNEL_NAME,
                    NotificationManager.IMPORTANCE_DEFAULT
                )
            )
        }
    }

    fun buildProgressNotification(context: Context, title: String, progress: Int, indeterminate: Boolean = false): Notification {
        return NotificationCompat.Builder(context, CHANNEL_ID)
            .setContentTitle(title)
            .setSmallIcon(R.drawable.ic_logo_notification)
            .setProgress(100, progress, indeterminate)
            .setOngoing(true)
            .setOnlyAlertOnce(true)
            .build()
    }

    fun buildCompleteNotification(context: Context, title: String, success: Boolean): Notification {
        val message = if (success) context.getString(R.string.notification_upload_complete)
        else context.getString(R.string.notification_upload_failed)

        val fullTitle = "${context.getString(R.string.notification_title)} «$title»"

        return NotificationCompat.Builder(context, CHANNEL_ID)
            .setContentTitle(fullTitle)
            .setSmallIcon(R.drawable.ic_logo_notification)
            .setContentText(message)
            .setOngoing(false)
            .build()
    }

    fun buildForegroundNotification(context: Context, title: String): Notification {
        return buildProgressNotification(context, title, progress = 0, indeterminate = true)
    }
}