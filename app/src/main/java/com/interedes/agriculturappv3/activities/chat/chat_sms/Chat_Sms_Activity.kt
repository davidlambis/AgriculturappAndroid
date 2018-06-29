package com.interedes.agriculturappv3.activities.chat.chat_sms

import android.Manifest
import android.app.Activity
import android.app.PendingIntent
import android.content.*
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import com.interedes.agriculturappv3.R
import android.view.View
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import android.provider.Settings
import android.support.v4.app.ActivityCompat
import android.support.v4.app.NavUtils
import android.support.v4.app.TaskStackBuilder
import android.support.v4.content.ContextCompat
import android.support.v7.app.AlertDialog
import android.support.v7.widget.LinearLayoutManager
import android.telephony.SmsManager
import android.view.MenuItem
import android.widget.Toast
import com.interedes.agriculturappv3.activities.chat.chat_sms.adapter.SmsAdapter
import com.interedes.agriculturappv3.modules.account.IMainViewAccount
import com.interedes.agriculturappv3.modules.models.sms.Sms
import com.interedes.agriculturappv3.services.Const
import com.interedes.agriculturappv3.services.resources.MessageSmsType
import com.kaopiz.kprogresshud.KProgressHUD
import kotlinx.android.synthetic.main.activity_chat__sms_.*
import kotlinx.android.synthetic.main.custom_message_toast.view.*
import java.util.*


class Chat_Sms_Activity : AppCompatActivity(),View.OnClickListener,IMainViewChatSms.MainView {

    private val TAG = "SMSCHATAPP"
    val TAG_USER_NAME = "USER_NAME"

    var contactUserName:String?=""
    var contactNumber:String?=""

    var presenter: IMainViewChatSms.Presenter? = null


    //Progress
    private var hud: KProgressHUD?=null


    //permission
    var PERMISSION_ALL = 1
    var PERMISSIONS = arrayOf(
            Manifest.permission.SEND_SMS,
            Manifest.permission.READ_SMS,
            Manifest.permission.RECEIVE_SMS
    )

    val PERMISSION_REQUEST_CODE = 1

    //private var sentStatusReceiver: BroadcastReceiver? = null
    //private var deliveredStatusReceiver: BroadcastReceiver? = null
    //private var mNotificationReceiver: BroadcastReceiver? = null
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
        presenter = ChatSms_Presenter(this)
        presenter?.onCreate()
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
                ///refreshSmsInbox()
            }
        } else {
            //Helper function
           /// refreshSmsInbox()
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


    /*
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

    */

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
        //refreshSmsInbox()
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

                    if (Build.VERSION.SDK_INT >= 23) {
                        if (!hasPermissions(applicationContext, *PERMISSIONS)) {
                            requestPermission()
                        } else {
                            sendSMS()
                        }
                    } else {
                        sendSMS()
                    }


                }
            }
        }
    }

    protected fun sendSMS() {
        val phone =contactNumber
        val smsMessage = getString(R.string.idenfication_send_sms_app)+" "+messageEditText.getText().toString()

       /* try {

        var intent =  Intent(Intent.ACTION_VIEW, Uri.parse("sms:" + toPhoneNumber?.replace(" ","")));
        intent.putExtra("sms_body", smsMessage);
        startActivityForResult(intent,34);

        } catch (e: Exception) {
            Toast.makeText(applicationContext,
                    "Sending SMS failed.",
                    Toast.LENGTH_LONG).show()
            e.printStackTrace()
        }*/

        try {
            val message = smsMessage

            //Check if the phoneNumber is empty
            if (phone!!.isEmpty()) {
                Toast.makeText(applicationContext, "Please Enter a Valid Phone Number", Toast.LENGTH_SHORT).show()
            } else {

                val sms = SmsManager.getDefault()
                // if message length is too long messages are divided
                val messages = sms.divideMessage(message)
                for (msg in messages) {
                    val sentIntent = PendingIntent.getBroadcast(this, 0, Intent(Const.SERVICE_SMS_SENT), 0)
                    val deliveredIntent = PendingIntent.getBroadcast(this, 0, Intent(Const.SERVICE_SMS_DELIVERED), 0)
                    sms.sendTextMessage(phone, null, msg, sentIntent, deliveredIntent)
                }

                showProgressHud()
            }

           // refreshSmsInbox()
        } catch (e: Exception) {
            Toast.makeText(applicationContext,
                    "Sending SMS failed.",
                    Toast.LENGTH_LONG).show()
            e.printStackTrace()
            hideProgressHud()
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
        ActivityCompat.requestPermissions(this, PERMISSIONS, PERMISSION_REQUEST_CODE);
        //ContextCompat.requestPermissions(activity!!, PERMISSIONS, PERMISSION_ALL)
        // requestPermissions(PERMISSIONS, PERMISSION_ALL)
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        when (requestCode) {
            PERMISSION_REQUEST_CODE ->
                if (grantResults.size > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    Toast.makeText(applicationContext, "Permission Granted, Now you can access sms", Toast.LENGTH_SHORT).show()
                    sendSMS()
                } else if (grantResults[0] == PackageManager.PERMISSION_DENIED) {


                    if (android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.M){
                        if (shouldShowRequestPermissionRationale(Manifest.permission.SEND_SMS) || shouldShowRequestPermissionRationale(Manifest.permission.READ_SMS) || shouldShowRequestPermissionRationale(Manifest.permission.RECEIVE_SMS)) {
                            Toast.makeText(this,
                                    "Permiso denegado", Toast.LENGTH_LONG).show()
                        }
                        else{
                            if (hasPermissions(this, *PERMISSIONS)) {
                                sendSMS()
                            } else {
                                val builder = AlertDialog.Builder(this)
                                builder.setMessage("Permission Granted, Now you can access sms")
                                        .setPositiveButton("Aceptar") { dialog, id ->
                                            val intent = Intent()
                                            intent.setAction(Settings.ACTION_APPLICATION_DETAILS_SETTINGS)
                                            val uri = Uri.fromParts("package", "com.example.usuario.sendsms", null)
                                            intent.setData(uri)
                                            startActivity(intent)
                                        }
                                builder.setTitle("Permiso")
                                builder.setIcon(R.mipmap.ic_launcher)
                                // Create the AlertDialog object and return it
                                builder.show()

                            }
                        }
                    }
                }
        //else -> super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        }
    }


    //region Services Broadkast

    private val sentStatusReceiver = object : BroadcastReceiver() {

        override fun onReceive(arg0: Context, arg1: Intent) {
            var s = "Unknown Error"
            when (resultCode) {
                Activity.RESULT_OK -> s = "Message Sent Successfully !!"
                SmsManager.RESULT_ERROR_GENERIC_FAILURE -> s = "Generic Failure Error"
                SmsManager.RESULT_ERROR_NO_SERVICE -> s = "Error : No Service Available"
                SmsManager.RESULT_ERROR_NULL_PDU -> s = "Error : Null PDU"
                SmsManager.RESULT_ERROR_RADIO_OFF -> s = "Error : Radio is off"
                else -> {
                }
            }
            //hideProgressHud()
            //messageEditText.setText("")
            //message_status_text_view.setText(s)

        }
    }

    private val deliveredStatusReceiver = object : BroadcastReceiver() {
        override fun onReceive(arg0: Context, arg1: Intent) {
            var s = "Message Not Delivered"
            when (resultCode) {
                Activity.RESULT_OK -> s = "Message Delivered Successfully"
                Activity.RESULT_CANCELED -> {
                }
            }
            //delivery_status_text_view.setText(s)
            //phone_number_edit_text.setText("")
            //message_edit_text.setText("")

            if(s.contains("Successfully")){
                onMessageToas("Mensage enviado correctamente",R.color.green)
                messageEditText.setText("")
            }else{
                onMessageToas("Mensage no enviado",R.color.red_900)
            }

            hideProgressHud()
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


    //endregion



    //region IMPLEMNTS INTERFACE CHAT SMS

    override fun showProgress() {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun hideProgress() {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun validarSendSms(): Boolean {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun requestResponseOK() {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun requestResponseError(error: String?) {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun onMessageOk(colorPrimary: Int, msg: String?) {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun onMessageError(colorPrimary: Int, msg: String?) {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun onEventBroadcastReceiver(extras: Bundle, intent: Intent) {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }




    //endregion






    public override fun onResume() {
        super.onResume()
        registerReceiver(sentStatusReceiver, IntentFilter(Const.SERVICE_SMS_SENT))
        registerReceiver(deliveredStatusReceiver, IntentFilter(Const.SERVICE_SMS_DELIVERED))
        registerReceiver(mNotificationReceiver, IntentFilter(Const.SERVICE_RECYVE_MESSAGE))
    }


     fun onMessageToas(message:String,color:Int){
        val inflater = this.layoutInflater
        var viewToast = inflater.inflate(R.layout.custom_message_toast, null)
        viewToast.txtMessageToastCustom.setText(message)
        viewToast.contetnToast.setBackgroundColor(ContextCompat.getColor(this, color))
        var mytoast =  Toast(this);
        mytoast.setView(viewToast);
        mytoast.setDuration(Toast.LENGTH_LONG);
        mytoast.show();
        ///onMessageError(R.color.red_900, getString(R.string.disabledGPS))
    }

    public override fun onPause() {
        super.onPause()
        unregisterReceiver(sentStatusReceiver)
        unregisterReceiver(deliveredStatusReceiver)
        unregisterReceiver(this.mNotificationReceiver);
    }
}
