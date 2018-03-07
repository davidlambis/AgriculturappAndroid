package com.interedes.agriculturappv3.asistencia_tecnica.modules.asistencia_tecnica_module.Lote.interactor

import com.interedes.agriculturappv3.asistencia_tecnica.models.Lote
import com.interedes.agriculturappv3.asistencia_tecnica.modules.asistencia_tecnica_module.Lote.repository.LoteRepository
import com.interedes.agriculturappv3.asistencia_tecnica.modules.asistencia_tecnica_module.Lote.repository.LoteRepositoryImpl

/**
 * Created by EnuarMunoz on 7/03/18.
 */
class LoteInteractorImpl:LoteInteractor {

    var loteRepository: LoteRepository? = null

    init {
        loteRepository = LoteRepositoryImpl()
    }

    override fun registerLote(lote: Lote) {
        loteRepository?.registerLote(lote)
    }

}