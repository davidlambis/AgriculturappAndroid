package com.interedes.agriculturappv3.modules.productor.asistencia_tecnica_module.Lote

import com.interedes.agriculturappv3.modules.models.lote.Lote
import com.interedes.agriculturappv3.modules.models.unidad_medida.Unidad_Medida
import com.interedes.agriculturappv3.modules.productor.asistencia_tecnica_module.Lote.events.RequestEventLote
import com.interedes.agriculturappv3.libs.EventBus
import com.interedes.agriculturappv3.libs.GreenRobotEventBus
import com.interedes.agriculturappv3.modules.models.control_plaga.ControlPlaga
import com.interedes.agriculturappv3.modules.models.control_plaga.ControlPlaga_Table
import com.interedes.agriculturappv3.modules.models.cultivo.Cultivo
import com.interedes.agriculturappv3.modules.models.cultivo.Cultivo_Table
import com.interedes.agriculturappv3.modules.models.lote.Lote_Table
import com.interedes.agriculturappv3.modules.models.lote.PostLote
import com.interedes.agriculturappv3.modules.models.ofertas.DetalleOferta
import com.interedes.agriculturappv3.modules.models.ofertas.DetalleOferta_Table
import com.interedes.agriculturappv3.modules.models.ofertas.Oferta
import com.interedes.agriculturappv3.modules.models.ofertas.Oferta_Table
import com.interedes.agriculturappv3.modules.models.produccion.Produccion
import com.interedes.agriculturappv3.modules.models.produccion.Produccion_Table
import com.interedes.agriculturappv3.modules.models.producto.Producto
import com.interedes.agriculturappv3.modules.models.producto.Producto_Table
import com.interedes.agriculturappv3.modules.models.unidad_medida.Unidad_Medida_Table
import com.interedes.agriculturappv3.modules.models.unidad_productiva.Unidad_Productiva
import com.interedes.agriculturappv3.modules.models.unidad_productiva.Unidad_Productiva_Table
import com.interedes.agriculturappv3.modules.models.usuario.Usuario
import com.interedes.agriculturappv3.modules.models.usuario.Usuario_Table
import com.interedes.agriculturappv3.modules.models.ventas.Tercero
import com.interedes.agriculturappv3.modules.models.ventas.Tercero_Table
import com.interedes.agriculturappv3.modules.models.ventas.Transaccion
import com.interedes.agriculturappv3.modules.models.ventas.Transaccion_Table
import com.interedes.agriculturappv3.services.api.ApiInterface
import com.interedes.agriculturappv3.services.resources.CategoriaMediaResources
import com.raizlabs.android.dbflow.kotlinextensions.delete
import com.raizlabs.android.dbflow.sql.language.SQLite
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.math.BigDecimal
import java.math.MathContext


class LoteRepositoryImpl : MainViewLote.Repository {

    var eventBus: EventBus? = null
    var apiService: ApiInterface? = null

    init {
        eventBus = GreenRobotEventBus()
        apiService = ApiInterface.create()
    }


    override fun verificateAreaLoteBiggerUp(unidad_productiva_id:Long?,area:Double):Boolean {
        val response: Boolean
        val up_area = SQLite.select().from(Unidad_Productiva::class.java).where(Unidad_Productiva_Table.Unidad_Productiva_Id.eq(unidad_productiva_id)).querySingle()?.Area
        val total_areas = area_lotes(area, unidad_productiva_id)
        if (total_areas!! > up_area!!) {
            response= true
            //postEventError(RequestEventLote.ERROR_EVENT, "No se puede registrar. El área total de lotes supera el área de la Unidad Productiva")
        } else {
            response= false
        }
        return response
    }

    //region METHODS
    override fun saveLotes(mLote: Lote, unidad_productiva_id: Long?,checkConection:Boolean) {
        mLote.UsuarioId= getLastUserLogued()?.Id

        //TODO si existe conexion a internet
        if(checkConection){
            //TODO Ciudad Id de la tabla del backend
            val unidad_productiva = SQLite.select().from(Unidad_Productiva::class.java).where(Unidad_Productiva_Table.Unidad_Productiva_Id.eq(unidad_productiva_id)).querySingle()
            if (unidad_productiva?.Estado_Sincronizacion == true) {

                val areaBig = BigDecimal(mLote.Area!!, MathContext.DECIMAL64)

                val postLote = PostLote(0,
                        areaBig,
                        mLote?.Codigo,
                        mLote.Nombre,
                        mLote.Descripcion,
                        mLote.Localizacion,
                        mLote?.Localizacion_Poligono,
                        mLote.Unidad_Medida_Id,
                        unidad_productiva.Id_Remote)


                val call = apiService?.postLote(postLote)
                call?.enqueue(object : Callback<Lote> {
                        override fun onResponse(call: Call<Lote>?, response: Response<Lote>?) {
                            if (response != null && response.code() == 201) {
                                mLote.Id_Remote = response.body()?.Id_Remote!!
                                if( mLote.Id_Remote!!>0){
                                    val last_lote = getLastLote()
                                    if (last_lote == null) {
                                        mLote.LoteId = 1
                                    } else {
                                        mLote.LoteId = last_lote.LoteId + 1
                                    }

                                    mLote.EstadoSincronizacion = true
                                    mLote.Estado_SincronizacionUpdate = true
                                    mLote.save()
                                    postEventOk(RequestEventLote.SAVE_EVENT, getLotes(unidad_productiva_id), mLote)
                                }else{
                                    postEventError(RequestEventLote.ERROR_EVENT, "Por favor intente nuevamente")
                                }
                            } else {
                                postEventError(RequestEventLote.ERROR_EVENT, "Comprueba tu conexión")
                            }
                        }
                        override fun onFailure(call: Call<Lote>?, t: Throwable?) {
                            postEventError(RequestEventLote.ERROR_EVENT, "Comprueba tu conexión")
                        }
                    })
            }
            //TODO con conexion a internet sin sincronizacion, registro local
            else {
                mLote.EstadoSincronizacion = false
                mLote.Estado_SincronizacionUpdate = false
                saveLotesLocal(mLote,unidad_productiva_id)
            }
        }
        //TODO sin conexion a internet, registro local
        else{
            mLote.EstadoSincronizacion = false
            mLote.Estado_SincronizacionUpdate = false
            saveLotesLocal(mLote,unidad_productiva_id)
        }
    }


    override fun saveLotesLocal(mLote: Lote, unidad_productiva_id: Long?){
            val last_lote = getLastLote()
            if (last_lote == null) {
                mLote.LoteId = 1
            } else {
                mLote.LoteId = last_lote.LoteId + 1
            }
            mLote.save()
            val lotes = getLotes(unidad_productiva_id)
            postEventOk(RequestEventLote.SAVE_EVENT, lotes, mLote)
    }


    private fun area_lotes(area: Double, unidad_productiva_id: Long?): Double? {
        val lotes_up = SQLite.select().from(Lote::class.java).where(Lote_Table.Unidad_Productiva_Id.eq(unidad_productiva_id)).queryList()
        var suma_areas = 0.0
        for (item in lotes_up) {
            suma_areas = suma_areas +item.Area!!
        }
        val total_areas = suma_areas + area
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
        val lastLote = SQLite.select().from(Lote::class.java).where().orderBy(Lote_Table.LoteId, false).querySingle()
        return lastLote
    }

    override fun getLastUserLogued(): Usuario? {
        val usuarioLogued = SQLite.select().from(Usuario::class.java).where(Usuario_Table.UsuarioRemembered.eq(true)).querySingle()
        return usuarioLogued
    }

    override fun getLotes(unidad_productiva_id: Long?): List<Lote> {
        var listResponse: List<Lote>? = null

        if (unidad_productiva_id == null) {
            listResponse = SQLite.select().from(Lote::class.java)
                    .where(Lote_Table.UsuarioId.eq(getLastUserLogued()?.Id)).queryList()
        } else {
            listResponse = SQLite.select().from(Lote::class.java)
                    .where(Lote_Table.Unidad_Productiva_Id.eq(unidad_productiva_id))
                    .and(Lote_Table.UsuarioId.eq(getLastUserLogued()?.Id)).queryList()
        }
        return listResponse;
    }

    override fun updateLote(mLote: Lote, unidad_productiva_id: Long?,checkConection:Boolean){
        //TODO si existe coneccion a internet
        if(checkConection){

            val unidad_productiva = SQLite.select().from(Unidad_Productiva::class.java).where(Unidad_Productiva_Table.Unidad_Productiva_Id.eq(unidad_productiva_id)).querySingle()
            val areaBig = BigDecimal(mLote.Area!!, MathContext.DECIMAL64)
            //TODO se valida estado de sincronizacion  para actualizar,actualizacion remota
            if (mLote.EstadoSincronizacion == true) {
                val postLote = PostLote(mLote.Id_Remote,
                        areaBig,
                        mLote?.Codigo,
                        mLote.Nombre,
                        mLote.Descripcion,
                        mLote.Localizacion,
                        mLote?.Localizacion_Poligono,
                        mLote.Unidad_Medida_Id,
                        unidad_productiva?.Id_Remote)


                val call = apiService?.updateLote(postLote, mLote.Id_Remote!!)
                call?.enqueue(object : Callback<Lote> {
                    override fun onResponse(call: Call<Lote>?, response: Response<Lote>?) {
                        if (response != null && response.code() == 200) {
                            mLote.EstadoSincronizacion = true
                            mLote.Estado_SincronizacionUpdate = true
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

            }
            //TODO con  conexion a internet, pero no se ha sincronizado,actualizacion local
            else {
                mLote.Estado_SincronizacionUpdate = false
                mLote.update()
                postEventOk(RequestEventLote.UPDATE_EVENT, getLotes(unidad_productiva_id), mLote)
                //postEventError(RequestEventLote.ERROR_EVENT, "Error!. El lote no se ha subido")
            }
        }
        //TODO sin conexion a internet, actualizacion local
        else{
            mLote.Estado_SincronizacionUpdate = false
            mLote.update()
            postEventOk(RequestEventLote.UPDATE_EVENT, getLotes(unidad_productiva_id), mLote)
        }
    }


    override fun deleteLote(mLote: Lote, unidad_productiva_id: Long?,checkConection:Boolean) {
        //TODO se valida estado de sincronizacion  para eliminar
        if (mLote.EstadoSincronizacion == true) {
            //TODO si existe coneccion a internet se elimina
            if(checkConection){
                val call = apiService?.deleteLote(mLote.Id_Remote!!)
                call?.enqueue(object : Callback<Lote> {
                    override fun onResponse(call: Call<Lote>?, response: Response<Lote>?) {
                        if (response != null && response.code() == 204) {
                            deleteLote(mLote)
                            postEventOk(RequestEventLote.DELETE_EVENT, getLotes(unidad_productiva_id), mLote)
                        }
                    }
                    override fun onFailure(call: Call<Lote>?, t: Throwable?) {
                        postEventError(RequestEventLote.ERROR_EVENT, "Comprueba tu conexión")
                    }
                })
            }else{
                postEventError(RequestEventLote.ERROR_VERIFICATE_CONECTION, null)
            }
        } else {
            //TODO No sincronizado, Eliminar de manera local
            deleteLote(mLote)
            //Verificate if cultivos register
            /*var vericateRegisterCultivos= SQLite.select().from(Cultivo::class.java).where(Cultivo_Table.LoteId.eq(mLote.LoteId)).querySingle()
            if(vericateRegisterCultivos!=null){

                postEventError(RequestEventLote.ERROR_EVENT, "Error!. El lote no se ha podido eliminar, recuerde eliminar los cultivos")
            }else{
                mLote.delete()
                postEventOk(RequestEventLote.DELETE_EVENT, getLotes(unidad_productiva_id), mLote)
            }*/
        }
    }


    fun deleteLote(lote: Lote) {
        val cultivos = SQLite.select().from(Cultivo::class.java).where(Cultivo_Table.LoteId.eq(lote.LoteId)).queryList()
        for (cultivo in cultivos) {

            SQLite.delete<Produccion>(Produccion::class.java)
                    .where(Produccion_Table.CultivoId.eq(cultivo.CultivoId))
                    .async()
                    .execute()

            SQLite.delete<ControlPlaga>(ControlPlaga::class.java)
                    .where(ControlPlaga_Table.CultivoId.eq(cultivo.CultivoId))
                    .async()
                    .execute()

            val listTransacciones = SQLite.select().from(Transaccion::class.java).where(Transaccion_Table.Cultivo_Id.eq(cultivo?.CultivoId)).queryList()
            for (transaccion in listTransacciones) {
                SQLite.delete<Tercero>(Tercero::class.java)
                        .where(Tercero_Table.TerceroId.eq(transaccion.TerceroId))
                        .async()
                        .execute()
                transaccion.delete()
            }

            val listProductos = SQLite.select().from(Producto::class.java).where(Producto_Table.cultivoId.eq(cultivo?.CultivoId)).queryList()
            for (producto in listProductos) {
                val listDetalleOferta = SQLite.select().from(DetalleOferta::class.java).where(DetalleOferta_Table.ProductoId.eq(producto?.ProductoId)).queryList()
                for (detalleoferta in listDetalleOferta) {

                    SQLite.delete<Oferta>(Oferta::class.java)
                            .where(Oferta_Table.Oferta_Id.eq(detalleoferta.OfertasId))
                            .async()
                            .execute()

                    detalleoferta.delete()
                }
                producto.delete()
            }

            cultivo.delete()
        }
        lote.delete()
    }

    //endregion

    //region Events

    private fun postEventOk(type: Int, lotes: List<Lote>?, lote: Lote?) {
        val loteListMitable = lotes as MutableList<Object>
        var LoteMutable: Object? = null
        if (lote != null) {
            LoteMutable = lote as Object
        }
        postEvent(type, loteListMitable, LoteMutable, null)
    }

    private fun postEventError(type: Int, messageError: String?) {
        postEvent(type, null, null, messageError)
    }

    private fun postEventListUp(type: Int, listUnidadProductiva: List<Unidad_Productiva>?, messageError: String?) {
        val upMutable = listUnidadProductiva as MutableList<Object>
        postEvent(type, upMutable, null, messageError)
    }

    private fun postEventListUnidadMedida(type: Int, listUnidadMedida: List<Unidad_Medida>?, messageError: String?) {
        val upMutable = listUnidadMedida as MutableList<Object>
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