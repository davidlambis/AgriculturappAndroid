package com.interedes.agriculturappv3.productor.modules.comercial_module.productos

import android.app.Activity
import android.app.Fragment
import com.interedes.agriculturappv3.productor.models.producto.Producto


class ProductosInteractor : IProductos.Interactor {
    var repository: IProductos.Repository? = null

    init {
        this.repository = ProductosRepository()
    }

    override fun registerProducto(producto: Producto, cultivo_id: Long) {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun updateProducto(producto: Producto, cultivo_id: Long) {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun deleteProducto(producto: Producto, cultivo_id: Long?) {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun getListProductos(cultivo_id: Long?) {
        repository?.getListProducto(cultivo_id)
    }

    override fun getListas() {
        repository?.getListas()
    }

    override fun getCultivo(cultivo_id: Long?) {
        repository?.getCultivo(cultivo_id)
    }

}