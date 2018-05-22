package com.interedes.agriculturappv3.modules.main_menu.ui

import android.util.Log
import com.interedes.agriculturappv3.libs.EventBus
import com.interedes.agriculturappv3.libs.GreenRobotEventBus
import com.interedes.agriculturappv3.modules.main_menu.ui.events.RequestEventMainMenu
import com.interedes.agriculturappv3.modules.models.control_plaga.ControlPlaga
import com.interedes.agriculturappv3.modules.models.control_plaga.ControlPlaga_Table
import com.interedes.agriculturappv3.modules.models.control_plaga.PostControlPlaga
import com.interedes.agriculturappv3.modules.models.cultivo.Cultivo
import com.interedes.agriculturappv3.modules.models.cultivo.Cultivo_Table
import com.interedes.agriculturappv3.modules.models.cultivo.PostCultivo
import com.interedes.agriculturappv3.modules.models.lote.Lote
import com.interedes.agriculturappv3.modules.models.lote.Lote_Table
import com.interedes.agriculturappv3.modules.models.lote.PostLote
import com.interedes.agriculturappv3.modules.models.produccion.PostProduccion
import com.interedes.agriculturappv3.modules.models.produccion.Produccion
import com.interedes.agriculturappv3.modules.models.produccion.Produccion_Table
import com.interedes.agriculturappv3.modules.models.producto.PostProducto
import com.interedes.agriculturappv3.modules.models.producto.Producto
import com.interedes.agriculturappv3.modules.models.producto.Producto_Table
import com.interedes.agriculturappv3.modules.models.sincronizacion.QuantitySync
import com.interedes.agriculturappv3.modules.models.unidad_productiva.PostUnidadProductiva
import com.interedes.agriculturappv3.modules.models.unidad_productiva.Unidad_Productiva
import com.interedes.agriculturappv3.modules.models.unidad_productiva.Unidad_Productiva_Table
import com.interedes.agriculturappv3.modules.models.unidad_productiva.localizacion.LocalizacionUp
import com.interedes.agriculturappv3.modules.models.ventas.RequestApi.PostTercero
import com.interedes.agriculturappv3.modules.models.ventas.RequestApi.PostTransaccion
import com.interedes.agriculturappv3.modules.models.ventas.Tercero
import com.interedes.agriculturappv3.modules.models.ventas.Tercero_Table
import com.interedes.agriculturappv3.modules.models.ventas.Transaccion
import com.interedes.agriculturappv3.modules.models.ventas.Transaccion_Table
import com.interedes.agriculturappv3.modules.productor.accounting_module.transacciones.events.RequestEventTransaccion
import com.interedes.agriculturappv3.modules.productor.asistencia_tecnica_module.UnidadProductiva.events.RequestEventUP
import com.interedes.agriculturappv3.services.api.ApiInterface
import com.raizlabs.android.dbflow.kotlinextensions.save
import com.raizlabs.android.dbflow.kotlinextensions.update
import com.raizlabs.android.dbflow.sql.language.SQLite
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.text.SimpleDateFormat

class MenuRepository: MainViewMenu.Repository {


    var eventBus: EventBus? = null
    var apiService: ApiInterface? = null
    init {
        eventBus = GreenRobotEventBus()
        apiService = ApiInterface.create()
    }

    override fun syncQuantityData() {

        var counRegisterUnidadesProductivas=SQLite.select().from(Unidad_Productiva::class.java).where(Unidad_Productiva_Table.Estado_Sincronizacion.eq(false)).count()
        var counRegisterLotes=SQLite.select().from(Lote::class.java).where(Lote_Table.EstadoSincronizacion.eq(false)).count()
        var counRegisterCultivos=SQLite.select().from(Cultivo::class.java).where(Cultivo_Table.EstadoSincronizacion.eq(false)).count()
        var counRegisterControlPlagas=SQLite.select().from(ControlPlaga::class.java).where(ControlPlaga_Table.Estado_Sincronizacion.eq(false)).count()
        var counRegisterProducccion=SQLite.select().from(Produccion::class.java).where(Produccion_Table.Estado_Sincronizacion.eq(false)).count()
        var counRegisterProductos=SQLite.select().from(Producto::class.java).where(Producto_Table.Estado_Sincronizacion.eq(false)).count()
        var counRegisterTransacciones=SQLite.select().from(Transaccion::class.java).where(Transaccion_Table.Estado_Sincronizacion.eq(false)).count()


        var registerTotal= counRegisterUnidadesProductivas+
                counRegisterControlPlagas+
                counRegisterCultivos+
                counRegisterLotes+counRegisterProducccion+counRegisterProductos+counRegisterTransacciones

        var countUpdatesUnidadesProductivas=SQLite.select().from(Unidad_Productiva::class.java).where(Unidad_Productiva_Table.Estado_Sincronizacion.eq(true)).and(Unidad_Productiva_Table.Estado_SincronizacionUpdate.eq(false)).count()
        var countUpdatesLotes=SQLite.select().from(Lote::class.java).where(Lote_Table.EstadoSincronizacion.eq(true)).and(Lote_Table.Estado_SincronizacionUpdate.eq(false)).count()
        var countUpdatesCultivos=SQLite.select().from(Cultivo::class.java).where(Cultivo_Table.EstadoSincronizacion.eq(true)).and(Cultivo_Table.Estado_SincronizacionUpdate.eq(false)).count()
        var countUpdatesControlPlagas=SQLite.select().from(ControlPlaga::class.java).where(ControlPlaga_Table.Estado_Sincronizacion.eq(true)).and(ControlPlaga_Table.Estado_SincronizacionUpdate.eq(false)).count()
        var countUpdatesProducccion=SQLite.select().from(Produccion::class.java).where(Produccion_Table.Estado_Sincronizacion.eq(true)).and(Produccion_Table.Estado_SincronizacionUpdate.eq(false)).count()
        var countUpdatesProductos=SQLite.select().from(Producto::class.java).where(Producto_Table.Estado_Sincronizacion.eq(true)).and(Producto_Table.Estado_SincronizacionUpdate.eq(false)).count()
        var countUpdatesTransacciones=SQLite.select().from(Transaccion::class.java).where(Transaccion_Table.Estado_Sincronizacion.eq(true)).and(Transaccion_Table.Estado_SincronizacionUpdate.eq(false)).count()

        var updatesTotal= countUpdatesUnidadesProductivas+
                countUpdatesLotes+
                countUpdatesCultivos+
                countUpdatesControlPlagas+countUpdatesProducccion+countUpdatesProductos+countUpdatesTransacciones


        var quantitySync= QuantitySync(registerTotal,updatesTotal)
        postEventOkQuntitySync(RequestEventMainMenu.SYNC_RESUME,quantitySync)
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
                        val idUP = response.body()?.Id_Remote
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
                                mUnidadProductiva?.Id_Remote,
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
                                    var mUnidadProductivaPost= SQLite.select()
                                           .from(Unidad_Productiva::class.java)
                                            .where(Unidad_Productiva_Table.Estado_Sincronizacion.eq(false))
                                            .orderBy(Unidad_Productiva_Table.Unidad_Productiva_Id,false)
                                            .querySingle()
                                   if(mUnidadProductivaPost!=null){
                                        syncData()
                                    }else{
                                       syncDataLotes()
                                   }
                                } else {
                                    postEventError(RequestEventMainMenu.ERROR_EVENT, "Comprueba tu conexión")
                                }
                            }
                            override fun onFailure(call: Call<LocalizacionUp>?, t: Throwable?) {
                                postEventError(RequestEventMainMenu.ERROR_EVENT, "Comprueba tu conexión")
                            }
                        })
                    } else {
                        postEventError(RequestEventMainMenu.ERROR_EVENT, "Comprueba tu conexión")
                    }
                }
                override fun onFailure(call: Call<Unidad_Productiva>?, t: Throwable?) {
                    postEventError(RequestEventMainMenu.ERROR_EVENT, "Comprueba tu conexión")
                }
            })
        }else{
            syncDataLotes()
        }
    }

    fun syncDataLotes() {
        var mLote= SQLite.select()
                .from(Lote::class.java)
                .where(Lote_Table.EstadoSincronizacion.eq(false))
                .orderBy(Lote_Table.LoteId,false).querySingle()
        val unidad_productiva = SQLite.select().from(Unidad_Productiva::class.java).where(Unidad_Productiva_Table.Unidad_Productiva_Id.eq(mLote?.Unidad_Productiva_Id)).querySingle()
        if(mLote!=null && unidad_productiva?.Estado_Sincronizacion==true){
            val postLote = PostLote(0,
                    mLote.Area,
                    mLote.Codigo,
                    mLote.Nombre,
                    mLote.Descripcion,
                    mLote.Localizacion,
                    mLote.Localizacion_Poligono,
                    mLote.Unidad_Medida_Id,
                    unidad_productiva?.Id_Remote)

                val call = apiService?.postLote(postLote)
                call?.enqueue(object : Callback<Lote> {
                    override fun onResponse(call: Call<Lote>?, response: Response<Lote>?) {
                        if (response != null && response.code() == 201) {
                            mLote.Id_Remote = response.body()?.Id_Remote!!
                            mLote.EstadoSincronizacion = true
                            mLote.Estado_SincronizacionUpdate = true
                            mLote.save()
                            var mLotePost= SQLite.select()
                                    .from(Lote::class.java)
                                    .where(Lote_Table.EstadoSincronizacion.eq(false))
                                    .orderBy(Lote_Table.LoteId,false).querySingle()

                            if(mLotePost!=null){
                                syncDataLotes()
                            }else{
                                syncDataCultivos()
                            }
                        } else {
                            postEventError(RequestEventMainMenu.ERROR_EVENT, "Comprueba tu conexión")
                        }
                    }
                    override fun onFailure(call: Call<Lote>?, t: Throwable?) {
                        postEventError(RequestEventMainMenu.ERROR_EVENT, "Comprueba tu conexión")
                    }
                })
        }else{
            syncDataCultivos()
        }
    }

    private fun syncDataCultivos() {
        var mCultivo= SQLite.select()
                .from(Cultivo::class.java)
                .where(Cultivo_Table.EstadoSincronizacion.eq(false))
                .orderBy(Cultivo_Table.CultivoId,false).querySingle()
        val lote = SQLite.select().from(Lote::class.java).where(Lote_Table.LoteId.eq(mCultivo?.LoteId)).querySingle()
        if(mCultivo!=null && lote?.EstadoSincronizacion==true){
                val postCultivo = PostCultivo(0,
                        mCultivo?.Descripcion,
                        mCultivo?.DetalleTipoProductoId,
                        mCultivo.Unidad_Medida_Id,
                        mCultivo?.EstimadoCosecha,
                        mCultivo?.stringFechaFin,
                        mCultivo?.stringFechaInicio,
                        lote?.Id_Remote,
                        mCultivo?.Nombre,
                        mCultivo?.siembraTotal)

                val call = apiService?.postCultivo(postCultivo)
                call?.enqueue(object : Callback<Cultivo> {
                    override fun onResponse(call: Call<Cultivo>?, response: Response<Cultivo>?) {
                        if (response != null && response.code() == 201) {
                            val value = response.body()
                            mCultivo?.Id_Remote = value?.Id_Remote!!
                            mCultivo?.FechaIncio = value.FechaIncio
                            mCultivo?.FechaFin = value.FechaFin
                            mCultivo?.EstadoSincronizacion = true
                            mCultivo?.Estado_SincronizacionUpdate = true
                            mCultivo?.save()
                            var mCultivo= SQLite.select()
                                    .from(Cultivo::class.java)
                                    .where(Cultivo_Table.EstadoSincronizacion.eq(false))
                                    .orderBy(Cultivo_Table.CultivoId,false).querySingle()

                            if(mCultivo!=null){
                                syncDataCultivos()
                            }else{
                                syncDataControlPlagas()
                            }

                        } else {
                            postEventError(RequestEventMainMenu.ERROR_EVENT, "Comprueba tu conexión")
                            Log.e("error", response?.message().toString())
                        }
                    }
                    override fun onFailure(call: Call<Cultivo>?, t: Throwable?) {
                        postEventError(RequestEventMainMenu.ERROR_EVENT, "Comprueba tu conexión")
                    }
                })
        }else{
            syncDataControlPlagas()
        }
    }

    private fun syncDataControlPlagas() {
        var controlPlaga= SQLite.select()
                .from(ControlPlaga::class.java)
                .where(ControlPlaga_Table.Estado_Sincronizacion.eq(false))
                .orderBy(ControlPlaga_Table.ControlPlagaId,false).querySingle()
        val cultivo = SQLite.select().from(Cultivo::class.java).where(Cultivo_Table.CultivoId.eq(controlPlaga?.CultivoId)).querySingle()
        if(controlPlaga!=null && cultivo?.EstadoSincronizacion==true){
            val postControlPlaga = PostControlPlaga(
                    0,
                    cultivo.Id_Remote,
                    controlPlaga.Dosis,
                    controlPlaga.EnfermedadesId,
                    controlPlaga.getFechaAplicacionFormatApi(),
                    controlPlaga.TratamientoId,
                    controlPlaga.UnidadMedidaId,
                    controlPlaga.getFechaErradicacionFormatApi(),
                    controlPlaga.EstadoErradicacion
            )

            val call = apiService?.postControlPlaga(postControlPlaga)
            call?.enqueue(object : Callback<PostControlPlaga> {
                override fun onResponse(call: Call<PostControlPlaga>?, response: Response<PostControlPlaga>?) {
                    if (response != null && response.code() == 201 || response?.code() == 200) {
                        var controlPlagaResponse= response.body()
                        controlPlaga.Id_Remote = controlPlagaResponse?.Id!!
                        controlPlaga.Estado_Sincronizacion = true
                        controlPlaga?.Estado_SincronizacionUpdate = true
                        controlPlaga.save()
                        var controlPlaga= SQLite.select()
                                .from(ControlPlaga::class.java)
                                .where(ControlPlaga_Table.Estado_Sincronizacion.eq(false))
                                .orderBy(ControlPlaga_Table.ControlPlagaId,false).querySingle()
                        if(controlPlaga!=null){
                            syncDataControlPlagas()
                        }else{
                            syncDataProduccion()
                        }
                    } else {
                        postEventError(RequestEventMainMenu.ERROR_EVENT, "Comprueba tu conexión")
                    }
                }
                override fun onFailure(call: Call<PostControlPlaga>?, t: Throwable?) {
                    postEventError(RequestEventMainMenu.ERROR_EVENT, "Comprueba tu conexión")
                }
            })
        }else{
            syncDataProduccion()
        }
    }

    private fun syncDataProduccion() {
        var produccion= SQLite.select()
                .from(Produccion::class.java)
                .where(Produccion_Table.Estado_Sincronizacion.eq(false))
                .orderBy(Produccion_Table.ProduccionId,false).querySingle()
        val cultivo = SQLite.select().from(Cultivo::class.java).where(Cultivo_Table.CultivoId.eq(produccion?.CultivoId)).querySingle()
        if(produccion!=null && cultivo?.EstadoSincronizacion==true){

            val format1 = SimpleDateFormat("yyyy-MM-dd")
            val fecha_inicio = format1.format(produccion.FechaInicioProduccion)
            val fecha_fecha_fin = format1.format(produccion.FechaFinProduccion)
            val postProduccion = PostProduccion(
                    0,
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
                        produccion.Estado_Sincronizacion = true
                        produccion?.Estado_SincronizacionUpdate = true
                        produccion.save()

                        var produccionPost= SQLite.select()
                                .from(Produccion::class.java)
                                .where(Produccion_Table.Estado_Sincronizacion.eq(false))
                                .orderBy(Produccion_Table.ProduccionId,false).querySingle()

                        if(produccionPost!=null){
                            syncDataProduccion()
                        }else{
                            syncDataProductos()
                        }

                    } else {
                        postEventError(RequestEventMainMenu.ERROR_EVENT, "Comprueba tu conexión")
                    }
                }
                override fun onFailure(call: Call<PostProduccion>?, t: Throwable?) {
                    postEventError(RequestEventMainMenu.ERROR_EVENT, "Comprueba tu conexión")
                }
            })
        }else{
            syncDataProductos()

        }
    }

    private fun syncDataProductos() {
        var mProducto= SQLite.select()
                .from(Producto::class.java)
                .where(Producto_Table.Estado_Sincronizacion.eq(false))
                .orderBy(Producto_Table.ProductoId,false).querySingle()
        val cultivo = SQLite.select().from(Cultivo::class.java).where(Cultivo_Table.CultivoId.eq(mProducto?.cultivoId)).querySingle()

        if(mProducto!=null && cultivo?.EstadoSincronizacion==true){
            val postProducto = PostProducto(0,
                    mProducto.CalidadId,
                    1,
                    mProducto.CodigoUp,
                    mProducto.Descripcion,
                    mProducto.FechaLimiteDisponibilidad,
                    mProducto.Imagen,
                    true,
                    mProducto.Precio,
                    mProducto.PrecioSpecial,
                    mProducto.Stock,
                    cultivo.Id_Remote,
                    mProducto.Nombre,
                    mProducto.Unidad_Medida_Id,
                    mProducto.PrecioUnidadMedida,
                    mProducto.userId
            )

            val call = apiService?.postProducto(postProducto)
            call?.enqueue(object : Callback<Producto> {
                override fun onResponse(call: Call<Producto>?, response: Response<Producto>?) {
                    if (response != null && response.code() == 201) {
                        val value = response.body()
                        mProducto.Id_Remote = value?.Id_Remote!!
                        mProducto.Estado_Sincronizacion = true
                        mProducto.Estado_SincronizacionUpdate = true
                        mProducto.save()

                        var mProductoPost= SQLite.select()
                                .from(Producto::class.java)
                                .where(Producto_Table.Estado_Sincronizacion.eq(false))
                                .orderBy(Producto_Table.ProductoId,false).querySingle()

                        if(mProductoPost!=null){
                            syncDataProductos()
                        }else{
                            syncDataTransacciones()
                        }
                    } else {
                        postEventError(RequestEventMainMenu.ERROR_EVENT, "Comprueba tu conexión")
                        Log.e("error", response?.message().toString())
                    }
                }
                override fun onFailure(call: Call<Producto>?, t: Throwable?) {
                    postEventError(RequestEventMainMenu.ERROR_EVENT, "Comprueba tu conexión")
                }
            })

        }else{
            syncDataTransacciones()
        }
    }

    private fun syncDataTransacciones() {
        var transaccion= SQLite.select()
                .from(Transaccion::class.java)
                .where(Transaccion_Table.Estado_Sincronizacion.eq(false))
                .orderBy(Transaccion_Table.TransaccionId,false).querySingle()
        val cultivo = SQLite.select().from(Cultivo::class.java).where(Cultivo_Table.CultivoId.eq(transaccion?.Cultivo_Id)).querySingle()
        if(transaccion!=null && cultivo?.EstadoSincronizacion==true){

            var terceroLocal= SQLite.select().from(Tercero::class.java).where(Tercero_Table.TerceroId.eq(transaccion.TerceroId)).querySingle()
            val postTercero = PostTercero(
                    0,
                    terceroLocal?.Nombre,
                    terceroLocal?.Apellido,
                    terceroLocal?.NitRut,
                    ""
            )
            val postTrecero = apiService?.postTercero(postTercero)
            postTrecero?.enqueue(object : Callback<PostTercero> {
                override fun onResponse(call: Call<PostTercero>?, response: Response<PostTercero>?) {
                    if (response != null && response.code() == 201 || response?.code() == 200) {
                        terceroLocal?.Id_Remote = response.body()?.Id!!
                        terceroLocal?.Estado_Sincronizacion = true
                        transaccion?.Estado_SincronizacionUpdate = true
                        terceroLocal?.save()

                        val postTransaccion = PostTransaccion(
                                0,
                                transaccion.Concepto,
                                transaccion.EstadoId,
                                transaccion.getFechaTransacccionFormatApi(),
                                transaccion.NaturalezaId,
                                transaccion.PucId,
                                terceroLocal?.Id_Remote,
                                transaccion.Valor_Total,
                                transaccion.Cantidad,
                                cultivo.Id_Remote,
                                transaccion.UsuarioId
                        )

                        val call = apiService?.postTransaccion(postTransaccion)
                        call?.enqueue(object : Callback<PostTransaccion> {
                            override fun onResponse(call: Call<PostTransaccion>?, response: Response<PostTransaccion>?) {
                                if (response != null && response.code() == 201 || response?.code() == 200) {
                                    transaccion.Id_Remote = response.body()?.Id!!
                                    transaccion.Estado_Sincronizacion = true
                                    transaccion.Estado_SincronizacionUpdate = true
                                    transaccion.save()

                                    var transaccionPost= SQLite.select()
                                            .from(Transaccion::class.java)
                                            .where(Transaccion_Table.Estado_Sincronizacion.eq(false))
                                            .orderBy(Transaccion_Table.TransaccionId,false).querySingle()

                                    if(transaccionPost!=null){
                                        syncDataTransacciones()
                                    }else{
                                        postEventOk(RequestEventMainMenu.SYNC_EVENT)
                                    }
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
        }else{

            postEventOk(RequestEventMainMenu.SYNC_EVENT)
        }
    }




    //region Events
    private fun postEventOk(type: Int) {
        postEvent(type, null,null,null)
    }

    private fun postEventOkQuntitySync(type: Int,  quantitySync: QuantitySync?) {
        var QuantitySyncMutable:Object?=null
        if(quantitySync!=null){
            QuantitySyncMutable = quantitySync as Object
        }
        postEvent(type, null,QuantitySyncMutable,null)
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