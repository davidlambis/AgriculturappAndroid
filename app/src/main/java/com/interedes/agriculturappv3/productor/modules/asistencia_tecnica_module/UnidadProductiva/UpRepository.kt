package com.interedes.agriculturappv3.productor.modules.asistencia_tecnica_module.UnidadProductiva

import com.interedes.agriculturappv3.productor.models.unidad_productiva.UnidadProductiva
import com.interedes.agriculturappv3.productor.models.unidad_medida.Unidad_Medida
import com.interedes.agriculturappv3.productor.modules.asistencia_tecnica_module.UnidadProductiva.events.RequestEventUP
import com.interedes.agriculturappv3.libs.EventBus
import com.interedes.agriculturappv3.libs.GreenRobotEventBus
import com.interedes.agriculturappv3.productor.models.Ciudad
import com.interedes.agriculturappv3.productor.models.Departamento
import com.interedes.agriculturappv3.productor.models.unidad_medida.Unidad_Medida_Table
import com.interedes.agriculturappv3.productor.models.unidad_productiva.PostUnidadProductiva
import com.interedes.agriculturappv3.productor.models.unidad_productiva.UnidadProductiva_Table
import com.interedes.agriculturappv3.productor.models.usuario.Usuario
import com.interedes.agriculturappv3.productor.models.usuario.Usuario_Table
import com.interedes.agriculturappv3.services.api.ApiInterface
import com.raizlabs.android.dbflow.kotlinextensions.delete
import com.raizlabs.android.dbflow.kotlinextensions.save
import com.raizlabs.android.dbflow.kotlinextensions.update
import com.raizlabs.android.dbflow.sql.language.SQLite
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class UpRepository() : IUnidadProductiva.Repo {
    var eventBus: EventBus? = null
    var apiService: ApiInterface? = null


    init {
        eventBus = GreenRobotEventBus()
        apiService = ApiInterface.create()
    }

    override fun getListUPs() {
        postEventOk(RequestEventUP.READ_EVENT, getUPs(), null)
    }

    override fun saveUp(mUnidadProductiva: UnidadProductiva) {
        val last_up = getLastUp()
        if (last_up == null) {
            mUnidadProductiva.Id = 1
        } else {
            mUnidadProductiva.Id = last_up.Id!! + 1
        }
        mUnidadProductiva.save()
        postEventOk(RequestEventUP.SAVE_EVENT, getUPs(), mUnidadProductiva)
    }

    override fun registerOnlineUP(mUnidadProductiva: UnidadProductiva?) {

        //TODO Ciudad Id de la tabla del backend
        val postUnidadProductiva = PostUnidadProductiva(0,
                mUnidadProductiva?.Area,
                1,
                mUnidadProductiva?.Codigo,
                mUnidadProductiva?.UnidadMedidaId,
                mUnidadProductiva?.UsuarioId,
                mUnidadProductiva?.descripcion,
                mUnidadProductiva?.nombre)

        // mUnidadProductiva?.CiudadId = 1
        val call = apiService?.postUnidadProductiva(postUnidadProductiva)
        call?.enqueue(object : Callback<UnidadProductiva> {

            override fun onResponse(call: Call<UnidadProductiva>?, response: Response<UnidadProductiva>?) {
                if (response != null && response.code() == 201) {
                    val idUP = response.body()?.Id
                    mUnidadProductiva?.Id = idUP
                    mUnidadProductiva?.Estado_Sincronizacion = true
                    mUnidadProductiva?.save()
                    postEventOk(RequestEventUP.SAVE_EVENT, getUPs(), mUnidadProductiva)
                } else {
                    postEventError(RequestEventUP.ERROR_EVENT, "Comprueba tu conexión")
                }
            }

            override fun onFailure(call: Call<UnidadProductiva>?, t: Throwable?) {
                postEventError(RequestEventUP.ERROR_EVENT, "Comprueba tu conexión")
            }

        })

    }

    override fun updateUp(mUnidadProductiva: UnidadProductiva) {
        if (mUnidadProductiva.Estado_Sincronizacion == true) {
            val updateUnidadProductiva = PostUnidadProductiva(mUnidadProductiva.Id,
                    mUnidadProductiva.Area,
                    1,
                    mUnidadProductiva.Codigo,
                    mUnidadProductiva.UnidadMedidaId,
                    mUnidadProductiva.UsuarioId,
                    mUnidadProductiva.descripcion,
                    mUnidadProductiva.nombre)

            // mUnidadProductiva?.CiudadId = 1
            val call = apiService?.updateUnidadProductiva(updateUnidadProductiva, mUnidadProductiva.Id!!)
            call?.enqueue(object : Callback<UnidadProductiva> {
                override fun onResponse(call: Call<UnidadProductiva>?, response: Response<UnidadProductiva>?) {
                    if (response != null && response.code() == 204) {
                        mUnidadProductiva.update()
                        postEventOk(RequestEventUP.UPDATE_EVENT, getUPs(), mUnidadProductiva)
                    } else {
                        postEventError(RequestEventUP.ERROR_EVENT, "Comprueba tu conexión")
                    }
                }

                override fun onFailure(call: Call<UnidadProductiva>?, t: Throwable?) {
                    postEventError(RequestEventUP.ERROR_EVENT, "Comprueba tu conexión")
                }
            })
        } else {
            postEventError(RequestEventUP.ERROR_EVENT, "La Unidad Productiva no se ha subido")
        }
    }

    override fun deleteUp(mUnidadProductiva: UnidadProductiva) {
        mUnidadProductiva.delete()
        postEventOk(RequestEventUP.DELETE_EVENT, getUPs(), mUnidadProductiva)
    }

    override fun getUPs(): List<UnidadProductiva> {
        val usuario = getLastUserLogued()
        return SQLite.select().from(UnidadProductiva::class.java).where(UnidadProductiva_Table.UsuarioId.eq(usuario?.Id)).queryList()
    }

    override fun getLastUp(): UnidadProductiva? {
        val lastUnidadProductiva = SQLite.select().from(UnidadProductiva::class.java).where().orderBy(UnidadProductiva_Table.Id, false).querySingle()
        return lastUnidadProductiva
    }

    override fun getLastUserLogued(): Usuario? {
        val usuarioLogued = SQLite.select().from(Usuario::class.java).where(Usuario_Table.UsuarioRemembered.eq(true)).querySingle()
        return usuarioLogued
    }

    override fun getListas() {
        val listUnidadMedida = SQLite.select().from(Unidad_Medida::class.java).where(Unidad_Medida_Table.CategoriaMedidaId.eq(1)).queryList()
        val listDepartamentos = SQLite.select().from(Departamento::class.java).queryList()
        val listCiudades = SQLite.select().from(Ciudad::class.java).queryList()
        postEventListUnidadMedida(RequestEventUP.LIST_EVENT_UNIDAD_MEDIDA, listUnidadMedida, null)
        postEventListDepartamentos(RequestEventUP.LIST_EVENT_DEPARTAMENTOS, listDepartamentos, null)
        postEventListCiudades(RequestEventUP.LIST_EVENT_CIUDADES, listCiudades, null)
    }


    //region EVENTS
    private fun postEventListUnidadMedida(type: Int, listUnidadMedida: List<Unidad_Medida>?, messageError: String?) {
        var upMutable = listUnidadMedida as MutableList<Object>
        postEvent(type, upMutable, null, messageError)
    }

    private fun postEventListDepartamentos(type: Int, listDepartamentos: List<Departamento>?, messageError: String?) {
        var departamentoMutable = listDepartamentos as MutableList<Object>
        postEvent(type, departamentoMutable, null, messageError)
    }

    private fun postEventListCiudades(type: Int, listCiudades: List<Ciudad>?, messageError: String?) {
        var ciudadMutable = listCiudades as MutableList<Object>
        postEvent(type, ciudadMutable, null, messageError)
    }


    private fun postEventOk(type: Int, listUnidadProductivas: List<UnidadProductiva>?, unidadProductiva: UnidadProductiva?) {
        var UpListMutable = listUnidadProductivas as MutableList<Object>
        var UpMutable: Object? = null
        if (unidadProductiva != null) {
            UpMutable = unidadProductiva as Object
        }
        postEvent(type, UpListMutable, UpMutable, null)
    }

    //Main Post Event
    private fun postEvent(type: Int, listModel: MutableList<Object>?, model: Object?, errorMessage: String?) {
        val event = RequestEventUP(type, listModel, model, errorMessage)
        event.eventType = type
        eventBus?.post(event)
    }

    //Post Event Error
    private fun postEventError(type: Int, messageError: String?) {
        val errorEvent = RequestEventUP(type, null, null, messageError)
        errorEvent.eventType = type
        errorEvent.mensajeError = messageError
        eventBus?.post(errorEvent)
    }

    //endregion
}


