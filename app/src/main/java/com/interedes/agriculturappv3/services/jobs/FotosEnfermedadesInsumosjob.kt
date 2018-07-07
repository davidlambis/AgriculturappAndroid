package com.interedes.agriculturappv3.services.jobs

import com.evernote.android.job.Job
import com.evernote.android.job.JobManager
import com.evernote.android.job.JobRequest
import com.interedes.agriculturappv3.services.api.ApiInterface
import com.interedes.agriculturappv3.services.jobs.repository.IMainViewJob
import com.interedes.agriculturappv3.services.jobs.repository.JobSyncRepository

class FotosEnfermedadesInsumosjob : Job() {
    var repository: IMainViewJob.Repository? = null


    init {
        repository= JobSyncRepository()
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
        repository?.getListSyncEnfermedadesAndTratamiento(context)
        return Job.Result.SUCCESS
    }
}