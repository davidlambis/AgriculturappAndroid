package com.interedes.agriculturappv3.activities.home

import android.Manifest
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.content.pm.PackageManager
import android.graphics.Color
import android.net.ConnectivityManager
import android.os.Build
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.support.design.widget.Snackbar
import android.support.v4.app.ActivityCompat
import android.support.v4.content.ContextCompat
import android.util.Log
import android.util.TypedValue
import android.view.View
import android.widget.TextView
import android.widget.Toast
import com.interedes.agriculturappv3.AgriculturApplication
import com.interedes.agriculturappv3.R
import com.interedes.agriculturappv3.activities.intro.PermissionsIntro
import com.interedes.agriculturappv3.activities.login.ui.LoginActivity
import com.interedes.agriculturappv3.activities.registration.register_rol.RegisterRolActivity
import com.interedes.agriculturappv3.modules.models.detalle_metodo_pago.DetalleMetodoPago
import com.interedes.agriculturappv3.modules.models.detalle_metodo_pago.DetalleMetodoPagoResponse
import com.interedes.agriculturappv3.modules.models.metodopago.MetodoPago
import com.interedes.agriculturappv3.modules.models.metodopago.MetodoPagoResponse
import com.interedes.agriculturappv3.modules.models.rol.Rol
import com.interedes.agriculturappv3.modules.models.rol.RolResponse
import com.interedes.agriculturappv3.modules.models.usuario.Usuario
import com.interedes.agriculturappv3.modules.models.usuario.Usuario_Table
import com.interedes.agriculturappv3.modules.productor.ui.main_menu.MenuMainActivity
import com.interedes.agriculturappv3.services.api.ApiInterface
import com.interedes.agriculturappv3.services.chat.SharedPreferenceHelper
import com.interedes.agriculturappv3.services.internet_connection.ConnectivityReceiver
import com.interedes.agriculturappv3.services.resources.RolResources
import com.interedes.agriculturappv3.services.resources.Status_Sync_Data_Resources
import com.raizlabs.android.dbflow.kotlinextensions.save
import com.raizlabs.android.dbflow.sql.language.Delete
import com.raizlabs.android.dbflow.sql.language.SQLite
import com.raizlabs.android.dbflow.sql.language.Select
import kotlinx.android.synthetic.main.activity_home.*
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response


class HomeActivity : AppCompatActivity(), View.OnClickListener, ConnectivityReceiver.connectivityReceiverListener {

    var connectivityReceiver: ConnectivityReceiver? = null
    var lista: MutableList<Rol>? = null
    var apiService: ApiInterface? = null

    //PERMISSION
    private val PERMISSION_REQUEST_CODE = 1
    var PERMISSION_ALL = 9
    var PERMISSIONS = arrayOf(Manifest.permission.CAMERA,
            Manifest.permission.WRITE_EXTERNAL_STORAGE,
            Manifest.permission.READ_EXTERNAL_STORAGE,
            Manifest.permission.ACCESS_FINE_LOCATION,
            Manifest.permission.READ_PHONE_STATE,
            Manifest.permission.SEND_SMS,
            Manifest.permission.READ_SMS,
            Manifest.permission.RECEIVE_SMS,
            Manifest.permission.READ_CONTACTS
            )

    init {
        connectivityReceiver = ConnectivityReceiver()
        apiService = ApiInterface.create()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_home)
        PermissionGoToMainActivity()

        registerReceiver(connectivityReceiver, IntentFilter(ConnectivityManager.CONNECTIVITY_ACTION))
        AgriculturApplication.instance.setConnectivityListener(this)
        loadRoles()
        loadInitialLists()
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
    private fun PermissionGoToMainActivity() {

        if (Build.VERSION.SDK_INT >= 23) {
            if (!hasPermissions(this, *PERMISSIONS)) {
                startActivity(Intent(getBaseContext(), PermissionsIntro::class.java))
            } else {
                val response = doPermissionGrantedStuffs()
                if (response) {
                    goToMainActivity()
                    //startActivityForResult(Intent(Intent.ACTION_OPEN_DOCUMENT_TREE), READ_REQUEST_CODE)
                }
            }
        } else {
            val response = doPermissionGrantedStuffs()
            if (response) {
                goToMainActivity()
                //startActivityForResult(Intent(Intent.ACTION_OPEN_DOCUMENT_TREE), READ_REQUEST_CODE)
            }
        }
    }


    private fun goToMainActivity() {
        var list = SQLite.select().from(Usuario::class.java).queryList()
        var usuario= getLastUserLogued()
        if (usuario != null) {
            SharedPreferenceHelper.getInstance(this).savePostSyncData(Status_Sync_Data_Resources.STOP);
            val i = Intent(this, MenuMainActivity::class.java)
            i.flags = Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK
            startActivity(i)
        }
    }


    private fun loadRoles() {
        if (checkConnection()) {
                val apiService = ApiInterface.create()
                val call = apiService.getRoles()
                call.enqueue(object : Callback<RolResponse> {
                    override fun onResponse(call: Call<RolResponse>, response: retrofit2.Response<RolResponse>?) {
                        if (response != null && response.code() == 200) {
                            lista = response.body()?.value
                            if (lista != null) {
                                for (item: Rol in lista!!) {
                                    if (item.Nombre.equals(RolResources.COMPRADOR)) {
                                        item.Imagen = R.drawable.ic_comprador_big
                                        item.save()
                                    } else if (item.Nombre.equals(RolResources.PRODUCTOR)) {
                                        item.Imagen = R.drawable.ic_productor_big
                                        item.save()
                                    }
                                }
                            }
                        }
                    }
                    override fun onFailure(call: Call<RolResponse>?, t: Throwable?) {
                        onMessageOk(R.color.grey_luiyi, getString(R.string.error_request))
                        Log.e("Error", t?.message.toString())
                    }
                })
            } else {
                onMessageOk(R.color.grey_luiyi, getString(R.string.not_internet_connected))
            }
    }


    fun loadInitialLists() {
        if (checkConnection()) {
            val call = apiService?.getMetodoPagos()
            call?.enqueue(object : Callback<MetodoPagoResponse> {
                override fun onResponse(call: Call<MetodoPagoResponse>?, response: Response<MetodoPagoResponse>?) {
                    if (response != null && response.code() == 200) {
                        val metodos_pago = response.body()?.value!!
                        for (item: MetodoPago in metodos_pago) {
                            item.save()
                        }
                    }
                }
                override fun onFailure(call: Call<MetodoPagoResponse>?, t: Throwable?) {
                }
            })
            val call_2 = apiService?.getDetalleMetodoPagos()
            call_2?.enqueue(object : Callback<DetalleMetodoPagoResponse> {
                override fun onResponse(call: Call<DetalleMetodoPagoResponse>?, response: Response<DetalleMetodoPagoResponse>?) {
                    if (response != null && response.code() == 200) {
                        val detalle_metodos_pago = response.body()?.value!!
                        for (item: DetalleMetodoPago in detalle_metodos_pago) {
                            item.save()
                        }
                    }
                }
                override fun onFailure(call: Call<DetalleMetodoPagoResponse>?, t: Throwable?) {
                }
            })
        }
    }

/*

    override fun loadDetalleMetodosPagoByMetodoPagoId(Id: Long?) {
        val call = apiService?.getDetalleMetodoPagos()
        call?.enqueue(object : Callback<DetalleMetodoPagoResponse> {
            override fun onResponse(call: Call<DetalleMetodoPagoResponse>?, response: GenericResponse<DetalleMetodoPagoResponse>?) {
                if (response != null && response.code() == 200) {
                    val detalle_metodos_pago = response.body()?.value!!
                    for (item: DetalleMetodoPago in detalle_metodos_pago) {
                        item.save()
                    }
                    val detalleMetodosPagoList = getDetalleMetodosPagoByMetodoPagoId(Id)
                    postEventDetalleMetodoPago(RegisterEvent.onDetalleMetodosPagoExitoso, detalleMetodosPagoList, null)

                } else {
                    postEvent(RegisterEvent.onLoadInfoError, "No se pudieron cargar los bancos")
                }
            }

            override fun onFailure(call: Call<DetalleMetodoPagoResponse>?, t: Throwable?) {

            }
        })
    }

     */

    private fun checkConnection(): Boolean {
        return ConnectivityReceiver.isConnected
        //showSnack(isConnected);
    }

    override fun onNetworkConnectionChanged(isConnected: Boolean) {
        if (isConnected) {
            onMessageOk(R.color.colorPrimary, getString(R.string.internet_connected))
            if (Select().from(Rol::class.java).queryList().size <= 0) {
                loadRoles()
            }
        } else {
            if (Select().from(Rol::class.java).queryList().size <= 0) {
                onMessageOk(R.color.grey_luiyi, getString(R.string.error_load_roles))
            } else {
                onMessageOk(R.color.grey_luiyi, getString(R.string.not_internet_connected))
            }
        }
    }

    fun onMessageOk(colorPrimary: Int, message: String?) {
        val color = Color.WHITE
        val snackbar = Snackbar
                .make(container, message!!, Snackbar.LENGTH_LONG)
        val sbView = snackbar.view
        sbView.setBackgroundColor(ContextCompat.getColor(applicationContext, colorPrimary))
        val textView = sbView.findViewById<View>(android.support.design.R.id.snackbar_text) as TextView
        textView.setCompoundDrawablesWithIntrinsicBounds(R.drawable.ic_productor, 0, 0, 0)
        // textView.setCompoundDrawablePadding(getResources().getDimensionPixelOffset(R.dimen.activity_horizontal_margin));
        textView.setTextColor(color)
        textView.setTextSize(TypedValue.COMPLEX_UNIT_PX,
                getResources().getDimension(R.dimen.text_size_16))
        snackbar.show()
    }

    private fun getLastUserLogued(): Usuario? {
        var usuarioLoguedHome = SQLite.select().from(Usuario::class.java)
                .where(Usuario_Table.UsuarioRemembered.eq(true))
                .querySingle()
        return usuarioLoguedHome
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

    override fun onDestroy() {
        super.onDestroy()
        unregisterReceiver(connectivityReceiver)
    }



    //region PERMISSIONS
    //region PERMISSIONS

    fun hasPermissions(context: Context?, vararg permissions: String): Boolean {
        if (android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.M && context != null && permissions != null) {
            for (permission in permissions) {
                if (ActivityCompat.checkSelfPermission(context, permission) != PackageManager.PERMISSION_GRANTED) {
                    return false
                }
            }
        }
        return true
    }


    fun doPermissionGrantedStuffs(): Boolean {
        /// String SIMSerialNumber=tm.getSimSerialNumber();
        for (permission in PERMISSIONS) {
            if (ActivityCompat.checkSelfPermission(this, permission) != PackageManager.PERMISSION_GRANTED) {
                val response = false
                return response
            }
        }
        val response = true
        return response
    }

    //endregion


    //endregion
}
