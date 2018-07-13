package com.interedes.agriculturappv3.activities.chat.chat_sms.user_sms_ui

import android.app.Activity
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import com.interedes.agriculturappv3.R
import com.interedes.agriculturappv3.activities.chat.chat_sms.user_sms_ui.events.RequestEventUserSms
import com.interedes.agriculturappv3.libs.EventBus
import com.interedes.agriculturappv3.libs.GreenRobotEventBus
import com.interedes.agriculturappv3.modules.models.sms.Sms
import com.interedes.agriculturappv3.services.resources.Const_Resources
import com.interedes.agriculturappv3.services.internet_connection.ConnectivityReceiver
import org.greenrobot.eventbus.Subscribe

class UserSms_Presenter(var mainView: IMainViewUserSms.MainView?): IMainViewUserSms.Presenter {

    var interactor: IMainViewUserSms.Interactor? = null
    var eventBus: EventBus? = null



    companion object {
        var instance: UserSms_Presenter? = null
    }

    init {
        instance = this
        interactor = UsersSms_Interactor()
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
        //On Activity Chat_Sms_Activity
        //On Activity UsersSmsActivity
        context.registerReceiver(mNotificationReceiverApp, IntentFilter(Const_Resources.SERVICE_CONECTIVITY))
    }

    override fun onPause(context: Context) {
        //On Activity UsersSmsActivity
        context.unregisterReceiver(this.mNotificationReceiverApp);
    }

    //endregion

    //region Suscribe Events
    @Subscribe
    override fun onEventMainThread(event: RequestEventUserSms?) {
        when (event?.eventType) {

            RequestEventUserSms.UPDATE_EVENT -> {
                onMessageOk()
            }

            RequestEventUserSms.ERROR_EVENT -> {
                onMessageError(event.mensajeError)
            }

            RequestEventUserSms.LIST_SMS_EVENT -> {
                mainView?.hideProgressHud()
                mainView?.hideProgress()
                val listSms = event.mutableList as List<Sms>
                mainView?.setListSms(listSms)
            }

            RequestEventUserSms.ITEM_EVENTS_DETAIL_SMS -> {
                val sms = event.objectMutable as Sms
                mainView?.navigateDetailSms(sms)
            }


            RequestEventUserSms.ITEM_EVENTS_ADD_CONTAT -> {
                val sms = event.objectMutable as Sms
                mainView?.addContact(sms)
            }

            RequestEventUserSms.NEW_MESSAGE_EVENT -> {
                val new_sms=event.mensajeError
                mainView?.onMessageToas(new_sms!!, R.color.green_900)
                mainView?.getListSms()
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

    override fun getListSms(context: Activity,smsUser:Sms?) {
        mainView?.showProgressHud()
        interactor?.getListSms(context,smsUser)
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