package com.interedes.agriculturappv3.activities.chat.online

import android.content.Intent
import android.os.Build
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.support.v4.app.NavUtils
import android.support.v4.app.TaskStackBuilder
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.view.MenuItem
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*
import com.interedes.agriculturappv3.R
import com.interedes.agriculturappv3.activities.chat.online.adapters.UsersAdapter
import com.interedes.agriculturappv3.modules.models.chat.Room
import com.interedes.agriculturappv3.modules.models.chat.UserFirebase
import com.interedes.agriculturappv3.services.resources.Chat_Resources
import kotlinx.android.synthetic.main.activity_chat_users.*
import java.util.ArrayList
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.ValueEventListener

import com.interedes.agriculturappv3.modules.models.chat.RoomConversation


class ConversationsUsersActivity : AppCompatActivity() {

    private var mAuth: FirebaseAuth? = null
    private var mUsersDBRef: DatabaseReference? = null
    private var mLayoutManager: RecyclerView.LayoutManager? = null
    private var adapter: UsersAdapter? = null
    private val mUsersList = ArrayList<RoomConversation>()


    private var mRoomDBRef: DatabaseReference? = null
    private var listRoom: ArrayList<Room>? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_chat_users)
        setToolbarInjection()
        //assign firebase auth
        mAuth = FirebaseAuth.getInstance()
        mUsersDBRef = Chat_Resources.mUserDBRef
        mRoomDBRef = Chat_Resources.mRoomDBRef
        //initialize the recyclerview variables

        //usersRecyclerView.setHasFixedSize(true)
        // use a linear layout manager
       // mLayoutManager = LinearLayoutManager(this)
       // usersRecyclerView.setLayoutManager(mLayoutManager)
        initAdapter()
    }


    private fun initAdapter() {
        usersRecyclerView?.layoutManager = LinearLayoutManager(this)
        adapter = UsersAdapter(ArrayList<RoomConversation>())
        usersRecyclerView?.adapter = adapter
    }

    private fun setToolbarInjection() {
        setSupportActionBar(toolbar)
        supportActionBar!!.setDisplayShowTitleEnabled(false)
        if (supportActionBar != null)
        // Habilitar Up Button
            supportActionBar!!.setDisplayHomeAsUpEnabled(true)
        toolbar.title = getString(R.string.title_usuario)
    }

    private fun populaterecyclerView() {
        adapter?.clear()
        adapter?.setItems(mUsersList)
    }

    private fun queryUsersAndAddthemToList() {
        mRoomDBRef?.child(mAuth?.currentUser?.uid)?.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                
                if (dataSnapshot.value != null) {

                    listRoom = ArrayList<Room>()

                    if (dataSnapshot.childrenCount > 0) {
                        mUsersList.clear()
                        for (snap in dataSnapshot.children) {
                            var room = snap.getValue<Room>(Room::class.java)
                            //if not current user, as we do not want to show ourselves then chat with ourselves lol
                            //if not current user, as we do not want to show ourselves then chat with ourselves lol
                            try {
                                listRoom?.add(room!!)
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
            }
        })
    }

    private fun getAllFriendInfo(index: Int) {

        if (index === listRoom?.size) {
            populaterecyclerView()
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
                        var user = dataSnapshot.getValue<RoomConversation>(RoomConversation::class.java)
                        user?.Room = listRoom?.get(index)
                        mUsersList.add(user!!)
                        //dataListFriend.getListFriend().add(user)
                        //FriendDB.getInstance(getContext()).addFriend(user)
                    }
                    getAllFriendInfo(index + 1)
                }

                override fun onCancelled(databaseError: DatabaseError) {

                }
            })
        }
    }



    override fun onStart() {
        super.onStart()

        checkIfUserIsSignIn()
        /**query usrs and add them to a list */
        queryUsersAndAddthemToList()
    }


    private fun checkIfUserIsSignIn() {
        val user = FirebaseAuth.getInstance().currentUser
        if (user != null) {
            // User is signed in
        } else {
            // No user is signed in
            /**go to login user first */
            goToSignIn()
        }
    }

    private fun goToSignIn() {
        //startActivity(Intent(this, LoginActivity::class.java))
    }


    //region MENU
    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        // as you specify a parent activity in AndroidManifest.xml.
        when (item.itemId) {

        ///Metodo que permite no recargar la pagina al devolverse
            android.R.id.home -> {
                // Obtener intent de la actividad padre
                val upIntent = NavUtils.getParentActivityIntent(this)
                upIntent!!.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK)

                // Comprobar si DetailActivity no se creó desde CourseActivity
                if (NavUtils.shouldUpRecreateTask(this, upIntent) || this.isTaskRoot) {
                    // Construir de nuevo la tarea para ligar ambas actividades
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
                        TaskStackBuilder.create(this)
                                .addNextIntentWithParentStack(upIntent)
                                .startActivities()
                    }
                }

                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                    // Terminar con el método correspondiente para Android 5.x
                    this.finishAfterTransition()
                    return true
                }

                //Para versiones anterios a 5.x
                if (Build.VERSION.SDK_INT < Build.VERSION_CODES.LOLLIPOP) {
                    // Terminar con el método correspondiente para Android 5.x
                    onBackPressed()
                    return true
                }
            }
            else -> {
            }
        }
        return super.onOptionsItemSelected(item)

    }
    //endregion
}
