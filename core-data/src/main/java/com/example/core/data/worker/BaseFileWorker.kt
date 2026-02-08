package com.example.core.data.worker

import android.app.NotificationManager
import android.content.Context
import android.content.pm.ServiceInfo.FOREGROUND_SERVICE_TYPE_DATA_SYNC
import android.os.Build
import androidx.work.CoroutineWorker
import androidx.work.ForegroundInfo
import androidx.work.WorkerParameters
import com.example.core.data.util.NotificationUtils

abstract class BaseFileWorker(
    context: Context,
    params: WorkerParameters
): CoroutineWorker(context, params) {

    protected val notificationManager: NotificationManager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

    protected fun createForegroundInfo(notificationId: Int, title: String): ForegroundInfo {
        val notification = NotificationUtils.buildForegroundNotification(applicationContext, title)
        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            ForegroundInfo(notificationId, notification, FOREGROUND_SERVICE_TYPE_DATA_SYNC)
        } else {
            ForegroundInfo(notificationId, notification)
        }
    }

    protected fun notifyComplete(notificationId: Int, title: String, success: Boolean) {
        notificationManager.notify(
            notificationId,
            NotificationUtils.buildCompleteNotification(applicationContext, title, success)
        )
    }
}