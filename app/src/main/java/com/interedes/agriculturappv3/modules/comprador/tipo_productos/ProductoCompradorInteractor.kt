package com.interedes.agriculturappv3.modules.comprador.tipo_productos

class ProductoCompradorInteractor:IMainViewProductoComprador.Interactor  {


    var repository: IMainViewProductoComprador.Repository? = null

    init {
        repository = ProductosCompradorRepository()
    }


    override fun execute(checkConection:Boolean) {
      repository?.getListTipoProductos(checkConection)
    }


}