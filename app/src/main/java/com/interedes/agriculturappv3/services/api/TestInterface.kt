package com.interedes.agriculturappv3.services.api

import com.interedes.agriculturappv3.productor.models.departments.DeparmentsResponse
import okhttp3.OkHttpClient
import retrofit2.Call
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.GET
import java.util.concurrent.TimeUnit


interface TestInterface {

    @GET("api/ResponseDataSync")
    fun getDepartamentos(): Call<DeparmentsResponse>


    companion object Factory {
        internal val okHttpClient = OkHttpClient.Builder()
                .readTimeout(5, TimeUnit.MINUTES) //Tiempo de respuesta del servicio
                .connectTimeout(5, TimeUnit.MINUTES)
                .build()

        /*var gson = GsonBuilder()
                .setLenient()
                .create()*/

        val BASE_URL = "http://181.60.56.39:89/"
        fun create(): TestInterface {
            val retrofit = Retrofit.Builder()
                    .baseUrl(BASE_URL)
                    .client(okHttpClient)
                    .addConverterFactory(GsonConverterFactory.create())
                    .build()

            return retrofit.create(TestInterface::class.java);
        }
    }
}