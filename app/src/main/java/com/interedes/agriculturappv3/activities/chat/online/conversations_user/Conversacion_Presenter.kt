package com.interedes.agriculturappv3.activities.chat.online.conversations_user

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import com.interedes.agriculturappv3.activities.chat.online.conversations_user.events.RequestEventChatOnline
import com.interedes.agriculturappv3.libs.EventBus
import com.interedes.agriculturappv3.libs.GreenRobotEventBus
import com.interedes.agriculturappv3.modules.models.chat.RoomConversation
import com.interedes.agriculturappv3.services.resources.Const_Resources
import com.interedes.agriculturappv3.services.internet_connection.ConnectivityReceiver
import org.greenrobot.eventbus.Subscribe

class Conversacion_Presenter (var mainView: IMainViewConversacion.MainView?): IMainViewConversacion.Presenter {

    var interactor: IMainViewConversacion.Interactor? = null
    var eventBus: EventBus? = null

    companion object {
        var instance: Conversacion_Presenter? = null
    }

    init {
        instance = this
        interactor = Conversacion_Interactor()
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
            var extras = intent.extras
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
    override fun onEventMainThread(event: RequestEventChatOnline?) {
        when (event?.eventType) {

            RequestEventChatOnline.UPDATE_EVENT -> {
                onMessageOk()
            }

            RequestEventChatOnline.ERROR_EVENT -> {
                onMessageError(event.mensajeError)
            }

            RequestEventChatOnline.LIST_ROOM_EVENT -> {
                val list = event.mutableList as List<RoomConversation>
                mainView?.setListRoom(list)
            }

        }
    }
    //endregion


    //region METHODS
    override fun getListRoom() {
       interactor?.getListRoom(checkConnection())
    }

    //endregion

    //region Messages/Notificaciones
    private fun onMessageOk() {
        mainView?.hideProgressHud()
    }

    private fun onMessageError(error: String?) {
        mainView?.hideProgress()
        mainView?.hideProgressHud()
    }

    private fun onMessageConectionError() {
        mainView?.hideProgress()
        mainView?.hideProgressHud()
    }


    //endregion


}