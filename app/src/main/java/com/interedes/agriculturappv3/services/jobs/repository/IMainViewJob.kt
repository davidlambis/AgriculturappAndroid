package com.interedes.agriculturappv3.services.jobs.repository

import android.content.Context
import com.interedes.agriculturappv3.modules.models.sincronizacion.QuantitySync

interface IMainViewJob {
    interface Repository {
        fun syncQuantityData(): QuantitySync
        fun syncFotos(context: Context)
        fun updateUserStatus()

        fun getListSyncEnfermedadesAndTratamiento(context: Context)


        fun checkControlPlagas(context: Context)
        // fun syncQuantityData(): QuantitySync

    }
}