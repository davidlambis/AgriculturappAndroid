package com.interedes.agriculturappv3.events

/**
 * Created by usuario on 21/03/2018.
 */

data class RequestEvent (var eventType: Int,
                             var mutableList: MutableList<Object>? = null,
                             var objectMutable: Object? = null,
                             var mensajeError: String?
) {
    companion object {
        val READ_EVENT: Int = 0
        val SAVE_EVENT: Int = 1
        val UPDATE_EVENT: Int = 2
        val DELETE_EVENT: Int = 3
        val ERROR_EVENT: Int = 4

        //Events On item Click
        val ITEM_EVENT: Int = 5
        val ITEM_READ_EVENT: Int = 6
        val ITEM_EDIT_EVENT: Int = 7
        val ITEM_DELETE_EVENT: Int = 8

        //Event Personality
        val LIST_EVENT_UP: Int = 9
        val LIST_EVENT_UNIDAD_MEDIDA: Int = 10
    }
}
