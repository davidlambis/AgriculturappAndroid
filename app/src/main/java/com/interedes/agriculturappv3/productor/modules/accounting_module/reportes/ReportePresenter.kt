package com.interedes.agriculturappv3.productor.modules.accounting_module.reportes

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import com.interedes.agriculturappv3.libs.EventBus
import com.interedes.agriculturappv3.libs.GreenRobotEventBus
import com.interedes.agriculturappv3.productor.models.Cultivo
import com.interedes.agriculturappv3.productor.models.lote.Lote
import com.interedes.agriculturappv3.productor.models.unidad_productiva.UnidadProductiva
import com.interedes.agriculturappv3.productor.models.ventas.CategoriaPuk
import com.interedes.agriculturappv3.productor.models.ventas.resports.BalanceContable
import com.interedes.agriculturappv3.productor.modules.accounting_module.reportes.events.RequestEventReporte
import com.interedes.agriculturappv3.services.Const
import com.interedes.agriculturappv3.services.internet_connection.ConnectivityReceiver
import org.greenrobot.eventbus.Subscribe
import java.util.*

class ReportePresenter(var mainView: IMainViewReportes.MainView?):IMainViewReportes.Presenter {

    var interactor: IMainViewReportes.Interactor? = null
    var eventBus: EventBus? = null

    //GLOBALS
    var listUnidadProductivaGlobal:List<UnidadProductiva>?= ArrayList<UnidadProductiva>()
    var listLoteGlobal:List<Lote>?= ArrayList<Lote>()
    var listCultivosGlobal:List<Cultivo>?= ArrayList<Cultivo>()


    companion object {
        var instance: ReportePresenter? = null
    }

    init {
        instance = this
        interactor = ReporteInteractor()
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
        context.registerReceiver(mNotificationReceiver, IntentFilter(Const.SERVICE_CONECTIVITY))
    }

    override fun onPause(context: Context) {
        context.unregisterReceiver(this.mNotificationReceiver);
    }


    //region Suscribe Events
    @Subscribe
    override fun onEventMainThread(event: RequestEventReporte?) {
        when (event?.eventType) {

            RequestEventReporte.ERROR_EVENT -> {
                onMessageError(event.mensajeError)
            }

        //EVENTS ONITEM CLICK
            RequestEventReporte.ITEM_EVENT -> {

            }
            RequestEventReporte.ITEM_READ_EVENT -> {

            }
            RequestEventReporte.ITEM_EDIT_EVENT -> {

            }
            RequestEventReporte.ITEM_DELETE_EVENT -> {

            }

        //LIST EVENTS
            RequestEventReporte.LIST_EVENT_UP -> {
                listUnidadProductivaGlobal= event.mutableList as List<UnidadProductiva>
            }

            RequestEventReporte.LIST_EVENT_LOTE -> {
                listLoteGlobal= event.mutableList as List<Lote>
            }

            RequestEventReporte.LIST_EVENT_CULTIVO -> {
                listCultivosGlobal= event.mutableList as List<Cultivo>
            }

            RequestEventReporte.LIST_EVENT_REPORT_CATEGORIAS -> {
                var categoriaList= event.mutableList as List<CategoriaPuk>
                mainView?.setListReportCategoriasPuk(categoriaList)
            }

        //Get Single
            RequestEventReporte.GET_EVENT_CULTIVO -> {
                var cultivo = event.objectMutable as Cultivo
                mainView?.setCultivo(cultivo)
            }

            RequestEventReporte.EVENT_BALANCE_CONTABLE -> {
                var balance = event.objectMutable as BalanceContable
                mainView?.setBalanceContable(balance)
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

    override fun validarDatesSelect(): Boolean {
        if (mainView?.validarDatesSelect() == true) {
            return true
        }
        return false
    }

    override fun validarListasFilterReports(): Boolean {
        if (mainView?.validarListasFilterReports() == true) {
            return true
        }
        return false
    }


    override fun getCultivo(cultivo_id: Long?) {
        interactor?.getCultivo(cultivo_id)
    }

    override fun getListas() {
        interactor?.getListas()
    }

    override fun getTotalTransacciones(cultivo_id: Long?,dateStart: Date?, dateEnd: Date?) {
        interactor?.getTotalTransacciones(cultivo_id,dateStart,dateEnd)
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

    private fun onMessageOkItem() {
        mainView?.requestResponseOK()
    }
    //endregion

}