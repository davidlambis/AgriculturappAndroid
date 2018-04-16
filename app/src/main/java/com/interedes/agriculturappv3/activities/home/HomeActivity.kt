package com.interedes.agriculturappv3.activities.home

import android.content.Intent
import android.content.IntentFilter
import android.graphics.Color
import android.net.ConnectivityManager
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.support.design.widget.Snackbar
import android.support.v4.content.ContextCompat
import android.util.Log
import android.util.TypedValue
import android.view.View
import android.widget.TextView
import com.interedes.agriculturappv3.AgriculturApplication
import com.interedes.agriculturappv3.R
import com.interedes.agriculturappv3.activities.login.ui.LoginActivity
import com.interedes.agriculturappv3.activities.registration.register_rol.RegisterRolActivity
import com.interedes.agriculturappv3.productor.models.GenericResponse
import com.interedes.agriculturappv3.productor.models.detalle_metodo_pago.DetalleMetodoPago
import com.interedes.agriculturappv3.productor.models.detalle_metodo_pago.DetalleMetodoPagoResponse
import com.interedes.agriculturappv3.productor.models.metodopago.MetodoPago
import com.interedes.agriculturappv3.productor.models.metodopago.MetodoPagoResponse
import com.interedes.agriculturappv3.productor.models.rol.Rol
import com.interedes.agriculturappv3.productor.models.rol.RolResponse
import com.interedes.agriculturappv3.productor.models.usuario.Usuario
import com.interedes.agriculturappv3.productor.models.usuario.Usuario_Table
import com.interedes.agriculturappv3.productor.modules.ui.main_menu.MenuMainActivity
import com.interedes.agriculturappv3.services.api.ApiInterface
import com.interedes.agriculturappv3.services.internet_connection.ConnectivityReceiver
import com.interedes.agriculturappv3.services.resources.RolResources
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

    init {
        connectivityReceiver = ConnectivityReceiver()
        apiService = ApiInterface.create()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_home)
        goToMainActivity()
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
    private fun goToMainActivity() {
        if (getLastUserLogued() != null) {
            val i = Intent(this, MenuMainActivity::class.java)
            i.flags = Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK
            startActivity(i)
        }
    }

    private fun loadRoles() {
        val lista_roles = SQLite.select().from(Rol::class.java).queryList()
        if (lista_roles.size <= 0) {
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
                                //lista = Select().from(Rol::class.java).queryList()
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
        } else {
            if (checkConnection()) {
                val apiService = ApiInterface.create()
                val call = apiService.getRoles()
                call.enqueue(object : Callback<RolResponse> {
                    override fun onResponse(call: Call<RolResponse>, response: retrofit2.Response<RolResponse>?) {
                        if (response != null && response.code() == 200) {
                            lista = response.body()?.value
                            if (lista != null) {
                                Delete.table<Rol>(Rol::class.java)
                                for (item: Rol in lista!!) {
                                    if (item.Nombre.equals(RolResources.COMPRADOR)) {
                                        item.Imagen = R.drawable.ic_comprador_big
                                        item.save()
                                    } else if (item.Nombre.equals(RolResources.PRODUCTOR)) {
                                        item.Imagen = R.drawable.ic_productor_big
                                        item.save()
                                    }
                                }
                                //lista = Select().from(Rol::class.java).queryList()
                            }
                        }
                    }

                    override fun onFailure(call: Call<RolResponse>?, t: Throwable?) {
                        onMessageOk(R.color.grey_luiyi, getString(R.string.error_request))
                        Log.e("Error", t?.message.toString())
                    }

                })
            }
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

    override fun onDestroy() {
        super.onDestroy()
        unregisterReceiver(connectivityReceiver)
    }
}
