package com.interedes.agriculturappv3

import android.app.Application
import com.google.firebase.database.FirebaseDatabase


class AgriculturApplication : Application() {


    override fun onCreate() {
        super.onCreate()
        FirebaseDatabase.getInstance().setPersistenceEnabled(true)
    }
}