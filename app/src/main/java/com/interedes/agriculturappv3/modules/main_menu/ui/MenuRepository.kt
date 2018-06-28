package com.interedes.agriculturappv3.modules.main_menu.ui

import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.drawable.Drawable
import android.preference.PreferenceManager
import android.support.v4.content.ContextCompat.startActivity
import android.util.Base64
import android.util.Log
import com.google.firebase.FirebaseException
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ServerValue
import com.google.firebase.iid.FirebaseInstanceId
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
import com.interedes.agriculturappv3.modules.models.ofertas.*
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
import com.interedes.agriculturappv3.services.Const
import com.interedes.agriculturappv3.services.api.ApiInterface
import com.interedes.agriculturappv3.services.listas.Listas
import com.interedes.agriculturappv3.services.resources.Chat_Resources
import com.interedes.agriculturappv3.services.resources.RolResources
import com.interedes.agriculturappv3.services.resources.S3Resources
import com.interedes.agriculturappv3.services.resources.Status_Chat
import com.raizlabs.android.dbflow.data.Blob
import com.raizlabs.android.dbflow.kotlinextensions.delete
import com.raizlabs.android.dbflow.kotlinextensions.save
import com.raizlabs.android.dbflow.kotlinextensions.update
import com.raizlabs.android.dbflow.sql.language.SQLite
import com.squareup.picasso.Picasso
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.io.ByteArrayOutputStream
import java.io.IOException
import java.io.InputStream
import java.math.BigDecimal
import java.math.MathContext
import java.net.HttpURLConnection
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
        mUserDBRef = Chat_Resources.mUserDBRef
        mStorageRef = FirebaseStorage.getInstance().reference.child("Photos").child("Users")
    }


    override fun verificateUserLoguedFirebaseFirebase(): FirebaseUser?
    {
        return  FirebaseAuth.getInstance().currentUser
    }

    override fun makeUserOnline(checkConection:Boolean,context: Context) {
        val userLogued=getLastUserLogued()
        if(userLogued!=null){
            val verficateLoguedFirebase=verificateUserLoguedFirebaseFirebase()
            if(checkConection){
                if(verficateLoguedFirebase==null){
                    loginFirebase(userLogued,context)
                }else{
                    makeUserOnlineSet()
                }
            }
        }
    }

    private fun makeUserOnlineSet() {
        val userStatus= mUserDBRef?.child(mCurrentUserID+"/status")
        val userLastOnlineRef= mUserDBRef?.child(mCurrentUserID+"/last_Online")
        userStatus?.setValue(Status_Chat.ONLINE)
        userStatus?.onDisconnect()?.setValue(Status_Chat.OFFLINE)
        userLastOnlineRef?.onDisconnect()?.setValue(ServerValue.TIMESTAMP);

        //Status FCM
        val userTokenMessaginStatus= mUserDBRef?.child(mCurrentUserID+"/statusTokenFcm")

        userTokenMessaginStatus?.setValue(true)
    }

    override fun makeUserOffline(checkConection:Boolean,context: Context) {
        var verficateLoguedFirebase=verificateUserLoguedFirebaseFirebase()
        if(verficateLoguedFirebase!=null){
                makeUserOfflineSet()
        }
    }

    private fun makeUserOfflineSet() {
        val userStatus= mUserDBRef?.child(mCurrentUserID+"/status")
        val userLastOnlineRef= mUserDBRef?.child(mCurrentUserID+"/last_Online")
        userStatus?.setValue(Status_Chat.OFFLINE)
        userLastOnlineRef?.setValue(ServerValue.TIMESTAMP);
    }

    override fun loginFirebase(usuario:Usuario,context: Context)
    {
        mAuth?.signInWithEmailAndPassword(usuario?.Email!!, usuario?.Contrasena!!)?.addOnCompleteListener({ task ->
            if (task.isSuccessful) {
                val mCurrentUserID =task.result.user.uid
                usuario.IdFirebase=mCurrentUserID
                usuario.save()

                resetTokenFCM(context)
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

    //region TOKEN
    private fun resetTokenFCM(context: Context) {
        try {
            // Check for current token
            val originalToken = getTokenFromPrefs(context)
            Log.d("TAG", "Token before deletion: $originalToken")
            // Resets Instance ID and revokes all tokens.
            Thread(Runnable {
                try {
                    FirebaseInstanceId.getInstance().deleteInstanceId()
                    // Clear current saved token
                    saveTokenToPrefs("",context)

                    // Check for success of empty token
                    val tokenCheck = getTokenFromPrefs(context)
                    Log.d("TAG", "Token deleted. Proof: $tokenCheck")

                    // Now manually call onTokenRefresh()
                    Log.d("TAG", "Getting new token")
                    FirebaseInstanceId.getInstance().token

                } catch (e: IOException) {
                    e.printStackTrace()
                }
            }).start()
        } catch (e: IOException) {
            e.printStackTrace()
        }
    }

    private fun saveTokenToPrefs(_token:String?,context: Context )
    {
        // Access Shared Preferences
        //var preferences = PreferenceManager.getDefaultSharedPreferences(this);
        //var editor = preferences.edit();
        val preferences = PreferenceManager.getDefaultSharedPreferences(context)
        // Save to SharedPreferences
        //editor.putString("registration_id", _token);
        //editor.apply();
        preferences.edit().putString(Const.FIREBASE_TOKEN, _token).apply()
    }

    private fun  getTokenFromPrefs(context:Context):String?
    {
        val preferences = PreferenceManager.getDefaultSharedPreferences(context);
        return preferences.getString(Const.FIREBASE_TOKEN, null);
    }



    //endregion

    override fun logOut(usuario: Usuario?) {
        try {
            mAuth = FirebaseAuth.getInstance()
            if (mAuth?.currentUser != null) {
                mAuth?.signOut()

                var currentUserID = mAuth?.currentUser?.uid
                if(currentUserID==null){
                    currentUserID=getLastUserLogued()?.IdFirebase
                }

                val userTokenMessaginStatus= mUserDBRef?.child(currentUserID+"/statusTokenFcm")

                userTokenMessaginStatus?.setValue(false)
            }
            usuario?.UsuarioRemembered = false
            //usuario?.AccessToken = null
            usuario?.save()
        } catch (e: Exception) {
           // postEvent(RequestEvent.ERROR_EVENT, e.message.toString())
        }
    }

    override fun syncQuantityData(automatic:Boolean) {
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


        if(automatic){
            var quantitySync= QuantitySync(registerTotal.toLong(),updatesTotal.toLong())
            postEventOkQuntitySync(RequestEventMainMenu.SYNC_RESUME_AUTOMATIC,quantitySync)
        }else{
            var quantitySync= QuantitySync(registerTotal.toLong(),updatesTotal.toLong())
            postEventOkQuntitySync(RequestEventMainMenu.SYNC_RESUME,quantitySync)
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


                            /*if(item?.Insumo?.Imagen!=null || item?.Insumo?.Imagen!=""){
                                try {
                                    val base64String = item?.Insumo?.Imagen
                                    val base64Image = base64String?.split(",".toRegex())?.dropLastWhile { it.isEmpty() }!!.toTypedArray()[1]
                                    val byte = Base64.decode(base64Image, Base64.DEFAULT)
                                    item.Insumo?.blobImagen = Blob(byte)
                                }catch (ex:Exception){
                                    var ss= ex.toString()
                                    Log.d("Convert Image", "defaultValue = " + ss);
                                }
                            }*/

                            if(item.Insumo?.Laboratorio!=null){
                                item.Insumo?.NombreLaboratorio=item.Insumo?.Laboratorio?.Nombre
                                item.Insumo?.Laboratorio?.save()
                            }

                            if(item.Insumo?.TipoInsumo!=null){
                                item.Insumo?.NombreTipoInsumo=item.Insumo?.TipoInsumo?.Nombre
                                item.Insumo?.TipoInsumo?.save()
                            }
                            item.Insumo!!.save()
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

                    postEventOk(RequestEventMainMenu.SYNC_FOTOS_INSUMOS)
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

     fun getLastOferta(): Oferta? {
        val lastOferta = SQLite.select().from(Oferta::class.java).where().orderBy(Oferta_Table.Oferta_Id, false).querySingle()
        return lastOferta
    }

     fun getLastDetalleOferta(): DetalleOferta? {
        val lastDetalleOferta = SQLite.select().from(DetalleOferta::class.java).where().orderBy(DetalleOferta_Table.Detalle_Oferta_Id, false).querySingle()
        return lastDetalleOferta
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
                    .where(Producto_Table.userId.eq(usuario?.Id)).orderBy(Producto_Table.ProductoId, false).querySingle()
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

            var users=SQLite.select().from(Usuario::class.java).where(Usuario_Table.RolNombre.eq(RolResources.COMPRADOR)).queryList()
            for (user in users){
                user.delete()
            }

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
                            val unidadProductivaVerficateSave= SQLite.select()
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
                                    val loteVerficateSave= SQLite.select()
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

                                        var coordenadas =lote.Localizacion

                                        if(coordenadas!=null){
                                            if(coordenadas.isNotEmpty()){
                                                val separated = coordenadas?.split("/".toRegex())?.dropLastWhile { it.isEmpty() }?.toTypedArray()
                                                var latitud= separated!![0].toDoubleOrNull() // this will contain "Fruit"
                                                var longitud=separated!![1].toDoubleOrNull() // this will contain " they taste good"
                                                lote.Latitud=latitud
                                                lote.Longitud=longitud
                                                lote.Coordenadas=coordenadas
                                            }
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


                                            val cultivoVerficateSave= SQLite.select()
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

                                                    val produccionVerficateSave= SQLite.select()
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

                                                    val controlPlagasVerficateSave= SQLite.select()
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
                        postEventError(RequestEventMainMenu.ERROR_EVENT, "Comprueba tu conexión a Internet")
                    }
                }
                override fun onFailure(call: Call<GetSincronizacionResponse>?, t: Throwable?) {
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

            //Limpiar BD con informacion de un productor
            //TODO Delete information in local, add new remote
            SQLite.delete<Oferta>(Oferta::class.java)
                    .async()
                    .execute()

            SQLite.delete<DetalleOferta>(DetalleOferta::class.java)
                    .async()
                    .execute()

            loadOfertas(usuario)

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



        //Calidades de Producto
        val callEstadoOfertas = apiService?.getEstadosOfertas()
        callEstadoOfertas?.enqueue(object : Callback<EstadoOfertaResponse> {
            override fun onResponse(call: Call<EstadoOfertaResponse>?, response: Response<EstadoOfertaResponse>?) {
                if (response != null && response.code() == 200) {
                    val listEstadosOfertas = response.body()?.value as MutableList<EstadoOferta>
                    for (item in listEstadosOfertas) {
                        item.save()
                    }
                } else {
                    postEventError(RequestEventMainMenu.ERROR_EVENT, "Comprueba tu conexión a Internet")
                }
            }
            override fun onFailure(call: Call<EstadoOfertaResponse>?, t: Throwable?) {
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
                            .and(Transaccion_Table.UsuarioId.eq(usuario?.Id))
                            .and(Transaccion_Table.Estado_SincronizacionUpdate.eq(true))
                            .async()
                            .execute()

                    SQLite.delete<Tercero>(Tercero::class.java)
                            .where(Tercero_Table.Estado_Sincronizacion.eq(true))
                            .and(Tercero_Table.Usuario_Id.eq(usuario?.Id))
                            .and(Tercero_Table.Estado_SincronizacionUpdate.eq(true))
                            .async()
                            .execute()

                    for (item in transacciones) {

                        val transaccionVerficateSave= SQLite.select()
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
                            val fechaDate=item.getFechaDate(item.FechaString)
                            item.Fecha_Transaccion=fechaDate

                            val dateFechaFromatMMddyyy =item.getFechaTransacccionFormatMMddyyyy()
                            item.FechaString=dateFechaFromatMMddyyy

                            item.Valor_Unitario=if (item.Valor_Total!=null && item.Cantidad!=null)  item.Valor_Total!! / item.Cantidad?.toLong()!! else null

                            val cultivo =SQLite.select().from(Cultivo::class.java).where(Cultivo_Table.Id_Remote.eq(item.Cultivo_Id)).querySingle()
                            item.Nombre_Cultivo=cultivo?.Nombre
                            item.Nombre_Detalle_Producto_Cultivo=cultivo?.Nombre_Detalle_Tipo_Producto
                            item.Cultivo_Id=cultivo?.CultivoId

                            val terceroVerificateSave= SQLite.select()
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

                    loadProductos(usuario)
                } else {
                    postEventError(RequestEventMainMenu.ERROR_EVENT, "Comprueba tu conexión a Internet")
                }
            }
            override fun onFailure(call: Call<GetSincronizacionTransacciones>?, t: Throwable?) {
                postEventError(RequestEventMainMenu.ERROR_EVENT, "Comprueba tu conexión a Internet")
            }
        })
    }


    fun loadProductos(usuario:Usuario?){
        //Get Productos by user
        val queryProductos = Listas.queryGeneral("userId",usuario?.Id.toString())
        val callProductos = apiService?.getSyncProductos(queryProductos)
        callProductos?.enqueue(object : Callback<GetSynProductosUserResponse> {
            override fun onResponse(call: Call<GetSynProductosUserResponse>?, response: Response<GetSynProductosUserResponse>?) {
                if (response != null && response.code() == 200) {
                    val productos = response.body()?.value as MutableList<Producto>


                    SQLite.delete<Producto>(Producto::class.java)
                            .where(Producto_Table.userId.eq(usuario?.Id))
                            .and(Producto_Table.Estado_Sincronizacion.eq(true))
                            .and(Producto_Table.Estado_SincronizacionUpdate.eq(true))
                            .async()
                            .execute()



                    for(producto in productos){

                        val cultivo= SQLite.select().from(Cultivo::class.java).where(Cultivo_Table.Id_Remote.eq(producto.cultivoId)).querySingle()


                        val productoVerficateSave= SQLite.select()
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

                            producto.cultivoId=if(cultivo!=null)cultivo?.CultivoId else null

                            producto.NombreDetalleTipoProducto=if(producto.Cultivo!=null)producto.Cultivo?.detalleTipoProducto?.Nombre else null
                            /*
                            try {
                                val base64String = producto?.Imagen
                                val base64Image = base64String?.split(",".toRegex())?.dropLastWhile { it.isEmpty() }!!.toTypedArray()[1]
                                val byte = Base64.decode(base64Image, Base64.DEFAULT)
                                producto.blobImagen = Blob(byte)
                            }catch (ex:Exception){
                                var ss= ex.toString()
                                Log.d("Convert Image", "defaultValue = " + ss);
                            }

                            */





                            producto.save()
                        }
                    }


                   // if(producto.Imagen!=null){
                     //   if(producto.Imagen!!.contains("Productos")){

                           /* try{
                                Picasso.get()
                                        //.load(S3Resources.RootImage+""+producto?.Imagen)
                                        .load("https://s3.amazonaws.com/agriculturapp/Productos/7ea3bcfc-4e4a-484d-9727-31f106b998297e0c69fe-1753-44b5-ac80-e939afd27d2e.jpg")
                                        .into(object : com.squareup.picasso.Target {
                                            override fun onPrepareLoad(placeHolderDrawable: Drawable?) {
                                            }
                                            override fun onBitmapFailed(e: java.lang.Exception?, errorDrawable: Drawable?) {
                                                var error= e.toString()
                                            }
                                            override fun onBitmapLoaded(bitmap: Bitmap?, from: Picasso.LoadedFrom?) {
                                                if(bitmap!=null){
                                                    try {
                                                        var imgByteArray=convertBitmapToByte(bitmap!!)
                                                        /* val base64String = producto?.Imagen
                                                         val base64Image = base64String?.split(",".toRegex())?.dropLastWhile { it.isEmpty() }!!.toTypedArray()[1]
                                                         val byte = Base64.decode(base64Image, Base64.DEFAULT)*/
                                                        // producto.blobImagen = Blob(imgByteArray)
                                                    }catch (ex:Exception){
                                                        var ss= ex.toString()
                                                        Log.d("Convert Image", "defaultValue = " + ss);
                                                    }
                                                }

                                            }

                                        })
                            }catch (ex:Exception){
                                var message=ex.toString()
                            }

                            */

                    //    }
                  //  }
                    loadOfertas(usuario)
                } else {
                    postEventError(RequestEventMainMenu.ERROR_EVENT, "Comprueba tu conexión a Internet")
                }
            }
            override fun onFailure(call: Call<GetSynProductosUserResponse>?, t: Throwable?) {
                postEventError(RequestEventMainMenu.ERROR_EVENT, "Comprueba tu conexión a Internet")
            }
        })
    }

    fun convertBitmapToByte(bitmapCompressed: Bitmap): ByteArray? {
        val stream = ByteArrayOutputStream()
        bitmapCompressed.compress(Bitmap.CompressFormat.JPEG, 80, stream)
        return stream.toByteArray()
        //return BitmapFactory.decodeByteArray(byteFormat, 0, byteFormat.size)
    }

    private fun loadOfertas(usuario: Usuario?) {
        var queryOfertas = ""
        var callOfertas: Call<OfertaResponse>? = null
        val orderDEsc = Listas.queryOrderByDesc("Id")
        if (usuario?.RolNombre.equals(RolResources.COMPRADOR)) {
            queryOfertas = Listas.queryGeneralWithContains("Oferta","UsuarioId", usuario?.Id.toString())
            callOfertas = apiService?.getOfertasComprador(queryOfertas, orderDEsc)
        } else {
            queryOfertas = Listas.queryGeneralWithContains("Oferta","usuarioto", usuario?.Id.toString())
            callOfertas = apiService?.getOfertasProductor(queryOfertas, orderDEsc)
        }

        callOfertas?.enqueue(object : Callback<OfertaResponse> {
            override fun onResponse(call: Call<OfertaResponse>?, response: Response<OfertaResponse>?) {
                if (response != null && response.code() == 200) {
                    if (usuario?.RolNombre.equals(RolResources.PRODUCTOR)) {
                        val listOferta = SQLite.select().from(Oferta::class.java).where(Oferta_Table.UsuarioTo.eq(usuario?.Id)).queryList()
                        for (oferta in listOferta) {
                            SQLite.delete<DetalleOferta>(DetalleOferta::class.java)
                                    .where(DetalleOferta_Table.OfertasId.eq(oferta.Oferta_Id))
                                    .async()
                                    .execute()
                            oferta.delete()
                        }
                    }

                    val detalleOfertas = response.body()?.value as MutableList<DetalleOferta>
                    for (detalleoferta in detalleOfertas) {

                        val ofertaVerficateSave = SQLite.select()
                                .from(Oferta::class.java)
                                .where(Oferta_Table.Id_Remote.eq(detalleoferta.Oferta?.Id_Remote))
                                .querySingle()
                        //TODO Verifica si tiene pendiente actualizacion por sincronizar
                        if (ofertaVerficateSave != null) {
                            detalleoferta.Oferta?.Oferta_Id = ofertaVerficateSave.Oferta_Id
                        } else {
                            val last_oferta = getLastOferta()
                            if (last_oferta == null) {
                                detalleoferta.Oferta?.Oferta_Id = 1
                            } else {
                                detalleoferta.Oferta?.Oferta_Id = last_oferta.Oferta_Id!! + 1
                            }
                        }
                        detalleoferta.Oferta!!.CreatedOnLocal = detalleoferta.Oferta!!.getFechaDate(detalleoferta.Oferta!!.CreatedOn)
                        detalleoferta.Oferta!!.UpdatedOnLocal = detalleoferta.Oferta!!.getFechaDate(detalleoferta.Oferta!!.UpdatedOn)
                        detalleoferta.Oferta!!.Nombre_Estado_Oferta = if (detalleoferta.Oferta!!.Estado_Oferta != null) detalleoferta.Oferta!!.Estado_Oferta?.Nombre else null
                        detalleoferta.Oferta!!.save()


                        val detalleOfertaVerficateSave = SQLite.select()
                                .from(DetalleOferta::class.java)
                                .where(DetalleOferta_Table.Id_Remote.eq(detalleoferta.Id_Remote))
                                .querySingle()
                        if (detalleOfertaVerficateSave != null) {
                            detalleoferta.Detalle_Oferta_Id = detalleOfertaVerficateSave.Detalle_Oferta_Id
                        } else {
                            val last_detalle_oferta = getLastDetalleOferta()
                            if (last_detalle_oferta == null) {
                                detalleoferta?.Detalle_Oferta_Id = 1
                            } else {
                                detalleoferta?.Detalle_Oferta_Id = last_detalle_oferta.Detalle_Oferta_Id!! + 1
                            }
                        }
                        detalleoferta.NombreUnidadMedidaPrecio = if (detalleoferta.UnidadMedida != null) detalleoferta.UnidadMedida?.Descripcion else null
                        detalleoferta.OfertasId = detalleoferta.Oferta?.Oferta_Id
                        detalleoferta.Detalle_Oferta_Id = detalleOfertaVerficateSave?.Detalle_Oferta_Id
                        detalleoferta.save()

                        if (usuario?.RolNombre.equals(RolResources.COMPRADOR)) {
                            //UserTo
                            val usuario = detalleoferta?.Producto?.Cultivo?.Lote?.UnidadProductiva?.Usuario
                            if (usuario != null) {
                                usuario.save()
                            }

                            //TODO Unidades Productivas
                            val unidaProductiva = detalleoferta?.Producto?.Cultivo?.Lote?.UnidadProductiva
                            if (unidaProductiva != null) {
                                val unidadProductivaVerficateSave = SQLite.select()
                                        .from(Unidad_Productiva::class.java)
                                        .where(Unidad_Productiva_Table.Id_Remote.eq(unidaProductiva.Id_Remote))
                                        .querySingle()
                                //TODO Verifica si ya existe
                                if (unidadProductivaVerficateSave != null) {
                                    unidaProductiva.Unidad_Productiva_Id = unidadProductivaVerficateSave?.Unidad_Productiva_Id
                                } else {
                                    val last_up = getLastUp(null)
                                    if (last_up == null) {
                                        unidaProductiva.Unidad_Productiva_Id = 1
                                    } else {
                                        unidaProductiva.Unidad_Productiva_Id = last_up.Unidad_Productiva_Id!! + 1
                                    }
                                }
                                unidaProductiva.UsuarioId = usuario?.Id
                                unidaProductiva.Nombre_Ciudad = if (unidaProductiva.Ciudad != null) unidaProductiva.Ciudad?.Nombre else null
                                unidaProductiva.Nombre_Unidad_Medida = if (unidaProductiva.UnidadMedida != null) unidaProductiva.UnidadMedida?.Descripcion else null
                                unidaProductiva.Nombre_Departamento = if (unidaProductiva.Ciudad != null) unidaProductiva.Ciudad?.Departamento?.Nombre else null
                                unidaProductiva.save()
                            }


                            //TODO Lote
                            val lote = detalleoferta?.Producto?.Cultivo?.Lote
                            if (lote != null) {
                                val loteVerficateSave = SQLite.select()
                                        .from(Lote::class.java)
                                        .where(Lote_Table.Id_Remote.eq(lote.Id_Remote))
                                        .querySingle()
                                //TODO Verifica si ya existe
                                if (loteVerficateSave != null) {
                                    lote.LoteId = loteVerficateSave.LoteId
                                } else {
                                    val last_lote = getLastLote(null)
                                    if (last_lote == null) {
                                        lote.LoteId = 1
                                    } else {
                                        lote.LoteId = last_lote.LoteId!! + 1
                                    }
                                }

                                val coordenadas = lote.Localizacion
                                if (coordenadas != null) {
                                    val separated = coordenadas?.split("/".toRegex())?.dropLastWhile { it.isEmpty() }?.toTypedArray()
                                    var latitud = separated!![0].toDoubleOrNull() // this will contain "Fruit"
                                    var longitud = separated!![1].toDoubleOrNull() // this will contain " they taste good"
                                    lote.Latitud = latitud
                                    lote.Longitud = longitud
                                    lote.Coordenadas = coordenadas
                                }

                                lote.Unidad_Productiva_Id = unidaProductiva?.Unidad_Productiva_Id
                                //lote.Nombre_Unidad_Medida= if (lote.UnidadMedida!=null) lote.UnidadMedida?.Descripcion else null
                                lote.Nombre_Unidad_Productiva = unidaProductiva?.nombre
                                lote.Nombre = if (lote.Nombre == null) "" else lote.Nombre
                                lote.Descripcion = if (lote.Descripcion == null) "" else lote.Descripcion
                                // lote.EstadoSincronizacion=true
                                // lote.Estado_SincronizacionUpdate=true
                                lote.save()
                            }

                            //TODO Cultivo
                            val cultivo = detalleoferta?.Producto?.Cultivo
                            if (cultivo != null) {
                                val cultivoVerficateSave = SQLite.select()
                                        .from(Cultivo::class.java)
                                        .where(Cultivo_Table.Id_Remote.eq(cultivo?.Id_Remote))
                                        .querySingle()
                                //TODO Verifica si tiene pendiente actualizacion por sincronizar
                                if (cultivoVerficateSave != null) {
                                    cultivo.CultivoId = cultivoVerficateSave.CultivoId
                                } else {
                                    val last_cultivo = getLastCultivo(null)
                                    if (last_cultivo == null) {
                                        cultivo.CultivoId = 1
                                    } else {
                                        cultivo.CultivoId = last_cultivo.CultivoId!! + 1
                                    }
                                }

                                cultivo.LoteId = lote?.LoteId
                                cultivo.NombreUnidadProductiva = unidaProductiva?.nombre
                                cultivo.NombreLote = lote?.Nombre
                                cultivo.EstadoSincronizacion = true
                                cultivo.Estado_SincronizacionUpdate = true
                                cultivo.stringFechaInicio = cultivo.getFechaStringFormatApi(cultivo.FechaIncio)
                                cultivo.stringFechaFin = cultivo.getFechaStringFormatApi(cultivo.FechaFin)

                                //cultivo.Nombre_Tipo_Producto= if (detalleTipoProducto.tipoProducto!=null) detalleTipoProducto?.tipoProducto?.Nombre else null
                                //cultivo.Nombre_Detalle_Tipo_Producto=detalleTipoProducto.Nombre
                                //cultivo.Id_Tipo_Producto= detalleTipoProducto.TipoProductoId
                                //cultivo.Nombre_Unidad_Medida=if (cultivo.unidadMedida!=null) cultivo.unidadMedida?.Descripcion else null
                                //cultivo.EstadoSincronizacion=true
                                //cultivo.Estado_SincronizacionUpdate=true
                                cultivo.save()
                            }

                            //TODO Producto
                            val producto = detalleoferta?.Producto
                            if (producto != null) {
                                val productoVerficateSave = SQLite.select()
                                        .from(Producto::class.java)
                                        .where(Producto_Table.Id_Remote.eq(producto.Id_Remote))
                                        .querySingle()

                                if (productoVerficateSave != null) {
                                    producto.ProductoId = productoVerficateSave.ProductoId
                                } else {
                                    val last_producto = getLastProducto(null)
                                    if (last_producto == null) {
                                        producto.ProductoId = 1
                                    } else {
                                        producto.ProductoId = last_producto.ProductoId!! + 1
                                    }
                                }
                                producto.EmailProductor = usuario?.Email
                                producto.NombreProductor = "${usuario?.Nombre} ${usuario?.Apellidos}"
                                producto.CodigoUp = unidaProductiva?.Unidad_Productiva_Id.toString()
                                producto.Ciudad = unidaProductiva?.Nombre_Ciudad
                                producto.Departamento = unidaProductiva?.Nombre_Departamento
                                //producto.TipoProductoId=detalleTipoProducto.TipoProductoId
                                producto.NombreCultivo = cultivo?.Nombre
                                producto.NombreLote = if (lote != null) lote.Nombre else null
                                producto.NombreUnidadProductiva = if (unidaProductiva != null) unidaProductiva.nombre else null
                                producto.NombreUnidadMedidaCantidad = if (producto.UnidadMedida != null) producto.UnidadMedida?.Descripcion else null
                                producto.NombreCalidad = if (producto.Calidad != null) producto.Calidad?.Nombre else null
                                producto.NombreUnidadMedidaPrecio = producto.PrecioUnidadMedida
                                producto.Usuario_Logued = usuario?.Id
                                //producto.NombreDetalleTipoProducto=detalleTipoProducto.Nombre
                                //producto.Estado_Sincronizacion=true
                                //producto.Estado_SincronizacionUpdate=true
                                try {
                                    val base64String = producto?.Imagen
                                    val base64Image = base64String?.split(",".toRegex())?.dropLastWhile { it.isEmpty() }!!.toTypedArray()[1]
                                    val byte = Base64.decode(base64Image, Base64.DEFAULT)
                                    producto.blobImagen = Blob(byte)
                                } catch (ex: Exception) {
                                    var ss = ex.toString()
                                    Log.d("Convert Image", "defaultValue = " + ss);
                                }
                                producto.save()
                            }
                        } else if (usuario?.RolNombre.equals(RolResources.PRODUCTOR)) {
                            if (detalleoferta.Oferta?.Usuario != null) {
                                /*if(oferta.Usuario?.Fotopefil!=null){
                                    try {
                                        val base64String = oferta.Usuario?.Fotopefil
                                        val base64Image = base64String?.split(",".toRegex())?.dropLastWhile { it.isEmpty() }!!.toTypedArray()[1]
                                        val byte = Base64.decode(base64Image, Base64.DEFAULT)
                                        oferta.Usuario?.blobImagenUser = Blob(byte)
                                    }catch (ex:Exception){
                                        var ss= ex.toString()
                                        Log.d("Convert Image", "defaultValue = " + ss);
                                    }
                                }*/
                                detalleoferta.Oferta?.Usuario!!.save()
                            }
                        }

                    }
                } else {
                    postEventError(RequestEventMainMenu.ERROR_EVENT, "Comprueba tu conexión a Internet")
                }
            }

            override fun onFailure(call: Call<OfertaResponse>?, t: Throwable?) {
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