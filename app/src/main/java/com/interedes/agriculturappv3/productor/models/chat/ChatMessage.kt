package com.interedes.agriculturappv3.productor.models.chat

data class ChatMessage(var message: String? = null,
                       var senderId: String? = null,
                       var receiverId: String? = null) {
}