package com.interedes.agriculturappv3.activities.chat

import android.content.Context
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.view.View
import android.view.inputmethod.InputMethodManager
import android.widget.EditText
import android.widget.ImageButton
import android.widget.Toast
import com.google.android.gms.tasks.OnCompleteListener
import com.google.android.gms.tasks.Task
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*
import com.interedes.agriculturappv3.R
import com.interedes.agriculturappv3.activities.chat.util.MessagesAdapter
import com.interedes.agriculturappv3.productor.models.chat.ChatMessage
import com.interedes.agriculturappv3.productor.models.chat.UserFirebase
import kotlinx.android.synthetic.main.activity_chat_message.*
import android.graphics.BitmapFactory
import java.util.*


class ChatMessageActivity : AppCompatActivity() {

    private var mLayoutManager: LinearLayoutManager? = null
    private var mMessagesDBRef: DatabaseReference? = null
    private var mUsersRef: DatabaseReference? = null
    private val mMessagesList = ArrayList<ChatMessage>()
    private var adapter: MessagesAdapter? = null

    private var mReceiverId: String? = null
    private var mReceiverFoto: String? = null
    private var mReceiverName: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_chat_message)




        messagesRecyclerView?.setHasFixedSize(true)
        // use a linear layout manager
        mLayoutManager = LinearLayoutManager(this)
        mLayoutManager?.setStackFromEnd(true)
        messagesRecyclerView?.setLayoutManager(mLayoutManager)

        //init Firebase
        mMessagesDBRef = FirebaseDatabase.getInstance().reference.child("Messages")
        mUsersRef = FirebaseDatabase.getInstance().reference.child("Users")

        //get receiverId from intent
        mReceiverId = intent.getStringExtra("USER_ID")
        mReceiverFoto = intent.getStringExtra("FOTO")

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
    }


    override fun onStart() {
        super.onStart()
        /**Query and populate chat messages */
        querymessagesBetweenThisUserAndClickedUser()
        /**sets title bar with recepient name */
        queryRecipientName(mReceiverId)
    }

    private fun sendMessageToFirebase(message: String, senderId: String, receiverId: String?) {
        mMessagesList.clear()
        val newMsg = ChatMessage(message, senderId, receiverId)
        mMessagesDBRef?.push()?.setValue(newMsg)?.addOnCompleteListener { task ->
            if (!task.isSuccessful) {
                //error
                Toast.makeText(applicationContext, "Error " + task.exception!!.localizedMessage, Toast.LENGTH_SHORT).show()
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
        mMessagesDBRef?.addValueEventListener(object : ValueEventListener {
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

            }

            override fun onCancelled(databaseError: DatabaseError) {

            }
        })
    }

    private fun populateMessagesRecyclerView() {



        adapter = MessagesAdapter(mMessagesList)
        messagesRecyclerView?.setAdapter(adapter)
    }

    private fun queryRecipientName(receiverId: String?) {
        mUsersRef?.child(receiverId)?.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                val recepient = dataSnapshot.getValue<UserFirebase>(UserFirebase::class.java)
                mReceiverName = recepient!!.Nombre+" "+recepient!!.Apellido
                try {
                    supportActionBar!!.setTitle(mReceiverName)
                    actionBar!!.title = mReceiverName
                } catch (e: Exception) {
                    e.printStackTrace()
                }
            }
            override fun onCancelled(databaseError: DatabaseError) {

            }
        })
    }
}
