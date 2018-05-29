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






    }
}