package com.interedes.agriculturappv3.activities.registration.register_rol

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
import android.support.v4.widget.SwipeRefreshLayout
import android.support.v7.widget.GridLayoutManager
import android.util.Log
import android.util.TypedValue
import android.view.View
import android.widget.TextView
import com.interedes.agriculturappv3.AgriculturApplication
import com.interedes.agriculturappv3.R
import com.interedes.agriculturappv3.activities.home.HomeActivity
import com.interedes.agriculturappv3.activities.registration.register_rol.adapters.RegisterRolAdapter
import com.interedes.agriculturappv3.activities.registration.register_user.ui.RegisterUserActivity
import com.interedes.agriculturappv3.modules.models.rol.RolResponse
import com.interedes.agriculturappv3.modules.models.rol.Rol
import com.interedes.agriculturappv3.modules.models.rol.Rol_Table
import com.interedes.agriculturappv3.services.api.ApiInterface
import com.interedes.agriculturappv3.services.internet_connection.ConnectivityReceiver
import com.interedes.agriculturappv3.services.resources.RolResources
import com.raizlabs.android.dbflow.kotlinextensions.save
import com.raizlabs.android.dbflow.sql.language.Delete
import com.raizlabs.android.dbflow.sql.language.SQLite
import com.raizlabs.android.dbflow.sql.language.Select
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import kotlinx.android.synthetic.main.activity_register_rol.*
import retrofit2.Call
import retrofit2.Callback
import java.util.concurrent.TimeUnit

class RegisterRolActivity : AppCompatActivity(), RegisterRolView, View.OnClickListener, ConnectivityReceiver.connectivityReceiverListener,SwipeRefreshLayout.OnRefreshListener {
    override fun onRefresh() {
        loadRoles()
    }

    var lista: MutableList<Rol>? = ArrayList<Rol>()
    var adapter: RegisterRolAdapter? = null
    var connectivityReceiver: ConnectivityReceiver? = null

    init {
        connectivityReceiver = ConnectivityReceiver()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_register_rol)
        swipeRefreshLayout.setOnRefreshListener(this)
        loadRoles()
        imageViewBackButton?.setOnClickListener(this)
        registerReceiver(connectivityReceiver, IntentFilter(ConnectivityManager.CONNECTIVITY_ACTION))
        AgriculturApplication.instance.setConnectivityListener(this)
    }

    //region Métodos Interfaz
    override fun loadRoles() {
            swipeRefreshLayout.setRefreshing(true)
            //Si hay conectividad a Internet
            if (checkConnection()) {

                val apiService = ApiInterface.create()
                val call = apiService.getRoles()
                //val callProductos = apiService?.getProductosByTipoProductos(queryProductos)
                // mCompositeDisposable?.add(callProductos?.observeOn(AndroidSchedulers.mainThread())?.subscribeOn(Schedulers.io())?.subscribe(this::handleResponse,this::handleError)!!);
                call.delay(500, TimeUnit.MILLISECONDS)?.subscribeOn(Schedulers.io())?.observeOn(AndroidSchedulers.mainThread())?.subscribe({ searchResponse ->
                    //Log.d("search", searchString)
                    lista?.clear()
                    lista =searchResponse.value
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
                        lista = Select().from(Rol::class.java).where(Rol_Table.Nombre.eq("Comprador")).or(Rol_Table.Nombre.eq("Productor")).queryList()

                    }

                    swipeRefreshLayout.setRefreshing(false)
                    loadRecyclerView()

                    //view.showSearchResult(searchResponse.items())
                   },
                    { throwable ->
                            val error= throwable.toString()
                        swipeRefreshLayout.setRefreshing(false)

                    })
                //Si no hay conectividad a Internet
            } else {
                lista = Select().from(Rol::class.java).where(Rol_Table.Nombre.eq("Comprador")).or(Rol_Table.Nombre.eq("Productor")).queryList()
                loadRecyclerView()
                onMessageOk(R.color.grey_luiyi, getString(R.string.not_internet_connected))
                swipeRefreshLayout.setRefreshing(false)
            }
    }

    override fun navigateToParentActivity() {
        imageViewBackButton?.setColorFilter(ContextCompat.getColor(this.applicationContext, R.color.colorPrimary))
        returnToParentActivity()
    }

    override fun limpiarCambios() {
        imageViewBackButton?.setColorFilter(ContextCompat.getColor(this.applicationContext, R.color.grey_luiyi))
    }

    override fun showProgress() {
        progressBarRol?.visibility = View.VISIBLE
    }

    override fun hideProgress() {
        progressBarRol?.visibility = View.GONE
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

    //region Conexión a Internet
    //Revisar manualmente
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
    //endregion

    //region Métodos Generales
    fun loadRecyclerView() {
        if (lista != null && lista?.size!! > 0) {
            recyclerView?.layoutManager = GridLayoutManager(this, 2)
            adapter?.clear()
            adapter = RegisterRolAdapter(lista) { position ->
                if (lista!![position].Nombre.equals(RolResources.PRODUCTOR)) {
                    showProgress()
                    val i = Intent(this, RegisterUserActivity::class.java)
                    i.putExtra("rol", "productor")
                    i.putExtra("rol_id", lista!![position].Id)
                    startActivity(i)
                } else if (lista!![position].Nombre.equals(RolResources.COMPRADOR)) {
                    showProgress()
                    val j = Intent(this, RegisterUserActivity::class.java)
                    j.putExtra("rol", "comprador")
                    j.putExtra("rol_id", lista!![position].Id)
                    startActivity(j)
                }
            }
            recyclerView?.adapter = adapter
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
        hideProgress()
        limpiarCambios()
    }

    override fun onDestroy() {
        super.onDestroy()
        unregisterReceiver(connectivityReceiver)
    }

    //endregion
}
