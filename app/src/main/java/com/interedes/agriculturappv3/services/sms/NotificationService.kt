package com.interedes.agriculturappv3.services.sms

import android.app.Service
import android.content.Intent
import android.telephony.TelephonyManager
import android.content.IntentFilter
import android.os.*


class NotificationService: Service() {

    private var serviceLooper: Looper? = null
    private var serviceHandler: ServiceHandler? = null

    private var receiver: MySmsBroadcastReceiver? = null

    // Handler that receives messages from the thread
    private inner class ServiceHandler(looper: Looper?) : Handler(looper) {
        override fun handleMessage(msg: Message) {
            // TODO: Stop this thread when application is opened?
            val filter = IntentFilter()
            filter.addAction("android.provider.Telephony.SMS_RECIEVED")
            filter.addAction(TelephonyManager.ACTION_PHONE_STATE_CHANGED)
            registerReceiver(receiver, filter)
            //            registerReceiver(notificationReceiver, filter);
        }
    }

    override fun onCreate() {
        // Start up the thread running the service.  Note that we create a
        // separate thread because the service normally runs in the process's
        // main thread, which we don't want to block.  We also make it
        // background priority so CPU-intensive work will not disrupt our UI.
        val thread = HandlerThread("ServiceStartArguments", Process.THREAD_PRIORITY_BACKGROUND)
        thread.start()

        // Get the HandlerThread's Looper and use it for our Handler
        serviceLooper = thread.looper
        serviceHandler = ServiceHandler(serviceLooper)
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        // For each start request, send a message to start a job and deliver the
        // start ID so we know which request we're stopping when we finish the job
        val msg = serviceHandler?.obtainMessage()
        msg?.arg1 = startId
        serviceHandler?.sendMessage(msg)

        // If we get killed, after returning from here, restart
        return START_STICKY
    }

    override fun onBind(intent: Intent?): IBinder? {
        // We don't provide binding, so return null
        return null
    }


    override fun onDestroy() {
        if (receiver!=null) {
            unregisterReceiver(receiver)
        }
    }
}