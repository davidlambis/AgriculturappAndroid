package com.interedes.agriculturappv3.modules.productor.asistencia_tecnica_module.Lote

import android.app.Activity
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.location.LocationManager
import com.interedes.agriculturappv3.R
import com.interedes.agriculturappv3.modules.models.lote.Lote
import com.interedes.agriculturappv3.modules.models.unidad_medida.Unidad_Medida
import com.interedes.agriculturappv3.modules.productor.asistencia_tecnica_module.Lote.events.RequestEventLote
import com.interedes.agriculturappv3.libs.EventBus
import com.interedes.agriculturappv3.libs.GreenRobotEventBus
import com.interedes.agriculturappv3.modules.models.unidad_productiva.Unidad_Productiva
import com.interedes.agriculturappv3.services.Const
import com.interedes.agriculturappv3.services.coords.CoordsServiceKotlin
import com.interedes.agriculturappv3.services.internet_connection.ConnectivityReceiver
import org.greenrobot.eventbus.Subscribe

/**
 * Created by EnuarMunoz on 7/03/18.
 */

class LotePresenterImpl(var loteMainView: MainViewLote.View?) : MainViewLote.Presenter {

    var coordsService: CoordsServiceKotlin? = null
    var loteInteractor: MainViewLote.Interactor? = null
    var eventBus: EventBus? = null
    var locationManager: LocationManager? = null

    companion object {
        var instance: LotePresenterImpl? = null
    }

    init {
        instance = this
        loteInteractor = LoteInteractorImpl()
        eventBus = GreenRobotEventBus()

    }

    override fun onCreate() {
        eventBus?.register(this)
        loadListas()
        //loteMainView?.showAlertDialogSelectUp()
    }

    override fun onDestroy() {
        loteMainView = null
       /* if (coordsService != null) {
            CoordsServiceKotlin.instance?.closeService()
            //coordsService!!.closeService()
        }*/
        eventBus?.unregister(this)
    }


    override fun closeServiceGps(activity: Activity) {
        var intent =  Intent(activity, CoordsServiceKotlin::class.java);
        activity!!.stopService(intent)
        // IUpView?.showProgressHudCoords()
        /*if (coordsService != null) {
           //CoordsService.instance?.closeService()
           CoordsServiceKotlin.instance?.closeService()
           //coordsService!!.closeService()
       }*/
    }





    //region GPS
    override fun startGps(activity: Activity) {
        locationManager = activity.getSystemService(Context.LOCATION_SERVICE) as LocationManager
        if (!isLocationEnabled()) {
            loteMainView?.showGpsDisabledDialog()
        }else{
            loteMainView?.showProgressHudCoords()
            startLocationGps(activity)
        }
        // IUpView?.showProgressHudCoords()
        /*coordsService = CoordsServiceKotlin(activity)
        if (CoordsServiceKotlin.instance!!.isLocationEnabled()) {
            IUpView?.showProgressHudCoords()
        }*/
    }
    override  fun isLocationEnabled(): Boolean {
        return locationManager!!.isProviderEnabled(LocationManager.GPS_PROVIDER) || locationManager!!.isProviderEnabled(LocationManager.NETWORK_PROVIDER)
    }

    fun startLocationGps(activity: Activity) {
        var intent =  Intent(activity, CoordsServiceKotlin::class.java);
        activity!!.startService(intent)
    }


    //endregion




    override fun getLotes(unidad_productiva_id: Long?) {
        loteInteractor?.execute(unidad_productiva_id)
    }

    //region Conectividad
    private val mNotificationReceiverApp = object : BroadcastReceiver() {
        override fun onReceive(context: Context, intent: Intent) {
            var extras = intent.extras
            if (extras != null) {
                loteMainView?.onEventBroadcastReceiver(extras, intent)
            }
        }
    }

    override fun checkConnection(): Boolean {
        return ConnectivityReceiver.isConnected
        //showSnack(isConnected);
    }

    override fun onResume(context: Context) {
        context.registerReceiver(mNotificationReceiverApp, IntentFilter(Const.SERVICE_CONECTIVITY))
        context.registerReceiver(mNotificationReceiverApp, IntentFilter(Const.SERVICE_LOCATION))

    }

    override fun onPause(context: Context) {
        context.unregisterReceiver(this.mNotificationReceiverApp);
    }



    //region Suscribe Events
    @Subscribe
    override fun onEventMainThread(eventLote: RequestEventLote?) {
        when (eventLote?.eventType) {
            RequestEventLote.READ_EVENT -> {
                var loteList = eventLote.mutableList as List<Lote>
                loteMainView?.setListLotes(loteList)
            }
            RequestEventLote.SAVE_EVENT -> {
                onLoteSaveOk()
                var loteList = eventLote.mutableList as List<Lote>
                loteMainView?.setListLotes(loteList)
            }
            RequestEventLote.UPDATE_EVENT -> {
                onLoteUpdateOk()
                var loteList = eventLote.mutableList as List<Lote>
                loteMainView?.setListLotes(loteList)
            }
            RequestEventLote.DELETE_EVENT -> {
                onLoteDeleteOk()
                var loteList = eventLote.mutableList as List<Lote>
                loteMainView?.setListLotes(loteList)
            }
            RequestEventLote.ERROR_EVENT -> {
                onMessageError(eventLote.mensajeError)
            }

        //EVENTS ONITEM CLICK
            RequestEventLote.ITEM_EVENT -> {
                var lote = eventLote.objectMutable as Lote
                loteMainView?.onMessageOk(R.color.colorPrimary, "Item: " + lote.Nombre)
            }
            RequestEventLote.ITEM_READ_EVENT -> {
                var lote = eventLote.objectMutable as Lote
                loteMainView?.onMessageOk(R.color.colorPrimary, "Leer: " + lote.Nombre)
                ///  Toast.makeText(activity,"Leer: "+lote.Nombre,Toast.LENGTH_LONG).show()
            }
            RequestEventLote.ITEM_EDIT_EVENT -> {
                var lote = eventLote.objectMutable as Lote
                loteMainView?.showAlertDialogAddLote(lote)
            }
            RequestEventLote.ITEM_DELETE_EVENT -> {
                var lote = eventLote.objectMutable as Lote
                loteMainView?.confirmDelete(lote)
                //// Toast.makeText(activity,"Eliminar: "+lote.Nombre,Toast.LENGTH_LONG).show()
            }

        //LIST EVENTS
            RequestEventLote.LIST_EVENT_UP -> {
                var list = eventLote.mutableList as List<Unidad_Productiva>
                loteMainView?.setListUP(list)
            }

            RequestEventLote.LIST_EVENT_UNIDAD_MEDIDA -> {
                var list = eventLote.mutableList as List<Unidad_Medida>
                loteMainView?.setListUnidadMedida(list)
            }

            //Verificate Conection
        //Error Conection
            RequestEventLote.ERROR_VERIFICATE_CONECTION -> {
                onMessageConectionError()
            }
        }

    }


    //endregion

    //region Methods

    override fun validarCampos(): Boolean? {
        if (loteMainView?.validarCampos() == true) {
            return true
        }
        return false
    }

    override fun registerLote(lote: Lote, unidad_productiva_id: Long?) {
        loteMainView?.showProgressHud()
        loteMainView?.disableInputs()
        loteInteractor?.registerLote(lote, unidad_productiva_id,checkConnection())
    }

    override fun updateLote(lote: Lote, unidad_productiva_id: Long?) {
        loteMainView?.showProgressHud()
        loteMainView?.disableInputs()
        loteInteractor?.updateLote(lote, unidad_productiva_id,checkConnection())
    }

    override fun deleteLote(lote: Lote, unidad_productiva_id: Long?) {
        loteMainView?.showProgressHud()
        loteInteractor?.deleteLote(lote, unidad_productiva_id,checkConnection())
    }

    override fun loadListas() {
        loteInteractor?.loadListas()
    }

    override fun verificateAreaLoteBiggerUp(unidad_productiva_id:Long?,area:Double):Boolean {
        return loteInteractor?.verificateAreaLoteBiggerUp(unidad_productiva_id,area)!!
    }

    //endregion

    //region Acciones de Respuesta a Post de Eventos
    private fun onLoteSaveOk() {
        onMessageOk()
    }

    private fun onLoteUpdateOk() {
        onMessageOk()
    }

    private fun onLoteDeleteOk() {
        loteMainView?.hideProgressHud()
        loteMainView?.requestResponseOk()
    }


    //endregion


    //region Messages/Notificaciones

    private fun onMessageOk() {
        loteMainView?.enableInputs()
        loteMainView?.hideProgress()
        loteMainView?.hideProgressHud()
        loteMainView?.limpiarCampos()
        loteMainView?.requestResponseOk()
    }

    private fun onMessageError(error: String?) {
        loteMainView?.limpiarCampos()
        loteMainView?.enableInputs()
        loteMainView?.hideProgress()
        loteMainView?.hideProgressHud()
        loteMainView?.requestResponseError(error)
    }

    private fun onMessageConectionError() {
        loteMainView?.hideProgress()
        loteMainView?.hideProgressHud()
        loteMainView?.verificateConnection()
    }

    //endregion

}