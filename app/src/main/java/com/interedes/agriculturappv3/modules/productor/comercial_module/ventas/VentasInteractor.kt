package com.interedes.agriculturappv3.modules.productor.comercial_module.ventas


class VentasInteractor : IVentas.Interactor{

    var repository: IVentas.Repository? = null

    init {
        this.repository = VentasRepository()
    }

    //region Métodos Interfaz
    override fun getListVentas(productoId: Long?) {
        repository?.getListVentas(productoId)
    }

    override fun getListas() {
        repository?.getListas()
    }

    override fun getProducto(productoId: Long?) {
        repository?.getProducto(productoId)
    }

    //endregion
}