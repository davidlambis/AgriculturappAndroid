package com.interedes.agriculturappv3.asistencia_tecnica.activities.login.ui

import android.content.Intent
import android.content.IntentFilter
import android.graphics.Color
import android.net.ConnectivityManager
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.support.design.widget.Snackbar
import android.support.v4.content.ContextCompat
import android.util.TypedValue
import android.view.View
import android.widget.TextView
import android.widget.Toast
import com.interedes.agriculturappv3.AgriculturApplication
import com.interedes.agriculturappv3.BuildConfig
import com.interedes.agriculturappv3.R
import com.interedes.agriculturappv3.R.id.textInputLayoutContrasena
import com.interedes.agriculturappv3.R.id.textInputLayoutCorreo
import com.interedes.agriculturappv3.asistencia_tecnica.activities.login.presenter.LoginPresenter
import com.interedes.agriculturappv3.asistencia_tecnica.activities.login.presenter.LoginPresenterImpl
import com.interedes.agriculturappv3.asistencia_tecnica.models.login.Login
import com.interedes.agriculturappv3.asistencia_tecnica.models.usuario.Usuario
import com.interedes.agriculturappv3.asistencia_tecnica.modules.ui.main_menu.MenuMainActivity
import com.interedes.agriculturappv3.services.internet_connection.ConnectivityReceiver
import kotlinx.android.synthetic.main.activity_login.*

class LoginActivity : AppCompatActivity(), LoginView, View.OnClickListener, ConnectivityReceiver.connectivityReceiverListener {

    var camposValidados: Boolean? = false
    var presenter: LoginPresenter? = null
    var connectivityReceiver: ConnectivityReceiver? = null

    companion object {
        //val em = BuildConfig.em
        //val ps = BuildConfig.ps
    }

    init {
        presenter = LoginPresenterImpl(this)
        (presenter as LoginPresenterImpl).onCreate()
        connectivityReceiver = ConnectivityReceiver()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)
        //Quitar Barra de Navegacion
        if (getSupportActionBar() != null)
            getSupportActionBar()?.hide();
        fabLogin?.setOnClickListener(this)
        if (intent.extras != null && intent.extras["correo"].equals(getString(R.string.snackbar_verificacion_correo))) {
            onMessageOk(R.color.colorPrimary, getString(R.string.snackbar_verificacion_correo))
        }
        //Registra al receiver de Internet
        registerInternetReceiver()
    }


    //región Click
    override fun onClick(p0: View?) {
        when (p0?.id) {
            R.id.fabLogin -> {
                ingresar()
            }

        }
    }
    //endregion

    //region Métodos Interfaz
    //Login
    override fun ingresar() {
        if (presenter?.validarCampos() == true) {
            /* val login = Login(edtCorreo?.text?.trim().toString(),
                     edtContrasena?.text?.trim()?.toString())
             presenter?.ingresar(login) */
            progressBar.visibility = View.VISIBLE
            startActivity(Intent(this, MenuMainActivity::class.java))
        }
    }

    //Validación de campos
    override fun validarCampos(): Boolean? {
        var cancel = false
        var focusView: View? = null
        if (edtCorreo?.text.toString().isEmpty()) {
            edtCorreo?.setError(getString(R.string.error_field_required))
            focusView = edtCorreo
            cancel = true
        } else if (edtContrasena?.text.toString().isEmpty()) {
            edtContrasena?.setError(getString(R.string.error_field_required))
            focusView = edtContrasena
            cancel = true
        }
        if (cancel) {
            focusView?.requestFocus()
        } else {
            camposValidados = true
        }
        return camposValidados
    }

    //Registra al receiver de Internet
    override fun registerInternetReceiver() {
        registerReceiver(connectivityReceiver, IntentFilter(ConnectivityManager.CONNECTIVITY_ACTION))
        AgriculturApplication.instance.setConnectivityListener(this)
    }

    //Cancela registro al receiver de Internet
    override fun unRegisterInternetReceiver() {
        unregisterReceiver(connectivityReceiver)
    }


    //Error al Ingresar
    override fun errorIngresar(error: String?) {
        onMessageError(R.color.grey_luiyi, error)
    }


    override fun onMessageOk(colorPrimary: Int, message: String?) {
        val color = Color.WHITE
        val snackbar = Snackbar
                .make(container, message!!, Snackbar.LENGTH_LONG)
        val sbView = snackbar.view
        sbView.setBackgroundColor(ContextCompat.getColor(applicationContext, colorPrimary))
        val textView = sbView.findViewById<View>(android.support.design.R.id.snackbar_text) as TextView
        textView.setCompoundDrawablesWithIntrinsicBounds(R.drawable.ic_productor, 0, 0, 0)

        textView.setTextSize(TypedValue.COMPLEX_UNIT_PX,
                getResources().getDimension(R.dimen.text_size_16))
        textView.setTextColor(color)
        snackbar.show()
    }

    override fun onMessageError(colorPrimary: Int, message: String?) {
        onMessageOk(colorPrimary, message)
    }

    override fun showProgress() {
        progressBar?.visibility = View.VISIBLE
    }

    override fun hideProgress() {
        progressBar?.visibility = View.GONE
    }

    override fun enableInputs() {
        setInputs(true)
    }

    override fun disableInputs() {
        setInputs(false)
    }

    override fun navigateToMainActivity() {
        val i = Intent(this, MenuMainActivity::class.java)
        i.flags = Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK
        startActivity(i)
    }

    //region Conexión a Internet
    override fun checkConnection(): Boolean? {
        return ConnectivityReceiver.isConnected
    }


    override fun onNetworkConnectionChanged(isConnected: Boolean) {
        if (isConnected) {
            onMessageOk(R.color.colorPrimary, getString(R.string.internet_connected))
        } else {
            onMessageError(R.color.grey_luiyi, getString(R.string.not_internet_connected))
        }
    }
    //endregion

    //region Métodos Generales
    private fun setInputs(b: Boolean) {
        textInputLayoutCorreo?.isEnabled = b
        textInputLayoutContrasena?.isEnabled = b
    }
    //endregion

    //region Ciclo de Vida Actividad
    override fun onDestroy() {
        super.onDestroy()
        unregisterReceiver(connectivityReceiver)
        presenter?.onDestroy()
    }

    //endregion
}
