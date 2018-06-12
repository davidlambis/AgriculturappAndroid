package com.interedes.agriculturappv3.modules.productor.asistencia_tecnica_module.plagas

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import com.interedes.agriculturappv3.modules.models.plagas.Enfermedad
import com.interedes.agriculturappv3.modules.productor.asistencia_tecnica_module.plagas.events.PlagasEvent
import com.interedes.agriculturappv3.libs.EventBus
import com.interedes.agriculturappv3.libs.GreenRobotEventBus
import com.interedes.agriculturappv3.modules.models.tipoproducto.TipoProducto
import com.interedes.agriculturappv3.services.Const
import com.interedes.agriculturappv3.services.internet_connection.ConnectivityReceiver
import org.greenrobot.eventbus.Subscribe


class PlagaPresenter(var view: IPlaga.View?) : IPlaga.Presenter {
    var interactor: IPlaga.Interactor? = null
    var eventBus: EventBus? = null

    init {
        interactor = PlagaInteractor()
        eventBus = GreenRobotEventBus()
    }

    //region Métodos Interfaz
    override fun onCreate() {
        eventBus?.register(this)
    }

    override fun onDestroy() {
        view = null
        eventBus?.unregister(this)
    }

    //region Conectividad
    private val mNotificationReceiverApp = object : BroadcastReceiver() {
        override fun onReceive(context: Context, intent: Intent) {
            var extras = intent.extras
            if (extras != null) {
                view?.onEventBroadcastReceiver(extras, intent)
            }
        }
    }

    override fun checkConnection(): Boolean {
        return ConnectivityReceiver.isConnected
    }

    override fun onResume(context: Context) {
        context.registerReceiver(mNotificationReceiverApp, IntentFilter(Const.SERVICE_CONECTIVITY))
    }

    override fun onPause(context: Context) {
        context.unregisterReceiver(this.mNotificationReceiverApp);
    }

    @Subscribe
    override fun onEventMainThread(plagasEvent: PlagasEvent?) {
        when (plagasEvent?.eventType) {
            PlagasEvent.READ_EVENT -> {
                val list_plagas = plagasEvent.mutableList as ArrayList<Enfermedad>
                view?.setDialogListPlagas(list_plagas)
                //view?.setListPlagas(list_plagas)
            }
            PlagasEvent.SET_EVENT -> {
                val list_plagas = plagasEvent.mutableList as ArrayList<Enfermedad>
                view?.setListPlagas(list_plagas)
            }

            PlagasEvent.ITEM_EVENT_PLAGA -> {
                val enfermedad = plagasEvent.objectMutable as Enfermedad
                view?.setViewDialogDescriptionFoto(enfermedad)

            }





            PlagasEvent.LOAD_LIST_TIPO_PRODUCTO -> {
                val lista_tipo_producto = plagasEvent.mutableList as ArrayList<TipoProducto>
                view?.loadListTipoProducto(lista_tipo_producto)
            }

            ///
            PlagasEvent.ITEM_SELECT_PLAGA_EVENT -> {
                val plaga = plagasEvent.objectMutable as Enfermedad
                val list_plagas = ArrayList<Enfermedad>()
                list_plagas.add(plaga)
                view?.setListPlagas(list_plagas)
            }
            PlagasEvent.ITEM_EVENT -> {
                val tipo_producto = plagasEvent.objectMutable as TipoProducto
                view?.hideDialog(tipo_producto)
                view?.getPlagasByTipoProducto(tipo_producto.Id)
            }
            PlagasEvent.ITEM_OPEN_EVENT -> {
                val tipo_enfermedad = plagasEvent.objectMutable as Enfermedad
                view?.verInsumos(tipo_enfermedad)
            }
        }
    }

    override fun getPlagasByTipoProducto(tipoProductoId: Long?) {
        interactor?.getPlagasByTipoProducto(tipoProductoId)
    }


    override fun setPlaga(tipoEnfermedadId: Long?) {
        interactor?.setPlaga(tipoEnfermedadId)
    }

    override fun getTiposProducto() {
        interactor?.getTiposProducto()
    }
    //endregion
}