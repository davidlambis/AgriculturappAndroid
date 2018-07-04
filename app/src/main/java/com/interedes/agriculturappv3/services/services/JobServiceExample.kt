package com.interedes.agriculturappv3.services.services

import android.app.job.JobParameters
import android.app.job.JobService
import android.os.Build
import android.os.Handler
import android.os.Message
import android.support.annotation.RequiresApi
import android.widget.Toast



@RequiresApi(Build.VERSION_CODES.LOLLIPOP)
class JobServiceExample: JobService() {


    private val mJobHandler = Handler(object : Handler.Callback {
        override fun handleMessage(msg: Message): Boolean {
            Toast.makeText(applicationContext, "JobService task running", Toast.LENGTH_SHORT).show()
            jobFinished(msg.obj as JobParameters, false)
            return true
        }
    })

    override fun onStartJob(params: JobParameters?): Boolean {
        mJobHandler.sendMessage( Message.obtain( mJobHandler, 1, params ) );
        return true;
    }



    override fun onStopJob(params: JobParameters?): Boolean {
        mJobHandler.removeMessages( 1 );
        return false;
    }

}