package com.interedes.agriculturappv3.services.jobs

import com.evernote.android.job.Job
import com.evernote.android.job.JobManager
import com.evernote.android.job.JobRequest
import com.interedes.agriculturappv3.services.jobs.repository.IMainViewJob
import com.interedes.agriculturappv3.services.jobs.repository.JobSyncRepository

class SyncFotoProductosJob : Job() {
    var repository: IMainViewJob.Repository? = null


    init {
        repository= JobSyncRepository()
    }

    companion object {
        val TAG = "job_fotos_productos_get_sync"
        fun scheduleFotosProductosJob() {
            val jobRequests = JobManager.instance().getAllJobRequestsForTag(SyncFotoProductosJob.TAG)
            if (!jobRequests.isEmpty()) {
                return
            }

            JobRequest.Builder(SyncFotoProductosJob.TAG)
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
        repository?.syncFotoProductos(context)
        return Job.Result.SUCCESS
    }
}