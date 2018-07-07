package com.interedes.agriculturappv3.services.jobs.creators

import com.evernote.android.job.JobCreator
import com.evernote.android.job.Job
import com.interedes.agriculturappv3.services.jobs.DataSyncJob

class SyncJobCreator: JobCreator {
    override fun create(tag: String): Job? {
        when (tag) {
            DataSyncJob.TAG -> return DataSyncJob()
           // FotosEnfermedadesInsumosjob.TAG -> return FotosEnfermedadesInsumosjob()
            else -> return null
        }
    }
}