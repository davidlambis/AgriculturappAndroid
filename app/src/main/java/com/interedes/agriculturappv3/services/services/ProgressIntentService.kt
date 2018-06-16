package com.interedes.agriculturappv3.services.services

import android.app.IntentService;
import android.content.Intent;
import android.support.v4.app.NotificationCompat;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;
import android.widget.Toast;
import com.interedes.agriculturappv3.services.Const

class ProgressIntentService: IntentService("ProgressIntentService") {

    private val TAG = ProgressIntentService::class.java.simpleName


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

    }

    override fun onDestroy() {
        Toast.makeText(this, "Servicio destruido...", Toast.LENGTH_SHORT).show()

        // Emisión para avisar que se terminó el servicio
        val localIntent = Intent(Const.ACTION_PROGRESS_EXIT)
        LocalBroadcastManager.getInstance(this).sendBroadcast(localIntent)

        Log.d(TAG, "Servicio destruido...")
    }
}