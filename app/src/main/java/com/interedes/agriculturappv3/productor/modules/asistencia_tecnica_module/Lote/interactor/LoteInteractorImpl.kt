package com.interedes.agriculturappv3.productor.modules.asistencia_tecnica_module.Lote.interactor

import com.interedes.agriculturappv3.productor.models.Lote
import com.interedes.agriculturappv3.productor.modules.asistencia_tecnica_module.Lote.repository.LoteRepository
import com.interedes.agriculturappv3.productor.modules.asistencia_tecnica_module.Lote.repository.LoteRepositoryImpl

/**
 * Created by EnuarMunoz on 7/03/18.
 */
class LoteInteractorImpl:LoteInteractor {


    var loteRepository: LoteRepository? = null

    init {
        loteRepository = LoteRepositoryImpl()
    }

    override fun loadListas() {
        loteRepository?.loadListas()
    }

    override fun execute(unidad_productiva_id:Long?) {
        loteRepository?.getListLotes(unidad_productiva_id)
    }

    override fun registerLote(lote: Lote,unidad_productiva_id:Long?) {
        loteRepository?.saveLotes(lote,unidad_productiva_id)
    }
    override fun updateLote(lote: Lote,unidad_productiva_id:Long?) {
        loteRepository?.updateLote(lote,unidad_productiva_id)
    }

    override fun deleteLote(lote: Lote,unidad_productiva_id:Long?) {
        loteRepository?.deleteLote(lote,unidad_productiva_id)
    }

}