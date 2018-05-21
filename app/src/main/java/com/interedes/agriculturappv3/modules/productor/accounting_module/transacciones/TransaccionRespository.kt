package com.interedes.agriculturappv3.modules.productor.accounting_module.transacciones

import com.interedes.agriculturappv3.modules.productor.accounting_module.transacciones.events.RequestEventTransaccion
import com.interedes.agriculturappv3.libs.EventBus
import com.interedes.agriculturappv3.libs.GreenRobotEventBus
import com.interedes.agriculturappv3.modules.models.cultivo.Cultivo
import com.interedes.agriculturappv3.modules.models.cultivo.Cultivo_Table
import com.interedes.agriculturappv3.modules.models.lote.Lote
import com.interedes.agriculturappv3.modules.models.unidad_productiva.Unidad_Productiva
import com.interedes.agriculturappv3.modules.models.unidad_medida.Unidad_Medida
import com.interedes.agriculturappv3.modules.models.usuario.Usuario
import com.interedes.agriculturappv3.modules.models.usuario.Usuario_Table
import com.interedes.agriculturappv3.modules.models.ventas.*
import com.interedes.agriculturappv3.modules.models.ventas.RequestApi.PostTercero
import com.interedes.agriculturappv3.modules.models.ventas.RequestApi.PostTransaccion
import com.interedes.agriculturappv3.services.api.ApiInterface
import com.raizlabs.android.dbflow.kotlinextensions.delete
import com.raizlabs.android.dbflow.kotlinextensions.save
import com.raizlabs.android.dbflow.kotlinextensions.update
import com.raizlabs.android.dbflow.sql.language.SQLite
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class TransaccionRespository: IMainViewTransacciones.Repository {


    var eventBus: EventBus? = null
    var apiService: ApiInterface? = null
    init {
        eventBus = GreenRobotEventBus()
        apiService = ApiInterface.create()
    }

    override fun getListas() {
        var listUnidadProductiva = SQLite.select().from(Unidad_Productiva::class.java).queryList()
        var listLotes = SQLite.select().from(Lote::class.java).queryList()
        var listCultivos = SQLite.select().from(Cultivo::class.java).queryList()


        //var listCategoriasPuk= Listas.listCategoriaPuk()
        //var listPuk= Listas.listPuk()

        var listCategoriasPuk= SQLite.select().from(CategoriaPuk::class.java).queryList()
        var listPuk= SQLite.select().from(Puk::class.java).queryList()

        postEventListUnidadProductiva(RequestEventTransaccion.LIST_EVENT_UP,listUnidadProductiva,null);
        postEventListLotes(RequestEventTransaccion.LIST_EVENT_LOTE,listLotes,null);
        postEventListCultivos(RequestEventTransaccion.LIST_EVENT_CULTIVO,listCultivos,null);

        postEventListCategoriaPuk(RequestEventTransaccion.LIST_EVENT_CATEGORIA_PUK,listCategoriasPuk,null);
        postEventListPuk(RequestEventTransaccion.LIST_EVENT_PUK,listPuk,null);
    }

    fun getLastUserLogued(): Usuario? {
        val usuarioLogued = SQLite.select().from(Usuario::class.java).where(Usuario_Table.UsuarioRemembered.eq(true)).querySingle()
        return usuarioLogued
    }


    override fun getListTransacciones(cultivo_id: Long?,typeTransaccion:Long?) {
        var listaProduccion = getTransaccion(cultivo_id,typeTransaccion)
        postEventOk(RequestEventTransaccion.READ_EVENT,listaProduccion,null);
    }

    override fun getTransaccion(cultivo_id: Long?,typeTransaccion:Long?): List<Transaccion> {
        var listResponse: List<Transaccion>?
        if(cultivo_id==null){
            listResponse = SQLite.select().from(Transaccion::class.java).where(Transaccion_Table.CategoriaPuk_Id.eq(typeTransaccion)).queryList()
        }else{
            listResponse = SQLite.select().from(Transaccion::class.java).where(Transaccion_Table.Cultivo_Id.eq(cultivo_id)).and(Transaccion_Table.CategoriaPuk_Id.eq(typeTransaccion)).queryList()
        }
        return listResponse;
    }

    override fun saveTransaccion(transaccion: Transaccion, cultivo_id: Long?,checkConection:Boolean) {

        transaccion.UsuarioId= getLastUserLogued()?.Id

        var terceroLocal=Tercero(Id = transaccion.TerceroId,Nombre = transaccion.Nombre_Tercero,Apellido = "",NitRut = transaccion.Identificacion_Tercero)

        if(checkConection){
            val cultivo = SQLite.select().from(Cultivo::class.java).where(Cultivo_Table.Id.eq(cultivo_id)).querySingle()
            if (cultivo?.EstadoSincronizacion == true) {

                val postTercero = PostTercero(
                        0,
                        terceroLocal.Nombre,
                        terceroLocal.Apellido,
                        terceroLocal.NitRut,
                        ""
                )
                val postTrecero = apiService?.postTercero(postTercero)
                postTrecero?.enqueue(object : Callback<PostTercero> {
                    override fun onResponse(call: Call<PostTercero>?, response: Response<PostTercero>?) {
                        if (response != null && response.code() == 201 || response?.code() == 200) {
                            terceroLocal.Id = response.body()?.Id!!
                            terceroLocal.Estado_Sincronizacion = true
                            transaccion.Estado_SincronizacionUpdate = true
                            terceroLocal.save()
                            val postTransaccion = PostTransaccion(
                                    0,
                                    transaccion.Concepto,
                                    transaccion.EstadoId,
                                    transaccion.getFechaTransacccionFormatApi(),
                                    transaccion.NaturalezaId,
                                    transaccion.PucId,
                                    terceroLocal.Id,
                                    transaccion.Valor_Total,
                                    transaccion.Cantidad,
                                    transaccion.Cultivo_Id,
                                    getLastUserLogued()?.Id
                            )
                            val call = apiService?.postTransaccion(postTransaccion)
                            call?.enqueue(object : Callback<PostTransaccion> {
                                override fun onResponse(call: Call<PostTransaccion>?, response: Response<PostTransaccion>?) {
                                    if (response != null && response.code() == 201 || response?.code() == 200) {
                                        transaccion.Id = response.body()?.Id!!
                                        transaccion.Estado_Sincronizacion = true
                                        transaccion.Estado_SincronizacionUpdate = true
                                        transaccion.TerceroId=terceroLocal.Id
                                        transaccion.save()
                                        var listProduccion = getTransaccion(cultivo_id,transaccion.CategoriaPuk_Id)
                                        postEventOk(RequestEventTransaccion.SAVE_EVENT, listProduccion, transaccion)
                                    } else {
                                        postEventError(RequestEventTransaccion.ERROR_EVENT, "Comprueba tu conexión")
                                    }
                                }
                                override fun onFailure(call: Call<PostTransaccion>?, t: Throwable?) {
                                    postEventError(RequestEventTransaccion.ERROR_EVENT, "Comprueba tu conexión")
                                }
                            })

                            //postEventOk(RequestEventTransaccion.SAVE_EVENT, getProductions(cultivo_id), produccion)
                        } else {
                            postEventError(RequestEventTransaccion.ERROR_EVENT, "Comprueba tu conexión")
                        }
                    }
                    override fun onFailure(call: Call<PostTercero>?, t: Throwable?) {
                        postEventError(RequestEventTransaccion.ERROR_EVENT, "Comprueba tu conexión")
                    }
                })
                //}
            } else {

                saveTransaccionLocal(transaccion,cultivo_id,terceroLocal)
            }

        }else{
            saveTransaccionLocal(transaccion,cultivo_id,terceroLocal)
        }

    }

    override fun saveTransaccionLocal(transaccion: Transaccion, cultivo_id:Long?,tercero: Tercero){

        val lastTercero = getLastTercero()
        if (lastTercero == null) {
            tercero.Id = 1
        } else {
            tercero.Id = lastTercero.Id!! + 1
        }
        tercero.save()



        val lastTransaccion = getLastTransaccion()
        if (lastTransaccion == null) {
            transaccion.Id = 1
        } else {
            transaccion.Id = lastTransaccion.Id!! + 1
        }
        transaccion.TerceroId=tercero.Id
        transaccion.save()
        var listProduccion = getTransaccion(cultivo_id,transaccion.CategoriaPuk_Id)
        postEventOk(RequestEventTransaccion.SAVE_EVENT,listProduccion,null);
    }

    override fun getLastTransaccion(): Transaccion? {
        val lastTransaccion = SQLite.select().from(Transaccion::class.java).where().orderBy(Transaccion_Table.Id, false).querySingle()
        return lastTransaccion
    }

    override fun getLastTercero(): Tercero? {
        val lastTercero = SQLite.select().from(Tercero::class.java).where().orderBy(Tercero_Table.Id, false).querySingle()
        return lastTercero
    }

    override fun updateTransaccion(transaccion: Transaccion, cultivo_id: Long?,checkConection:Boolean) {
        transaccion.UsuarioId= getLastUserLogued()?.Id
        var terceroLocal=Tercero(Id = transaccion.TerceroId,Nombre = transaccion.Nombre_Tercero,Apellido = "",NitRut = transaccion.Identificacion_Tercero)
        //TODO si existe coneccion a internet
        if(checkConection){
            //TODO se valida estado de sincronizacion  para actualizar,actualizacion remota
            if (transaccion.Estado_Sincronizacion == true) {

                val postTercero = PostTercero(
                        transaccion.TerceroId,
                        terceroLocal.Nombre,
                        terceroLocal.Apellido,
                        terceroLocal.NitRut,
                        ""
                )

                val call = apiService?.updateTercero(postTercero, terceroLocal.Id!!)
                call?.enqueue(object : Callback<PostTercero> {
                    override fun onResponse(call: Call<PostTercero>?, response: Response<PostTercero>?) {
                        if (response != null && response.code() == 200  || response?.code()==204) {

                            terceroLocal.update()
                            val postTransaccion = PostTransaccion(
                                    transaccion.Id,
                                    transaccion.Concepto,
                                    transaccion.EstadoId,
                                    transaccion.getFechaTransacccionFormatApi(),
                                    transaccion.NaturalezaId,
                                    transaccion.PucId,
                                    terceroLocal.Id,
                                    transaccion.Valor_Total,
                                    transaccion.Cantidad,
                                    transaccion.Cultivo_Id,
                                    getLastUserLogued()?.Id
                            )

                            val call = apiService?.updateTransaccion(postTransaccion, transaccion.Id!!)
                            call?.enqueue(object : Callback<PostTransaccion> {
                                override fun onResponse(call: Call<PostTransaccion>?, response: Response<PostTransaccion>?) {
                                    if (response != null && response.code() == 200) {
                                        transaccion.update()
                                        var listProduccion = getTransaccion(cultivo_id,transaccion.CategoriaPuk_Id)
                                        postEventOk(RequestEventTransaccion.UPDATE_EVENT, listProduccion, transaccion)
                                    } else {
                                        postEventError(RequestEventTransaccion.ERROR_EVENT, "Comprueba tu conexión")
                                    }
                                }
                                override fun onFailure(call: Call<PostTransaccion>?, t: Throwable?) {
                                    postEventError(RequestEventTransaccion.ERROR_EVENT, "Comprueba tu conexión")
                                }
                            })
                        } else {
                            postEventError(RequestEventTransaccion.ERROR_EVENT, "Comprueba tu conexión")
                        }
                    }
                    override fun onFailure(call: Call<PostTercero>?, t: Throwable?) {
                        postEventError(RequestEventTransaccion.ERROR_EVENT, "Comprueba tu conexión")
                    }
                })

            }
            //TODO con  conexion a internet, pero no se ha sincronizado,actualizacion local
            else {
                terceroLocal.Estado_SincronizacionUpdate = false
                terceroLocal.update()

                transaccion.Estado_SincronizacionUpdate = false
                transaccion.update()

                //postEventError(RequestEventLote.ERROR_EVENT, "Error!. El lote no se ha subido")
            }
        }
        //TODO sin conexion a internet, actualizacion local
        else{

            terceroLocal.Estado_SincronizacionUpdate = false
            terceroLocal.update()

            transaccion.Estado_SincronizacionUpdate = false
            transaccion?.update()
        }
    }

    override fun deleteTransaccion(transaccion: Transaccion, cultivo_id: Long?,checkConection:Boolean) {

        //TODO se valida estado de sincronizacion  para eliminar
        if (transaccion.Estado_Sincronizacion == true) {
            //TODO si existe coneccion a internet se elimina
            if(checkConection){




                val call = apiService?.deletetransaccion(transaccion.Id!!)
                call?.enqueue(object : Callback<PostTransaccion> {
                    override fun onResponse(call: Call<PostTransaccion>?, response: Response<PostTransaccion>?) {
                        if (response != null && response.code() == 204 ||  response?.code() == 201 ||  response?.code() == 200 ) {
                            transaccion.delete()

                            var tercero = SQLite.select().from(Tercero::class.java).where(Tercero_Table.Id.eq(transaccion.TerceroId)).querySingle()

                            if(tercero!=null){
                                val callDeleteTercero = apiService?.deleteTercero(transaccion.TerceroId!!)
                                callDeleteTercero?.enqueue(object : Callback<PostTercero> {
                                    override fun onResponse(call: Call<PostTercero>?, response: Response<PostTercero>?) {
                                        if (response != null && response.code() == 204 ||  response?.code() == 201 ||  response?.code() == 200 ) {
                                            tercero?.delete()

                                            postEventOk(RequestEventTransaccion.DELETE_EVENT,getTransaccion(cultivo_id,transaccion.CategoriaPuk_Id), transaccion)
                                        }
                                    }
                                    override fun onFailure(call: Call<PostTercero>?, t: Throwable?) {
                                        postEventError(RequestEventTransaccion.ERROR_EVENT, "Comprueba tu conexión")
                                    }
                                })
                            }else{
                                postEventOk(RequestEventTransaccion.DELETE_EVENT,getTransaccion(cultivo_id,transaccion.CategoriaPuk_Id), transaccion)

                            }
                        }
                    }
                    override fun onFailure(call: Call<PostTransaccion>?, t: Throwable?) {
                        postEventError(RequestEventTransaccion.ERROR_EVENT, "Comprueba tu conexión")
                    }
                })

            }else{
                postEventError(RequestEventTransaccion.ERROR_VERIFICATE_CONECTION, null)
            }
        } else {
            //TODO No sincronizado, Eliminar de manera local
            transaccion.delete()
            postEventOk(RequestEventTransaccion.DELETE_EVENT,  getTransaccion(cultivo_id,transaccion.CategoriaPuk_Id), transaccion)
        }
       // transaccion.delete()
        //SQLite.delete<Lote>(Lote::class.java).where(Lote_Table.Id.eq(lote.Id)).async().execute()
       // postEventOk(RequestEventTransaccion.DELETE_EVENT, getTransaccion(cultivo_id,transaccion.CategoriaPuk_Id),transaccion);
    }

    override fun getCultivo(cultivo_id: Long?) {
        var cultivo = SQLite.select().from(Cultivo::class.java).where(Cultivo_Table.Id.eq(cultivo_id)).querySingle()
        postEventOkCultivo(RequestEventTransaccion.GET_EVENT_CULTIVO,cultivo)
    }

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

    private fun postEventListCategoriaPuk(type: Int, listCategoriaPuk:List<CategoriaPuk>?, messageError:String?) {
        var upMutable= listCategoriaPuk as MutableList<Object>
        postEvent(type, upMutable,null,messageError)
    }


    private fun postEventListPuk(type: Int, listPuk:List<Puk>?, messageError:String?) {
        var upMutable= listPuk as MutableList<Object>
        postEvent(type, upMutable,null,messageError)
    }


    private fun postEventOk(type: Int, transacciones: List<Transaccion>?, trasaccion: Transaccion?) {
        var transaccionListMitable= transacciones as MutableList<Object>
        var ProducciconMutable:Object?=null
        if(trasaccion!=null){
            ProducciconMutable = trasaccion as Object
        }
        postEvent(type, transaccionListMitable,ProducciconMutable,null)
    }

    private fun postEventOkCultivo(type: Int,  cultivo: Cultivo?) {
        var CultivoMutable:Object?=null
        if(cultivo!=null){
            CultivoMutable = cultivo as Object
        }
        postEvent(type,null,CultivoMutable,null)
    }


    private fun postEventError(type: Int,messageError:String?) {
        postEvent(type, null,null,messageError)
    }

    //Main Post Event
    private fun postEvent(type: Int, listModel1:MutableList<Object>?,model:Object?,errorMessage: String?) {
        val event = RequestEventTransaccion(type, listModel1, model, errorMessage)
        event.eventType = type
        eventBus?.post(event)
    }
    //endregion
}