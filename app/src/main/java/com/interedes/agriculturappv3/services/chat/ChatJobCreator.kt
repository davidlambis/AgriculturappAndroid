package com.interedes.agriculturappv3.services.chat

import com.evernote.android.job.Job
import com.evernote.android.job.JobCreator

class ChatJobCreator: JobCreator {
    override fun create(tag: String): Job? {
        when (tag) {
            ChatRunJob.TAG -> return ChatRunJob()
            else -> return null
        }
    }
}