package com.interedes.agriculturappv3.productor.modules.asistencia_tecnica_module.UnidadProductiva.events

/**
 * Created by EnuarMunoz on 16/03/18.
 */


data class RequestEventUP (var eventType: Int, var mutableList: MutableList<Object>? = null, var objectMutable: Object? = null, var mensajeError: String?) {

    companion object {
        val READ_EVENT: Int = 0
        val SAVE_EVENT: Int = 1
        val UPDATE_EVENT: Int = 2
        val DELETE_EVENT: Int = 3
        val ERROR_EVENT: Int = 4


        //EVENTS PERSONLITY


        //EVENTS PERSONLITY
        val LIST_EVENT_UNIDAD_MEDIDA: Int = 5

        //Events On item Click
        val ITEM_EVENT: Int = 6
        val ITEM_READ_EVENT: Int = 7
        val ITEM_EDIT_EVENT: Int = 8
        val ITEM_DELETE_EVENT: Int = 9

        //Buttons
        val ADD_POLIGON_EVENT: Int = 10
        val ADD_LOCATION_EVENT: Int = 11
    }
}
