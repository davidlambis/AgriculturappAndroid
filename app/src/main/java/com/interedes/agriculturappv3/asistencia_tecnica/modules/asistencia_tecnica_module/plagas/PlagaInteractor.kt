package com.interedes.agriculturappv3.asistencia_tecnica.modules.asistencia_tecnica_module.plagas

import com.interedes.agriculturappv3.asistencia_tecnica.models.TipoProducto


class PlagaInteractor : IPlaga.Interactor {

    var repository: IPlaga.Repository? = null

    init {
        repository = PlagaRepository()
    }

    //region Métodos Interfaz
    override fun getPlagasByTipoProducto(tipoProductoId: Long?) {
        repository?.getPlagasByTipoProducto(tipoProductoId)
    }

    override fun setPlaga(tipoEnfermedadId: Long?) {
        repository?.setPlaga(tipoEnfermedadId)
    }

    override fun getTiposProducto() {
        repository?.getTiposProducto()
    }
    //endregion
}