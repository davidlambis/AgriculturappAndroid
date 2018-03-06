package com.interedes.agriculturappv3.asistencia_tecnica.activities.registration.register_rol

import android.content.Intent
import android.os.Build
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.support.v4.app.NavUtils
import android.support.v4.app.TaskStackBuilder
import android.support.v4.content.ContextCompat
import android.support.v7.widget.GridLayoutManager
import android.view.View
import com.interedes.agriculturappv3.R
import com.interedes.agriculturappv3.asistencia_tecnica.activities.registration.register_user.ui.RegisterUserActivity
import com.interedes.agriculturappv3.asistencia_tecnica.activities.registration.register_rol.adapters.RegisterRolAdapter
import com.interedes.agriculturappv3.asistencia_tecnica.services.listas.Listas
import kotlinx.android.synthetic.main.activity_register_rol.*
import kotlinx.android.synthetic.main.content_recyclerview.*

class RegisterRolActivity : AppCompatActivity(), RegisterRolView, View.OnClickListener {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_register_rol)
        loadRoles()
        imageViewBackButton?.setOnClickListener(this)
    }

    //region Métodos Interfaz
    override fun loadRoles() {
        recyclerView?.layoutManager = GridLayoutManager(this, 2)
        val lista = Listas.listaRoles()
        //Dentro ejecuta la acción del click en base a la position seleccionada
        val adapter = RegisterRolAdapter(lista) { position ->
            if (lista[position].Nombre.equals("Productor")) {
                val i = Intent(this, RegisterUserActivity::class.java)
                i.putExtra("rol", "productor")
                startActivity(i)
            } else if (lista[position].Nombre.equals("Comprador")) {
                val j = Intent(this, RegisterUserActivity::class.java)
                j.putExtra("rol", "comprador")
                startActivity(j)
            }
        }
        recyclerView?.adapter = adapter
    }

    override fun navigateToParentActivity() {
        imageViewBackButton?.setColorFilter(ContextCompat.getColor(this.applicationContext, R.color.colorPrimary))
        returnToParentActivity()
    }

    override fun limpiarCambios() {
        imageViewBackButton?.setColorFilter(ContextCompat.getColor(this.applicationContext, R.color.grey_luiyi))
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
