package com.interedes.agriculturappv3.services.jobs

import com.evernote.android.job.Job
import com.evernote.android.job.JobManager
import com.evernote.android.job.JobRequest
import com.interedes.agriculturappv3.BuildConfig
import com.interedes.agriculturappv3.services.jobs.repository.IMainViewJob
import com.interedes.agriculturappv3.services.jobs.repository.JobSyncRepository
import java.util.concurrent.TimeUnit

class ControlPlagasJob : Job() {


    var repository: IMainViewJob.Repository? = null
    init {
        repository= JobSyncRepository()
    }

    companion object {

        val TAG = "job_control_plagas"
        fun scheduleJobChat() {
            val jobRequests = JobManager.instance().getAllJobRequestsForTag(TAG)
            if (!jobRequests.isEmpty()) {
                return
                // return jobRequests.iterator().next().getJobId();
            }

            val interval = TimeUnit.HOURS.toMillis(6) // every 6 hours
            val flex = TimeUnit.HOURS.toMillis(3) // wait 3 hours before job runs again

            JobRequest.Builder(TAG)
                    .setPeriodic(interval, flex)
                    //.setPeriodic(TimeUnit.MINUTES.toMillis(15), TimeUnit.MINUTES.toMillis(5))
                    .setUpdateCurrent(true) // calls cancelAllForTag(NoteSyncJob.TAG) for you
                    .setRequirementsEnforced(true)
                    .build()
                    .schedule()
        }

        fun cancel() {
            JobManager.instance().cancelAllForTag(TAG)
        }
    }

    override fun onRunJob(params: Job.Params): Job.Result {
        repository?.checkControlPlagas(context)
        return Job.Result.SUCCESS
    }
}