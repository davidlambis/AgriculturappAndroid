package com.interedes.agriculturappv3.modules.productor.asistencia_tecnica_module.plagas

import android.content.Context
import android.content.Intent
import android.os.Bundle
import com.interedes.agriculturappv3.modules.models.plagas.Enfermedad
import com.interedes.agriculturappv3.modules.models.tipoproducto.TipoProducto
import com.interedes.agriculturappv3.modules.productor.asistencia_tecnica_module.plagas.events.PlagasEvent

interface IPlaga {

    interface View {
        fun showRefresh()
        fun hideRefresh()
        fun setListPlagas(list_plagas: ArrayList<Enfermedad>)
        fun setResults(plagas: Int)
        fun hideDialog(tipoProducto: TipoProducto)
        fun verInsumos(tipoEnfermedad: Enfermedad)
        fun setDialogListPlagas(list_plagas: ArrayList<Enfermedad>)
        fun loadListTipoProducto(listTipoProducto: ArrayList<TipoProducto>)

        fun setViewDialogDescriptionFoto(enfermedad:Enfermedad)
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
        fun checkListPlagas():Long

        fun onResume(context: Context)
        fun onPause(context: Context)
        //Conection
        fun checkConnection(): Boolean
    }

    interface Interactor {
        fun checkListPlagas():Long
        fun getPlagasByTipoProducto(tipoProductoId: Long?)
        fun setPlaga(tipoEnfermedadId: Long?)
        fun getTiposProducto()
    }

    interface Repository {

        fun checkListPlagas():Long

        fun getPlagasByTipoProducto(tipoProductoId: Long?)
        fun setPlaga(tipoEnfermedadId: Long?)
        fun getTiposProducto()
    }
}