package com.interedes.agriculturappv3.productor.modules.comercial_module.ofe

import com.interedes.agriculturappv3.productor.modules.comercial_module.ofertas.IOfertas
import com.interedes.agriculturappv3.productor.modules.comercial_module.ofertas.OfertasRepository

class OfertasInteractor : IOfertas.Interactor {

    var repository: IOfertas.Repository? = null

    init {
        this.repository = OfertasRepository()
    }

    //region Métodos Interfaz
    override fun getListOfertas(productoId: Long?) {
        repository?.getListOfertas(productoId)
    }

    override fun getListas() {
        repository?.getListas()
    }

    override fun getProducto(productoId: Long?) {
        repository?.getProducto(productoId)
    }

    //endregion
}