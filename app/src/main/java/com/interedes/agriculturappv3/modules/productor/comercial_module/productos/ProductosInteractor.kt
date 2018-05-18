package com.interedes.agriculturappv3.modules.productor.comercial_module.productos

import com.interedes.agriculturappv3.modules.models.producto.Producto


class ProductosInteractor : IProductos.Interactor {
    var repository: IProductos.Repository? = null

    init {
        this.repository = ProductosRepository()
    }

    override fun registerProducto(producto: Producto, cultivo_id: Long,checkConection:Boolean) {
        repository?.registerProducto(producto, cultivo_id,checkConection)
    }


    override fun updateProducto(mProducto: Producto, cultivo_id: Long,checkConection:Boolean) {
        repository?.updateProducto(mProducto, cultivo_id,checkConection)
    }

    override fun deleteProducto(producto: Producto, cultivo_id: Long?,checkConection:Boolean) {
        repository?.deleteProducto(producto, cultivo_id,checkConection)
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