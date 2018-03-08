package com.interedes.agriculturappv3

import android.app.Application
import android.os.StrictMode
import com.google.firebase.FirebaseApp
import com.google.firebase.database.FirebaseDatabase
import com.interedes.agriculturappv3.asistencia_tecnica.services.internet_connection.ConnectivityReceiver
import com.raizlabs.android.dbflow.config.FlowConfig
import com.raizlabs.android.dbflow.config.FlowManager


class AgriculturApplication : Application() {

    companion object {
        @get:Synchronized
        lateinit var instance: AgriculturApplication
    }

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


}