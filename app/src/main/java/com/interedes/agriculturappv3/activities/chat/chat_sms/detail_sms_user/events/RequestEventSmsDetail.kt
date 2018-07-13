package com.interedes.agriculturappv3.activities.chat.chat_sms.detail_sms_user.events




data class RequestEventSmsDetail (var eventType: Int,
                                var mutableList: MutableList<Object>? = null,
                                var objectMutable: Object? = null,
                                var mensajeError: String?
) {
    companion object {
        val SAVE_EVENT: Int = 1

        val UPDATE_EVENT: Int = 2
        val DELETE_EVENT: Int = 3
        val ERROR_EVENT: Int = 4

        val LIST_SMS_EVENT: Int = 5

        val ITEM_EVENTS_DETAIL_SMS: Int = 6

        val NEW_MESSAGE_EVENT: Int = 7

    }
}