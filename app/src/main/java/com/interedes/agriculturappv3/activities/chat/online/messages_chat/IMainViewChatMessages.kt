package com.interedes.agriculturappv3.activities.chat.online.messages_chat

import android.content.Context
import android.content.Intent
import android.os.Bundle
import com.interedes.agriculturappv3.activities.chat.online.messages_chat.events.RequestEventChatMessage
import com.interedes.agriculturappv3.modules.models.chat.ChatMessage
import com.interedes.agriculturappv3.modules.models.chat.Room

interface IMainViewChatMessages {



    interface MainView {
        //Progress and progress Hud
        fun showProgress()
        fun hideProgress()
        fun showProgressHud()
        fun hideProgressHud()

        fun limpiarCampos()


        //list
        fun setListMessagesByRoom(sms: List<ChatMessage>)

        fun setNewMessageByRoom(sms: ChatMessage)



        //Response Notify
        fun onMessageToas(message:String,color:Int)
        fun requestResponseOK()
        fun requestResponseError(error: String?)
        fun onMessageOk(colorPrimary: Int, msg: String?)
        fun onMessageError(colorPrimary: Int, msg: String?)

        fun checkConectionInternet()


        //Events
        fun onEventBroadcastReceiver(extras: Bundle, intent: Intent)

    }

    interface Presenter {
        fun onCreate()
        fun onDestroy()
        fun onResume(context: Context)
        fun onPause(context: Context)

        //Events
        fun onEventMainThread(requestEvent: RequestEventChatMessage?)

        //Methods
        fun getListMessagesByRoom(room:Room,mReceiverId:String)
        fun sendMessage(message:ChatMessage,room:Room)

        //Conecttion
        fun checkConnection(): Boolean
    }

    interface Interactor {
        fun getListMessagesByRoom(checkConection:Boolean,room:Room,mReceiverId:String)
        fun sendMessage(message:ChatMessage,room:Room,checkConection: Boolean)

    }

    interface Repository {
        fun getListMessagesByRoom(checkConection:Boolean,room:Room,mReceiverId:String)
        fun sendMessage(message:ChatMessage,room:Room,checkConection: Boolean)

    }
}