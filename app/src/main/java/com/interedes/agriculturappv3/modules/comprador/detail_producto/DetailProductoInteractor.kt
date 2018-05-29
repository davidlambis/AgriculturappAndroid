package com.interedes.agriculturappv3.modules.comprador.detail_producto

import com.interedes.agriculturappv3.modules.models.producto.Producto
import com.interedes.agriculturappv3.modules.models.tipoproducto.TipoProducto

class DetailProductoInteractor:IMainViewDetailProducto.Interactor {



    var repository: IMainViewDetailProducto.Repository? = null

    init {
        repository = DetailProductoRepository()
    }

    override fun getProducto(producto_id: Long): Producto? {
       return repository?.getProducto(producto_id)
    }

    override fun getTipoProducto(tipo_producto_id: Long): TipoProducto? {
        return  repository?.getTipoProducto(tipo_producto_id)
    }

    override fun verificateCantProducto(producto_id: Long?, catnidad: Double?): Boolean? {
       return repository?.verificateCantProducto(producto_id,catnidad)
    }

}