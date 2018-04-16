package com.interedes.agriculturappv3.services.api

import com.interedes.agriculturappv3.productor.models.GenericResponse
import com.interedes.agriculturappv3.productor.models.detalle_metodo_pago.DetalleMetodoPagoResponse
import com.interedes.agriculturappv3.productor.models.login.Login
import com.interedes.agriculturappv3.productor.models.login.LoginResponse
import com.interedes.agriculturappv3.productor.models.rol.RolResponse
import com.interedes.agriculturappv3.productor.models.metodopago.MetodoPagoResponse
import com.interedes.agriculturappv3.productor.models.rol.AspNetRolResponse
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
    @GET("odata/agpbd/Rols")
    fun getRoles(): Call<RolResponse>

    //Get Métodos Pago
    @GET("odata/agpbd/MetodoPagos")
    fun getMetodoPagos(): Call<MetodoPagoResponse>

    //Get Detalle Métodos Pago
    @GET("odata/agpbd/DetalleMetodopagos")
    fun getDetalleMetodoPagos(): Call<DetalleMetodoPagoResponse>

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
    @GET("odata/agriculturebd/Usuarios")
    fun getUsuarioByCorreo(@Query("\$filter") filter: String): Call<UsuarioResponse>

    @GET("auth/ApplicationUsers")
    fun getAuthUserByCorreo(@Header("Authorization") token: String, @Query("\$filter") filter: String): Call<GetUserResponse>

    //Get en AspNetRoles por Tipo User
    @GET("auth/ApplicationRoles")
    fun getAspNetRolesByTipoUser(@Header("Authorization") token: String, @Query("\$filter") filter: String): Call<AspNetRolResponse>

    //Patch AspNet User Roles

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