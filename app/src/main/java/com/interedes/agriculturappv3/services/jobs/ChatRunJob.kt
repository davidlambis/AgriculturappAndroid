package com.interedes.agriculturappv3.services.jobs

import com.evernote.android.job.Job
import com.evernote.android.job.JobManager
import com.evernote.android.job.JobRequest
import com.interedes.agriculturappv3.services.jobs.repository.IMainViewJob
import com.interedes.agriculturappv3.services.jobs.repository.JobSyncRepository
import java.util.concurrent.TimeUnit

class ChatRunJob : Job() {


    var repository: IMainViewJob.Repository? = null
    init {
        repository= JobSyncRepository()
    }

    companion object {

        val TAG = "job_chat"
        fun scheduleJobChat() {
            val jobRequests = JobManager.instance().getAllJobRequestsForTag(TAG)
            if (!jobRequests.isEmpty()) {
                return
            }
            JobRequest.Builder(TAG)
                    .setPeriodic(TimeUnit.MINUTES.toMillis(15), TimeUnit.MINUTES.toMillis(5))
                    .setUpdateCurrent(true) // calls cancelAllForTag(NoteSyncJob.TAG) for you
                    .setRequiredNetworkType(JobRequest.NetworkType.CONNECTED)
                    .setRequirementsEnforced(true)
                    .build()
                    .schedule()
        }

        fun cancel() {
            JobManager.instance().cancelAllForTag(TAG)
        }
    }

    override fun onRunJob(params: Job.Params): Job.Result {
        repository?.updateUserStatus()
        return Job.Result.SUCCESS
    }
}