package com.interedes.agriculturappv3.activities.chat.online.messages_chat

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import com.interedes.agriculturappv3.activities.chat.online.messages_chat.events.RequestEventChatMessage
import com.interedes.agriculturappv3.libs.EventBus
import com.interedes.agriculturappv3.libs.GreenRobotEventBus
import com.interedes.agriculturappv3.modules.models.chat.ChatMessage
import com.interedes.agriculturappv3.modules.models.chat.Room
import com.interedes.agriculturappv3.modules.models.chat.UserFirebase
import com.interedes.agriculturappv3.services.resources.Const_Resources
import com.interedes.agriculturappv3.services.internet_connection.ConnectivityReceiver
import org.greenrobot.eventbus.Subscribe

class ChatMessage_Presenter  (var mainView: IMainViewChatMessages.MainView?): IMainViewChatMessages.Presenter {


    var interactor: IMainViewChatMessages.Interactor? = null
    var eventBus: EventBus? = null

    companion object {
        var instance: ChatMessage_Presenter? = null
    }

    init {
        instance = this
        interactor = ChatMessage_Interactor()
        eventBus = GreenRobotEventBus()
    }

    override fun onCreate() {
        eventBus?.register(this)

    }

    override fun onDestroy() {
        mainView = null
        eventBus?.unregister(this)
    }

    //region Conectividad
    private val mNotificationReceiverApp = object : BroadcastReceiver() {
        override fun onReceive(context: Context, intent: Intent) {
            val extras = intent.extras
            if (extras != null) {
                mainView?.onEventBroadcastReceiver(extras, intent)
            }
        }
    }

    override fun checkConnection(): Boolean {
        return ConnectivityReceiver.isConnected
    }

    override fun onResume(context: Context) {
        context.registerReceiver(mNotificationReceiverApp, IntentFilter(Const_Resources.SERVICE_CONECTIVITY))
    }

    override fun onPause(context: Context) {
        context.unregisterReceiver(this.mNotificationReceiverApp);
    }

    //endregion

    //region Suscribe Events
    @Subscribe
    override fun onEventMainThread(event: RequestEventChatMessage?) {
        when (event?.eventType) {

            RequestEventChatMessage.UPDATE_EVENT -> {
                //onMessageOk()
            }

            RequestEventChatMessage.SEND_MESSAGE_EVENT_OK -> {
                onMessageSendOk()
            }

            RequestEventChatMessage.NEW_MESSAGES_EVENT -> {
                val newMessage = event.objectMutable as ChatMessage
                mainView?.setNewMessageByRoom(newMessage)
            }

            RequestEventChatMessage.ERROR_EVENT -> {
                onMessageError(event.mensajeError)
            }

            RequestEventChatMessage.LIST_MESSAGES_EVENT -> {
                val list = event.mutableList as List<ChatMessage>
                mainView?.setListMessagesByRoom(list)
            }

            RequestEventChatMessage.ERROR_VERIFICATE_CONECTION -> {
                mainView?.checkConectionInternet()
                val newMessage = event.objectMutable as ChatMessage
                mainView?.sendSmsVerificate(newMessage)

            }
        }
    }
    //endregion

    //region METHODS

    override fun getListMessagesByRoom(room: Room, mReceiverId: String) {
        interactor?.getListMessagesByRoom(checkConnection(),room, mReceiverId)
    }

    override fun sendMessage(message: ChatMessage, room: Room,userSelected: UserFirebase) {
        interactor?.sendMessage(message,room,userSelected,checkConnection())
    }

    //endregion


    //region Messages/Notificaciones
    private fun onMessageSendOk() {
        mainView?.hideProgressHud()
        //mainView?.requestResponseOK()
        mainView?.limpiarCampos()

    }

    private fun onMessageError(error: String?) {
        mainView?.hideProgress()
        mainView?.hideProgressHud()
        mainView?.requestResponseError(error)
    }

    private fun onMessageConectionError() {
        mainView?.hideProgress()
        mainView?.hideProgressHud()
    }


//endregion
}

