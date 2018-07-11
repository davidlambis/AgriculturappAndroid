package com.interedes.agriculturappv3.modules.productor.accounting_module.transacciones

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import com.interedes.agriculturappv3.modules.models.ventas.CategoriaPuk
import com.interedes.agriculturappv3.modules.models.ventas.Puk
import com.interedes.agriculturappv3.modules.models.ventas.Transaccion
import com.interedes.agriculturappv3.modules.productor.accounting_module.transacciones.events.RequestEventTransaccion
import com.interedes.agriculturappv3.libs.EventBus
import com.interedes.agriculturappv3.libs.GreenRobotEventBus
import com.interedes.agriculturappv3.modules.models.cultivo.Cultivo
import com.interedes.agriculturappv3.modules.models.lote.Lote
import com.interedes.agriculturappv3.modules.models.unidad_productiva.Unidad_Productiva
import com.interedes.agriculturappv3.modules.models.unidad_medida.Unidad_Medida
import com.interedes.agriculturappv3.modules.models.ventas.Estado_Transaccion
import com.interedes.agriculturappv3.services.resources.Const_Resources
import com.interedes.agriculturappv3.services.internet_connection.ConnectivityReceiver
import org.greenrobot.eventbus.Subscribe


class TransaccionPresenter(var mainView: IMainViewTransacciones.MainView?) : IMainViewTransacciones.Presenter {

    var interactor: IMainViewTransacciones.Interactor? = null
    var eventBus: EventBus? = null

    //GLOBALS
    var listUnidadProductivaGlobal:List<Unidad_Productiva>?= ArrayList<Unidad_Productiva>()
    var listLoteGlobal:List<Lote>?= ArrayList<Lote>()
    var listCultivosGlobal:List<Cultivo>?= ArrayList<Cultivo>()
    var listUnidadMedidaGlobal:List<Unidad_Medida>?= ArrayList<Unidad_Medida>()

    var listCategoriaPukGlobal:List<CategoriaPuk>?= ArrayList<CategoriaPuk>()
    var listPukGlobal:List<Puk>?= ArrayList<Puk>()



    companion object {
        var instance: TransaccionPresenter? = null
    }

    init {
        instance = this
        interactor = TransaccionInteractor()
        eventBus = GreenRobotEventBus()
    }

    override fun onCreate() {
        eventBus?.register(this)
        getListas()
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
        context.registerReceiver(mNotificationReceiver, IntentFilter(Const_Resources.SERVICE_CONECTIVITY))
    }

    override fun onPause(context: Context) {
        context.unregisterReceiver(this.mNotificationReceiver);
    }


    //region Suscribe Events
    @Subscribe
    override fun onEventMainThread(event: RequestEventTransaccion?) {
        when (event?.eventType) {
            RequestEventTransaccion.READ_EVENT -> {
                var list = event.mutableList as List<Transaccion>
                mainView?.setListTransaccion(list)
            }
            RequestEventTransaccion.SAVE_EVENT -> {
                var list = event.mutableList as List<Transaccion>
                mainView?.setListTransaccion(list)
                onSaveOk()
            }
            RequestEventTransaccion.UPDATE_EVENT -> {
                var list = event.mutableList as List<Transaccion>
                mainView?.setListTransaccion(list)
                onUpdateOk()
            }
            RequestEventTransaccion.DELETE_EVENT -> {
                var list = event.mutableList as List<Transaccion>
                mainView?.setListTransaccion(list)
                onDeleteOk()
            }
            RequestEventTransaccion.ERROR_EVENT -> {
                onMessageError(event.mensajeError)
            }

        //EVENTS ONITEM CLICK
            RequestEventTransaccion.ITEM_EVENT -> {
                var proiduccion = event.objectMutable as Transaccion
                //loteMainView?.onMessageOk(R.color.colorPrimary,"Item: "+lote.nombre)
            }
            RequestEventTransaccion.ITEM_READ_EVENT -> {
                var producccion = event.objectMutable as Transaccion
                //loteMainView?.onMessageOk(R.color.colorPrimary,"Leer: "+lote.nombre)
                ///  Toast.makeText(activity,"Leer: "+lote.nombre,Toast.LENGTH_LONG).show()
            }
            RequestEventTransaccion.ITEM_EDIT_EVENT -> {
                var producccion = event.objectMutable as Transaccion
                mainView?.showAlertDialogAddTransaccion(producccion)
            }
            RequestEventTransaccion.ITEM_DELETE_EVENT -> {
                var producccion = event.objectMutable as Transaccion
                mainView?.confirmDelete(producccion)
                //// Toast.makeText(activity,"Eliminar: "+lote.nombre,Toast.LENGTH_LONG).show()
            }

        //LIST EVENTS

            RequestEventTransaccion.LIST_EVENT_UP -> {
                listUnidadProductivaGlobal= event.mutableList as List<Unidad_Productiva>
            }

            RequestEventTransaccion.LIST_EVENT_LOTE -> {
                listLoteGlobal= event.mutableList as List<Lote>
            }

            RequestEventTransaccion.LIST_EVENT_CULTIVO -> {
                listCultivosGlobal= event.mutableList as List<Cultivo>
            }

            RequestEventTransaccion.LIST_EVENT_CATEGORIA_PUK -> {
                listCategoriaPukGlobal= event.mutableList as List<CategoriaPuk>
            }

            RequestEventTransaccion.LIST_EVENT_PUK -> {
                listPukGlobal= event.mutableList as List<Puk>

            }


        //Get Single
            RequestEventTransaccion.GET_EVENT_CULTIVO -> {
                var cultivo = event.objectMutable as Cultivo
                mainView?.setCultivo(cultivo)
            }

            RequestEventTransaccion.ITEM_EVENT_RADIO_TYPE_TRANSACION -> {
                var transaccion = event.objectMutable as Estado_Transaccion

                mainView?.requestResponseItemOK(transaccion.Nombre,transaccion.Id.toString())
            }
                //Error Conection
            RequestEventTransaccion.ERROR_VERIFICATE_CONECTION -> {
                onMessageConectionError()
            }
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

    override fun validarListasAddTransaccion(): Boolean? {
        if (mainView?.validarListasAddTransaccion() == true) {
            return true
        }
        return false
    }

    override fun registerTransaccion(transaccion: Transaccion, cultivo_Id: Long?) {
        mainView?.showProgress()
        mainView?.disableInputs()
        interactor?.registerTransaccion(transaccion, cultivo_Id,checkConnection())

    }

    override fun updateTransaccion(produccion: Transaccion, cultivo_id: Long?) {
        mainView?.showProgress()
        mainView?.disableInputs()
        interactor?.updateTransaccion(produccion, cultivo_id,checkConnection())

    }


    override fun deleteTransaccion(transaccion: Transaccion, cultivo_id: Long?) {
        mainView?.showProgressHud()
        interactor?.deleteProducccionTransaccion(transaccion, cultivo_id,checkConnection())

    }

    override fun getListTransaccion(cultivo_id:Long?,typeTransaccion:Long?) {
        interactor?.execute(cultivo_id,typeTransaccion)
    }

    override fun getListas() {
        interactor?.getListas()
    }


    override fun getCultivo(cultivo_id:Long?){
        interactor?.getCultivo(cultivo_id)
    }

    //endregion

    //region METHODS VIEWS
    override fun setListSpinnerLote(unidad_productiva_id: Long?) {
        var list= listLoteGlobal?.filter { lote: Lote -> lote.Unidad_Productiva_Id==unidad_productiva_id }
        mainView?.setListLotes(list)
    }

    override fun setListSpinnerCultivo(lote_id: Long?) {
        var list= listCultivosGlobal?.filter { cultivo: Cultivo -> cultivo.LoteId==lote_id }
        mainView?.setListCultivos(list)
    }

    override fun setListSpinnerUnidadProductiva() {
        mainView?.setListUnidadProductiva(listUnidadProductivaGlobal)
    }


    override fun setListSpinnerPuk(categoria_puk_id:Long?){
        var list= listPukGlobal?.filter { puk: Puk -> puk.CategoriaId==categoria_puk_id }
        mainView?.setListPuk(list)
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
        mainView?.hideProgressHud()
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


    private fun onMessageOkItem() {

        mainView?.requestResponseOK()
    }
    //endregion
}