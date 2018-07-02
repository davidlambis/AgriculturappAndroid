package com.interedes.agriculturappv3.activities.chat.online.messages_chat

import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.support.v7.widget.LinearLayoutManager
import android.view.View
import android.view.inputmethod.InputMethodManager
import android.widget.Toast
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*
import com.interedes.agriculturappv3.R
import com.interedes.agriculturappv3.activities.chat.online.messages_chat.adapter.MessagesAdapter
import com.interedes.agriculturappv3.modules.models.chat.ChatMessage
import com.interedes.agriculturappv3.modules.models.chat.UserFirebase
import kotlinx.android.synthetic.main.activity_chat_message.*
import android.os.Build
import android.support.design.widget.Snackbar
import android.support.v4.app.NavUtils
import android.support.v4.app.TaskStackBuilder
import android.support.v4.content.ContextCompat
import android.view.MenuItem
import android.widget.TextView
import com.interedes.agriculturappv3.modules.models.chat.Room
import com.interedes.agriculturappv3.services.resources.Chat_Resources
import com.kaopiz.kprogresshud.KProgressHUD
import com.squareup.picasso.Picasso
import kotlinx.android.synthetic.main.custom_message_toast.view.*
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.ArrayList


class ChatMessageActivity : AppCompatActivity(), IMainViewChatMessages.MainView {


    private var mLayoutManager: LinearLayoutManager? = null
    private var mUsersRef: DatabaseReference? = null
    private var adapter: MessagesAdapter? = null

    private var mReceiverId: String? = null
    var mReceiverFoto: String? = null
    var mReceiverRoom: Room? = null
    private var mReceiverName: String? = null


    var presenter: IMainViewChatMessages.Presenter? = null
    private var hud: KProgressHUD?=null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_chat_message)


        presenter = ChatMessage_Presenter(this)
        presenter?.onCreate()

        setToolbarInjection()
        messagesRecyclerView?.setHasFixedSize(true)
        // use a linear layout manager
        mLayoutManager = LinearLayoutManager(this)
        mLayoutManager?.setStackFromEnd(true)
        //mLayoutManager?.setReverseLayout(true);
        messagesRecyclerView?.setLayoutManager(mLayoutManager)
       // messagesRecyclerView.smoothScrollToPosition(0);

        //init Firebase
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
        iniAdapter()
        //querymessagesBetweenThisUserAndClickedUser()
        presenter?.getListMessagesByRoom(mReceiverRoom!!,mReceiverId!!)
        queryRecipientName(mReceiverId)
    }


    private fun sendMessageToFirebase(message: String, senderId: String, receiverId: String?) {
        //mMessagesList.clear()
        var time= System.currentTimeMillis()
        val dateFormat = SimpleDateFormat("MM/dd/yyyy")
        val date = Date()
        val fecha = dateFormat.format(date)
        //Obtener Hora
        val cal = Calendar.getInstance()
        val timeFormat = SimpleDateFormat("HH:mm")
        val hora = timeFormat.format(cal.time)
        val newMsg = ChatMessage(mReceiverRoom?.IdRoom,message, senderId, receiverId,fecha,hora,time)


        presenter?.sendMessage(newMsg,mReceiverRoom!!)
    }

    fun hideSoftKeyboard() {
        if (currentFocus != null) {
            val inputMethodManager = getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
            inputMethodManager.hideSoftInputFromWindow(currentFocus!!.windowToken, 0)
        }
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


    //region IMPLEMENTACION INTERFACE CHATMESSAGESMAIN VIEW



    override fun showProgress() {
        //  swipeRefreshLayout.setRefreshing(true);
    }

    override fun hideProgress() {
        // swipeRefreshLayout.setRefreshing(false);
    }

    override fun showProgressHud(){
        hud = KProgressHUD.create(this)
                .setStyle(KProgressHUD.Style.SPIN_INDETERMINATE)
                .setWindowColor(getResources().getColor(R.color.colorPrimary))
                .setLabel("Cargando...", resources.getColor(R.color.white))
        hud?.show()
    }

    override fun hideProgressHud(){
        hud?.dismiss()
    }

    override fun requestResponseOK() {
        onMessageOk(R.color.colorPrimary,getString(R.string.request_ok));
    }

    override fun requestResponseError(error: String?) {
        onMessageError(R.color.grey_luiyi, error)
    }

    override fun checkConectionInternet(){
        onMessageToas(getString(R.string.verificate_conexion),R.color.red_900)
    }

    override fun onMessageToas(message: String, color: Int) {
        val inflater = this.layoutInflater
        var viewToast = inflater.inflate(R.layout.custom_message_toast, null)
        viewToast.txtMessageToastCustom.setText(message)
        viewToast.contetnToast.setBackgroundColor(ContextCompat.getColor(this, color))
        var mytoast =  Toast(this);
        mytoast.setView(viewToast);
        mytoast.setDuration(Toast.LENGTH_SHORT);
        mytoast.show();
    }

    override fun onMessageOk(colorPrimary: Int, message: String?) {
        val color = Color.WHITE
        val snackbar = Snackbar
                .make(container, message!!, Snackbar.LENGTH_LONG)
        val sbView = snackbar.view
        sbView.setBackgroundColor(ContextCompat.getColor(applicationContext, colorPrimary))
        val textView = sbView.findViewById<View>(android.support.design.R.id.snackbar_text) as TextView
        textView.setCompoundDrawablesWithIntrinsicBounds(R.drawable.quantum_ic_cast_connected_white_24, 0, 0, 0)
        // textView.setCompoundDrawablePadding(getResources().getDimensionPixelOffset(R.dimen.activity_horizontal_margin));
        textView.setTextColor(color)
        snackbar.show()
    }

    override fun onMessageError(colorPrimary: Int, message: String?) {
        onMessageOk(colorPrimary, message)
    }

    override fun onEventBroadcastReceiver(extras: Bundle, intent: Intent) {
        if(extras!=null){
            if (extras.containsKey("state_conectivity")) {
                var state_conectivity = intent.extras!!.getBoolean("state_conectivity")
            }

        }
    }


    override fun setListMessagesByRoom(sms: List<ChatMessage>) {

    }

    override fun setNewMessageByRoom(sms: ChatMessage) {
        adapter?.add(sms)
        messagesRecyclerView.smoothScrollToPosition(adapter?.getItemCount()!! - 1);
    }

    override fun limpiarCampos(){
        messageEditText?.setText("")
        hideSoftKeyboard()

    }
    //endregion


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
