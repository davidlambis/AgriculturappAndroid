package com.interedes.agriculturappv3.services.jobs

import com.evernote.android.job.Job
import com.evernote.android.job.JobRequest
import javax.xml.datatype.DatatypeConstants.MINUTES
import com.evernote.android.job.JobManager
import android.provider.ContactsContract.CommonDataKinds.Note
import com.interedes.agriculturappv3.AgriculturApplication
import com.interedes.agriculturappv3.modules.models.unidad_productiva.Unidad_Productiva
import com.interedes.agriculturappv3.modules.models.unidad_productiva.Unidad_Productiva_Table
import com.interedes.agriculturappv3.services.notifications.repository.IMainFirebaseInstance
import com.raizlabs.android.dbflow.kotlinextensions.save
import com.raizlabs.android.dbflow.sql.language.SQLite
import java.util.concurrent.TimeUnit


class DataSyncJob: Job() {
    var repository: IMainViewJob.Repository? = null
    init {
        repository=JobSyncRepository()
    }

    companion object {
        val TAG = "job_data_post_sync"


        fun scheduleJob() {
            val jobRequests = JobManager.instance().getAllJobRequestsForTag(DataSyncJob.TAG)
            if (!jobRequests.isEmpty()) {
                return
            }
            JobRequest.Builder(DataSyncJob.TAG)
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
        val quantitySync= repository?.syncQuantityData()
        if(quantitySync?.CantidadRegistrosSync!!.toInt()>0 || quantitySync?.CantidadUpdatesSync!!.toInt()>0 ){
            AgriculturApplication.instance.showNotification(true)
        }

        return Job.Result.SUCCESS
    }

}