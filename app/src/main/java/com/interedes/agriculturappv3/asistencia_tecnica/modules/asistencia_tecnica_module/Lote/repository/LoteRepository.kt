package com.interedes.agriculturappv3.asistencia_tecnica.modules.asistencia_tecnica_module.Lote.repository

import com.interedes.agriculturappv3.asistencia_tecnica.models.Lote

/**
 * Created by EnuarMunoz on 7/03/18.
 */
interface LoteRepository {
    //fun registerLote(lote: Lote)

    fun getLotes(unidad_productiva_id:Long?):List<Lote>
    fun getListLotes(unidad_productiva_id:Long?)
    fun saveLotes(lote: Lote,unidad_productiva_id:Long?)
    fun updateLote(lote: Lote,unidad_productiva_id:Long?)
    fun deleteLote(lote: Lote,unidad_productiva_id:Long?)

    //ListUp
    fun loadListas()
}