package com.interedes.agriculturappv3.services.jobs.creators

import com.evernote.android.job.Job
import com.evernote.android.job.JobCreator
import com.interedes.agriculturappv3.services.jobs.FotosEnfermedadesInsumosjob

class FotosEnfermedadesJobCreator: JobCreator {
    override fun create(tag: String): Job? {
        when (tag) {
            FotosEnfermedadesInsumosjob.TAG -> return FotosEnfermedadesInsumosjob()
            else -> return null
        }
    }
}