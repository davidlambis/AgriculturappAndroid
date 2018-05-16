package com.interedes.agriculturappv3.modules.productor.comercial_module.productos

import com.interedes.agriculturappv3.modules.models.producto.Producto


class ProductosInteractor : IProductos.Interactor {
    var repository: IProductos.Repository? = null

    init {
        this.repository = ProductosRepository()
    }

    override fun registerProducto(producto: Producto, cultivo_id: Long) {
        repository?.registerProducto(producto, cultivo_id)
    }

    override fun registerOnlineProducto(producto: Producto, cultivo_id: Long) {
        repository?.registerOnlineProducto(producto, cultivo_id)
    }

    override fun updateProducto(mProducto: Producto, cultivo_id: Long) {
        repository?.updateProducto(mProducto, cultivo_id)
    }

    override fun deleteProducto(producto: Producto, cultivo_id: Long?) {
        repository?.deleteProducto(producto, cultivo_id)
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