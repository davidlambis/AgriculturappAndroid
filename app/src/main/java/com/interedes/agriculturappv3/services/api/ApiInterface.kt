package com.interedes.agriculturappv3.services.api

import com.interedes.agriculturappv3.modules.models.departments.DeparmentsResponse
import com.interedes.agriculturappv3.modules.models.ResetPassword
import com.interedes.agriculturappv3.modules.models.cultivo.Cultivo
import com.interedes.agriculturappv3.modules.models.cultivo.PostCultivo
import com.interedes.agriculturappv3.modules.models.lote.Lote
import com.interedes.agriculturappv3.modules.models.unidad_productiva.Unidad_Productiva
import com.interedes.agriculturappv3.modules.models.detalle_metodo_pago.DetalleMetodoPagoResponse
import com.interedes.agriculturappv3.modules.models.detalletipoproducto.DetalleTipoProductoResponse
import com.interedes.agriculturappv3.modules.models.login.Login
import com.interedes.agriculturappv3.modules.models.login.LoginResponse
import com.interedes.agriculturappv3.modules.models.lote.PostLote
import com.interedes.agriculturappv3.modules.models.rol.RolResponse
import com.interedes.agriculturappv3.modules.models.metodopago.MetodoPagoResponse
import com.interedes.agriculturappv3.modules.models.produccion.PostProduccion
import com.interedes.agriculturappv3.modules.models.rol.AspNetRolResponse
import com.interedes.agriculturappv3.modules.models.tipoproducto.TipoProductoResponse
import com.interedes.agriculturappv3.modules.models.unidad_medida.CategoriaMedidaResponse
import com.interedes.agriculturappv3.modules.models.unidad_medida.UnidadMedidaResponse
import com.interedes.agriculturappv3.modules.models.unidad_productiva.PostUnidadProductiva
import com.interedes.agriculturappv3.modules.models.usuario.*
import okhttp3.OkHttpClient
import retrofit2.Call
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit
import com.google.gson.Gson
import com.google.gson.GsonBuilder
import com.interedes.agriculturappv3.modules.models.Notification.PostNotification
import com.interedes.agriculturappv3.modules.models.Notification.ResponsePostNotification
import com.interedes.agriculturappv3.modules.models.control_plaga.PostControlPlaga
import com.interedes.agriculturappv3.modules.models.ofertas.*
import com.interedes.agriculturappv3.modules.models.plagas.EnfermedadResponseApi
import com.interedes.agriculturappv3.modules.models.producto.*
import com.interedes.agriculturappv3.modules.models.rol.RolUserLogued
import com.interedes.agriculturappv3.modules.models.sincronizacion.GetSincronizacionResponse
import com.interedes.agriculturappv3.modules.models.sincronizacion.GetSincronizacionTransacciones
import com.interedes.agriculturappv3.modules.models.sincronizacion.GetSynProductosUserResponse
import com.interedes.agriculturappv3.modules.models.tratamiento.TratamientoResponse
import com.interedes.agriculturappv3.modules.models.tratamiento.calificacion.Calificacion_Tratamiento
import com.interedes.agriculturappv3.modules.models.tratamiento.calificacion.PostCalificacion
import com.interedes.agriculturappv3.modules.models.tratamiento.calificacion.ResponseCalificacion
import com.interedes.agriculturappv3.modules.models.unidad_productiva.localizacion.LocalizacionUp
import com.interedes.agriculturappv3.modules.models.ventas.RequestApi.CategoriaPucResponse
import com.interedes.agriculturappv3.modules.models.ventas.RequestApi.EstadoTransaccionResponse
import com.interedes.agriculturappv3.modules.models.ventas.RequestApi.PostTercero
import com.interedes.agriculturappv3.modules.models.ventas.RequestApi.PostTransaccion
import retrofit2.Retrofit
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory
import retrofit2.http.*
import java.util.*
import okhttp3.ResponseBody
import retrofit2.http.POST
import retrofit2.http.GET


import io.reactivex.Observable;



interface ApiInterface {

    //region Peticiones







    //Get Roles
    @GET("odata/Agp2/Rols?\$select=Nombre,Id")
    fun getRoles(): Call<RolResponse>

    //Get Métodos Pago
    @GET("odata/Agp2/MetodoPagos")
    fun getMetodoPagos(): Call<MetodoPagoResponse>

    //Get Detalle Métodos Pago
    @GET("odata/Agp2/DetalleMetodopagos")
    fun getDetalleMetodoPagos(): Call<DetalleMetodoPagoResponse>

    //Get Tipo Productos
    @GET("odata/Agp2/TipoProductos?\$expand=DetalleTipoProductos")
    fun getTipoAndDetalleTipoProducto(): Call<TipoProductoResponse>


    @GET("odata/Agp2/DetalleTipoProductos")
    fun getDetalleTiposProducto(): Call<DetalleTipoProductoResponse>

    //Post Registro Usuarios
    @Headers("Content-Type: application/json")
    @POST("auth/register")
    fun postRegistroUsers(@Body body: User): Call<UserResponse>

    //TODO Requiere Token
    @Headers("Content-Type: application/json")
    @PATCH("odata/Agp2/Usuarios('{Id}')")
    fun updateUsuario(@Body body: PostUsuario, @Path("Id") Id: UUID): Call<PostUsuario>


    //Get Usuarios
    @GET("odata/agriculturebd/Usuarios")
    fun getUsuarios(): Call<UsuarioResponse>

    //Reset Password
    @Headers("Content-Type: application/json")
    @POST("auth/reset-password")
    fun resetPassword(@Body body: ResetPassword): Call<ResetPassword>

    //Post Login
    @Headers("Content-Type: application/json")
    @POST("auth/login")
    fun postLogin(@Body body: Login): Call<LoginResponse>

    //Get Usuario por Correo
    @GET("odata/Agp2/Usuarios")
    fun getUsuarioByCorreo(@Query("\$filter") filter: String): Call<UsuarioResponse>

    @GET("odata/Agp2/Rols('{Id}')")
    fun getRolUsuarioLogued( @Path("Id")  filter: String): Call<RolUserLogued>




    @GET("odata/Agp2/Usuarios('{Id}')?\$expand=Rol(\$select=Nombre,Id)")
    fun getUsuarioLogued( @Path("Id")  filter: String): Call<Usuario>



    @GET("auth/ApplicationUsers")
    fun getAuthUserByCorreo(@Header("Authorization") token: String, @Query("\$filter") filter: String): Call<GetUserResponse>

    //Get en AspNetRoles por Tipo User
    @GET("auth/ApplicationRoles")
    fun getAspNetRolesByTipoUser(@Header("Authorization") token: String, @Query("\$filter") filter: String): Call<AspNetRolResponse>

    //Get Categorías Medida
    @GET("odata/Agp2/CategoriaMedidas?\$expand=UnidadMedidas")
    fun getCategoriasMedida(): Call<CategoriaMedidaResponse>

    //Get Unidades Medida
    @GET("odata/Agp2/UnidadMedidas")
    fun getUnidadesMedida(): Call<UnidadMedidaResponse>

    //Get Calidades Producto
    @GET("odata/Agp2/Calidads")
    fun getCalidadesProducto(): Call<CalidadProductoResponse>

    //region UNIDADES PRODUCTIVAS
    //Post Unidad Productiva
    //TODO Requiere Token
    @Headers("Content-Type: application/json")
    @POST("odata/Agp2/UnidadProductivas")
    fun postUnidadProductiva(@Body body: PostUnidadProductiva): Call<Unidad_Productiva>


    @Headers("Content-Type: application/json")
    @POST("odata/Agp2/LocalizacionUps")
    fun postLocalizacionUnidadProductiva(@Body body: LocalizacionUp): Call<LocalizacionUp>


    //Patch Unidad Productiva
    //TODO Requiere Token
    @Headers("Content-Type: application/json")
    @PATCH("odata/Agp2/UnidadProductivas({Id})")
    fun updateUnidadProductiva(@Body body: PostUnidadProductiva, @Path("Id") Id: Long): Call<Unidad_Productiva>

    @Headers("Content-Type: application/json")
    @PATCH("odata/Agp2/LocalizacionUps({Id})")
    fun updateLocalizacionUnidadProductiva(@Body body: LocalizacionUp, @Path("Id") Id: Long): Call<LocalizacionUp>

    @GET("odata/Agp2/Departamentos?\$expand=Ciudads")
    fun getDepartamentos(): Call<DeparmentsResponse>



    @Headers("Content-Type: application/json")
    @DELETE("odata/Agp2/UnidadProductivas({Id})")
    fun deleteUnidadProductiva(@Path("Id") Id: Long?): Call<Unidad_Productiva>

    //endregion

    //region LOTES
    //Post Lotes
    //TODO Requiere Token
    @Headers("Content-Type: application/json")
    @POST("odata/Agp2/Lotes")
    fun postLote(@Body body: PostLote): Call<Lote>

    //Patch Lote
    //TODO Requiere Token
    @Headers("Content-Type: application/json")
    @PATCH("odata/Agp2/Lotes({Id})")
    fun updateLote(@Body body: PostLote, @Path("Id") Id: Long): Call<Lote>

    //Delete Lote
    //TODO Requiere Token
    @Headers("Content-Type: application/json")
    @DELETE("odata/Agp2/Lotes({Id})")
    fun deleteLote(@Path("Id") Id: Long): Call<Lote>
    //endregion

    //region Cultivos
    //TODO Requiere Token
    @Headers("Content-Type: application/json")
    @POST("odata/Agp2/Cultivos")
    fun postCultivo(@Body body: PostCultivo): Call<Cultivo>

    //TODO Requiere Token
    @Headers("Content-Type: application/json")
    @PATCH("odata/Agp2/Cultivos({Id})")
    fun updateCultivo(@Body body: PostCultivo, @Path("Id") Id: Long): Call<Cultivo>

    //TODO Requiere Token
    @Headers("Content-Type: application/json")
    @DELETE("odata/Agp2/Cultivos({Id})")
    fun deleteCultivo(@Path("Id") Id: Long): Call<Cultivo>



    //endregion


    //region Produccion
    //TODO Requiere Token
    @Headers("Content-Type: application/json")
    @POST("odata/Agp2/Produccions")
    fun postProduccion(@Body body: PostProduccion): Call<PostProduccion>
    //TODO Requiere Token
    @Headers("Content-Type: application/json")
    @PATCH("odata/Agp2/Produccions({Id})")
    fun updateProduccion(@Body body: PostProduccion, @Path("Id") Id: Long): Call<PostProduccion>

    //TODO Requiere Token
    @Headers("Content-Type: application/json")
    @DELETE("odata/Agp2/Produccions({Id})")
    fun deleteProduccion(@Path("Id") Id: Long): Call<PostProduccion>
    //endregion


    //region Productos
    //TODO Requiere Token
    @Headers("Content-Type: application/json")
    @POST("odata/Agp2/Productos")
    fun postProducto(@Body body: PostProducto): Call<Producto>

    //TODO Requiere Token
    @Headers("Content-Type: application/json")
    @PATCH("odata/Agp2/Productos({Id})")
    fun updateProducto(@Body body: PostProducto, @Path("Id") Id: Long): Call<Producto>

    //TODO Requiere Token
    @Headers("Content-Type: application/json")
    @DELETE("odata/Agp2/Productos({Id})")
    fun deleteProducto(@Path("Id") Id: Long): Call<Producto>
    //endregion


    //region Plagas y Enfermedades
    @GET("odata/Agp2/Enfermedades?\$expand=TipoEnfermedad,TipoProducto(\$select=Nombre),Fotos")
    fun getEnfermedades(): Call<EnfermedadResponseApi>

   // @GET("odata/Agp2/Tratamientos?\$expand=Insumo(\$expand=Laboratorio,TipoInsumo),Calificacions")
    @GET("odata/Agp2/Tratamientos?\$expand=Insumo(\$expand=Laboratorio,TipoInsumo),Calificacions")
    fun getTratamientos(): Call<TratamientoResponse>
    //endregion

    //region Control Plagas
    //TODO Requiere Token
    @Headers("Content-Type: application/json")
    @POST("odata/Agp2/ControlPlagas")
    fun postControlPlaga(@Body body: PostControlPlaga): Call<PostControlPlaga>

    //TODO Requiere Token
    @Headers("Content-Type: application/json")
    @PATCH("odata/Agp2/ControlPlagas({Id})")
    fun updateControlPlaga(@Body body: PostControlPlaga, @Path("Id") Id: Long): Call<PostControlPlaga>


    //TODO Requiere Token
    @Headers("Content-Type: application/json")
    @DELETE("odata/Agp2/ControlPlagas({Id})")
    fun deleteControlPlaga(@Path("Id") Id: Long): Call<PostControlPlaga>

    //endregion



    //region Contabilidad
    @GET("odata/Agp2/CategoriaPucs?\$expand=Pucs")
    fun getCategoriasPuc(): Call<CategoriaPucResponse>


    @GET("odata/Agp2/Estados")
    fun getEstadosTransaccion(): Call<EstadoTransaccionResponse>


    //TODO Requiere Token
    @Headers("Content-Type: application/json")
    @POST("odata/Agp2/Transaccions")
    fun postTransaccion(@Body body: PostTransaccion): Call<PostTransaccion>

    //TODO Requiere Token
    @Headers("Content-Type: application/json")
    @POST("odata/Agp2/Terceros")
    fun postTercero(@Body body: PostTercero): Call<PostTercero>

    //TODO Requiere Token
    @Headers("Content-Type: application/json")
    @PATCH("odata/Agp2/Terceros({Id})")
    fun updateTercero(@Body body: PostTercero, @Path("Id") Id: Long): Call<PostTercero>


    //TODO Requiere Token
    @Headers("Content-Type: application/json")
    @PATCH("odata/Agp2/Transaccions({Id})")
    fun updateTransaccion(@Body body: PostTransaccion, @Path("Id") Id: Long): Call<PostTransaccion>

    //TODO Requiere Token
    @Headers("Content-Type: application/json")
    @DELETE("odata/Agp2/Transaccions({Id})")
    fun deletetransaccion(@Path("Id") Id: Long): Call<PostTransaccion>


    //TODO Requiere Token
    @Headers("Content-Type: application/json")
    @DELETE("odata/Agp2/Terceros({Id})")
    fun deleteTercero(@Path("Id") Id: Long): Call<PostTercero>
    //endregion


    //region Calificacion
    //TODO Requiere Token
    @Headers("Content-Type: application/json")
    @POST("odata/Agp2/Calificacions")
    fun postCalificacionTratamiento(@Body body: PostCalificacion): Call<Calificacion_Tratamiento>
    //fun postCalificacionTratamiento( @Query("\$filter") filter: String): Call<GetUserResponse>


   // @GET("odata/Agp2/Calificacions?&\$top=1")
    @GET("odata/Agp2/Calificacions")
    fun getVerificateCalificationUser( @Query("\$filter") filter: String): Call<ResponseCalificacion>

    //http://18.233.87.16/odata/Agp2/Calificacions?$filter=TratamientoId eq 3 and userId eq '0bdb5fcd-79cd-458d-bc24-59074f7b7aab'


    //endregion


    //region SINCRONIZE INFORMACION USUARIO LOGUED
    @GET("odata/Agp2/UnidadProductivas?\$expand=LocalizacionUps,UnidadMedida,Ciudad(\$expand=Departamento),Lotes(\$expand=UnidadMedida,Cultivos(\$expand=UnidadMedida,ControlPlagas,Produccions(\$expand=UnidadMedida),DetalleTipoProducto(\$expand=TipoProducto(\$select=Nombre))))")
    fun getSyncInformacionUsuario( @Query("\$filter") filter: String): Call<GetSincronizacionResponse>


    @GET("odata/Agp2/Productos?\$expand=UnidadMedida,Calidad,Cultivo(\$expand=DetalleTipoProducto,Lote(\$expand=UnidadProductiva))")
    fun getSyncProductos( @Query("\$filter") filter: String): Call<GetSynProductosUserResponse>


    @GET("odata/Agp2/Transaccions?\$expand=Estado,Tercero,Puc(\$expand=CategoriaPuc)")
    fun getSyncInformacionUsuarioTransacciones( @Query("\$filter") filter: String): Call<GetSincronizacionTransacciones>

    // http://18.233.87.16/odata/Agp2/UnidadProductivas?$expand=Lotes($expand=Cultivos)&$filter=UsuarioId eq '0bdb5fcd-79cd-458d-bc24-59074f7b7aab'
    //endregion


    //region COMPRADOR
    //@GET("odata/Agp2/DetalleTipoProductos?\$expand=Cultivos(\$expand=Productos(\$expand=UnidadMedida,Calidad),Lote(\$expand=UnidadProductiva(\$expand=LocalizacionUps,Ciudad(\$expand=Departamento),Usuario))),TipoProducto(\$select=Nombre)")
    //fun getProductosByTipoProductos( @Query("\$filter") filter: String,@Query("\$top") top: Int,@Query("\$skip") skip: Int): Call<GetProductosByTipoResponse>


    //@GET("odata/Agp2/ViewProductos?\$select= id, nombre_producto,descripcion_producto, fechalimite_disponibilidad, unidadmedida_id_producto, nombre_unidadmedida_producto, sigla_unidadmedida_producto, imagen_producto, is_enabled_producto, precio_producto, precio_escpecial_producto, stock_producto, precio_unidadmedida_producto, usuarioid_producto, calidad_id, nombre_calidad, descripcion_calidad, cultivoid, descripcion_cultivo, estimado_cosecha, fechainicio_cultivo, fechafin_cultivo, nombre_cultivo, siembratotal_cultivo, unidadmedida_id_cultivo, descripcion_unidadmedida_cultivo, detalle_tipo_productoid, descripcion_detalle_tipoproducto, nombre_detalle_tipoproducto, tipo_producto_id, nombre_tipoproducto, lote_id, area_lote, localizacion_lote, localizacion_poligono_lote, descripcion_lote, nombre_lote, unidadmedida_id_lote, descripcion_unidadmedida_lote, unidad_productiva_id, area_up, codigo_up, descripcion_up, nombre_up, unidadmedida_id_up, descripcion_unidadmedida_up, ciudad_id, nombre_ciudad, departamento_id, nombre_departamento, usuario_id, apellido_usuario, email_usuario, estado_usuario, fecharegistro_usuario, identificacion_usuario, nombre_usuario, nro_movil_usuario, numero_cuenta_usuario, phone_number_usuario, username_usuario, detalle_metodopago_id, nombre_detallemetodo_pago, metodopago_id, nombre_metodopago")
    //@GET("odata/Agp2/ViewProductos")
    //fun getProductosByTipoProductos( @Query("\$filter") filter: String,@Query("\$top") top: Int,@Query("\$skip") skip: Int): Call<GetProductosByTipoResponse>



    //@GET("odata/Agp2/ViewProductos")
    //fun getProductosByTipoProductos(@Query("\$filter") filter: String,@Query("\$top") top: Int,@Query("\$skip") skip: Int): Observable<GetProductosByTipoResponse>


    @GET("odata/Agp2/ViewProductos")
    fun getProductosByTipoProductos(@Query("\$filter") filter: String): Observable<GetProductosByTipoResponse>



    @GET("odata/Agp2/ViewProductos")
    fun getProductosByTipoOffPaginate( @Query("\$filter") filter: String): Call<GetProductosByTipoResponse>



    //region Oferta Comprador

    //TODO Requiere Token
    @GET("/odata/Agp2/EstadoOferta")
    fun getEstadosOfertas(): Call<EstadoOfertaResponse>


    @Headers("Content-Type: application/json")
    @POST("odata/Agp2/Oferta")
    fun postOfertaComprador(@Body body: PostOferta): Call<PostOferta>


    @Headers("Content-Type: application/json")
    @POST("odata/Agp2/DetalleOferta")
    fun postDetalleOfertaComprador(@Body body: PostDetalleOferta): Call<PostDetalleOferta>

    //http://18.233.87.16/odata/Agp2/DetalleTipoProductos?$expand=Cultivos($expand=Productos($expand=UnidadMedida,Calidad),Lote($expand=UnidadProductiva($expand=Usuario))),TipoProducto&$filter=TipoProductoId eq 2

    //endregion


    //region Oferta Productor

    //TODO Requiere Token
    //@GET("odata/Agp2/DetalleOferta?\$expand=UnidadMedida,Oferta(\$expand=EstadoOfertum,Usuario(\$select=Id,Apellidos,DetallemetodoPagoId,Email,EmailConfirmed,FechaRegistro,Identificacion,Nombre,Nro_movil,NumeroCuenta,PhoneNumber,PhoneNumberConfirmed,RolId,UserName))")
    @GET("odata/Agp2/DetalleOferta?\$expand=UnidadMedida,Oferta(\$expand=EstadoOfertum,Usuario)")
    fun getOfertasProductor( @Query("\$filter") filter: String, @Query("\$orderby") order: String): Call<OfertaResponse>

    //@GET("odata/Agp2/DetalleOferta?\$expand=Oferta(\$expand=EstadoOfertum),UnidadMedida,Producto(\$expand=UnidadMedida,Calidad,Cultivo(\$expand=Lote(\$expand=UnidadProductiva(\$expand=Ciudad(\$expand=Departamento),Usuario(\$select=Id,Apellidos,DetallemetodoPagoId,Email,EmailConfirmed,FechaRegistro,Identificacion,Nombre,Nro_movil,NumeroCuenta,PhoneNumber,PhoneNumberConfirmed,RolId,UserName)))))")
    @GET("odata/Agp2/DetalleOferta?\$expand=Oferta(\$expand=EstadoOfertum),UnidadMedida,Producto(\$expand=UnidadMedida,Calidad,Cultivo(\$expand=Lote(\$expand=UnidadProductiva(\$expand=Ciudad(\$expand=Departamento),Usuario))))")
    fun getOfertasComprador(@Query("\$filter") filter: String, @Query("\$orderby") order: String): Call<OfertaResponse>



    //@GET("odata/Agp2/DetalleOferta?\$expand=UnidadMedida,Oferta(\$expand=EstadoOfertum,Usuario)")
    //fun getOfertasProductor(@Path("user") user: String): Observable<OfertaResponse>



    @GET("odata/Agp2/Productos")
    fun getProductoOfertas( @Query("\$filter") filter: String): Call<Producto>



    @Headers("Content-Type: application/json")
    @PATCH("odata/Agp2/Oferta({Id})")
    fun updateOferta(@Body body: PostOferta, @Path("Id") Id: Long): Call<PostOferta>

    //http://18.233.87.16/odata/Agp2/DetalleTipoProductos?$expand=Cultivos($expand=Productos($expand=UnidadMedida,Calidad),Lote($expand=UnidadProductiva($expand=Usuario))),TipoProducto&$filter=TipoProductoId eq 2

    //endregion

    //region NOTIFICATION
    @POST("fcm/send")
    @Headers("Content-Type: application/json")
    fun postSendNotifcation(@Header("Authorization") token: String,@Body body: PostNotification): Call<ResponsePostNotification>

    //endregion



    //endregion
    companion object Factory {

        internal val okHttpClient = OkHttpClient.Builder()
                .readTimeout(5, TimeUnit.MINUTES) //Tiempo de respuesta del servicio
                .connectTimeout(5, TimeUnit.MINUTES)
                .build()

        /*var gson = GsonBuilder()
                .setLenient()
                .create()*/

        private var gson: Gson? = GsonBuilder()
        .setDateFormat("yyyy-MM-dd'T'HH:mm")
        .create();



        val BASE_URL = "http://18.233.87.16/"
        fun create(): ApiInterface {
            val retrofit = Retrofit.Builder()
                    .baseUrl(BASE_URL)
                    .client(okHttpClient)
                    .addConverterFactory(GsonConverterFactory.create(gson))
                    .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
                    .build()

            return retrofit.create(ApiInterface::class.java);
        }

        val BASE_URL_NOTIFICATIONS_FCM = "https://fcm.googleapis.com/"
        fun getClienNotifcation(): ApiInterface? {
            var retrofitNotifcation:Retrofit?=null
                retrofitNotifcation = Retrofit.Builder()
                        .baseUrl(BASE_URL_NOTIFICATIONS_FCM)
                        .client(okHttpClient)
                        .addConverterFactory(GsonConverterFactory.create(gson))
                        .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
                        .build()
            return retrofitNotifcation.create(ApiInterface::class.java);
        }



    }

}