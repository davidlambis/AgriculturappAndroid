package com.interedes.agriculturappv3.asistencia_tecnica.registration.register_productor_comprador

import android.content.Intent
import android.os.Build
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.support.design.widget.Snackbar
import android.support.v4.app.NavUtils
import android.support.v4.app.TaskStackBuilder
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import com.interedes.agriculturappv3.R
import kotlinx.android.synthetic.main.activity_register_productor_comprador.*
import java.util.*

class RegisterProductorCompradorActivity : AppCompatActivity(), RegisterProductorCompradorView, View.OnClickListener {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_register_productor_comprador)
        loadInfo()
        ivBackButtonRegisterProductor?.setOnClickListener(this)
        btnRegistrarProductor?.setOnClickListener(this)
    }

    //region Métodos Interfaz
    override fun loadInfo() {
        //Carga de Spinners de método de pago y despliegue de spinner dependientes de seleccionar bancco y número de cuenta.
        val itemsMetodoPago = arrayOf("Transferencia Bancaria", "Efectivo", "Otros")
        val metodoPagoList = ArrayList<String>()
        metodoPagoList.addAll(Arrays.asList(*itemsMetodoPago))
        spinnerMetodoPago?.setAdapter(null)
        val metodoPagoArrayAdapter = ArrayAdapter(this, android.R.layout.simple_spinner_dropdown_item, metodoPagoList)
        spinnerMetodoPago?.setAdapter(metodoPagoArrayAdapter)

        spinnerMetodoPago?.onItemClickListener = AdapterView.OnItemClickListener { adapterView, view, position, l ->
            if (metodoPagoList[position].equals("Transferencia Bancaria")) {
                //Carga Spinner Banco
                spinnerBanco?.visibility = View.VISIBLE
                spinnerBanco?.setText("")
                val itemsBanco = arrayOf("Bancolombia", "BBVA", "Banco Agrario")
                val bancoList = ArrayList<String>()
                bancoList.addAll(Arrays.asList(*itemsBanco))
                spinnerBanco?.setAdapter(null)
                val bancoArrayAdapter = ArrayAdapter(applicationContext, android.R.layout.simple_spinner_dropdown_item, bancoList)
                spinnerBanco?.setAdapter(bancoArrayAdapter)
                spinnerBanco?.onItemClickListener = AdapterView.OnItemClickListener { adapterView, view, i, l ->
                    textInputNumeroCuenta?.visibility = View.VISIBLE
                    edtNumeroCuenta?.setText("")
                }
            } else if (metodoPagoList[position].equals("Otros")) {
                spinnerBanco?.visibility = View.VISIBLE
                spinnerBanco?.setText("")
                val itemsBanco = arrayOf("Efecty", "Su Chance", "Baloto")
                val bancoList = ArrayList<String>()
                bancoList.addAll(Arrays.asList(*itemsBanco))
                spinnerBanco?.setAdapter(null)
                val bancoArrayAdapter = ArrayAdapter(applicationContext, android.R.layout.simple_spinner_dropdown_item, bancoList)
                spinnerBanco?.setAdapter(bancoArrayAdapter)
                spinnerBanco?.onItemClickListener = AdapterView.OnItemClickListener { adapterView, view, i, l -> }
            } else {
                spinnerBanco?.visibility = View.GONE
                textInputNumeroCuenta?.visibility = View.GONE
            }
        }
    }

    override fun limpiarCambios() {
        ivBackButtonRegisterProductor?.setColorFilter(resources.getColor(R.color.grey_luiyi))
    }

    override fun navigateToParentActivity() {
        ivBackButtonRegisterProductor?.setColorFilter(resources.getColor(R.color.colorPrimary))
        returnToParentActivity()
    }

    override fun registerProductor() {
        Snackbar.make(container, "Registrado exitosamente", Snackbar.LENGTH_SHORT).show()
    }

    override fun disableInputs() {

    }

    override fun enableInputs() {

    }
    //endregion

    //region Método Click
    override fun onClick(p0: View?) {
        when (p0?.id) {
            R.id.ivBackButtonRegisterProductor -> {
                navigateToParentActivity()
            }
            R.id.btnRegistrarProductor -> {
                registerProductor()
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

    //region Ciclo de Vida Actividad
    override fun onResume() {
        super.onResume()
        limpiarCambios()
    }
    //endregion
}
