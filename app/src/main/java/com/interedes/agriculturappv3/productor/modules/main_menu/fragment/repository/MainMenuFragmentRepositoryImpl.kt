package com.interedes.agriculturappv3.productor.modules.main_menu.fragment.repository

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.interedes.agriculturappv3.productor.models.usuario.Usuario
import com.interedes.agriculturappv3.events.RequestEvent
import com.interedes.agriculturappv3.libs.EventBus
import com.interedes.agriculturappv3.libs.GreenRobotEventBus
import com.interedes.agriculturappv3.productor.TestResponse
import com.interedes.agriculturappv3.productor.models.Ciudad
import com.interedes.agriculturappv3.productor.models.Departamento
import com.interedes.agriculturappv3.productor.models.GenericResponse
import com.interedes.agriculturappv3.productor.models.unidad_medida.CategoriaMedida
import com.interedes.agriculturappv3.productor.models.unidad_medida.CategoriaMedidaResponse
import com.interedes.agriculturappv3.productor.models.unidad_medida.UnidadMedidaResponse
import com.interedes.agriculturappv3.productor.models.unidad_medida.Unidad_Medida
import com.interedes.agriculturappv3.services.api.ApiInterface
import com.interedes.agriculturappv3.services.api.TestInterface
import com.raizlabs.android.dbflow.kotlinextensions.save
import com.raizlabs.android.dbflow.sql.language.SQLite
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response


class MainMenuFragmentRepositoryImpl : MainMenuFragmentRepository {

    var eventBus: EventBus? = null
    var apiService: ApiInterface? = null
    //Firebase
    val mDatabase: DatabaseReference?
    var mUserDatabase: DatabaseReference? = null
    var mAuth: FirebaseAuth? = null
    var mUserReference: DatabaseReference? = null
    var testService: TestInterface? = null

    init {
        eventBus = GreenRobotEventBus()
        apiService = ApiInterface.create()
        mDatabase = FirebaseDatabase.getInstance().reference
        testService = TestInterface.create()
        //mUserDatabase = mDatabase.child("Users")
        //mAuth = FirebaseAuth.getInstance()
        //mUserReference = mUserDatabase?.child(mAuth?.currentUser?.uid)
    }


    //region Métodos Interfaz

    override fun getListasIniciales() {
        //Departamentos y Ciudades
        val lista_departamentos = SQLite.select().from(Departamento::class.java).queryList()
        val lista_ciudades = SQLite.select().from(Ciudad::class.java).queryList()
        if (lista_departamentos.size <= 0 || lista_ciudades.size <= 0) {
            val call = testService?.getDepartamentos()
            call?.enqueue(object : Callback<TestResponse> {
                override fun onResponse(call: Call<TestResponse>?, response: Response<TestResponse>?) {
                    if (response != null && response.code() == 200) {
                        val departamentos: MutableList<Departamento> = response.body()?.departCiudades!!
                        for (item in departamentos) {
                            item.save()
                            for (ciudad in item.ciudades!!) {
                                ciudad.save()
                            }
                        }
                    }
                }

                override fun onFailure(call: Call<TestResponse>?, t: Throwable?) {
                    postEvent(RequestEvent.ERROR_EVENT, t?.message.toString())
                }

            })
        }

        //Categorías Medida
        val callCategoriaMedida = apiService?.getCategoriasMedida()
        callCategoriaMedida?.enqueue(object : Callback<CategoriaMedidaResponse> {
            override fun onResponse(call: Call<CategoriaMedidaResponse>?, response: Response<CategoriaMedidaResponse>?) {
                if (response != null && response.code() == 200) {
                    val categoriasMedida = response.body()?.value as MutableList<CategoriaMedida>
                    for (item in categoriasMedida) {
                        item.save()
                    }
                }
            }

            override fun onFailure(call: Call<CategoriaMedidaResponse>?, t: Throwable?) {
                postEvent(RequestEvent.ERROR_EVENT, t?.message.toString())
            }
        })

        val callUnidadesMedida = apiService?.getUnidadesMedida()
        callUnidadesMedida?.enqueue(object : Callback<UnidadMedidaResponse> {
            override fun onResponse(call: Call<UnidadMedidaResponse>?, response: Response<UnidadMedidaResponse>?) {
                if (response != null && response.code() == 200) {
                    val unidadesMedida = response.body()?.value as MutableList<Unidad_Medida>
                    for (item in unidadesMedida) {
                        item.save()
                    }
                }
            }

            override fun onFailure(call: Call<UnidadMedidaResponse>?, t: Throwable?) {
                postEvent(RequestEvent.ERROR_EVENT, t?.message.toString())
            }

        })
    }

    override fun logOut(usuario: Usuario?) {
        try {
            mAuth = FirebaseAuth.getInstance()
            if (mAuth?.currentUser != null) {
                mAuth?.signOut()
            }
            usuario?.UsuarioRemembered = false
            usuario?.AccessToken = null
            usuario?.save()
            postEvent(RequestEvent.UPDATE_EVENT)
        } catch (e: Exception) {
            postEvent(RequestEvent.ERROR_EVENT, e.message.toString())
        }

    }

    override fun offlineLogOut(usuario: Usuario?) {
        try {
            usuario?.UsuarioRemembered = false
            usuario?.AccessToken = null
            usuario?.save()
            postEvent(RequestEvent.UPDATE_EVENT)
        } catch (e: Exception) {
            postEvent(RequestEvent.ERROR_EVENT, e.message.toString())
        }
    }
    //endregion


    //region Events
    private fun postEvent(type: Int) {
        postEvent(type, null)
    }

    private fun postEvent(type: Int, errorMessage: String?) {
        val event = RequestEvent(type, null, null, errorMessage)
        event.eventType = type
        event.mensajeError = errorMessage
        eventBus?.post(event)
    }
    //endregion

}