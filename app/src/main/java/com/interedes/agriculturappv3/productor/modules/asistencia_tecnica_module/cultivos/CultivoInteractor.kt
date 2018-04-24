package com.interedes.agriculturappv3.productor.modules.asistencia_tecnica_module.cultivos

import com.interedes.agriculturappv3.productor.models.cultivo.Cultivo


class CultivoInteractor : ICultivo.Interactor {


    var repository: ICultivo.Repository? = null

    init {
        repository = CultivoRepository()
    }

    override fun registerCultivo(cultivo: Cultivo?, loteId: Long?) {
        repository?.saveCultivo(cultivo!!,loteId)
    }

    override fun registerOnlineCultivo(cultivo: Cultivo?, loteId: Long?) {
        repository?.registerOnlineCultivo(cultivo, loteId)
    }

    override fun updateCultivo(cultivo: Cultivo?, loteId: Long?) {
        repository?.updateCultivo(cultivo!!,loteId)
    }

    override fun deleteCultivo(cultivo: Cultivo?, loteId: Long?) {
        repository?.deleteCultivo(cultivo!!,loteId)
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