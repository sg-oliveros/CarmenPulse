package com.example.carmenpulse.util

import android.content.Context
import androidx.work.Constraints
import androidx.work.ExistingPeriodicWorkPolicy
import androidx.work.NetworkType
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.PeriodicWorkRequestBuilder
import androidx.work.WorkManager
import java.util.concurrent.TimeUnit

object NotificationWorker {

    fun schedule(context: Context) {
        val constraints = Constraints.Builder()
            .setRequiredNetworkType(NetworkType.CONNECTED)
            .build()

        // Immediate background check
        val immediateRequest = OneTimeWorkRequestBuilder<NotificationBackgroundWorker>()
            .setConstraints(constraints)
            .build()

        WorkManager.getInstance(context).enqueue(immediateRequest)

        // Periodic 15-minute background worker
        val workRequest = PeriodicWorkRequestBuilder<NotificationBackgroundWorker>(
            15, TimeUnit.MINUTES
        )
            .setConstraints(constraints)
            .build()

        WorkManager.getInstance(context).enqueueUniquePeriodicWork(
            "advisory_background_worker",
            ExistingPeriodicWorkPolicy.KEEP,
            workRequest
        )
    }
}
