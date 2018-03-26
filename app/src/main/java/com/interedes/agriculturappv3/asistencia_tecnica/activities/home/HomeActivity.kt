package com.interedes.agriculturappv3.asistencia_tecnica.activities.home

import android.content.Intent
import android.graphics.Color
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.support.design.widget.Snackbar
import android.support.v4.content.ContextCompat
import android.view.View
import com.interedes.agriculturappv3.R
import com.interedes.agriculturappv3.asistencia_tecnica.activities.login.ui.LoginActivity
import com.interedes.agriculturappv3.asistencia_tecnica.activities.registration.register_rol.RegisterRolActivity
import com.interedes.agriculturappv3.asistencia_tecnica.models.usuario.Usuario
import com.interedes.agriculturappv3.asistencia_tecnica.models.usuario.Usuario_Table
import com.interedes.agriculturappv3.asistencia_tecnica.modules.ui.main_menu.MenuMainActivity
import com.raizlabs.android.dbflow.sql.language.SQLite
import kotlinx.android.synthetic.main.activity_home.*
import com.afollestad.materialdialogs.DialogAction
import com.afollestad.materialdialogs.GravityEnum
import com.afollestad.materialdialogs.MaterialDialog
import com.afollestad.materialdialogs.Theme
import android.support.v7.widget.GridLayoutManager
import com.interedes.agriculturappv3.asistencia_tecnica.models.TipoProducto
import com.interedes.agriculturappv3.asistencia_tecnica.modules.asistencia_tecnica_module.plagas.adapters.TipoProductosAdapter
import com.interedes.agriculturappv3.services.listas.Listas
import kotlinx.android.synthetic.main.reciclerview_dialog.view.*
import android.view.WindowManager




class HomeActivity : AppCompatActivity(), View.OnClickListener {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_home)
        goToMainActivity()
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
    private fun goToMainActivity() {
        if (getLastUserLogued() != null) {
            val i = Intent(this, MenuMainActivity::class.java)
            i.flags = Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK
            startActivity(i)
        }
    }

    private fun getLastUserLogued(): Usuario? {
        val usuarioLogued = SQLite.select().from(Usuario::class.java).where(Usuario_Table.UsuarioRemembered?.eq(true)).querySingle()
        return usuarioLogued
    }

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
