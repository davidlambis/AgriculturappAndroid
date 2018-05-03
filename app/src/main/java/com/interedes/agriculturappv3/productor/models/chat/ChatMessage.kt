package com.interedes.agriculturappv3.productor.models.chat

data class ChatMessage(var message: String? = null,
                       var senderId: String? = null,
                       var receiverId: String? = null,
                       var date: String? = null,
                       var hour: String? = null,
                       var timestamp:Long=0) {
}