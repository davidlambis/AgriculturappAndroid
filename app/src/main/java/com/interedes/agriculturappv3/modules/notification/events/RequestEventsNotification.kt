package com.interedes.agriculturappv3.modules.notification.events



data class RequestEventsNotification (var eventType: Int,
                                var mutableList: MutableList<Object>? = null,
                                var objectMutable: Object? = null,
                                var mensajeError: String?
) {
    companion object {
        val SAVE_EVENT: Int = 1

        val UPDATE_EVENT: Int = 2
        val DELETE_EVENT: Int = 3
        val ERROR_EVENT: Int = 4


        //Events On item Click

        val ITEM_EVENT: Int = 5
        val ITEM_READ_EVENT: Int = 6
        val ITEM_EDIT_EVENT: Int = 7
        val ITEM_DELETE_EVENT: Int = 8

        val ITEM_NEW_EVENT: Int = 10

        //Error Connection
        val ERROR_VERIFICATE_CONECTION: Int = 9

        val LIST_EVENT_NOTIFICATION: Int = 11


        val RELOAD_LIST_NOTIFICATION: Int = 12

    }
}