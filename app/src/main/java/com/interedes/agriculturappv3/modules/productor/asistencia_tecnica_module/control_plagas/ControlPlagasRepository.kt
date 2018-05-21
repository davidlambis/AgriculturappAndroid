package com.interedes.agriculturappv3.modules.productor.asistencia_tecnica_module.control_plagas

import com.interedes.agriculturappv3.modules.models.cultivo.Cultivo
import com.interedes.agriculturappv3.modules.models.lote.Lote
import com.interedes.agriculturappv3.modules.models.control_plaga.ControlPlaga
import com.interedes.agriculturappv3.modules.models.control_plaga.ControlPlaga_Table
import com.interedes.agriculturappv3.modules.productor.asistencia_tecnica_module.control_plagas.events.ControlPlagasEvent
import com.interedes.agriculturappv3.libs.EventBus
import com.interedes.agriculturappv3.libs.GreenRobotEventBus
import com.interedes.agriculturappv3.modules.models.control_plaga.PostControlPlaga
import com.interedes.agriculturappv3.modules.models.cultivo.Cultivo_Table
import com.interedes.agriculturappv3.modules.models.unidad_productiva.Unidad_Productiva
import com.interedes.agriculturappv3.services.api.ApiInterface
import com.raizlabs.android.dbflow.kotlinextensions.delete
import com.raizlabs.android.dbflow.kotlinextensions.update
import com.raizlabs.android.dbflow.sql.language.SQLite
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class ControlPlagasRepository : IControlPlagas.Repository {


    var eventBus: EventBus? = null
    var apiService: ApiInterface? = null
    init {
        eventBus = GreenRobotEventBus()
        apiService = ApiInterface.create()
    }

    //region Métodos Interfaz
    override fun getListas() {
        val listUnidadProductiva = SQLite.select().from(Unidad_Productiva::class.java).queryList()
        val listLotes = SQLite.select().from(Lote::class.java).queryList()
        val listCultivos = SQLite.select().from(Cultivo::class.java).queryList()

        postEventListUnidadProductiva(ControlPlagasEvent.LIST_EVENT_UP, listUnidadProductiva, null)
        postEventListLotes(ControlPlagasEvent.LIST_EVENT_LOTE, listLotes, null)
        postEventListCultivos(ControlPlagasEvent.LIST_EVENT_CULTIVO, listCultivos, null)
    }

    override fun getListControlPlaga(cultivo_id: Long?) {
        val listaControlPlagas = getControlPlagas(cultivo_id)
        postEventOk(ControlPlagasEvent.READ_EVENT, listaControlPlagas, null);
    }

    override fun getControlPlagas(cultivo_id: Long?): List<ControlPlaga> {
        val listResponse: List<ControlPlaga>
        if (cultivo_id == null) {
            listResponse = SQLite.select().from(ControlPlaga::class.java).queryList()
        } else {
            listResponse = SQLite.select().from(ControlPlaga::class.java).where(ControlPlaga_Table.CultivoId.eq(cultivo_id)).queryList()
        }
        return listResponse
    }

    override fun getCultivo(cultivo_id: Long?) {
        val cultivo = SQLite.select().from(Cultivo::class.java).where(Cultivo_Table.CultivoId.eq(cultivo_id)).querySingle()
        postEventOkCultivo(ControlPlagasEvent.GET_EVENT_CULTIVO, cultivo)
    }

    override fun deleteControlPlaga(controlPlaga: ControlPlaga, cultivo_id: Long?,checkConection: Boolean) {
        //TODO se valida estado de sincronizacion  para eliminar
        if (controlPlaga.Estado_Sincronizacion == true) {
            //TODO si existe coneccion a internet se elimina
            if(checkConection){
                val call = apiService?.deleteControlPlaga(controlPlaga.ControlPlagaId!!)
                call?.enqueue(object : Callback<PostControlPlaga> {
                    override fun onResponse(call: Call<PostControlPlaga>?, response: Response<PostControlPlaga>?) {
                        if (response != null && response.code() == 204 || response?.code() == 200) {
                            controlPlaga.delete()
                            postEventOk(ControlPlagasEvent.DELETE_EVENT, getControlPlagas(controlPlaga.CultivoId),controlPlaga)
                        }
                    }
                    override fun onFailure(call: Call<PostControlPlaga>?, t: Throwable?) {
                        postEventError(ControlPlagasEvent.ERROR_EVENT, "Comprueba tu conexión")
                    }
                })
            }else{
                postEventError(ControlPlagasEvent.ERROR_VERIFICATE_CONECTION, null)
            }
        } else {
            //TODO No sincronizado, Eliminar de manera local
            controlPlaga.delete()
            postEventOk(ControlPlagasEvent.DELETE_EVENT, getControlPlagas(controlPlaga.CultivoId), controlPlaga)
        }
    }

    override fun updateControlPlaga(controlPlaga: ControlPlaga?,checkConection: Boolean) {
        //TODO si existe coneccion a internet
        if(checkConection){

            //TODO se valida estado de sincronizacion  para actualizar
            if (controlPlaga?.Estado_Sincronizacion == true) {

                val cultivo = SQLite.select().from(Cultivo::class.java).where(Cultivo_Table.CultivoId.eq(controlPlaga.CultivoId)).querySingle()

                val postControlPlaga = PostControlPlaga(
                        controlPlaga?.Id_Remote,
                        cultivo?.Id_Remote,
                        controlPlaga?.Dosis,
                        controlPlaga?.EnfermedadesId,
                        controlPlaga?.getFechaAplicacionFormatApi(),
                        controlPlaga?.TratamientoId,
                        controlPlaga?.UnidadMedidaId,
                        controlPlaga?.getFechaErradicacionFormatApi(),
                        controlPlaga?.EstadoErradicacion
                )
                val call = apiService?.updateControlPlaga(postControlPlaga,controlPlaga?.Id_Remote!!)
                call?.enqueue(object : Callback<PostControlPlaga> {
                    override fun onResponse(call: Call<PostControlPlaga>?, response: Response<PostControlPlaga>?) {
                        if (response != null && response.code() == 201 || response?.code() == 200) {
                            var controlPlagaResponse= response.body()
                            controlPlaga.Estado_Sincronizacion = true
                            controlPlaga?.Estado_SincronizacionUpdate = true
                            controlPlaga.update()
                            postEventOk(ControlPlagasEvent.UPDATE_EVENT_OK, getControlPlagas(controlPlaga.CultivoId),controlPlaga)
                        } else {
                            postEventError(ControlPlagasEvent.ERROR_EVENT, "Comprueba tu conexión")
                        }
                    }
                    override fun onFailure(call: Call<PostControlPlaga>?, t: Throwable?) {
                        postEventError(ControlPlagasEvent.ERROR_EVENT, "Comprueba tu conexión")
                    }
                })

            }
            //TODO con  conexion a internet, pero no se ha sincronizado, actualizacion local
            else {
                controlPlaga?.Estado_SincronizacionUpdate = false
                controlPlaga?.update()
                postEventOk(ControlPlagasEvent.UPDATE_EVENT_OK,  getControlPlagas(controlPlaga?.CultivoId),controlPlaga)
            }
        }
        //TODO sin conexion a internet, actualizacion local
        else{
            controlPlaga?.Estado_SincronizacionUpdate = false
            controlPlaga?.update()
            postEventOk(ControlPlagasEvent.UPDATE_EVENT_OK, getControlPlagas(controlPlaga?.CultivoId),controlPlaga)
        }
    }



    //endregion

    //region Events
    private fun postEventListUnidadProductiva(type: Int, listUp: List<Unidad_Productiva>?, messageError: String?) {
        var upMutable = listUp as MutableList<Object>
        postEvent(type, upMutable, null, messageError)
    }

    private fun postEventListLotes(type: Int, listLote: List<Lote>?, messageError: String?) {
        var loteMutable = listLote as MutableList<Object>
        postEvent(type, loteMutable, null, messageError)
    }

    private fun postEventListCultivos(type: Int, listCultivo: List<Cultivo>?, messageError: String?) {
        var cultivoMutable = listCultivo as MutableList<Object>
        postEvent(type, cultivoMutable, null, messageError)
    }

    private fun postEventOk(type: Int, controlPlagas: List<ControlPlaga>?, controlPlaga: ControlPlaga?) {
        val controlPlagaListMutable = controlPlagas as MutableList<Object>
        var controlPlagaMutable: Object? = null
        if (controlPlaga != null) {
            controlPlagaMutable = controlPlaga as Object
        }
        postEvent(type, controlPlagaListMutable, controlPlagaMutable, null)
    }

    private fun postEventOkCultivo(type: Int, cultivo: Cultivo?) {
        var CultivoMutable: Object? = null
        if (cultivo != null) {
            CultivoMutable = cultivo as Object
        }
        postEvent(type, null, CultivoMutable, null)
    }

    private fun postEventError(type: Int,messageError:String?) {
        postEvent(type, null,null,messageError)
    }

    //Main Post Event
    private fun postEvent(type: Int, listModel1: MutableList<Object>?, model: Object?, errorMessage: String?) {
        val event = ControlPlagasEvent(type, listModel1, model, errorMessage)
        event.eventType = type
        eventBus?.post(event)
    }
    //endregion
}