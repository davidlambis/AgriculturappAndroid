package com.interedes.agriculturappv3.modules.productor.asistencia_tecnica_module.UnidadProductiva

import com.interedes.agriculturappv3.modules.models.unidad_productiva.Unidad_Productiva
import com.interedes.agriculturappv3.modules.models.unidad_medida.Unidad_Medida
import com.interedes.agriculturappv3.modules.productor.asistencia_tecnica_module.UnidadProductiva.events.RequestEventUP
import com.interedes.agriculturappv3.libs.EventBus
import com.interedes.agriculturappv3.libs.GreenRobotEventBus
import com.interedes.agriculturappv3.modules.models.control_plaga.ControlPlaga
import com.interedes.agriculturappv3.modules.models.control_plaga.ControlPlaga_Table
import com.interedes.agriculturappv3.modules.models.cultivo.Cultivo
import com.interedes.agriculturappv3.modules.models.cultivo.Cultivo_Table
import com.interedes.agriculturappv3.modules.models.departments.Ciudad
import com.interedes.agriculturappv3.modules.models.departments.Departamento
import com.interedes.agriculturappv3.modules.models.lote.Lote
import com.interedes.agriculturappv3.modules.models.lote.Lote_Table
import com.interedes.agriculturappv3.modules.models.ofertas.DetalleOferta
import com.interedes.agriculturappv3.modules.models.ofertas.DetalleOferta_Table
import com.interedes.agriculturappv3.modules.models.ofertas.Oferta
import com.interedes.agriculturappv3.modules.models.ofertas.Oferta_Table
import com.interedes.agriculturappv3.modules.models.produccion.Produccion
import com.interedes.agriculturappv3.modules.models.produccion.Produccion_Table
import com.interedes.agriculturappv3.modules.models.producto.Producto
import com.interedes.agriculturappv3.modules.models.producto.Producto_Table
import com.interedes.agriculturappv3.modules.models.unidad_medida.Unidad_Medida_Table
import com.interedes.agriculturappv3.modules.models.unidad_productiva.PostUnidadProductiva
import com.interedes.agriculturappv3.modules.models.unidad_productiva.Unidad_Productiva_Table
import com.interedes.agriculturappv3.modules.models.unidad_productiva.localizacion.LocalizacionUp
import com.interedes.agriculturappv3.modules.models.usuario.Usuario
import com.interedes.agriculturappv3.modules.models.usuario.Usuario_Table
import com.interedes.agriculturappv3.modules.models.ventas.Tercero
import com.interedes.agriculturappv3.modules.models.ventas.Tercero_Table
import com.interedes.agriculturappv3.modules.models.ventas.Transaccion
import com.interedes.agriculturappv3.modules.models.ventas.Transaccion_Table
import com.interedes.agriculturappv3.services.api.ApiInterface
import com.raizlabs.android.dbflow.kotlinextensions.delete
import com.raizlabs.android.dbflow.kotlinextensions.save
import com.raizlabs.android.dbflow.kotlinextensions.update
import com.raizlabs.android.dbflow.sql.language.SQLite
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.math.BigDecimal
import java.math.MathContext


class UpRepository() : IUnidadProductiva.Repo {
    var eventBus: EventBus? = null
    var apiService: ApiInterface? = null


    init {
        eventBus = GreenRobotEventBus()
        apiService = ApiInterface.create()
    }

    override fun getListUPs() {
        postEventOk(RequestEventUP.READ_EVENT, getUPs(), null)
    }

    override fun saveUp(mUnidadProductiva: Unidad_Productiva,checkConection:Boolean) {

        //TODO si existe conexion a internet
        if(checkConection){
            val cantidadBig = BigDecimal(mUnidadProductiva.Area!!, MathContext.DECIMAL64)
            //TODO Ciudad Id de la tabla del backend
            val postUnidadProductiva = PostUnidadProductiva(0,
                    cantidadBig,
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
                        val idUP = response.body()?.Id_Remote
                        if(idUP!!>0){
                            mUnidadProductiva?.Id_Remote = idUP

                            //Se crea valor primario en SQlite
                            val last_up = getLastUp()
                            if (last_up == null) {
                                mUnidadProductiva.Unidad_Productiva_Id = 1
                            } else {
                                mUnidadProductiva.Unidad_Productiva_Id = last_up.Unidad_Productiva_Id!! + 1
                            }
                            //mUnidadProductiva?.save()
                            val latitudBig = BigDecimal(mUnidadProductiva.Latitud!!, MathContext.DECIMAL64)
                            val longitudBig = BigDecimal(mUnidadProductiva.Longitud!!, MathContext.DECIMAL64)
//postLocalizacionUnidadProductiva
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
                                        mUnidadProductiva?.save()
                                        //postLocalizacionUnidadProductiva
                                        postEventOk(RequestEventUP.SAVE_EVENT, getUPs(), mUnidadProductiva)
                                    } else {
                                        postEventError(RequestEventUP.ERROR_EVENT, "Comprueba tu conexión")
                                    }
                                }

                                override fun onFailure(call: Call<LocalizacionUp>?, t: Throwable?) {
                                    postEventError(RequestEventUP.ERROR_EVENT, "Comprueba tu conexión")
                                }
                            })

                        }else{
                            postEventError(RequestEventUP.ERROR_EVENT, "Por favor intente nuevamente")
                        }
                        // postEventOk(RequestEventUP.SAVE_EVENT, getUPs(), mUnidadProductiva)
                    } else {
                        postEventError(RequestEventUP.ERROR_EVENT, "Comprueba tu conexión")
                    }
                }

                override fun onFailure(call: Call<Unidad_Productiva>?, t: Throwable?) {
                    postEventError(RequestEventUP.ERROR_EVENT, "Comprueba tu conexión")
                }
            })

        }
        //TODO sin conexion a internet, registro local
        else{
            saveUpLocal(mUnidadProductiva)
        }
    }


    override fun saveUpLocal(mUnidadProductiva: Unidad_Productiva){
        val last_up = getLastUp()
        if (last_up == null) {
            mUnidadProductiva.Unidad_Productiva_Id = 1
        } else {
            mUnidadProductiva.Unidad_Productiva_Id = last_up.Unidad_Productiva_Id!! + 1
        }
        mUnidadProductiva.save()
        postEventOk(RequestEventUP.SAVE_EVENT, getUPs(), mUnidadProductiva)
    }


    override fun updateUp(mUnidadProductiva: Unidad_Productiva,checkConection:Boolean) {
        //TODO si existe coneccion a internet
        if(checkConection){
            //TODO se valida estado de sincronizacion  para actualizar
            if (mUnidadProductiva.Estado_Sincronizacion == true) {

                val areaBig = BigDecimal(mUnidadProductiva.Area!!, MathContext.DECIMAL64)
                val updateUnidadProductiva = PostUnidadProductiva(mUnidadProductiva.Id_Remote,
                        areaBig,
                        mUnidadProductiva.CiudadId,
                        mUnidadProductiva.Codigo,
                        mUnidadProductiva.UnidadMedidaId,
                        mUnidadProductiva.UsuarioId,
                        mUnidadProductiva.descripcion,
                        mUnidadProductiva.nombre)
                // mUnidadProductiva?.CiudadId = 1
                val call = apiService?.updateUnidadProductiva(updateUnidadProductiva, mUnidadProductiva.Id_Remote!!)
                call?.enqueue(object : Callback<Unidad_Productiva> {
                    override fun onResponse(call: Call<Unidad_Productiva>?, response: Response<Unidad_Productiva>?) {
                        if (response != null && response.code() == 200) {

                            val latitudBig = BigDecimal(mUnidadProductiva.Latitud!!, MathContext.DECIMAL64)
                            val longitudBig = BigDecimal(mUnidadProductiva.Longitud!!, MathContext.DECIMAL64)

                            val postLocalizacionUnidadProductiva = LocalizacionUp(mUnidadProductiva.LocalizacionUpId,
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

                            val call = apiService?.updateLocalizacionUnidadProductiva(postLocalizacionUnidadProductiva, mUnidadProductiva.LocalizacionUpId!!)
                            call?.enqueue(object : Callback<LocalizacionUp> {
                                override fun onResponse(call: Call<LocalizacionUp>?, response: Response<LocalizacionUp>?) {
                                    if (response != null && response.code() == 200) {
                                        mUnidadProductiva.Estado_SincronizacionUpdate=true
                                        mUnidadProductiva.update()
                                        postEventOk(RequestEventUP.UPDATE_EVENT, getUPs(), mUnidadProductiva)

                                    } else {
                                        postEventError(RequestEventUP.ERROR_EVENT, "Comprueba tu conexión")
                                    }
                                }
                                override fun onFailure(call: Call<LocalizacionUp>?, t: Throwable?) {
                                    postEventError(RequestEventUP.ERROR_EVENT, "Comprueba tu conexión")
                                }
                            })
                        } else {
                            postEventError(RequestEventUP.ERROR_EVENT, "Comprueba tu conexión")
                        }
                    }
                    override fun onFailure(call: Call<Unidad_Productiva>?, t: Throwable?) {
                        postEventError(RequestEventUP.ERROR_EVENT, "Comprueba tu conexión")
                    }
                })
            }
            //TODO con  conexion a internet, pero no se ha sincronizado
            else {
                mUnidadProductiva.Estado_SincronizacionUpdate=false
                mUnidadProductiva.update()
                postEventOk(RequestEventUP.UPDATE_EVENT, getUPs(), mUnidadProductiva)
                //postEventError(RequestEventUP.ERROR_EVENT, "Error!. La Unidad Productiva no se ha subido")
            }
        }
        //TODO sin conexion a internet, actualizacion local
        else{
            mUnidadProductiva.Estado_SincronizacionUpdate=false
            mUnidadProductiva.update()
            postEventOk(RequestEventUP.UPDATE_EVENT, getUPs(), mUnidadProductiva)
        }
    }

    override fun deleteUp(mUnidadProductiva: Unidad_Productiva,checkConection:Boolean) {
        //TODO se valida estado de sincronizacion  para eliminar
        if (mUnidadProductiva.Estado_Sincronizacion == true) {
            //TODO si existe coneccion a internet se elimina
            if(checkConection){
                val call = apiService?.deleteUnidadProductiva(mUnidadProductiva.Id_Remote)
                call?.enqueue(object : Callback<Unidad_Productiva> {
                    override fun onResponse(call: Call<Unidad_Productiva>?, response: Response<Unidad_Productiva>?) {
                        if (response != null && response.code() == 204) {
                            deleteUp(mUnidadProductiva)
                            postEventOk(RequestEventUP.DELETE_EVENT, getUPs(), mUnidadProductiva)
                        }
                    }
                    override fun onFailure(call: Call<Unidad_Productiva>?, t: Throwable?) {
                        postEventError(RequestEventUP.ERROR_EVENT, "Comprueba tu conexión")
                    }
                })
            }else{

                postEventError(RequestEventUP.ERROR_VERIFICATE_CONECTION, null)
            }
        } else {
            //TODO No sincronizado, Eliminar de manera local
            //Verificate if cultivos register
            deleteUp(mUnidadProductiva)
            postEventOk(RequestEventUP.DELETE_EVENT, getUPs(), mUnidadProductiva)
           /* var vericateRegisterLotes= SQLite.select().from(Lote::class.java).where(Lote_Table.Unidad_Productiva_Id.eq(mUnidadProductiva.Unidad_Productiva_Id)).querySingle()
            if(vericateRegisterLotes!=null){
                postEventError(RequestEventUP.ERROR_EVENT, "Error!. La unidad productiva no se ha podido eliminar, recuerde eliminar los lotes")
            }else{
                mUnidadProductiva.delete()
                postEventOk(RequestEventUP.DELETE_EVENT, getUPs(), mUnidadProductiva)
            }*/
            //mUnidadProductiva.delete()
            //postEventOk(RequestEventUP.DELETE_EVENT, getUPs(), mUnidadProductiva)
        }
    }

    fun deleteUp(mUnidadProductiva: Unidad_Productiva){

        var lotes= SQLite.select().from(Lote::class.java).where(Lote_Table.Unidad_Productiva_Id.eq(mUnidadProductiva.Unidad_Productiva_Id)).queryList()
        for (lote in lotes){
            var cultivos= SQLite.select().from(Cultivo::class.java).where(Cultivo_Table.LoteId.eq(lote.LoteId)).queryList()
            for (cultivo in cultivos){

                SQLite.delete<Produccion>(Produccion::class.java)
                        .where(Produccion_Table.CultivoId.eq(cultivo.CultivoId))
                        .async()
                        .execute()

                SQLite.delete<ControlPlaga>(ControlPlaga::class.java)
                        .where(ControlPlaga_Table.CultivoId.eq(cultivo.CultivoId))
                        .async()
                        .execute()

                var listTransacciones=SQLite.select().from(Transaccion::class.java).where(Transaccion_Table.Cultivo_Id.eq(cultivo?.CultivoId)).queryList()
                for (transaccion in listTransacciones){
                    SQLite.delete<Tercero>(Tercero::class.java)
                            .where(Tercero_Table.TerceroId.eq(transaccion.TerceroId))
                            .async()
                            .execute()
                    transaccion.delete()
                }

                var listProductos=SQLite.select().from(Producto::class.java).where(Producto_Table.cultivoId.eq(cultivo?.CultivoId)).queryList()
                for (producto in listProductos){
                    var listDetalleOferta=SQLite.select().from(DetalleOferta::class.java).where(DetalleOferta_Table.ProductoId.eq(producto?.ProductoId)).queryList()
                    for(detalleoferta in listDetalleOferta){

                        SQLite.delete<Oferta>(Oferta::class.java)
                                .where(Oferta_Table.Oferta_Id.eq(detalleoferta.OfertasId))
                                .async()
                                .execute()

                        detalleoferta.delete()
                    }
                    producto.delete()
                }

                cultivo.delete()
            }

            lote.delete()
        }

        mUnidadProductiva.delete()
    }

    override fun getUPs(): List<Unidad_Productiva> {
        val usuario = getLastUserLogued()
        return SQLite.select().from(Unidad_Productiva::class.java).where(Unidad_Productiva_Table.UsuarioId.eq(usuario?.Id)).queryList()
    }

    override fun getLastUp(): Unidad_Productiva? {
        val lastUnidadProductiva = SQLite.select().from(Unidad_Productiva::class.java)
               // .where(Unidad_Productiva_Table.UsuarioId.eq(getLastUserLogued()?.Id))
                .orderBy(Unidad_Productiva_Table.Unidad_Productiva_Id, false).querySingle()
        return lastUnidadProductiva
    }

    override fun getLastUserLogued(): Usuario? {
        val usuarioLogued = SQLite.select().from(Usuario::class.java).where(Usuario_Table.UsuarioRemembered.eq(true)).querySingle()
        return usuarioLogued
    }

    override fun getListas() {
        val listUnidadMedida = SQLite.select().from(Unidad_Medida::class.java).where(Unidad_Medida_Table.CategoriaMedidaId.eq(1)).queryList()
        val listDepartamentos = SQLite.select().from(Departamento::class.java).queryList()
        val listCiudades = SQLite.select().from(Ciudad::class.java).queryList()
        postEventListUnidadMedida(RequestEventUP.LIST_EVENT_UNIDAD_MEDIDA, listUnidadMedida, null)
        postEventListDepartamentos(RequestEventUP.LIST_EVENT_DEPARTAMENTOS, listDepartamentos, null)
        postEventListCiudades(RequestEventUP.LIST_EVENT_CIUDADES, listCiudades, null)
    }


    //region EVENTS
    private fun postEventListUnidadMedida(type: Int, listUnidadMedida: List<Unidad_Medida>?, messageError: String?) {
        var upMutable = listUnidadMedida as MutableList<Object>
        postEvent(type, upMutable, null, messageError)
    }

    private fun postEventListDepartamentos(type: Int, listDepartamentos: List<Departamento>?, messageError: String?) {
        var departamentoMutable = listDepartamentos as MutableList<Object>
        postEvent(type, departamentoMutable, null, messageError)
    }

    private fun postEventListCiudades(type: Int, listCiudades: List<Ciudad>?, messageError: String?) {
        var ciudadMutable = listCiudades as MutableList<Object>
        postEvent(type, ciudadMutable, null, messageError)
    }

    private fun postEventOk(type: Int, listUnidadProductivas: List<Unidad_Productiva>?, unidadProductiva: Unidad_Productiva?) {
        var UpListMutable = listUnidadProductivas as MutableList<Object>
        var UpMutable: Object? = null
        if (unidadProductiva != null) {
            UpMutable = unidadProductiva as Object
        }
        postEvent(type, UpListMutable, UpMutable, null)
    }

    //Main Post Event
    private fun postEvent(type: Int, listModel: MutableList<Object>?, model: Object?, errorMessage: String?) {
        val event = RequestEventUP(type, listModel, model, errorMessage)
        event.eventType = type
        eventBus?.post(event)
    }

    //Post Event Error
    private fun postEventError(type: Int, messageError: String?) {
        val errorEvent = RequestEventUP(type, null, null, messageError)
        errorEvent.eventType = type
        errorEvent.mensajeError = messageError
        eventBus?.post(errorEvent)
    }

    //endregion
}


