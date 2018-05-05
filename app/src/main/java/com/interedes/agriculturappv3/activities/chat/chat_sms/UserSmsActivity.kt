package com.interedes.agriculturappv3.activities.chat.chat_sms

import android.Manifest
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.preference.PreferenceManager
import android.provider.BaseColumns
import android.provider.ContactsContract
import android.support.v4.app.ActivityCompat
import android.support.v4.app.NavUtils
import android.support.v4.app.TaskStackBuilder
import android.support.v7.widget.LinearLayoutManager
import android.view.Menu
import android.view.MenuItem
import android.widget.Toast
import com.interedes.agriculturappv3.R
import com.interedes.agriculturappv3.activities.chat.chat_sms.adapter.SmsUserAdapter
import com.interedes.agriculturappv3.services.resources.MessageSmsType
import kotlinx.android.synthetic.main.activity_user_sms.*
import kotlinx.android.synthetic.main.content_recyclerview.*
import java.util.*

class UserSmsActivity : AppCompatActivity() {


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


    ///Adapters
    var smsListUser:ArrayList<SmsUser> = ArrayList<SmsUser>()
    var smsListMessage:ArrayList<Sms> = ArrayList<Sms>()
    private var adapter: SmsUserAdapter? = null

    companion object {
        lateinit var instance: UserSmsActivity
        val NOTIFICATION_ID = 8675309
    }

    init {
        instance = this
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_user_sms)
        setToolbarInjection()
        initAdapter()
        PreferenceManager.setDefaultValues(this, R.xml.settings, false)
        //Start the service to display notifications
        val notificationServiceIntent = Intent(this, NotificationService::class.java)
        startService(notificationServiceIntent)


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

        val message = Uri.parse("content://sms/")
        val cr = contentResolver
        var c = cr.query(message, null, null, null, null)
        val indexBody = c.getColumnIndex("body")
        if (indexBody < 0 || !c.moveToFirst()) return
        this.startManagingCursor(c)
        val totalSMS = c.getCount()
        if (c.moveToFirst()) {
            for (i in 0 until totalSMS) {

                var typeMessage:String?=""
                if (c.getString(c.getColumnIndexOrThrow("type")).contains("1")) {
                    typeMessage= MessageSmsType.MESSAGE_TYPE_INBOX
                } else {
                    typeMessage= MessageSmsType.MESSAGE_TYPE_SENT
                }

                var objSms = Sms(
                        c.getString(c!!.getColumnIndexOrThrow("_id")),
                        c.getString(c
                                .getColumnIndexOrThrow("address")),
                        c.getString(c.getColumnIndexOrThrow("body")),
                        c.getString(c.getColumnIndex("read")),
                        c.getString(c.getColumnIndexOrThrow("date")),
                        typeMessage
                )

                smsListMessage.add(objSms)
                c.moveToNext()
            }

            //Ordenar
            Collections.sort(smsListMessage, object : Comparator<Sms> {
                override fun compare(lhs: Sms, rhs: Sms): Int {
                    // -1 - less than, 1 - greater than, 0 - equal, all inversed for descending
                    return if (lhs._id?.toInt()!! > rhs._id?.toInt()!!) -1 else if (lhs._id?.toInt()!! < rhs._id?.toInt()!!) 1 else 0
                }
            })

            //Reordenar Lista
            Collections.reverse(smsListMessage)

            for (objSms in smsListMessage){

                if(objSms._msg?.contains(getString(R.string.idenfication_send_sms_app))!!){

                    var verificateAddress = smsListUser?.filter { smsuser: SmsUser -> smsuser._address == objSms._address }
                    if(verificateAddress.size>0){
                        var item:SmsUser= verificateAddress.get(0)
                        smsListUser?.remove(item)
                    }

                    var newMsg=objSms._msg?.replace(getString(R.string.idenfication_send_sms_app),"")
                    val phoneNumber =  objSms._address
                    val contactName = getContactDisplayNameByNumber(phoneNumber)
                    var objSmsUser = SmsUser(
                            objSms._id,
                            objSms._address,
                            contactName,
                            newMsg,
                            objSms._readState,
                            objSms._time,
                            objSms._folderName
                    )
                    smsListUser.add(objSmsUser)
                }
            }

            setListSms(smsListUser)
        }
    }

     fun setListSms(sms: List<SmsUser>) {
        adapter?.clear()
        adapter?.setItems(sms)
        setResults(sms.size)
    }

     fun setResults(count: Int) {
        var results = String.format(getString(R.string.results_global_search),
                count);
        txtResults.setText(results);
    }

    private fun initAdapter() {
        recyclerView?.layoutManager = LinearLayoutManager(this)
        adapter = SmsUserAdapter(ArrayList<SmsUser>())
        recyclerView?.adapter = adapter
    }


    fun getContactDisplayNameByNumber(number: String?): String {
        val uri = Uri.withAppendedPath(ContactsContract.PhoneLookup.CONTENT_FILTER_URI, Uri.encode(number))
        var name = "Desconocido"
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


    //region PERMISSION

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


    //endregion

}
