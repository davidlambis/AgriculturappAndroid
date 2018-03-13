package com.interedes.agriculturappv3.asistencia_tecnica.modules.asistencia_tecnica_module.Lote.interactor

import com.interedes.agriculturappv3.asistencia_tecnica.models.Lote

/**
 * Created by EnuarMunoz on 7/03/18.
 */
interface LoteInteractor {
    fun registerLote(lote : Lote,unidad_productiva_id:Long?)
    fun updateLote(lote : Lote,unidad_productiva_id:Long?)
    fun deleteLote(lote : Lote,unidad_productiva_id:Long?)
    fun execute(unidad_productiva_id:Long?)

    fun loadListUp()
}