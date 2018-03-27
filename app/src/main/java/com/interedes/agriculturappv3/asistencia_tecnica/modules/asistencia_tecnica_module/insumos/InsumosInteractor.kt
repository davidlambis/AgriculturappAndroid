package com.interedes.agriculturappv3.asistencia_tecnica.modules.asistencia_tecnica_module.insumos


class InsumosInteractor : InterfaceInsumos.Interactor {

    var repository: InterfaceInsumos.Repository? = null

    init {
        repository = InsumosRepository()
    }

    //region Métodos Interfaz
    override fun getInsumosByPlaga(tipoEnfermedadId: Long?) {
        repository?.getInsumosByPlaga(tipoEnfermedadId)
    }

    /*
    override fun setInsumo(insumoId: Long?) {
        repository?.setInsumo(insumoId)
    }*/
    //endregion


}