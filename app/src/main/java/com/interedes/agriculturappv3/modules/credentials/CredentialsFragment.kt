package com.interedes.agriculturappv3.modules.credentials

import android.content.Context
import android.content.DialogInterface
import android.content.Intent
import android.graphics.Color
import android.net.Uri
import android.os.Bundle
import android.support.design.widget.Snackbar
import android.support.v4.app.Fragment
import android.support.v4.content.ContextCompat
import android.support.v7.app.AlertDialog
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.InputMethodManager
import android.widget.TextView

import com.interedes.agriculturappv3.R
import com.interedes.agriculturappv3.modules.models.usuario.RequestCredentials
import com.interedes.agriculturappv3.modules.productor.ui.main_menu.MenuMainActivity
import com.kaopiz.kprogresshud.KProgressHUD
import com.raizlabs.android.dbflow.kotlinextensions.save
import kotlinx.android.synthetic.main.activity_menu_main.*
import kotlinx.android.synthetic.main.fragment_credentials.*

class CredentialsFragment : Fragment(),IMainViewCredentials.MainView,View.OnClickListener {

    var presenter: IMainViewCredentials.Presenter? = null
    //Progress
    private var hud: KProgressHUD?=null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        presenter =  CredentialsPresenter(this)
        presenter?.onCreate()

    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_credentials, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        ivBackButton.setOnClickListener(this)
        btnSaveChangesAccount.setOnClickListener(this)
        (activity as MenuMainActivity).toolbar.title=getString(R.string.tittle_credentials)
    }

    //region IMPLEMNTS METHODS INTERFACE

    override fun showProgressHud(){
        hud = KProgressHUD.create(activity)
                .setStyle(KProgressHUD.Style.SPIN_INDETERMINATE)
                .setWindowColor(getResources().getColor(R.color.colorPrimary))
                .setLabel("Cargando...", resources.getColor(R.color.white))
        hud?.show()
    }

    override fun hideProgressHud(){
        hud?.dismiss()
    }


    override fun validarChangeCredentials(): Boolean {
        //val email_pattern = "[a-zA-Z0-9._-]+@[a-z]+\\.+[a-z]+"
        var cancel = false
        var focusView: View? = null

        if (edtOldContrasena?.text.toString().isEmpty()) {
            edtOldContrasena?.setError(getString(R.string.error_field_required))
            focusView = edtOldContrasena
            cancel = true
        }
        else if (edtContrasenaNew?.text.toString().isEmpty()) {
            edtContrasenaNew?.setError(getString(R.string.error_field_required))
            focusView = edtContrasenaNew
            cancel = true
        } else if (edtConfirmarContrasena?.text.toString().isEmpty()) {
            edtConfirmarContrasena?.setError(getString(R.string.error_field_required))
            focusView = edtConfirmarContrasena
            cancel = true
        }
        else if (!edtContrasenaNew?.text.toString().trim().equals(edtConfirmarContrasena.text.toString().trim())) {
            edtConfirmarContrasena?.setError(getString(R.string.edit_text_error_contrasenas))
            focusView = edtConfirmarContrasena
            cancel = true

        } else if (edtContrasenaNew.text.toString().trim().length < 6) {
            edtContrasenaNew?.setError(getString(R.string.edit_text_error_contrasena))
            focusView = edtContrasenaNew
            cancel = true
        } else if (!validatePassword(edtContrasenaNew.text.trim().toString())) {
            edtContrasenaNew?.setError(getString(R.string.edit_text_pattern_contrasena))
            focusView = edtContrasenaNew
            cancel = true
        }

        if (cancel) {
            focusView?.requestFocus()
        } else {
            return true
        }
        return false
    }

    fun  hideKeyboard() {
        val inputManager =  activity!!.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager;
        try {
            inputManager.hideSoftInputFromWindow(activity!!.getCurrentFocus().getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);
        } catch (npe:NullPointerException ) {
            Log.e(activity!!.getLocalClassName(), Log.getStackTraceString(npe));
        }
    }

    override fun limpiarCampos(){
        edtOldContrasena?.setText("")
        edtContrasenaNew?.setText("")
        edtConfirmarContrasena?.setText("")
    }

    fun validatePassword(password: String?): Boolean {
        if (password == null) {
            return false
        }
        var upper = false
        var lower = false
        var digit = false
        var symbol = false
        for (ch in password) {
            if (Character.isUpperCase(ch)) {
                upper = true
            } else if (Character.isLowerCase(ch)) {
                lower = true
            } else if (Character.isDigit(ch)) {
                digit = true
            } else { // or some symbol test.
                symbol = true
            }
            // This short-circuits the rest of the loop when all criteria are true.
            if (upper && lower && digit && symbol) {
                return true
            }
        }
        return upper && lower && digit && symbol
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
                .make(container_fragment, message!!, Snackbar.LENGTH_LONG)
        val sbView = snackbar.view
        sbView.setBackgroundColor(ContextCompat.getColor(activity!!.applicationContext, colorPrimary))
        val textView = sbView.findViewById<View>(android.support.design.R.id.snackbar_text) as TextView
        textView.setCompoundDrawablesWithIntrinsicBounds(R.drawable.quantum_ic_cast_connected_white_24, 0, 0, 0)
        // textView.setCompoundDrawablePadding(getResources().getDimensionPixelOffset(R.dimen.activity_horizontal_margin));
        textView.setTextColor(color)
        snackbar.show()
    }

    override fun onMessageError(colorPrimary: Int, message: String?) {
        onMessageOk(colorPrimary, message)
    }
    override fun verificateConnection(): AlertDialog? {
        val builder = AlertDialog.Builder(context!!)
        builder.setTitle(getString(R.string.alert));
        builder.setMessage(getString(R.string.verificate_conexion));
        builder.setPositiveButton(getString(R.string.confirm), DialogInterface.OnClickListener { dialog, which ->
            dialog.dismiss()
        })
        builder.setIcon(R.drawable.ic_action_menu_asistencia_tecnica);
        return builder.show();
    }

    override fun onEventBroadcastReceiver(extras: Bundle, intent: Intent) {
        if(extras!=null){
            if (extras.containsKey("state_conectivity")) {
                var state_conectivity = intent.extras!!.getBoolean("state_conectivity")
            }
        }
    }


    //endregion

    //region EVENTS
    override fun onClick(v: View?) {
        when (v?.id) {
            R.id.ivBackButton -> {
                ivBackButton.setColorFilter(ContextCompat.getColor(activity!!.applicationContext, R.color.colorPrimary))
                hideKeyboard()
                (activity as MenuMainActivity).onBackPressed()
            }

            R.id.btnSaveChangesAccount->{
                hideKeyboard()
                if(presenter?.validarChangeCredentials()!!){
                    val oldPassword= edtOldContrasena.text.toString()
                    val newPassword= edtContrasenaNew.text.toString()

                    val credentials= RequestCredentials(oldPassword,newPassword)
                    presenter?.updateCredentialsUserLogued(credentials)
                }
            }
        }
    }

    //endregion

}
