package com.interedes.agriculturappv3.events

/**
 * Created by EnuarMunoz on 9/03/18.
 */

data class ListenerAdapterOnClickEvent(var eventType:Int, var objectMutable: Object? = null) {

    companion object {
        val ITEM_EVENT: Int = 0
        val READ_EVENT: Int = 1
        val EDIT_EVENT: Int = 2
        val DELETE_EVENT: Int = 3

        //Unidad Productiva
        val ADD_POLIGON_EVENT: Int = 4
        val ADD_LOCATION_EVENT: Int = 5

    }
}
