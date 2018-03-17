package com.interedes.agriculturappv3.services.api

import com.interedes.agriculturappv3.asistencia_tecnica.models.usuario.Usuario
import com.interedes.agriculturappv3.asistencia_tecnica.models.detalle_metodo_pago.DetalleMetodoPagoResponse
import com.interedes.agriculturappv3.asistencia_tecnica.models.login.Login
import com.interedes.agriculturappv3.asistencia_tecnica.models.login.LoginResponse
import com.interedes.agriculturappv3.asistencia_tecnica.models.rol.RolResponse
import com.interedes.agriculturappv3.asistencia_tecnica.models.metodopago.MetodoPagoResponse
import com.interedes.agriculturappv3.asistencia_tecnica.models.usuario.User
import com.interedes.agriculturappv3.asistencia_tecnica.models.usuario.UserResponse
import com.interedes.agriculturappv3.asistencia_tecnica.models.usuario.UsuarioResponse
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
    @GET("odata/agriculturebd/Rols")
    fun getRoles(): Call<RolResponse>

    //Get Métodos Pago
    @GET("odata/agriculturebd/MetodoPagos")
    fun getMetodoPagos(): Call<MetodoPagoResponse>

    //Get Detalle Métodos Pago
    @GET("odata/agriculturebd/DetalleMetodopagos")
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
    fun getUsuarioByCorreo(@Query("\$filter") filter:String): Call<UsuarioResponse>


    //endregion

    companion object Factory {
        internal val okHttpClient = OkHttpClient.Builder()
                .readTimeout(5, TimeUnit.MINUTES) //Tiempo de respuesta del servicio
                .connectTimeout(5, TimeUnit.MINUTES)
                .build()

        val BASE_URL = "http://34.207.18.23/"
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