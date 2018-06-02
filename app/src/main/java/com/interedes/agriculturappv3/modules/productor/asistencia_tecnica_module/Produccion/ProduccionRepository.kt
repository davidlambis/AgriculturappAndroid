package com.interedes.agriculturappv3.modules.productor.asistencia_tecnica_module.Produccion

import com.interedes.agriculturappv3.modules.models.cultivo.Cultivo
import com.interedes.agriculturappv3.modules.models.lote.Lote
import com.interedes.agriculturappv3.modules.models.produccion.Produccion
import com.interedes.agriculturappv3.modules.models.produccion.Produccion_Table
import com.interedes.agriculturappv3.modules.models.unidad_medida.Unidad_Medida
import com.interedes.agriculturappv3.modules.productor.asistencia_tecnica_module.Produccion.events.RequestEventProduccion
import com.interedes.agriculturappv3.libs.EventBus
import com.interedes.agriculturappv3.libs.GreenRobotEventBus
import com.interedes.agriculturappv3.modules.models.cultivo.Cultivo_Table
import com.interedes.agriculturappv3.modules.models.lote.Lote_Table
import com.interedes.agriculturappv3.modules.models.produccion.PostProduccion
import com.interedes.agriculturappv3.modules.models.unidad_medida.Unidad_Medida_Table
import com.interedes.agriculturappv3.modules.models.unidad_productiva.Unidad_Productiva
import com.interedes.agriculturappv3.modules.models.unidad_productiva.Unidad_Productiva_Table
import com.interedes.agriculturappv3.modules.models.usuario.Usuario
import com.interedes.agriculturappv3.modules.models.usuario.Usuario_Table
import com.interedes.agriculturappv3.services.api.ApiInterface
import com.raizlabs.android.dbflow.kotlinextensions.delete
import com.raizlabs.android.dbflow.kotlinextensions.save
import com.raizlabs.android.dbflow.kotlinextensions.update
import com.raizlabs.android.dbflow.sql.language.SQLite

import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.text.SimpleDateFormat

/**
 * Created by usuario on 20/03/2018.
 */
class ProduccionRepository :IMainProduccion.Repository {


    var eventBus: EventBus? = null
    var apiService: ApiInterface? = null
    init {
        eventBus = GreenRobotEventBus()
        apiService = ApiInterface.create()
    }

    //region METHODS
    fun getLastUserLogued(): Usuario? {
        val usuarioLogued = SQLite.select().from(Usuario::class.java).where(Usuario_Table.UsuarioRemembered.eq(true)).querySingle()
        return usuarioLogued
    }

    override fun saveProduccion(produccion: Produccion,cultivo_id:Long,checkConection:Boolean) {

        produccion.UsuarioId= getLastUserLogued()?.Id

        //TODO si existe conexion a internet
        if(checkConection){
            //TODO Ciudad Id de la tabla del backend
            val cultivo = SQLite.select().from(Cultivo::class.java).where(Cultivo_Table.CultivoId.eq(cultivo_id)).querySingle()
            if (cultivo?.EstadoSincronizacion == true) {
                val format1 = SimpleDateFormat("yyyy-MM-dd")
                val fecha_inicio = format1.format(produccion.FechaInicioProduccion)
                val fecha_fecha_fin = format1.format(produccion.FechaFinProduccion)
                val postProduccion = PostProduccion(
                        0
                        ,
                        cultivo.Id_Remote,
                        fecha_inicio,
                        fecha_fecha_fin,
                        produccion.UnidadMedidaId,
                        produccion.ProduccionReal
                )
                val call = apiService?.postProduccion(postProduccion)
                call?.enqueue(object : Callback<PostProduccion> {
                    override fun onResponse(call: Call<PostProduccion>?, response: Response<PostProduccion>?) {
                        if (response != null && response.code() == 201 || response?.code() == 200) {

                            produccion.Id_Remote = response.body()?.Id!!


                            val las_prduccion = getLastProduccion()
                            if (las_prduccion == null) {
                                produccion.ProduccionId = 1
                            } else {
                                produccion.ProduccionId = las_prduccion.ProduccionId!! + 1
                            }



                            produccion.Estado_Sincronizacion = true
                            produccion?.Estado_SincronizacionUpdate = true
                            produccion.save()
                            postEventOk(RequestEventProduccion.SAVE_EVENT, getProductions(cultivo_id), produccion)
                        } else {
                            postEventError(RequestEventProduccion.ERROR_EVENT, "Comprueba tu conexión")
                        }
                    }
                    override fun onFailure(call: Call<PostProduccion>?, t: Throwable?) {
                        postEventError(RequestEventProduccion.ERROR_EVENT, "Comprueba tu conexión")
                    }
                })
            }
            //TODO con conexion a internet sin sincronizacion, registro local
            else {
                saveProduccionLocal(produccion,cultivo_id)
            }
        }
        //TODO sin conexion a internet, registro local
        else{
            saveProduccionLocal(produccion,cultivo_id)
        }
    }

    override fun saveProduccionLocal(produccion: Produccion,cultivo_id:Long){
        val las_prduccion = getLastProduccion()
        if (las_prduccion == null) {
            produccion.ProduccionId = 1
        } else {
            produccion.ProduccionId = las_prduccion.ProduccionId!! + 1
        }
        produccion.save()
        var listProduccion = getProductions(cultivo_id)
        postEventOk(RequestEventProduccion.SAVE_EVENT,listProduccion,null);
    }

    override fun updateProduccion(produccion: Produccion,cultivo_id:Long,checkConection:Boolean){
        //TODO si existe coneccion a internet
        if(checkConection){
            val cultivo = SQLite.select().from(Cultivo::class.java).where(Cultivo_Table.CultivoId.eq(cultivo_id)).querySingle()
            //TODO se valida estado de sincronizacion  para actualizar
            if (produccion.Estado_Sincronizacion == true) {

                val format1 = SimpleDateFormat("yyyy-MM-dd")
                val fecha_inicio = format1.format(produccion.FechaInicioProduccion)
                val fecha_fecha_fin = format1.format(produccion.FechaFinProduccion)
                val postProduccion = PostProduccion(
                        produccion.Id_Remote,
                        cultivo?.Id_Remote,
                        fecha_inicio,
                        fecha_fecha_fin,
                        produccion.UnidadMedidaId,
                        produccion.ProduccionReal
                )
                val call = apiService?.updateProduccion(postProduccion, produccion.Id_Remote!!)
                call?.enqueue(object : Callback<PostProduccion> {
                    override fun onResponse(call: Call<PostProduccion>?, response: Response<PostProduccion>?) {
                        if (response != null && response.code() == 200) {


                            produccion?.Estado_SincronizacionUpdate = true
                            produccion.update()
                            postEventOk(RequestEventProduccion.UPDATE_EVENT, getProductions(cultivo_id), produccion)
                        } else {
                            postEventError(RequestEventProduccion.ERROR_EVENT, "Comprueba tu conexión")
                        }
                    }
                    override fun onFailure(call: Call<PostProduccion>?, t: Throwable?) {
                        postEventError(RequestEventProduccion.ERROR_EVENT, "Comprueba tu conexión")
                    }
                })

            }
            //TODO con  conexion a internet, pero no se ha sincronizado, actualizacion local
            else {
                produccion?.Estado_SincronizacionUpdate = false
                produccion.update()
                postEventOk(RequestEventProduccion.UPDATE_EVENT, getProductions(cultivo_id), produccion)
            }
        }
        //TODO sin conexion a internet, actualizacion local
        else{
            produccion?.Estado_SincronizacionUpdate = false
            produccion.update()
            postEventOk(RequestEventProduccion.UPDATE_EVENT, getProductions(cultivo_id), produccion)
        }
    }

    override fun deleteProduccion(produccion: Produccion,cultivo_id: Long?,checkConection:Boolean) {
        //TODO se valida estado de sincronizacion  para eliminar
        if (produccion.Estado_Sincronizacion == true) {
            //TODO si existe coneccion a internet se elimina
            if(checkConection){
                val call = apiService?.deleteProduccion(produccion.Id_Remote!!)
                call?.enqueue(object : Callback<PostProduccion> {
                    override fun onResponse(call: Call<PostProduccion>?, response: Response<PostProduccion>?) {
                        if (response != null && response.code() == 204 || response?.code() == 200) {
                            produccion.delete()
                            postEventOk(RequestEventProduccion.DELETE_EVENT, getProductions(cultivo_id), produccion)
                        }
                    }
                    override fun onFailure(call: Call<PostProduccion>?, t: Throwable?) {
                        postEventError(RequestEventProduccion.ERROR_EVENT, "Comprueba tu conexión")
                    }
                })
            }else{
                postEventError(RequestEventProduccion.ERROR_VERIFICATE_CONECTION, null)
            }
        } else {
            //TODO No sincronizado, Eliminar de manera local
            produccion.delete()
            postEventOk(RequestEventProduccion.DELETE_EVENT, getProductions(cultivo_id), produccion)
        }
    }

    override fun getListProduccion(cultivo_id:Long?) {
        var listaProduccion = getProductions(cultivo_id)
        postEventOk(RequestEventProduccion.READ_EVENT,listaProduccion,null);
    }

    override fun getCultivo(cultivo_id:Long?) {
        var cultivo = SQLite.select().from(Cultivo::class.java!!).where(Cultivo_Table.CultivoId.eq(cultivo_id)).querySingle()
        postEventOkCultivo(RequestEventProduccion.GET_EVENT_CULTIVO,cultivo)
    }

    override fun getListas() {
        var usuario=getLastUserLogued()

        val listUnidadProductiva: List<Unidad_Productiva> = SQLite.select().from(Unidad_Productiva::class.java)
                .where(Unidad_Productiva_Table.UsuarioId.eq(usuario?.Id))
                .queryList()

        val listLotes = SQLite.select().from(Lote::class.java)
                .where(Lote_Table.UsuarioId.eq(usuario?.Id))
                .queryList()


        var listCultivos = SQLite.select().from(Cultivo::class.java!!)
                .where(Cultivo_Table.UsuarioId.eq(usuario?.Id))
                .queryList()



        val listUnidadMedida = SQLite.select().from(Unidad_Medida::class.java).where(Unidad_Medida_Table.CategoriaMedidaId.eq(3)).queryList()

        postEventListUnidadMedida(RequestEventProduccion.LIST_EVENT_UNIDAD_MEDIDA,listUnidadMedida,null);
        postEventListUnidadProductiva(RequestEventProduccion.LIST_EVENT_UP,listUnidadProductiva,null);
        postEventListLotes(RequestEventProduccion.LIST_EVENT_LOTE,listLotes,null);
        postEventListCultivos(RequestEventProduccion.LIST_EVENT_CULTIVO,listCultivos,null);
    }

    override fun getProductions(cultivo_id:Long?):List<Produccion> {
        var listResponse:List<Produccion>?=null
        if(cultivo_id==null){
            listResponse = SQLite.select().from(Produccion::class.java!!)
                    .where(Produccion_Table.UsuarioId.eq(getLastUserLogued()?.Id))
                    .queryList()
        }else{
            listResponse = SQLite.select().from(Produccion::class.java!!)
                    .where(Produccion_Table.CultivoId.eq(cultivo_id))
                    .and(Produccion_Table.UsuarioId.eq(getLastUserLogued()?.Id))
                    .queryList()
        }
        return listResponse;
    }

    fun getLastProduccion(): Produccion? {
        val lastProduccion = SQLite.select().from(Produccion::class.java).where().orderBy(Produccion_Table.ProduccionId, false).querySingle()
        return lastProduccion
    }
    //endregion

    //region Events

    private fun postEventListUnidadMedida(type: Int, listUnidadMedida:List<Unidad_Medida>?, messageError:String?) {
        var upMutable= listUnidadMedida as MutableList<Object>
        postEvent(type, upMutable,null,messageError)
    }

    private fun postEventListUnidadProductiva(type: Int, listUnidadMedida:List<Unidad_Productiva>?, messageError:String?) {
        var upMutable= listUnidadMedida as MutableList<Object>
        postEvent(type, upMutable,null,messageError)
    }

    private fun postEventListLotes(type: Int, listUnidadMedida:List<Lote>?, messageError:String?) {
        var upMutable= listUnidadMedida as MutableList<Object>
        postEvent(type, upMutable,null,messageError)
    }

    private fun postEventListCultivos(type: Int, listUnidadMedida:List<Cultivo>?, messageError:String?) {
        var upMutable= listUnidadMedida as MutableList<Object>
        postEvent(type, upMutable,null,messageError)
    }

    private fun postEventOkCultivo(type: Int,  cultivo: Cultivo?) {
        var CultivoMutable:Object?=null
        if(cultivo!=null){
            CultivoMutable = cultivo as Object
        }
        postEvent(type,null,CultivoMutable,null)
    }

    private fun postEventOk(type: Int, producciones: List<Produccion>?, produccion:Produccion?) {
        var produccionListMitable= producciones as MutableList<Object>
        var ProducciconMutable:Object?=null
        if(produccion!=null){
            ProducciconMutable = produccion as Object
        }
        postEvent(type, produccionListMitable,ProducciconMutable,null)
    }

    private fun postEventError(type: Int,messageError:String?) {
        postEvent(type, null,null,messageError)
    }

    //Main Post Event
    private fun postEvent(type: Int, listModel1:MutableList<Object>?,model:Object?,errorMessage: String?) {
        val event = RequestEventProduccion(type, listModel1, model, errorMessage)
        event.eventType = type
        eventBus?.post(event)
    }
    //endregion

}