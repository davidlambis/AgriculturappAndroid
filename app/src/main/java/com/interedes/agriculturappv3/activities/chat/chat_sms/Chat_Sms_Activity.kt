package com.interedes.agriculturappv3.activities.chat.chat_sms

import android.Manifest
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import com.interedes.agriculturappv3.R
import kotlinx.android.synthetic.main.activity_chat_users.*
import android.widget.ArrayAdapter
import android.widget.ListView
import android.content.Intent
import android.preference.PreferenceManager
import android.view.View
import android.widget.AdapterView
import android.content.ContentResolver
import android.content.Context
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import android.provider.ContactsContract
import android.provider.BaseColumns
import android.support.v4.app.ActivityCompat
import android.support.v4.app.NavUtils
import android.support.v4.app.TaskStackBuilder
import android.support.v7.widget.LinearLayoutManager
import android.view.Menu
import android.view.MenuItem
import android.widget.Toast
import com.interedes.agriculturappv3.activities.chat.chat_sms.adapter.SmsAdapter
import com.interedes.agriculturappv3.activities.chat.chat_sms.adapter.SmsUserAdapter
import com.interedes.agriculturappv3.services.resources.MessageSmsType
import kotlinx.android.synthetic.main.content_recyclerview.*
import java.util.*


class Chat_Sms_Activity : AppCompatActivity() {

    private val TAG = "SMSCHATAPP"

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
    var smsLisMessages = ArrayList<Sms>()
    private var adapter: SmsAdapter? = null


    companion object {
        lateinit var instance: Chat_Sms_Activity
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

        /*PreferenceManager.setDefaultValues(this, R.xml.settings, false)
        //Start the service to display notifications
        val notificationServiceIntent = Intent(this, NotificationService::class.java)
        startService(notificationServiceIntent)*/

        //You can see activity_main in the res folder: activity_main.xml
        //SMSList is the main listview in activity_main.xml

        //Bind the info to our listview



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


        var contactNumber = ""
        val intent = intent
        contactNumber = intent.getStringExtra(TAG)

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

                smsLisMessages.add(objSms)
                c.moveToNext()
            }

            setListSms(smsLisMessages)
        }
    }

    fun setListSms(sms: List<Sms>) {
        adapter?.clear()
        adapter?.setItems(sms)
      //  setResults(sms.size)
    }



    private fun initAdapter() {
        recyclerView?.layoutManager = LinearLayoutManager(this)
        adapter = SmsAdapter(ArrayList<Sms>())
        recyclerView?.adapter = adapter
    }



    fun updateList(smsMessage: String) {
        //arrayAdapter?.insert(smsMessage, 0)
        //arrayAdapter?.notifyDataSetChanged()
    }


    private fun populaterecyclerView() {
        adapter = SmsAdapter(smsLisMessages)
        usersRecyclerView.setAdapter(adapter)
    }


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
}