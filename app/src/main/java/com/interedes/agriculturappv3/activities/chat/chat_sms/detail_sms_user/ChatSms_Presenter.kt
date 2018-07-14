package com.interedes.agriculturappv3.activities.chat.chat_sms.detail_sms_user

import android.app.Activity
import android.app.PendingIntent
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.telephony.SmsManager
import com.interedes.agriculturappv3.R
import com.interedes.agriculturappv3.activities.chat.chat_sms.detail_sms_user.events.RequestEventSmsDetail
import com.interedes.agriculturappv3.libs.EventBus
import com.interedes.agriculturappv3.libs.GreenRobotEventBus
import com.interedes.agriculturappv3.modules.models.sms.Sms
import com.interedes.agriculturappv3.modules.models.sms.Sms_Table
import com.interedes.agriculturappv3.services.resources.Const_Resources
import com.interedes.agriculturappv3.services.internet_connection.ConnectivityReceiver
import com.raizlabs.android.dbflow.kotlinextensions.save
import com.raizlabs.android.dbflow.sql.language.SQLite
import org.greenrobot.eventbus.Subscribe

class ChatSms_Presenter(var mainView: IMainViewDetailSms.MainView?): IMainViewDetailSms.Presenter {

    var interactor: IMainViewDetailSms.Interactor? = null
    var eventBus: EventBus? = null
    var smsGlobal:Sms?=null

    private var sentStatusReceiver: BroadcastReceiver? = null
    private var deliveredStatusReceiver: BroadcastReceiver? = null


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

    /*
    private val sentStatusReceiver = object : BroadcastReceiver() {
        override fun onReceive(arg0: Context, arg1: Intent) {
            var s = "Unknown Error"
            when (resultCode) {
                Activity.RESULT_OK -> s = "Message Sent Successfully !!"
                SmsManager.RESULT_ERROR_GENERIC_FAILURE -> s = "Generic Failure Error"
                SmsManager.RESULT_ERROR_NO_SERVICE -> s = "Error : No Service Available"
                SmsManager.RESULT_ERROR_NULL_PDU -> s = "Error : Null PDU"
                SmsManager.RESULT_ERROR_RADIO_OFF -> s = "Error : Radio is off"
                else -> {
                }
            }

            if(!s.equals("Message Sent Successfully !!")){
                mainView?.onMessageToas("Mensage no enviado", R.color.red_900)
                mainView?.hideProgressHud()
            }
            //hideProgressHud()
            //messageEditText.setText("")
            //message_status_text_view.setText(s)
        }
    }

    private val deliveredStatusReceiver = object : BroadcastReceiver() {
        override fun onReceive(arg0: Context, arg1: Intent) {
            var s = "Message Not Delivered"
            when (resultCode) {
                Activity.RESULT_OK -> s = "Message Delivered Successfully"
                Activity.RESULT_CANCELED -> {
                }
            }
            //delivery_status_text_view.setText(s)
            //phone_number_edit_text.setText("")
            //message_edit_text.setText("")

            if(s.contains("Successfully")){
                //smsGlobal!!.save()
                val lastSms = getLastSms()
                if (lastSms == null) {
                    smsGlobal?.Id = 1
                } else {
                    smsGlobal?.Id = lastSms.Id!! + 1
                }
                smsGlobal!!.save()

                mainView?.onMessageToas("Mensage enviado correctamente", R.color.green)
                mainView?.limpiarCampos()
                mainView?.hideProgressHud()
                mainView?.getListSms(false)
                smsGlobal=null
            }else{
                mainView?.onMessageToas("Mensage no enviado", R.color.red_900)
                mainView?.hideProgressHud()
            }
        }
    }

    */

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

        sentStatusReceiver = object : BroadcastReceiver() {
            override fun onReceive(arg0: Context, arg1: Intent) {
                var s = "Unknown Error"
                when (resultCode) {
                    Activity.RESULT_OK -> s = "Message Sent Successfully !!"
                    SmsManager.RESULT_ERROR_GENERIC_FAILURE -> s = "Generic Failure Error"
                    SmsManager.RESULT_ERROR_NO_SERVICE -> s = "Error : No Service Available"
                    SmsManager.RESULT_ERROR_NULL_PDU -> s = "Error : Null PDU"
                    SmsManager.RESULT_ERROR_RADIO_OFF -> s = "Error : Radio is off"
                    else -> {
                    }
                }
                if(!s.equals("Message Sent Successfully !!")){
                    mainView?.onMessageToas("Mensage no enviado", R.color.red_900)
                    mainView?.hideProgressHud()
                }
            }
        }

        deliveredStatusReceiver = object : BroadcastReceiver() {
            override fun onReceive(arg0: Context, arg1: Intent) {
                var s = "Message Not Delivered"
                when (resultCode) {
                    Activity.RESULT_OK -> s = "Message Delivered Successfully"
                    Activity.RESULT_CANCELED -> {
                    }
                }
                if(s.contains("Successfully")){
                    //smsGlobal!!.save()
                    val lastSms = getLastSms()
                    if (lastSms == null) {
                        smsGlobal?.Id = 1
                    } else {
                        smsGlobal?.Id = lastSms.Id!! + 1
                    }
                    smsGlobal!!.save()

                    mainView?.onMessageToas("Mensage enviado correctamente", R.color.green)
                    mainView?.limpiarCampos()
                    mainView?.hideProgressHud()
                    mainView?.getListSms(false)
                    smsGlobal=null
                }else{
                    mainView?.onMessageToas("Mensage no enviado", R.color.red_900)
                    mainView?.hideProgressHud()
                }
            }
        }


        //On Activity Chat_Sms_Activity
        context.registerReceiver(sentStatusReceiver, IntentFilter(Const_Resources.SERVICE_SMS_SENT))
        context.registerReceiver(deliveredStatusReceiver, IntentFilter(Const_Resources.SERVICE_SMS_DELIVERED))

        //On Activity UsersSmsActivity
        context.registerReceiver(mNotificationReceiverApp, IntentFilter(Const_Resources.SERVICE_CONECTIVITY))
    }

    override fun onPause(context: Context) {
        //On Activity Chat_Sms_Activity
        context.unregisterReceiver(this.sentStatusReceiver);
        context.unregisterReceiver(this.deliveredStatusReceiver);
        //On Activity UsersSmsActivity
        context.unregisterReceiver(this.mNotificationReceiverApp);
    }

    //endregion

    //region Suscribe Events
    @Subscribe
    override fun onEventMainThread(event: RequestEventSmsDetail?) {
        when (event?.eventType) {

            RequestEventSmsDetail.UPDATE_EVENT -> {
                onMessageOk()
            }

            RequestEventSmsDetail.ERROR_EVENT -> {
                onMessageError(event.mensajeError)
            }

            RequestEventSmsDetail.LIST_SMS_EVENT -> {
                mainView?.hideProgressHud()
                mainView?.hideProgress()
                val listSms = event.mutableList as List<Sms>
                mainView?.setListSmsDetaiil(listSms)
            }

            RequestEventSmsDetail.NEW_MESSAGE_EVENT -> {
                val new_sms=event.mensajeError
                mainView?.onMessageToas(new_sms!!, R.color.green_900)
                mainView?.getListSms(false)
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

    fun getLastSms(): Sms? {
        val lastCultivo = SQLite.select().from(Sms::class.java).where().orderBy(Sms_Table.Id, false).querySingle()
        return lastCultivo
    }

    override fun getListSms(context: Activity, smsUser: Sms?,eventNotification:Boolean) {
        mainView?.showProgressHud()
        interactor?.getListSms(context,smsUser,eventNotification)
    }

    override fun saveSms(context:Context,sms: Sms?){
        smsGlobal=sms
        mainView?.showProgressHud()
        val phone =sms?.Address
        val smsMessage = context.getString(R.string.idenfication_send_sms_app)+" "+sms?.Message
        try {
            val message = smsMessage
            //Check if the phoneNumber is empty
            val sms = SmsManager.getDefault()
            // if message length is too long messages are divided
            val messages = sms.divideMessage(message)
            for (msg in messages) {
                val sentIntent = PendingIntent.getBroadcast(context, 0, Intent(Const_Resources.SERVICE_SMS_SENT), 0)
                val deliveredIntent = PendingIntent.getBroadcast(context, 0, Intent(Const_Resources.SERVICE_SMS_DELIVERED), 0)
                sms.sendTextMessage(phone, null, msg, sentIntent, deliveredIntent)
            }

            // refreshSmsInbox()
        } catch (e: Exception) {
            //Toast.makeText(applicationContext,"Sending SMS failed.",Toast.LENGTH_LONG).show()
            e.printStackTrace()
            mainView?.hideProgressHud()
            //hideProgressHud()
        }
    }

    //endregion

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