package com.interedes.agriculturappv3.productor.modules.asistencia_tecnica_module.tratamiento

import com.interedes.agriculturappv3.productor.models.control_plaga.ControlPlaga
import com.interedes.agriculturappv3.productor.models.tratamiento.Tratamiento


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

    override fun registerControlPlaga(controlPlaga: ControlPlaga, cultivo_id: Long?) {
        repository?.registerControlPlaga(controlPlaga, cultivo_id)
    }

    override fun sendCalificationTratamietno(tratamiento: Tratamiento?, calificacion: Double?) {
        repository?.sendCalificationTratamietno(tratamiento,calificacion)
    }

    //endregion
}