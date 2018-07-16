package com.interedes.agriculturappv3.modules.productor.asistencia_tecnica_module.cultivos

import android.util.Log
import com.interedes.agriculturappv3.modules.models.unidad_medida.Unidad_Medida
import com.interedes.agriculturappv3.modules.productor.asistencia_tecnica_module.cultivos.events.CultivoEvent
import com.interedes.agriculturappv3.libs.EventBus
import com.interedes.agriculturappv3.libs.GreenRobotEventBus
import com.interedes.agriculturappv3.modules.models.control_plaga.ControlPlaga
import com.interedes.agriculturappv3.modules.models.control_plaga.ControlPlaga_Table
import com.interedes.agriculturappv3.modules.models.cultivo.Cultivo
import com.interedes.agriculturappv3.modules.models.cultivo.Cultivo_Table
import com.interedes.agriculturappv3.modules.models.cultivo.PostCultivo
import com.interedes.agriculturappv3.modules.models.detalletipoproducto.DetalleTipoProducto
import com.interedes.agriculturappv3.modules.models.lote.Lote
import com.interedes.agriculturappv3.modules.models.lote.Lote_Table
import com.interedes.agriculturappv3.modules.models.ofertas.DetalleOferta
import com.interedes.agriculturappv3.modules.models.ofertas.DetalleOferta_Table
import com.interedes.agriculturappv3.modules.models.ofertas.Oferta
import com.interedes.agriculturappv3.modules.models.ofertas.Oferta_Table
import com.interedes.agriculturappv3.modules.models.produccion.Produccion
import com.interedes.agriculturappv3.modules.models.produccion.Produccion_Table
import com.interedes.agriculturappv3.modules.models.producto.Producto
import com.interedes.agriculturappv3.modules.models.producto.Producto_Table
import com.interedes.agriculturappv3.modules.models.tipoproducto.TipoProducto
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
import com.raizlabs.android.dbflow.kotlinextensions.save
import com.raizlabs.android.dbflow.kotlinextensions.update
import com.raizlabs.android.dbflow.sql.language.SQLite
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.math.BigDecimal
import java.math.MathContext

class CultivoRepository : ICultivo.Repository {


    var eventBus: EventBus? = null
    var apiService: ApiInterface? = null

    init {
        eventBus = GreenRobotEventBus()
        apiService = ApiInterface.create()
    }

    //region Métodos Interfaz
    override fun getListas() {
        val usuario= getLastUserLogued()


        //get Unidades Productivas
        val listUnidadesProductivas: List<Unidad_Productiva> = SQLite.select().from(Unidad_Productiva::class.java)
                .where(Unidad_Productiva_Table.UsuarioId.eq(usuario?.Id))
                .queryList()

        val listLotes = SQLite.select().from(Lote::class.java)
                .where(Lote_Table.UsuarioId.eq(usuario?.Id))
                .queryList()


        postEventListLotes(CultivoEvent.LIST_EVENT_LOTES, listLotes, null);

        if (listUnidadesProductivas.size > 0) {
            postEventListUnidadProductiva(CultivoEvent.LIST_EVENT_UNIDAD_PRODUCTIVA, listUnidadesProductivas, null)
        } else {
            postEventError(CultivoEvent.ERROR_DIALOG_EVENT, "No hay Unidades productivas registradas")
        }



        /*val listTipoProducto: ArrayList<TipoProducto> = Listas.listaTipoProducto()
        for (item in listTipoProducto) {
            item.save()
        }*/
        val listTipoProducto = SQLite.select().from(TipoProducto::class.java).queryList()
        postEventListTipoProducto(CultivoEvent.LIST_EVENT_TIPO_PRODUCTO, listTipoProducto, null)

        //val listUnidadMedida = Listas.listaUnidadMedida()
        val listUnidadMedida = SQLite.select().from(Unidad_Medida::class.java).where(Unidad_Medida_Table.CategoriaMedidaId.eq(CategoriaMediaResources.Cosecha)).queryList()
        postEventListUnidadMedida(CultivoEvent.LIST_EVENT_UNIDAD_MEDIDA, listUnidadMedida, null)


        /*val listDetalleTipoProducto: ArrayList<DetalleTipoProducto> = Listas.listaDetalleTipoProducto()
        for (item in listDetalleTipoProducto) {
            item.save()
        }*/
        val listDetalleTipoProducto = SQLite.select().from(DetalleTipoProducto::class.java).queryList()
        postEventListDetalleTipoProducto(CultivoEvent.LIST_EVENT_DETALLE_TIPO_PRODUCTO, listDetalleTipoProducto, null)
    }

    fun getLastUserLogued(): Usuario? {
        val usuarioLogued = SQLite.select().from(Usuario::class.java).where(Usuario_Table.UsuarioRemembered.eq(true)).querySingle()
        return usuarioLogued
    }

    override fun getListCultivos(lote_id: Long?) {
        var cultivos = getCultivos(lote_id)
        postEventOk(CultivoEvent.READ_EVENT, cultivos, null)
    }

    override fun getLote(loteId: Long?) {
        val lote = SQLite.select().from(Lote::class.java).where(Lote_Table.LoteId.eq(loteId)).querySingle()
        postEventOkLote(CultivoEvent.GET_EVENT_LOTE, lote)
        /*
        var cultivo = SQLite.select().from(Cultivo::class.java!!).where(Cultivo_Table.Id.eq(cultivo_id)).querySingle()
        postEventOkCultivo(RequestEventProduccion.GET_EVENT_CULTIVO,cultivo)
         */
    }

    override fun getCultivos(loteId: Long?): List<Cultivo> {
        var listResponse: List<Cultivo>? = null
        if (loteId == null || loteId == 0.toLong()) {
            listResponse = SQLite.select().from(Cultivo::class.java)
                    .where(Cultivo_Table.UsuarioId.eq(getLastUserLogued()?.Id))
                    .queryList()
        } else {
            listResponse = SQLite.select().from(Cultivo::class.java)
                    .where(Cultivo_Table.LoteId.eq(loteId))
                    .and(Cultivo_Table.UsuarioId.eq(getLastUserLogued()?.Id))
                    .queryList()
        }

        return listResponse;
    }

    override fun saveCultivo(mCultivo: Cultivo, loteId: Long?,checkConection:Boolean) {

        mCultivo.UsuarioId=getLastUserLogued()?.Id

        //TODO si existe conexion a internet
        if(checkConection){
            //TODO Ciudad Id de la tabla del backend
            val lote = SQLite.select().from(Lote::class.java).where(Lote_Table.LoteId.eq(loteId)).querySingle()

            //val areaBig = BigDecimal(mCultivo!!, MathContext.DECIMAL64)
            if (lote?.EstadoSincronizacion == true) {
                val postCultivo = PostCultivo(0,
                        mCultivo.Descripcion,
                        mCultivo.DetalleTipoProductoId,
                        mCultivo.Unidad_Medida_Id,
                        mCultivo.EstimadoCosecha,
                        mCultivo.stringFechaFin,
                        mCultivo.stringFechaInicio,
                        lote.Id_Remote,
                        mCultivo.Nombre,
                        mCultivo.siembraTotal)

                val call = apiService?.postCultivo(postCultivo)
                call?.enqueue(object : Callback<Cultivo> {
                    override fun onResponse(call: Call<Cultivo>?, response: Response<Cultivo>?) {
                        if (response != null && response.code() == 201) {
                            val value = response.body()
                            if(value?.Id_Remote!!>0){
                                mCultivo.Id_Remote = value?.Id_Remote!!
                                mCultivo.FechaIncio = value.FechaIncio
                                mCultivo.FechaFin = value.FechaFin
                                mCultivo.EstadoSincronizacion = true
                                mCultivo.Estado_SincronizacionUpdate = true
                                saveCultivoLocal(mCultivo,loteId)
                            }else{
                                postEventError(CultivoEvent.ERROR_EVENT, "Por favor intente nuevamente")
                            }
                            //postEventOk(CultivoEvent.SAVE_EVENT, getCultivos(loteId), mCultivo)
                        } else {
                            postEventError(CultivoEvent.ERROR_EVENT, "Comprueba tu conexión")
                            Log.e("error", response?.message().toString())
                        }
                    }
                    override fun onFailure(call: Call<Cultivo>?, t: Throwable?) {
                        postEventError(CultivoEvent.ERROR_EVENT, "Comprueba tu conexión")
                    }
                })
            }
            //TODO con conexion a internet sin sincronizacion, registro local
            else {
                mCultivo.EstadoSincronizacion = false
                mCultivo.Estado_SincronizacionUpdate = false
                saveCultivoLocal(mCultivo, loteId)
            }
        }
        //TODO sin conexion a internet, registro local
        else{
            mCultivo.EstadoSincronizacion = false
            mCultivo.Estado_SincronizacionUpdate = false
            saveCultivoLocal(mCultivo, loteId)
        }
    }

    override fun saveCultivoLocal(mCultivo: Cultivo, loteId: Long?){
        val last_cultivo = getLastCultivo()
        if (last_cultivo == null) {
            mCultivo.CultivoId = 1
        } else {
            mCultivo.CultivoId = last_cultivo.CultivoId!! + 1
        }
        mCultivo.save()

        postEventOk(CultivoEvent.SAVE_EVENT, getCultivos(loteId), mCultivo)
        getLote(loteId)
    }


    override fun updateCultivo(mCultivo: Cultivo, loteId: Long?,checkConection:Boolean) {

        //TODO si existe coneccion a internet
        if(checkConection){
            val lote = SQLite.select().from(Lote::class.java).where(Lote_Table.LoteId.eq(loteId)).querySingle()
            //TODO se valida estado de sincronizacion  para actualizar,actualizacion remota
            if (mCultivo.EstadoSincronizacion == true) {
                val postCultivo = PostCultivo(mCultivo.Id_Remote,
                        mCultivo.Descripcion,
                        mCultivo.DetalleTipoProductoId,
                        mCultivo.Unidad_Medida_Id,
                        mCultivo.EstimadoCosecha,
                        mCultivo.stringFechaFin,
                        mCultivo.stringFechaInicio,
                        lote?.Id_Remote,
                        mCultivo.Nombre,
                        mCultivo.siembraTotal)
                val call = apiService?.updateCultivo(postCultivo, mCultivo.Id_Remote!!)
                call?.enqueue(object : Callback<Cultivo> {
                    override fun onResponse(call: Call<Cultivo>?, response: Response<Cultivo>?) {
                        if (response != null && response.code() == 200) {
                            mCultivo.EstadoSincronizacion=true
                            mCultivo.Estado_SincronizacionUpdate = true
                            mCultivo.update()
                            postEventOk(CultivoEvent.UPDATE_EVENT, getCultivos(mCultivo.LoteId), mCultivo)
                        } else {
                            postEventError(CultivoEvent.ERROR_EVENT, "Comprueba tu conexión")
                        }
                    }
                    override fun onFailure(call: Call<Cultivo>?, t: Throwable?) {
                        postEventError(CultivoEvent.ERROR_EVENT, "Comprueba tu conexión")
                    }
                })
            }
            //TODO con  conexion a internet, pero no se ha sincronizado,actualizacion local
            else {
                mCultivo.Estado_SincronizacionUpdate = false
                mCultivo.update()
                postEventOk(CultivoEvent.UPDATE_EVENT, getCultivos(mCultivo.LoteId), mCultivo)
            }
        }
        //TODO sin conexion a internet, actualizacion local
        else{
            mCultivo.Estado_SincronizacionUpdate = false
            mCultivo.update()
            postEventOk(CultivoEvent.UPDATE_EVENT, getCultivos(mCultivo.LoteId), mCultivo)
        }
    }

    override fun deleteCultivo(mCultivo: Cultivo, loteId: Long?,checkConection:Boolean) {

        //TODO se valida estado de sincronizacion  para eliminar
        if (mCultivo.EstadoSincronizacion == true) {
            //TODO si existe coneccion a internet se elimina
            if(checkConection){
                val call = apiService?.deleteCultivo(mCultivo.Id_Remote!!)
                call?.enqueue(object : Callback<Cultivo> {
                    override fun onResponse(call: Call<Cultivo>?, response: Response<Cultivo>?) {
                        if (response != null && response.code() == 204) {
                            //mCultivo.delete()
                            deleteCultivo(mCultivo)
                            postEventOk(CultivoEvent.DELETE_EVENT, getCultivos(loteId), mCultivo)
                        }
                    }
                    override fun onFailure(call: Call<Cultivo>?, t: Throwable?) {
                        postEventError(CultivoEvent.ERROR_EVENT, "Comprueba tu conexión")
                    }
                })

            }else{
                postEventError(CultivoEvent.ERROR_VERIFICATE_CONECTION, null)
            }
        } else {
            //TODO No sincronizado, Eliminar de manera local
            deleteCultivo(mCultivo)
            postEventOk(CultivoEvent.DELETE_EVENT, getCultivos(loteId), mCultivo)
            //Verificate if cultivos register
           /* var vericateRegisterProduccion= SQLite.select().from(Produccion::class.java).where(Produccion_Table.CultivoId.eq(mCultivo.CultivoId)).querySingle()
            if(vericateRegisterProduccion!=null){
                postEventError(CultivoEvent.ERROR_EVENT, "Error!.El cultivo no se ha podido eliminar, recuerde eliminar la produccion primero")
            }else{
                mCultivo.delete()
                postEventOk(CultivoEvent.DELETE_EVENT,  getCultivos(loteId), mCultivo)
                //getLote(mCultivo.LoteId)
            }*/
        }
    }

    fun deleteCultivo(cultivo: Cultivo) {

        SQLite.delete<Produccion>(Produccion::class.java)
                .where(Produccion_Table.CultivoId.eq(cultivo.CultivoId))
                .async()
                .execute()
        SQLite.delete<ControlPlaga>(ControlPlaga::class.java)
                .where(ControlPlaga_Table.CultivoId.eq(cultivo.CultivoId))
                .async()
                .execute()
        val listTransacciones = SQLite.select().from(Transaccion::class.java).where(Transaccion_Table.Cultivo_Id.eq(cultivo.CultivoId)).queryList()
        for (transaccion in listTransacciones) {
            SQLite.delete<Tercero>(Tercero::class.java)
                    .where(Tercero_Table.TerceroId.eq(transaccion.TerceroId))
                    .async()
                    .execute()
            transaccion.delete()
        }
        val listProductos = SQLite.select().from(Producto::class.java).where(Producto_Table.cultivoId.eq(cultivo.CultivoId)).queryList()
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


    override fun getLastCultivo(): Cultivo? {
        val lastCultivo = SQLite.select().from(Cultivo::class.java).where().orderBy(Cultivo_Table.CultivoId, false).querySingle()
        return lastCultivo
    }

    //endregion


    //region Events
    private fun postEventListUnidadProductiva(type: Int, listUnidadProductiva: List<Unidad_Productiva>?, messageError: String?) {
        val unidadProductivaMutable = listUnidadProductiva as MutableList<Object>
        postEvent(type, unidadProductivaMutable, null, messageError)
    }

    private fun postEventListTipoProducto(type: Int, listTipoProducto: List<TipoProducto>?, messageError: String?) {
        val tipoProductoMutable = listTipoProducto as MutableList<Object>
        postEvent(type, tipoProductoMutable, null, messageError)
    }

    private fun postEventListDetalleTipoProducto(type: Int, listDetalleTipoProducto: List<DetalleTipoProducto>?, messageError: String?) {
        val detalleTipoProductoMutable = listDetalleTipoProducto as MutableList<Object>
        postEvent(type, detalleTipoProductoMutable, null, messageError)
    }

    private fun postEventListUnidadMedida(type: Int, listUnidadMedida: List<Unidad_Medida>?, messageError: String?) {
        val unidadMedidaMutable = listUnidadMedida as MutableList<Object>
        postEvent(type, unidadMedidaMutable, null, messageError)
    }

    private fun postEventListLotes(type: Int, listLote: List<Lote>?, messageError: String?) {
        val loteMutable = listLote as MutableList<Object>
        postEvent(type, loteMutable, null, messageError)
    }


    private fun postEventError(type: Int, messageError: String?) {
        val event = CultivoEvent(type, null, null, messageError)
        event.eventType = type
        eventBus?.post(event)
    }

    private fun postEventOkLote(type: Int, lote: Lote?) {
        var loteMutable: Object? = null
        if (lote != null) {
            loteMutable = lote as Object
        }
        postEvent(type, null, loteMutable, null)
    }

    private fun postEventOk(type: Int, listCultivos: List<Cultivo>?, cultivo: Cultivo?) {
        val cultivoListMutable = listCultivos as MutableList<Object>
        var cultivoMutable: Object? = null
        if (cultivo != null) {
            cultivoMutable = cultivo as Object
        }
        postEvent(type, cultivoListMutable, cultivoMutable, null)
    }

    //Main Post Event
    private fun postEvent(type: Int, listModel: MutableList<Object>?, model: Object?, errorMessage: String?) {
        val event = CultivoEvent(type, listModel, model, errorMessage)
        event.eventType = type
        eventBus?.post(event)
    }
    //endregion

}