package com.interedes.agriculturappv3.productor.modules.asistencia_tecnica_module.Lote.repository

import com.interedes.agriculturappv3.productor.models.lote.Lote
import com.interedes.agriculturappv3.productor.models.usuario.Usuario

/**
 * Created by EnuarMunoz on 7/03/18.
 */
interface LoteRepository {
    //fun registerLote(lote: Lote)
    fun getLotes(unidad_productiva_id: Long?): List<Lote>

    fun getListLotes(unidad_productiva_id: Long?)
    fun saveLotes(mLote: Lote, unidad_productiva_id: Long?)
    fun registerOnlineLote(mLote: Lote, unidad_productiva_id: Long?)
    fun updateLote(mLote: Lote, unidad_productiva_id: Long?)
    fun deleteLote(mLote: Lote, unidad_productiva_id: Long?)
    //ListUp
    fun loadListas()
    fun getLastUserLogued(): Usuario?
    fun getLastLote(): Lote?
}