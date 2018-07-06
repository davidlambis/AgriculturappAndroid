package com.interedes.agriculturappv3.services.chat

import com.evernote.android.job.Job
import com.evernote.android.job.JobManager
import com.evernote.android.job.JobRequest
import com.interedes.agriculturappv3.AgriculturApplication
import java.util.concurrent.TimeUnit

class ChatRunJob : Job() {


    var repository: IMainViewJobChat.Repository? = null
    init {
        repository=ChatJobRepository()
    }

    companion object {

        val TAG = "job_chat"
        fun scheduleJobChat() {
            val jobRequests = JobManager.instance().getAllJobRequestsForTag(ChatRunJob.TAG)
            if (!jobRequests.isEmpty()) {
                return
            }
            JobRequest.Builder(ChatRunJob.TAG)
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