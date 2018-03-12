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
    fun registerLote(lote: Lote)
    fun updateLote(lote: Lote)
    fun deleteLote(lote: Lote)
    fun getLotes()

    //Coords Service
    fun startGps(activity:Activity)
    fun closeServiceGps()
    fun onEventMainThreadOnItemClick(event: ListenerAdapterEvent?)
    fun onEventMainThreadList(event: ListEvent?)

}