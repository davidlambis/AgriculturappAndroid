package com.interedes.agriculturappv3.activities.reset_password

import android.content.Intent
import android.content.IntentFilter
import android.graphics.Color
import android.graphics.drawable.AnimationDrawable
import android.net.ConnectivityManager
import android.os.Build
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.support.design.widget.Snackbar
import android.support.v4.app.NavUtils
import android.support.v4.app.TaskStackBuilder
import android.support.v4.content.ContextCompat
import android.text.TextUtils
import android.util.TypedValue
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import com.interedes.agriculturappv3.AgriculturApplication
import com.interedes.agriculturappv3.R
import com.interedes.agriculturappv3.modules.models.ResetPassword
import com.interedes.agriculturappv3.services.api.ApiInterface
import com.interedes.agriculturappv3.services.internet_connection.ConnectivityReceiver
import com.kaopiz.kprogresshud.KProgressHUD
import kotlinx.android.synthetic.main.activity_reset_password.*
import kotlinx.android.synthetic.main.custom_message_toast.view.*
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class ResetPasswordActivity : AppCompatActivity(), View.OnClickListener, ConnectivityReceiver.connectivityReceiverListener {

    var connectivityReceiver: ConnectivityReceiver? = null
    private var hud: KProgressHUD? = null
    init {
        connectivityReceiver = ConnectivityReceiver()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_reset_password)

        btnResetPassword.setOnClickListener(this)
        ivBackButton.setOnClickListener(this)
        registerReceiver(connectivityReceiver, IntentFilter(ConnectivityManager.CONNECTIVITY_ACTION))
        AgriculturApplication.instance.setConnectivityListener(this)
    }


    fun onMessageOk(colorPrimary: Int, message: String?) {
        val color = Color.WHITE
        val snackbar = Snackbar
                .make(container, message!!, Snackbar.LENGTH_LONG)
        val sbView = snackbar.view
        sbView.setBackgroundColor(ContextCompat.getColor(applicationContext, colorPrimary))
        val textView = sbView.findViewById<View>(android.support.design.R.id.snackbar_text) as TextView
        textView.setCompoundDrawablesWithIntrinsicBounds(R.drawable.ic_stat_agrapp_icon_notificacion, 0, 0, 0)
        // textView.setCompoundDrawablePadding(getResources().getDimensionPixelOffset(R.dimen.activity_horizontal_margin));
        textView.setTextColor(color)
        textView.setTextSize(TypedValue.COMPLEX_UNIT_PX,
                getResources().getDimension(R.dimen.text_size_16))
        snackbar.show()
    }

    override fun onClick(p0: View?) {
        when (p0?.id) {

            R.id.ivBackButton -> {
                navigateToParentActivity()
            }

            R.id.btnResetPassword -> {
                 if (edtCorreo?.text.toString().isEmpty()) {
                     edtCorreo?.setError(getString(R.string.error_field_required))
                 }else if(!isValidEmail(edtCorreo.text.trim().toString())){
                     edtCorreo?.setError(getString(R.string.edit_text_error_correo))
                 }
                 else {
                     val conection= checkConnection()
                     if(conection){
                         showProgress()
                         resetPassword(edtCorreo?.text.toString())
                     }else{
                         onMessageOk(R.color.red_900,getString(R.string.verifcate_conection_info))
                     }
                 }
            }
        }
    }

    fun isValidEmail(target: CharSequence): Boolean {
        return !TextUtils.isEmpty(target) && android.util.Patterns.EMAIL_ADDRESS.matcher(target).matches()
    }

    fun showProgress() {

        hud = KProgressHUD.create(this)
                .setStyle(KProgressHUD.Style.SPIN_INDETERMINATE)
                .setWindowColor(getResources().getColor(R.color.colorPrimary))
                .setLabel("Enviando...", resources.getColor(R.color.white));
        hud?.show()
    }

    fun hideProgress() {
        hud?.dismiss()
    }

    private fun resetPassword(correo:String) {
        val resetPassword = ResetPassword(correo)
        val apiService = ApiInterface.create()
        val call = apiService.resetPassword(resetPassword)
        call.enqueue(object : Callback<ResetPassword> {
            override fun onResponse(call: Call<ResetPassword>?, response: Response<ResetPassword>?) {
                if (response != null && response.code() == 204) {
                    onMessageOk(R.color.green_900,getString(R.string.send_email_reset_password_ok))
                    hideProgress()
                } else {
                    onMessageOk(R.color.red_900,getString(R.string.error_request_reset_password))
                    hideProgress()
                }
            }
            override fun onFailure(call: Call<ResetPassword>?, t: Throwable?) {
                onMessageOk(R.color.red_900,getString(R.string.error_request_reset_password))
                hideProgress()
            }
        })
    }

    fun navigateToParentActivity() {
         ivBackButton?.setColorFilter(ContextCompat.getColor(this.applicationContext, R.color.colorPrimary))
         returnToParentActivity()
    }

    //region menu
    private fun returnToParentActivity() {
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
        }

        //Para versiones anterios a 5.x
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.LOLLIPOP) {
            // Terminar con el método correspondiente para Android 5.x
            onBackPressed()
        }
    }

    //endregion

    //Revisar manualmente
    private fun checkConnection(): Boolean {
        return ConnectivityReceiver.isConnected
        //showSnack(isConnected);
    }

    override fun onNetworkConnectionChanged(isConnected: Boolean) {
        if (isConnected) {
            onMessageOk(R.color.colorPrimary, getString(R.string.internet_connected))
        } else {
            onMessageOk(R.color.grey_luiyi, getString(R.string.not_internet_connected))
        }
    }
    //endregion

    //region Métodos Ciclo de Vida Actividad
    override fun onResume() {
        super.onResume()
    }

    override fun onDestroy() {
        super.onDestroy()
        unregisterReceiver(connectivityReceiver)
    }
    //endregion
}
