package com.interedes.agriculturappv3.asistencia_tecnica.activities.registration.register_user.ui

import android.content.Intent
import android.content.IntentFilter
import android.graphics.Color
import android.net.ConnectivityManager
import android.os.Build
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.support.design.widget.Snackbar
import android.support.v4.app.NavUtils
import android.support.v4.app.TaskStackBuilder
import android.support.v4.content.ContextCompat
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.TextView
import com.interedes.agriculturappv3.AgriculturApplication
import com.interedes.agriculturappv3.R
import com.interedes.agriculturappv3.asistencia_tecnica.activities.login.ui.LoginActivity
import com.interedes.agriculturappv3.asistencia_tecnica.activities.registration.register_user.presenter.RegisterUserPresenter
import com.interedes.agriculturappv3.asistencia_tecnica.activities.registration.register_user.presenter.RegisterUserPresenterImpl
import com.interedes.agriculturappv3.asistencia_tecnica.models.Usuario
import com.interedes.agriculturappv3.asistencia_tecnica.models.detalle_metodo_pago.DetalleMetodoPago
import com.interedes.agriculturappv3.asistencia_tecnica.models.metodopago.MetodoPago
import com.interedes.agriculturappv3.services.internet_connection.ConnectivityReceiver
import kotlinx.android.synthetic.main.activity_register_user.*
import java.sql.Timestamp

class RegisterUserActivity : AppCompatActivity(), RegisterUserView, View.OnClickListener, ConnectivityReceiver.connectivityReceiverListener {


    var camposValidados: Boolean? = false
    var rol: String? = null
    var presenter: RegisterUserPresenter? = null
    var metodo_pago_id: Long? = null
    var nombre_metodo_pago: String? = null
    var detalle_metodo_pago_id: Long? = null

    init {
        //Presenter
        presenter = RegisterUserPresenterImpl(this)
        (presenter as RegisterUserPresenterImpl).onCreate()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_register_user)
        baseContext.registerReceiver(ConnectivityReceiver(), IntentFilter(ConnectivityManager.CONNECTIVITY_ACTION))
        AgriculturApplication.instance.setConnectivityListener(this)
        if (intent.extras != null && intent.extras["rol"].equals("productor")) {
            rol = "productor"
            loadMetodosPago()
        } else if (intent.extras["rol"].equals("comprador")) {
            rol = "comprador"
            imageViewRol?.setImageResource(R.drawable.ic_comprador_big)
            textViewRol?.text = getString(R.string.title_register_comprador)
            spinnerMetodoPago?.visibility = View.GONE
            spinnerBanco?.visibility = View.GONE
            textInputLayoutNumeroCuenta?.visibility = View.GONE
        }
        ivBackButton?.setOnClickListener(this)
        btnRegistrar?.setOnClickListener(this)


    }

    //Cargar métodos de Pago de SQLite
    override fun getSqliteMetodosPago() {
        presenter?.getSqliteMetodosPago()
    }


    //Cargar métodos Pago
    override fun setMetodosPago(metodosPago: List<MetodoPago>?) {
        spinnerMetodoPago?.setAdapter(null)
        val metodoPagoArrayAdapter = ArrayAdapter<MetodoPago>(this, android.R.layout.simple_spinner_dropdown_item, metodosPago)
        spinnerMetodoPago?.setAdapter(metodoPagoArrayAdapter)
        spinnerMetodoPago?.onItemClickListener = AdapterView.OnItemClickListener { adapterView, view, position, l ->
            spinnerBanco?.visibility = View.VISIBLE
            spinnerBanco?.setText("")
            metodo_pago_id = metodosPago?.get(position)?.Id
            nombre_metodo_pago = metodosPago?.get(position)?.Nombre
            presenter?.loadDetalleMetodosPagoByMetodoPagoId(metodo_pago_id)
            if (!nombre_metodo_pago.equals("Transferencia Bancaria")) {
                textInputLayoutNumeroCuenta?.visibility = View.GONE
                edtNumeroCuenta?.setText("")
            }
        }
    }

    //Cargar Detalle métodos Pago
    override fun setDetalleMetodosPago(detalleMetodosPago: List<DetalleMetodoPago>?) {
        spinnerBanco?.setAdapter(null)
        val detalleMetodoPagoArrayAdapter = ArrayAdapter<DetalleMetodoPago>(this, android.R.layout.simple_spinner_dropdown_item, detalleMetodosPago)
        spinnerBanco?.setAdapter(detalleMetodoPagoArrayAdapter)
        spinnerBanco?.onItemClickListener = AdapterView.OnItemClickListener { adapterView, view, position, l ->
            detalle_metodo_pago_id = detalleMetodosPago?.get(position)?.Id
            if (nombre_metodo_pago.equals("Transferencia Bancaria")) {
                textInputLayoutNumeroCuenta?.visibility = View.VISIBLE
                edtNumeroCuenta?.setText("")
            } else {
                textInputLayoutNumeroCuenta?.visibility = View.GONE
                edtNumeroCuenta?.setText("")
            }
        }
    }

    override fun loadMetodosPagoError(error: String?) {
        onMessageError(R.color.grey_luiyi, error)
    }

    override fun loadMetodosPago() {
        presenter?.loadMetodosPago()
    }

    override fun limpiarCambios() {
        ivBackButton?.setColorFilter(ContextCompat.getColor(this, R.color.grey_luiyi))
    }

    override fun navigateToParentActivity() {
        ivBackButton?.setColorFilter(ContextCompat.getColor(this, R.color.colorPrimary))
        returnToParentActivity()
    }


    override fun validarCampos(): Boolean? {
        //val email_pattern = "[a-zA-Z0-9._-]+@[a-z]+\\.+[a-z]+"
        var cancel = false
        var focusView: View? = null
        if (edtNombres?.text.toString().isEmpty()) {
            edtNombres?.setError(getString(R.string.error_field_required))
            focusView = edtNombres
            cancel = true
        } else if (edtApellidos?.text.toString().isEmpty()) {
            edtApellidos?.setError(getString(R.string.error_field_required))
            focusView = edtApellidos
            cancel = true
        } else if (edtCorreo?.text.toString().isEmpty()) {
            edtCorreo?.setError(getString(R.string.error_field_required))
            focusView = edtCorreo
            cancel = true
        } else if (edtCorreo?.text.toString().isEmpty()) {
            edtCorreo?.setError(getString(R.string.error_field_required))
            focusView = edtCorreo
            cancel = true
        } else if (edtContrasena?.text.toString().isEmpty()) {
            edtContrasena?.setError(getString(R.string.error_field_required))
            focusView = edtContrasena
            cancel = true
        } else if (edtConfirmarContrasena?.text.toString().isEmpty()) {
            edtConfirmarContrasena?.setError(getString(R.string.error_field_required))
            focusView = edtConfirmarContrasena
            cancel = true
        } else if (edtCelular?.text.toString().isEmpty()) {
            edtCelular?.setError(getString(R.string.error_field_required))
            focusView = edtCelular
            cancel = true
        } else if (spinnerMetodoPago?.visibility == View.VISIBLE && spinnerMetodoPago?.text.toString().isEmpty()) {
            spinnerMetodoPago?.setError(getString(R.string.error_field_required))
            focusView = spinnerMetodoPago
            cancel = true
        } else if (spinnerBanco?.visibility == View.VISIBLE && spinnerBanco.text.toString().isEmpty()) {
            spinnerBanco?.setError(getString(R.string.error_field_required))
            focusView = spinnerBanco
            cancel = true
        } else if (textInputLayoutNumeroCuenta?.visibility == View.VISIBLE && edtNumeroCuenta.text.toString().isEmpty()) {
            edtNumeroCuenta?.setError(getString(R.string.error_field_required))
            focusView = edtNumeroCuenta
            cancel = true
        } else if (!edtContrasena?.text.toString().trim().equals(edtConfirmarContrasena.text.toString().trim())) {
            edtConfirmarContrasena?.setError(getString(R.string.edit_text_error_contrasenas))
            focusView = edtConfirmarContrasena
            cancel = true
        } else if (edtContrasena.text.toString().trim().length < 6) {
            edtContrasena?.setError(getString(R.string.edit_text_error_contrasena))
            focusView = edtContrasena
            cancel = true
        } /* else if (!edtCorreo.text.toString().trim().matches(email_pattern.toRegex())) {
            edtCorreo?.setError(getString(R.string.edit_text_error_correo))
            focusView = edtCorreo
            cancel = true
        }*/

        if (cancel) {
            focusView?.requestFocus()
        } else {
            camposValidados = true
        }
        return camposValidados
    }

    override fun registerUsuario() {
        //TODO primero registro en el backend, retorna respuesta, si es OK hace registro a firebase y SQLITE
        //Llamar al presentador
        //presenter?.validarCampos()
        if (presenter?.validarCampos() == true) {

            //TEST
            val tsLong = System.currentTimeMillis() / 1000
            val tsTemp = Timestamp(tsLong)

            //TODO Load Métodos Pago y Detalle backend
            //TODO Encriptar Contraseña IMPORTANTE

            val usuario = Usuario(0,
                    edtNombres?.text.toString(),
                    edtApellidos?.text.toString(),
                    edtCorreo?.text.toString(),
                    false,
                    edtCedula?.text.toString(),
                    edtContrasena?.text.toString(),
                    "",
                    tsTemp.toString(),
                    "",
                    edtCelular?.text.toString(),
                    edtNumeroCuenta?.text.toString(),
                    edtCelular?.text.toString(),
                    false,
                    "",
                    0,
                    0,
                    spinnerBanco?.text.toString(),
                    rol
            )

            presenter?.registerUsuario(usuario)

        }
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


    override fun disableInputs() {
        setInputs(false)
    }


    override fun enableInputs() {
        setInputs(true)
    }

    override fun hideMetodosPago() {
        textInputLayoutNumeroCuenta?.isEnabled = false
        spinnerMetodoPago?.isEnabled = false
        spinnerBanco?.isEnabled = false
        spinnerMetodoPago?.visibility = View.GONE
        spinnerBanco?.visibility = View.GONE
        textInputLayoutNumeroCuenta?.visibility = View.GONE
    }


    override fun registroExitoso() {
        //Snackbar.make(container, getString(R.string.registro_exitoso), Snackbar.LENGTH_SHORT).show()
        val i = Intent(this, LoginActivity::class.java)
        i.putExtra("correo", getString(R.string.snackbar_verificacion_correo))
        i.flags = Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK
        startActivity(i)
    }

    override fun registroError(error: String?) {
        //Snackbar.make(container, error.toString(), Snackbar.LENGTH_LONG).show()
        onMessageError(R.color.grey_luiyi, error)
    }

    override fun showProgress() {
        progressBar.visibility = View.VISIBLE
    }

    override fun hideProgress() {
        progressBar.visibility = View.GONE
    }

    override fun hasNotConnectivity() {
        //Snackbar.make(container, getString(R.string.not_internet_connected), Snackbar.LENGTH_SHORT).show()
        onMessageOk(R.color.grey_luiyi, getString(R.string.not_internet_connected))
    }

    private fun setInputs(b: Boolean) {
        textInputLayoutNombres?.isEnabled = b
        textInputLayoutApellidos?.isEnabled = b
        textInputLayoutCedula?.isEnabled = b
        textInputLayoutCelular?.isEnabled = b
        textInputLayoutCorreo?.isEnabled = b
        textInputLayoutContrasena?.isEnabled = b
        textInputLayoutConfirmarContrasena?.isEnabled = b
        textInputLayoutNumeroCuenta?.isEnabled = b
        spinnerMetodoPago?.isEnabled = b
        spinnerBanco?.isEnabled = b
        btnRegistrar?.isEnabled = b
    }

    //endregion

    //region Método Click
    override fun onClick(p0: View?) {
        when (p0?.id) {
            R.id.ivBackButton -> {
                navigateToParentActivity()
            }
            R.id.btnRegistrar -> {
                registerUsuario()
            }

        }
    }
    //endregion

    //region Conexión a Internet
    //Revisar manualmente
    private fun checkConnection(): Boolean {
        return ConnectivityReceiver.isConnected
        //showSnack(isConnected);
    }

    override fun onNetworkConnectionChanged(isConnected: Boolean) {
        if (isConnected) {
            onMessageOk(R.color.colorPrimary, getString(R.string.internet_connected))
            if (rol.equals("productor")) {
                spinnerMetodoPago?.isEnabled = true
                spinnerMetodoPago?.visibility = View.VISIBLE
                loadMetodosPago()
            }
        } else {
            onMessageError(R.color.grey_luiyi, getString(R.string.not_internet_connected))
            presenter?.getSqliteMetodosPago()
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

    override fun onDestroy() {
        presenter?.onDestroy()
        super.onDestroy()
    }
    //endregion
}
