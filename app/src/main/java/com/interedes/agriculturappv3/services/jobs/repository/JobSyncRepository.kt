package com.interedes.agriculturappv3.services.jobs.repository

import android.content.Context
import android.graphics.Bitmap
import android.util.Log
import com.interedes.agriculturappv3.modules.models.control_plaga.ControlPlaga
import com.interedes.agriculturappv3.modules.models.control_plaga.ControlPlaga_Table
import com.interedes.agriculturappv3.modules.models.cultivo.Cultivo
import com.interedes.agriculturappv3.modules.models.cultivo.Cultivo_Table
import com.interedes.agriculturappv3.modules.models.insumos.Insumo
import com.interedes.agriculturappv3.modules.models.lote.Lote
import com.interedes.agriculturappv3.modules.models.lote.Lote_Table
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
import com.interedes.agriculturappv3.services.api.ApiInterface
import com.interedes.agriculturappv3.services.resources.Chat_Resources
import com.interedes.agriculturappv3.services.resources.S3Resources
import com.interedes.agriculturappv3.services.resources.Status_Chat
import com.krishna.fileloader.FileLoader
import com.krishna.fileloader.listener.FileRequestListener
import com.krishna.fileloader.pojo.FileResponse
import com.krishna.fileloader.request.FileLoadRequest
import com.raizlabs.android.dbflow.data.Blob
import com.raizlabs.android.dbflow.kotlinextensions.save
import com.raizlabs.android.dbflow.sql.language.SQLite
import id.zelory.compressor.Compressor
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.io.ByteArrayOutputStream
import java.io.File

class JobSyncRepository: IMainViewJob.Repository {
    var apiService: ApiInterface? = null

    init {
        apiService = ApiInterface.create()
    }


    private var listInsmo: List<Insumo>? = null
    private var listFotoEnfermedad: List<FotoEnfermedad>? = null
    private val TAG_INSUMOS = "INSUMOS"
    private val DIR_INSUMOS_PLAGAS = "INSUMOS_PLAGAS"
    private val TAG_FIREBASE = "FIREBASE UIID"



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

    //region POST DATA SYNC
    override fun syncQuantityData(): QuantitySync {
        var usuarioLogued=getLastUserLogued()
        var counRegisterUnidadesProductivas= SQLite.select().from(Unidad_Productiva::class.java)
                .where(Unidad_Productiva_Table.Estado_Sincronizacion.eq(false))
                .and(Unidad_Productiva_Table.UsuarioId.eq(usuarioLogued?.Id))
                .queryList().count()

        var counRegisterLotes= SQLite.select().from(Lote::class.java).where(Lote_Table.EstadoSincronizacion.eq(false))
                .and(Lote_Table.UsuarioId.eq(usuarioLogued?.Id))
                .queryList().count()
        var counRegisterCultivos= SQLite.select().from(Cultivo::class.java).where(Cultivo_Table.EstadoSincronizacion.eq(false))
                .and(Cultivo_Table.UsuarioId.eq(usuarioLogued?.Id))
                .queryList().count()
        var counRegisterControlPlagas= SQLite.select().from(ControlPlaga::class.java).where(ControlPlaga_Table.Estado_Sincronizacion.eq(false))
                .and(ControlPlaga_Table.UsuarioId.eq(usuarioLogued?.Id))
                .queryList().count()
        var counRegisterProducccion= SQLite.select().from(Produccion::class.java).where(Produccion_Table.Estado_Sincronizacion.eq(false))
                .and(Produccion_Table.UsuarioId.eq(usuarioLogued?.Id))
                .queryList().count()
        var counRegisterProductos= SQLite.select().from(Producto::class.java).where(Producto_Table.Estado_Sincronizacion.eq(false))
                .and(Producto_Table.userId.eq(usuarioLogued?.Id))
                .queryList().count()
        var counRegisterTransacciones= SQLite.select().from(Transaccion::class.java).where(Transaccion_Table.Estado_Sincronizacion.eq(false))
                .and(Transaccion_Table.UsuarioId.eq(usuarioLogued?.Id))
                .queryList().count()

        var registerTotal= counRegisterUnidadesProductivas+
                counRegisterControlPlagas+
                counRegisterCultivos+
                counRegisterLotes+counRegisterProducccion+counRegisterProductos+counRegisterTransacciones

        var countUpdatesUnidadesProductivas= SQLite.select()
                .from(Unidad_Productiva::class.java)
                .where(Unidad_Productiva_Table.Estado_Sincronizacion.eq(true))
                .and(Unidad_Productiva_Table.UsuarioId.eq(usuarioLogued?.Id))
                .and(Unidad_Productiva_Table.Estado_SincronizacionUpdate.eq(false)).queryList().count()


        var countUpdatesLotes= SQLite.select().from(Lote::class.java)
                .where(Lote_Table.EstadoSincronizacion.eq(true))
                .and(Lote_Table.UsuarioId.eq(usuarioLogued?.Id))
                .and(Lote_Table.Estado_SincronizacionUpdate.eq(false)).queryList().count()


        var countUpdatesCultivos= SQLite.select().from(Cultivo::class.java)
                .where(Cultivo_Table.EstadoSincronizacion.eq(true))
                .and(Cultivo_Table.UsuarioId.eq(usuarioLogued?.Id))
                .and(Cultivo_Table.Estado_SincronizacionUpdate.eq(false)).queryList().count()

        var countUpdatesControlPlagas= SQLite.select().from(ControlPlaga::class.java)
                .where(ControlPlaga_Table.Estado_Sincronizacion.eq(true))
                .and(ControlPlaga_Table.UsuarioId.eq(usuarioLogued?.Id))
                .and(ControlPlaga_Table.Estado_SincronizacionUpdate.eq(false)).queryList().count()

        var countUpdatesProducccion= SQLite.select().from(Produccion::class.java)
                .where(Produccion_Table.Estado_Sincronizacion.eq(true))
                .and(Produccion_Table.UsuarioId.eq(usuarioLogued?.Id))
                .and(Produccion_Table.Estado_SincronizacionUpdate.eq(false)).queryList().count()

        var countUpdatesProductos= SQLite.select().from(Producto::class.java)
                .where(Producto_Table.Estado_Sincronizacion.eq(true))
                .and(Producto_Table.userId.eq(usuarioLogued?.Id))
                .and(Producto_Table.Estado_SincronizacionUpdate.eq(false)).queryList().count()

        var countUpdatesTransacciones= SQLite.select().from(Transaccion::class.java)
                .where(Transaccion_Table.Estado_Sincronizacion.eq(true))
                .and(Transaccion_Table.UsuarioId.eq(usuarioLogued?.Id))
                .and(Transaccion_Table.Estado_SincronizacionUpdate.eq(false)).queryList().count()

        var updatesTotal= countUpdatesUnidadesProductivas+
                countUpdatesLotes+
                countUpdatesCultivos+
                countUpdatesControlPlagas+countUpdatesProducccion+countUpdatesProductos+countUpdatesTransacciones

        var quantitySync= QuantitySync(registerTotal.toLong(),updatesTotal.toLong())
        return  quantitySync

    }

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
                            for (itemFoto in item?.Fotos!!){
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

                    syncFotos(context)
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



    override fun syncFotos(context:Context) {
        listInsmo= SQLite.select().from(Insumo::class.java).queryList()
        listFotoEnfermedad= SQLite.select().from(FotoEnfermedad::class.java).queryList()
        getFotoPerfil(context)
    }


    private fun getFotoPerfil(context:Context){
        val user = SQLite.select().from(Usuario::class.java).where(Usuario_Table.UsuarioRemembered.eq(true)).querySingle()
        if(user!=null){
            if(user.Fotopefil!=null){

                FileLoader.with(context)
                        .load(S3Resources.RootImage+"${user.Fotopefil}",false) //2nd parameter is optioal, pass true to force load from network
                        //.fromDirectory("test4", FileLoader.DIR_EXTERNAL_PUBLIC)
                        .fromDirectory(DIR_INSUMOS_PLAGAS, FileLoader.DIR_INTERNAL)
                        .asFile( object: FileRequestListener<File> {
                            override fun onLoad(request: FileLoadRequest?, response: FileResponse<File>?) {
                                val loadedFile = response?.getBody();
                                if(loadedFile?.length()!!>0){
                                    val compressedImage = Compressor(context)
                                            .setMaxHeight(300)
                                            .setQuality(100)
                                            .compressToBitmap(loadedFile)
                                    val bitmap= convertBitmapToByte(compressedImage)
                                    user.blobImagenUser = Blob(bitmap)
                                    user.save()
                                }
                                getAllPlagasFoto(0,context)
                            }
                            override fun onError(request: FileLoadRequest?, error : Throwable?) {
                                var error = error.toString()
                                getAllPlagasFoto(0,context)
                            }
                        });
            }
            else{
                getAllPlagasFoto(0,context)
            }
        }else{
            getAllPlagasFoto(0,context)
        }
    }

    private fun getAllInsumoFoto(index: Int,context: Context) {
        if (index >= listInsmo!!.size) {
            //onMessageToast(R.color.green,"Imagenes Cargadas.");
            //Toast.makeText(this,"Insumos cargados",Toast.LENGTH_LONG).show()

            Log.d(TAG_INSUMOS, "Fotografias de insumos y plagas cargados")
            FileLoader.deleteWith (context) .fromDirectory ( DIR_INSUMOS_PLAGAS , FileLoader.DIR_INTERNAL) .deleteAllFiles ();
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
                        .fromDirectory(DIR_INSUMOS_PLAGAS, FileLoader.DIR_INTERNAL)
                        .asFile( object: FileRequestListener<File> {
                            override fun onLoad(request: FileLoadRequest?, response: FileResponse<File>?) {
                                val loadedFile = response?.getBody();
                                if(loadedFile?.length()!!>0){
                                    val compressedImage = Compressor(context)
                                            .setMaxHeight(300)
                                            .setQuality(100)
                                            .compressToBitmap(loadedFile)
                                    val bitmap= convertBitmapToByte(compressedImage)
                                    insumo.blobImagen = Blob(bitmap)
                                    insumo.FotoLoaded=true
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
            getAllInsumoFoto(0,context)
        } else {
            val fotoEnfermedad = listFotoEnfermedad?.get(index)
            if(fotoEnfermedad?.Ruta!=null){
                FileLoader.with(context)
                        .load(S3Resources.RootImage+"${fotoEnfermedad?.Ruta}",false) //2nd parameter is optioal, pass true to force load from network
                        //.fromDirectory("test4", FileLoader.DIR_EXTERNAL_PUBLIC)
                        .fromDirectory(DIR_INSUMOS_PLAGAS, FileLoader.DIR_INTERNAL)
                        .asFile( object: FileRequestListener<File> {
                            override fun onLoad(request: FileLoadRequest?, response: FileResponse<File>?) {
                                val loadedFile = response?.getBody();
                                if(loadedFile?.length()!!>0){
                                    val compressedImage = Compressor(context)
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

    //endregion

}