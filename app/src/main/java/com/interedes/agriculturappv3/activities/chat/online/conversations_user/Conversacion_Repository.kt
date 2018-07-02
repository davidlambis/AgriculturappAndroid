package com.interedes.agriculturappv3.activities.chat.online.conversations_user

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*
import com.interedes.agriculturappv3.activities.chat.online.conversations_user.events.RequestEventChatOnline
import com.interedes.agriculturappv3.libs.EventBus
import com.interedes.agriculturappv3.libs.GreenRobotEventBus
import com.interedes.agriculturappv3.modules.models.chat.*
import com.interedes.agriculturappv3.services.api.ApiInterface
import com.interedes.agriculturappv3.services.resources.Chat_Resources
import com.raizlabs.android.dbflow.kotlinextensions.save
import com.raizlabs.android.dbflow.sql.language.SQLite
import java.util.ArrayList

class Conversacion_Repository:IMainViewConversacion.Repository {


    var eventBus: EventBus? = null
    var apiService: ApiInterface? = null
    //FIREBASE
    private var mUsersDBRef: DatabaseReference? = null
    private var mAuth: FirebaseAuth? = null
    private var mRoomDBRef: DatabaseReference? = null


    //Listas
    private var listRoom: ArrayList<Room>? = null
    private val mUsersList = ArrayList<RoomConversation>()


    init {
        eventBus = GreenRobotEventBus()
        apiService = ApiInterface.create()

        mAuth = FirebaseAuth.getInstance()
        mUsersDBRef = Chat_Resources.mUserDBRef
        mRoomDBRef = Chat_Resources.mRoomDBRef


    }

    override fun getListRoom(checkConection: Boolean) {
        if(checkConection){
            queryUsersAndAddthemToList()
        }else{

            mUsersList.clear()
            val uidFirebase= mAuth?.currentUser?.uid

            if(uidFirebase!=null){
                var listConversaciones= SQLite.select().from(Room::class.java).where(Room_Table.User_From.eq(uidFirebase)).queryList()
                for (item in listConversaciones){
                    val userFirebase= SQLite.select().from(UserFirebase::class.java).where(UserFirebase_Table.User_Id.eq(item.User_To)).querySingle()
                    val conversation = RoomConversation()

                    conversation.UserFirebase=userFirebase
                    conversation.Room = item
                    //val user= user
                    mUsersList.add(conversation)
                }

                postEventOk(RequestEventChatOnline.LIST_ROOM_EVENT,mUsersList,null)
            }else{
                postEventOk(RequestEventChatOnline.LIST_ROOM_EVENT,ArrayList<RoomConversation>(),null)
            }
        }
    }


    private fun queryUsersAndAddthemToList() {
        mRoomDBRef?.child(mAuth?.currentUser?.uid)?.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {

                if (dataSnapshot.value != null) {
                    listRoom = ArrayList<Room>()
                    if (dataSnapshot.childrenCount > 0) {
                        mUsersList.clear()
                        for (snap in dataSnapshot.children) {

                            /*val room = Room()
                            val mapRoomInfo = dataSnapshot.value as HashMap<*, *>
                            room.Date = mapRoomInfo["date"] as Long
                            room.DateString = mapRoomInfo["dateString"] as String
                            room.Date_Last = mapRoomInfo["date_Last"] as Long
                            room.IdRoom = mapRoomInfo["idRoom"] as String*/
                            val room = snap.getValue<Room>(Room::class.java)

                            //if not current user, as we do not want to show ourselves then chat with ourselves lol
                            //if not current user, as we do not want to show ourselves then chat with ourselves lol
                            try {
                                room!!.save()
                                listRoom?.add(room)
                            } catch (e: Exception) {
                                e.printStackTrace()
                            }
                        }
                        getAllFriendInfo(0);
                    }
                    /* val mapRecord = dataSnapshot.value as HashMap<*, *>
                     val listKey = mapRecord.mapKeys {  }.iterator()
                     while (listKey.hasNext()) {
                         val key = listKey.next().toString()
                         listFriendID?.add(mapRecord[key].toString())
                     }
                     */
                } else {

                }
                /*
                 if (dataSnapshot.childrenCount > 0) {
                     mUsersList.clear()
                     for (snap in dataSnapshot.children) {
                         //var room = snap.getValue<Room>(Room::class.java)
                         //if not current user, as we do not want to show ourselves then chat with ourselves lol
                         var user = snap.getValue<UserFirebase>(UserFirebase::class.java)
                         //if not current user, as we do not want to show ourselves then chat with ourselves lol
                         try {
                             var  auth= mAuth?.currentUser?.uid
                             if(!user?.User_Id.equals(auth))
                              {
                                 mUsersList.add(user!!)
                              }
                         } catch (e: Exception) {
                             e.printStackTrace()
                         }
                     }
                 }
                 populaterecyclerView()
                 */
            }
            override fun onCancelled(databaseError: DatabaseError) {
                postEventError(RequestEventChatOnline.ERROR_EVENT,databaseError.message)
            }
        })
    }

    private fun getAllFriendInfo(index: Int) {
        if (index === listRoom?.size) {
                postEventOk(RequestEventChatOnline.LIST_ROOM_EVENT,mUsersList,null)
            //populaterecyclerView()
            //save list friend
            /*adapter.notifyDataSetChanged()
            dialogFindAllFriend.dismiss()
            mSwipeRefreshLayout.setRefreshing(false)
            detectFriendOnline.start()*/
        } else {
            val id = listRoom?.get(index)?.User_To
            mUsersDBRef?.child("$id")?.addListenerForSingleValueEvent(object : ValueEventListener {
                override fun onDataChange(dataSnapshot: DataSnapshot) {
                    if (dataSnapshot.value != null) {
                        val user = dataSnapshot.getValue<UserFirebase>(UserFirebase::class.java)
                        user?.save()

                        val conversation = RoomConversation()
                        //val room = dataSnapshot.getValue<RoomConversation>(RoomConversation::class.java)
                        conversation.UserFirebase=user
                        conversation.Room = listRoom?.get(index)
                        //val user= user
                        mUsersList.add(conversation)
                        //dataListFriend.getListFriend().add(user)
                        //FriendDB.getInstance(getContext()).addFriend(user)
                    }
                    getAllFriendInfo(index + 1)
                }
                override fun onCancelled(databaseError: DatabaseError) {
                    postEventError(RequestEventChatOnline.ERROR_EVENT,databaseError.message)
                }
            })
        }
    }




    //region Events

    private fun postEventOk(type: Int, list: List<RoomConversation>?, roomConversation: RoomConversation?) {
        var listMutable:MutableList<Object>? = null
        var roomMutable: Object? = null
        if (roomConversation != null) {
            roomMutable = roomConversation as Object
        }
        if (list != null) {
            listMutable=list as MutableList<Object>
        }

        postEvent(type, listMutable, roomMutable, null)
    }

    private fun postEventError(type: Int,messageError:String?) {
        postEvent(type, null,null,messageError)
    }

    //Main Post Event
    private fun postEvent(type: Int, listModel1:MutableList<Object>?,model:Object?,errorMessage: String?) {
        val event = RequestEventChatOnline(type, listModel1, model, errorMessage)
        event.eventType = type
        eventBus?.post(event)
    }
    //endregion

}