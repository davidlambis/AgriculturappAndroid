package com.interedes.agriculturappv3.services.services.request

import android.util.Log
import com.interedes.agriculturappv3.libs.EventBus
import com.interedes.agriculturappv3.libs.GreenRobotEventBus
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
import com.interedes.agriculturappv3.modules.models.usuario.Usuario
import com.interedes.agriculturappv3.modules.models.usuario.Usuario_Table
import com.interedes.agriculturappv3.modules.models.ventas.RequestApi.PostTercero
import com.interedes.agriculturappv3.modules.models.ventas.RequestApi.PostTransaccion
import com.interedes.agriculturappv3.modules.models.ventas.Tercero
import com.interedes.agriculturappv3.modules.models.ventas.Tercero_Table
import com.interedes.agriculturappv3.modules.models.ventas.Transaccion
import com.interedes.agriculturappv3.modules.models.ventas.Transaccion_Table
import com.interedes.agriculturappv3.services.api.ApiInterface
import com.interedes.agriculturappv3.services.services.events.EventsService
import com.interedes.agriculturappv3.services.services.IMainViewService
import com.raizlabs.android.dbflow.kotlinextensions.save
import com.raizlabs.android.dbflow.kotlinextensions.update
import com.raizlabs.android.dbflow.sql.language.SQLite
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.math.BigDecimal
import java.math.MathContext
import java.text.SimpleDateFormat

class RequestPostDataSync : IMainViewService.RepositoryPost {

    var eventBus: EventBus? = null
    var apiService: ApiInterface? = null

    init {
        eventBus = GreenRobotEventBus()
        apiService = ApiInterface.create()
    }

    //region UPDATE

     fun syncUpdateDataUnidadProductiva() {
        val usuario= getLastUserLogued()
        val mUnidadProductiva= SQLite.select()
                .from(Unidad_Productiva::class.java)
                .where(Unidad_Productiva_Table.Estado_Sincronizacion.eq(true))
                .and(Unidad_Productiva_Table.Estado_SincronizacionUpdate.eq(false))
                .and(Unidad_Productiva_Table.UsuarioId.eq(usuario?.Id))
                .orderBy(Unidad_Productiva_Table.Unidad_Productiva_Id,false).querySingle()

        if(mUnidadProductiva!=null){
            val areaBig = BigDecimal(mUnidadProductiva.Area!!, MathContext.DECIMAL64)
            val updateUnidadProductiva = PostUnidadProductiva(mUnidadProductiva.Id_Remote,
                    areaBig,
                    mUnidadProductiva.CiudadId,
                    mUnidadProductiva.Codigo,
                    mUnidadProductiva.UnidadMedidaId,
                    mUnidadProductiva.UsuarioId,
                    mUnidadProductiva.descripcion,
                    mUnidadProductiva.nombre)
            // mUnidadProductiva?.CiudadId = 1
            val call = apiService?.updateUnidadProductiva(updateUnidadProductiva, mUnidadProductiva.Id_Remote!!)
            call?.enqueue(object : Callback<Unidad_Productiva> {
                override fun onResponse(call: Call<Unidad_Productiva>?, response: Response<Unidad_Productiva>?) {
                    if (response != null && response.code() == 200) {

                        val latitudBig = BigDecimal(mUnidadProductiva.Latitud!!, MathContext.DECIMAL64)
                        val longitudBig = BigDecimal(mUnidadProductiva.Longitud!!, MathContext.DECIMAL64)

                        val postLocalizacionUnidadProductiva = LocalizacionUp(mUnidadProductiva.LocalizacionUpId,
                                "",
                                mUnidadProductiva?.Coordenadas,
                                if (mUnidadProductiva?.Direccion!=null) mUnidadProductiva.Direccion else "",
                                mUnidadProductiva?.DireccionAproximadaGps,
                                latitudBig,
                                longitudBig,
                                "",
                                "",
                                "",
                                mUnidadProductiva?.Id_Remote,
                                "")

                        val callLocalizacion = apiService?.updateLocalizacionUnidadProductiva(postLocalizacionUnidadProductiva, mUnidadProductiva.LocalizacionUpId!!)
                        callLocalizacion?.enqueue(object : Callback<LocalizacionUp> {
                            override fun onResponse(call: Call<LocalizacionUp>?, response: Response<LocalizacionUp>?) {
                                if (response != null && response.code() == 200) {
                                    mUnidadProductiva.Estado_SincronizacionUpdate=true
                                    mUnidadProductiva.update()



                                    val mUnidadProductivaUpdate= SQLite.select()
                                            .from(Unidad_Productiva::class.java)
                                            .where(Unidad_Productiva_Table.Estado_Sincronizacion.eq(true))
                                            .and(Unidad_Productiva_Table.Estado_SincronizacionUpdate.eq(false))
                                            .and(Unidad_Productiva_Table.UsuarioId.eq(usuario?.Id))
                                            .orderBy(Unidad_Productiva_Table.Unidad_Productiva_Id,false)
                                            .querySingle()

                                    if(mUnidadProductivaUpdate!=null){
                                        syncUpdateDataUnidadProductiva()
                                    }else{
                                        syncUpdateDataLote()
                                    }

                                } else {
                                    postEventError(EventsService.ERROR_EVENT, "UpdateError 500...")
                                }
                            }
                            override fun onFailure(call: Call<LocalizacionUp>?, t: Throwable?) {
                                postEventError(EventsService.ERROR_EVENT, "UpdateError Failure...")
                            }
                        })
                    } else {
                        postEventError(EventsService.ERROR_EVENT, "UpdateError 500...")
                    }
                }
                override fun onFailure(call: Call<Unidad_Productiva>?, t: Throwable?) {
                    postEventError(EventsService.ERROR_EVENT, "UpdateError Failure...")
                }
            })
        }else{
            syncUpdateDataLote()
        }
    }

    fun syncUpdateDataLote() {
        val usuario=getLastUserLogued()
        val mLote= SQLite.select()
                .from(Lote::class.java)
                .where(Lote_Table.EstadoSincronizacion.eq(true))
                .and(Lote_Table.Estado_SincronizacionUpdate.eq(false))
                .and(Lote_Table.UsuarioId.eq(usuario?.Id))
                .orderBy(Lote_Table.LoteId,false).querySingle()

        val unidad_productiva = SQLite.select().from(Unidad_Productiva::class.java).where(Unidad_Productiva_Table.Unidad_Productiva_Id.eq(mLote?.Unidad_Productiva_Id)).querySingle()

        if(mLote!=null){
            val areaBig = BigDecimal(mLote.Area!!, MathContext.DECIMAL64)
            //TODO se valida estado de sincronizacion  para actualizar,actualizacion remota
            val postLote = PostLote(mLote.Id_Remote,
                    areaBig,
                    mLote.Codigo,
                    mLote.Nombre,
                    mLote.Descripcion,
                    mLote.Localizacion,
                    mLote.Localizacion_Poligono,
                    mLote.Unidad_Medida_Id,
                    unidad_productiva?.Id_Remote)


            val call = apiService?.updateLote(postLote, mLote.Id_Remote!!)
            call?.enqueue(object : Callback<Lote> {
                override fun onResponse(call: Call<Lote>?, response: Response<Lote>?) {
                    if (response != null && response.code() == 200) {
                        mLote.Estado_SincronizacionUpdate = true
                        mLote.update()

                        val mLotePost= SQLite.select()
                                .from(Lote::class.java)
                                .where(Lote_Table.EstadoSincronizacion.eq(true))
                                .and(Lote_Table.Estado_SincronizacionUpdate.eq(false))
                                .and(Lote_Table.UsuarioId.eq(usuario?.Id))
                                .orderBy(Lote_Table.LoteId,false).querySingle()

                        if(mLotePost!=null){
                            syncUpdateDataLote()
                        }else{
                            syncUpdateDataCultivos()
                        }
                    } else {
                        postEventError(EventsService.ERROR_EVENT, "UpdateError 500...")
                    }
                }

                override fun onFailure(call: Call<Lote>?, t: Throwable?) {
                    postEventError(EventsService.ERROR_EVENT, "UpdateError Failure...")
                }
            })
        }else{
            syncUpdateDataCultivos()
        }

    }

    private fun syncUpdateDataCultivos() {
        val usuario=getLastUserLogued()
        val mCultivo= SQLite.select()
                .from(Cultivo::class.java)
                .where(Cultivo_Table.EstadoSincronizacion.eq(true))
                .and(Cultivo_Table.Estado_SincronizacionUpdate.eq(false))
                .and(Cultivo_Table.UsuarioId.eq(usuario?.Id))
                .orderBy(Cultivo_Table.CultivoId,false).querySingle()

        if(mCultivo!=null){
            val lote = SQLite.select().from(Lote::class.java).where(Lote_Table.LoteId.eq(mCultivo.LoteId)).querySingle()
            //TODO se valida estado de sincronizacion  para actualizar,actualizacion remota
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
                            mCultivo.Estado_SincronizacionUpdate = true
                            mCultivo.update()
                            val mCultivo= SQLite.select()
                                    .from(Cultivo::class.java)
                                    .where(Cultivo_Table.EstadoSincronizacion.eq(true))
                                    .and(Cultivo_Table.Estado_SincronizacionUpdate.eq(false))
                                    .and(Cultivo_Table.UsuarioId.eq(usuario?.Id))
                                    .orderBy(Cultivo_Table.CultivoId,false).querySingle()

                            if(mCultivo!=null){
                                syncUpdateDataCultivos()
                            }else{
                                syncUpdateDataControlPlagas()
                            }
                        } else {
                            postEventError(EventsService.ERROR_EVENT, "UpdateError Cultivo 500...")
                        }
                    }
                    override fun onFailure(call: Call<Cultivo>?, t: Throwable?) {
                        postEventError(EventsService.ERROR_EVENT, "UpdateError Cultivo Failure...")
                    }
                })
        }else{
            syncUpdateDataControlPlagas()
        }
    }


    private fun syncUpdateDataControlPlagas() {
        val usuario= getLastUserLogued()
        val controlPlaga= SQLite.select()
                .from(ControlPlaga::class.java)
                .where(ControlPlaga_Table.Estado_SincronizacionUpdate.eq(false))
                .and(ControlPlaga_Table.Estado_Sincronizacion.eq(true))
                .and(ControlPlaga_Table.UsuarioId.eq(usuario?.Id))
                .orderBy(ControlPlaga_Table.ControlPlagaId,false).querySingle()
        if(controlPlaga!=null){
            val cultivo = SQLite.select().from(Cultivo::class.java).where(Cultivo_Table.CultivoId.eq(controlPlaga.CultivoId)).querySingle()
            val postControlPlaga = PostControlPlaga(
                    controlPlaga.Id_Remote!!,
                    cultivo?.Id_Remote,
                    controlPlaga.Dosis,
                    controlPlaga.EnfermedadesId,
                    controlPlaga.getFechaAplicacionFormatApi(),
                    controlPlaga.TratamientoId,
                    controlPlaga.UnidadMedidaId,
                    controlPlaga.getFechaErradicacionFormatApi(),
                    controlPlaga.EstadoErradicacion
            )
            val call = apiService?.updateControlPlaga(postControlPlaga,controlPlaga.Id_Remote!!)
            call?.enqueue(object : Callback<PostControlPlaga> {
                override fun onResponse(call: Call<PostControlPlaga>?, response: Response<PostControlPlaga>?) {
                    if (response != null && response.code() == 200) {
                        var controlPlagaResponse= response.body()
                        controlPlaga.Estado_Sincronizacion = true
                        controlPlaga.Estado_SincronizacionUpdate = true
                        val controlPlaga= SQLite.select()
                                .from(ControlPlaga::class.java)
                                .where(ControlPlaga_Table.Estado_SincronizacionUpdate.eq(false))
                                .and(ControlPlaga_Table.Estado_Sincronizacion.eq(true))
                                .and(ControlPlaga_Table.UsuarioId.eq(usuario?.Id))
                                .orderBy(ControlPlaga_Table.ControlPlagaId,false).querySingle()
                        if(controlPlaga!=null){
                            syncUpdateDataControlPlagas()
                        }else{
                            syncUpdateDataProduccion()
                        }
                    } else {
                        postEventError(EventsService.ERROR_EVENT, "UpdateError ControlPlaga 500...")
                    }
                }
                override fun onFailure(call: Call<PostControlPlaga>?, t: Throwable?) {
                    postEventError(EventsService.ERROR_EVENT, "UpdateError ControlPlaga 500...")
                }
            })
        }else{
            syncUpdateDataProduccion()
        }
    }

    private fun syncUpdateDataProduccion() {

        val usuario=getLastUserLogued()
        val produccion= SQLite.select()
                .from(Produccion::class.java)
                .where(Produccion_Table.Estado_SincronizacionUpdate.eq(false))
                .and(Produccion_Table.Estado_Sincronizacion.eq(true))
                .and(Produccion_Table.UsuarioId.eq(usuario?.Id))
                .orderBy(Produccion_Table.ProduccionId,false).querySingle()

        if(produccion!=null){
            val cultivo = SQLite.select().from(Cultivo::class.java).where(Cultivo_Table.CultivoId.eq(produccion.CultivoId)).querySingle()
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

                        produccion.Estado_SincronizacionUpdate = true
                        produccion.update()

                        val produccionPost= SQLite.select()
                                .from(Produccion::class.java)
                                .where(Produccion_Table.Estado_SincronizacionUpdate.eq(false))
                                .and(Produccion_Table.Estado_Sincronizacion.eq(true))
                                .and(Produccion_Table.UsuarioId.eq(usuario?.Id))
                                .orderBy(Produccion_Table.ProduccionId,false).querySingle()

                        if(produccionPost!=null){
                            syncUpdateDataProduccion()
                        }else{
                            syncUpdateDataProductos()
                        }

                    } else {
                        postEventError(EventsService.ERROR_EVENT, "Error Produccion 500...")
                    }
                }
                override fun onFailure(call: Call<PostProduccion>?, t: Throwable?) {
                    postEventError(EventsService.ERROR_EVENT, "Faillure Produccion 500...")
                }
            })
        }else{
            syncUpdateDataProductos()

        }
    }

    private fun syncUpdateDataProductos() {
        val usuario=getLastUserLogued()
        val mProducto= SQLite.select()
                .from(Producto::class.java)
                .where(Producto_Table.Estado_Sincronizacion.eq(true))
                .and(Producto_Table.Estado_SincronizacionUpdate.eq(false))
                .and(Producto_Table.userId.eq(usuario?.Id))
                .orderBy(Producto_Table.ProductoId,false).querySingle()

        if(mProducto!=null){
                val cultivo = SQLite.select().from(Cultivo::class.java).where(Cultivo_Table.CultivoId.eq(mProducto.cultivoId)).querySingle()
                val postProducto = PostProducto(mProducto.Id_Remote,
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
                        cultivo?.Id_Remote,
                        mProducto.Nombre,
                        mProducto.Unidad_Medida_Id,
                        mProducto.PrecioUnidadMedida,
                        mProducto.userId
                )

                val call = apiService?.updateProducto(postProducto, mProducto.Id_Remote!!)
                call?.enqueue(object : Callback<Producto> {
                    override fun onResponse(call: Call<Producto>?, response: Response<Producto>?) {
                        if (response != null && response.code() == 200) {
                            mProducto.Estado_SincronizacionUpdate=true
                            mProducto.update()

                            val mProductoPost= SQLite.select()
                                    .from(Producto::class.java)
                                    .where(Producto_Table.Estado_SincronizacionUpdate.eq(false))
                                    .and(Producto_Table.Estado_Sincronizacion.eq(true))
                                    .and(Producto_Table.userId.eq(usuario?.Id))
                                    .orderBy(Producto_Table.ProductoId,false).querySingle()

                            if(mProductoPost!=null){
                                syncUpdateDataProductos()
                            }else{
                                syncUpdateDataTransacciones()
                            }

                        } else {
                            postEventError(EventsService.ERROR_EVENT, "Faillure Productos 500...")
                        }
                    }
                    override fun onFailure(call: Call<Producto>?, t: Throwable?) {
                        postEventError(EventsService.ERROR_EVENT, "Faillure Productos 500...")
                    }
                })
        }else{
            syncUpdateDataTransacciones()
        }
    }

    private fun syncUpdateDataTransacciones() {

        val usuario=getLastUserLogued()

        val transaccion= SQLite.select()
                .from(Transaccion::class.java)
                .where(Transaccion_Table.Estado_SincronizacionUpdate.eq(false))
                .and(Transaccion_Table.Estado_Sincronizacion.eq(true))
                .and(Transaccion_Table.UsuarioId.eq(usuario?.Id))
                .orderBy(Transaccion_Table.TransaccionId,false).querySingle()

        val cultivo = SQLite.select().from(Cultivo::class.java).where(Cultivo_Table.CultivoId.eq(transaccion?.Cultivo_Id)).querySingle()
        if(transaccion!=null && cultivo?.EstadoSincronizacion==true){

            val terceroLocal=Tercero(TerceroId = transaccion.TerceroId,Nombre = transaccion.Nombre_Tercero,Apellido = "",NitRut = transaccion.Identificacion_Tercero,Usuario_Id = transaccion.UsuarioId)

            val cultivo = SQLite.select().from(Cultivo::class.java).where(Cultivo_Table.CultivoId.eq(transaccion.Cultivo_Id)).querySingle()
            val tercero = SQLite.select().from(Tercero::class.java).where(Tercero_Table.TerceroId.eq(transaccion.TerceroId)).querySingle()
            terceroLocal.Id_Remote=tercero?.Id_Remote
            val postTercero = PostTercero(
                    tercero?.Id_Remote,
                    terceroLocal.Nombre,
                    terceroLocal.Apellido,
                    terceroLocal.NitRut,
                    ""
            )

            val call = apiService?.updateTercero(postTercero, tercero?.Id_Remote!!)
            call?.enqueue(object : Callback<PostTercero> {
                override fun onResponse(call: Call<PostTercero>?, response: Response<PostTercero>?) {
                    if (response != null && response.code() == 200  || response?.code()==204) {

                        terceroLocal.update()

                        val decimalBig = BigDecimal(transaccion.Valor_Total!!, MathContext.DECIMAL64)
                        val cantidadBig = BigDecimal(transaccion.Cantidad!!, MathContext.DECIMAL64)

                        val postTransaccion = PostTransaccion(
                                transaccion.Id_Remote,
                                transaccion.Concepto,
                                transaccion.EstadoId,
                                transaccion.getFechaTransacccionFormatApi(),
                                transaccion.NaturalezaId,
                                transaccion.PucId,
                                terceroLocal.Id_Remote,
                                decimalBig,
                                cantidadBig,
                                cultivo?.Id_Remote,
                                getLastUserLogued()?.Id
                        )

                        val calltransaccion = apiService?.updateTransaccion(postTransaccion, transaccion.Id_Remote!!)
                        calltransaccion?.enqueue(object : Callback<PostTransaccion> {
                            override fun onResponse(call: Call<PostTransaccion>?, response: Response<PostTransaccion>?) {
                                if (response != null && response.code() == 200) {
                                    transaccion.Estado_Sincronizacion=true
                                    transaccion.Estado_SincronizacionUpdate=true
                                    transaccion.update()

                                    val transaccionPost= SQLite.select()
                                            .from(Transaccion::class.java)
                                            .where(Transaccion_Table.Estado_SincronizacionUpdate.eq(false))
                                            .and(Transaccion_Table.Estado_Sincronizacion.eq(true))
                                            .and(Transaccion_Table.UsuarioId.eq(usuario?.Id))
                                            .orderBy(Transaccion_Table.TransaccionId,false).querySingle()


                                    if(transaccionPost!=null){
                                        syncUpdateDataTransacciones()
                                    }else{
                                        postEventOk(EventsService.POST_SYNC_EVENT)
                                    }
                                } else {
                                    postEventError(EventsService.ERROR_EVENT, "Error TransaccionUpdate 500...")
                                }
                            }
                            override fun onFailure(call: Call<PostTransaccion>?, t: Throwable?) {
                                postEventError(EventsService.ERROR_EVENT, "Faillure TransaccionUpdate 500...")
                            }
                        })
                    } else {
                        postEventError(EventsService.ERROR_EVENT, "Error TransaccionUpdate 500...")
                    }
                }
                override fun onFailure(call: Call<PostTercero>?, t: Throwable?) {
                    postEventError(EventsService.ERROR_EVENT, "Faillure TerceroUpdate 500...")
                }
            })
        }else{
            postEventOk(EventsService.POST_SYNC_EVENT)
        }
    }
    //endregion

    //region POST  DATASYNC
    override fun syncData() {
        val usuario= getLastUserLogued()
        val mUnidadProductiva= SQLite.select()
                .from(Unidad_Productiva::class.java)
                .where(Unidad_Productiva_Table.Estado_Sincronizacion.eq(false))
                .and(Unidad_Productiva_Table.UsuarioId.eq(usuario?.Id))
                .orderBy(Unidad_Productiva_Table.Unidad_Productiva_Id,false).querySingle()
        if(mUnidadProductiva!=null){

            val areaBig = BigDecimal(mUnidadProductiva.Area!!, MathContext.DECIMAL64)
            val postUnidadProductiva = PostUnidadProductiva(0,
                    areaBig,
                    mUnidadProductiva.CiudadId,
                    mUnidadProductiva.Codigo,
                    mUnidadProductiva.UnidadMedidaId,
                    mUnidadProductiva.UsuarioId,
                    mUnidadProductiva.descripcion,
                    mUnidadProductiva.nombre)

            // mUnidadProductiva?.CiudadId = 1
            val call = apiService?.postUnidadProductiva(postUnidadProductiva)
            call?.enqueue(object : Callback<Unidad_Productiva> {

                override fun onResponse(call: Call<Unidad_Productiva>?, response: Response<Unidad_Productiva>?) {
                    if (response != null && response.code() == 201) {

                        //Thread.sleep(100)
                        val idUP = response.body()?.Id_Remote
                        if(idUP!!>0){

                            mUnidadProductiva.Id_Remote = idUP
                            //mUnidadProductiva?.save()
                            //postLocalizacionUnidadProductiva

                            val latitudBig = BigDecimal(mUnidadProductiva.Latitud!!, MathContext.DECIMAL64)
                            val longitudBig = BigDecimal(mUnidadProductiva.Longitud!!, MathContext.DECIMAL64)

                            val postLocalizacionUnidadProductiva = LocalizacionUp(0,
                                    "",
                                    mUnidadProductiva.Coordenadas,
                                    if (mUnidadProductiva.Direccion!=null) mUnidadProductiva.Direccion else "",
                                    mUnidadProductiva.DireccionAproximadaGps,
                                    latitudBig,
                                    longitudBig,
                                    "",
                                    "",
                                    "",
                                    mUnidadProductiva.Id_Remote,
                                    "")

                            val callLocalizacion = apiService?.postLocalizacionUnidadProductiva(postLocalizacionUnidadProductiva)
                            callLocalizacion?.enqueue(object : Callback<LocalizacionUp> {
                                override fun onResponse(call: Call<LocalizacionUp>?, response: Response<LocalizacionUp>?) {
                                    if (response != null && response.code() == 201) {
                                        val idLocalizacion = response.body()?.Id
                                        mUnidadProductiva.LocalizacionUpId=idLocalizacion
                                        mUnidadProductiva.Estado_Sincronizacion = true
                                        mUnidadProductiva.Estado_SincronizacionUpdate=true
                                        mUnidadProductiva.update()
                                        //postLocalizacionUnidadProductiva
                                        val mUnidadProductivaPost= SQLite.select()
                                                .from(Unidad_Productiva::class.java)
                                                .where(Unidad_Productiva_Table.Estado_Sincronizacion.eq(false))
                                                .and(Unidad_Productiva_Table.UsuarioId.eq(usuario?.Id))
                                                .orderBy(Unidad_Productiva_Table.Unidad_Productiva_Id,false)
                                                .querySingle()
                                        if(mUnidadProductivaPost!=null){
                                            syncData()
                                        }else{
                                            syncDataLotes()
                                        }
                                    } else {
                                        postEventError(EventsService.ERROR_EVENT, "Error 500...")
                                    }
                                }
                                override fun onFailure(call: Call<LocalizacionUp>?, t: Throwable?) {
                                    postEventError(EventsService.ERROR_EVENT, "Error Faillure...")
                                }
                            })

                        }else{
                            syncData()
                        }
                    } else {
                        postEventError(EventsService.ERROR_EVENT, "Error 500...")
                    }
                }
                override fun onFailure(call: Call<Unidad_Productiva>?, t: Throwable?) {
                    postEventError(EventsService.ERROR_EVENT, "Error Failure...")
                }
            })
        }else{
            syncDataLotes()
        }
    }

    fun syncDataLotes() {
        val usuario=getLastUserLogued()
        val mLote= SQLite.select()
                .from(Lote::class.java)
                .where(Lote_Table.EstadoSincronizacion.eq(false))
                .and(Lote_Table.UsuarioId.eq(usuario?.Id))
                .orderBy(Lote_Table.LoteId,false).querySingle()
        val unidad_productiva = SQLite.select().from(Unidad_Productiva::class.java).where(Unidad_Productiva_Table.Unidad_Productiva_Id.eq(mLote?.Unidad_Productiva_Id)).querySingle()
        if(mLote!=null && unidad_productiva?.Estado_Sincronizacion==true){

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

                        if(response.body()?.Id_Remote!!>0){

                            mLote.Id_Remote = response.body()?.Id_Remote!!
                            mLote.EstadoSincronizacion = true
                            mLote.Estado_SincronizacionUpdate = true
                            mLote.save()
                            val mLotePost= SQLite.select()
                                    .from(Lote::class.java)
                                    .where(Lote_Table.EstadoSincronizacion.eq(false))
                                    .and(Lote_Table.UsuarioId.eq(usuario?.Id))
                                    .orderBy(Lote_Table.LoteId,false).querySingle()

                            if(mLotePost!=null){
                                syncDataLotes()
                            }else{
                                syncDataCultivos()
                            }
                        }else{
                            syncDataLotes()
                        }
                    } else {
                        postEventError(EventsService.ERROR_EVENT, "Error 500...")
                    }
                }
                override fun onFailure(call: Call<Lote>?, t: Throwable?) {
                    postEventError(EventsService.ERROR_EVENT, "Error Failure...")
                }
            })
        }else{
            syncDataCultivos()
        }
    }

    private fun syncDataCultivos() {

        val usuario=getLastUserLogued()
        val mCultivo= SQLite.select()
                .from(Cultivo::class.java)
                .where(Cultivo_Table.EstadoSincronizacion.eq(false))
                .and(Cultivo_Table.UsuarioId.eq(usuario?.Id))
                .orderBy(Cultivo_Table.CultivoId,false).querySingle()
        val lote = SQLite.select().from(Lote::class.java).where(Lote_Table.LoteId.eq(mCultivo?.LoteId)).querySingle()
        if(mCultivo!=null && lote?.EstadoSincronizacion==true){

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

                        if(response.body()?.Id_Remote!!>0){
                            mCultivo.Id_Remote = value?.Id_Remote!!
                            mCultivo.FechaIncio = value.FechaIncio
                            mCultivo.FechaFin = value.FechaFin
                            mCultivo.EstadoSincronizacion = true
                            mCultivo.Estado_SincronizacionUpdate = true
                            mCultivo.save()
                            val mCultivo= SQLite.select()
                                    .from(Cultivo::class.java)
                                    .where(Cultivo_Table.EstadoSincronizacion.eq(false))
                                    .and(Cultivo_Table.UsuarioId.eq(usuario?.Id))
                                    .orderBy(Cultivo_Table.CultivoId,false).querySingle()

                            if(mCultivo!=null){
                                syncDataCultivos()
                            }else{
                                syncDataControlPlagas()
                            }
                        }else{
                            syncDataCultivos()
                        }
                    } else {
                        postEventError(EventsService.ERROR_EVENT, "Error 500...")
                        Log.e("error", response?.message().toString())
                    }
                }
                override fun onFailure(call: Call<Cultivo>?, t: Throwable?) {
                    postEventError(EventsService.ERROR_EVENT, "Error Failure...")
                }
            })
        }else{
            syncDataControlPlagas()
        }
    }

    private fun syncDataControlPlagas() {

        val usuario= getLastUserLogued()

        val controlPlaga= SQLite.select()
                .from(ControlPlaga::class.java)
                .where(ControlPlaga_Table.Estado_Sincronizacion.eq(false))
                .and(ControlPlaga_Table.UsuarioId.eq(usuario?.Id))
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

                        val controlPlagaResponse= response.body()
                        if(controlPlagaResponse?.Id!!>0){
                            controlPlaga.Id_Remote = controlPlagaResponse.Id
                            controlPlaga.Estado_Sincronizacion = true
                            controlPlaga.Estado_SincronizacionUpdate = true
                            controlPlaga.save()


                            val controlPlaga= SQLite.select()
                                    .from(ControlPlaga::class.java)
                                    .where(ControlPlaga_Table.Estado_Sincronizacion.eq(false))
                                    .and(ControlPlaga_Table.UsuarioId.eq(usuario?.Id))
                                    .orderBy(ControlPlaga_Table.ControlPlagaId,false).querySingle()
                            if(controlPlaga!=null){
                                syncDataControlPlagas()
                            }else{
                                syncDataProduccion()
                            }
                        }else{
                            syncDataControlPlagas()
                        }
                    } else {
                        postEventError(EventsService.ERROR_EVENT, "Error 500...")
                    }
                }
                override fun onFailure(call: Call<PostControlPlaga>?, t: Throwable?) {
                    postEventError(EventsService.ERROR_EVENT, "Error Failure...")
                }
            })
        }else{
            syncDataProduccion()
        }
    }

    private fun syncDataProduccion() {

        val usuario=getLastUserLogued()

        val produccion= SQLite.select()
                .from(Produccion::class.java)
                .where(Produccion_Table.Estado_Sincronizacion.eq(false))
                .and(Produccion_Table.UsuarioId.eq(usuario?.Id))
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

                        if(response.body()?.Id!!>0){

                            produccion.Estado_Sincronizacion = true
                            produccion.Estado_SincronizacionUpdate = true
                            produccion.save()

                            val produccionPost= SQLite.select()
                                    .from(Produccion::class.java)
                                    .where(Produccion_Table.Estado_Sincronizacion.eq(false))
                                    .and(Produccion_Table.UsuarioId.eq(usuario?.Id))
                                    .orderBy(Produccion_Table.ProduccionId,false).querySingle()

                            if(produccionPost!=null){
                                syncDataProduccion()
                            }else{
                                syncDataProductos()
                            }
                        }else{
                            syncDataProduccion()
                        }
                    } else {
                        postEventError(EventsService.ERROR_EVENT, "Error 500...")
                    }
                }
                override fun onFailure(call: Call<PostProduccion>?, t: Throwable?) {
                    postEventError(EventsService.ERROR_EVENT, "Error Failure...")
                }
            })
        }else{
            syncDataProductos()

        }
    }

    private fun syncDataProductos() {
        val usuario=getLastUserLogued()

        val mProducto= SQLite.select()
                .from(Producto::class.java)
                .where(Producto_Table.Estado_Sincronizacion.eq(false))
                .and(Producto_Table.userId.eq(usuario?.Id))
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

                        if(value?.Id_Remote!!>0){
                            mProducto.Id_Remote = value.Id_Remote!!
                            mProducto.Estado_Sincronizacion = true
                            mProducto.Estado_SincronizacionUpdate = true
                            mProducto.save()


                            val mProductoPost= SQLite.select()
                                    .from(Producto::class.java)
                                    .where(Producto_Table.Estado_Sincronizacion.eq(false))
                                    .and(Producto_Table.userId.eq(usuario?.Id))
                                    .orderBy(Producto_Table.ProductoId,false).querySingle()


                            if(mProductoPost!=null){
                                syncDataProductos()
                            }else{
                                syncDataTransacciones()
                            }
                        }else{
                            syncDataProductos()
                        }
                    } else {
                        postEventError(EventsService.ERROR_EVENT, "Error 500...")
                        Log.e("error", response?.message().toString())
                    }
                }
                override fun onFailure(call: Call<Producto>?, t: Throwable?) {
                    postEventError(EventsService.ERROR_EVENT, "Error Failure...")
                }
            })

        }else{
            syncDataTransacciones()
        }
    }

    private fun syncDataTransacciones() {

        val usuario=getLastUserLogued()

        val transaccion= SQLite.select()
                .from(Transaccion::class.java)
                .where(Transaccion_Table.Estado_Sincronizacion.eq(false))
                .and(Transaccion_Table.UsuarioId.eq(usuario?.Id))
                .orderBy(Transaccion_Table.TransaccionId,false).querySingle()

        val cultivo = SQLite.select().from(Cultivo::class.java).where(Cultivo_Table.CultivoId.eq(transaccion?.Cultivo_Id)).querySingle()
        if(transaccion!=null && cultivo?.EstadoSincronizacion==true){

            val terceroLocal= SQLite.select().from(Tercero::class.java).where(Tercero_Table.TerceroId.eq(transaccion.TerceroId)).querySingle()
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

                        if(response.body()?.Id!!>0){
                            terceroLocal?.Estado_Sincronizacion = true
                            transaccion.Estado_SincronizacionUpdate = true
                            terceroLocal?.save()

                            val decimal = BigDecimal(transaccion.Valor_Total!!, MathContext.DECIMAL64)
                            val cantidad = BigDecimal(transaccion.Cantidad!!, MathContext.DECIMAL64)

                            val postTransaccion = PostTransaccion(
                                    0,
                                    transaccion.Concepto,
                                    transaccion.EstadoId,
                                    transaccion.getFechaTransacccionFormatApi(),
                                    transaccion.NaturalezaId,
                                    transaccion.PucId,
                                    terceroLocal?.Id_Remote,
                                    decimal,
                                    cantidad,
                                    cultivo.Id_Remote,
                                    transaccion.UsuarioId
                            )

                            val callTransa = apiService?.postTransaccion(postTransaccion)
                            callTransa?.enqueue(object : Callback<PostTransaccion> {

                                override fun onResponse(call: Call<PostTransaccion>?, response: Response<PostTransaccion>?) {
                                    if (response != null && response.code() == 201 || response?.code() == 200) {
                                        transaccion.Id_Remote = response.body()?.Id!!
                                        transaccion.Estado_Sincronizacion = true
                                        transaccion.Estado_SincronizacionUpdate = true
                                        transaccion.save()

                                        val transaccionPost= SQLite.select()
                                                .from(Transaccion::class.java)
                                                .where(Transaccion_Table.Estado_Sincronizacion.eq(false))
                                                .and(Transaccion_Table.UsuarioId.eq(usuario?.Id))
                                                .orderBy(Transaccion_Table.TransaccionId,false).querySingle()

                                        if(transaccionPost!=null){
                                            syncDataTransacciones()
                                        }else{
                                            syncUpdateDataUnidadProductiva()
                                        }
                                    } else {
                                        postEventError(EventsService.ERROR_EVENT, "Error 500...")
                                    }
                                }
                                override fun onFailure(call: Call<PostTransaccion>?, t: Throwable?) {
                                    postEventError(EventsService.ERROR_EVENT, "Error Failure...")
                                }
                            })
                        }else{
                            syncDataTransacciones()
                        }
                        //postEventOk(RequestEventTransaccion.SAVE_EVENT, getProductions(cultivo_id), produccion)
                    } else {
                        postEventError(EventsService.ERROR_EVENT, "Error 500...")
                    }
                }
                override fun onFailure(call: Call<PostTercero>?, t: Throwable?) {
                    postEventError(EventsService.ERROR_EVENT, "Error Failure...")
                }
            })
        }else{
            syncUpdateDataUnidadProductiva()
        }
    }


    //endregion SYN
    fun getLastUserLogued(): Usuario? {
        val usuarioLogued = SQLite.select().from(Usuario::class.java).where(Usuario_Table.UsuarioRemembered.eq(true)).querySingle()
        return usuarioLogued
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
        val event = EventsService(type, listModel1, model, errorMessage)
        event.eventType = type
        eventBus?.post(event)
    }
    //endregion
}