package com.interedes.agriculturappv3.asistencia_tecnica.modules.asistencia_tecnica_module.plagas

import android.content.Context
import android.content.Intent
import android.os.Bundle
import com.interedes.agriculturappv3.asistencia_tecnica.models.TipoProducto
import com.interedes.agriculturappv3.asistencia_tecnica.models.plagas.Enfermedad
import com.interedes.agriculturappv3.asistencia_tecnica.models.plagas.TipoEnfermedad
import com.interedes.agriculturappv3.asistencia_tecnica.modules.asistencia_tecnica_module.plagas.events.PlagasEvent

interface IPlaga {

    interface View {
        fun showRefresh()
        fun hideRefresh()
        fun getPlagasByTipoProducto(tipoProductoId: Long?)
        fun setListPlagas(list_plagas: ArrayList<TipoEnfermedad>)
        fun setResults(plagas: Int)
        fun hideDialog(tipoProducto: TipoProducto)
        fun verInsumos(tipoEnfermedad: TipoEnfermedad)
        fun setDialogListPlagas(list_plagas: ArrayList<TipoEnfermedad>)
        fun setIdEnfermedad(enfermedadId: Long?)
        fun loadListTipoProducto(listTipoProducto: ArrayList<TipoProducto>)
        //Events
        fun onEventBroadcastReceiver(extras: Bundle, intent: Intent)
    }

    interface Presenter {
        fun onCreate()
        fun onDestroy()
        fun getPlagasByTipoProducto(tipoProductoId: Long?)
        fun setPlaga(tipoEnfermedadId: Long?)
        fun onEventMainThread(plagasEvent: PlagasEvent?)
        fun getTiposProducto()

        fun onResume(context: Context)
        fun onPause(context: Context)
        //Conection
        fun checkConnection(): Boolean
    }

    interface Interactor {
        fun getPlagasByTipoProducto(tipoProductoId: Long?)
        fun setPlaga(tipoEnfermedadId: Long?)
        fun getTiposProducto()
    }

    interface Repository {
        fun getPlagasByTipoProducto(tipoProductoId: Long?)
        fun setPlaga(tipoEnfermedadId: Long?)
        fun getTiposProducto()
    }
}