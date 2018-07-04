package com.interedes.agriculturappv3

import android.annotation.SuppressLint
import android.app.Application
import android.app.Notification
import android.app.NotificationManager
import android.app.PendingIntent
import android.app.job.JobService
import android.content.Context
import android.content.Intent
import android.graphics.BitmapFactory
import android.os.Build
import android.os.StrictMode
import android.support.multidex.MultiDex
import android.util.Log
import com.google.firebase.FirebaseApp
import com.google.firebase.database.FirebaseDatabase
import com.interedes.agriculturappv3.modules.productor.ui.main_menu.MenuMainActivity
import com.interedes.agriculturappv3.services.internet_connection.ConnectivityReceiver
import com.raizlabs.android.dbflow.config.FlowConfig
import com.raizlabs.android.dbflow.config.FlowManager


import android.support.v4.app.NotificationCompat.Builder
import com.interedes.agriculturappv3.services.services.JobSyncService


class AgriculturApplication : Application() {

    companion object {
        @get:Synchronized
        lateinit var instance: AgriculturApplication


        //SERVICE
        private const val TAG = "MainApplication"
        private const val NOTIFICATION_REQUEST_CODE = 100
        private const val NOTIFICATION_CHANNEL_ID = "notification_channel_id"
    }


    private lateinit var notification: Notification

    var isNotificationShowing: Boolean = false
        private set
    var isRepro: Boolean = true
        internal set
    var isWorkaround: Boolean = true
        internal set




    @SuppressLint("InlinedApi")
    override fun onCreate() {
        super.onCreate()
        instance = this

        //Firebase
        FirebaseApp.initializeApp(this)
        FirebaseDatabase.getInstance().setPersistenceEnabled(true)

        //DbFlow
        //FlowManager.init(this)
        FlowManager.init(FlowConfig.Builder(this).build())



        //Avoiding Memory Leaks and code optimization
        val builder = StrictMode.VmPolicy.Builder()
        StrictMode.setVmPolicy(builder.build())




        if (Build.VERSION.SDK_INT >= 26) {
            val appName = getString(R.string.app_name)
            val channelName = "$appName channel name"
            val channelImportance = NotificationManager.IMPORTANCE_LOW
            val channelDescription = "$appName channel description"
            JobSyncService.createNotificationChannel(this,
                    NOTIFICATION_CHANNEL_ID,
                    channelName,
                    channelImportance,
                    channelDescription)
        }

        notification = createOngoingNotification(NOTIFICATION_REQUEST_CODE, R.drawable.ic_launcher_background, "Sincronizando informacion")
        Log.d(TAG, "-onCreate()")
    }


    override fun attachBaseContext(base: Context?) {
        super.attachBaseContext(base)
        MultiDex.install(this)
    }

    override fun onTerminate() {
        super.onTerminate()
        //DbFlow
        FlowManager.destroy()
    }

    //Listener del Broadcast Receiver de detección de Internet


    fun setConnectivityListener(listener: ConnectivityReceiver.connectivityReceiverListener) {
        ConnectivityReceiver.connectivityReceiverListener = listener
    }

    private fun createOngoingNotification(requestCode: Int, icon: Int, text: String): Notification {

        val context: Context = this

        val contentIntent = Intent(context, MenuMainActivity::class.java)
                .setAction(Intent.ACTION_MAIN)
                .addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK)
        val contentPendingIntent = PendingIntent.getActivity(context, requestCode, contentIntent, PendingIntent.FLAG_UPDATE_CURRENT)

        return Builder(context, NOTIFICATION_CHANNEL_ID)
                .setOngoing(true)
                .setAutoCancel(true)
                //.setSmallIcon(icon)
                .setSmallIcon(R.drawable.ic_stat_agrapp_icon_notificacion)
                .setLargeIcon(BitmapFactory.decodeResource(context.resources, R.mipmap.ic_launcher))
                .setContentTitle("Sincronizacion")
                .setContentText(text)
                .setContentIntent(contentPendingIntent)
                .setProgress(0, 0, true)
                .build()

    }

    fun showNotification(show: Boolean) {
        if (show) {
            JobSyncService.showNotification(this, NOTIFICATION_REQUEST_CODE, notification, isRepro, isWorkaround)
            isNotificationShowing = true
        } else {
            isNotificationShowing = false
            JobSyncService.stop(this, isWorkaround)
        }
    }


}
