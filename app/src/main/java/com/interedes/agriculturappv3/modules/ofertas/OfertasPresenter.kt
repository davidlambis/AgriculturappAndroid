package com.interedes.agriculturappv3.modules.ofertas

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import com.interedes.agriculturappv3.libs.EventBus
import com.interedes.agriculturappv3.libs.GreenRobotEventBus
import com.interedes.agriculturappv3.modules.models.cultivo.Cultivo
import com.interedes.agriculturappv3.modules.models.lote.Lote
import com.interedes.agriculturappv3.modules.models.ofertas.Oferta
import com.interedes.agriculturappv3.modules.models.producto.Producto
import com.interedes.agriculturappv3.modules.models.unidad_productiva.Unidad_Productiva
import com.interedes.agriculturappv3.modules.models.usuario.Usuario
import com.interedes.agriculturappv3.modules.productor.comercial_module.ofe.OfertasInteractor
import com.interedes.agriculturappv3.modules.ofertas.events.OfertasEvent
import com.interedes.agriculturappv3.services.Const
import com.interedes.agriculturappv3.services.internet_connection.ConnectivityReceiver
import org.greenrobot.eventbus.Subscribe
import java.util.ArrayList


class OfertasPresenter(var view: IOfertas.View?) : IOfertas.Presenter {

    var interactor: IOfertas.Interactor? = null
    var eventBus: EventBus? = null

    //GLOBALS
    var listUnidadProductivaGlobal: List<Unidad_Productiva>? = ArrayList<Unidad_Productiva>()
    var listLoteGlobal: List<Lote>? = ArrayList<Lote>()
    var listCultivosGlobal: List<Cultivo>? = ArrayList<Cultivo>()
    var listProductosGlobal: List<Producto>? = ArrayList<Producto>()

    companion object {
        var instance: OfertasPresenter? = null
    }

    init {
        instance = this
        interactor = OfertasInteractor()
        eventBus = GreenRobotEventBus()
    }

    //region Métodos Interfaz
    override fun onCreate() {
        eventBus?.register(this)

        getListas()
    }

    override fun onDestroy() {
        view = null
        eventBus?.unregister(this)
    }

    //region Conectividad
    private val mNotificationReceiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context, intent: Intent) {
            val extras = intent.extras
            if (extras != null) {
                view?.onEventBroadcastReceiver(extras, intent)
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
        context.unregisterReceiver(this.mNotificationReceiver)
    }

    override fun getListas() {
        interactor?.getListas()
    }

    @Subscribe
    override fun onEventMainThread(event: OfertasEvent?) {
        when (event?.eventType) {

            OfertasEvent.ERROR_EVENT -> {
                onMessageError(event.mensajeError)
            }

            OfertasEvent.UPDATE_EVENT -> {
                var list = event.mutableList as List<Oferta>
                view?.setListOfertas(list)
                onUpdateOk()
            }

        //Listas
            OfertasEvent.LIST_EVENT_UP -> {
                listUnidadProductivaGlobal = event.mutableList as List<Unidad_Productiva>
            }
            OfertasEvent.LIST_EVENT_LOTE -> {
                listLoteGlobal = event.mutableList as List<Lote>
            }

            OfertasEvent.LIST_EVENT_CULTIVO -> {
                listCultivosGlobal = event.mutableList as List<Cultivo>
            }
            OfertasEvent.LIST_EVENT_PRODUCTO -> {
                listProductosGlobal = event.mutableList as List<Producto>
            }
            OfertasEvent.READ_EVENT -> {
                val list = event.mutableList as List<Oferta>
                view?.setListOfertas(list)
                view?.hideProgress()
            }
            OfertasEvent.GET_EVENT_PRODUCTO -> {
                val producto = event.objectMutable as Producto
                view?.setProducto(producto)
            }


            OfertasEvent.REQUEST_CONFIRM_ITEM_EVENT -> {
                var oferta = event.objectMutable as Oferta
                view?.confirmAceptOferta(oferta)
            }

            OfertasEvent.REQUEST_CHAT_ITEM_EVENT -> {
                var oferta = event.objectMutable as Oferta

            }

            OfertasEvent.REQUEST_REFUSED_ITEM_EVENT -> {
                var oferta = event.objectMutable as Oferta
                view?.confirmResusedOferta(oferta)
            }

        //Error Conection
            OfertasEvent.ERROR_VERIFICATE_CONECTION -> {
                onMessageConectionError()
            }
        }
    }

    override fun validarListas(): Boolean? {
        if (view?.validarListas() == true) {
            return true
        }
        return false
    }

    override fun getListOfertas(productoId: Long?) {
        interactor?.getListOfertas(productoId)
    }

    override fun getProducto(productoId: Long?) {
        interactor?.getProducto(productoId)
    }


    override fun updateOferta(oferta: Oferta, productoId: Long?) {
        view?.showProgressHud()
        interactor?.updateOferta(oferta,productoId,checkConnection())
    }

    override  fun getUserLogued(): Usuario? {
        return interactor?.getUserLogued()
    }

    //SET Listas
    override fun setListSpinnerUnidadProductiva() {
        view?.setListUnidadProductiva(listUnidadProductivaGlobal)
    }

    override fun setListSpinnerLote(unidad_productiva_id: Long?) {
        val list = listLoteGlobal?.filter { lote: Lote -> lote.Unidad_Productiva_Id == unidad_productiva_id }
        view?.setListLotes(list)
    }

    override fun setListSpinnerCultivo(lote_id: Long?) {
        val list = listCultivosGlobal?.filter { cultivo: Cultivo -> cultivo.LoteId == lote_id }
        view?.setListCultivos(list)
    }

    override fun setListSpinnerProducto(cultivo_id: Long?) {
        val list = listProductosGlobal?.filter { producto: Producto -> producto.cultivoId == cultivo_id }
        view?.setListProductos(list)
    }


    //endregion


    // region Acciones de Respuesta a Post de Eventos
    private fun onSaveOk() {
        onMessageOk()
    }

    private fun onUpdateOk() {
        onMessageOk()
    }

    private fun onDeleteOk() {
        view?.hideProgress()
        view?.requestResponseOK()
    }
    //endregion

    //region Messages/Notificaciones
    private fun onMessageOk() {
        view?.hideProgressHud()
        view?.hideProgress()
        view?.requestResponseOK()
    }

    private fun onMessageError(error: String?) {
        view?.hideProgress()
        view?.requestResponseError(error)
    }

    private fun onMessageConectionError() {
        view?.hideProgressHud()
        view?.verificateConnection()
    }

}