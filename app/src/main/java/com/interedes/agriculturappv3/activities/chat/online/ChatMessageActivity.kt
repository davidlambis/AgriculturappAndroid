package com.interedes.agriculturappv3.activities.chat.online

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.support.v7.widget.LinearLayoutManager
import android.view.View
import android.view.inputmethod.InputMethodManager
import android.widget.Toast
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*
import com.interedes.agriculturappv3.R
import com.interedes.agriculturappv3.activities.chat.online.adapters.MessagesAdapter
import com.interedes.agriculturappv3.modules.models.chat.ChatMessage
import com.interedes.agriculturappv3.modules.models.chat.UserFirebase
import kotlinx.android.synthetic.main.activity_chat_message.*
import android.os.Build
import android.os.Message
import android.support.v4.app.NavUtils
import android.support.v4.app.TaskStackBuilder
import android.view.MenuItem
import android.widget.ImageView
import com.interedes.agriculturappv3.modules.models.chat.Room
import com.interedes.agriculturappv3.services.resources.Chat_Resources
import com.interedes.agriculturappv3.services.resources.S3Resources
import com.squareup.picasso.Picasso
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.ArrayList


class ChatMessageActivity : AppCompatActivity() {

    private var mLayoutManager: LinearLayoutManager? = null
    private var mMessagesDBRef: DatabaseReference? = null



    private var mUsersRef: DatabaseReference? = null
    private val mMessagesList = ArrayList<ChatMessage>()
    private var adapter: MessagesAdapter? = null

    private var mReceiverId: String? = null
    var mReceiverFoto: String? = null
    var mReceiverRoom: Room? = null
    private var mReceiverName: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_chat_message)

        setToolbarInjection()
        messagesRecyclerView?.setHasFixedSize(true)
        // use a linear layout manager
        mLayoutManager = LinearLayoutManager(this)
        mLayoutManager?.setStackFromEnd(true)
        //mLayoutManager?.setReverseLayout(true);
        messagesRecyclerView?.setLayoutManager(mLayoutManager)
       // messagesRecyclerView.smoothScrollToPosition(0);

        //init Firebase
        mMessagesDBRef =  Chat_Resources.mMessagesDBRef
        mUsersRef = Chat_Resources.mUserDBRef

        //get receiverId from intent
        mReceiverId = intent.getStringExtra("USER_ID")
        mReceiverFoto = intent.getStringExtra("FOTO")
        mReceiverRoom= intent.getParcelableExtra("ROOM")
        /**listen to send message imagebutton click**/
        sendMessageImagebutton?.setOnClickListener(View.OnClickListener {
            val message = messageEditText?.getText().toString()
            val senderId = FirebaseAuth.getInstance().currentUser!!.uid
            if (message.isEmpty()) {
                Toast.makeText(applicationContext, "You must enter a message", Toast.LENGTH_SHORT).show()
            } else {
                //message is entered, send
                sendMessageToFirebase(message, senderId, mReceiverId)
            }
        })

        querymessagesBetweenThisUserAndClickedUser()
        queryRecipientName(mReceiverId)
    }


    private fun sendMessageToFirebase(message: String, senderId: String, receiverId: String?) {
        mMessagesList.clear()
        var time= System.currentTimeMillis()
        val dateFormat = SimpleDateFormat("MM/dd/yyyy")
        val date = Date()
        val fecha = dateFormat.format(date)
        //Obtener Hora
        val cal = Calendar.getInstance()
        val timeFormat = SimpleDateFormat("HH:mm")
        val hora = timeFormat.format(cal.time)
        val newMsg = ChatMessage(mReceiverRoom?.IdRoom,message, senderId, receiverId,fecha,hora,time)


        var roomDateComprador= Chat_Resources.mRoomDBRef?.child(mReceiverRoom?.User_From)?.child(Chat_Resources.getRoomById(mReceiverRoom?.User_To)+"/lastMessage")
        roomDateComprador?.setValue(message);

        var roomDateProductor= Chat_Resources.mRoomDBRef?.child(mReceiverRoom?.User_To)?.child(Chat_Resources.getRoomById(mReceiverRoom?.User_From)+"/lastMessage")
        roomDateProductor?.setValue(message);

        var roomDateCompradorDate= Chat_Resources.mRoomDBRef?.child(mReceiverRoom?.User_From)?.child(Chat_Resources.getRoomById(mReceiverRoom?.User_To)+"/date_Last")
        roomDateCompradorDate?.setValue(ServerValue.TIMESTAMP);

        var roomDateProductorDate= Chat_Resources.mRoomDBRef?.child(mReceiverRoom?.User_To)?.child(Chat_Resources.getRoomById(mReceiverRoom?.User_From)+"/date_Last")
        roomDateProductorDate?.setValue(ServerValue.TIMESTAMP);

        mMessagesDBRef?.push()?.setValue(newMsg)?.addOnCompleteListener { task ->
            if (!task.isSuccessful) {
                //error
                Toast.makeText(applicationContext, "Error " + task.exception!!.localizedMessage, Toast.LENGTH_SHORT).show()
                //mLayoutManager?.scrollToPositionWithOffset(0, 0);
            } else {
                Toast.makeText(applicationContext, "Message sent successfully!", Toast.LENGTH_SHORT).show()
                messageEditText?.setText(null)
                hideSoftKeyboard()
            }
        }
    }

    fun hideSoftKeyboard() {
        if (currentFocus != null) {
            val inputMethodManager = getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
            inputMethodManager.hideSoftInputFromWindow(currentFocus!!.windowToken, 0)
        }
    }

    private fun querymessagesBetweenThisUserAndClickedUser() {
        val query = mMessagesDBRef?.orderByChild("room_id")?.equalTo("${mReceiverRoom?.IdRoom}")
        iniAdapter()
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
                    if (chatMessage!!.senderId.equals(FirebaseAuth.getInstance().currentUser!!.uid) && chatMessage.receiverId.equals(mReceiverId) ||
                            chatMessage.senderId.equals(mReceiverId) && chatMessage.receiverId.equals(FirebaseAuth.getInstance().currentUser!!.uid)) {
                        mMessagesList.add(chatMessage)
                    }
                    //populateMessagesRecyclerView()
                    adapter?.add(chatMessage)
                    messagesRecyclerView.smoothScrollToPosition(adapter?.getItemCount()!! - 1);
                    //mLayoutManager?.scrollToPositionWithOffset(0, 0);
                    //mLayoutManager?.scrollToPosition(mMessagesList.size - 1);
                }
            }
            override fun onChildRemoved(p0: DataSnapshot?) {
            }
        })
        /*
        query?.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                mMessagesList.clear()
                for (snap in dataSnapshot.children) {
                    val chatMessage = snap.getValue(ChatMessage::class.java)
                    if (chatMessage!!.senderId.equals(FirebaseAuth.getInstance().currentUser!!.uid) && chatMessage.receiverId.equals(mReceiverId) ||
                            chatMessage.senderId.equals(mReceiverId) && chatMessage.receiverId.equals(FirebaseAuth.getInstance().currentUser!!.uid)) {
                        mMessagesList.add(chatMessage)
                    }
                }
                /**populate messages */
                populateMessagesRecyclerView()

                iniAdapter()
            }
            override fun onCancelled(databaseError: DatabaseError) {
            }
        })*/
    }

    private fun iniAdapter() {
        adapter = MessagesAdapter(ArrayList<ChatMessage>())
        messagesRecyclerView?.setAdapter(adapter)
    }

    private fun queryRecipientName(receiverId: String?) {
        mUsersRef?.child(receiverId)?.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                val recepient = dataSnapshot.getValue<UserFirebase>(UserFirebase::class.java)
                mReceiverName = recepient!!.Nombre+" "+recepient!!.Apellido
                try {
                    nameUserTo.setText(mReceiverName)

                    /*try {
                        Picasso.with(applicationContext).load(recepient.Imagen).placeholder(R.drawable.default_avata).into(imgUserTo)
                    } catch (e: Exception) {
                        e.printStackTrace()
                    }*/
                    Picasso.get()
                            .load(recepient.Imagen)
                            .into( imgUserTo, object : com.squareup.picasso.Callback {
                                override fun onError(e: java.lang.Exception?) {
                                    imgUserTo.setImageResource(R.drawable.default_avata)
                                }
                                override fun onSuccess() {
                                    // Toast.makeText(context,"Loaded foto",Toast.LENGTH_LONG).show()
                                }
                            })
                    //supportActionBar!!.setTitle(mReceiverName)
                    //actionBar!!.title = mReceiverName
                } catch (e: Exception) {
                    e.printStackTrace()
                }
            }
            override fun onCancelled(databaseError: DatabaseError) {

            }
        })
    }



    private fun setToolbarInjection() {
        setSupportActionBar(toolbar)
        supportActionBar!!.setDisplayShowTitleEnabled(false)
        if (supportActionBar != null)
        // Habilitar Up Button
            supportActionBar!!.setDisplayHomeAsUpEnabled(true)
        toolbar.title = getString(R.string.title_usuario)
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

}
