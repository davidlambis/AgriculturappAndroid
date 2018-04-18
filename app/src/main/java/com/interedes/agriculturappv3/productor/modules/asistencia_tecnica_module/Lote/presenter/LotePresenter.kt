package com.interedes.agriculturappv3.productor.modules.asistencia_tecnica_module.Lote.presenter

import android.app.Activity
import android.content.Context
import com.interedes.agriculturappv3.productor.models.lote.Lote
import com.interedes.agriculturappv3.productor.modules.asistencia_tecnica_module.Lote.events.RequestEventLote

/**
 * Created by EnuarMunoz on 7/03/18.
 */
interface LotePresenter {
    fun onCreate()
    fun onDestroy()
    fun onResume(context:Context)
    fun onPause(context:Context)
    fun validarCampos(): Boolean?

    //Listas
    fun loadListas()

    //CRUD
    fun registerLote(lote: Lote, unidad_productiva_id:Long?)
    fun updateLote(lote: Lote,unidad_productiva_id:Long?)
    fun deleteLote(lote: Lote,unidad_productiva_id:Long?)
    fun getLotes(unidad_productiva_id:Long?)


    //Events
    fun onEventMainThread(eventLote: RequestEventLote?)


    //Coords Service
    fun startGps(activity:Activity)
    fun closeServiceGps()
    //Conecttion
    fun checkConnection(): Boolean

}