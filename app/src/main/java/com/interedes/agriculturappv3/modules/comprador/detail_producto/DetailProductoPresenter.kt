package com.interedes.agriculturappv3.modules.comprador.detail_producto

import android.app.Activity
import android.app.PendingIntent
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.telephony.SmsManager
import com.interedes.agriculturappv3.R
import com.interedes.agriculturappv3.libs.EventBus
import com.interedes.agriculturappv3.libs.GreenRobotEventBus
import com.interedes.agriculturappv3.modules.comprador.detail_producto.events.RequestEventDetalleProducto
import com.interedes.agriculturappv3.modules.models.ofertas.DetalleOferta
import com.interedes.agriculturappv3.modules.models.ofertas.DetalleOferta_Table
import com.interedes.agriculturappv3.modules.models.ofertas.Oferta
import com.interedes.agriculturappv3.modules.models.producto.Producto
import com.interedes.agriculturappv3.modules.models.producto.Producto_Table
import com.interedes.agriculturappv3.modules.models.tipoproducto.TipoProducto
import com.interedes.agriculturappv3.modules.models.unidad_medida.Unidad_Medida
import com.interedes.agriculturappv3.modules.models.usuario.Usuario
import com.interedes.agriculturappv3.services.resources.Const_Resources
import com.interedes.agriculturappv3.services.internet_connection.ConnectivityReceiver
import com.raizlabs.android.dbflow.sql.language.SQLite
import org.greenrobot.eventbus.Subscribe
import java.util.ArrayList
import java.util.regex.Pattern


class DetailProductoPresenter(var mainView: IMainViewDetailProducto.MainView?):IMainViewDetailProducto.Presenter {



    var interactor: IMainViewDetailProducto.Interactor? = null
    var eventBus: EventBus? = null
    var listUnidadMedidaGlobalPrecios: List<Unidad_Medida>? = ArrayList<Unidad_Medida>()

    //private var sentStatusReceiver: BroadcastReceiver? = null
    //private var deliveredStatusReceiver: BroadcastReceiver? = null


    private var sendMessageSuccess=false


    //GLOBALS
    var listTipoProducto:List<Producto>?= ArrayList<Producto>()

    companion object {
        var instance: DetailProductoPresenter? = null
    }

    init {
        instance = this
        interactor = DetailProductoInteractor()
        eventBus = GreenRobotEventBus()
    }

    override fun onCreate() {
        eventBus?.register(this)

    }

    override fun onDestroy() {
        mainView = null
        eventBus?.unregister(this)
    }


    //region Service SMS

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


            /*
            if(!s.contains("Successfully")){
                // onMessageToas("Mensage enviado correctamente",R.color.green)
                // messageEditText.setText("")
                onMessageOk()
                mainView?.sucessResponseOferta()
                mainView?.onMessageToas("Mensage enviado correctamente", R.color.green)
            }else{
                onMessageOk()
                mainView?.sucessResponseOferta()
                mainView?.onMessageToas("Mensage no enviado",R.color.red_900)
            }*/


           // mainView?.hideProgressHud()
            //messageEditText.setText("")
            //message_status_text_view.setText(s)

        }
    }


    override fun validatePhoneNumber(phone:String?):Boolean{
        var check = false
        if (!Pattern.matches("[a-zA-Z]+", phone)) {
            if (phone?.length!! < 6 || phone.length > 13) {
                // if(phone.length() != 10) {
                check = false
               // txtPhone.setError("Not Valid Number")
            } else {
                check = true
            }
        } else {
            check = false
        }
        return check
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
                // onMessageToas("Mensage enviado correctamente",R.color.green)
                // messageEditText.setText("")
                if(!sendMessageSuccess){
                    onMessageOk()
                    mainView?.sucessResponseOferta()
                    mainView?.onMessageToas("Mensage enviado correctamente", R.color.green)
                    sendMessageSuccess=true
                }
            }else{
                onMessageOk()
                mainView?.sucessResponseOferta()
                mainView?.onMessageToas("Mensage no enviado",R.color.red_900)
            }

            mainView?.hideProgressHud()
            //hideProgressHud()
        }
    }


    //endregion

    //region Conectividad
    private val mNotificationReceiver = object : BroadcastReceiver() {
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
        context.registerReceiver(mNotificationReceiver, IntentFilter(Const_Resources.SERVICE_CONECTIVITY))
        //SMS
        context.registerReceiver(sentStatusReceiver, IntentFilter(Const_Resources.SERVICE_SMS_SENT))
        context.registerReceiver(deliveredStatusReceiver, IntentFilter(Const_Resources.SERVICE_SMS_DELIVERED))
    }

    override fun onPause(context: Context) {
        context.unregisterReceiver(this.sentStatusReceiver)
        context.unregisterReceiver(this.deliveredStatusReceiver)
        context.unregisterReceiver(this.mNotificationReceiver);
    }

    //endregion





    //region Suscribe Events
    @Subscribe
    override fun onEventMainThread(event: RequestEventDetalleProducto?) {
        when (event?.eventType) {

            RequestEventDetalleProducto.ERROR_EVENT -> {
                onMessageError(event.mensajeError)
            }

            RequestEventDetalleProducto.PRODUCTO_EVENT -> {
                var producto = event.objectMutable as Producto
            }
            RequestEventDetalleProducto.LIST_EVENT_UNIDAD_MEDIDA_PRICE -> {
                listUnidadMedidaGlobalPrecios = event.mutableList as List<Unidad_Medida>
            }

            RequestEventDetalleProducto.OK_SEND_EVENT_OFERTA -> {
                onMessageOk()
                mainView?.sucessResponseOferta()
            }

            RequestEventDetalleProducto.OK_SEND_EVENT_SMS -> {
                onMessageSendSmsOk()
                val oferta = event.objectMutable as Oferta
                mainView?.showConfirmSendSmsOferta(oferta)
            }

        //Error Conection
            RequestEventDetalleProducto.ERROR_VERIFICATE_CONECTION -> {
                onMessageConectionError()
            }
        }
    }
    //endregion

    //region VALIDACIONES
    //region Methods
    override fun validarCamposAddOferta(): Boolean {
        if (mainView?.validarAddOferta() == true) {
            return true
        }
        return false
    }



    //endregion

    //region METHODS
    override fun getProducto(producto_id: Long): Producto? {
       return interactor?.getProducto(producto_id)
    }

    override fun getTipoProducto(tipo_producto_id: Long): TipoProducto? {
        return  interactor?.getTipoProducto(tipo_producto_id)
    }
    override fun verificateCantProducto(producto_id: Long?, catnidad: Double?): Boolean? {
       return interactor?.verificateCantProducto(producto_id,catnidad)
    }


    override fun getListas() {
        interactor?.getListas()
    }

    override fun postOferta(oferta: Oferta){
        mainView?.showProgressHud()
        interactor?.postOferta(oferta,checkConnection())
    }



    override fun getLastUserLogued(): Usuario?{
        return  interactor?.getLastUserLogued()
    }

    override fun sendSmsOferta(user: Usuario,oferta:Oferta, activity:Activity) {
        val phone =user.PhoneNumber?.trim()
        //val smsMessage = "Oferta por sms"
        try {
            var disponibilidad = ""
            var precioOferta = ""
            var productoCantidad=""
            var calidad=""

            val producto =SQLite.select().from(Producto::class.java).where(Producto_Table.Id_Remote.eq(oferta.ProductoId)).querySingle()

            val detalleOferta=SQLite.select().from(DetalleOferta::class.java).where(DetalleOferta_Table.OfertasId.eq(oferta.Oferta_Id)).querySingle()

            if (detalleOferta?.Cantidad.toString().contains(".0")) {
                disponibilidad = String.format(activity?.getString(R.string.price_empty_signe)!!,
                        detalleOferta?.Cantidad)
            } else {
                disponibilidad = detalleOferta?.Cantidad.toString()
            }

            productoCantidad= String.format("%s %s ", disponibilidad, producto?.NombreUnidadMedidaCantidad)
            calidad= String.format("%s",producto?.NombreCalidad)

            precioOferta=String.format(activity.getString(R.string.price_producto),
                    detalleOferta?.Valor_Oferta, detalleOferta?.NombreUnidadMedidaPrecio)


            val idSms=activity.getString(R.string.idenfication_send_sms_oferta_app)

            val message="$idSms "+String.format(activity.getString(R.string.descripcion_oferta_productor)
                    ,user?.Nombre,user.Apellidos,productoCantidad,precioOferta,producto?.Nombre,calidad)

            val sms = SmsManager.getDefault()
            // if message length is too long messages are divided


            //val sendIntent=PendingIntent.getBroadcast(activity, 0, Intent(Const_Resources.SERVICE_SMS_SENT), 0)
            //val deliveredIntent=PendingIntent.getBroadcast(activity, 0, Intent(Const_Resources.SERVICE_SMS_DELIVERED), 0)
            //sms.sendTextMessage(phone, null, message, sendIntent, deliveredIntent)



            val messages = sms.divideMessage(message)
            val sentIntents = ArrayList<PendingIntent>()
            val deliveryIntents = ArrayList<PendingIntent>()

            for (msg in messages) {
                sentIntents.add(PendingIntent.getBroadcast(activity, 0, Intent(Const_Resources.SERVICE_SMS_SENT), 0))
                deliveryIntents.add(PendingIntent.getBroadcast(activity, 0, Intent(Const_Resources.SERVICE_SMS_DELIVERED), 0))
                //sms.sendTextMessage(phone, null, msg, sentIntent, deliveredIntent)
            }
            sms.sendMultipartTextMessage(phone, null, messages, sentIntents, deliveryIntents);


            mainView?.showProgressHud()
            // refreshSmsInbox()
        } catch (e: Exception) {
            e.printStackTrace()
            mainView?.hideProgressHud()
            mainView?.sucessResponseOferta()
        }
    }



    //endregion

    //region set DATA VIEW
    override fun setListSpinnerMoneda() {
        mainView?.setListMoneda(listUnidadMedidaGlobalPrecios)
    }



    //endregion

    //region Messages/Notificaciones
    private fun onMessageSendSmsOk() {
        mainView?.hideProgress()
        mainView?.hideProgressHud()
        //mainView?.requestResponseOK()
    }

    private fun onMessageOk() {
        mainView?.hideProgress()
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
        mainView?.verificateConnection()
    }



    //endregion


}