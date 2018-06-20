package com.interedes.agriculturappv3.modules.productor.asistencia_tecnica_module.Lote

import com.interedes.agriculturappv3.modules.models.lote.Lote

/**
 * Created by EnuarMunoz on 7/03/18.
 */
class LoteInteractorImpl : MainViewLote.Interactor {


    var loteRepository: MainViewLote.Repository? = null

    init {
        loteRepository = LoteRepositoryImpl()
    }

    override fun loadListas() {
        loteRepository?.loadListas()
    }

    override fun execute(unidad_productiva_id: Long?) {
        loteRepository?.getListLotes(unidad_productiva_id)
    }

    override fun registerLote(lote: Lote, unidad_productiva_id: Long?,checkConection:Boolean) {
        loteRepository?.saveLotes(lote, unidad_productiva_id,checkConection)
    }

    override fun updateLote(lote: Lote, unidad_productiva_id: Long?,checkConection:Boolean) {
        loteRepository?.updateLote(lote, unidad_productiva_id,checkConection)
    }

    override fun deleteLote(lote: Lote, unidad_productiva_id: Long?,checkConection:Boolean) {
        loteRepository?.deleteLote(lote, unidad_productiva_id,checkConection)
    }

    override fun verificateAreaLoteBiggerUp(unidad_productiva_id:Long?,area:Double):Boolean {
      return loteRepository?.verificateAreaLoteBiggerUp(unidad_productiva_id,area)!!
    }
}