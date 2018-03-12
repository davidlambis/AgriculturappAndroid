package com.interedes.agriculturappv3.services.api

import com.interedes.agriculturappv3.asistencia_tecnica.models.rol.RolResponse
import com.interedes.agriculturappv3.asistencia_tecnica.models.metodopago.MetodoPago
import com.interedes.agriculturappv3.asistencia_tecnica.models.metodopago.MetodoPagoResponse
import okhttp3.OkHttpClient
import retrofit2.Call
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.GET
import java.util.concurrent.TimeUnit

interface ApiInterface {

    //region Peticiones
    @GET("odata/agriculturebd/Rols")
    abstract fun getRoles(): Call<RolResponse>

    @GET("odata/agriculturebd/MetodoPagos")
    abstract fun getMetodoPagos(): Call<MetodoPagoResponse>


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