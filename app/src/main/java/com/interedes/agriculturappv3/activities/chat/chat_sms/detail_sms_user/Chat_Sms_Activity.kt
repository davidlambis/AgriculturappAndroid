package com.interedes.agriculturappv3.activities.chat.chat_sms.detail_sms_user

import android.Manifest
import android.content.*
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import com.interedes.agriculturappv3.R
import android.view.View
import android.content.pm.PackageManager
import android.graphics.Color
import android.net.Uri
import android.os.Build
import android.provider.Settings
import android.support.design.widget.Snackbar
import android.support.v4.app.ActivityCompat
import android.support.v4.app.NavUtils
import android.support.v4.app.TaskStackBuilder
import android.support.v4.content.ContextCompat
import android.support.v7.app.AlertDialog
import android.support.v7.widget.LinearLayoutManager
import android.view.MenuItem
import android.widget.TextView
import android.widget.Toast
import com.interedes.agriculturappv3.activities.chat.chat_sms.detail_sms_user.adapter.SmsAdapter
import com.interedes.agriculturappv3.modules.models.sms.Sms
import com.interedes.agriculturappv3.services.Const
import com.interedes.agriculturappv3.services.resources.EmisorType_Message_Resources
import com.interedes.agriculturappv3.services.resources.MessageSmsType
import com.interedes.agriculturappv3.services.resources.TagSmsResources
import com.kaopiz.kprogresshud.KProgressHUD
import kotlinx.android.synthetic.main.activity_chat__sms_.*
import kotlinx.android.synthetic.main.custom_message_toast.view.*
import java.util.*


class Chat_Sms_Activity : AppCompatActivity(),View.OnClickListener, IMainViewDetailSms.MainView {



    var contactUserName:String?=""
    var contactNumber:String?=""
    var sms:Sms?=null

    var presenter: IMainViewDetailSms.Presenter? = null


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

    private var adapter: SmsAdapter? = null


    companion object {
        var instance: Chat_Sms_Activity?=null
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
        if (intent.extras.containsKey(TagSmsResources.TAG_SMS)) {
            sms = intent.getParcelableExtra(TagSmsResources.TAG_SMS)
            contactNumber=sms?.Address
            contactUserName=sms?.ContactName

            getListSms(false)
        }else if(intent.extras.containsKey(TagSmsResources.PHONE_NUMBER)){
            sms=Sms()
            sms?.Address=intent.getStringExtra(TagSmsResources.PHONE_NUMBER)
            sms?.ContactName=intent.getStringExtra(TagSmsResources.CONTACT_NAME)
            contactNumber=sms?.Address
            contactUserName=sms?.ContactName
            getListSms(true)
        }

        nameUserTo.setText(contactUserName)
        adressUserTo.setText(contactNumber)
        sendMessageImagebutton.setOnClickListener(this)
    }

    override fun getListSms(eventClickNotification: Boolean) {
        if (Build.VERSION.SDK_INT >= 23) {
            if (!hasPermissions(applicationContext, *PERMISSIONS)) {
                requestPermission()
            } else {
                //Helper function
                presenter?.getListSms(this,sms,eventClickNotification)
                ///refreshSmsInbox()
            }
        } else {
            presenter?.getListSms(this,sms,eventClickNotification)
            //Helper function
            /// refreshSmsInbox()
        }

    }




    private fun initAdapter() {
        messagesRecyclerView?.layoutManager = LinearLayoutManager(this)
        adapter = SmsAdapter(ArrayList<Sms>())
        messagesRecyclerView?.adapter = adapter
    }

    private fun setToolbarInjection() {
        setSupportActionBar(toolbar)
        supportActionBar!!.setDisplayShowTitleEnabled(false)
        if (supportActionBar != null)
        // Habilitar Up Button
            supportActionBar!!.setDisplayHomeAsUpEnabled(true)
        toolbar.title = getString(R.string.title_sms_text)
    }


    protected fun sendSMS() {
        val sms=Sms(0,
                "",
                sms?.Address,
                messageEditText.getText().toString(),
                "",
                System.currentTimeMillis().toString(),
                MessageSmsType.MESSAGE_TYPE_SENT,
                EmisorType_Message_Resources.MESSAGE_EMISOR_TYPE_SMS,
                sms?.ContactName
        )
        presenter?.saveSms(this,sms)
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






    //region IMPLEMNTS INTERFACE CHAT SMS

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

    override fun showProgress() {

    }

    override fun hideProgress() {

    }

    override fun validarSendSms(): Boolean {
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
            return true
        }

        return false
    }


    override fun requestResponseOK() {

        onMessageOk(R.color.colorPrimary,getString(R.string.request_ok));
    }

    override fun requestResponseError(error: String?) {
        onMessageError(R.color.red_900, error)
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
            if (extras.containsKey("new_message_sms")) {
                var new_sms = intent.extras!!.getString("new_message_sms")
                onMessageToas(new_sms,R.color.green_900)
                presenter?.getListSms(this,sms,true)
            }
        }
    }

    override fun limpiarCampos() {
        messageEditText.setText("")
    }
    override fun setListSmsDetaiil(sms: List<Sms>) {
        adapter?.clear()
        adapter?.setItems(sms)
        messagesRecyclerView.scrollToPosition(sms.size-1);
        //  setResults(sms.size)
    }
    override fun onMessageToas(message:String,color:Int){
        val inflater = this.layoutInflater
        var viewToast = inflater.inflate(R.layout.custom_message_toast, null)
        viewToast.txtMessageToastCustom.setText(message)
        viewToast.contetnToast.setBackgroundColor(ContextCompat.getColor(this, color))
        var mytoast =  Toast(this);
        mytoast.setView(viewToast);
        mytoast.setDuration(Toast.LENGTH_SHORT);
        mytoast.show();
        ///onMessageError(R.color.red_900, getString(R.string.disabledGPS))
    }

    //endregion


    //region EVENTS

    override fun onClick(p0: View?) {
        when (p0?.id) {

            R.id.sendMessageImagebutton -> {
                if(presenter?.validarSendSms()==true){
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


    //This method handles user clicks on the menu option buttons.
    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        when (item.itemId) {
        ///Metodo que permite no recargar la pagina al devolverse
            android.R.id.home -> {
               // Obtener intent de la actividad padre
                var upIntent = NavUtils.getParentActivityIntent(this);
                upIntent?.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);

                // Comprobar si DetailActivity no se creó desde CourseActivity
                if (NavUtils.shouldUpRecreateTask(this, upIntent!!)
                        || this.isTaskRoot()) {

                    // Construir de nuevo la tarea para ligar ambas actividades
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
                        TaskStackBuilder.create(this)
                                .addNextIntentWithParentStack(upIntent)
                                .startActivities();
                    }
                }

                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                    // Terminar con el método correspondiente para Android 5.x
                    this.finishAfterTransition();
                    return true;
                }

                //Para versiones anterios a 5.x
                if (Build.VERSION.SDK_INT < Build.VERSION_CODES.LOLLIPOP) {
                    // Terminar con el método correspondiente para Android 5.x
                    onBackPressed();
                    return true;
                }
            }
            else -> {
            }
        }
        return super.onOptionsItemSelected(item)
    }


    //endregion


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
        ActivityCompat.requestPermissions(this, PERMISSIONS, PERMISSION_REQUEST_CODE);
        //ContextCompat.requestPermissions(activity!!, PERMISSIONS, PERMISSION_ALL)
        // requestPermissions(PERMISSIONS, PERMISSION_ALL)
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        when (requestCode) {
            PERMISSION_REQUEST_CODE ->
                if (grantResults.size > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    //Toast.makeText(applicationContext, "Permission Granted, Now you can access sms", Toast.LENGTH_SHORT).show()
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
                                            val uri = Uri.fromParts("package", Const.PAKAGE, null)
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

    //endregion




    //region OVERRIDE METHODS

    override fun onResume() {
        super.onResume()
        presenter?.onResume(this)
    }

    override fun onPause() {
        super.onPause()
        presenter?.onPause(this)

    }
    //endregion
}
