package com.interedes.agriculturappv3.asistencia_tecnica.modules.asistencia_tecnica_module.Lote.presenter

import android.app.Activity
import android.content.Context
import com.interedes.agriculturappv3.asistencia_tecnica.models.Lote
import com.interedes.agriculturappv3.asistencia_tecnica.modules.asistencia_tecnica_module.Lote.adapter.ListenerAdapterEvent
import com.interedes.agriculturappv3.events.ListEvent
import com.interedes.agriculturappv3.events.RequestEvent

/**
 * Created by EnuarMunoz on 7/03/18.
 */
interface LotePresenter {
    fun onCreate()
    fun onDestroy()
    fun onResume(context:Context)
    fun onPause(context:Context)
    fun onEventMainThread(event: RequestEvent?)
    fun validarCampos(): Boolean?

    //Listas
    fun listUP()

    //CRUD
    fun registerLote(lote: Lote,unidad_productiva_id:Long?)
    fun updateLote(lote: Lote,unidad_productiva_id:Long?)
    fun deleteLote(lote: Lote,unidad_productiva_id:Long?)
    fun getLotes(unidad_productiva_id:Long?)

    //Coords Service
    fun startGps(activity:Activity)
    fun closeServiceGps()
    fun onEventMainThreadOnItemClick(event: ListenerAdapterEvent?)
    fun onEventMainThreadList(event: ListEvent?)

    //Conecttion
    fun checkConnection(): Boolean

}