package com.example.appslist

import android.annotation.TargetApi
import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.Service
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.os.Build
import android.os.IBinder

class BackgroundService : Service() {

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        startForeground(1, createNotification())
        registerPackageChangeReceiver()
        return START_STICKY
    }

    @TargetApi(Build.VERSION_CODES.O)
    private fun createNotification(): Notification {
        val channelId = "background_service_channel"
        val channelName = "Background Service"
        val notificationText = "Background service is running."
        val notificationManager =
            getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager?
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel =
                NotificationChannel(channelId, channelName, NotificationManager.IMPORTANCE_DEFAULT)
            notificationManager!!.createNotificationChannel(channel)
        }
        return Notification.Builder(this, channelId)
            .setContentTitle(channelName)
            .setContentText(notificationText)
            .setSmallIcon(R.mipmap.ic_launcher)
            .build()
    }

    private fun registerPackageChangeReceiver() {
        val packageChangeReceiver = PackageChangeReceiver()
        val intentFilter = IntentFilter()
        intentFilter.addAction(Intent.ACTION_PACKAGE_ADDED)
        intentFilter.addAction(Intent.ACTION_PACKAGE_REMOVED)
        intentFilter.addDataScheme("package")
        registerReceiver(packageChangeReceiver, intentFilter)
    }

    override fun onBind(intent: Intent?): IBinder? {
        return null
    }

}