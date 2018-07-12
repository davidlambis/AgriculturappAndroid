package com.interedes.agriculturappv3.services.jobs.repository

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Color
import android.media.AudioAttributes
import android.media.RingtoneManager
import android.os.Build
import android.os.Environment
import android.support.annotation.RequiresApi
import android.support.v4.app.NotificationCompat
import android.support.v4.app.NotificationManagerCompat
import android.support.v4.content.ContextCompat
import android.util.Log
import com.interedes.agriculturappv3.R
import com.interedes.agriculturappv3.config.DataSource
import com.interedes.agriculturappv3.libs.EventBus
import com.interedes.agriculturappv3.libs.GreenRobotEventBus
import com.interedes.agriculturappv3.libs.eventbus_rx.Rx_Bus
import com.interedes.agriculturappv3.modules.main_menu.ui.events.RequestEventMainMenu
import com.interedes.agriculturappv3.modules.models.Notification.NotificationLocal
import com.interedes.agriculturappv3.modules.models.Notification.NotificationLocal_Table
import com.interedes.agriculturappv3.modules.models.control_plaga.ControlPlaga
import com.interedes.agriculturappv3.modules.models.control_plaga.ControlPlaga_Table
import com.interedes.agriculturappv3.modules.models.cultivo.Cultivo
import com.interedes.agriculturappv3.modules.models.cultivo.Cultivo_Table
import com.interedes.agriculturappv3.modules.models.insumos.Insumo
import com.interedes.agriculturappv3.modules.models.lote.Lote
import com.interedes.agriculturappv3.modules.models.lote.Lote_Table
import com.interedes.agriculturappv3.modules.models.ofertas.EstadoOferta
import com.interedes.agriculturappv3.modules.models.plagas.Enfermedad
import com.interedes.agriculturappv3.modules.models.plagas.EnfermedadResponseApi
import com.interedes.agriculturappv3.modules.models.plagas.Enfermedad_Table
import com.interedes.agriculturappv3.modules.models.plagas.FotoEnfermedad
import com.interedes.agriculturappv3.modules.models.produccion.Produccion
import com.interedes.agriculturappv3.modules.models.produccion.Produccion_Table
import com.interedes.agriculturappv3.modules.models.producto.Producto
import com.interedes.agriculturappv3.modules.models.producto.Producto_Table
import com.interedes.agriculturappv3.modules.models.sincronizacion.QuantitySync
import com.interedes.agriculturappv3.modules.models.tratamiento.Tratamiento
import com.interedes.agriculturappv3.modules.models.tratamiento.TratamientoResponse
import com.interedes.agriculturappv3.modules.models.tratamiento.calificacion.Calificacion_Tratamiento
import com.interedes.agriculturappv3.modules.models.tratamiento.calificacion.Calificacion_Tratamiento_Table
import com.interedes.agriculturappv3.modules.models.unidad_productiva.Unidad_Productiva
import com.interedes.agriculturappv3.modules.models.unidad_productiva.Unidad_Productiva_Table
import com.interedes.agriculturappv3.modules.models.usuario.Usuario
import com.interedes.agriculturappv3.modules.models.usuario.Usuario_Table
import com.interedes.agriculturappv3.modules.models.ventas.Transaccion
import com.interedes.agriculturappv3.modules.models.ventas.Transaccion_Table
import com.interedes.agriculturappv3.modules.productor.ui.main_menu.MenuMainActivity
import com.interedes.agriculturappv3.services.api.ApiInterface
import com.interedes.agriculturappv3.services.resources.*
import com.krishna.fileloader.FileLoader
import com.krishna.fileloader.listener.FileRequestListener
import com.krishna.fileloader.pojo.FileResponse
import com.krishna.fileloader.request.FileLoadRequest
import com.raizlabs.android.dbflow.config.FlowManager
import com.raizlabs.android.dbflow.data.Blob
import com.raizlabs.android.dbflow.kotlinextensions.save
import com.raizlabs.android.dbflow.sql.language.SQLite
import com.raizlabs.android.dbflow.structure.database.transaction.FastStoreModelTransaction
import id.zelory.compressor.Compressor
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.io.ByteArrayOutputStream
import java.io.File
import java.text.SimpleDateFormat
import java.util.*

class JobSyncRepository: IMainViewJob.Repository {

    //List
    private var listInsmo: List<Insumo>? = null
    private var listFotoEnfermedad: List<FotoEnfermedad>? = null

    //Folder Privates
    private val DIR_PLAGAS = "DIR_PLAGAS"
    private val DIR_FOTO_PERFIL = "DIR_FOTO_PERFIL"
    private val DIR_INSUMOS = "DIR_INSUMOS"
    private val TAG_FIREBASE = "FIREBASE UIID"

    //Folders Public
    private val DIR_PLAGAS_PUBLIC = "Enfermedades"
    private val DIR_INSUMO_PUBLIC = "Insumos"
    private val DIR_AGRICULTUR_APP_PUBLIC = "AgriculturApp"

    //Services
    var apiService: ApiInterface? = null
    var eventBus: EventBus? = null

    //Notifications
    private var notificationManager: NotificationManager? = null
    private val ADMIN_CHANNEL_ID = "plagas_channel"
    init {
        eventBus = GreenRobotEventBus()
        apiService = ApiInterface.create()
    }

    fun getLastUserLogued(): Usuario? {
        val usuarioLogued = SQLite.select().from(Usuario::class.java).where(Usuario_Table.UsuarioRemembered.eq(true)).querySingle()
        return usuarioLogued
    }

    //region CHAT
    override fun updateUserStatus() {
        val userLogued= getLastUserLogued()
        if(userLogued!=null){
            val uid= userLogued.IdFirebase
            if(uid!=""){
                Log.d(TAG_FIREBASE, "FIREBASE SERVICE SECOND PLANE: $uid")
                Chat_Resources.mUserDBRef.child("$uid/status").setValue(Status_Chat.ONLINE)
                //Chat_Resources.Companion.getMUserDBRef().child(uid+"/last_Online").setValue(ServerValue.TIMESTAMP);
                Chat_Resources.mUserDBRef.child("$uid/status").onDisconnect().setValue(Status_Chat.OFFLINE)
            }
        }
    }
    //endregion

    //region FOTO PERFIL

    override fun syncFotoPerfilUserLogued(context: Context){
        getFotoPerfil(context)
    }

    private fun getFotoPerfil(context:Context){
        val user = SQLite.select().from(Usuario::class.java).where(Usuario_Table.UsuarioRemembered.eq(true)).querySingle()
        if(user!=null){
            if(user.Fotopefil!=null){
                FileLoader.with(context)
                        .load(S3Resources.RootImage+"${user.Fotopefil}",false) //2nd parameter is optioal, pass true to force load from network
                        //.fromDirectory("test4", FileLoader.DIR_EXTERNAL_PUBLIC)
                        .fromDirectory(DIR_FOTO_PERFIL, FileLoader.DIR_INTERNAL)
                        .asFile( object: FileRequestListener<File> {
                            override fun onLoad(request: FileLoadRequest?, response: FileResponse<File>?) {
                                val loadedFile = response?.getBody();
                                if(loadedFile?.length()!!>0){
                                    val compressedImage = Compressor(context)
                                            .setMaxWidth(300)
                                            .setMaxHeight(300)
                                            .setQuality(75)
                                            //.setCompressFormat(Bitmap.CompressFormat.WEBP)
                                            //.setQuality(80)
                                            .setCompressFormat(Bitmap.CompressFormat.PNG)
                                            .compressToBitmap(loadedFile)

                                    val bitmap= convertBitmapToByte(compressedImage)
                                    user.blobImagenUser = Blob(bitmap)
                                    user.save()
                                }

                                Rx_Bus.publish(user)
                                FileLoader.deleteWith (context) .fromDirectory ( DIR_FOTO_PERFIL , FileLoader.DIR_INTERNAL) .deleteAllFiles ();
                                Log.d("SYNC DATA", "Foto perfil Loaded" )
                            }
                            override fun onError(request: FileLoadRequest?, error : Throwable?) {
                                Log.d("SYNC DATA", error.toString() )
                                getAllPlagasFoto(0,context)
                            }
                        })
            }
        }
    }

    //endregion

    //region CONTROL PLAGAS
    override fun checkControlPlagas(context: Context) {
        val controlPlagas= SQLite.select().from(ControlPlaga::class.java).where(ControlPlaga_Table.EstadoErradicacion.eq(false)).queryList()
        val date= Calendar.getInstance().time
        val dateNow = SimpleDateFormat("dd/MM/yyyy")
        val dateNowFormat=dateNow.format(date)

        var isPending=false
        for(item in controlPlagas){
            if(item.getFechaAplicacionFormat().equals(dateNowFormat)){
                //isPending=true
                //if(isPending){

                    var message = ""

                    val cultivo =SQLite.select().from(Cultivo::class.java).where(Cultivo_Table.CultivoId.eq(item.CultivoId)).querySingle()
                    if(cultivo!=null){
                        message= String.format(context.getString(R.string.description_control_pending),cultivo.Nombre)
                    }else{
                        message=context.getString(R.string.description_control_pending_cultivo_empty)
                    }

                    if (notificationManager == null) {
                        notificationManager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
                        // notificationManager = NotificationManagerCompat.from(this)
                    }
                    //var intent:Intent?=null
                    val intent = Intent(context, MenuMainActivity::class.java)
                    intent.putExtra(TagNavigationResources.TAG_NAVIGATE_CONTROL_PLAGAS,TagNavigationResources.NAVIGATE_CONTROL_PLAGAS)

                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                        val ticker = "Ver"
                        val notificationId = Random().nextInt(60000)

                        val builder: NotificationCompat.Builder
                        //intent.putExtra(TagSmsResources.PHONE_NUMBER,smsAddress)
                        //intent.putExtra(TagSmsResources.CONTACT_NAME,messageAdress)
                        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
                        val pendingIntent: PendingIntent
                        setupChannels(context)
                        builder = NotificationCompat.Builder(context, ADMIN_CHANNEL_ID)
                        //intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_SINGLE_TOP
                        intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK
                        pendingIntent = PendingIntent.getActivity(context, notificationId+1, intent, PendingIntent.FLAG_ONE_SHOT)
                        builder.setContentTitle(context.getString(R.string.tittle_notification_control_plaga))
                                .setSmallIcon(getNotificationIcon()) // required
                                .setContentText(message)  // required
                                .setDefaults(Notification.DEFAULT_ALL)
                                .setAutoCancel(true)
                                .setTicker(ticker)
                                //API Level min 16 is required
                                .setLargeIcon( BitmapFactory.decodeResource(context.resources, R.drawable.ic_plagas) )
                                //.setGroup(smsAddress)
                                .setBadgeIconType(R.mipmap.ic_launcher_notification)
                                .setContentIntent(pendingIntent)
                                ///.setSound(RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION))
                                .setShowWhen(true)
                                .setWhen(Calendar.getInstance().getTimeInMillis())
                                .setPriority(NotificationCompat.PRIORITY_HIGH)
                                .setContentInfo(message)
                                .setStyle( NotificationCompat.BigTextStyle()
                                        .bigText(message))

                        builder.setVibrate( longArrayOf(0))


                        //builder.setDefaults(NotificationLocal.DEFAULT_VIBRATE)
                        val notification = builder.build()
                        notificationManager!!.notify(notificationId, notification)

                    } else {

                        val ticker = "Ver"
                        val defaultSoundUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION)

                        val notificationId = 237

                        //intent.putExtra(TagSmsResources.PHONE_NUMBER,smsAddress)
                        //intent.putExtra(TagSmsResources.CONTACT_NAME,messageAdress)
                        //intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
                        intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK
                        var pendingIntent: PendingIntent? = null

                        pendingIntent = PendingIntent.getActivity(context, notificationId, intent, PendingIntent.FLAG_ONE_SHOT)

                        val notificationBuilder = NotificationCompat.Builder(context)
                                .setSmallIcon(R.mipmap.ic_launcher)
                                .setContentTitle(context.getString(R.string.tittle_notification_control_plaga))
                                .setContentText(message)
                                .setDefaults(Notification.DEFAULT_ALL) // must requires VIBRATE permission
                                .setPriority(NotificationCompat.PRIORITY_HIGH) //must give priority to High, Max which will considered as heads-up notification
                                .addAction(R.mipmap.ic_launcher_notification,
                                        ticker, pendingIntent)
                                .setAutoCancel(true)
                                .setTicker(ticker)
                                .setShowWhen(true)
                                ///.setColor(context.getColor(R.color.colorPrimary))
                                .setSound(defaultSoundUri)
                                .setSmallIcon(getNotificationIcon())
                                .setContentIntent(pendingIntent)
                                .setLargeIcon( BitmapFactory.decodeResource(context.resources, R.drawable.ic_plagas) )
                                //.setGroupSummary(true)
                                // .setNumber(notificationSum)
                                .setStyle( NotificationCompat.BigTextStyle()
                                        .bigText(message))
                                .setWhen(System.currentTimeMillis())
                                .setContentInfo(message)
                        //.setFullScreenIntent(pendingIntent, true)
                        //API Level min 16 is required
                        //.setStyle(NotificationCompat.BigTextStyle().setBigContentTitle(fcmNotificationLocalBuilder.title).bigText(fcmNotificationLocalBuilder.message))

                        if (android.os.Build.VERSION.SDK_INT >= 21) {
                            notificationBuilder.setColor(context.getResources().getColor(R.color.green_900))
                                    .setVisibility(Notification.VISIBILITY_PUBLIC)
                            notificationBuilder.setVibrate( longArrayOf(0))
                        }

                        val notification:Notification = notificationBuilder.build();
                        //val notificationManager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
                        notificationManager!!.notify(notificationId,notification)
                    }
                //  }
                val fcmNotificationBuilder= com.interedes.agriculturappv3.modules.models.Notification.NotificationLocal()
                fcmNotificationBuilder.title=NotificationTypeResources.NOTIFICATION_TYPE_CONTROLPLAGA
                fcmNotificationBuilder.message= message
                fcmNotificationBuilder.user_name=""
                fcmNotificationBuilder.ui=""
                fcmNotificationBuilder.fcm_token=""
                fcmNotificationBuilder.room_id=""
                fcmNotificationBuilder.type_notification=NotificationTypeResources.NOTIFICATION_TYPE_CONTROLPLAGA
                fcmNotificationBuilder.image_url= ""
                fcmNotificationBuilder.parameter=item.CultivoId.toString()
                saveNotification(fcmNotificationBuilder)
        }
        }
    }


    override fun saveNotification(notification: NotificationLocal) {
        val lastNotification = getLastNotification()
        if (lastNotification == null) {
            notification.Id = 1
        } else {
            notification.Id = lastNotification.Id!! + 1
        }

        notification.time= System.currentTimeMillis()
        val lastUserLogued= getLastUserLogued()
        notification.userLoguedId=lastUserLogued?.Id
        notification.save()
    }

    override fun getLastNotification(): NotificationLocal? {
        val lastNotification = SQLite.select().from(NotificationLocal::class.java).orderBy(NotificationLocal_Table.Id, false).querySingle()
        return lastNotification
    }

    private fun getNotificationIcon(): Int {
        val useWhiteIcon = Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP
        return if (useWhiteIcon) R.mipmap.ic_launcher else R.mipmap.ic_launcher
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    private fun setupChannels(context:Context) {
        val notificationSoundUri=RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION)

        val adminChannelName = context.getString(R.string.notifications_admin_channel_name)
        val adminChannelDescription = context.getString(R.string.notifications_admin_channel_description)

        val audioAttributes = AudioAttributes.Builder()
                .setContentType(AudioAttributes.CONTENT_TYPE_SONIFICATION)
                .setUsage(AudioAttributes.USAGE_ALARM)
                .build()

        val adminChannel: NotificationChannel
        adminChannel = NotificationChannel(ADMIN_CHANNEL_ID, adminChannelName, NotificationManager.IMPORTANCE_HIGH)
        adminChannel.description = adminChannelDescription
        adminChannel.enableLights(true)
        adminChannel.lightColor = Color.BLUE
        adminChannel.enableVibration(true)
        adminChannel.vibrationPattern= longArrayOf(100, 200, 300, 400, 500, 400, 500, 200, 500)
        adminChannel.lockscreenVisibility = Notification.VISIBILITY_PUBLIC
        adminChannel.setShowBadge(true)
        // adminChannel.sound
        adminChannel.setSound(notificationSoundUri, audioAttributes)
        if (notificationManager != null) {
            notificationManager!!.createNotificationChannel(adminChannel)
        }
    }

    //endregion

    //region POST DATA SYNC
    override fun syncQuantityData(): QuantitySync {
        val usuarioLogued=getLastUserLogued()
        val counRegisterUnidadesProductivas= SQLite.select().from(Unidad_Productiva::class.java)
                .where(Unidad_Productiva_Table.Estado_Sincronizacion.eq(false))
                .and(Unidad_Productiva_Table.UsuarioId.eq(usuarioLogued?.Id))
                .queryList().count()

        val counRegisterLotes= SQLite.select().from(Lote::class.java).where(Lote_Table.EstadoSincronizacion.eq(false))
                .and(Lote_Table.UsuarioId.eq(usuarioLogued?.Id))
                .queryList().count()
        val counRegisterCultivos= SQLite.select().from(Cultivo::class.java).where(Cultivo_Table.EstadoSincronizacion.eq(false))
                .and(Cultivo_Table.UsuarioId.eq(usuarioLogued?.Id))
                .queryList().count()
        val counRegisterControlPlagas= SQLite.select().from(ControlPlaga::class.java).where(ControlPlaga_Table.Estado_Sincronizacion.eq(false))
                .and(ControlPlaga_Table.UsuarioId.eq(usuarioLogued?.Id))
                .queryList().count()
        val counRegisterProducccion= SQLite.select().from(Produccion::class.java).where(Produccion_Table.Estado_Sincronizacion.eq(false))
                .and(Produccion_Table.UsuarioId.eq(usuarioLogued?.Id))
                .queryList().count()
        val counRegisterProductos= SQLite.select().from(Producto::class.java).where(Producto_Table.Estado_Sincronizacion.eq(false))
                .and(Producto_Table.userId.eq(usuarioLogued?.Id))
                .queryList().count()
        val counRegisterTransacciones= SQLite.select().from(Transaccion::class.java).where(Transaccion_Table.Estado_Sincronizacion.eq(false))
                .and(Transaccion_Table.UsuarioId.eq(usuarioLogued?.Id))
                .queryList().count()

        val registerTotal= counRegisterUnidadesProductivas+
                counRegisterControlPlagas+
                counRegisterCultivos+
                counRegisterLotes+counRegisterProducccion+counRegisterProductos+counRegisterTransacciones

        val countUpdatesUnidadesProductivas= SQLite.select()
                .from(Unidad_Productiva::class.java)
                .where(Unidad_Productiva_Table.Estado_Sincronizacion.eq(true))
                .and(Unidad_Productiva_Table.UsuarioId.eq(usuarioLogued?.Id))
                .and(Unidad_Productiva_Table.Estado_SincronizacionUpdate.eq(false)).queryList().count()


        val countUpdatesLotes= SQLite.select().from(Lote::class.java)
                .where(Lote_Table.EstadoSincronizacion.eq(true))
                .and(Lote_Table.UsuarioId.eq(usuarioLogued?.Id))
                .and(Lote_Table.Estado_SincronizacionUpdate.eq(false)).queryList().count()


        val countUpdatesCultivos= SQLite.select().from(Cultivo::class.java)
                .where(Cultivo_Table.EstadoSincronizacion.eq(true))
                .and(Cultivo_Table.UsuarioId.eq(usuarioLogued?.Id))
                .and(Cultivo_Table.Estado_SincronizacionUpdate.eq(false)).queryList().count()

        val countUpdatesControlPlagas= SQLite.select().from(ControlPlaga::class.java)
                .where(ControlPlaga_Table.Estado_Sincronizacion.eq(true))
                .and(ControlPlaga_Table.UsuarioId.eq(usuarioLogued?.Id))
                .and(ControlPlaga_Table.Estado_SincronizacionUpdate.eq(false)).queryList().count()

        val countUpdatesProducccion= SQLite.select().from(Produccion::class.java)
                .where(Produccion_Table.Estado_Sincronizacion.eq(true))
                .and(Produccion_Table.UsuarioId.eq(usuarioLogued?.Id))
                .and(Produccion_Table.Estado_SincronizacionUpdate.eq(false)).queryList().count()

        val countUpdatesProductos= SQLite.select().from(Producto::class.java)
                .where(Producto_Table.Estado_Sincronizacion.eq(true))
                .and(Producto_Table.userId.eq(usuarioLogued?.Id))
                .and(Producto_Table.Estado_SincronizacionUpdate.eq(false)).queryList().count()

        val countUpdatesTransacciones= SQLite.select().from(Transaccion::class.java)
                .where(Transaccion_Table.Estado_Sincronizacion.eq(true))
                .and(Transaccion_Table.UsuarioId.eq(usuarioLogued?.Id))
                .and(Transaccion_Table.Estado_SincronizacionUpdate.eq(false)).queryList().count()

        val updatesTotal= countUpdatesUnidadesProductivas+
                countUpdatesLotes+
                countUpdatesCultivos+
                countUpdatesControlPlagas+countUpdatesProducccion+countUpdatesProductos+countUpdatesTransacciones

        val quantitySync= QuantitySync(registerTotal.toLong(),updatesTotal.toLong())
        return  quantitySync

    }

    //endregion

    //region PUT DATA SYNC



    //endregion

    //region  FOTOS DE PERFIL, PLAGAS Y ENFERMEDADES, INSUMOS
    override fun getListSyncEnfermedadesAndTratamiento(context: Context) {
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
                        if(item.Fotos!=null){
                            /*val fastStoreModelTransactionFotos= FastStoreModelTransaction
                                    .insertBuilder(FlowManager.getModelAdapter(FotoEnfermedad::class.java))
                                    .addAll(item.Fotos)
                                    .build()
                            val databaseFotoEnfermedad = FlowManager.getDatabase(DataSource::class.java)
                            val transactionFotoEnfermedad = databaseFotoEnfermedad.beginTransactionAsync(fastStoreModelTransactionFotos).build();
                            transactionFotoEnfermedad.execute()*/
                            for (itemFoto in item.Fotos!!){
                                itemFoto.save()
                               /*try {
                                    val base64String = itemFoto?.Ruta
                                    val base64Image = base64String?.split(",".toRegex())?.dropLastWhile { it.isEmpty() }!!.toTypedArray()[1]
                                    val byte = Base64.decode(base64Image, Base64.DEFAULT)

                                    item.blobImagenEnfermedad= Blob(byte)
                                    itemFoto.blobImagen = Blob(byte)
                                }catch (ex:Exception){
                                    var ss= ex.toString()
                                    Log.d("Convert Image", "defaultValue = " + ss);
                                }*/
                            }
                        }
                        item.save()
                    }
                    loadTratamientos(context)
                } else {
                    //postEventError(RequestEventMainMenu.ERROR_EVENT, "Comprueba tu conexión a Internet")
                    Log.e("JOB SYNC ENFERMEDADES", enfermedadResponse?.message().toString())
                }
            }
            override fun onFailure(call: Call<EnfermedadResponseApi>?, t: Throwable?) {
                //postEventError(RequestEventMainMenu.ERROR_EVENT, "Comprueba tu conexión a Internet")
                Log.e("JOB SYNC ENFERMEDADES", t?.toString())
            }
        })
    }

    private fun loadTratamientos(context: Context) {
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

                        if(item.Calificacions!!.size==0){
                            SQLite.delete<Calificacion_Tratamiento>(Calificacion_Tratamiento::class.java)
                                    .where(Calificacion_Tratamiento_Table.TratamientoId.eq(item.Id))
                                    .async()
                                    .execute()
                        }else{
                            SQLite.delete<Calificacion_Tratamiento>(Calificacion_Tratamiento::class.java)
                                    .where(Calificacion_Tratamiento_Table.TratamientoId.eq(item.Id))
                                    .async()
                                    .execute()

                            val fastStoreModelCalificacion= FastStoreModelTransaction
                                    .insertBuilder(FlowManager.getModelAdapter(Calificacion_Tratamiento::class.java))
                                    .addAll(item.Calificacions)
                                    .build()
                            val databaseCalificaciones = FlowManager.getDatabase(DataSource::class.java)
                            val transactionCalificacion = databaseCalificaciones.beginTransactionAsync(fastStoreModelCalificacion).build();
                            transactionCalificacion.execute()
                            for (calification in item.Calificacions!!){
                                //calification.save()
                                sumacalificacion=sumacalificacion!!+ calification.Valor!!
                            }
                            promedioCalificacion= sumacalificacion!! /item.Calificacions!!.size
                        }
                        item.CalificacionPromedio=promedioCalificacion
                        item.save()
                    }

                    syncFotosPlagasEnfermedades(context)
                    //postEventOk(RequestEventMainMenu.SYNC_FOTOS_INSUMOS_PLAGAS)
                    //postEventOk(RequestEventMainMenu.SYNC_EVENT)

                } else {
                    Log.e("JOB SYNC TRATAMIENTOS", tratamientoResponse?.message().toString())
                    //postEventError(RequestEventMainMenu.ERROR_EVENT, "Comprueba tu conexión a Internet")
                }
            }
            override fun onFailure(call: Call<TratamientoResponse>?, t: Throwable?) {
                Log.e("JOB SYNC TRATAMIENTOS", t?.toString())

                //postEventError(RequestEventMainMenu.ERROR_EVENT, "Comprueba tu conexión a Internet")
            }
        })
    }



    override fun syncFotosPlagasEnfermedades(context:Context) {
        listInsmo= SQLite.select().from(Insumo::class.java).queryList()
        listFotoEnfermedad= SQLite.select().from(FotoEnfermedad::class.java).queryList()
        getAllPlagasFoto(0,context)
    }

    private fun getAllInsumoFoto(index: Int,context: Context) {
        if (index >= listInsmo!!.size) {
            //onMessageToast(R.color.green,"Imagenes Cargadas.");
            //Toast.makeText(this,"Insumos cargados",Toast.LENGTH_LONG).show()
            Log.d("SYNC DATA", "Fotografias de insumos y plagas Loaded" )
            //Log.d(TAG_INSUMOS, "Fotografias de insumos y plagas cargados")
            FileLoader.deleteWith (context) .fromDirectory ( DIR_INSUMOS , FileLoader.DIR_INTERNAL) .deleteAllFiles ();
            FileLoader.deleteWith (context) .fromDirectory ( DIR_PLAGAS , FileLoader.DIR_INTERNAL) .deleteAllFiles ();
            postEventMainMenu(RequestEventMainMenu.SYNC_EVENT,null,null,null)
         //   FileLoader.deleteWith (context) .fromDirectory ( DI , FileLoader.DIR_INTERNAL) .deleteAllFiles ();
            ///FileLoader.deleteWith(this).fromDirectory(Environment.DIRECTORY_DOWNLOADS, FileLoader.DIR_EXTERNAL_PUBLIC).deleteFiles(uris);
            //this.stopSelf()
            //stopForeground(true); //true will remove notification
            //populaterecyclerView()
            //save list friend
            /*adapter.notifyDataSetChanged()
            dialogFindAllFriend.dismiss()
            mSwipeRefreshLayout.setRefreshing(false)
            detectFriendOnline.start()*/
        } else {
            val insumo = listInsmo?.get(index)
            if(insumo?.Imagen!=null){
                FileLoader.with(context)
                        .load(S3Resources.RootImage+"${insumo?.Imagen}",false) //2nd parameter is optioal, pass true to force load from network
                        //.fromDirectory("test4", FileLoader.DIR_EXTERNAL_PUBLIC)
                        .fromDirectory(DIR_INSUMOS, FileLoader.DIR_INTERNAL)
                        .asFile( object: FileRequestListener<File> {
                            override fun onLoad(request: FileLoadRequest?, response: FileResponse<File>?) {
                                val loadedFile = response?.getBody()


                                    val directAgriculturAppFolder = File(Environment.getExternalStorageDirectory().toString() + "/$DIR_AGRICULTUR_APP_PUBLIC")
                                    if (!directAgriculturAppFolder.exists()) {
                                        directAgriculturAppFolder.mkdir() //directory is created;
                                        Log.d("DIRECT AGRICULTURAPP", "Created" )
                                    }

                                    val directInsumo = File(directAgriculturAppFolder.path+"/$DIR_INSUMO_PUBLIC")
                                    if (!directInsumo.exists()) {
                                        directInsumo.mkdir() //directory is created;
                                        Log.d("DIRECT Insumo", "Created" )
                                    }

                                if(loadedFile?.length()!!>0){
                                    val compressedImage = Compressor(context)
                                            .setMaxWidth(300)
                                            .setMaxHeight(300)
                                            .setQuality(75)

                                            .setCompressFormat(Bitmap.CompressFormat.PNG)
                                            .setDestinationDirectoryPath(directInsumo.absolutePath)
                                            .compressToFile(loadedFile)
                                    //val bitmap= convertBitmapToByte(compressedImage)
                                    //insumo.blobImagen = Blob(bitmap)
                                    insumo.FotoLoaded=true
                                    insumo.ImagenLocal=compressedImage.path
                                    insumo.save()
                                }
                                getAllInsumoFoto(index+1,context)
                            }
                            override fun onError(request: FileLoadRequest?, error : Throwable?) {
                                var error = error.toString()
                                getAllPlagasFoto(index+1,context)
                            }
                        });
            }else{
                getAllInsumoFoto(index+1,context)
            }
        }
    }



    private fun getAllPlagasFoto(index: Int,context: Context) {
        if (index >= listFotoEnfermedad!!.size) {


            Log.d("SYNC DATA", "Plagas y Enfermedades Loaded" )
            getAllInsumoFoto(0,context)
        } else {
            val fotoEnfermedad = listFotoEnfermedad?.get(index)
            if(fotoEnfermedad?.Ruta!=null){
                FileLoader.with(context)
                        .load(S3Resources.RootImage+"${fotoEnfermedad?.Ruta}",false) //2nd parameter is optioal, pass true to force load from network
                        //.fromDirectory("test4", FileLoader.DIR_EXTERNAL_PUBLIC)
                        .fromDirectory(DIR_PLAGAS, FileLoader.DIR_INTERNAL)

                        .asFile( object: FileRequestListener<File> {
                            override fun onLoad(request: FileLoadRequest?, response: FileResponse<File>?) {
                                val loadedFile = response?.getBody();
                                if(loadedFile?.length()!!>0){

                                    val directAgriculturAppFolder = File(Environment.getExternalStorageDirectory().toString() + "/$DIR_AGRICULTUR_APP_PUBLIC")


                                    if (!directAgriculturAppFolder.exists()) {
                                        directAgriculturAppFolder.mkdir() //directory is created;
                                        Log.d("DIRECT AGRICULTURAPP", "Created" )
                                    }

                                    val directPlaga = File(directAgriculturAppFolder.path+"/$DIR_PLAGAS_PUBLIC")

                                    if (!directPlaga.exists()) {
                                        directPlaga.mkdir() //directory is created;
                                        Log.d("DIRECT PLAGAS", "Created" )
                                    }

                                    val compressedImage = Compressor(context)
                                            .setMaxHeight(300)
                                            .setQuality(100)
                                            //.setDestinationDirectoryPath(Environment.getExternalStoragePublicDirectory(
                                                  //  Environment.DIRECTORY_PICTURES).getAbsolutePath())
                                            .setDestinationDirectoryPath(directPlaga.absolutePath)
                                            .compressToFile(loadedFile)

                                    fotoEnfermedad.FotoLoaded=true
                                    fotoEnfermedad.save()
                                   // val bitmap= convertBitmapToBytePlagasAndFotoPerfil(compressedImage)
                                    SQLite.update(Enfermedad::class.java)
                                            //.set(Enfermedad_Table.blobImagenEnfermedad.eq(Blob(bitmap)))
                                            .set(Enfermedad_Table.RutaImagenEnfermedad.eq(compressedImage.path)
                                                 )
                                            .where(Enfermedad_Table.Id.eq(fotoEnfermedad.EnfermedadesId))
                                            //.and(Usuario_Table.IsRemembered.is(true))
                                            .async()
                                            .execute(); // non-UI blocking
                                }
                                getAllPlagasFoto(index+1,context)
                            }
                            override fun onError(request: FileLoadRequest?, error : Throwable?) {
                                var error = error.toString()
                                getAllPlagasFoto(index+1,context)
                            }
                        })
            }else{
                getAllPlagasFoto(index+1,context)
            }
        }
    }

    fun convertBitmapToByte(bitmapCompressed: Bitmap): ByteArray? {
        val stream = ByteArrayOutputStream()
        bitmapCompressed.compress(Bitmap.CompressFormat.PNG, 100, stream)
        return stream.toByteArray()
        //return BitmapFactory.decodeByteArray(byteFormat, 0, byteFormat.size)
    }


    fun convertBitmapToBytePlagasAndFotoPerfil(bitmapCompressed: Bitmap): ByteArray? {
        val stream = ByteArrayOutputStream()
        bitmapCompressed.compress(Bitmap.CompressFormat.JPEG, 100, stream)
        return stream.toByteArray()
        //return BitmapFactory.decodeByteArray(byteFormat, 0, byteFormat.size)
    }

    //endregion

    //region Main Post Event
    private fun postEventMainMenu(type: Int, listModel1:MutableList<Object>?,model:Object?,errorMessage: String?) {
        val event = RequestEventMainMenu(type, listModel1, model, errorMessage)
        event.eventType = type
        eventBus?.post(event)
    }
    //endregion

}