package com.interedes.agriculturappv3.modules.comprador.productos

class ProductoCompradorInteractor:IMainViewProductoComprador.Interactor  {


    var repository: IMainViewProductoComprador.Repository? = null

    init {
        repository = ProductosCompradorRepository()
    }


    override fun execute() {
      repository?.getListTipoProductos()
    }


}