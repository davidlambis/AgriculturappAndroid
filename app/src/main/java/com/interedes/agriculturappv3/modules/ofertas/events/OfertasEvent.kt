package com.interedes.agriculturappv3.modules.ofertas.events

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

        val REQUEST_REFUSED_ITEM_EVENT: Int = 7
        val REQUEST_CONFIRM_ITEM_EVENT: Int = 8
        val REQUEST_CHAT_ITEM_EVENT: Int = 9

        val UPDATE_EVENT: Int = 10
        //Error Connection
        val ERROR_VERIFICATE_CONECTION: Int = 11
        val ERROR_EVENT: Int = 12
    }

}