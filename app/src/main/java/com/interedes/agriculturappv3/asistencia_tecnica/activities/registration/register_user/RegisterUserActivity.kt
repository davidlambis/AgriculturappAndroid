package com.interedes.agriculturappv3.asistencia_tecnica.activities.registration.register_user

import android.content.Intent
import android.os.Build
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.support.v4.app.NavUtils
import android.support.v4.app.TaskStackBuilder
import android.support.v7.widget.GridLayoutManager
import android.view.View
import android.widget.Toast
import com.interedes.agriculturappv3.R
import com.interedes.agriculturappv3.asistencia_tecnica.activities.registration.register_productor_comprador.RegisterProductorCompradorActivity
import com.interedes.agriculturappv3.asistencia_tecnica.activities.registration.register_user.adapters.RegisterUserAdapter
import com.interedes.agriculturappv3.asistencia_tecnica.modules.main_menu.MenuMainActivity
import com.interedes.agriculturappv3.asistencia_tecnica.services.listas.Listas
import kotlinx.android.synthetic.main.activity_register_user.*
import kotlinx.android.synthetic.main.content_recyclerview.*

class RegisterUserActivity : AppCompatActivity(), RegisterUserView, View.OnClickListener {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_register_user)
        loadRoles()
        imageViewBackButton?.setOnClickListener(this)
    }

    //region Métodos Interfaz
    override fun loadRoles() {
        recyclerView?.layoutManager = GridLayoutManager(this, 2)
        val lista = Listas.listaRoles()
        //Dentro ejecuta la acción del click en base a la position seleccionada
        val adapter = RegisterUserAdapter(lista) { position ->
            if (lista[position].Nombre.equals("Productor")) {
                startActivity(Intent(this, RegisterProductorCompradorActivity::class.java))
            } else if (lista[position].Nombre.equals("Comprador")) {
                Toast.makeText(this, "Go to Comprador", Toast.LENGTH_SHORT).show()
            }
        }
        recyclerView?.adapter = adapter
    }

    override fun navigateToParentActivity() {
        imageViewBackButton?.setColorFilter(resources.getColor(R.color.colorPrimary))
        returnToParentActivity()
    }

    override fun limpiarCambios() {
        imageViewBackButton?.setColorFilter(resources.getColor(R.color.grey_luiyi))
    }
    //endregion

    //region Método Click
    override fun onClick(p0: View?) {
        when (p0?.id) {
            R.id.imageViewBackButton -> {
                navigateToParentActivity()
            }
        }

    }
    //endregion

    //region Métodos Generales
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

    //region Métodos Ciclo de Vida Actividad
    override fun onResume() {
        super.onResume()
        limpiarCambios()
    }
    //endregion
}
