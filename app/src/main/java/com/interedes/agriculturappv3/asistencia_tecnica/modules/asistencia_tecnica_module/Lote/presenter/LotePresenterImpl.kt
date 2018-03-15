package com.interedes.agriculturappv3.asistencia_tecnica.modules.asistencia_tecnica_module.Lote.presenter

import android.app.Activity
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import com.interedes.agriculturappv3.R
import com.interedes.agriculturappv3.asistencia_tecnica.models.Lote
import com.interedes.agriculturappv3.asistencia_tecnica.models.UnidadProductiva
import com.interedes.agriculturappv3.asistencia_tecnica.modules.asistencia_tecnica_module.Lote.adapter.ListenerAdapterEvent
import com.interedes.agriculturappv3.events.RequestEvent
import com.interedes.agriculturappv3.asistencia_tecnica.modules.asistencia_tecnica_module.Lote.interactor.LoteInteractor
import com.interedes.agriculturappv3.asistencia_tecnica.modules.asistencia_tecnica_module.Lote.interactor.LoteInteractorImpl
import com.interedes.agriculturappv3.asistencia_tecnica.modules.asistencia_tecnica_module.Lote.ui.LoteFragment
import com.interedes.agriculturappv3.asistencia_tecnica.modules.asistencia_tecnica_module.Lote.ui.MainViewLote
import com.interedes.agriculturappv3.events.ListEvent
import com.interedes.agriculturappv3.libs.EventBus
import com.interedes.agriculturappv3.libs.GreenRobotEventBus
import com.interedes.agriculturappv3.services.coords.CoordsServiceKotlin
import com.interedes.agriculturappv3.services.internet_connection.ConnectivityReceiver
import org.greenrobot.eventbus.Subscribe

/**
 * Created by EnuarMunoz on 7/03/18.
 */
class LotePresenterImpl(var loteMainView: MainViewLote?): LotePresenter{

    var coordsService: CoordsServiceKotlin? = null
    var loteInteractor: LoteInteractor? = null
    var eventBus: EventBus? = null

    init {
        loteInteractor=LoteInteractorImpl()
        eventBus = GreenRobotEventBus()

    }

    override fun onCreate() {
        eventBus?.register(this)
        listUP()
    }

    override fun onDestroy() {
        loteMainView = null
        if(coordsService!=null){
            CoordsServiceKotlin.instance?.closeService()
            //coordsService!!.closeService()
        }
    }

    override fun closeServiceGps() {
        if(coordsService!=null){
            //CoordsService.instance?.closeService()
            CoordsServiceKotlin.instance?.closeService()
            //coordsService!!.closeService()
        }
    }

    override fun getLotes(unidad_productiva_id:Long?) {
        loteInteractor?.execute(unidad_productiva_id)
    }

    //region Conectividad
    private val mNotificationReceiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context, intent: Intent) {
            var extras =intent.extras
            loteMainView?.onEventBroadcastReceiver(extras,intent);
        }
    }

    override fun checkConnection(): Boolean {
        return ConnectivityReceiver.isConnected
        //showSnack(isConnected);
    }

    override fun onResume(context:Context) {
        context.registerReceiver(mNotificationReceiver, IntentFilter("CONECTIVIDAD"))
        context.registerReceiver(mNotificationReceiver, IntentFilter("LOCATION"))
    }

    override fun onPause(context:Context) {
        context.unregisterReceiver(this.mNotificationReceiver);
    }

    override fun startGps(activity: Activity) {
        coordsService= CoordsServiceKotlin(activity)
        if(CoordsServiceKotlin.instance!!.isLocationEnabled()){
            loteMainView?.showProgressHud()
        }
    }

    //region Suscribe Events
    @Subscribe
    override fun onEventMainThread(event: RequestEvent?) {
            when (event?.eventType) {
                RequestEvent.READ_EVENT -> {
                    var loteList= event.mutableList as List<Lote>
                    loteMainView?.setListLotes(loteList)
                }
                RequestEvent.SAVE_EVENT ->{
                    var loteList= event.mutableList as List<Lote>
                    loteMainView?.setListLotes(loteList)
                    onLoteSaveOk()
                }
                RequestEvent.UPDATE_EVENT -> {
                    var loteList= event.mutableList as List<Lote>
                    loteMainView?.setListLotes(loteList)
                    onLoteUpdateOk()
                }
                RequestEvent.DELETE_EVENT -> {
                    var loteList= event.mutableList as List<Lote>
                    loteMainView?.setListLotes(loteList)
                    onLoteDeleteOk()
                }
                RequestEvent.ERROR_EVENT -> {
                    onMessageError(event.mensajeError)
                }
        }

    }


    @Subscribe
    override fun onEventMainThreadOnItemClick(event: ListenerAdapterEvent?) {
        when (event?.eventType) {
            ListenerAdapterEvent.ITEM_EVENT -> {
                var lote= event.objectMutable as Lote
                loteMainView?.onMessageOk(R.color.colorPrimary,"Item: "+lote.Nombre)
            }
            ListenerAdapterEvent.READ_EVENT -> {
                var lote= event.objectMutable as Lote
                loteMainView?.onMessageOk(R.color.colorPrimary,"Leer: "+lote.Nombre)
              ///  Toast.makeText(activity,"Leer: "+lote.Nombre,Toast.LENGTH_LONG).show()
            }
            ListenerAdapterEvent.EDIT_EVENT -> {
                var lote= event.objectMutable as Lote
                LoteFragment.instance?.loteGlobal=lote
                loteMainView?.showAlertDialogAddLote(LoteFragment.instance?.loteGlobal)
            }
            ListenerAdapterEvent.DELETE_EVENT -> {
                var lote= event.objectMutable as Lote
                loteMainView?.confirmDelete(lote)


                //// Toast.makeText(activity,"Eliminar: "+lote.Nombre,Toast.LENGTH_LONG).show()
            }
        }
    }


    @Subscribe
    override fun onEventMainThreadList(event: ListEvent?) {
        when (event?.eventType) {
            ListEvent.LIST_EVENT -> {
                var list= event.mutableList as List<UnidadProductiva>
                loteMainView?.setListUP(list)
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

    override fun registerLote(lote: Lote,unidad_productiva_id:Long?) {
        loteMainView?.showProgress()
        if(checkConnection()){
            loteMainView?.disableInputs()
            loteInteractor?.registerLote(lote,unidad_productiva_id)
        }else{
            onMessageConectionError()
        }
    }

    override fun updateLote(lote: Lote,unidad_productiva_id:Long?) {
        loteMainView?.showProgress()
        if(checkConnection()){
            loteMainView?.disableInputs()
            loteInteractor?.registerLote(lote,unidad_productiva_id)
        }else{
            onMessageConectionError()
        }
    }


    override fun deleteLote(lote: Lote,unidad_productiva_id:Long?) {
        loteMainView?.showProgress()
        if(checkConnection()){
            loteInteractor?.deleteLote(lote,unidad_productiva_id)
        }else{
            onMessageConectionError()
        }
    }



    override fun listUP() {
        loteInteractor?.loadListUp()
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
        loteMainView?.requestResponseOk()
    }


    //endregion


    //region Messages/Notificaciones

    private fun onMessageOk() {
        loteMainView?.enableInputs()
        loteMainView?.hideProgress()
        loteMainView?.limpiarCampos()
        loteMainView?.requestResponseOk()
    }

    private fun onMessageError(error: String?) {
        loteMainView?.enableInputs()
        loteMainView?.hideProgress()
        loteMainView?.requestResponseError(error)
    }

    private fun onMessageConectionError() {
        loteMainView?.hideProgress()
        loteMainView?.verificateConnection()
    }

    //endregion

}