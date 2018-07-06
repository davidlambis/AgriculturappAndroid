package com.interedes.agriculturappv3.services.jobs

import com.evernote.android.job.Job
import com.evernote.android.job.JobCreator

class FotosEnfermedadesJobCreator: JobCreator {
    override fun create(tag: String): Job? {
        when (tag) {
            FotosEnfermedadesInsumosjob.TAG -> return FotosEnfermedadesInsumosjob()
            else -> return null
        }
    }
}