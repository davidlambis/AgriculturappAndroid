package com.interedes.agriculturappv3.services.services

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.Service
import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.Color
import android.os.*
import android.util.Log
import android.widget.Toast
import com.interedes.agriculturappv3.R
import com.interedes.agriculturappv3.modules.models.insumos.Insumo
import com.interedes.agriculturappv3.services.Const
import com.interedes.agriculturappv3.services.resources.S3Resources
import com.krishna.fileloader.FileLoader
import com.krishna.fileloader.listener.FileRequestListener
import com.krishna.fileloader.pojo.FileResponse
import com.krishna.fileloader.request.FileLoadRequest
import com.raizlabs.android.dbflow.data.Blob
import com.raizlabs.android.dbflow.kotlinextensions.save
import com.raizlabs.android.dbflow.sql.language.SQLite
import id.zelory.compressor.Compressor
import java.io.ByteArrayOutputStream
import java.io.File
import io.fabric.sdk.android.services.settings.IconRequest.build
import android.os.Build
import android.support.annotation.RequiresApi
import android.support.v4.app.JobIntentService
import android.support.v4.app.NotificationCompat
import com.interedes.agriculturappv3.modules.models.plagas.Enfermedad
import com.interedes.agriculturappv3.modules.models.plagas.Enfermedad_Table
import com.interedes.agriculturappv3.modules.models.plagas.FotoEnfermedad
import com.interedes.agriculturappv3.modules.models.usuario.Usuario
import com.interedes.agriculturappv3.modules.models.usuario.Usuario_Table


class JobDownloadFotosService : Service() {


    private val ADMIN_CHANNEL_ID = "admin_channel"
    private var listInsmo: List<Insumo>? = null
    private var listFotoEnfermedad: List<FotoEnfermedad>? = null
    private val TAG = "INSUMOS"
    private val DIR_INSUMOS_PLAGAS = "INSUMOS_PLAGAS"
    private var notificationManager: NotificationManager? = null
    var uris =ArrayList<String>()

    // Binder given to clients

    override fun onCreate() {
        super.onCreate()
        //startForeground(1, NotificationLocal())
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        Log.d(TAG, "OnStartService")
        listInsmo= SQLite.select().from(Insumo::class.java).queryList()
        listFotoEnfermedad= SQLite.select().from(FotoEnfermedad::class.java).queryList()

        notificationManager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager?
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            setupChannels()
            val builder = Notification.Builder(this, ADMIN_CHANNEL_ID)
                    .setContentTitle(getString(R.string.app_name))
                    .setContentText("Cargando imagenes de insumos...")
                    .setProgress(0, 0, true)
                    .setAutoCancel(false)

            val notification = builder.build()
            startForeground(1, notification)

        } else {
            val builder = NotificationCompat.Builder(this)
                    .setContentTitle(getString(R.string.app_name))
                    .setContentText("Cargando imagenes de insumos...")
                    .setPriority(NotificationCompat.PRIORITY_DEFAULT)
                    .setProgress(0, 0, true)
                    .setAutoCancel(false)

            val notification = builder.build()

            startForeground(1, notification)
        }

        getFotoPerfil()

        return START_STICKY
    }


    private fun getFotoPerfil(){
        val user = SQLite.select().from(Usuario::class.java).where(Usuario_Table.UsuarioRemembered.eq(true)).querySingle()
        if(user!=null){
            if(user.Fotopefil!=null){
                FileLoader.with(this)
                        .load(S3Resources.RootImage+"${user.Fotopefil}",false) //2nd parameter is optioal, pass true to force load from network
                        //.fromDirectory("test4", FileLoader.DIR_EXTERNAL_PUBLIC)
                        .fromDirectory(DIR_INSUMOS_PLAGAS, FileLoader.DIR_INTERNAL)
                        .asFile( object: FileRequestListener<File> {
                            override fun onLoad(request: FileLoadRequest?, response: FileResponse<File>?) {
                                val loadedFile = response?.getBody();
                                if(loadedFile?.length()!!>0){
                                    val compressedImage = Compressor(applicationContext)
                                            .setMaxHeight(300)
                                            .setQuality(100)
                                            .compressToBitmap(loadedFile)
                                    val bitmap= convertBitmapToByte(compressedImage)
                                    user.blobImagenUser = Blob(bitmap)
                                    user.save()
                                }
                                getAllPlagasFoto(0)
                            }
                            override fun onError(request: FileLoadRequest?, error : Throwable?) {
                                var error = error.toString()
                                getAllPlagasFoto(0)
                            }
                        });
            }
            else{
                getAllPlagasFoto(0)
            }
        }else{
            getAllPlagasFoto(0)
        }
    }

    private fun getAllInsumoFoto(index: Int) {
        if (index >= listInsmo?.size!!) {

            //onMessageToast(R.color.green,"Imagenes Cargadas.");
            //Toast.makeText(this,"Insumos cargados",Toast.LENGTH_LONG).show()
            Log.d(TAG, "Fotografias de insumos y plagas cargados")
            FileLoader.deleteWith (this) .fromDirectory ( DIR_INSUMOS_PLAGAS , FileLoader.DIR_INTERNAL) .deleteAllFiles ();
            ///FileLoader.deleteWith(this).fromDirectory(Environment.DIRECTORY_DOWNLOADS, FileLoader.DIR_EXTERNAL_PUBLIC).deleteFiles(uris);
            this.stopSelf()
            stopForeground(true); //true will remove notification
            //populaterecyclerView()
            //save list friend
            /*adapter.notifyDataSetChanged()
            dialogFindAllFriend.dismiss()
            mSwipeRefreshLayout.setRefreshing(false)
            detectFriendOnline.start()*/
        } else {
            val insumo = listInsmo?.get(index)
            if(insumo?.Imagen!=null){
                FileLoader.with(this)
                        .load(S3Resources.RootImage+"${insumo?.Imagen}",false) //2nd parameter is optioal, pass true to force load from network
                        //.fromDirectory("test4", FileLoader.DIR_EXTERNAL_PUBLIC)
                        .fromDirectory(DIR_INSUMOS_PLAGAS, FileLoader.DIR_INTERNAL)
                        .asFile( object: FileRequestListener<File> {
                            override fun onLoad(request: FileLoadRequest?, response: FileResponse<File>?) {
                                val loadedFile = response?.getBody();
                                if(loadedFile?.length()!!>0){
                                    val compressedImage = Compressor(applicationContext)
                                            .setMaxHeight(300)
                                            .setQuality(100)
                                            .compressToBitmap(loadedFile)
                                    val bitmap= convertBitmapToByte(compressedImage)
                                    insumo?.blobImagen = Blob(bitmap)
                                    insumo?.FotoLoaded=true
                                    insumo!!.save()
                                }
                                getAllInsumoFoto(index+1)
                            }
                            override fun onError(request: FileLoadRequest?, error : Throwable?) {
                                var error = error.toString()
                                getAllPlagasFoto(index+1)
                            }
                        });
            }else{
                getAllInsumoFoto(index+1)
            }


        }
    }



    private fun getAllPlagasFoto(index: Int) {
        if (index >= listFotoEnfermedad?.size!!) {
           getAllInsumoFoto(0)
        } else {
            val fotoEnfermedad = listFotoEnfermedad?.get(index)
            if(fotoEnfermedad?.Ruta!=null){
                FileLoader.with(this)
                        .load(S3Resources.RootImage+"${fotoEnfermedad?.Ruta}",false) //2nd parameter is optioal, pass true to force load from network
                        //.fromDirectory("test4", FileLoader.DIR_EXTERNAL_PUBLIC)
                        .fromDirectory(DIR_INSUMOS_PLAGAS, FileLoader.DIR_INTERNAL)
                        .asFile( object: FileRequestListener<File> {
                            override fun onLoad(request: FileLoadRequest?, response: FileResponse<File>?) {
                                val loadedFile = response?.getBody();
                                if(loadedFile?.length()!!>0){
                                    val compressedImage = Compressor(applicationContext)
                                            .setMaxHeight(300)
                                            .setQuality(100)
                                            .compressToBitmap(loadedFile)
                                    val bitmap= convertBitmapToByte(compressedImage)

                                    SQLite.update(Enfermedad::class.java)
                                            .set(Enfermedad_Table.blobImagenEnfermedad.eq(Blob(bitmap)))
                                            .where(Enfermedad_Table.Id.eq(fotoEnfermedad?.EnfermedadesId))
                                            //   .and(Usuario_Table.IsRemembered.is(true))
                                            .async()
                                            .execute(); // non-UI blocking
                                }

                                getAllPlagasFoto(index+1)
                            }
                            override fun onError(request: FileLoadRequest?, error : Throwable?) {
                                var error = error.toString()
                                getAllPlagasFoto(index+1)
                            }
                        })
            }else{
                getAllPlagasFoto(index+1)
            }

        }
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    private fun setupChannels() {
        val adminChannelName = getString(R.string.notifications_admin_channel_name)
        val adminChannelDescription = getString(R.string.notifications_admin_channel_description)

        val adminChannel: NotificationChannel
        adminChannel = NotificationChannel(ADMIN_CHANNEL_ID, adminChannelName, NotificationManager.IMPORTANCE_LOW)
        adminChannel.description = adminChannelDescription
        adminChannel.enableLights(true)
        adminChannel.lightColor = Color.RED
        adminChannel.enableVibration(true)
        if (notificationManager != null) {
            notificationManager!!.createNotificationChannel(adminChannel)
        }
    }

    fun convertBitmapToByte(bitmapCompressed: Bitmap): ByteArray? {
        val stream = ByteArrayOutputStream()
        bitmapCompressed.compress(Bitmap.CompressFormat.PNG, 100, stream)
        return stream.toByteArray()
        //return BitmapFactory.decodeByteArray(byteFormat, 0, byteFormat.size)
    }


    override fun onBind(intent: Intent): IBinder? {
        Log.d(TAG, "OnBindService")
        return null
    }

    override fun onDestroy() {
        super.onDestroy()
        //stopForeground(true); //true will remove notification
        Log.d(TAG, "OnDestroyService")
    }
}