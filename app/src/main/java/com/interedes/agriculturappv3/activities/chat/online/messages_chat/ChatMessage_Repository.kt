package com.interedes.agriculturappv3.activities.chat.online.messages_chat

import android.widget.Toast
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*
import com.interedes.agriculturappv3.activities.chat.online.messages_chat.events.RequestEventChatMessage
import com.interedes.agriculturappv3.libs.EventBus
import com.interedes.agriculturappv3.libs.GreenRobotEventBus
import com.interedes.agriculturappv3.modules.models.chat.ChatMessage
import com.interedes.agriculturappv3.modules.models.chat.Room
import com.interedes.agriculturappv3.modules.models.chat.RoomConversation
import com.interedes.agriculturappv3.services.api.ApiInterface
import com.interedes.agriculturappv3.services.resources.Chat_Resources
import kotlinx.android.synthetic.main.activity_chat_message.*
import java.util.ArrayList
import com.interedes.agriculturappv3.R.string.send
import com.interedes.agriculturappv3.modules.models.Notification.FcmNotificationBuilder
import com.interedes.agriculturappv3.modules.models.chat.UserFirebase
import com.interedes.agriculturappv3.modules.models.usuario.Usuario
import com.interedes.agriculturappv3.modules.models.usuario.Usuario_Table
import com.interedes.agriculturappv3.services.resources.NotificationTypeResources
import com.interedes.agriculturappv3.services.resources.S3Resources
import com.raizlabs.android.dbflow.sql.language.SQLite


class ChatMessage_Repository:IMainViewChatMessages.Repository {



    var eventBus: EventBus? = null
    var apiService: ApiInterface? = null
    //FIREBASE
    private var mUsersDBRef: DatabaseReference? = null
    private var mAuth: FirebaseAuth? = null
    private var mRoomDBRef: DatabaseReference? = null
    private var mMessagesDBRef: DatabaseReference? = null


    init {
        eventBus = GreenRobotEventBus()
        apiService = ApiInterface.create()

        mAuth = FirebaseAuth.getInstance()
        mUsersDBRef = Chat_Resources.mUserDBRef
        mRoomDBRef = Chat_Resources.mRoomDBRef
        mMessagesDBRef =  Chat_Resources.mMessagesDBRef
    }

    override fun getListMessagesByRoom(checkConection:Boolean,room:Room,mReceiverId:String) {
        //if(checkConection){
            val query = mMessagesDBRef?.orderByChild("room_id")?.equalTo("${room?.IdRoom}")
            query?.addChildEventListener(object : ChildEventListener {
                override fun onCancelled(p0: DatabaseError?) {

                }
                override fun onChildMoved(p0: DataSnapshot?, p1: String?) {
                }
                override fun onChildChanged(p0: DataSnapshot?, p1: String?) {
                }
                override fun onChildAdded(dataSnapshot: DataSnapshot?, p1: String?) {
                    if(dataSnapshot!=null){
                        val chatMessage = dataSnapshot.getValue(ChatMessage::class.java)
                        /*if (chatMessage!!.senderId.equals(FirebaseAuth.getInstance().currentUser!!.uid) && chatMessage.receiverId.equals(mReceiverId) ||
                                chatMessage.senderId.equals(mReceiverId) && chatMessage.receiverId.equals(FirebaseAuth.getInstance().currentUser!!.uid)) {
                            mMessagesList.add(chatMessage)
                        }*/
                        postEventOk(RequestEventChatMessage.NEW_MESSAGES_EVENT,null,chatMessage)
                    }
                }
                override fun onChildRemoved(p0: DataSnapshot?) {
                }
            })
        //}
    }

    override fun sendMessage(message: ChatMessage,room:Room,userSelected:UserFirebase,checkConection: Boolean) {
       if(checkConection){
           val roomDateComprador= Chat_Resources.mRoomDBRef?.child(room?.User_From)?.child(Chat_Resources.getRoomById(room?.User_To)+"/lastMessage")
           roomDateComprador?.setValue(message.message);

           val roomDateProductor= Chat_Resources.mRoomDBRef?.child(room?.User_To)?.child(Chat_Resources.getRoomById(room?.User_From)+"/lastMessage")
           roomDateProductor?.setValue(message.message);

           val roomDateCompradorDate= Chat_Resources.mRoomDBRef?.child(room?.User_From)?.child(Chat_Resources.getRoomById(room?.User_To)+"/date_Last")
           roomDateCompradorDate?.setValue(ServerValue.TIMESTAMP);

           val roomDateProductorDate= Chat_Resources.mRoomDBRef?.child(room?.User_To)?.child(Chat_Resources.getRoomById(room?.User_From)+"/date_Last")
           roomDateProductorDate?.setValue(ServerValue.TIMESTAMP);

           mMessagesDBRef?.push()?.setValue(message)?.addOnCompleteListener { task ->
               if (!task.isSuccessful) {
                   //error
                   postEventError(RequestEventChatMessage.ERROR_EVENT,task.exception.toString())
                   //mLayoutManager?.scrollToPositionWithOffset(0, 0);
               } else {
                   sendPushNotificationToReceiver(message,room,userSelected)
                   postEventOk(RequestEventChatMessage.SEND_MESSAGE_EVENT_OK,null,null)
               }
           }
       }else{
           postEventOk(RequestEventChatMessage.ERROR_VERIFICATE_CONECTION,null,message)
       }
    }

     fun getLastUserLogued(): Usuario? {

        val usuarioLogued = SQLite.select().from(Usuario::class.java).where(Usuario_Table.UsuarioRemembered.eq(true)).querySingle()
        return usuarioLogued
    }

    private fun sendPushNotificationToReceiver(message: ChatMessage,room:Room,userSelected:UserFirebase) {
        val userLogued= getLastUserLogued()
        var uiUserFirebase:String?=null
        uiUserFirebase = FirebaseAuth.getInstance().currentUser?.uid
        if(uiUserFirebase==null){
            uiUserFirebase=getLastUserLogued()?.IdFirebase
        }

        val fcmNotificationBuilder=FcmNotificationBuilder()
        fcmNotificationBuilder.title=userLogued?.Nombre+" ${userLogued?.Apellidos}"
        fcmNotificationBuilder.image_url= S3Resources.RootImage+"${userLogued?.Fotopefil}"
        fcmNotificationBuilder.message=message.message
        fcmNotificationBuilder.user_name=userSelected.Nombre+" ${userLogued?.Apellidos}"
        fcmNotificationBuilder.ui=uiUserFirebase
        fcmNotificationBuilder.receiver_firebase_token=userSelected.TokenFcm
        fcmNotificationBuilder.room_id=room.IdRoom
        fcmNotificationBuilder.type_notification=NotificationTypeResources.NOTIFICATION_TYPE_MESSAGE_ONLINE
        fcmNotificationBuilder.send()
    }


    //region Events
    private fun postEventOk(type: Int, list: List<ChatMessage>?, message: ChatMessage?) {
        var listMutable:MutableList<Object>? = null
        var messageMutable: Object? = null
        if (message != null) {
            messageMutable = message as Object
        }
        if (list != null) {
            listMutable=list as MutableList<Object>
        }

        postEvent(type, listMutable, messageMutable, null)
    }

    private fun postEventError(type: Int,messageError:String?) {
        postEvent(type, null,null,messageError)
    }

    //Main Post Event
    private fun postEvent(type: Int, listModel1:MutableList<Object>?,model:Object?,errorMessage: String?) {
        val event = RequestEventChatMessage(type, listModel1, model, errorMessage)
        event.eventType = type
        eventBus?.post(event)
    }
    //endregion

}