package com.interedes.agriculturappv3.modules.main_menu.ui

import android.content.Intent
import android.support.v4.content.ContextCompat.startActivity
import android.util.Base64
import android.util.Log
import com.google.firebase.FirebaseException
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ServerValue
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import com.interedes.agriculturappv3.activities.login.ui.LoginActivity
import com.interedes.agriculturappv3.libs.EventBus
import com.interedes.agriculturappv3.libs.GreenRobotEventBus
import com.interedes.agriculturappv3.modules.main_menu.ui.events.RequestEventMainMenu
import com.interedes.agriculturappv3.modules.models.control_plaga.ControlPlaga
import com.interedes.agriculturappv3.modules.models.control_plaga.ControlPlaga_Table
import com.interedes.agriculturappv3.modules.models.control_plaga.PostControlPlaga
import com.interedes.agriculturappv3.modules.models.cultivo.Cultivo
import com.interedes.agriculturappv3.modules.models.cultivo.Cultivo_Table
import com.interedes.agriculturappv3.modules.models.cultivo.PostCultivo
import com.interedes.agriculturappv3.modules.models.departments.Ciudad
import com.interedes.agriculturappv3.modules.models.departments.DeparmentsResponse
import com.interedes.agriculturappv3.modules.models.departments.Departamento
import com.interedes.agriculturappv3.modules.models.lote.Lote
import com.interedes.agriculturappv3.modules.models.lote.Lote_Table
import com.interedes.agriculturappv3.modules.models.lote.PostLote
import com.interedes.agriculturappv3.modules.models.plagas.Enfermedad
import com.interedes.agriculturappv3.modules.models.plagas.EnfermedadResponseApi
import com.interedes.agriculturappv3.modules.models.produccion.PostProduccion
import com.interedes.agriculturappv3.modules.models.produccion.Produccion
import com.interedes.agriculturappv3.modules.models.produccion.Produccion_Table
import com.interedes.agriculturappv3.modules.models.producto.*
import com.interedes.agriculturappv3.modules.models.sincronizacion.GetSincronizacionResponse
import com.interedes.agriculturappv3.modules.models.sincronizacion.GetSincronizacionTransacciones
import com.interedes.agriculturappv3.modules.models.sincronizacion.GetSynProductosUserResponse
import com.interedes.agriculturappv3.modules.models.sincronizacion.QuantitySync
import com.interedes.agriculturappv3.modules.models.tipoproducto.TipoProducto
import com.interedes.agriculturappv3.modules.models.tipoproducto.TipoProductoResponse
import com.interedes.agriculturappv3.modules.models.tratamiento.Tratamiento
import com.interedes.agriculturappv3.modules.models.tratamiento.TratamientoResponse
import com.interedes.agriculturappv3.modules.models.tratamiento.calificacion.Calificacion_Tratamiento
import com.interedes.agriculturappv3.modules.models.tratamiento.calificacion.Calificacion_Tratamiento_Table
import com.interedes.agriculturappv3.modules.models.unidad_medida.CategoriaMedida
import com.interedes.agriculturappv3.modules.models.unidad_medida.CategoriaMedidaResponse
import com.interedes.agriculturappv3.modules.models.unidad_productiva.PostUnidadProductiva
import com.interedes.agriculturappv3.modules.models.unidad_productiva.Unidad_Productiva
import com.interedes.agriculturappv3.modules.models.unidad_productiva.Unidad_Productiva_Table
import com.interedes.agriculturappv3.modules.models.unidad_productiva.localizacion.LocalizacionUp
import com.interedes.agriculturappv3.modules.models.usuario.Usuario
import com.interedes.agriculturappv3.modules.models.usuario.Usuario_Table
import com.interedes.agriculturappv3.modules.models.ventas.*
import com.interedes.agriculturappv3.modules.models.ventas.RequestApi.CategoriaPucResponse
import com.interedes.agriculturappv3.modules.models.ventas.RequestApi.EstadoTransaccionResponse
import com.interedes.agriculturappv3.modules.models.ventas.RequestApi.PostTercero
import com.interedes.agriculturappv3.modules.models.ventas.RequestApi.PostTransaccion
import com.interedes.agriculturappv3.modules.productor.accounting_module.transacciones.events.RequestEventTransaccion
import com.interedes.agriculturappv3.modules.productor.asistencia_tecnica_module.UnidadProductiva.events.RequestEventUP
import com.interedes.agriculturappv3.services.api.ApiInterface
import com.interedes.agriculturappv3.services.listas.Listas
import com.interedes.agriculturappv3.services.resources.RolResources
import com.interedes.agriculturappv3.services.resources.Status_Chat
import com.raizlabs.android.dbflow.data.Blob
import com.raizlabs.android.dbflow.kotlinextensions.save
import com.raizlabs.android.dbflow.kotlinextensions.update
import com.raizlabs.android.dbflow.sql.language.SQLite
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.math.BigDecimal
import java.math.MathContext
import java.text.SimpleDateFormat

class MenuRepository: MainViewMenu.Repository {



    var eventBus: EventBus? = null
    var apiService: ApiInterface? = null

    //FIREBASE
    private var mUserDBRef: DatabaseReference? = null
    private var mStorageRef: StorageReference? = null
    var mAuth: FirebaseAuth? = null
    private var  mCurrentUserID: String? = null

    init {
        eventBus = GreenRobotEventBus()
        apiService = ApiInterface.create()

        mCurrentUserID = FirebaseAuth.getInstance().currentUser?.uid

        if(mCurrentUserID==null){
            mCurrentUserID=getLastUserLogued()?.IdFirebase
        }


        mAuth= FirebaseAuth.getInstance()
        mUserDBRef = FirebaseDatabase.getInstance().reference.child("Users")
        mStorageRef = FirebaseStorage.getInstance().reference.child("Photos").child("Users")
    }


    override fun verificateUserLoguedFirebaseFirebase(): FirebaseUser?
    {
        return  FirebaseAuth.getInstance().currentUser
    }

    override fun makeUserOnline(checkConection:Boolean) {
        var userLogued=getLastUserLogued()
        if(userLogued!=null){
            var verficateLoguedFirebase=verificateUserLoguedFirebaseFirebase()
            if(checkConection){
                if(verficateLoguedFirebase==null){
                    loginFirebase(userLogued)
                }else{
                    makeUserOnlineSet()
                }
            }
        }
    }

    private fun makeUserOnlineSet() {
        var userStatus= mUserDBRef?.child(mCurrentUserID+"/status")
        var userLastOnlineRef= mUserDBRef?.child(mCurrentUserID+"/last_Online")
        userStatus?.setValue(Status_Chat.ONLINE)
        userStatus?.onDisconnect()?.setValue(Status_Chat.OFFLINE)
        userLastOnlineRef?.onDisconnect()?.setValue(ServerValue.TIMESTAMP);
    }

    override fun makeUserOffline(checkConection:Boolean) {
        var verficateLoguedFirebase=verificateUserLoguedFirebaseFirebase()
        if(verficateLoguedFirebase!=null){
                makeUserOfflineSet()
        }
    }

    private fun makeUserOfflineSet() {
        var userStatus= mUserDBRef?.child(mCurrentUserID+"/status")
        var userLastOnlineRef= mUserDBRef?.child(mCurrentUserID+"/last_Online")
        userStatus?.setValue(Status_Chat.OFFLINE)
        userLastOnlineRef?.setValue(ServerValue.TIMESTAMP);
    }

    override fun loginFirebase(usuario:Usuario?)
    {
        mAuth?.signInWithEmailAndPassword(usuario?.Email!!, usuario?.Contrasena!!)?.addOnCompleteListener({ task ->
            if (task.isSuccessful) {
                makeUserOnlineSet()
            } else {
                try {
                    throw task.exception!!
                } catch (firebaseException: FirebaseException) {
                   // postEventError(RequestEventAccount.ERROR_EVENT, firebaseException.toString())
                    Log.e("Error Post", firebaseException.toString())
                }
            }
        })
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
        } catch (e: Exception) {
           // postEvent(RequestEvent.ERROR_EVENT, e.message.toString())
        }
    }



    override fun syncQuantityData() {

        var usuarioLogued=getLastUserLogued()

        var counRegisterUnidadesProductivas=SQLite.select().from(Unidad_Productiva::class.java)
                .where(Unidad_Productiva_Table.Estado_Sincronizacion.eq(false))
                .and(Unidad_Productiva_Table.UsuarioId.eq(usuarioLogued?.Id))
                .queryList().count()

        var counRegisterLotes=SQLite.select().from(Lote::class.java).where(Lote_Table.EstadoSincronizacion.eq(false))
                .and(Lote_Table.UsuarioId.eq(usuarioLogued?.Id))
                .queryList().count()
        var counRegisterCultivos=SQLite.select().from(Cultivo::class.java).where(Cultivo_Table.EstadoSincronizacion.eq(false))
                .and(Cultivo_Table.UsuarioId.eq(usuarioLogued?.Id))
                .queryList().count()
        var counRegisterControlPlagas=SQLite.select().from(ControlPlaga::class.java).where(ControlPlaga_Table.Estado_Sincronizacion.eq(false))
                .and(ControlPlaga_Table.UsuarioId.eq(usuarioLogued?.Id))
                .queryList().count()
        var counRegisterProducccion=SQLite.select().from(Produccion::class.java).where(Produccion_Table.Estado_Sincronizacion.eq(false))
                .and(Produccion_Table.UsuarioId.eq(usuarioLogued?.Id))
                .queryList().count()
        var counRegisterProductos=SQLite.select().from(Producto::class.java).where(Producto_Table.Estado_Sincronizacion.eq(false))
                .and(Producto_Table.userId.eq(usuarioLogued?.Id))
                .queryList().count()
        var counRegisterTransacciones=SQLite.select().from(Transaccion::class.java).where(Transaccion_Table.Estado_Sincronizacion.eq(false))
                .and(Transaccion_Table.UsuarioId.eq(usuarioLogued?.Id))
                .queryList().count()

        var registerTotal= counRegisterUnidadesProductivas+
                counRegisterControlPlagas+
                counRegisterCultivos+
                counRegisterLotes+counRegisterProducccion+counRegisterProductos+counRegisterTransacciones

        var countUpdatesUnidadesProductivas=SQLite.select()
                .from(Unidad_Productiva::class.java)
                .where(Unidad_Productiva_Table.Estado_Sincronizacion.eq(true))
                .and(Unidad_Productiva_Table.UsuarioId.eq(usuarioLogued?.Id))
                .and(Unidad_Productiva_Table.Estado_SincronizacionUpdate.eq(false)).queryList().count()


        var countUpdatesLotes=SQLite.select().from(Lote::class.java)
                .where(Lote_Table.EstadoSincronizacion.eq(true))
                .and(Lote_Table.UsuarioId.eq(usuarioLogued?.Id))
                .and(Lote_Table.Estado_SincronizacionUpdate.eq(false)).queryList().count()


        var countUpdatesCultivos=SQLite.select().from(Cultivo::class.java)
                .where(Cultivo_Table.EstadoSincronizacion.eq(true))
                .and(Cultivo_Table.UsuarioId.eq(usuarioLogued?.Id))
                .and(Cultivo_Table.Estado_SincronizacionUpdate.eq(false)).queryList().count()


        var countUpdatesControlPlagas=SQLite.select().from(ControlPlaga::class.java)
                .where(ControlPlaga_Table.Estado_Sincronizacion.eq(true))
                .and(ControlPlaga_Table.UsuarioId.eq(usuarioLogued?.Id))
                .and(ControlPlaga_Table.Estado_SincronizacionUpdate.eq(false)).queryList().count()

        var countUpdatesProducccion=SQLite.select().from(Produccion::class.java)
                .where(Produccion_Table.Estado_Sincronizacion.eq(true))
                .and(Produccion_Table.UsuarioId.eq(usuarioLogued?.Id))
                .and(Produccion_Table.Estado_SincronizacionUpdate.eq(false)).queryList().count()

        var countUpdatesProductos=SQLite.select().from(Producto::class.java)
                .where(Producto_Table.Estado_Sincronizacion.eq(true))
                .and(Producto_Table.userId.eq(usuarioLogued?.Id))
                .and(Producto_Table.Estado_SincronizacionUpdate.eq(false)).queryList().count()

        var countUpdatesTransacciones=SQLite.select().from(Transaccion::class.java)
                .where(Transaccion_Table.Estado_Sincronizacion.eq(true))
                .and(Transaccion_Table.UsuarioId.eq(usuarioLogued?.Id))
                .and(Transaccion_Table.Estado_SincronizacionUpdate.eq(false)).queryList().count()

        var updatesTotal= countUpdatesUnidadesProductivas+
                countUpdatesLotes+
                countUpdatesCultivos+
                countUpdatesControlPlagas+countUpdatesProducccion+countUpdatesProductos+countUpdatesTransacciones


        var quantitySync= QuantitySync(registerTotal.toLong(),updatesTotal.toLong())
        postEventOkQuntitySync(RequestEventMainMenu.SYNC_RESUME,quantitySync)
    }

    override fun syncData() {

        var usuario= getLastUserLogued()

        var mUnidadProductiva= SQLite.select()
                .from(Unidad_Productiva::class.java)
                .where(Unidad_Productiva_Table.Estado_Sincronizacion.eq(false))
                .and(Unidad_Productiva_Table.UsuarioId.eq(usuario?.Id))
                .orderBy(Unidad_Productiva_Table.Unidad_Productiva_Id,false).querySingle()
        if(mUnidadProductiva!=null){


            val areaBig = BigDecimal(mUnidadProductiva.Area!!, MathContext.DECIMAL64)
            val postUnidadProductiva = PostUnidadProductiva(0,
                    areaBig,
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

                        //Thread.sleep(100)


                        val idUP = response.body()?.Id_Remote
                        mUnidadProductiva?.Id_Remote = idUP
                        //mUnidadProductiva?.save()
                        //postLocalizacionUnidadProductiva

                        val latitudBig = BigDecimal(mUnidadProductiva.Latitud!!, MathContext.DECIMAL64)
                        val longitudBig = BigDecimal(mUnidadProductiva.Longitud!!, MathContext.DECIMAL64)



                        val postLocalizacionUnidadProductiva = LocalizacionUp(0,
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

        var usuario=getLastUserLogued()

        var mLote= SQLite.select()
                .from(Lote::class.java)
                .where(Lote_Table.EstadoSincronizacion.eq(false))
                .and(Lote_Table.UsuarioId.eq(usuario?.Id))
                .orderBy(Lote_Table.LoteId,false).querySingle()
        val unidad_productiva = SQLite.select().from(Unidad_Productiva::class.java).where(Unidad_Productiva_Table.Unidad_Productiva_Id.eq(mLote?.Unidad_Productiva_Id)).querySingle()
        if(mLote!=null && unidad_productiva?.Estado_Sincronizacion==true){

            val areaBig = BigDecimal(mLote.Area!!, MathContext.DECIMAL64)
            val postLote = PostLote(0,
                    areaBig,
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
        var usuario=getLastUserLogued()
        var mCultivo= SQLite.select()
                .from(Cultivo::class.java)
                .where(Cultivo_Table.EstadoSincronizacion.eq(false))
                .and(Cultivo_Table.UsuarioId.eq(usuario?.Id))
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

        var usuario= getLastUserLogued()

        var controlPlaga= SQLite.select()
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

        var usuario=getLastUserLogued()

        var produccion= SQLite.select()
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
        var usuario=getLastUserLogued()

        var mProducto= SQLite.select()
                .from(Producto::class.java)
                .where(Producto_Table.Estado_Sincronizacion.eq(false))
                .and(Produccion_Table.UsuarioId.eq(usuario?.Id))
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

        var usuario=getLastUserLogued()

        var transaccion= SQLite.select()
                .from(Transaccion::class.java)
                .where(Transaccion_Table.Estado_Sincronizacion.eq(false))
                .and(Transaccion_Table.UsuarioId.eq(usuario?.Id))
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
                                    postEventError(RequestEventMainMenu.ERROR_EVENT, "Comprueba tu conexión")
                                }
                            }
                            override fun onFailure(call: Call<PostTransaccion>?, t: Throwable?) {
                                postEventError(RequestEventMainMenu.ERROR_EVENT, "Comprueba tu conexión")
                            }
                        })
                        //postEventOk(RequestEventTransaccion.SAVE_EVENT, getProductions(cultivo_id), produccion)
                    } else {
                        postEventError(RequestEventMainMenu.ERROR_EVENT, "Comprueba tu conexión")
                    }
                }
                override fun onFailure(call: Call<PostTercero>?, t: Throwable?) {
                    postEventError(RequestEventMainMenu.ERROR_EVENT, "Comprueba tu conexión")
                }
            })
        }else{
            postEventOk(RequestEventMainMenu.SYNC_EVENT)
        }
    }


    override fun getListSyncEnfermedadesAndTratamiento() {
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
                    loadTratamientos()
                } else {
                    postEventError(RequestEventMainMenu.ERROR_EVENT, "Comprueba tu conexión a Internet")
                }
            }
            override fun onFailure(call: Call<EnfermedadResponseApi>?, t: Throwable?) {
                postEventError(RequestEventMainMenu.ERROR_EVENT, "Comprueba tu conexión a Internet")
            }
        })



    }

    private fun loadTratamientos() {
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
                    postEventOk(RequestEventMainMenu.SYNC_EVENT)

                } else {
                    postEventError(RequestEventMainMenu.ERROR_EVENT, "Comprueba tu conexión a Internet")
                }
            }
            override fun onFailure(call: Call<TratamientoResponse>?, t: Throwable?) {
                postEventError(RequestEventMainMenu.ERROR_EVENT, "Comprueba tu conexión a Internet")
            }
        })
    }


    //Listas Iniciales
    fun getLastUp(usuario:Usuario?): Unidad_Productiva? {

        if(usuario!=null){

            val lastUnidadProductiva = SQLite.select()
                    .from(Unidad_Productiva::class.java)
                    .where(Unidad_Productiva_Table.UsuarioId.eq(usuario?.Id)).orderBy(Unidad_Productiva_Table.Unidad_Productiva_Id, false)
                    .querySingle()
            return lastUnidadProductiva
        }else{
            val lastUnidadProductiva = SQLite.select()
                    .from(Unidad_Productiva::class.java)
                    .orderBy(Unidad_Productiva_Table.Unidad_Productiva_Id, false)
                    .querySingle()
            return lastUnidadProductiva

        }
    }

    fun getLastLote(usuario:Usuario?): Lote? {


        if(usuario!=null){
            val lastLote = SQLite.select().from(Lote::class.java)
                    .where(Lote_Table.UsuarioId.eq(usuario?.Id))
                    .orderBy(Lote_Table.LoteId, false).querySingle()
            return lastLote


        }else{
            val lastLote = SQLite.select().from(Lote::class.java)
                    .orderBy(Lote_Table.LoteId, false).querySingle()
            return lastLote

        }

    }

    fun getLastCultivo(usuario:Usuario?): Cultivo? {


        if(usuario!=null){
            val lastCultivo = SQLite.select().from(Cultivo::class.java)
                    .where(Cultivo_Table.UsuarioId.eq(usuario?.Id))
                    .orderBy(Cultivo_Table.CultivoId, false).querySingle()
            return lastCultivo

        }else{
            val lastCultivo = SQLite.select().from(Cultivo::class.java)
                    .orderBy(Cultivo_Table.CultivoId, false).querySingle()
            return lastCultivo
        }


    }

    fun getLastProduccion(usuario:Usuario?): Produccion? {
        if(usuario!=null){
            val lastProduccion = SQLite.select().from(Produccion::class.java)
                    .where(Produccion_Table.UsuarioId.eq(usuario?.Id))
                    .orderBy(Produccion_Table.ProduccionId, false).querySingle()
            return lastProduccion

        }else{
            val lastProduccion = SQLite.select().from(Produccion::class.java)
                    .orderBy(Produccion_Table.ProduccionId, false).querySingle()
            return lastProduccion
        }
    }

    fun getLastControlPlaga(usuario:Usuario?): ControlPlaga? {

        if(usuario!=null){
            val lastControlPlaga = SQLite.select().from(ControlPlaga::class.java)
                    .where(ControlPlaga_Table.UsuarioId.eq(usuario?.Id)).orderBy(ControlPlaga_Table.ControlPlagaId, false).querySingle()
            return lastControlPlaga

        }else{
            val lastControlPlaga = SQLite.select().from(ControlPlaga::class.java)
                    .orderBy(ControlPlaga_Table.ControlPlagaId, false).querySingle()
            return lastControlPlaga
        }



    }

    fun getLastTransaccion(usuario:Usuario?): Transaccion? {

        if(usuario!=null){
            val lastTransaccion = SQLite.select().from(Transaccion::class.java)
                    .where(Transaccion_Table.UsuarioId.eq(usuario?.Id)).orderBy(Transaccion_Table.TransaccionId, false).querySingle()
            return lastTransaccion

        }else{
            val lastTransaccion = SQLite.select().from(Transaccion::class.java)
                    .orderBy(Transaccion_Table.TransaccionId, false).querySingle()
            return lastTransaccion
        }



    }

    fun getLastTercero(usuario:Usuario?): Tercero? {

        if(usuario!=null){
            val lastTercero = SQLite.select().from(Tercero::class.java).where(Tercero_Table.Usuario_Id.eq(usuario.Id)).orderBy(Tercero_Table.TerceroId, false).querySingle()
            return lastTercero

        }else{
            val lastTercero = SQLite.select().from(Tercero::class.java).orderBy(Tercero_Table.TerceroId, false).querySingle()
            return lastTercero
        }


    }

    fun getLastProducto(usuario:Usuario?): Producto? {

        if(usuario!=null){
            val lastProducto = SQLite.select().from(Producto::class.java)
                    .where(Produccion_Table.UsuarioId.eq(usuario?.Id)).orderBy(Producto_Table.ProductoId, false).querySingle()
            return lastProducto

        }else{
            val lastProducto = SQLite.select().from(Producto::class.java)
                    .orderBy(Producto_Table.ProductoId, false).querySingle()
            return lastProducto
        }



    }


    //region Métodos Interfaz

    override fun getLastUserLogued(): Usuario? {

        val usuarioLogued = SQLite.select().from(Usuario::class.java).where(Usuario_Table.UsuarioRemembered.eq(true)).querySingle()
        return usuarioLogued
    }

    override fun getListasIniciales() {

        var usuario= getLastUserLogued()

        //LISTAS ROL PRODUCTOR
        /*-----------------------------------------------------------------------------------------------------------------*/
        if(usuario?.RolNombre?.equals(RolResources.PRODUCTOR)!!){

            val query = Listas.queryGeneral("UsuarioId",usuario?.Id.toString())
            val callInformacionSinronized = apiService?.getSyncInformacionUsuario(query)
            callInformacionSinronized?.enqueue(object : Callback<GetSincronizacionResponse> {
                override fun onResponse(call: Call<GetSincronizacionResponse>?, response: Response<GetSincronizacionResponse>?) {
                    if (response != null && response.code() == 200) {


                        val unidadesProductivas = response.body()?.value as MutableList<Unidad_Productiva>

                        //TODO Delete information in local, add new remote
                        SQLite.delete<Unidad_Productiva>(Unidad_Productiva::class.java)
                                .where(Unidad_Productiva_Table.Estado_Sincronizacion.eq(true))
                                .and(Unidad_Productiva_Table.UsuarioId.eq(usuario.Id))
                                .and(Unidad_Productiva_Table.Estado_SincronizacionUpdate.eq(true))
                                .async()
                                .execute()

                        SQLite.delete<Lote>(Lote::class.java)
                                .where(Lote_Table.EstadoSincronizacion.eq(true))
                                .and(Lote_Table.UsuarioId.eq(usuario.Id))
                                .and(Lote_Table.Estado_SincronizacionUpdate.eq(true))
                                .async()
                                .execute()


                        SQLite.delete<Cultivo>(Cultivo::class.java)
                                .where(Cultivo_Table.EstadoSincronizacion.eq(true))
                                .and(Cultivo_Table.UsuarioId.eq(usuario.Id))
                                .and(Cultivo_Table.Estado_SincronizacionUpdate.eq(true))
                                .async()
                                .execute()

                        SQLite.delete<Produccion>(Produccion::class.java)
                                .where(Produccion_Table.Estado_Sincronizacion.eq(true))
                                .and(Produccion_Table.UsuarioId.eq(usuario.Id))
                                .and(Produccion_Table.Estado_SincronizacionUpdate.eq(true))
                                .async()
                                .execute()

                        SQLite.delete<ControlPlaga>(ControlPlaga::class.java)
                                .where(ControlPlaga_Table.Estado_Sincronizacion.eq(true))
                                .and(ControlPlaga_Table.UsuarioId.eq(usuario.Id))
                                .and(ControlPlaga_Table.Estado_SincronizacionUpdate.eq(true))
                                .async()
                                .execute()


                        //TODO Add information new remote
                        for (item in unidadesProductivas) {
                            var unidadProductivaVerficateSave= SQLite.select()
                                    .from(Unidad_Productiva::class.java)
                                    .where(Unidad_Productiva_Table.Id_Remote.eq(item.Id_Remote))
                                    .and(Unidad_Productiva_Table.Estado_SincronizacionUpdate.eq(false))
                                    .querySingle()
                            //TODO Verifica si tiene pendiente actualizacion por sincronizar
                            if (unidadProductivaVerficateSave !=null){
                                item.Unidad_Productiva_Id=unidadProductivaVerficateSave?.Unidad_Productiva_Id
                            }else{
                                val last_up = getLastUp(null)
                                if (last_up == null) {
                                    item.Unidad_Productiva_Id = 1
                                } else {
                                    item.Unidad_Productiva_Id = last_up.Unidad_Productiva_Id!! + 1
                                }

                                item.UsuarioId=usuario?.Id
                                item.Nombre_Ciudad= if (item.Ciudad!=null) item.Ciudad?.Nombre else null
                                item.Nombre_Unidad_Medida= if (item.UnidadMedida!=null) item.UnidadMedida?.Descripcion else null
                                item.Nombre_Departamento= if (item.Ciudad!=null) item.Ciudad?.Departamento?.Nombre else null
                                item.Estado_Sincronizacion=true
                                item.Estado_SincronizacionUpdate=true

                                if(item.LocalizacionUps?.size!!>0){
                                    for (localizacion in item.LocalizacionUps!!){
                                        item.DireccionAproximadaGps=localizacion.DireccionAproximadaGps
                                        item.Latitud=localizacion.Latitud?.toDouble()
                                        item.Longitud=localizacion.Longitud?.toDouble()
                                        item.Coordenadas=localizacion.Coordenadas
                                        item.Direccion=localizacion.Direccion
                                        item.LocalizacionUpId=localizacion.Id
                                    }
                                }

                                item.Configuration_Point=true
                                item.Configuration_Poligon=false
                                item.save()
                            }

                            if(item.Lotes?.size!!>0){
                                for (lote in item.Lotes!!){
                                    var loteVerficateSave= SQLite.select()
                                            .from(Lote::class.java)
                                            .where(Lote_Table.Id_Remote.eq(lote.Id_Remote))
                                            .and(Lote_Table.Estado_SincronizacionUpdate.eq(false))
                                            .querySingle()

                                    //TODO Verifica si tiene pendiente actualizacion por sincronizar
                                    if (loteVerficateSave!=null){
                                        lote.LoteId=loteVerficateSave.LoteId
                                    }else{

                                        val last_lote = getLastLote(null)
                                        if (last_lote == null) {
                                            lote.LoteId = 1
                                        } else {
                                            lote.LoteId = last_lote.LoteId!! + 1
                                        }

                                        val coordenadas =lote.Localizacion
                                        if(coordenadas!=null || coordenadas!=""){
                                            val separated = coordenadas?.split("/".toRegex())?.dropLastWhile { it.isEmpty() }?.toTypedArray()
                                            var latitud= separated!![0].toDoubleOrNull() // this will contain "Fruit"
                                            var longitud=separated!![1].toDoubleOrNull() // this will contain " they taste good"
                                            lote.Latitud=latitud
                                            lote.Longitud=longitud
                                            lote.Coordenadas=coordenadas
                                        }
                                        lote.UsuarioId=usuario.Id
                                        lote.Unidad_Productiva_Id=item.Unidad_Productiva_Id
                                        lote.Nombre_Unidad_Medida= if (lote.UnidadMedida!=null) lote.UnidadMedida?.Descripcion else null
                                        lote.Nombre_Unidad_Productiva= item.nombre

                                        lote.Nombre= if (lote.Nombre==null) "" else lote.Nombre
                                        lote.Descripcion= if (lote.Descripcion==null) "" else lote.Descripcion
                                        lote.Estado_SincronizacionUpdate=true
                                        lote.EstadoSincronizacion=true

                                        lote.save()
                                    }

                                    if(lote.Cultivos?.size!!>0){

                                        for(cultivo in lote.Cultivos!!){


                                            var cultivoVerficateSave= SQLite.select()
                                                    .from(Cultivo::class.java)
                                                    .where(Cultivo_Table.Id_Remote.eq(cultivo.Id_Remote))
                                                    .and(Cultivo_Table.Estado_SincronizacionUpdate.eq(false))
                                                    .querySingle()
                                            //TODO Verifica si tiene pendiente actualizacion por sincronizar
                                            if (cultivoVerficateSave!=null){
                                                cultivo.CultivoId=cultivoVerficateSave.CultivoId
                                            }else{
                                                val last_cultivo = getLastCultivo(null)
                                                if (last_cultivo == null) {
                                                    cultivo.CultivoId = 1
                                                } else {
                                                    cultivo.CultivoId = last_cultivo.CultivoId!! + 1
                                                }
                                                cultivo.UsuarioId=usuario.Id
                                                cultivo.LoteId=lote.LoteId
                                                cultivo.NombreUnidadProductiva= item.nombre
                                                cultivo.NombreLote= lote.Nombre
                                                cultivo.EstadoSincronizacion= true
                                                cultivo.Estado_SincronizacionUpdate= true
                                                cultivo.stringFechaInicio=cultivo.getFechaStringFormatApi(cultivo.FechaIncio)
                                                cultivo.stringFechaFin=cultivo.getFechaStringFormatApi(cultivo.FechaFin)

                                                cultivo.Nombre_Tipo_Producto= if (cultivo.detalleTipoProducto!=null) cultivo.detalleTipoProducto?.tipoProducto?.Nombre else null
                                                cultivo.Nombre_Detalle_Tipo_Producto= if (cultivo.detalleTipoProducto!=null) cultivo.detalleTipoProducto?.Descripcion else null
                                                cultivo.Id_Tipo_Producto= if (cultivo.detalleTipoProducto!=null) cultivo.detalleTipoProducto?.TipoProductoId else null
                                                cultivo.Nombre_Unidad_Medida=if (cultivo.unidadMedida!=null) cultivo.unidadMedida?.Descripcion else null

                                                cultivo.save()
                                            }

                                            if(cultivo.produccions?.size!!>0){

                                                for(produccion in cultivo.produccions!!){

                                                    var produccionVerficateSave= SQLite.select()
                                                            .from(Produccion::class.java)
                                                            .where(Producto_Table.Id_Remote.eq(produccion.Id_Remote))
                                                            .and(Producto_Table.Estado_SincronizacionUpdate.eq(false))
                                                            .querySingle()

                                                    //TODO Verifica si tiene pendiente actualizacion por sincronizar
                                                    if (produccionVerficateSave!=null){
                                                        produccion.ProduccionId=produccionVerficateSave.ProduccionId
                                                    }else{
                                                        val last_productions = getLastProduccion(null)
                                                        if (last_productions == null) {
                                                            produccion.ProduccionId = 1
                                                        } else {
                                                            produccion.ProduccionId = last_productions.ProduccionId!! + 1
                                                        }

                                                        produccion.UsuarioId=usuario.Id
                                                        produccion.CultivoId=cultivo.CultivoId
                                                        produccion.Estado_Sincronizacion=true
                                                        produccion.Estado_SincronizacionUpdate=true
                                                        produccion.NombreCultivo=cultivo.Nombre
                                                        produccion.NombreLote=lote.Nombre
                                                        produccion.NombreUnidadProductiva=item.nombre
                                                        produccion.FechaInicioProduccion=produccion.getFechaDate(produccion.StringFechaInicio)
                                                        produccion.FechaFinProduccion=produccion.getFechaDate(produccion.StringFechaFin)
                                                        produccion.NombreUnidadMedida= if (produccion.unidadMedida!=null) produccion.unidadMedida?.Descripcion else null
                                                        produccion.save()
                                                    }
                                                }
                                            }



                                            if(cultivo.controlPlagas?.size!!>0){
                                                for(controlplaga in cultivo.controlPlagas!!){

                                                    var controlPlagasVerficateSave= SQLite.select()
                                                            .from(ControlPlaga::class.java)
                                                            .where(ControlPlaga_Table.Id_Remote.eq(controlplaga.Id_Remote))
                                                            .and(ControlPlaga_Table.Estado_SincronizacionUpdate.eq(false))
                                                            .querySingle()

                                                    //TODO Verifica si tiene pendiente actualizacion por sincronizar
                                                    if (controlPlagasVerficateSave!=null){
                                                        controlplaga.ControlPlagaId=controlPlagasVerficateSave.ControlPlagaId
                                                    }else{
                                                        val last_controlplaga = getLastControlPlaga(null)
                                                        if (last_controlplaga == null) {
                                                            controlplaga.ControlPlagaId = 1
                                                        } else {
                                                            controlplaga.ControlPlagaId = last_controlplaga.ControlPlagaId!! + 1
                                                        }

                                                        controlplaga.UsuarioId=usuario.Id
                                                        controlplaga.CultivoId=cultivo.CultivoId
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

                        loadTransacciones(usuario)

                    } else {
                        postEventError(RequestEventTransaccion.ERROR_EVENT, "Comprueba tu conexión a Internet")
                    }
                }
                override fun onFailure(call: Call<GetSincronizacionResponse>?, t: Throwable?) {
                    postEventError(RequestEventTransaccion.ERROR_EVENT, "Comprueba tu conexión a Internet")
                }
            })



            //Get Productos by user
            val queryProductos = Listas.queryGeneral("userId",usuario?.Id.toString())
            val callProductos = apiService?.getSyncProductos(queryProductos)
            callProductos?.enqueue(object : Callback<GetSynProductosUserResponse> {
                override fun onResponse(call: Call<GetSynProductosUserResponse>?, response: Response<GetSynProductosUserResponse>?) {
                    if (response != null && response.code() == 200) {
                        val productos = response.body()?.value as MutableList<Producto>


                        SQLite.delete<Producto>(Producto::class.java)
                                .where(Producto_Table.Estado_Sincronizacion.eq(true))
                                .and(Producto_Table.Estado_SincronizacionUpdate.eq(true))
                                .async()
                                .execute()


                        for(producto in productos){

                            var productoVerficateSave= SQLite.select()
                                    .from(Producto::class.java)
                                    .where(Producto_Table.Id_Remote.eq(producto.Id_Remote))
                                    .and(Producto_Table.Estado_SincronizacionUpdate.eq(false))
                                    .querySingle()

                            //TODO Verifica si tiene pendiente actualizacion por sincronizar
                            if (productoVerficateSave!=null){
                                producto.ProductoId=productoVerficateSave.ProductoId
                            }else{

                                val last_producto = getLastProducto(null)
                                if (last_producto == null) {
                                    producto.ProductoId = 1
                                } else {
                                    producto.ProductoId = last_producto.ProductoId!! + 1
                                }

                                producto.NombreCultivo= if(producto.Cultivo!=null)producto?.Cultivo?.Nombre else null
                                producto.NombreLote= if(producto.Cultivo!=null)producto?.Cultivo?.Lote?.Nombre else null
                                producto.NombreUnidadProductiva= if(producto.Cultivo!=null)producto?.Cultivo?.Lote?.UnidadProductiva?.nombre else null
                                producto.NombreUnidadMedidaCantidad=if(producto.UnidadMedida!= null)producto.UnidadMedida?.Descripcion else null
                                producto.NombreCalidad=if(producto.Calidad!=null)producto.Calidad?.Nombre else null
                                producto.NombreUnidadMedidaPrecio=producto.PrecioUnidadMedida
                                producto.Estado_Sincronizacion=true
                                producto.Estado_SincronizacionUpdate=true
                                producto.Usuario_Logued=usuario?.Id

                                producto.NombreDetalleTipoProducto=if(producto.Cultivo!=null)producto.Cultivo?.detalleTipoProducto?.Nombre else null

                                try {
                                    val base64String = producto?.Imagen
                                    val base64Image = base64String?.split(",".toRegex())?.dropLastWhile { it.isEmpty() }!!.toTypedArray()[1]
                                    val byte = Base64.decode(base64Image, Base64.DEFAULT)
                                    producto.blobImagen = Blob(byte)
                                }catch (ex:Exception){
                                    var ss= ex.toString()
                                    Log.d("Convert Image", "defaultValue = " + ss);
                                }

                                producto.save()
                            }
                        }
                    } else {
                        postEventError(RequestEventMainMenu.ERROR_EVENT, "Comprueba tu conexión a Internet")
                    }
                }
                override fun onFailure(call: Call<GetSynProductosUserResponse>?, t: Throwable?) {
                    postEventError(RequestEventMainMenu.ERROR_EVENT, "Comprueba tu conexión a Internet")
                }
            })



            ///ENFERMEDADES AND TRATAMIENTOS
            val enfermedades = SQLite.select().from(Enfermedad::class.java).queryList()
            if(enfermedades.size<=0){
                getListSyncEnfermedadesAndTratamiento()
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
                        postEventError(RequestEventMainMenu.ERROR_EVENT, "Comprueba tu conexión a Internet")
                    }
                }
                override fun onFailure(call: Call<CategoriaPucResponse>?, t: Throwable?) {
                    postEventError(RequestEventMainMenu.ERROR_EVENT, "Comprueba tu conexión a Internet")
                }
            })

            //Estados de  transaccion
            val estadoTransaccion = SQLite.select().from(Estado_Transaccion::class.java).queryList()
            if(estadoTransaccion.size>0){

            }else{
                val estadosTransaccion = apiService?.getEstadosTransaccion()
                estadosTransaccion?.enqueue(object : Callback<EstadoTransaccionResponse> {
                    override fun onResponse(call: Call<EstadoTransaccionResponse>?, response: Response<EstadoTransaccionResponse>?) {
                        if (response != null && response.code() == 200) {
                            val estadostransaccion: MutableList<Estado_Transaccion> = response.body()?.value!!
                            for (item in estadostransaccion) {
                                item.save()
                            }
                        } else {
                            postEventError(RequestEventMainMenu.ERROR_EVENT, "Comprueba tu conexión a Internet")
                        }
                    }
                    override fun onFailure(call: Call<EstadoTransaccionResponse>?, t: Throwable?) {
                        postEventError(RequestEventMainMenu.ERROR_EVENT, "Comprueba tu conexión a Internet")
                    }
                })

            }


        }


        //LISTAS ROL COMPRADOR
        /*-----------------------------------------------------------------------------------------------------------------*/
        else if(usuario?.RolNombre?.equals(RolResources.COMPRADOR)!!){

            //TODO Delete information in local, add new remote
            /*SQLite.delete<Unidad_Productiva>(Unidad_Productiva::class.java)
                    .async()
                    .execute()

            SQLite.delete<Lote>(Lote::class.java)
                    .async()
                    .execute()


            SQLite.delete<Cultivo>(Cultivo::class.java)
                    .async()
                    .execute()

            SQLite.delete<Produccion>(Produccion::class.java)
                    .async()
                    .execute()

            SQLite.delete<ControlPlaga>(ControlPlaga::class.java)
                    .async()
                    .execute()*/

            SQLite.delete<Producto>(Producto::class.java)
                    .where(Producto_Table.Usuario_Logued.notEq(usuario.Id))
                    .async()
                    .execute()

        }



        //LISTAS PARA AMBOS ROLES
        /*------------------------------------------------------------------------------------------------------------------*/
        //Tipos de Producto
        //val listTipoProducto: ArrayList<TipoProducto> = Listas.listaTipoProducto()

        //val tipoProducto = SQLite.select().from(TipoProducto::class.java).queryList()



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
                    postEventError(RequestEventMainMenu.ERROR_EVENT, "Comprueba tu conexión a Internet")
                }
            }
            override fun onFailure(call: Call<TipoProductoResponse>?, t: Throwable?) {
                postEventError(RequestEventMainMenu.ERROR_EVENT, "Comprueba tu conexión a Internet")
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
                        postEventError(RequestEventMainMenu.ERROR_EVENT, "Comprueba tu conexión a Internet")
                    }
                }
                override fun onFailure(call: Call<DeparmentsResponse>?, t: Throwable?) {
                    postEventError(RequestEventMainMenu.ERROR_EVENT, "Comprueba tu conexión a Internet")
                }
            })
        }



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
                    postEventError(RequestEventMainMenu.ERROR_EVENT, "Comprueba tu conexión a Internet")
                }
            }
            override fun onFailure(call: Call<CategoriaMedidaResponse>?, t: Throwable?) {
                postEventError(RequestEventMainMenu.ERROR_EVENT, "Comprueba tu conexión a Internet")
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
                    postEventError(RequestEventMainMenu.ERROR_EVENT, "Comprueba tu conexión a Internet")
                }
            }

            override fun onFailure(call: Call<CalidadProductoResponse>?, t: Throwable?) {
                postEventError(RequestEventMainMenu.ERROR_EVENT, "Comprueba tu conexión a Internet")
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
                            .and(Transaccion_Table.Estado_SincronizacionUpdate.eq(true))
                            .async()
                            .execute()

                    SQLite.delete<Tercero>(Tercero::class.java)
                            .where(Tercero_Table.Estado_Sincronizacion.eq(true))
                            .and(Tercero_Table.Estado_SincronizacionUpdate.eq(true))
                            .async()
                            .execute()

                    for (item in transacciones) {

                        var transaccionVerficateSave= SQLite.select()
                                .from(Transaccion::class.java)
                                .where(Transaccion_Table.Id_Remote.eq(item.Id_Remote))
                                .and(Transaccion_Table.Estado_SincronizacionUpdate.eq(false))
                                .querySingle()

                        //TODO Verifica si tiene pendiente actualizacion por sincronizar
                        if (transaccionVerficateSave!=null){
                            item.TransaccionId=transaccionVerficateSave.TransaccionId
                        }else{
                            val lastTransaccion = getLastTransaccion(null)
                            if (lastTransaccion == null) {
                                item.TransaccionId = 1
                            } else {
                                item.TransaccionId = lastTransaccion.TransaccionId!! + 1
                            }


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

                            val dateFechaFromatMMddyyy =item.getFechaTransacccionFormatMMddyyyy()
                            item.FechaString=dateFechaFromatMMddyyy

                            item.Valor_Unitario=if (item.Valor_Total!=null && item.Cantidad!=null)  item.Valor_Total!! / item.Cantidad?.toLong()!! else null

                            var cultivo =SQLite.select().from(Cultivo::class.java).where(Cultivo_Table.Id_Remote.eq(item.Cultivo_Id)).querySingle()
                            item.Nombre_Cultivo=cultivo?.Nombre
                            item.Nombre_Detalle_Producto_Cultivo=cultivo?.Nombre_Detalle_Tipo_Producto
                            item.Cultivo_Id=cultivo?.CultivoId




                            var terceroVerificateSave= SQLite.select()
                                    .from(Tercero::class.java)
                                    .where(Tercero_Table.Id_Remote.eq(item.TerceroId))
                                    .and(Tercero_Table.Estado_SincronizacionUpdate.eq(false))
                                    .querySingle()

                            if (terceroVerificateSave!=null){
                                item.Tercero?.TerceroId=terceroVerificateSave?.TerceroId
                                item.TerceroId=terceroVerificateSave?.TerceroId
                            }else{
                                val lastTercero = getLastTercero(null)
                                if (lastTercero == null) {
                                    item.Tercero?.TerceroId = 1
                                } else {
                                    item.Tercero?.TerceroId = lastTercero.TerceroId!! + 1
                                }

                                item.Tercero?.Estado_Sincronizacion=true
                                item.Tercero?.Estado_SincronizacionUpdate=true
                                item.TerceroId=item.Tercero?.TerceroId
                                item.Tercero?.save()

                            }

                            item.save()
                        }
                    }
                } else {
                    postEventError(RequestEventMainMenu.ERROR_EVENT, "Comprueba tu conexión a Internet")
                }
            }
            override fun onFailure(call: Call<GetSincronizacionTransacciones>?, t: Throwable?) {
                postEventError(RequestEventMainMenu.ERROR_EVENT, "Comprueba tu conexión a Internet")
            }
        })
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