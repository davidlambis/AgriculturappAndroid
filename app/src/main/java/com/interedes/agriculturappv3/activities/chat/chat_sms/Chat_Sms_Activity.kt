package com.interedes.agriculturappv3.activities.chat.chat_sms

import android.Manifest
import android.content.*
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import com.interedes.agriculturappv3.R
import android.widget.ArrayAdapter
import android.widget.ListView
import android.preference.PreferenceManager
import android.view.View
import android.widget.AdapterView
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import android.provider.ContactsContract
import android.provider.BaseColumns
import android.support.v4.app.ActivityCompat
import android.support.v4.app.NavUtils
import android.support.v4.app.TaskStackBuilder
import android.support.v7.widget.LinearLayoutManager
import android.telephony.SmsManager
import android.view.Menu
import android.view.MenuItem
import android.widget.Toast
import com.interedes.agriculturappv3.activities.chat.chat_sms.adapter.SmsAdapter
import com.interedes.agriculturappv3.activities.chat.chat_sms.adapter.SmsUserAdapter
import com.interedes.agriculturappv3.services.Const
import com.interedes.agriculturappv3.services.resources.MessageSmsType
import kotlinx.android.synthetic.main.activity_chat__sms_.*
import java.util.*


class Chat_Sms_Activity : AppCompatActivity(),View.OnClickListener {


    private val TAG = "SMSCHATAPP"
    val TAG_USER_NAME = "USER_NAME"

    var contactUserName:String?=""
    var contactNumber:String?=""

     var PERMISSIONS = arrayOf(
            Manifest.permission.READ_PHONE_STATE,
            Manifest.permission.WRITE_EXTERNAL_STORAGE,
            Manifest.permission.READ_EXTERNAL_STORAGE,
             Manifest.permission.SEND_SMS,
             Manifest.permission.READ_SMS,
             Manifest.permission.RECEIVE_SMS,
             Manifest.permission.READ_CONTACTS,
             Manifest.permission.WRITE_CONTACTS
             )

     val PERMISSION_REQUEST_CODE = 1
     var PERMISSION_ALL = 1


    ///
    private var mLayoutManager: LinearLayoutManager? = null
    var smsLisMessages = ArrayList<Sms>()
    private var adapter: SmsAdapter? = null


    companion object {
        var instance: Chat_Sms_Activity?=null
        val NOTIFICATION_ID = 8675309
    }

    init {
        instance = this
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_chat__sms_)
        setToolbarInjection()
        initAdapter()

        messagesRecyclerView?.setHasFixedSize(true)
        // use a linear layout manager
        mLayoutManager = LinearLayoutManager(this)
        mLayoutManager?.setStackFromEnd(true)
        messagesRecyclerView?.setLayoutManager(mLayoutManager)

        /*PreferenceManager.setDefaultValues(this, R.xml.settings, false)
        //Start the service to display notifications
        val notificationServiceIntent = Intent(this, NotificationService::class.java)
        startService(notificationServiceIntent)*/

        //You can see activity_main in the res folder: activity_main.xml
        //SMSList is the main listview in activity_main.xml

        //Bind the info to our listview

        val intent = intent
        contactNumber = intent.getStringExtra(TAG)
        contactUserName = intent.getStringExtra(TAG_USER_NAME)
        nameUserTo.setText(contactUserName)
        adressUserTo.setText(contactNumber)

        sendMessageImagebutton.setOnClickListener(this)


        if (Build.VERSION.SDK_INT >= 23) {
            if (!hasPermissions(applicationContext, *PERMISSIONS)) {
                requestPermission()
            } else {
                //Helper function
                refreshSmsInbox()
            }
        } else {
            //Helper function
            refreshSmsInbox()
        }
    }

    private fun setToolbarInjection() {
        setSupportActionBar(toolbar)
        supportActionBar!!.setDisplayShowTitleEnabled(false)
        if (supportActionBar != null)
        // Habilitar Up Button
            supportActionBar!!.setDisplayHomeAsUpEnabled(true)
        toolbar.title = getString(R.string.title_sms_text)
    }

    fun refreshSmsInbox() {

        for (permission in PERMISSIONS) {
            if (ActivityCompat.checkSelfPermission(applicationContext, permission) != PackageManager.PERMISSION_GRANTED) {
                return
            }
        }

        val qStr = arrayOf(contactNumber)

        // Getting all of the inbox messages
        val inboxResolver = contentResolver
        val c = contentResolver.query(Uri.parse("content://sms/"), arrayOf("_id", "thread_id", "address", "person", "date", "body","read", "type"), "address=?", qStr, null)
        val indexBody = c!!.getColumnIndex("body")
        if (indexBody < 0 || !c.moveToFirst()) return



        var objSms = Sms()

        this.startManagingCursor(c)
        val totalSMS = c.getCount()

        if (c.moveToFirst()) {

            smsLisMessages.clear()
            for (i in 0 until totalSMS) {

                var typeMessage:String?=""
                if (c.getString(c.getColumnIndexOrThrow("type")).contains("1")) {
                    typeMessage=MessageSmsType.MESSAGE_TYPE_INBOX
                } else {
                    typeMessage=MessageSmsType.MESSAGE_TYPE_SENT
                }

                objSms = Sms(
                        c.getString(c.getColumnIndexOrThrow("_id")),
                        c.getString(c
                                .getColumnIndexOrThrow("address")),
                        c.getString(c.getColumnIndexOrThrow("body")),
                        c.getString(c.getColumnIndex("read")),
                        c.getString(c.getColumnIndexOrThrow("date")),
                        typeMessage
                )

                var smsNew:String?=""


                if(objSms._msg?.contains(getString(R.string.idenfication_send_sms_app))!!){
                    smsNew=objSms._msg?.replace(getString(R.string.idenfication_send_sms_app),"")
                    objSms._msg=smsNew
                }

                smsLisMessages.add(objSms)
                c.moveToNext()
            }

            //Ordenar
            Collections.sort(smsLisMessages, object : Comparator<Sms> {
                override fun compare(lhs: Sms, rhs: Sms): Int {
                    // -1 - less than, 1 - greater than, 0 - equal, all inversed for descending
                    return if (lhs._id?.toInt()!! > rhs._id?.toInt()!!) -1 else if (lhs._id?.toInt()!! < rhs._id?.toInt()!!) 1 else 0
                }
            })

            //Reordenar Lista
            Collections.reverse(smsLisMessages)

            setListSms(smsLisMessages)
        }
    }

    fun setListSms(sms: List<Sms>) {
        adapter?.clear()
        adapter?.setItems(sms)
        messagesRecyclerView.scrollToPosition(smsLisMessages.size-1);
      //  setResults(sms.size)
    }



    private fun initAdapter() {
        messagesRecyclerView?.layoutManager = LinearLayoutManager(this)
        adapter = SmsAdapter(ArrayList<Sms>())
        messagesRecyclerView?.adapter = adapter
    }



    fun updateList(smsMessage: String) {
        refreshSmsInbox()
        //arrayAdapter?.insert(smsMessage, 0)
        //arrayAdapter?.notifyDataSetChanged()
    }

    override fun onClick(p0: View?) {
        when (p0?.id) {

            R.id.sendMessageImagebutton -> {

                var cancel = false
                var focusView: View? = null
                if (messageEditText?.text.toString().isEmpty()) {
                    messageEditText?.setError(getString(R.string.error_field_required))
                    focusView = messageEditText
                    cancel = true
                }


                if (cancel) {
                    focusView?.requestFocus()
                } else {
                    sendSMS()
                }
            }
        }
    }

    protected fun sendSMS() {
        val toPhoneNumber =contactNumber
        val smsMessage = getString(R.string.idenfication_send_sms_app)+" "+messageEditText.getText().toString()
        try {
            val smsManager = SmsManager.getDefault()
            smsManager.sendTextMessage(toPhoneNumber, null, smsMessage, null, null)

            Toast.makeText(applicationContext, "SMS sent.",
                    Toast.LENGTH_LONG).show()
            messageEditText.setText("")
            refreshSmsInbox()
        } catch (e: Exception) {
            Toast.makeText(applicationContext,
                    "Sending SMS failed.",
                    Toast.LENGTH_LONG).show()
            e.printStackTrace()
        }
    }


    /*
    private fun populaterecyclerView() {
        adapter = SmsAdapter(smsLisMessages)
        usersRecyclerView.setAdapter(adapter)
    }*/


    //This method handles user clicks on the menu option buttons.
    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
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



    fun hasPermissions(context: Context?, vararg permissions: String): Boolean {
        if (android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.M && context != null && permissions != null) {
            for (permission in permissions) {
                if (ActivityCompat.checkSelfPermission(context, permission) != PackageManager.PERMISSION_GRANTED) {
                    return false
                }
            }
        }
        return true
    }

    private fun requestPermission() {
        //ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, PERMISSION_REQUEST_CODE);
        ActivityCompat.requestPermissions(this, PERMISSIONS, PERMISSION_ALL)
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<String>, grantResults: IntArray) {
        when (requestCode) {
            PERMISSION_REQUEST_CODE -> if (grantResults.size > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                refreshSmsInbox()
            } else {
                Toast.makeText(applicationContext,
                        "Permiso denegado", Toast.LENGTH_LONG).show()

            }
        }
    }

    private val mNotificationReceiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context, intent: Intent) {
            var extras = intent.extras
            if (extras != null) {
               // if (extras.containsKey("new_message")) {
                    updateList("")
               // }
            }
        }
    }

    override fun onResume() {
        super.onResume()
        registerReceiver(mNotificationReceiver, IntentFilter(Const.SERVICE_RECYVE_MESSAGE))
    }

    override fun onPause() {
        super.onPause()
        unregisterReceiver(this.mNotificationReceiver);
    }
}
