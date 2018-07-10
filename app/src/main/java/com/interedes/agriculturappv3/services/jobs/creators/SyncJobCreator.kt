package com.interedes.agriculturappv3.services.jobs.creators

import com.evernote.android.job.JobCreator
import com.evernote.android.job.Job
import com.interedes.agriculturappv3.services.jobs.DataSyncJob
import android.R.attr.tag
import com.interedes.agriculturappv3.services.jobs.ChatRunJob
import com.interedes.agriculturappv3.services.jobs.ControlPlagasJob
import com.interedes.agriculturappv3.services.jobs.FotosEnfermedadesInsumosjob


class SyncJobCreator: JobCreator {
    override fun create(tag: String): Job? {

        when (tag) {
            DataSyncJob.TAG -> return DataSyncJob()

            FotosEnfermedadesInsumosjob.TAG -> return FotosEnfermedadesInsumosjob()

            ChatRunJob.TAG -> return ChatRunJob()

            ControlPlagasJob.TAG -> return ControlPlagasJob()

            else -> return null
        }
    }
}