package com.interedes.agriculturappv3.activities.chat.chat_sms

import android.app.Activity
import android.content.Context

class ChatSms_Interactor:IMainViewChatSms.Interactor {
    var repository: IMainViewChatSms.Repository? = null

    init {
        repository = ChatSms_Repository()
    }

    override fun getListSms(context: Activity) {

    }

}