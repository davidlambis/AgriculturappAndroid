package com.interedes.agriculturappv3.modules.productor.comercial_module.ofe

import com.interedes.agriculturappv3.modules.models.ofertas.Oferta
import com.interedes.agriculturappv3.modules.models.usuario.Usuario
import com.interedes.agriculturappv3.modules.ofertas.IOfertas
import com.interedes.agriculturappv3.modules.ofertas.OfertasRepository

class OfertasInteractor : IOfertas.Interactor {

    var repository: IOfertas.Repository? = null

    init {
        this.repository = OfertasRepository()
    }

    //region Métodos Interfaz
    override fun getListOfertas(productoId: Long?,checkConection: Boolean) {
        repository?.getListOfertas(productoId,checkConection)
    }

    override fun getListas() {
        repository?.getListas()
    }

    override fun getProducto(productoId: Long?) {
        repository?.getProducto(productoId)
    }

    override fun updateOferta(oferta: Oferta, productoId: Long?, checkConection: Boolean) {
        repository?.updateOferta(oferta,productoId,checkConection)
    }


    override  fun getUserLogued(): Usuario? {
        return repository?.getUserLogued()
    }
    //endregion
}