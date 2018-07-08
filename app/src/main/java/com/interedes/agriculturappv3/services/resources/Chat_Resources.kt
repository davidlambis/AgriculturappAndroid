package com.interedes.agriculturappv3.services.resources

import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.storage.FirebaseStorage

class Chat_Resources {

    companion object {

        val mUserDBRef = FirebaseDatabase.getInstance().reference.child("Users")
        val mMessagesDBRef = FirebaseDatabase.getInstance().reference.child("Messages")
        val mRoomDBRef = FirebaseDatabase.getInstance().reference.child("Room")
        val mStorageRef = FirebaseStorage.getInstance().reference.child("Photos").child("Users")

        fun getRoomByCompradorProductor(SenderId:String?, ReceiverId:String?):String?{
            return String.format("%s_%s",SenderId,ReceiverId);
        }


        fun getRoomById(SenderId:String?):String?{
            return String.format("%s",SenderId);
        }
    }
}