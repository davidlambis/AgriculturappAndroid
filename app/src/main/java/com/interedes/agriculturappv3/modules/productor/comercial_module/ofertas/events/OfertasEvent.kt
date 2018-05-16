package com.interedes.agriculturappv3.modules.productor.comercial_module.ofertas.events

data class OfertasEvent(var eventType: Int,
                        var mutableList: MutableList<Object>? = null,
                        var objectMutable: Object? = null,
                        var mensajeError: String?
) {

    companion object {
        val LIST_EVENT_UP: Int = 1
        val LIST_EVENT_LOTE: Int = 2
        val LIST_EVENT_CULTIVO: Int = 3
        val LIST_EVENT_PRODUCTO: Int = 4
        val READ_EVENT: Int = 5
        val GET_EVENT_PRODUCTO: Int = 6
    }

}