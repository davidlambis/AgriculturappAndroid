package com.interedes.agriculturappv3.modules.comprador.productores

import com.interedes.agriculturappv3.modules.models.tipoproducto.TipoProducto

class ProductorInteractor:IMainViewProductor.Interactor {



    var repository: IMainViewProductor.Repository? = null

    init {
        repository = ProductorRepository()
    }

    override fun execute(checkConection: Boolean,tipoProducto:Long,top:Int,skip:Int) {
       repository?.getListTipoProductos(checkConection,tipoProducto,top,skip)
    }

    override fun getTipoProducto(tipoProducto: Long): TipoProducto? {
        return repository?.getTipoProducto(tipoProducto)
    }
}