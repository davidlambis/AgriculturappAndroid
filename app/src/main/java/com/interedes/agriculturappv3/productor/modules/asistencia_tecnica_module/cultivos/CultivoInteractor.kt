package com.interedes.agriculturappv3.productor.modules.asistencia_tecnica_module.cultivos

import com.interedes.agriculturappv3.productor.models.Cultivo


class CultivoInteractor : ICultivo.Interactor {


    var repository: ICultivo.Repository? = null

    init {
        repository = CultivoRepository()
    }

    override fun registerCultivo(cultivo: Cultivo?) {
        repository?.saveCultivo(cultivo!!)
    }

    override fun updateCultivo(cultivo: Cultivo?) {
        repository?.updateCultivo(cultivo!!)
    }

    override fun deleteCultivo(cultivo: Cultivo?) {
        repository?.deleteCultivo(cultivo!!)
    }

    override fun getListas() {
        repository?.getListas()
    }

    override fun execute(cultivo_id:Long?) {
        repository?.getListCultivos(cultivo_id)
    }

}