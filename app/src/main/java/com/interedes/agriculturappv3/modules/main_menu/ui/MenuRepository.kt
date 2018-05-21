package com.interedes.agriculturappv3.modules.main_menu.ui

import com.interedes.agriculturappv3.libs.EventBus
import com.interedes.agriculturappv3.libs.GreenRobotEventBus
import com.interedes.agriculturappv3.modules.main_menu.ui.events.RequestEventMainMenu
import com.interedes.agriculturappv3.modules.models.unidad_productiva.PostUnidadProductiva
import com.interedes.agriculturappv3.modules.models.unidad_productiva.Unidad_Productiva
import com.interedes.agriculturappv3.modules.models.unidad_productiva.Unidad_Productiva_Table
import com.interedes.agriculturappv3.modules.models.unidad_productiva.localizacion.LocalizacionUp
import com.interedes.agriculturappv3.modules.productor.asistencia_tecnica_module.UnidadProductiva.events.RequestEventUP
import com.interedes.agriculturappv3.services.api.ApiInterface
import com.raizlabs.android.dbflow.kotlinextensions.save
import com.raizlabs.android.dbflow.kotlinextensions.update
import com.raizlabs.android.dbflow.sql.language.SQLite
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class MenuRepository: MainViewMenu.Repository {

    var eventBus: EventBus? = null
    var apiService: ApiInterface? = null
    init {
        eventBus = GreenRobotEventBus()
        apiService = ApiInterface.create()
    }

    override fun syncData() {

        var mUnidadProductiva= SQLite.select()
                .from(Unidad_Productiva::class.java)
                .where(Unidad_Productiva_Table.Estado_Sincronizacion.eq(false)).orderBy(Unidad_Productiva_Table.Unidad_Productiva_Id,false).querySingle()

        if(mUnidadProductiva!=null){
            val postUnidadProductiva = PostUnidadProductiva(0,
                    mUnidadProductiva?.Area,
                    mUnidadProductiva?.CiudadId,
                    mUnidadProductiva?.Codigo,
                    mUnidadProductiva?.UnidadMedidaId,
                    mUnidadProductiva?.UsuarioId,
                    mUnidadProductiva?.descripcion,
                    mUnidadProductiva?.nombre)

            // mUnidadProductiva?.CiudadId = 1
            val call = apiService?.postUnidadProductiva(postUnidadProductiva)
            call?.enqueue(object : Callback<Unidad_Productiva> {

                override fun onResponse(call: Call<Unidad_Productiva>?, response: Response<Unidad_Productiva>?) {
                    if (response != null && response.code() == 201) {
                        val idUP = response.body()?.Unidad_Productiva_Id
                        mUnidadProductiva?.Id_Remote = idUP
                        //mUnidadProductiva?.save()

                        //postLocalizacionUnidadProductiva
                        val postLocalizacionUnidadProductiva = LocalizacionUp(0,
                                "",
                                mUnidadProductiva?.Coordenadas,
                                if (mUnidadProductiva?.Direccion!=null) mUnidadProductiva.Direccion else "",
                                mUnidadProductiva?.DireccionAproximadaGps,
                                mUnidadProductiva?.Latitud,
                                mUnidadProductiva?.Longitud,
                                "",
                                "",
                                "",
                                mUnidadProductiva?.Unidad_Productiva_Id,
                                "")

                        val call = apiService?.postLocalizacionUnidadProductiva(postLocalizacionUnidadProductiva)
                        call?.enqueue(object : Callback<LocalizacionUp> {

                            override fun onResponse(call: Call<LocalizacionUp>?, response: Response<LocalizacionUp>?) {
                                if (response != null && response.code() == 201) {
                                    val idLocalizacion = response.body()?.Id
                                    mUnidadProductiva?.LocalizacionUpId=idLocalizacion
                                    mUnidadProductiva?.Estado_Sincronizacion = true
                                    mUnidadProductiva?.Estado_SincronizacionUpdate=true
                                    mUnidadProductiva?.update()
                                    //postLocalizacionUnidadProductiva

                                    //var mUnidadProductivaPost= SQLite.select()
                                     //       .from(Unidad_Productiva::class.java)
                                     //       .where(Unidad_Productiva_Table.Estado_Sincronizacion.eq(false)).orderBy(Unidad_Productiva_Table.Id,false).querySingle()
                                   // if(mUnidadProductivaPost!=null){
                                    //    syncData()
                                   // }else{
                                        postEventOk(RequestEventMainMenu.SYNC_EVENT)
                                   //}

                                } else {
                                    postEventError(RequestEventMainMenu.ERROR_EVENT, "Comprueba tu conexión")
                                }
                            }

                            override fun onFailure(call: Call<LocalizacionUp>?, t: Throwable?) {
                                postEventError(RequestEventMainMenu.ERROR_EVENT, "Comprueba tu conexión")
                            }
                        })
                        // postEventOk(RequestEventUP.SAVE_EVENT, getUPs(), mUnidadProductiva)
                    } else {
                        postEventError(RequestEventMainMenu.ERROR_EVENT, "Comprueba tu conexión")
                    }
                }

                override fun onFailure(call: Call<Unidad_Productiva>?, t: Throwable?) {
                    postEventError(RequestEventMainMenu.ERROR_EVENT, "Comprueba tu conexión")
                }

            })


        }else{
            postEventOk(RequestEventMainMenu.SYNC_EVENT)

        }
    }



    //region Events
    private fun postEventOk(type: Int) {
        postEvent(type, null,null,null)
    }


    private fun postEventError(type: Int,messageError:String?) {
        postEvent(type, null,null,messageError)
    }

    //Main Post Event
    private fun postEvent(type: Int, listModel1:MutableList<Object>?,model:Object?,errorMessage: String?) {
        val event = RequestEventMainMenu(type, listModel1, model, errorMessage)
        event.eventType = type
        eventBus?.post(event)
    }
    //endregion
    //endregion
}