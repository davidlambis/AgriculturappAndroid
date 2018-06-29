package com.interedes.agriculturappv3.activities.chat.chat_sms

import android.app.Activity
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import com.interedes.agriculturappv3.activities.chat.chat_sms.events.RequestEventChatSms
import com.interedes.agriculturappv3.libs.EventBus
import com.interedes.agriculturappv3.libs.GreenRobotEventBus
import com.interedes.agriculturappv3.services.Const
import com.interedes.agriculturappv3.services.internet_connection.ConnectivityReceiver
import org.greenrobot.eventbus.Subscribe
import java.util.ArrayList

class ChatSms_Presenter(var mainView:IMainViewChatSms.MainView?):IMainViewChatSms.Presenter {

    var interactor: IMainViewChatSms.Interactor? = null
    var eventBus: EventBus? = null



    companion object {
        var instance: ChatSms_Presenter? = null
    }

    init {
        instance = this
        interactor = ChatSms_Interactor()
        eventBus = GreenRobotEventBus()
    }

    override fun onCreate() {
        eventBus?.register(this)

    }

    override fun onDestroy() {
        mainView = null
        eventBus?.unregister(this)
    }

    //SMS

    private val mNotificationReceiverSms = object : BroadcastReceiver() {
        override fun onReceive(context: Context, intent: Intent) {
            var extras = intent.extras
            if (extras != null) {
                // if (extras.containsKey("new_message")) {
                mainView?.onEventBroadcastReceiver(extras,intent)
                // }
            }
        }
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
        context.registerReceiver(mNotificationReceiverApp, IntentFilter(Const.SERVICE_CONECTIVITY))
        context.registerReceiver(mNotificationReceiverSms, IntentFilter(Const.SERVICE_RECYVE_MESSAGE))

    }

    override fun onPause(context: Context) {
        context.unregisterReceiver(this.mNotificationReceiverApp);
        context.unregisterReceiver(this.mNotificationReceiverSms);
    }

    //endregion

    //region Suscribe Events
    @Subscribe
    override fun onEventMainThread(event: RequestEventChatSms?) {
        when (event?.eventType) {

            RequestEventChatSms.UPDATE_EVENT -> {
                onMessageOk()
            }

            RequestEventChatSms.ERROR_EVENT -> {
                onMessageError(event.mensajeError)
            }

            RequestEventChatSms.LIST_SMS_EVENT -> {
                //listDetalleMetodo = event.mutableList as List<DetalleMetodoPago>
            }
        }
    }
    //endregion

    //region Validations
    override fun validarSendSms(): Boolean {
        if (mainView?.validarSendSms() == true) {
            return true
        }
        return false
    }
    //endregion

    //region METHODS

    override fun getListSms(context: Activity) {
        interactor?.getListSms(context)
    }

    //region Messages/Notificaciones
    private fun onMessageOk() {
        mainView?.hideProgressHud()
        mainView?.requestResponseOK()
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