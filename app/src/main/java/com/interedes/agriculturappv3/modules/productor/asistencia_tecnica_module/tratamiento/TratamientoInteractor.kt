package com.interedes.agriculturappv3.modules.productor.asistencia_tecnica_module.tratamiento

import com.interedes.agriculturappv3.modules.models.control_plaga.ControlPlaga
import com.interedes.agriculturappv3.modules.models.tratamiento.Tratamiento


class TratamientoInteractor : ITratamiento.Interactor {

    var repository: ITratamiento.Repository? = null

    init {
        repository = TratamientoRepository()
    }

    //region Métodos Interfaz
    override fun getTratamiento(insumoId: Long?) {
        repository?.getTratamiento(insumoId)
    }

    override fun getListas() {
        repository?.getListas()
    }

    override fun registerControlPlaga(controlPlaga: ControlPlaga, cultivo_id: Long?,checkConection:Boolean) {
        repository?.registerControlPlaga(controlPlaga, cultivo_id,checkConection)
    }



    override fun sendCalificationTratamietno(tratamiento: Tratamiento?, calificacion: Double?) {
        repository?.sendCalificationTratamietno(tratamiento,calificacion)
    }

    //endregion
}