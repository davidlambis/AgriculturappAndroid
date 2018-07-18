package com.interedes.agriculturappv3.modules.comprador.productores

import com.interedes.agriculturappv3.modules.comprador.productores.resources.RequestFilter
import com.interedes.agriculturappv3.modules.models.tipoproducto.TipoProducto

class ProductorInteractor:IMainViewProductor.Interactor {



    var repository: IMainViewProductor.Repository? = null

    init {
        repository = ProductorRepository()
    }

    override fun  execute(filter: RequestFilter) {
       repository?.getListTipoProductos(filter)
    }

    override fun getTipoProducto(tipoProducto: Long): TipoProducto? {
        return repository?.getTipoProducto(tipoProducto)
    }

    override fun getListDepartmentCities() {
        repository?.getListDepartmentCities()
    }
}