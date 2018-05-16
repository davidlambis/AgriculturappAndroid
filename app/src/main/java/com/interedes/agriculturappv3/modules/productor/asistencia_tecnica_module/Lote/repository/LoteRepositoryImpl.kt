package com.interedes.agriculturappv3.modules.productor.asistencia_tecnica_module.Lote.repository

import com.interedes.agriculturappv3.modules.models.lote.Lote
import com.interedes.agriculturappv3.modules.models.unidad_medida.Unidad_Medida
import com.interedes.agriculturappv3.modules.productor.asistencia_tecnica_module.Lote.events.RequestEventLote
import com.interedes.agriculturappv3.libs.EventBus
import com.interedes.agriculturappv3.libs.GreenRobotEventBus
import com.interedes.agriculturappv3.modules.models.cultivo.Cultivo
import com.interedes.agriculturappv3.modules.models.cultivo.Cultivo_Table
import com.interedes.agriculturappv3.modules.models.lote.Lote_Table
import com.interedes.agriculturappv3.modules.models.lote.PostLote
import com.interedes.agriculturappv3.modules.models.unidad_medida.Unidad_Medida_Table
import com.interedes.agriculturappv3.modules.models.unidad_productiva.Unidad_Productiva
import com.interedes.agriculturappv3.modules.models.unidad_productiva.Unidad_Productiva_Table
import com.interedes.agriculturappv3.modules.models.usuario.Usuario
import com.interedes.agriculturappv3.modules.models.usuario.Usuario_Table
import com.interedes.agriculturappv3.services.api.ApiInterface
import com.interedes.agriculturappv3.services.resources.CategoriaMediaResources
import com.raizlabs.android.dbflow.sql.language.SQLite
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response


class LoteRepositoryImpl : LoteRepository {

    var eventBus: EventBus? = null
    var apiService: ApiInterface? = null

    init {
        eventBus = GreenRobotEventBus()
        apiService = ApiInterface.create()
    }

    //region METHODS
    override fun saveLotes(mLote: Lote, unidad_productiva_id: Long?) {
        val up_area = SQLite.select().from(Unidad_Productiva::class.java).where(Unidad_Productiva_Table.Id.eq(unidad_productiva_id)).querySingle()?.Area
        val total_areas = area_lotes(mLote, unidad_productiva_id)
        if (total_areas!! < up_area!!) {
            val last_lote = getLastLote()
            if (last_lote == null) {
                mLote.Id = 1
            } else {
                mLote.Id = last_lote.Id + 1
            }
            mLote.save()
            val lotes = getLotes(unidad_productiva_id)
            postEventOk(RequestEventLote.SAVE_EVENT, lotes, mLote)
        } else {
            postEventError(RequestEventLote.ERROR_EVENT, "No se puede registrar. El área total de lotes supera el área de la Unidad Productiva")
        }
    }


    override fun registerOnlineLote(mLote: Lote, unidad_productiva_id: Long?) {
        val unidad_productiva = SQLite.select().from(Unidad_Productiva::class.java).where(Unidad_Productiva_Table.Id.eq(unidad_productiva_id)).querySingle()
        if (unidad_productiva?.Estado_Sincronizacion == true) {
            val postLote = PostLote(0,
                    mLote.Area,
                    mLote.Codigo,
                    mLote.Localizacion,
                    mLote.Localizacion_Poligono,
                    mLote.Unidad_Medida_Id,
                    mLote.Unidad_Productiva_Id)

            val up_area = SQLite.select().from(Unidad_Productiva::class.java).where(Unidad_Productiva_Table.Id.eq(unidad_productiva_id)).querySingle()?.Area
            val total_areas = area_lotes(mLote, unidad_productiva_id)

            if (total_areas!! > up_area!!) {
                postEventError(RequestEventLote.ERROR_EVENT, "No se puede registrar. El área total de lotes supera el área de la Unidad Productiva")
            } else {
                val call = apiService?.postLote(postLote)
                call?.enqueue(object : Callback<Lote> {
                    override fun onResponse(call: Call<Lote>?, response: Response<Lote>?) {
                        if (response != null && response.code() == 201) {
                            mLote.Id = response.body()?.Id!!
                            mLote.EstadoSincronizacion = true
                            mLote.save()
                            postEventOk(RequestEventLote.SAVE_EVENT, getLotes(unidad_productiva_id), mLote)
                        } else {
                            postEventError(RequestEventLote.ERROR_EVENT, "Comprueba tu conexión")
                        }
                    }

                    override fun onFailure(call: Call<Lote>?, t: Throwable?) {
                        postEventError(RequestEventLote.ERROR_EVENT, "Comprueba tu conexión")
                    }

                })
            }

        } else {
            val up_area = SQLite.select().from(Unidad_Productiva::class.java).where(Unidad_Productiva_Table.Id.eq(unidad_productiva_id)).querySingle()?.Area
            val total_areas = area_lotes(mLote, unidad_productiva_id)
            if (total_areas!! > up_area!!) {
                postEventError(RequestEventLote.ERROR_EVENT, "No se puede registrar. El área total de lotes supera el área de la Unidad Productiva")
            } else {
                saveLotes(mLote, unidad_productiva_id)
                ///postEventOk(RequestEventLote.SAVE_EVENT, getLotes(unidad_productiva_id), mLote)
            }
        }
    }

    private fun area_lotes(mLote: Lote, unidad_productiva_id: Long?): Double? {
        val lotes_up = SQLite.select().from(Lote::class.java).where(Lote_Table.Unidad_Productiva_Id.eq(unidad_productiva_id)).queryList()
        var suma_areas = 0.0
        for (item in lotes_up) {
            suma_areas = suma_areas + item.Area!!
        }
        val total_areas = suma_areas + mLote.Area!!
        return total_areas
    }

    override fun getListLotes(unidad_productiva_id: Long?) {
        val lotes = getLotes(unidad_productiva_id)
        postEventOk(RequestEventLote.READ_EVENT, lotes, null);
    }

    override fun loadListas() {
        val listUp = SQLite.select().from(Unidad_Productiva::class.java).where(Unidad_Productiva_Table.UsuarioId.eq(getLastUserLogued()?.Id)).queryList()
        val listUnidadMedida = SQLite.select().from(Unidad_Medida::class.java).where(Unidad_Medida_Table.CategoriaMedidaId.eq(CategoriaMediaResources.Area)).queryList()

        postEventListUnidadMedida(RequestEventLote.LIST_EVENT_UNIDAD_MEDIDA, listUnidadMedida, null)
        postEventListUp(RequestEventLote.LIST_EVENT_UP, listUp, null)
    }

    override fun getLastLote(): Lote? {
        val lastLote = SQLite.select().from(Lote::class.java).where().orderBy(Lote_Table.Id, false).querySingle()
        return lastLote
    }

    override fun getLastUserLogued(): Usuario? {
        val usuarioLogued = SQLite.select().from(Usuario::class.java).where(Usuario_Table.UsuarioRemembered.eq(true)).querySingle()
        return usuarioLogued
    }

    override fun getLotes(unidad_productiva_id: Long?): List<Lote> {
        var listResponse: List<Lote>? = null

        if (unidad_productiva_id == null) {
            listResponse = SQLite.select().from(Lote::class.java).queryList()
        } else {
            listResponse = SQLite.select().from(Lote::class.java).where(Lote_Table.Unidad_Productiva_Id.eq(unidad_productiva_id)).queryList()
        }
        return listResponse;
    }

    override fun updateLote(mLote: Lote, unidad_productiva_id: Long?) {
        if (mLote.EstadoSincronizacion == true) {
            val postLote = PostLote(mLote.Id,
                    mLote.Area,
                    mLote.Codigo,
                    mLote.Localizacion,
                    mLote.Localizacion_Poligono,
                    mLote.Unidad_Medida_Id,
                    mLote.Unidad_Productiva_Id)


            val call = apiService?.updateLote(postLote, mLote.Id)
            call?.enqueue(object : Callback<Lote> {
                override fun onResponse(call: Call<Lote>?, response: Response<Lote>?) {
                    if (response != null && response.code() == 200) {
                        mLote.update()
                        postEventOk(RequestEventLote.UPDATE_EVENT, getLotes(unidad_productiva_id), mLote)
                    } else {
                        postEventError(RequestEventLote.ERROR_EVENT, "Comprueba tu conexión")
                    }
                }

                override fun onFailure(call: Call<Lote>?, t: Throwable?) {
                    postEventError(RequestEventLote.ERROR_EVENT, "Comprueba tu conexión")
                }
            })

        } else {
            postEventError(RequestEventLote.ERROR_EVENT, "Error!. El lote no se ha subido")
        }
    }


    override fun deleteLote(mLote: Lote, unidad_productiva_id: Long?) {
        if (mLote.EstadoSincronizacion == true) {
            val call = apiService?.deleteLote(mLote.Id)
            call?.enqueue(object : Callback<Lote> {
                override fun onResponse(call: Call<Lote>?, response: Response<Lote>?) {
                    if (response != null && response.code() == 204) {
                        mLote.delete()
                        postEventOk(RequestEventLote.DELETE_EVENT, getLotes(unidad_productiva_id), mLote)
                    }
                }

                override fun onFailure(call: Call<Lote>?, t: Throwable?) {
                    postEventError(RequestEventLote.ERROR_EVENT, "Comprueba tu conexión")
                }

            })
        } else {
            //Verificate if cultivos register
            var vericateRegisterCultivos= SQLite.select().from(Cultivo::class.java).where(Cultivo_Table.LoteId.eq(mLote.Id)).querySingle()
            if(vericateRegisterCultivos!=null){

                postEventError(RequestEventLote.ERROR_EVENT, "Error!. El lote no se ha podido eliminar, recuerde eliminar los cultivos")
            }else{
                mLote.delete()
                postEventOk(RequestEventLote.DELETE_EVENT, getLotes(unidad_productiva_id), mLote)
            }

        }
    }

    //endregion

    //region Events

    private fun postEventOk(type: Int, lotes: List<Lote>?, lote: Lote?) {
        var loteListMitable = lotes as MutableList<Object>
        var LoteMutable: Object? = null
        if (lote != null) {
            LoteMutable = lote as Object
        }
        postEvent(type, loteListMitable, LoteMutable, null)
    }

    private fun postEventError(type: Int, messageError: String) {
        postEvent(type, null, null, messageError)
    }

    private fun postEventListUp(type: Int, listUnidadProductiva: List<Unidad_Productiva>?, messageError: String?) {
        var upMutable = listUnidadProductiva as MutableList<Object>
        postEvent(type, upMutable, null, messageError)
    }

    private fun postEventListUnidadMedida(type: Int, listUnidadMedida: List<Unidad_Medida>?, messageError: String?) {
        var upMutable = listUnidadMedida as MutableList<Object>
        postEvent(type, upMutable, null, messageError)
    }


    //Main Post Event
    private fun postEvent(type: Int, listModel1: MutableList<Object>?, model: Object?, errorMessage: String?) {
        val event = RequestEventLote(type, listModel1, model, errorMessage)
        event.eventType = type
        eventBus?.post(event)
    }
    //endregion

}