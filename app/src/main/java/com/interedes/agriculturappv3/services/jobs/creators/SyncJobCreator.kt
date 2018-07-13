package com.interedes.agriculturappv3.services.jobs.creators

import com.evernote.android.job.JobCreator
import com.evernote.android.job.Job
import android.R.attr.tag
import com.interedes.agriculturappv3.services.jobs.*


class SyncJobCreator: JobCreator {
    override fun create(tag: String): Job? {

        when (tag) {
            DataSyncJob.TAG -> return DataSyncJob()

            FotosEnfermedadesInsumosjob.TAG -> return FotosEnfermedadesInsumosjob()

            ChatRunJob.TAG -> return ChatRunJob()

            ControlPlagasJob.TAG -> return ControlPlagasJob()

            FotoPerfilJob.TAG -> return FotoPerfilJob()

            SyncFotoProductosJob.TAG -> return SyncFotoProductosJob()
            //DataSyncUpdateJob.TAG -> return DataSyncUpdateJob()
            else -> return null
        }
    }
}