package com.interedes.agriculturappv3.modules.productor.comercial_module.clientes.events


data class ClientesEvent(var eventType: Int,
                         var mutableList: MutableList<Object>? = null,
                         var objectMutable: Object? = null,
                         var mensajeError: String?
) {

    companion object {
        val READ_EVENT : Int = 1
    }

}