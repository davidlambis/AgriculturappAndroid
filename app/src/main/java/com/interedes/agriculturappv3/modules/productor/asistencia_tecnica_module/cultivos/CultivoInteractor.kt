package com.interedes.agriculturappv3.modules.productor.asistencia_tecnica_module.cultivos

import com.interedes.agriculturappv3.modules.models.cultivo.Cultivo


class CultivoInteractor : ICultivo.Interactor {


    var repository: ICultivo.Repository? = null

    init {
        repository = CultivoRepository()
    }

    override fun registerCultivo(cultivo: Cultivo, loteId: Long?,checkConection:Boolean) {
        repository?.saveCultivo(cultivo,loteId,checkConection)
    }

    override fun updateCultivo(cultivo: Cultivo, loteId: Long?,checkConection:Boolean) {
        repository?.updateCultivo(cultivo,loteId,checkConection)
    }

    override fun deleteCultivo(cultivo: Cultivo, loteId: Long?,checkConection:Boolean) {
        repository?.deleteCultivo(cultivo,loteId,checkConection)
    }

    override fun getListas() {
        repository?.getListas()
    }

    override fun execute(cultivo_id: Long?) {
        repository?.getListCultivos(cultivo_id)
    }

    override fun getLote(loteId: Long?) {
        repository?.getLote(loteId)
    }

}