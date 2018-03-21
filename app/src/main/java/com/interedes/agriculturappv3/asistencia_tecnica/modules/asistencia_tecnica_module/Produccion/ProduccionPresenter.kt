package com.interedes.agriculturappv3.asistencia_tecnica.modules.asistencia_tecnica_module.Produccion

import android.app.Activity
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import com.interedes.agriculturappv3.asistencia_tecnica.models.produccion.Produccion
import com.interedes.agriculturappv3.events.RequestEvent
import com.interedes.agriculturappv3.libs.EventBus
import com.interedes.agriculturappv3.libs.GreenRobotEventBus
import com.interedes.agriculturappv3.services.Const
import com.interedes.agriculturappv3.services.internet_connection.ConnectivityReceiver
import org.greenrobot.eventbus.Subscribe

/**
 * Created by usuario on 21/03/2018.
 */


class ProduccionPresenter(var mainView: IMainProduccion.MainView?) : IMainProduccion.Presenter {

    var interactor: IMainProduccion.Interactor? = null
    var eventBus: EventBus? = null


    companion object {
        var instance: ProduccionPresenter? = null
    }

    init {
        instance = this
        interactor = ProduccionInteractor()
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
    private val mNotificationReceiver = object : BroadcastReceiver() {
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
        context.registerReceiver(mNotificationReceiver, IntentFilter(Const.SERVICE_CONECTIVITY))
    }

    override fun onPause(context: Context) {
        context.unregisterReceiver(this.mNotificationReceiver);
    }


    //region Suscribe Events
    @Subscribe
    override fun onEventMainThread(eventLote: RequestEvent?) {
        when (eventLote?.eventType) {
            RequestEvent.READ_EVENT -> {
                var produccionList = eventLote.mutableList as List<Produccion>
                //loteMainView?.setListLotes(loteList)
            }
            RequestEvent.SAVE_EVENT -> {
                var produccionList = eventLote.mutableList as List<Produccion>
                //loteMainView?.setListLotes(loteList)
                onSaveOk()
            }
            RequestEvent.UPDATE_EVENT -> {
                var produccionList = eventLote.mutableList as List<Produccion>
                //loteMainView?.setListLotes(loteList)
                onUpdateOk()
            }
            RequestEvent.DELETE_EVENT -> {
                var produccionList = eventLote.mutableList as List<Produccion>
                //loteMainView?.setListLotes(loteList)
                onDeleteOk()
            }
            RequestEvent.ERROR_EVENT -> {
                onMessageError(eventLote.mensajeError)
            }

        //EVENTS ONITEM CLICK
            RequestEvent.ITEM_EVENT -> {
                var proiduccion = eventLote.objectMutable as Produccion
                //loteMainView?.onMessageOk(R.color.colorPrimary,"Item: "+lote.Nombre)
            }
            RequestEvent.ITEM_READ_EVENT -> {
                var lote = eventLote.objectMutable as Produccion
                //loteMainView?.onMessageOk(R.color.colorPrimary,"Leer: "+lote.Nombre)
                ///  Toast.makeText(activity,"Leer: "+lote.Nombre,Toast.LENGTH_LONG).show()
            }
            RequestEvent.ITEM_EDIT_EVENT -> {
                var lote = eventLote.objectMutable as Produccion
                //Lote_Fragment.instance?.loteGlobal=lote
                //loteMainView?.showAlertDialogAddLote(Lote_Fragment.instance?.loteGlobal)
            }
            RequestEvent.ITEM_DELETE_EVENT -> {
                var lote = eventLote.objectMutable as Produccion
                //loteMainView?.confirmDelete(lote)
                //// Toast.makeText(activity,"Eliminar: "+lote.Nombre,Toast.LENGTH_LONG).show()
            }
        //LIST EVENTS
        }
    }
    //endregion

    //region Methods
    override fun validarCampos(): Boolean? {
        if (mainView?.validarCampos() == true) {
            return true
        }
        return false
    }

    override fun registerProdcuccion(producccion: Produccion, cultivo_Id: Long) {
        mainView?.showProgress()
        //if(checkConnection()){
        mainView?.disableInputs()
        interactor?.registerProduccion(producccion, cultivo_Id)
    }

    override fun updateProducccion(produccion: Produccion, cultivo_id: Long) {
        mainView?.showProgress()
        if (checkConnection()) {
            mainView?.disableInputs()
            interactor?.registerProduccion(produccion, cultivo_id)
        } else {
            onMessageConectionError()
        }
    }


    override fun deleteProduccion(producccion: Produccion, cultivo_id: Long) {
        mainView?.showProgress()
        if (checkConnection()) {
            interactor?.deleteProducccion(producccion, cultivo_id)
        } else {
            onMessageConectionError()
        }
    }

    override fun getListProduccion(cultivo_id:Long?) {
        interactor?.execute(cultivo_id)
    }

    //endregion

    //region Acciones de Respuesta a Post de Eventos
    private fun onSaveOk() {
        onMessageOk()
    }

    private fun onUpdateOk() {
        onMessageOk()
    }

    private fun onDeleteOk() {
        mainView?.requestResponseOK()
    }
    //endregion

    //region Messages/Notificaciones
    private fun onMessageOk() {
        mainView?.enableInputs()
        mainView?.hideProgress()
        mainView?.limpiarCampos()
        mainView?.requestResponseOK()
    }

    private fun onMessageError(error: String?) {
        mainView?.enableInputs()
        mainView?.hideProgress()
        mainView?.requestResponseError(error)
    }

    private fun onMessageConectionError() {
        mainView?.hideProgress()
        mainView?.verificateConnection()
    }
    //endregion
}