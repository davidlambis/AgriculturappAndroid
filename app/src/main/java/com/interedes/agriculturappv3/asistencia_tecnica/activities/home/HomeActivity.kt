package com.interedes.agriculturappv3.asistencia_tecnica.activities.home

import android.content.Intent
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.support.design.widget.Snackbar
import android.support.v4.content.ContextCompat
import android.view.View
import com.interedes.agriculturappv3.R
import com.interedes.agriculturappv3.asistencia_tecnica.activities.login.ui.LoginActivity
import com.interedes.agriculturappv3.asistencia_tecnica.activities.registration.register_rol.RegisterRolActivity
import kotlinx.android.synthetic.main.activity_home.*

class HomeActivity : AppCompatActivity(), View.OnClickListener {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_home)
// prueba
        linearLayoutIngresar?.setOnClickListener(this)
        linearLayoutRegistrar?.setOnClickListener(this)
        linearLayoutContactanos?.setOnClickListener(this)

    }

    //region Método Click
    override fun onClick(p0: View?) {
        when (p0?.id) {
            R.id.linearLayoutIngresar -> {
                ingresar()
            }
            R.id.linearLayoutRegistrar -> {
                registrarse()
            }
            R.id.linearLayoutContactanos -> {
                contactarnos()
            }
        }
    }

    //endregion

    //region Métodos
    private fun ingresar() {
        imageViewIngresar?.setColorFilter(ContextCompat.getColor(this, R.color.colorPrimary))
        startActivity(Intent(this, LoginActivity::class.java))
    }

    private fun registrarse() {
        imageViewRegistrarse?.setColorFilter(ContextCompat.getColor(this, R.color.colorPrimary))
        startActivity(Intent(this, RegisterRolActivity::class.java))
    }

    private fun contactarnos() {
        imageViewContactanos?.setColorFilter(ContextCompat.getColor(this, R.color.colorPrimary))
        Snackbar.make(container, getString(R.string.title_contactanos), Snackbar.LENGTH_SHORT).show()
    }

    private fun limpiarCambios() {
        imageViewIngresar?.setColorFilter(ContextCompat.getColor(this, R.color.grey_luiyi))
        imageViewRegistrarse?.setColorFilter(ContextCompat.getColor(this, R.color.grey_luiyi))
        imageViewContactanos?.setColorFilter(ContextCompat.getColor(this, R.color.grey_luiyi))
    }
    //endregion

    //region Ciclo de Vida Actividad
    override fun onResume() {
        super.onResume()
        limpiarCambios()
    }
}
