package com.interedes.agriculturappv3.activities.chat.chat_sms.user_sms_ui

import android.Manifest
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Color
import android.net.Uri
import android.os.Build
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.preference.PreferenceManager
import android.provider.Settings
import android.support.design.widget.Snackbar
import android.support.v4.app.ActivityCompat
import android.support.v4.app.NavUtils
import android.support.v4.app.TaskStackBuilder
import android.support.v4.content.ContextCompat
import android.support.v4.widget.SwipeRefreshLayout
import android.support.v7.app.AlertDialog
import android.support.v7.widget.LinearLayoutManager
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.TextView
import android.widget.Toast
import com.interedes.agriculturappv3.R
import com.interedes.agriculturappv3.activities.chat.chat_sms.detail_sms_user.Chat_Sms_Activity
import com.interedes.agriculturappv3.activities.chat.chat_sms.user_sms_ui.adapter.SmsUserAdapter
import com.interedes.agriculturappv3.modules.models.sms.Sms
import com.interedes.agriculturappv3.services.resources.Const_Resources
import com.interedes.agriculturappv3.services.resources.TagSmsResources
import com.kaopiz.kprogresshud.KProgressHUD
import kotlinx.android.synthetic.main.activity_user_sms.*
import kotlinx.android.synthetic.main.content_list_recycler_view.*
import kotlinx.android.synthetic.main.custom_message_toast.view.*
import java.util.*

class UserSmsActivity : AppCompatActivity(), SwipeRefreshLayout.OnRefreshListener, IMainViewUserSms.MainView {

    //Permission
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

    var presenter: IMainViewUserSms.Presenter? = null


    private var adapter: SmsUserAdapter? = null

    //Progress
    private var hud: KProgressHUD?=null

    companion object {
        lateinit var instance: UserSmsActivity
    }

    init {
        instance = this
        presenter = UserSms_Presenter(this)
        presenter?.onCreate()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_user_sms)
        setToolbarInjection()
        initAdapter()
        swipeRefreshLayout.setOnRefreshListener(this)
        PreferenceManager.setDefaultValues(this, R.xml.settings, false)
        //Start the service to display notifications

        if (Build.VERSION.SDK_INT >= 23) {
            if (!hasPermissions(applicationContext, *PERMISSIONS)) {
                requestPermission()
            } else {
                //Helper function
                presenter?.getListSms(this,null)
            }
        } else {
            //Helper function
            presenter?.getListSms(this,null)
        }
    }

    private fun initAdapter() {
        recyclerView?.layoutManager = LinearLayoutManager(this)
        adapter = SmsUserAdapter(ArrayList<Sms>())
        recyclerView?.adapter = adapter
    }

    private fun setToolbarInjection() {
        setSupportActionBar(toolbar)
        supportActionBar!!.setDisplayShowTitleEnabled(false)
        if (supportActionBar != null)
        // Habilitar Up Button
            supportActionBar!!.setDisplayHomeAsUpEnabled(true)
        toolbar.title = getString(R.string.title_sms_text)



    }


    override fun addContact(sms:Sms){


        val phoneNo=sms.Address?.toLong()

        val dial = "tel:$phoneNo"
        startActivity(Intent(Intent.ACTION_DIAL, Uri.parse(dial)))
    }

     override fun setListSms(sms: List<Sms>) {
        adapter?.clear()
        adapter?.setItems(sms)
        setResults(sms.size)
    }

     fun setResults(count: Int) {
        var results = String.format(getString(R.string.results_global_search),
                count);
        txtResults.setText(results);
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
                    presenter?.getListSms(this,null)
                } else if (grantResults[0] == PackageManager.PERMISSION_DENIED) {

                    if (android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.M){
                        if (shouldShowRequestPermissionRationale(Manifest.permission.SEND_SMS) || shouldShowRequestPermissionRationale(Manifest.permission.READ_SMS) || shouldShowRequestPermissionRationale(Manifest.permission.RECEIVE_SMS)) {
                            Toast.makeText(this,
                                    "Permiso denegado", Toast.LENGTH_LONG).show()
                        }
                        else{
                            if (hasPermissions(this, *PERMISSIONS)) {
                                presenter?.getListSms(this,null)
                            } else {
                                val builder = AlertDialog.Builder(this)
                                builder.setMessage("Permission Granted, Now you can access sms")
                                        .setPositiveButton("Aceptar") { dialog, id ->
                                            val intent = Intent()
                                            intent.setAction(Settings.ACTION_APPLICATION_DETAILS_SETTINGS)
                                            val uri = Uri.fromParts("package", Const_Resources.PAKAGE, null)
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

    //region IMPLEMNTS INTERFACE CHAT SMS

    override fun showProgress() {
        swipeRefreshLayout.isRefreshing=true
    }

    override fun hideProgress() {
        swipeRefreshLayout.isRefreshing=false
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
        onMessageError(R.color.red_900, error)
    }


    override fun validarSendSms(): Boolean {
        return false
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
                presenter?.getListSms(this,null)
            }
        }
    }

    override fun navigateDetailSms(sms: Sms) {

        val goToUpdate = Intent(this, Chat_Sms_Activity::class.java)
        goToUpdate.putExtra(TagSmsResources.TAG_SMS, sms)
        goToUpdate.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
        //goToUpdate.flags = Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK
        startActivity(goToUpdate)
    }

    override fun limpiarCampos() {

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

    //endregion


    //region OVERIDES METHODS
    override fun onResume() {
        super.onResume()
        presenter?.onResume(this)
    }

    override fun onPause() {
        super.onPause()
        presenter?.onPause(this)

    }

    override fun onRefresh() {
        swipeRefreshLayout.isRefreshing=true
        presenter?.getListSms(this,null)
    }

    //endregion
}
