package com.interedes.agriculturappv3.asistencia_tecnica.activities.login.ui

import android.content.Intent
import android.graphics.Color
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.support.design.widget.Snackbar
import android.support.v4.content.ContextCompat
import android.view.View
import android.widget.TextView
import com.interedes.agriculturappv3.R
import com.interedes.agriculturappv3.asistencia_tecnica.activities.login.presenter.LoginPresenter
import com.interedes.agriculturappv3.asistencia_tecnica.activities.login.presenter.LoginPresenterImpl
import com.interedes.agriculturappv3.asistencia_tecnica.modules.ui.main_menu.MenuMainActivity
import kotlinx.android.synthetic.main.activity_login.*

class LoginActivity : AppCompatActivity(), LoginView, View.OnClickListener {

    var camposValidados: Boolean? = false
    var presenter: LoginPresenter? = null


    init {
        presenter = LoginPresenterImpl(this)
        (presenter as LoginPresenterImpl).onCreate()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)
        fabLogin?.setOnClickListener(this)
        if (intent.extras != null && intent.extras["correo"].equals(getString(R.string.snackbar_verificacion_correo))) {
            Snackbar.make(container, getString(R.string.snackbar_verificacion_correo), Snackbar.LENGTH_LONG).show()
        }
    }


    //región Click
    override fun onClick(p0: View?) {
        when (p0?.id) {
            R.id.fabLogin -> {
                ingresar(edtCorreo.text.toString(), edtContrasena.text.toString())
            }

        }
    }
    //endregion

    /*override fun validarCampos(): Boolean? {

    }*/

    override fun ingresar(email: String, password: String) {
        if (presenter?.validarCampos() == true) {
            //presenter?.ingresar(email, password)
            progressBar.visibility = View.VISIBLE
            startActivity(Intent(this, MenuMainActivity::class.java))
        }
    }

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

    override fun getUsuario() {

    }

    override fun errorIngresar(error: String?) {

    }

    override fun requestResponseOk() {

    }

    override fun requestResponseError(error: String?) {
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
        // textView.setCompoundDrawablePadding(getResources().getDimensionPixelOffset(R.dimen.activity_horizontal_margin));
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

    }

    //region Métodos Generales
    private fun setInputs(b: Boolean) {
        textInputLayoutCorreo?.isEnabled = b
        textInputLayoutContrasena?.isEnabled = b

    }
    //endregion
}
