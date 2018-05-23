package com.interedes.agriculturappv3.modules.comprador.productores

class ProductorInteractor:IMainViewProductor.Interactor {


    var repository: IMainViewProductor.Repository? = null

    init {
        repository = ProductorRepository()
    }

    override fun execute(checkConection: Boolean,tipoProducto:Long) {
       repository?.getListTipoProductos(checkConection,tipoProducto)
    }
}