package com.interedes.agriculturappv3.modules.productor.asistencia_tecnica_module.control_plagas

import com.interedes.agriculturappv3.modules.models.control_plaga.ControlPlaga


class ControlPlagasInteractor : IControlPlagas.Interactor {

    var repository: IControlPlagas.Repository? = null

    init {
        repository = ControlPlagasRepository()
    }

    //region Métodos Interfaz
    override fun getListas() {
        repository?.getListas()
    }

    override fun getListControlPlaga(cultivo_id: Long?) {
        repository?.getListControlPlaga(cultivo_id)
    }

    override fun getCultivo(cultivo_id: Long?) {
        repository?.getCultivo(cultivo_id)
    }

    override fun deleteControlPlaga(controlPlaga: ControlPlaga, cultivo_id: Long?,checkConection:Boolean) {
        repository?.deleteControlPlaga(controlPlaga, cultivo_id,checkConection)
    }

    override fun updateControlPlaga(controlPlaga: ControlPlaga?,checkConection:Boolean) {
        repository?.updateControlPlaga(controlPlaga,checkConection)
    }



    //endregion

}