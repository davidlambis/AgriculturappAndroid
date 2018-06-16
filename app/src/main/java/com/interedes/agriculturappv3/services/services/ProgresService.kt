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





class ProgresService: JobIntentService() {




    private val TAG = ProgressIntentService::class.java.simpleName
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
        Toast.makeText(this, "Servicio destruido...", Toast.LENGTH_SHORT).show()

        /*try {
            // Se construye la notificación
            val builder = NotificationCompat.Builder(this)
                    .setSmallIcon(android.R.drawable.stat_sys_download_done)
                    .setContentTitle("Servicio en segundo plano")
                    .setContentText("Procesando...")




            // Bucle de simulación
            for (i in 1..10) {

                Log.d(TAG, i.toString() + "") // Logueo

                // Poner en primer plano
                // builder.setProgress(10, i, false);
                builder.setProgress(0, 0, true)

                startForeground(1, builder.build())

                val localIntent = Intent(Const.ACTION_RUN_ISERVICE)
                        .putExtra(Const.EXTRA_PROGRESS, i)

                // Emisión de {@code localIntent}
                LocalBroadcastManager.getInstance(this).sendBroadcast(localIntent)

                // Retardo de 1 segundo en la iteración
                Thread.sleep(1000)
            }
            // Quitar de primer plano
            //stopForeground(true)
        } catch (e: InterruptedException) {
            e.printStackTrace()
        }
*/
        /*
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
        }*/

    }


    fun onStartJob(params: JobParameters): Boolean {
        handleActionRun()
        return true
    }

    fun onStopJob(params: JobParameters): Boolean {
        // whether or not you would like JobScheduler to automatically retry your failed job.
        return false
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