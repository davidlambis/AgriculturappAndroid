package com.interedes.agriculturappv3.modules.main_menu.fragment.repository

import android.util.Base64
import android.util.Log
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.interedes.agriculturappv3.modules.models.usuario.Usuario
import com.interedes.agriculturappv3.events.RequestEvent
import com.interedes.agriculturappv3.libs.EventBus
import com.interedes.agriculturappv3.libs.GreenRobotEventBus
import com.interedes.agriculturappv3.modules.models.departments.DeparmentsResponse
import com.interedes.agriculturappv3.modules.models.departments.Ciudad
import com.interedes.agriculturappv3.modules.models.departments.Departamento
import com.interedes.agriculturappv3.modules.models.plagas.Enfermedad
import com.interedes.agriculturappv3.modules.models.plagas.EnfermedadResponseApi
import com.interedes.agriculturappv3.modules.models.producto.CalidadProducto
import com.interedes.agriculturappv3.modules.models.producto.CalidadProductoResponse
import com.interedes.agriculturappv3.modules.models.tipoproducto.TipoProducto
import com.interedes.agriculturappv3.modules.models.tipoproducto.TipoProductoResponse
import com.interedes.agriculturappv3.modules.models.unidad_medida.CategoriaMedida
import com.interedes.agriculturappv3.modules.models.unidad_medida.CategoriaMedidaResponse
import com.interedes.agriculturappv3.services.api.ApiInterface
import com.raizlabs.android.dbflow.data.Blob
import com.raizlabs.android.dbflow.kotlinextensions.save
import com.raizlabs.android.dbflow.sql.language.SQLite
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import com.google.gson.JsonParser
import com.google.gson.Gson
import com.interedes.agriculturappv3.modules.models.control_plaga.ControlPlaga
import com.interedes.agriculturappv3.modules.models.control_plaga.ControlPlaga_Table
import com.interedes.agriculturappv3.modules.models.cultivo.Cultivo
import com.interedes.agriculturappv3.modules.models.cultivo.Cultivo_Table
import com.interedes.agriculturappv3.modules.models.lote.Lote
import com.interedes.agriculturappv3.modules.models.lote.Lote_Table
import com.interedes.agriculturappv3.modules.models.produccion.Produccion
import com.interedes.agriculturappv3.modules.models.produccion.Produccion_Table
import com.interedes.agriculturappv3.modules.models.sincronizacion.GetSincronizacionResponse
import com.interedes.agriculturappv3.modules.models.sincronizacion.GetSincronizacionTransacciones
import com.interedes.agriculturappv3.modules.models.tratamiento.Tratamiento
import com.interedes.agriculturappv3.modules.models.tratamiento.TratamientoResponse
import com.interedes.agriculturappv3.modules.models.tratamiento.calificacion.Calificacion_Tratamiento
import com.interedes.agriculturappv3.modules.models.tratamiento.calificacion.Calificacion_Tratamiento_Table
import com.interedes.agriculturappv3.modules.models.unidad_medida.Unidad_Medida_Table
import com.interedes.agriculturappv3.modules.models.unidad_productiva.PostUnidadProductiva
import com.interedes.agriculturappv3.modules.models.unidad_productiva.Unidad_Productiva
import com.interedes.agriculturappv3.modules.models.unidad_productiva.Unidad_Productiva_Table
import com.interedes.agriculturappv3.modules.models.usuario.Usuario_Table
import com.interedes.agriculturappv3.modules.models.ventas.RequestApi.CategoriaPucResponse
import com.interedes.agriculturappv3.modules.models.ventas.CategoriaPuk
import com.interedes.agriculturappv3.modules.models.ventas.RequestApi.EstadoTransaccionResponse
import com.interedes.agriculturappv3.modules.models.ventas.Estado_Transaccion
import com.interedes.agriculturappv3.modules.models.ventas.Transaccion
import com.interedes.agriculturappv3.modules.models.ventas.Transaccion_Table
import com.interedes.agriculturappv3.services.listas.Listas


class MainMenuFragmentRepositoryImpl : MainMenuFragmentRepository {

    var eventBus: EventBus? = null
    var apiService: ApiInterface? = null
    //Firebase
    val mDatabase: DatabaseReference?
    var mUserDatabase: DatabaseReference? = null
    var mAuth: FirebaseAuth? = null
    var mUserReference: DatabaseReference? = null

    init {
        eventBus = GreenRobotEventBus()
        apiService = ApiInterface.create()
        mDatabase = FirebaseDatabase.getInstance().reference
        //mUserDatabase = mDatabase.child("Users")
        //mAuth = FirebaseAuth.getInstance()
        //mUserReference = mUserDatabase?.child(mAuth?.currentUser?.uid)
    }


    //region Métodos Interfaz

    override fun getListasIniciales() {


        var usuario= getLastUserLogued()
        val query = Listas.queryGeneral("UsuarioId",usuario?.Id.toString())
        val callInformacionSinronized = apiService?.getSyncInformacionUsuario(query)
        callInformacionSinronized?.enqueue(object : Callback<GetSincronizacionResponse> {
            override fun onResponse(call: Call<GetSincronizacionResponse>?, response: Response<GetSincronizacionResponse>?) {
                if (response != null && response.code() == 200) {
                    val unidadesProductivas = response.body()?.value as MutableList<Unidad_Productiva>


                    //TODO Delete information in local, add new remote
                    SQLite.delete<Unidad_Productiva>(Unidad_Productiva::class.java)
                            .where(Unidad_Productiva_Table.Estado_Sincronizacion.eq(true))
                            .async()
                            .execute()


                    SQLite.delete<Lote>(Lote::class.java)
                            .where(Lote_Table.EstadoSincronizacion.eq(true))
                            .async()
                            .execute()


                    SQLite.delete<Cultivo>(Cultivo::class.java)
                            .where(Cultivo_Table.EstadoSincronizacion.eq(true))
                            .async()
                            .execute()

                    SQLite.delete<Produccion>(Produccion::class.java)
                            .where(Produccion_Table.Estado_Sincronizacion.eq(true))
                            .async()
                            .execute()

                    SQLite.delete<ControlPlaga>(ControlPlaga::class.java)
                            .where(ControlPlaga_Table.Estado_Sincronizacion.eq(true))
                            .async()
                            .execute()



                    //TODO Add information new remote
                    for (item in unidadesProductivas) {

                         item.UsuarioId=usuario?.Id
                         item.Nombre_Ciudad= if (item.Ciudad!=null) item.Ciudad?.Nombre else null
                         item.Nombre_Unidad_Medida= if (item.UnidadMedida!=null) item.UnidadMedida?.Descripcion else null
                         item.Nombre_Departamento= if (item.Ciudad!=null) item.Ciudad?.Departamento?.Nombre else null
                         item.Estado_Sincronizacion=true
                        item.Estado_SincronizacionUpdate=true

                        if(item.LocalizacionUps?.size!!>0){
                            for (localizacion in item.LocalizacionUps!!){
                                item.DireccionAproximadaGps=localizacion.DireccionAproximadaGps
                                item.Latitud=localizacion.Latitud
                                item.Longitud=localizacion.Longitud
                                item.Coordenadas=localizacion.Coordenadas
                                item.Direccion=localizacion.Direccion
                                item.Configuration_Point=true
                                item.Configuration_Poligon=false
                                item.LocalizacionUpId=localizacion.Id
                            }
                        }

                        if(item.Lotes?.size!!>0){
                            for (lote in item.Lotes!!){
                                val coordenadas =lote.Localizacion
                                if(coordenadas!=null || coordenadas!=""){
                                    val separated = coordenadas?.split("/".toRegex())?.dropLastWhile { it.isEmpty() }?.toTypedArray()
                                    var latitud= separated!![0].toDoubleOrNull() // this will contain "Fruit"
                                    var longitud=separated!![1].toDoubleOrNull() // this will contain " they taste good"
                                    lote.Latitud=latitud
                                    lote.Longitud=longitud
                                    lote.Coordenadas=coordenadas
                                }

                                lote.Nombre_Unidad_Medida= if (item.UnidadMedida!=null) item.UnidadMedida?.Descripcion else null
                                lote.Nombre_Unidad_Productiva= item.nombre
                                lote.EstadoSincronizacion=true
                                lote.Nombre= if (lote.Nombre==null) "" else lote.Nombre
                                lote.Descripcion= if (lote.Descripcion==null) "" else lote.Descripcion
                                lote.Estado_SincronizacionUpdate=true

                                lote.save()

                                if(lote.Cultivos?.size!!>0){
                                    for(cultivo in lote.Cultivos!!){
                                        cultivo.NombreUnidadProductiva= item.nombre
                                        cultivo.NombreLote= lote.Nombre
                                        cultivo.EstadoSincronizacion= true
                                        cultivo.Estado_SincronizacionUpdate= true

                                        cultivo.Nombre_Detalle_Tipo_Producto= if (cultivo.detalleTipoProducto!=null) cultivo.detalleTipoProducto?.Descripcion else null
                                        cultivo.Id_Tipo_Producto= if (cultivo.detalleTipoProducto!=null) cultivo.detalleTipoProducto?.TipoProductoId else null
                                        cultivo.Nombre_Unidad_Medida=if (cultivo.unidadMedida!=null) cultivo.unidadMedida?.Descripcion else null
                                        cultivo.save()


                                        if(cultivo.produccions?.size!!>0){
                                            for(produccion in cultivo.produccions!!){
                                                produccion.Estado_Sincronizacion=true
                                                produccion.Estado_SincronizacionUpdate=true
                                                produccion.NombreCultivo=cultivo.Nombre
                                                produccion.NombreLote=lote.Nombre
                                                produccion.NombreUnidadProductiva=item.nombre
                                                produccion.FechaInicioProduccion=produccion.getFechaDate(produccion.StringFechaInicio)
                                                produccion.FechaFinProduccion=produccion.getFechaDate(produccion.StringFechaFin)
                                                produccion.NombreUnidadMedida= if (produccion.unidadMedida!=null) produccion.unidadMedida?.Descripcion else null
                                                produccion.save()


                                                if(cultivo.controlPlagas?.size!!>0){
                                                    for(controlplaga in cultivo.controlPlagas!!){
                                                        controlplaga.Estado_Sincronizacion=true
                                                        controlplaga.Estado_SincronizacionUpdate=true
                                                        controlplaga.Fecha_aplicacion_local=controlplaga.getFechaDate(controlplaga.Fecha_aplicacion)
                                                        controlplaga.Fecha_Erradicacion_Local=if(controlplaga.FechaErradicacion!=null)controlplaga.getFechaDate(controlplaga.FechaErradicacion)else null

                                                        controlplaga.save()

                                                    }
                                                }
                                            }
                                        }
                                    }
                                }
                            }
                        }

                        item.save()
                    }

                    loadTransacciones(usuario)

                } else {
                    postEvent(RequestEvent.ERROR_EVENT, "Comprueba tu conexión a Internet")
                }
            }
            override fun onFailure(call: Call<GetSincronizacionResponse>?, t: Throwable?) {
                postEvent(RequestEvent.ERROR_EVENT, "Comprueba tu conexión a Internet")
            }
        })





        //Tipos de Producto
        //val listTipoProducto: ArrayList<TipoProducto> = Listas.listaTipoProducto()
        val callTipoProducto = apiService?.getTipoAndDetalleTipoProducto()
        callTipoProducto?.enqueue(object : Callback<TipoProductoResponse> {
            override fun onResponse(call: Call<TipoProductoResponse>?, response: Response<TipoProductoResponse>?) {
                if (response != null && response.code() == 200) {
                    val tiposProducto = response.body()?.value as MutableList<TipoProducto>
                    for (item in tiposProducto) {

                        try {
                            val base64String = item?.Icono
                            val base64Image = base64String?.split(",".toRegex())?.dropLastWhile { it.isEmpty() }!!.toTypedArray()[1]
                            val byte = Base64.decode(base64Image, Base64.DEFAULT)
                            item.Imagen = Blob(byte)

                        }catch (ex:Exception){
                            var ss= ex.toString()
                            Log.d("Convert Image", "defaultValue = " + ss);
                        }

                        item.save()
                        for (detalleTipoProducto in item.DetalleTipoProductos!!) {
                            detalleTipoProducto.save()
                        }
                    }
                } else {
                    postEvent(RequestEvent.ERROR_EVENT, "Comprueba tu conexión a Internet")
                }
            }
            override fun onFailure(call: Call<TipoProductoResponse>?, t: Throwable?) {
                postEvent(RequestEvent.ERROR_EVENT, "Comprueba tu conexión a Internet")
            }
        })

        //Enfermedades
        val callEnfermedades = apiService?.getEnfermedades()
        callEnfermedades?.enqueue(object : Callback<EnfermedadResponseApi> {
            override fun onResponse(call: Call<EnfermedadResponseApi>?, enfermedadResponse: Response<EnfermedadResponseApi>?) {
                if (enfermedadResponse != null && enfermedadResponse.code() == 200) {
                    val enfermedades = enfermedadResponse.body()?.value as MutableList<Enfermedad>
                    for (item in enfermedades){
                        item.NombreTipoEnfermedad=item.TipoEnfermedad?.Nombre
                        item.NombreTipoProducto=item.TipoProducto?.Nombre
                        item.NombreCientificoTipoEnfermedad=item.TipoEnfermedad?.NombreCientifico
                        item.DescripcionTipoEnfermedad=item.TipoEnfermedad?.Descripcion

                        item.save()
                       if(item.Fotos!=null){
                           for (itemFoto in item?.Fotos!!){

                               try {
                                   val base64String = itemFoto?.Ruta
                                   val base64Image = base64String?.split(",".toRegex())?.dropLastWhile { it.isEmpty() }!!.toTypedArray()[1]
                                   val byte = Base64.decode(base64Image, Base64.DEFAULT)
                                   itemFoto.blobImagen = Blob(byte)

                               }catch (ex:Exception){
                                   var ss= ex.toString()
                                   Log.d("Convert Image", "defaultValue = " + ss);
                               }

                               itemFoto.save()
                           }
                       }
                    }
                } else {
                    postEvent(RequestEvent.ERROR_EVENT, "Comprueba tu conexión a Internet")
                }
            }
            override fun onFailure(call: Call<EnfermedadResponseApi>?, t: Throwable?) {
                postEvent(RequestEvent.ERROR_EVENT, "Comprueba tu conexión a Internet")
            }
        })


        //Tratamientos, Calificaciones e Insumos
        val callTrtamientos = apiService?.getTratamientos()
        callTrtamientos?.enqueue(object : Callback<TratamientoResponse> {
            override fun onResponse(call: Call<TratamientoResponse>?, tratamientoResponse: Response<TratamientoResponse>?) {
                if (tratamientoResponse != null && tratamientoResponse.code() == 200) {
                    val tratamientos = tratamientoResponse.body()?.value as MutableList<Tratamiento>

                    //Update Informacion




                    for (item in tratamientos){

                        var sumacalificacion:Double?=0.0
                        var promedioCalificacion:Double?=0.0

                        if(item.Insumo!=null){


                            item.Descripcion_Insumo=item.Insumo?.Descripcion
                            item.Nombre_Insumo=item.Insumo?.Nombre

                            if(item?.Insumo?.Imagen!=null || item?.Insumo?.Imagen!=""){
                                try {
                                    val base64String = item?.Insumo?.Imagen
                                    val base64Image = base64String?.split(",".toRegex())?.dropLastWhile { it.isEmpty() }!!.toTypedArray()[1]
                                    val byte = Base64.decode(base64Image, Base64.DEFAULT)
                                    item.Insumo?.blobImagen = Blob(byte)
                                }catch (ex:Exception){
                                    var ss= ex.toString()
                                    Log.d("Convert Image", "defaultValue = " + ss);
                                }
                            }

                            if(item.Insumo?.Laboratorio!=null){
                                item.Insumo?.NombreLaboratorio=item.Insumo?.Laboratorio?.Nombre
                                item.Insumo?.Laboratorio?.save()
                            }

                            if(item.Insumo?.TipoInsumo!=null){
                                item.Insumo?.NombreTipoInsumo=item.Insumo?.TipoInsumo?.Nombre
                                item.Insumo?.TipoInsumo?.save()
                            }

                            item.Insumo?.save()
                        }


                        if(item?.Calificacions!!.size==0){
                            SQLite.delete<Calificacion_Tratamiento>(Calificacion_Tratamiento::class.java)
                                    .where(Calificacion_Tratamiento_Table.TratamientoId.eq(item.Id))
                                    .async()
                                    .execute()
                        }else{
                            SQLite.delete<Calificacion_Tratamiento>(Calificacion_Tratamiento::class.java)
                                    .where(Calificacion_Tratamiento_Table.TratamientoId.eq(item.Id))
                                    .async()
                                    .execute()

                            for (calification in item?.Calificacions!!){
                                calification.save()
                                sumacalificacion=sumacalificacion!!+ calification.Valor!!
                            }
                            promedioCalificacion= sumacalificacion!! /item?.Calificacions!!.size
                        }

                        item.CalificacionPromedio=promedioCalificacion
                        item.save()
                    }
                } else {
                    postEvent(RequestEvent.ERROR_EVENT, "Comprueba tu conexión a Internet")
                }
            }
            override fun onFailure(call: Call<TratamientoResponse>?, t: Throwable?) {
                postEvent(RequestEvent.ERROR_EVENT, "Comprueba tu conexión a Internet")
            }
        })


        //Departamentos y Ciudades
        val lista_departamentos = SQLite.select().from(Departamento::class.java).queryList()
        val lista_ciudades = SQLite.select().from(Ciudad::class.java).queryList()
        if (lista_departamentos.size <= 0 || lista_ciudades.size <= 0) {
            val call = apiService?.getDepartamentos()
            call?.enqueue(object : Callback<DeparmentsResponse> {
                override fun onResponse(call: Call<DeparmentsResponse>?, response: Response<DeparmentsResponse>?) {
                    if (response != null && response.code() == 200) {
                        val departamentos: MutableList<Departamento> = response.body()?.value!!
                        for (item in departamentos) {
                            item.save()
                            for (ciudad in item.ciudades!!) {
                                ciudad.save()
                            }
                        }
                    } else {
                        postEvent(RequestEvent.ERROR_EVENT, "Comprueba tu conexión a Internet")
                    }
                }
                override fun onFailure(call: Call<DeparmentsResponse>?, t: Throwable?) {
                    postEvent(RequestEvent.ERROR_EVENT, "Comprueba tu conexión a Internet")
                }
            })
        }

        //Categorias Puk
        val categoriaspuk = apiService?.getCategoriasPuc()
        categoriaspuk?.enqueue(object : Callback<CategoriaPucResponse> {
            override fun onResponse(call: Call<CategoriaPucResponse>?, response: Response<CategoriaPucResponse>?) {
                if (response != null && response.code() == 200) {
                    val categorias: MutableList<CategoriaPuk> = response.body()?.value!!
                    for (item in categorias) {
                        item.save()
                        for (puk in item.Pucs!!) {
                            puk.save()
                        }
                    }
                } else {
                    postEvent(RequestEvent.ERROR_EVENT, "Comprueba tu conexión a Internet")
                }
            }
            override fun onFailure(call: Call<CategoriaPucResponse>?, t: Throwable?) {
                postEvent(RequestEvent.ERROR_EVENT, "Comprueba tu conexión a Internet")
            }
        })

        //Estados de  transaccion
        val estadosTransaccion = apiService?.getEstadosTransaccion()
        estadosTransaccion?.enqueue(object : Callback<EstadoTransaccionResponse> {
            override fun onResponse(call: Call<EstadoTransaccionResponse>?, response: Response<EstadoTransaccionResponse>?) {
                if (response != null && response.code() == 200) {
                    val estadostransaccion: MutableList<Estado_Transaccion> = response.body()?.value!!
                    for (item in estadostransaccion) {
                        item.save()
                    }
                } else {
                    postEvent(RequestEvent.ERROR_EVENT, "Comprueba tu conexión a Internet")
                }
            }
            override fun onFailure(call: Call<EstadoTransaccionResponse>?, t: Throwable?) {
                postEvent(RequestEvent.ERROR_EVENT, "Comprueba tu conexión a Internet")
            }
        })


        //Categorías Medida
        val callCategoriaMedida = apiService?.getCategoriasMedida()
        callCategoriaMedida?.enqueue(object : Callback<CategoriaMedidaResponse> {
            override fun onResponse(call: Call<CategoriaMedidaResponse>?, response: Response<CategoriaMedidaResponse>?) {
                if (response != null && response.code() == 200) {
                    val categoriasMedida = response.body()?.value as MutableList<CategoriaMedida>
                    for (item in categoriasMedida) {
                        item.save()
                        for (itemUnidadmedida in item?.UnidadMedidas!!){
                            itemUnidadmedida.save()
                        }
                    }
                } else {
                    postEvent(RequestEvent.ERROR_EVENT, "Comprueba tu conexión a Internet")
                }
            }
            override fun onFailure(call: Call<CategoriaMedidaResponse>?, t: Throwable?) {
                postEvent(RequestEvent.ERROR_EVENT, "Comprueba tu conexión a Internet")
            }
        })



        //Calidades de Producto
        val callCalidadProducto = apiService?.getCalidadesProducto()
        callCalidadProducto?.enqueue(object : Callback<CalidadProductoResponse> {
            override fun onResponse(call: Call<CalidadProductoResponse>?, response: Response<CalidadProductoResponse>?) {
                if (response != null && response.code() == 200) {
                    val calidadesProducto = response.body()?.value as MutableList<CalidadProducto>
                    for (item in calidadesProducto) {
                        item.save()
                    }
                } else {
                    postEvent(RequestEvent.ERROR_EVENT, "Comprueba tu conexión a Internet")
                }
            }

            override fun onFailure(call: Call<CalidadProductoResponse>?, t: Throwable?) {
                postEvent(RequestEvent.ERROR_EVENT, "Comprueba tu conexión a Internet")
            }

        })


        //Categorías de Producto
        //getLastUserLogued



        //
    }

     fun loadTransacciones(usuario: Usuario?) {
         //Sinc transacciones
         val queryTransacciones = Listas.queryGeneral("userId",usuario?.Id.toString())
         val callInformacionTransaccionesSinronized = apiService?.getSyncInformacionUsuarioTransacciones(queryTransacciones)
         callInformacionTransaccionesSinronized?.enqueue(object : Callback<GetSincronizacionTransacciones> {
             override fun onResponse(call: Call<GetSincronizacionTransacciones>?, response: Response<GetSincronizacionTransacciones>?) {
                 if (response != null && response.code() == 200) {
                     val transacciones = response.body()?.value as MutableList<Transaccion>

                     SQLite.delete<Transaccion>(Transaccion::class.java)
                             .where(Transaccion_Table.Estado_Sincronizacion.eq(true))
                             .async()
                             .execute()


                     for (item in transacciones) {

                         item.UsuarioId=usuario?.Id
                         item.Nombre_Tercero= if (item.Tercero!=null) item.Tercero?.Nombre else null
                         item.Nombre_Estado_Transaccion= if (item.EstadoTransaccion!=null) item.EstadoTransaccion?.Nombre else null
                         item.Estado_Sincronizacion=true
                         item.Identificacion_Tercero= if (item.Tercero!=null) item.Tercero?.NitRut else null
                         item.Descripcion_Puk=if (item.Puc!=null) item.Puc?.Descripcion else null
                         item.CategoriaPuk_Id=if (item.Puc!=null) item.Puc?.CategoriaId else null
                         item.Estado_Sincronizacion=true
                         item.Estado_SincronizacionUpdate=true
                         var fechaDate=item.getFechaDate(item.FechaString)
                         item.Fecha_Transaccion=fechaDate



                         item.Valor_Unitario=if (item.Valor_Total!=null && item.Cantidad!=null)  item.Valor_Total!! / item.Cantidad?.toLong()!! else null

                         var cultivo =SQLite.select().from(Cultivo::class.java).where(Cultivo_Table.Id.eq(item.Cultivo_Id)).querySingle()
                         if(cultivo!=null){
                             item.Nombre_Cultivo=cultivo.Nombre
                             item.Nombre_Detalle_Producto_Cultivo=cultivo.Nombre_Detalle_Tipo_Producto
                         }

                         item.save()
                     }
                 } else {
                     postEvent(RequestEvent.ERROR_EVENT, "Comprueba tu conexión a Internet")
                 }
             }
             override fun onFailure(call: Call<GetSincronizacionTransacciones>?, t: Throwable?) {
                 postEvent(RequestEvent.ERROR_EVENT, "Comprueba tu conexión a Internet")
             }
         })



    }



    fun getLastUserLogued(): Usuario? {
        val usuarioLogued = SQLite.select().from(Usuario::class.java).where(Usuario_Table.UsuarioRemembered.eq(true)).querySingle()
        return usuarioLogued
    }

    fun <T> getObjectList(jsonString: String, cls: Class<T>): List<T> {
        val list = ArrayList<T>()
        try {
            val gson = Gson()
            val arry = JsonParser().parse(jsonString).asJsonArray
            for (jsonElement in arry) {
                list.add(gson.fromJson(jsonElement, cls))
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }

        return list
    }

    override fun logOut(usuario: Usuario?) {
        try {
            mAuth = FirebaseAuth.getInstance()
            if (mAuth?.currentUser != null) {
                mAuth?.signOut()
            }
            usuario?.UsuarioRemembered = false
            usuario?.AccessToken = null
            usuario?.save()
            postEvent(RequestEvent.UPDATE_EVENT)
        } catch (e: Exception) {
            postEvent(RequestEvent.ERROR_EVENT, e.message.toString())
        }

    }

    override fun offlineLogOut(usuario: Usuario?) {
        try {
            usuario?.UsuarioRemembered = false
            usuario?.AccessToken = null
            usuario?.save()
            postEvent(RequestEvent.UPDATE_EVENT)
        } catch (e: Exception) {
            postEvent(RequestEvent.ERROR_EVENT, e.message.toString())
        }
    }
    //endregion


    //region Events
    private fun postEvent(type: Int) {
        postEvent(type, null)
    }

    private fun postEvent(type: Int, errorMessage: String?) {
        val event = RequestEvent(type, null, null, errorMessage)
        event.eventType = type
        event.mensajeError = errorMessage
        eventBus?.post(event)
    }
    //endregion

}