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
import android.view.Menu
import android.view.MenuItem
import android.widget.Toast




class Chat_Sms_Activity : AppCompatActivity(),AdapterView.OnItemClickListener {



    private val TAG = "SMSCHATAPP"

    var smsMessagesList = ArrayList<String>()

    var smsListView: ListView? = null       //The ListView for this activity
    var arrayAdapter: ArrayAdapter<String>? = null  //An array adapter to put sms messages into the ListView

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

        PreferenceManager.setDefaultValues(this, R.xml.settings, false)

        //Start the service to display notifications
        val notificationServiceIntent = Intent(this, NotificationService::class.java)
        startService(notificationServiceIntent)

        //You can see activity_main in the res folder: activity_main.xml

        //SMSList is the main listview in activity_main.xml


        smsListView = findViewById(R.id.SMSList)
        //Bind the info to our listview
        arrayAdapter = ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, smsMessagesList)
        smsListView?.setAdapter(arrayAdapter)
        smsListView?.setOnItemClickListener(this)


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
        toolbar.title = getString(R.string.title_usuario)
    }

    fun refreshSmsInbox() {

        for (permission in PERMISSIONS) {
            if (ActivityCompat.checkSelfPermission(applicationContext, permission) != PackageManager.PERMISSION_GRANTED) {
                return
            }
        }

        val lstSms = ArrayList<Sms>()
        var objSms = Sms()

        val message = Uri.parse("content://sms/")
        val cr = contentResolver

        val c = cr.query(message, null, null, null, null)
        val indexBody = c!!.getColumnIndex("body")
        if (indexBody < 0 || !c.moveToFirst()) return


        this.startManagingCursor(c)
        val totalSMS = c!!.getCount()
        arrayAdapter?.clear()
        if (c!!.moveToFirst()) {
            for (i in 0 until totalSMS) {
                objSms = Sms()
                objSms._id=c!!.getString(c!!.getColumnIndexOrThrow("_id"))
                objSms._address=c!!.getString(c!!
                        .getColumnIndexOrThrow("address"))
                objSms._msg=c!!.getString(c!!.getColumnIndexOrThrow("body"))
                objSms._readState=c!!.getString(c!!.getColumnIndex("read"))
                objSms._time=c!!.getString(c!!.getColumnIndexOrThrow("date"))
                if (c!!.getString(c!!.getColumnIndexOrThrow("type")).contains("1")) {
                    objSms._folderName="inbox"
                } else {
                    objSms._folderName="sent"
                }
                lstSms.add(objSms)

                val phoneNumber = objSms._address
                val contactName = getContactDisplayNameByNumber(phoneNumber)

                val str = phoneNumber + " : " + contactName +
                        "\n" + objSms._msg + "\n"
                arrayAdapter?.add(str)

                c!!.moveToNext()
            }
        }
        // else {
        // throw new RuntimeException("You have no SMS");
        // }
        c!!.close()

        /*
        val contentResolver = contentResolver
        //Get the SMS inbox messages.  This is the query format for the SMS database
        val smsInboxCursor = contentResolver.query(Uri.parse("content://sms/inbox"), null, null, null, null)
        //"content://sms/sent" are the sent sms messages
        //"content://sms/draft" are the draft sms messages
        //val indexBody = smsInboxCursor!!.getColumnIndex("body")
        val indexAddress = smsInboxCursor.getColumnIndex("address")
        if (indexBody < 0 || !smsInboxCursor.moveToFirst()) return
        arrayAdapter?.clear()
        //Add all these messages into the List
        var i = 0
        do {
            val phoneNumber = smsInboxCursor.getString(indexAddress)
            val contactName = getContactDisplayNameByNumber(phoneNumber)

            val str = phoneNumber + " : " + contactName +
                    "\n" + smsInboxCursor.getString(indexBody) + "\n"
            arrayAdapter?.add(str)

        } while (smsInboxCursor.moveToNext() && (i++) < 100)
        */


    }

    fun getContactDisplayNameByNumber(number: String?): String {
        val uri = Uri.withAppendedPath(ContactsContract.PhoneLookup.CONTENT_FILTER_URI, Uri.encode(number))
        var name = "?"
        val contentResolver = contentResolver
        val contactLookup = contentResolver.query(uri, arrayOf(BaseColumns._ID, ContactsContract.PhoneLookup.DISPLAY_NAME), null, null, null)
        try {
            if (contactLookup != null && contactLookup.count > 0) {
                contactLookup.moveToNext()
                name = contactLookup.getString(contactLookup.getColumnIndex(ContactsContract.Data.DISPLAY_NAME))
                //String contactId = contactLookup.getString(contactLookup.getColumnIndex(BaseColumns._ID));
            }
        } finally {
            contactLookup?.close()
        }
        return name
    }


    fun updateList(smsMessage: String) {
        arrayAdapter?.insert(smsMessage, 0)
        arrayAdapter?.notifyDataSetChanged()
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        // Inflate the menu; this adds items to the action bar if it is present.
        menuInflater.inflate(R.menu.menu_message_sms, menu)
        return true
    }

    //This method handles user clicks on the menu option buttons.
    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        when (item.itemId) {

            R.id.action_settings->{
                val intent = Intent(this, SettingsActivity::class.java)
                startActivity(intent)
                return true
            }

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


    override fun onItemClick(p0: AdapterView<*>?, p1: View?, pos: Int, id: Long) {

        try {
            val smsMessages = smsMessagesList[pos].split("\n")
            //Include the address (phone number).
            val address = smsMessages[0]
            var smsMessage = ""
            //Get the correct message.
            for (i in 1 until smsMessages.size) {
                smsMessage += smsMessages[i]
            }
            //Creates the message string for displaying in the Toast
            var smsMessageStr = address + "\n"
            smsMessageStr += smsMessage
            Toast.makeText(this, smsMessageStr, Toast.LENGTH_SHORT).show()
            /* val intent = Intent(this, read_messages::class.java)
             //Getting the phone number
             val phoneNumber = address.split("\\s+".toRegex()).dropLastWhile { it.isEmpty() }.toTypedArray()[0]
             intent.putExtra(FragmentActivity.TAG, phoneNumber)
             Log.v(FragmentActivity.TAG, smsMessage)
             // Finally, launch the activity.
             startActivity(intent)*/

        } catch (e: Exception) {
            e.printStackTrace()
        }

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

            }/* else if ((Build.VERSION.SDK_INT >= 23 && !shouldShowRequestPermissionRationale(permissions[0])) ||
                        (Build.VERSION.SDK_INT >= 23 && !shouldShowRequestPermissionRationale(permissions[1]))) {
                    //Toast.makeText(MainActivity.this, "Go to Settings and Grant the permission to use this feature.", Toast.LENGTH_SHORT).show();
                    // User selected the Never Ask Again Option
                    Intent i = new Intent();
                    i.setAction(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
                    i.addCategory(Intent.CATEGORY_DEFAULT);
                    i.setData(Uri.parse("package:" + getApplicationContext().getPackageName()));
                    i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    i.addFlags(Intent.FLAG_ACTIVITY_NO_HISTORY);
                    i.addFlags(Intent.FLAG_ACTIVITY_EXCLUDE_FROM_RECENTS);
                    getApplicationContext().startActivity(i);

                } */
        }
    }
}
