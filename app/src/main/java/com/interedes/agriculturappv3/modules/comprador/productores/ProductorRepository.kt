package com.interedes.agriculturappv3.modules.comprador.productores

import android.util.Base64
import android.util.Log
import com.interedes.agriculturappv3.libs.EventBus
import com.interedes.agriculturappv3.libs.GreenRobotEventBus
import com.interedes.agriculturappv3.modules.comprador.productores.events.RequestEventProductor
import com.interedes.agriculturappv3.modules.models.control_plaga.ControlPlaga
import com.interedes.agriculturappv3.modules.models.cultivo.Cultivo
import com.interedes.agriculturappv3.modules.models.cultivo.Cultivo_Table
import com.interedes.agriculturappv3.modules.models.detalletipoproducto.DetalleTipoProducto
import com.interedes.agriculturappv3.modules.models.lote.Lote
import com.interedes.agriculturappv3.modules.models.lote.Lote_Table
import com.interedes.agriculturappv3.modules.models.produccion.Produccion
import com.interedes.agriculturappv3.modules.models.producto.GetProductosByTipoResponse
import com.interedes.agriculturappv3.modules.models.producto.Producto
import com.interedes.agriculturappv3.modules.models.producto.Producto_Table
import com.interedes.agriculturappv3.modules.models.rol.Rol
import com.interedes.agriculturappv3.modules.models.rol.Rol_Table
import com.interedes.agriculturappv3.modules.models.sincronizacion.GetSynProductosUserResponse
import com.interedes.agriculturappv3.modules.models.unidad_productiva.Unidad_Productiva
import com.interedes.agriculturappv3.modules.models.unidad_productiva.Unidad_Productiva_Table
import com.interedes.agriculturappv3.modules.models.usuario.Usuario
import com.interedes.agriculturappv3.modules.models.usuario.Usuario_Table
import com.interedes.agriculturappv3.services.api.ApiInterface
import com.interedes.agriculturappv3.services.listas.Listas
import com.raizlabs.android.dbflow.data.Blob
import com.raizlabs.android.dbflow.kotlinextensions.save
import com.raizlabs.android.dbflow.sql.language.SQLite
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class ProductorRepository:IMainViewProductor.Repository {

    var eventBus: EventBus? = null
    var apiService: ApiInterface? = null
    init {
        eventBus = GreenRobotEventBus()
        apiService = ApiInterface.create()
    }


    override fun getListTipoProductos(checkConection: Boolean,tipoProducto:Long) {

        if(checkConection){
            //Get Productos by user
            val queryProductos = Listas.queryGeneralLong("TipoProductoId",tipoProducto)
            val callProductos = apiService?.getProductosByTipoProductos(queryProductos)
            callProductos?.enqueue(object : Callback<GetProductosByTipoResponse> {
                override fun onResponse(call: Call<GetProductosByTipoResponse>?, response: Response<GetProductosByTipoResponse>?) {
                    if (response != null && response.code() == 200) {

                        //TODO Delete information in local, add new remote
                        SQLite.delete<Unidad_Productiva>(Unidad_Productiva::class.java)
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
                                .execute()



                        val list = response.body()?.value as MutableList<DetalleTipoProducto>


                        for (detalleTipoProducto in list){

                            detalleTipoProducto.save()

                            if(detalleTipoProducto.cultivos?.size!!>0){

                                for(cultivo in detalleTipoProducto?.cultivos!!){
                                    var usuario=cultivo?.Lote?.UnidadProductiva?.Usuario
                                    if(usuario!=null){
                                        usuario.save()
                                    }

                                    //TODO Unidades Productivas
                                    var unidaProductiva=cultivo?.Lote?.UnidadProductiva
                                    if(unidaProductiva!=null){
                                        var unidadProductivaVerficateSave= SQLite.select()
                                                .from(Unidad_Productiva::class.java)
                                                .where(Unidad_Productiva_Table.Id_Remote.eq(unidaProductiva.Id_Remote))
                                                .querySingle()
                                        //TODO Verifica si ya existe
                                        if (unidadProductivaVerficateSave !=null){
                                            unidaProductiva.Unidad_Productiva_Id=unidadProductivaVerficateSave?.Unidad_Productiva_Id
                                        }else{
                                            val last_up = getLastUp()
                                            if (last_up == null) {
                                                unidaProductiva.Unidad_Productiva_Id = 1
                                            } else {
                                                unidaProductiva.Unidad_Productiva_Id = last_up.Unidad_Productiva_Id!! + 1
                                            }
                                        }
                                        unidaProductiva.UsuarioId=usuario?.Id
                                        unidaProductiva.Nombre_Ciudad= if (unidaProductiva.Ciudad!=null) unidaProductiva.Ciudad?.Nombre else null
                                        unidaProductiva.Nombre_Unidad_Medida= if (unidaProductiva.UnidadMedida!=null) unidaProductiva.UnidadMedida?.Descripcion else null
                                        unidaProductiva.Nombre_Departamento= if (unidaProductiva.Ciudad!=null) unidaProductiva.Ciudad?.Departamento?.Nombre else null

                                        if(unidaProductiva.LocalizacionUps?.size!!>0){
                                            for (localizacion in unidaProductiva.LocalizacionUps!!){
                                                unidaProductiva.DireccionAproximadaGps=localizacion.DireccionAproximadaGps
                                                unidaProductiva.Latitud=localizacion.Latitud
                                                unidaProductiva.Longitud=localizacion.Longitud
                                                unidaProductiva.Coordenadas=localizacion.Coordenadas
                                                unidaProductiva.Direccion=localizacion.Direccion
                                                unidaProductiva.Configuration_Point=true
                                                unidaProductiva.Configuration_Poligon=false
                                                unidaProductiva.LocalizacionUpId=localizacion.Id
                                            }
                                        }

                                        unidaProductiva.save()
                                    }


                                    //TODO Lote
                                    var lote=cultivo?.Lote
                                    if(lote!=null){
                                        var loteVerficateSave= SQLite.select()
                                                .from(Lote::class.java)
                                                .where(Lote_Table.Id_Remote.eq(lote.Id_Remote))
                                                .querySingle()
                                        //TODO Verifica si ya existe
                                        if (loteVerficateSave!=null){
                                            lote.LoteId=loteVerficateSave.LoteId
                                        }else{
                                            val last_lote = getLastLote()
                                            if (last_lote == null) {
                                                lote.LoteId = 1
                                            } else {
                                                lote.LoteId = last_lote.LoteId!! + 1
                                            }
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

                                        lote.Unidad_Productiva_Id=unidaProductiva?.Unidad_Productiva_Id
                                        lote.Nombre_Unidad_Medida= if (lote.UnidadMedida!=null) lote.UnidadMedida?.Descripcion else null
                                        lote.Nombre_Unidad_Productiva= unidaProductiva?.nombre
                                        lote.Nombre= if (lote.Nombre==null) "" else lote.Nombre
                                        lote.Descripcion= if (lote.Descripcion==null) "" else lote.Descripcion


                                        lote.save()
                                    }

                                    //TODO Cultivo

                                    var cultivoVerficateSave= SQLite.select()
                                            .from(Cultivo::class.java)
                                            .where(Cultivo_Table.Id_Remote.eq(cultivo.Id_Remote))
                                            .querySingle()
                                    //TODO Verifica si tiene pendiente actualizacion por sincronizar
                                    if (cultivoVerficateSave!=null){
                                        cultivo.CultivoId=cultivoVerficateSave.CultivoId
                                    }else{
                                        val last_cultivo = getLastCultivo()
                                        if (last_cultivo == null) {
                                            cultivo.CultivoId = 1
                                        } else {
                                            cultivo.CultivoId = last_cultivo.CultivoId!! + 1
                                        }
                                    }

                                    cultivo.LoteId=lote?.LoteId
                                    cultivo.NombreUnidadProductiva= unidaProductiva?.nombre
                                    cultivo.NombreLote= lote?.Nombre
                                    cultivo.EstadoSincronizacion= true
                                    cultivo.Estado_SincronizacionUpdate= true
                                    cultivo.stringFechaInicio=cultivo.getFechaStringFormatApi(cultivo.FechaIncio)
                                    cultivo.stringFechaFin=cultivo.getFechaStringFormatApi(cultivo.FechaFin)

                                    cultivo.Nombre_Tipo_Producto= if (detalleTipoProducto.tipoProducto!=null) detalleTipoProducto?.tipoProducto?.Nombre else null
                                    cultivo.Nombre_Detalle_Tipo_Producto=detalleTipoProducto.Nombre
                                    cultivo.Id_Tipo_Producto= detalleTipoProducto.TipoProductoId
                                    cultivo.Nombre_Unidad_Medida=if (cultivo.unidadMedida!=null) cultivo.unidadMedida?.Descripcion else null
                                    cultivo.save()

                                    if(cultivo?.productos?.size!!>0){
                                        for(producto in cultivo?.productos!!){
                                            var productoVerficateSave= SQLite.select()
                                                    .from(Producto::class.java)
                                                    .where(Producto_Table.Id_Remote.eq(producto.Id_Remote))
                                                    .querySingle()
                                            //TODO Verifica si tiene pendiente actualizacion por sincronizar
                                            if (productoVerficateSave!=null){
                                                producto.ProductoId=productoVerficateSave.ProductoId
                                            }else {
                                                val last_producto = getLastProducto()
                                                if (last_producto == null) {
                                                    producto.ProductoId = 1
                                                } else {
                                                    producto.ProductoId = last_producto.ProductoId!! + 1
                                                }
                                            }
                                            producto.NombreCultivo= cultivo.Nombre
                                            producto.NombreLote= if(lote!=null)lote.Nombre else null
                                            producto.NombreUnidadProductiva= if(unidaProductiva!=null)unidaProductiva.nombre else null
                                            producto.NombreUnidadMedidaCantidad=if(producto.UnidadMedida!= null)producto.UnidadMedida?.Descripcion else null
                                            producto.NombreCalidad=if(producto.Calidad!=null)producto.Calidad?.Nombre else null
                                            producto.NombreUnidadMedidaPrecio=producto.PrecioUnidadMedida
                                            producto.Usuario_Logued=usuario?.Id
                                            producto.NombreDetalleTipoProducto=detalleTipoProducto.Nombre
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
                                }
                            }
                        }
                    } else {
                        postEventError(RequestEventProductor.ERROR_EVENT, "Comprueba tu conexión a Internet")
                    }
                }
                override fun onFailure(call: Call<GetProductosByTipoResponse>?, t: Throwable?) {
                    postEventError(RequestEventProductor.ERROR_EVENT, "Comprueba tu conexión a Internet")
                }
            })


        }else{



        }




    }





    fun getLastUp(): Unidad_Productiva? {
        val lastUnidadProductiva = SQLite.select().from(Unidad_Productiva::class.java).where().orderBy(Unidad_Productiva_Table.Unidad_Productiva_Id, false).querySingle()
        return lastUnidadProductiva
    }

    fun getLastLote(): Lote? {
        val lastLote = SQLite.select().from(Lote::class.java).where().orderBy(Lote_Table.LoteId, false).querySingle()
        return lastLote
    }

    fun getLastCultivo(): Cultivo? {
        val lastCultivo = SQLite.select().from(Cultivo::class.java).where().orderBy(Cultivo_Table.CultivoId, false).querySingle()
        return lastCultivo
    }

    fun getLastProducto(): Producto? {
        val lastProducto = SQLite.select().from(Producto::class.java).where().orderBy(Producto_Table.ProductoId, false).querySingle()
        return lastProducto
    }



    //region Events
    private fun postEventOk(type: Int) {
        postEvent(type, null,null,null)
    }



    private fun postEventError(type: Int,messageError:String?) {
        postEvent(type, null,null,messageError)
    }

    //Main Post Event
    private fun postEvent(type: Int, listModel1:MutableList<Object>?,model:Object?,errorMessage: String?) {
        val event = RequestEventProductor(type, listModel1, model, errorMessage)
        event.eventType = type
        eventBus?.post(event)
    }
    //endregion
    //endregion
}