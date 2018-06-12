package com.interedes.agriculturappv3.modules.comprador.detail_producto

import com.interedes.agriculturappv3.modules.models.ofertas.Oferta
import com.interedes.agriculturappv3.modules.models.producto.Producto
import com.interedes.agriculturappv3.modules.models.tipoproducto.TipoProducto
import com.interedes.agriculturappv3.modules.models.usuario.Usuario

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

    override fun getListas() {
        repository?.getListas()
    }


    override fun postOferta(oferta: Oferta, checkConection:Boolean){
        repository?.postOferta(oferta,checkConection)
    }


    override fun getLastUserLogued(): Usuario?{
       return  repository?.getLastUserLogued()
    }
}