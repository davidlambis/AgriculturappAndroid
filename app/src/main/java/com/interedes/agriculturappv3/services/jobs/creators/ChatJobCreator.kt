package com.interedes.agriculturappv3.services.jobs.creators

import com.evernote.android.job.Job
import com.evernote.android.job.JobCreator
import com.interedes.agriculturappv3.services.jobs.ChatRunJob

class ChatJobCreator: JobCreator {
    override fun create(tag: String): Job? {
        when (tag) {
            ChatRunJob.TAG -> return ChatRunJob()
            else -> return null
        }
    }
}