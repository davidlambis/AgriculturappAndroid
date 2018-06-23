package com.interedes.agriculturappv3.modules.comprador.detail_producto.events



data class RequestEventDetalleProducto (var eventType: Int,
                                  var mutableList: MutableList<Object>? = null,
                                  var objectMutable: Object? = null,
                                  var mensajeError: String?
) {
    companion object {
        val PRODUCTO_EVENT: Int = 0
        val SAVE_EVENT: Int = 1
        val UPDATE_EVENT: Int = 2
        val DELETE_EVENT: Int = 3
        val ERROR_EVENT: Int = 4
        val LIST_EVENT_UNIDAD_MEDIDA_PRICE: Int = 5

        val OK_SEND_EVENT_OFERTA: Int = 6
        val OK_SEND_EVENT_SMS: Int = 7
        val OK_SEND_EVENT_SMS_ONLINE: Int = 8

        //Error Connection
        val ERROR_VERIFICATE_CONECTION: Int = 9




    }
}