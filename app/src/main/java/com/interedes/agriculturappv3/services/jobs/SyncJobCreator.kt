package com.interedes.agriculturappv3.services.jobs

import com.evernote.android.job.JobCreator
import com.evernote.android.job.Job

class SyncJobCreator: JobCreator {
    override fun create(tag: String): Job? {
        when (tag) {
            DataSyncJob.TAG -> return DataSyncJob()
           // FotosEnfermedadesInsumosjob.TAG -> return FotosEnfermedadesInsumosjob()
            else -> return null
        }
    }
}