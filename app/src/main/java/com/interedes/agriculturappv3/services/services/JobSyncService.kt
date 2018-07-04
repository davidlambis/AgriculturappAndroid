package com.interedes.agriculturappv3.services.services

import android.app.*
import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.content.ComponentName
import android.os.*
import android.support.annotation.RequiresApi
import android.support.v4.app.JobIntentService
import android.widget.Toast
import android.app.NotificationManager
import android.app.NotificationChannel
import android.os.Build
import com.interedes.agriculturappv3.libs.EventBus
import com.interedes.agriculturappv3.libs.GreenRobotEventBus
import com.interedes.agriculturappv3.modules.productor.ui.main_menu.MenuMainActivity
import com.interedes.agriculturappv3.services.api.ApiInterface
import com.interedes.agriculturappv3.services.chat.SharedPreferenceHelper
import com.interedes.agriculturappv3.services.resources.Status_Sync_Data_Resources
import com.interedes.agriculturappv3.services.services.Events.EventsService
import com.interedes.agriculturappv3.services.services.request.RequestPostDataSync
import org.greenrobot.eventbus.Subscribe



class JobSyncService : JobIntentService() {

    companion object {

        lateinit var instance: JobSyncService

        var runnableService:Boolean=false

        //SYNC
        var runnableGlobal:Runnable?=null
        var eventBus: EventBus? = null
        var apiService: ApiInterface? = null
        var repository: IMainViewService.RepositoryPost? = null


        //region NOTIFCATION
        private const val TAG = "MainService"
        const val EXTRA_NOTIFICATION_REQUEST_CODE = "EXTRA_NOTIFICATION_REQUEST_CODE"
        const val EXTRA_NOTIFICATION = "EXTRA_NOTIFICATION"
        fun showNotification(context: Context, requestCode: Int, notification: Notification, repro: Boolean, workaround: Boolean): Boolean {
            val intent = Intent(context, JobSyncService::class.java)
            intent.putExtra(EXTRA_NOTIFICATION_REQUEST_CODE, requestCode)
            intent.putExtra(EXTRA_NOTIFICATION, notification)
            intent.putExtra("repro", repro)
            return startService(context, intent, repro, workaround)
        }

        fun stop(context: Context, workaround: Boolean) {
            val intent = Intent(context, JobSyncService::class.java)
            stopService(context, intent, workaround)
        }

        /**
         * @see android.support.v4.content.ContextCompat#startForegroundService(android.content.Context, android.content.Intent)
         */
        @Suppress("MemberVisibilityCanBePrivate")
        fun startService(context: Context, intent: Intent, repro: Boolean, workaround: Boolean): Boolean {

            if (repro) {
                HANDLER.postDelayed({
                    Log.w(TAG, "startService will repro https://issuetracker.google.com/issues/76112072 by stopping service before it can call startForeground")
                    stop(context, workaround)
                }, 500)
            }
            //
            // Similar to ContextCompat.startForegroundService(context, intent)
            //
            val componentName: ComponentName? = if (Build.VERSION.SDK_INT >= 26) {
                context.startForegroundService(intent)
            } else {
                context.startService(intent)
            }
            return componentName != null
        }


        @Suppress("MemberVisibilityCanBePrivate")
        const val APPLICATION_NOT_RESPONDING_TIMEOUT_MILLIS = 500

        private val PENDING_STOP_SERVICE = mutableMapOf<Intent, Runnable>()
        private val HANDLER = Handler()

        @Suppress("MemberVisibilityCanBePrivate")
        fun stopService(context: Context, intent: Intent, workaround: Boolean) {

            if (workaround) {
                var runnable = PENDING_STOP_SERVICE[intent]
                if (runnable != null) {
                    HANDLER.removeCallbacks(runnable)
                }

                runnable = Runnable { context.stopService(intent) }
                PENDING_STOP_SERVICE[intent] = runnable
                runnableGlobal=runnable
                runnableService=true
                repository?.syncData()
               // for (i in 1..10) {
                    // Retardo de 1 segundo en la iteración
                   // HANDLER.postDelayed(runnable,10000)
               // }
               //context.stopService(intent)
                // HANDLER.postDelayed(runnable, (APPLICATION_NOT_RESPONDING_TIMEOUT_MILLIS + 10000).toLong())
            } else {
                context.stopService(intent)
            }
        }

        @RequiresApi(api = 26)
        fun createNotificationChannel(context: Context,
                                      id: String, name: String, importance: Int,
                                      description: String) {
            val channel = NotificationChannel(id, name, importance)
            channel.description = description
            createNotificationChannel(context, channel)
        }

        @Suppress("MemberVisibilityCanBePrivate")
        @RequiresApi(api = 26)
        fun createNotificationChannel(context: Context,
                                      channel: NotificationChannel) {
            val notificationManager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            notificationManager.createNotificationChannel(channel)
        }

        //endregion
    }

    init {
        instance=this
        eventBus = GreenRobotEventBus()
        apiService = ApiInterface.create()
        repository = RequestPostDataSync()
    }

    override fun onCreate() {
        //Log.d(TAG, "+onCreate()")
        super.onCreate()
        //Log.d(TAG, "-onCreate()")
        eventBus?.register(this)
    }

    //region Override Methods
    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        try {
            Log.d(TAG, "+onStartCommand(...)")
            if (intent != null) {
                val extras = intent.extras
                if (extras != null) {
                    if (extras.containsKey(EXTRA_NOTIFICATION)) {
                        val notification = extras.getParcelable<Parcelable>(EXTRA_NOTIFICATION)
                        if (notification is Notification) {
                            if (extras.containsKey(EXTRA_NOTIFICATION_REQUEST_CODE)) {
                                val requestCode = extras.getInt(EXTRA_NOTIFICATION_REQUEST_CODE)
                                val repro = extras.getBoolean("repro")
                                if (repro) {
                                    Log.w(TAG, "onStartCommand will repro https://issuetracker.google.com/issues/76112072 by delaying startForeground")
                                    startForeground(requestCode, notification)

                                    HANDLER.postDelayed({
                                        Log.w(TAG, "onStartCommand delayed startForeground")
                                        startForeground(requestCode, notification)
                                    }, 500)
                                } else {
                                    startForeground(requestCode, notification)
                                }
                            }
                        }
                    }
                }
            }
            return START_NOT_STICKY
        } finally {
            Log.d(TAG, "-onStartCommand(...)")
        }
    }

    override fun onHandleWork(intent: Intent) {

    }

    override fun onDestroy() {
        Log.d(TAG, "+onDestroy()")
        runnableService=false
        //Toast.makeText(this, "Servicio destruido...", Toast.LENGTH_SHORT).show()
        //PbLog.s(TAG, PbStringUtils.separateCamelCaseWords("onDestroy"));
        super.onDestroy()
        SharedPreferenceHelper.getInstance(this).savePostSyncData(Status_Sync_Data_Resources.STOP);
        stopForeground(true)
        Log.d(TAG, "-onDestroy()")
        eventBus?.unregister(this)

        //MenuMainActivity.instance?.RUNING_SYNC=false
    }

    override fun onBind(intent: Intent): IBinder? {
        try {
            Log.d(TAG, "+onBind(...)")
            return null
        } finally {
            Log.d(TAG, "-onBind(...)")
        }
    }

    override fun onUnbind(intent: Intent?): Boolean {
        try {
            Log.d(TAG, "+onUnbind(...)")
            return true
        } finally {
            Log.d(TAG, "-onUnbind(...)")
        }
    }


    //endregion

    //region Suscribe Events

    @Subscribe
    fun onEventMainThread(event: EventsService?) {
        when (event?.eventType) {
            EventsService.POST_SYNC_EVENT -> {
                HANDLER.postDelayed(runnableGlobal,1000)
                ///Toast.makeText(this, "Sincronizado...", Toast.LENGTH_SHORT).show()
            }
            EventsService.ERROR_EVENT -> {
                HANDLER.postDelayed(runnableGlobal,1000)
                Toast.makeText(this, event.mensajeError, Toast.LENGTH_SHORT).show()

            }
        }
    }

    //endregion

}






