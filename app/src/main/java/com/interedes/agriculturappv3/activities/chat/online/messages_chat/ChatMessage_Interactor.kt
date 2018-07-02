package com.interedes.agriculturappv3.activities.chat.online.messages_chat

import com.interedes.agriculturappv3.modules.models.chat.ChatMessage
import com.interedes.agriculturappv3.modules.models.chat.Room

class ChatMessage_Interactor :IMainViewChatMessages.Interactor{

    var repository: IMainViewChatMessages.Repository? = null

    init {
        repository = ChatMessage_Repository()
    }

    override fun getListMessagesByRoom(checkConection: Boolean, room: Room, mReceiverId: String) {
        repository?.getListMessagesByRoom(checkConection,room,mReceiverId)
    }

    override fun sendMessage(message: ChatMessage, room: Room, checkConection: Boolean) {
       repository?.sendMessage(message,room,checkConection)
    }

}