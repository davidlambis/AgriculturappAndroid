package com.interedes.agriculturappv3.asistencia_tecnica.modules.asistencia_tecnica_module.cultivos

import com.interedes.agriculturappv3.asistencia_tecnica.models.Cultivo


class CultivoInteractor : ICultivo.Interactor {

    var repository: ICultivo.Repository? = null

    init {
        repository = CultivoRepository()
    }

    override fun registerCultivo(cultivo: Cultivo?) {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun updateCultivo(cultivo: Cultivo?) {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun deleteCultivo(cultivo: Cultivo?) {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun execute() {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun getListas() {
        repository?.getListas()
    }


}