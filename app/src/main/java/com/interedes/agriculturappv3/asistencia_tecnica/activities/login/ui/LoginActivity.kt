package com.interedes.agriculturappv3.asistencia_tecnica.activities.login.ui

import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.support.design.widget.Snackbar
import android.view.View
import com.interedes.agriculturappv3.R
import kotlinx.android.synthetic.main.activity_login.*

class LoginActivity : AppCompatActivity(), LoginView, View.OnClickListener {


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
                ingresar()
            }

        }
    }
    //endregion

    /*override fun validarCampos(): Boolean? {

    }*/

    override fun ingresar() {

    }

    override fun errorIngresar(error: String?) {

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
    /*
    private fun ingresar() {
        progressBar.visibility = View.VISIBLE
        startActivity(Intent(this, MenuMainActivity::class.java))
    }*/
    //endregion

    //region Métodos Generales
    private fun setInputs(b: Boolean) {
        textInputLayoutCorreo?.isEnabled = b
        textInputLayoutContrasena?.isEnabled = b

    }
    //endregion
}
