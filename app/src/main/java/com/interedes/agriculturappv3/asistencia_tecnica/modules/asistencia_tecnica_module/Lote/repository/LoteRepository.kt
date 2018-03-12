package com.interedes.agriculturappv3.asistencia_tecnica.modules.asistencia_tecnica_module.Lote.repository

import com.interedes.agriculturappv3.asistencia_tecnica.models.Lote

/**
 * Created by EnuarMunoz on 7/03/18.
 */
interface LoteRepository {
    //fun registerLote(lote: Lote)

    fun getLotes():List<Lote>
    fun getListLotes()
    fun saveLotes(lote: Lote)
    fun updateLote(lote: Lote)
    fun deleteLote(lote: Lote)

    //ListUp
    fun getListUp()
}