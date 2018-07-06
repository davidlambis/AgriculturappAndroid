package com.interedes.agriculturappv3.services.jobs

import android.graphics.Bitmap
import android.util.Log
import com.evernote.android.job.Job
import com.evernote.android.job.JobManager
import com.evernote.android.job.JobRequest
import com.interedes.agriculturappv3.modules.models.insumos.Insumo
import com.interedes.agriculturappv3.modules.models.plagas.Enfermedad
import com.interedes.agriculturappv3.modules.models.plagas.Enfermedad_Table
import com.interedes.agriculturappv3.modules.models.plagas.FotoEnfermedad
import com.interedes.agriculturappv3.modules.models.usuario.Usuario
import com.interedes.agriculturappv3.modules.models.usuario.Usuario_Table
import com.interedes.agriculturappv3.services.resources.S3Resources
import com.krishna.fileloader.FileLoader
import com.krishna.fileloader.listener.FileRequestListener
import com.krishna.fileloader.pojo.FileResponse
import com.krishna.fileloader.request.FileLoadRequest
import com.raizlabs.android.dbflow.data.Blob
import com.raizlabs.android.dbflow.kotlinextensions.save
import com.raizlabs.android.dbflow.sql.language.SQLite
import id.zelory.compressor.Compressor
import java.io.ByteArrayOutputStream
import java.io.File
import java.util.concurrent.TimeUnit

class FotosEnfermedadesInsumosjob : Job() {
    var repository: IMainViewJob.Repository? = null
    init {
        repository=JobSyncRepository()
    }

    companion object {
        val TAG = "job_fotos_get_sync"
        fun scheduleFotosJob() {
            val jobRequests = JobManager.instance().getAllJobRequestsForTag(FotosEnfermedadesInsumosjob.TAG)
            if (!jobRequests.isEmpty()) {
                return
            }
            JobRequest.Builder(FotosEnfermedadesInsumosjob.TAG)
                    //.setPeriodic(TimeUnit.MINUTES.toMillis(15), TimeUnit.MINUTES.toMillis(5))
                     //.setUpdateCurrent(true) // calls cancelAllForTag(NoteSyncJob.TAG) for you
                    //.setRequiredNetworkType(JobRequest.NetworkType.CONNECTED)
                    //.setRequirementsEnforced(true)
                    .startNow()
                    .build()
                    .schedule()
        }
        fun cancel() {
            JobManager.instance().cancelAllForTag(TAG)
        }
    }

    override fun onRunJob(params: Job.Params): Job.Result {
        repository?.syncFotos(context)
        return Job.Result.SUCCESS
    }
}