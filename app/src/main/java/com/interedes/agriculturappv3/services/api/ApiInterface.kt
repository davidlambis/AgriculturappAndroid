package com.interedes.agriculturappv3.services.api

import com.interedes.agriculturappv3.productor.models.cultivo.Cultivo
import com.interedes.agriculturappv3.productor.models.cultivo.PostCultivo
import com.interedes.agriculturappv3.productor.models.lote.Lote
import com.interedes.agriculturappv3.productor.models.unidad_productiva.UnidadProductiva
import com.interedes.agriculturappv3.productor.models.detalle_metodo_pago.DetalleMetodoPagoResponse
import com.interedes.agriculturappv3.productor.models.detalletipoproducto.DetalleTipoProductoResponse
import com.interedes.agriculturappv3.productor.models.login.Login
import com.interedes.agriculturappv3.productor.models.login.LoginResponse
import com.interedes.agriculturappv3.productor.models.lote.PostLote
import com.interedes.agriculturappv3.productor.models.rol.RolResponse
import com.interedes.agriculturappv3.productor.models.metodopago.MetodoPagoResponse
import com.interedes.agriculturappv3.productor.models.rol.AspNetRolResponse
import com.interedes.agriculturappv3.productor.models.tipoproducto.TipoProductoResponse
import com.interedes.agriculturappv3.productor.models.unidad_medida.CategoriaMedidaResponse
import com.interedes.agriculturappv3.productor.models.unidad_medida.UnidadMedidaResponse
import com.interedes.agriculturappv3.productor.models.unidad_productiva.PostUnidadProductiva
import com.interedes.agriculturappv3.productor.models.usuario.*
import okhttp3.OkHttpClient
import retrofit2.Call
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.*
import java.util.concurrent.TimeUnit
import retrofit2.http.GET


interface ApiInterface {

    //region Peticiones
    //Get Roles
    @GET("odata/Agp2/Rols")
    fun getRoles(): Call<RolResponse>

    //Get Métodos Pago
    @GET("odata/Agp2/MetodoPagos")
    fun getMetodoPagos(): Call<MetodoPagoResponse>

    //Get Detalle Métodos Pago
    @GET("odata/Agp2/DetalleMetodopagos")
    fun getDetalleMetodoPagos(): Call<DetalleMetodoPagoResponse>

    //Get Tipo Productos
    @GET("odata/Agp2/TipoProductos")
    fun getTiposProducto(): Call<TipoProductoResponse>

    @GET("odata/Agp2/DetalleTipoProductos")
    fun getDetalleTiposProducto(): Call<DetalleTipoProductoResponse>

    //Post Registro Usuarios
    @Headers("Content-Type: application/json")
    @POST("auth/register")
    fun postRegistroUsers(@Body body: User): Call<UserResponse>

    //Get Usuarios
    @GET("odata/agriculturebd/Usuarios")
    fun getUsuarios(): Call<UsuarioResponse>

    //Post Login
    @Headers("Content-Type: application/json")
    @POST("auth/login")
    fun postLogin(@Body body: Login): Call<LoginResponse>

    //Get Usuario por Correo
    @GET("odata/Agp2/Usuarios")
    fun getUsuarioByCorreo(@Query("\$filter") filter: String): Call<UsuarioResponse>

    @GET("auth/ApplicationUsers")
    fun getAuthUserByCorreo(@Header("Authorization") token: String, @Query("\$filter") filter: String): Call<GetUserResponse>

    //Get en AspNetRoles por Tipo User
    @GET("auth/ApplicationRoles")
    fun getAspNetRolesByTipoUser(@Header("Authorization") token: String, @Query("\$filter") filter: String): Call<AspNetRolResponse>

    //Get Categorías Medida
    @GET("odata/Agp2/CategoriaMedidas")
    fun getCategoriasMedida(): Call<CategoriaMedidaResponse>

    //Get Unidades Medida
    @GET("odata/Agp2/UnidadMedidas")
    fun getUnidadesMedida(): Call<UnidadMedidaResponse>

    //region UNIDADES PRODUCTIVAS
    //Post Unidad Productiva
    //TODO Requiere Token
    @Headers("Content-Type: application/json")
    @POST("odata/Agp2/UnidadProductivas")
    fun postUnidadProductiva(@Body body: PostUnidadProductiva): Call<UnidadProductiva>

    //Patch Unidad Productiva
    //TODO Requiere Token
    @Headers("Content-Type: application/json")
    @PATCH("odata/Agp2/UnidadProductivas({Id})")
    fun updateUnidadProductiva(@Body body: PostUnidadProductiva, @Path("Id") Id: Long): Call<UnidadProductiva>
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

        val BASE_URL = "http://18.233.87.16/"
        fun create(): ApiInterface {
            val retrofit = Retrofit.Builder()
                    .baseUrl(BASE_URL)
                    .client(okHttpClient)
                    .addConverterFactory(GsonConverterFactory.create())
                    .build()

            return retrofit.create(ApiInterface::class.java);
        }
    }

}