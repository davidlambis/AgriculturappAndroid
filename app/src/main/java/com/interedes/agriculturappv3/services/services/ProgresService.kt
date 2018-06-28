package com.interedes.agriculturappv3.services.services

import android.app.IntentService
import android.app.Service
import android.content.Context
import android.content.Intent
import android.os.Handler
import android.os.IBinder
import android.support.v4.app.JobIntentService
import android.support.v4.app.NotificationCompat
import android.support.v4.content.LocalBroadcastManager
import android.util.Log
import android.widget.Toast
import com.interedes.agriculturappv3.services.Const
import android.os.SystemClock
import android.app.job.JobParameters
import android.app.NotificationManager
import android.app.NotificationChannel
import android.os.Build



class ProgresService: JobIntentService() {

    private val TAG = JobIntentService::class.java.simpleName
    private var StartService=false
    private var Count=0




    override fun onHandleWork(intent: Intent) {
        if (intent != null) {
            val action = intent.action
            if (Const.ACTION_RUN_ISERVICE.equals(action)) {
                handleActionRun()
            }
        }
    }

    /**
     * Maneja la acción de ejecución del servicio
     */
    private fun handleActionRun() {
        Log.i("D", "start")
        val CHANNEL_ID = "my_channel_01"
        val mBuilder = NotificationCompat.Builder(this, CHANNEL_ID)

                .setContentTitle("My notification")
                .setContentText("Hello World!")

        startForeground(-1, mBuilder.build())
        stopForeground(true)




        for (i in 1..10) {

            /* Log.d(TAG, i.toString() + "") // Logueo

             // Poner en primer plano
             // builder.setProgress(10, i, false);
             builder.setProgress(0, 0, true)

             startForeground(1, builder.build())

             val localIntent = Intent(Const.ACTION_RUN_ISERVICE)
                     .putExtra(Const.EXTRA_PROGRESS, i)

             // Emisión de {@code localIntent}
             LocalBroadcastManager.getInstance(this).sendBroadcast(localIntent)*/

            // Retardo de 1 segundo en la iteración
            Thread.sleep(1000)
        }

        stopSelf()

    }


    override fun onDestroy() {
        Toast.makeText(this, "Servicio destruido...", Toast.LENGTH_SHORT).show()
        //StartService=false
        // Emisión para avisar que se terminó el servicio
        //val localIntent = Intent(Const.ACTION_PROGRESS_EXIT)
        //LocalBroadcastManager.getInstance(this).sendBroadcast(localIntent)
        //Log.d(TAG, "Servicio destruido...")
    }



    override fun onCreate() {
        super.onCreate()

    }


    private fun fakeStartForeground() {
        val builder = NotificationCompat.Builder(this, "2")
                .setContentTitle("")
                .setContentText("")
        startForeground(1, builder.build())
    }
}