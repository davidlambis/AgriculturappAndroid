package com.interedes.agriculturappv3.services.jobs

import android.content.Context
import com.interedes.agriculturappv3.modules.models.sincronizacion.QuantitySync

interface IMainViewJob {
    interface Repository {
        fun syncQuantityData(): QuantitySync
        fun syncFotos(context: Context)
       // fun syncQuantityData(): QuantitySync

    }
}