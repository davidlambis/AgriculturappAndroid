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



class ProgresService: IntentService("ProgressIntentService") {

    private val TAG = ProgressIntentService::class.java.simpleName
    private var StartService=false
    private var Count=0

    override fun onHandleIntent(intent: Intent?) {
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
        try {


            if(StartService==false){
                // Se construye la notificación
                val builder = NotificationCompat.Builder(this)
                        .setSmallIcon(android.R.drawable.stat_sys_download_done)
                        .setContentTitle("Servicio en segundo plano")
                        .setContentText("Procesando...")

                builder.setProgress(0, 0, true)

                startForeground(1, builder.build())
            }



            Thread.sleep(1000)
            StartService=true
            Count=Count+1

            if(Count==10){
                stopForeground(true)
                Toast.makeText(this, "Aqui...", Toast.LENGTH_SHORT).show()
            }else{

                handleActionRun()
            }



            // Quitar de primer plano
            //stopForeground(true)
        } catch (e: InterruptedException) {
            e.printStackTrace()
        }

    }

    override fun onDestroy() {
        Toast.makeText(this, "Servicio destruido...", Toast.LENGTH_SHORT).show()
        StartService=false
        // Emisión para avisar que se terminó el servicio
        val localIntent = Intent(Const.ACTION_PROGRESS_EXIT)
        LocalBroadcastManager.getInstance(this).sendBroadcast(localIntent)

        Log.d(TAG, "Servicio destruido...")
    }
}