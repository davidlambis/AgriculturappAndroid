package com.interedes.agriculturappv3.modules.productor.asistencia_tecnica_module.insumos

import android.content.Context
import android.content.Intent
import android.os.Bundle
import com.interedes.agriculturappv3.modules.models.tratamiento.Tratamiento
import com.interedes.agriculturappv3.modules.productor.asistencia_tecnica_module.insumos.events.InsumosEvent


interface InterfaceInsumos {

    interface View {
        fun showRefresh()
        fun hideRefresh()
        fun getInsumosByPlaga(tipoEnfermedadId: Long?)
        fun setTratamientosList(listTratamiento: List<Tratamiento>)
        fun setResults(insumos: Int)
        fun verTratamiento(tratamiento:Tratamiento? )
       // fun setDialogListInsumos(listInsumos: List<Insumo>)
       //Events
       fun onEventBroadcastReceiver(extras: Bundle, intent: Intent)
    }

    interface Presenter {
        fun onCreate()
        fun onDestroy()
        fun getInsumosByPlaga(tipoEnfermedadId: Long?)
        fun onEventMainThread(insumosEvent: InsumosEvent?)
        //fun setInsumo(insumoId: Long?)
        fun onResume(context: Context)
        fun onPause(context: Context)
        //Conection
        fun checkConnection(): Boolean
    }

    interface Interactor {
        fun getInsumosByPlaga(tipoEnfermedadId: Long?)
        //fun setInsumo(insumoId: Long?)
    }

    interface Repository {
        fun getInsumosByPlaga(tipoEnfermedadId: Long?)
        //fun setInsumo(insumoId: Long?)
    }
}