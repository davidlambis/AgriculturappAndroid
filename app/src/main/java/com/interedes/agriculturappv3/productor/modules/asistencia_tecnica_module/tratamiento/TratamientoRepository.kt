package com.interedes.agriculturappv3.productor.modules.asistencia_tecnica_module.tratamiento

import android.util.Log
import com.interedes.agriculturappv3.productor.models.cultivo.Cultivo
import com.interedes.agriculturappv3.productor.models.lote.Lote
import com.interedes.agriculturappv3.productor.models.tratamiento.Tratamiento
import com.interedes.agriculturappv3.productor.models.control_plaga.ControlPlaga
import com.interedes.agriculturappv3.productor.models.control_plaga.ControlPlaga_Table
import com.interedes.agriculturappv3.productor.models.unidad_medida.Unidad_Medida
import com.interedes.agriculturappv3.productor.modules.asistencia_tecnica_module.tratamiento.events.TratamientoEvent
import com.interedes.agriculturappv3.libs.EventBus
import com.interedes.agriculturappv3.libs.GreenRobotEventBus
import com.interedes.agriculturappv3.productor.models.insumos.Insumo
import com.interedes.agriculturappv3.productor.models.insumos.Insumo_Table
import com.interedes.agriculturappv3.productor.models.tratamiento.Tratamiento_Table
import com.interedes.agriculturappv3.productor.models.tratamiento.calificacion.Calificacion_Tratamiento
import com.interedes.agriculturappv3.productor.models.tratamiento.calificacion.Calificacion_Tratamiento_Table
import com.interedes.agriculturappv3.productor.models.tratamiento.calificacion.PostCalificacion
import com.interedes.agriculturappv3.productor.models.tratamiento.calificacion.ResponseCalificacion
import com.interedes.agriculturappv3.productor.models.unidad_medida.Unidad_Medida_Table
import com.interedes.agriculturappv3.productor.models.unidad_productiva.Unidad_Productiva
import com.interedes.agriculturappv3.productor.models.usuario.Usuario
import com.interedes.agriculturappv3.productor.models.usuario.Usuario_Table
import com.interedes.agriculturappv3.services.api.ApiInterface
import com.interedes.agriculturappv3.services.listas.Listas
import com.raizlabs.android.dbflow.kotlinextensions.and
import com.raizlabs.android.dbflow.kotlinextensions.save
import com.raizlabs.android.dbflow.kotlinextensions.update
import com.raizlabs.android.dbflow.sql.language.SQLite

import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response


class TratamientoRepository : ITratamiento.Repository {


    var apiService: ApiInterface? = null
    var eventBus: EventBus? = null

    init {
        eventBus = GreenRobotEventBus()
        apiService = ApiInterface.create()
    }

    override fun registerControlPlaga(controlPlaga: ControlPlaga, cultivo_id: Long?) {
        controlPlaga.save()
        val list_control_plagas = getControlPlagasByCultivo(cultivo_id)
        postEventControlPlaga(TratamientoEvent.SAVE_CONTROL_PLAGA_EVENT, list_control_plagas)
    }

    override fun getTratamiento(tratamientoId: Long?) {
        val firstTratamientoInsumo = SQLite.select().from(Tratamiento::class.java).where(Tratamiento_Table.Id.eq(tratamientoId)).querySingle()
        var firtsInsumo= SQLite.select().from(Insumo::class.java).where(Insumo_Table.Id.eq(firstTratamientoInsumo?.InsumoId)).querySingle()


        var calificaciones= SQLite.select()
                .from(Calificacion_Tratamiento::class.java)
                .where(Calificacion_Tratamiento_Table.TratamientoId.eq(tratamientoId)).queryList()


        val usuarioLogued = SQLite.select().from(Usuario::class.java).where(Usuario_Table.UsuarioRemembered?.eq(true)).querySingle()


        var calificacionByUser= SQLite.select()
                .from(Calificacion_Tratamiento::class.java)
                .where(Calificacion_Tratamiento_Table.TratamientoId.eq(tratamientoId)
                        .and(Calificacion_Tratamiento_Table.User_Id.eq(usuarioLogued?.Id.toString())))
                .querySingle()

        var calificacion:Calificacion_Tratamiento?=null
        if(calificaciones.size>0){
            var sumacalificacion:Double?=0.0
            for (item in calificaciones){
                sumacalificacion=sumacalificacion!!+ item.Valor!!
            }
            var promedio= sumacalificacion!! /calificaciones.size

            if(calificacionByUser!=null){
                calificacion=Calificacion_Tratamiento(User_Id = calificacionByUser.User_Id,Valor = calificacionByUser?.Valor,Valor_Promedio = promedio)
            }else{
                calificacion=Calificacion_Tratamiento(User_Id =null,Valor = null,Valor_Promedio = promedio)
            }

        }else{
            calificacion=Calificacion_Tratamiento(User_Id = null,Valor = 0.0,Valor_Promedio = 0.0)
        }


        if(firstTratamientoInsumo!=null){
            firstTratamientoInsumo?.CalificacionPromedio=calificacion.Valor_Promedio
            firstTratamientoInsumo.update()
        }


        postEventOk(TratamientoEvent.SET_EVENT, firstTratamientoInsumo)
        postEventInsumo(TratamientoEvent.EVENT_INSUMO, firtsInsumo)
        postEventCalificacion(TratamientoEvent.EVENT_CALIFICACION_TRATAMIENTO, calificacion)
    }

    override fun sendCalificationTratamietno(tratamiento: Tratamiento?,calificacion:Double?) {

        val usuarioLogued = SQLite.select().from(Usuario::class.java).where(Usuario_Table.UsuarioRemembered?.eq(true)).querySingle()

        val query = Listas.queryGeneral("userId", usuarioLogued?.Id.toString())

        val call_usuario = apiService?.getVerificateCalificationUser( query)

        call_usuario?.enqueue(object : Callback<ResponseCalificacion> {
            override fun onResponse(call: Call<ResponseCalificacion>?, response: Response<ResponseCalificacion>?) {
                if (response != null && response.code() == 200) {

                    //retorna la lista de califcaciones del usuario
                    val valificacionCalificacion: MutableList<Calificacion_Tratamiento>? = response.body()?.value!!

                    //se verifica si el tratamiento a calificar ya esta calificado
                    var verificateIsCalifcated= valificacionCalificacion?.filter { calificacion: Calificacion_Tratamiento -> calificacion.TratamientoId==tratamiento?.Id }

                    if(verificateIsCalifcated?.size!!>0){
                        var calificacionTratamiento:Calificacion_Tratamiento?=null
                        for (item in verificateIsCalifcated){
                            item.save()
                            calificacionTratamiento=item
                        }

                        getTratamiento(calificacionTratamiento?.TratamientoId)
                        postEventOk(TratamientoEvent.CALIFICATE_EVENT_EXIST, null)

                    }else{
                        //POST CALIFICAR
                        val postCalificacion = PostCalificacion(Id = 0,
                                Nombre = "",
                                TratamientoId = tratamiento?.Id,
                                Valor = calificacion,
                                userId =  usuarioLogued?.Id.toString()
                        )
                        val call = apiService?.postCalificacionTratamiento(postCalificacion)
                        call?.enqueue(object : Callback<Calificacion_Tratamiento> {
                            override fun onResponse(call: Call<Calificacion_Tratamiento>?, response: Response<Calificacion_Tratamiento>?) {
                                if (response != null && response.code() == 201 || response?.code() == 200) {
                                    val calificacion: Calificacion_Tratamiento? = response.body()
                                    calificacion?.save()
                                    getTratamiento(calificacion?.TratamientoId)
                                    postEventOk(TratamientoEvent.REQUEST_CALIFICATE_EVENT_TRATAMIENTO_OK, null)
                                } else {
                                    postEventError(TratamientoEvent.ERROR_EVENT, "Comprueba tu conexión")
                                }
                            }
                            override fun onFailure(call: Call<Calificacion_Tratamiento>?, t: Throwable?) {
                                postEventError(TratamientoEvent.ERROR_EVENT, "Comprueba tu conexión")
                            }
                        })
                    }
                } else {
                    postEventError(TratamientoEvent.ERROR_EVENT, "La peticion fallo, compruebe su conexión")
                    Log.e("Get Login User Response", response?.body().toString())
                }
            }

            override fun onFailure(call: Call<ResponseCalificacion>?, t: Throwable?) {
                postEventError(TratamientoEvent.ERROR_EVENT, "No puede ingresar, compruebe su conexión")
                Log.e("Failure Get Login User", t?.message.toString())
            }

        })


    }

    override fun getListas() {
        val listUnidadProductiva = SQLite.select().from(Unidad_Productiva::class.java).queryList()
        val listLotes = SQLite.select().from(Lote::class.java).queryList()
        val listCultivos = SQLite.select().from(Cultivo::class.java).queryList()
        val listUnidadMedida = SQLite.select().from(Unidad_Medida::class.java).where(Unidad_Medida_Table.CategoriaMedidaId.eq(4)).queryList()
        postEventListUnidadProductiva(TratamientoEvent.LIST_EVENT_UP, listUnidadProductiva, null)
        postEventListLotes(TratamientoEvent.LIST_EVENT_LOTE, listLotes, null)
        postEventListCultivos(TratamientoEvent.LIST_EVENT_CULTIVO, listCultivos, null);
        postEventListUnidadMedida(TratamientoEvent.LIST_EVENT_UNIDAD_MEDIDA, listUnidadMedida, null);
    }
    //endregion

    //region Methods
    private fun getControlPlagasByCultivo(cultivo_id: Long?): List<ControlPlaga>? {
        return SQLite.select().from(ControlPlaga::class.java).where(ControlPlaga_Table.CultivoId.eq(cultivo_id)).queryList()
    }
    //endregion

    //region Events
    //Main Post Event

    private fun postEventInsumo(type: Int, insumo: Insumo?) {
        val  objectMutable= insumo as Object
        postEvent(type, null, objectMutable, null)
    }

    private fun postEventCalificacion(type: Int, calificacion: Calificacion_Tratamiento?) {
        val  objectMutable= calificacion as Object
        postEvent(type, null, objectMutable, null)
    }


    private fun postEventControlPlaga(type: Int, listControlPlagas: List<ControlPlaga>?) {
        val listControlPlagasMutable = listControlPlagas as MutableList<Object>
        postEvent(type, listControlPlagasMutable, null, null)
    }

    private fun postEventOk(type: Int, tratamiento: Tratamiento?) {
        //val listTratamientostMutable = listTratamientos as MutableList<Object>
        var tratamientoMutable: Object? = null
        if (tratamiento != null) {
            tratamientoMutable = tratamiento as Object
        }
        postEvent(type, null, tratamientoMutable, null)
    }

    private fun postEventListUnidadProductiva(type: Int, listUnidadProductiva: List<Unidad_Productiva>?, messageError: String?) {
        val upMutable = listUnidadProductiva as MutableList<Object>
        postEvent(type, upMutable, null, messageError)
    }

    private fun postEventListLotes(type: Int, listLotes: List<Lote>?, messageError: String?) {
        val loteMutable = listLotes as MutableList<Object>
        postEvent(type, loteMutable, null, messageError)
    }

    private fun postEventListCultivos(type: Int, listCultivos: List<Cultivo>?, messageError: String?) {
        val cultivoMutable = listCultivos as MutableList<Object>
        postEvent(type, cultivoMutable, null, messageError)
    }

    private fun postEventListUnidadMedida(type: Int, listUnidadMedida: List<Unidad_Medida>?, messageError: String?) {
        val unidadMedidaMutable = listUnidadMedida as MutableList<Object>
        postEvent(type, unidadMedidaMutable, null, messageError)
    }

    private fun postEventError(type: Int, messageError: String) {
        postEvent(type, null, null, messageError)
    }

    private fun postEvent(type: Int, listModel: MutableList<Object>?, model: Object?, errorMessage: String?) {
        val event = TratamientoEvent(type, listModel, model, errorMessage)
        event.eventType = type
        eventBus?.post(event)
    }
    //endregion

}