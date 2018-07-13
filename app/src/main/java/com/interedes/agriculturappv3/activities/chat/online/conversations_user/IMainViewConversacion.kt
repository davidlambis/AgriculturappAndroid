package com.interedes.agriculturappv3.activities.chat.online.conversations_user

import android.content.Context
import android.content.Intent
import android.os.Bundle
import com.interedes.agriculturappv3.activities.chat.online.conversations_user.events.RequestEventChatOnline
import com.interedes.agriculturappv3.modules.models.chat.RoomConversation

interface IMainViewConversacion {


    interface MainView {
        //Progress and progress Hud
        fun showProgress()
        fun hideProgress()
        fun showProgressHud()
        fun hideProgressHud()

        //results
        fun setResults(resuts: Int)

        //list
        fun setListRoom(sms: List<RoomConversation>)

        //Response Notify
        fun onMessageToas(message:String,color:Int)

        //Events
        fun onEventBroadcastReceiver(extras: Bundle, intent: Intent)

    }

    interface Presenter {
        fun onCreate()
        fun onDestroy()
        fun onResume(context: Context)
        fun onPause(context: Context)

        //Events
        fun onEventMainThread(requestEvent: RequestEventChatOnline?)

        //Methods
        fun getListRoom()

        //Conecttion
        fun checkConnection(): Boolean
    }

    interface Interactor {
        fun getListRoom(checkConection:Boolean)

    }

    interface Repository {

        fun getListRoom(checkConection:Boolean)
    }
}