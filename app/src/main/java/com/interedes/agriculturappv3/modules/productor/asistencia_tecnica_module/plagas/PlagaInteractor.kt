package com.interedes.agriculturappv3.modules.productor.asistencia_tecnica_module.plagas


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

    override fun checkListPlagas(): Long {
      return repository?.checkListPlagas()!!
    }

    //endregion
}