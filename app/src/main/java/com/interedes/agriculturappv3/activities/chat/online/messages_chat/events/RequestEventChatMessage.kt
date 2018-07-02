package com.interedes.agriculturappv3.activities.chat.online.messages_chat.events




data class RequestEventChatMessage (var eventType: Int,
                                   var mutableList: MutableList<Object>? = null,
                                   var objectMutable: Object? = null,
                                   var mensajeError: String?
) {
    companion object {
        val SEND_MESSAGE_EVENT_OK: Int = 1

        val UPDATE_EVENT: Int = 2
        val DELETE_EVENT: Int = 3
        val ERROR_EVENT: Int = 4

        val LIST_MESSAGES_EVENT: Int = 5
        val NEW_MESSAGES_EVENT: Int = 6

        val ERROR_VERIFICATE_CONECTION: Int = 7

    }
}