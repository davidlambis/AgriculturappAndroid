package com.interedes.agriculturappv3.productor.modules.comercial_module.productos.events

data class ProductosEvent(var eventType: Int,
                          var mutableList: MutableList<Object>? = null,
                          var objectMutable: Object? = null,
                          var mensajeError: String?
) {
    companion object {
        //Event Personality
        val LIST_EVENT_UP: Int = 1
        val LIST_EVENT_UNIDAD_MEDIDA: Int = 2
        val LIST_EVENT_LOTE: Int = 3
        val LIST_EVENT_CULTIVO: Int = 4
        val LIST_EVENT_CALIDADES: Int = 5

        //
        val READ_EVENT: Int = 6
        val GET_EVENT_CULTIVO: Int = 7
        val SAVE_EVENT: Int = 8
        val DELETE_EVENT : Int = 9
        val ITEM_EDIT_EVENT: Int = 10
        val ITEM_DELETE_EVENT: Int = 11

        val ERROR_EVENT : Int = 12
    }

}
