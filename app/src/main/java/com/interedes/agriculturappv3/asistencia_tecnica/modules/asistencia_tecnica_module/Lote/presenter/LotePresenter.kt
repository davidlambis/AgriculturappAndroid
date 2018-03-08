package com.interedes.agriculturappv3.asistencia_tecnica.modules.asistencia_tecnica_module.Lote.presenter

import com.interedes.agriculturappv3.asistencia_tecnica.models.Lote
import com.interedes.agriculturappv3.events.RequestEvent

/**
 * Created by EnuarMunoz on 7/03/18.
 */
interface LotePresenter {
    fun onCreate()
    fun onDestroy()
    fun onEventMainThread(event: RequestEvent?)
    fun validarCampos(): Boolean?

    //CRUD
    fun registerLote(lote: Lote)
    fun updateLote(lote: Lote)
    fun deleteLote(lote: Lote)
    fun getLotes()
}