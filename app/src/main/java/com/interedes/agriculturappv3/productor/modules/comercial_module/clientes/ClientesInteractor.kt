package com.interedes.agriculturappv3.productor.modules.comercial_module.clientes



class ClientesInteractor : IClientes.Interactor {

    var repository: IClientes.Repository? = null

    init {
        this.repository = ClientesRepository()
    }

    //region Métodos Interfaz
    override fun getListClientes() {
        repository?.getListClientes()
    }
    //endregion
}