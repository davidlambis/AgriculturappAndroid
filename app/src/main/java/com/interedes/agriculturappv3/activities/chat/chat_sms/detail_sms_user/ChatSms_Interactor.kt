package com.interedes.agriculturappv3.activities.chat.chat_sms.detail_sms_user

import android.app.Activity
import com.interedes.agriculturappv3.modules.models.sms.Sms

class ChatSms_Interactor : IMainViewDetailSms.Interactor {
    var repository: IMainViewDetailSms.Repository? = null

    init {
        repository = ChatSms_Repository()
    }

    override fun getListSms(context: Activity, smsUser: Sms?,eventNotification:Boolean) {
        repository?.getListSms(context,smsUser,eventNotification)
    }

}