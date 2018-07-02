package com.interedes.agriculturappv3.activities.chat.chat_sms.user_sms_ui

import android.app.Activity
import com.interedes.agriculturappv3.modules.models.sms.Sms

class UsersSms_Interactor: IMainViewUserSms.Interactor {
    var repository: IMainViewUserSms.Repository? = null

    init {
        repository = UserSms_Repository()
    }

    override fun getListSms(context: Activity,smsUser: Sms?) {
        repository?.getListSms(context,smsUser)
    }

}