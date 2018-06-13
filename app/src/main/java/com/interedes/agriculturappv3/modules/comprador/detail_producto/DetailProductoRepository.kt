package com.interedes.agriculturappv3.modules.comprador.detail_producto

import com.interedes.agriculturappv3.libs.EventBus
import com.interedes.agriculturappv3.libs.GreenRobotEventBus
import com.interedes.agriculturappv3.modules.comprador.detail_producto.events.RequestEventDetalleProducto
import com.interedes.agriculturappv3.modules.models.ofertas.*
import com.interedes.agriculturappv3.modules.models.producto.Producto
import com.interedes.agriculturappv3.modules.models.producto.Producto_Table
import com.interedes.agriculturappv3.modules.models.tipoproducto.TipoProducto
import com.interedes.agriculturappv3.modules.models.tipoproducto.TipoProducto_Table
import com.interedes.agriculturappv3.modules.models.unidad_medida.Unidad_Medida
import com.interedes.agriculturappv3.modules.models.unidad_medida.Unidad_Medida_Table
import com.interedes.agriculturappv3.modules.models.usuario.Usuario
import com.interedes.agriculturappv3.modules.models.usuario.Usuario_Table
import com.interedes.agriculturappv3.services.api.ApiInterface
import com.interedes.agriculturappv3.services.resources.CategoriaMediaResources
import com.interedes.agriculturappv3.services.resources.EstadosOfertasResources
import com.raizlabs.android.dbflow.kotlinextensions.save
import com.raizlabs.android.dbflow.sql.language.SQLite
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.math.BigDecimal
import java.math.MathContext

class DetailProductoRepository :IMainViewDetailProducto.Repository {



    var eventBus: EventBus? = null
    var apiService: ApiInterface? = null
    init {
        eventBus = GreenRobotEventBus()
        apiService = ApiInterface.create()
    }

    //region Métodos Interfaz

    override fun getLastUserLogued(): Usuario? {
        val usuarioLogued = SQLite.select().from(Usuario::class.java).where(Usuario_Table.UsuarioRemembered.eq(true)).querySingle()
        return usuarioLogued
    }

    override fun getListas() {
        val listUnidadMedidaPrecios = SQLite.select().from(Unidad_Medida::class.java).where(Unidad_Medida_Table.CategoriaMedidaId.eq(CategoriaMediaResources.Moneda)).queryList()
        postEventListUnidadMedida(RequestEventDetalleProducto.LIST_EVENT_UNIDAD_MEDIDA_PRICE, listUnidadMedidaPrecios, null)
    }

    override fun getProducto(producto_id: Long): Producto? {
        var producto= SQLite.select().from(Producto::class.java).where(Producto_Table.ProductoId.eq(producto_id)).querySingle()
        return producto
    }

    override fun getTipoProducto(tipo_producto_id: Long): TipoProducto? {
        var tipoProducto= SQLite.select().from(TipoProducto::class.java).where(TipoProducto_Table.Id.eq(tipo_producto_id)).querySingle()
        return tipoProducto
    }

    override fun verificateCantProducto(producto_id:Long?,cantidad:Double?):Boolean? {
        var response= false
        var producto= SQLite.select().from(Producto::class.java).where(Producto_Table.ProductoId.eq(producto_id)).querySingle()
        if(cantidad!!<=producto?.Stock!!){
            response=true
        }
        return response
    }


    //OFERTA
    override fun postOferta(oferta: Oferta, checkConection:Boolean){

        oferta.UsuarioId=getLastUserLogued()?.Id
        //TODO si existe conexion a internet
        if(checkConection){

            val postOferta = PostOferta(
                    0,
                    oferta.CreatedOn,
                    EstadosOfertasResources.VIGENTE,
                    oferta.UpdatedOn,
                    oferta.UsuarioId,
                    oferta.UsuarioTo
            )
            val call = apiService?.postOfertaComprador(postOferta)
            call?.enqueue(object : Callback<PostOferta> {
                override fun onResponse(call: Call<PostOferta>?, response: Response<PostOferta>?) {
                    if (response != null && response.code() == 201 || response?.code() == 200) {

                        oferta.Id_Remote = response.body()?.Id
                        val varloTotalOfertaBig = BigDecimal(oferta.Valor_Oferta!!, MathContext.DECIMAL64)
                        val postDetalleOferta = PostDetalleOferta(
                                0,
                                oferta.CalidadId,
                                oferta.Cantidad,
                                oferta.Id_Remote,
                                oferta.ProductoId,
                                oferta.UnidadMedidaId,
                                varloTotalOfertaBig,
                                varloTotalOfertaBig,
                                varloTotalOfertaBig
                        )
                        val call = apiService?.postDetalleOfertaComprador(postDetalleOferta)
                        call?.enqueue(object : Callback<PostDetalleOferta> {
                            override fun onResponse(call: Call<PostDetalleOferta>?, response: Response<PostDetalleOferta>?) {
                                if (response != null && response.code() == 201 || response?.code() == 200) {
                                    var response = response.body()


                                    var detalleOferta=DetalleOferta()
                                    detalleOferta.Id_Remote=response?.Id
                                    detalleOferta.OfertasId=oferta?.Id_Remote
                                    detalleOferta.Cantidad= oferta.Cantidad
                                    detalleOferta.CalidadId=oferta.CalidadId
                                    detalleOferta.ProductoId=oferta.ProductoId
                                    detalleOferta.UnidadMedidaId=oferta.UnidadMedidaId
                                    detalleOferta.Valor_Oferta=oferta.Valor_Oferta
                                    detalleOferta.Valor_minimo=oferta.Valor_Oferta
                                    detalleOferta.Valor_transaccion=oferta.Valor_Oferta
                                    detalleOferta.NombreUnidadMedidaPrecio=oferta.NombreUnidadMedidaPrecio

                                    oferta.Nombre_Estado_Oferta=EstadosOfertasResources.VIGENTE_STRING

                                    saveOfertaLocal(oferta,detalleOferta)

                                } else {
                                    postEventError(RequestEventDetalleProducto.ERROR_EVENT, "Comprueba tu conexión")
                                }
                            }
                            override fun onFailure(call: Call<PostDetalleOferta>?, t: Throwable?) {
                                postEventError(RequestEventDetalleProducto.ERROR_EVENT, "Comprueba tu conexión")
                            }
                        })


                    } else {
                        postEventError(RequestEventDetalleProducto.ERROR_EVENT, "Comprueba tu conexión")
                    }
                }
                override fun onFailure(call: Call<PostOferta>?, t: Throwable?) {
                    postEventError(RequestEventDetalleProducto.ERROR_EVENT, "Comprueba tu conexión")
                }
            })
        }else{
            postEvent(RequestEventDetalleProducto.ERROR_VERIFICATE_CONECTION,null,null,null)
        }
    }



    override fun saveOfertaLocal(oferta: Oferta, detalleOferta: DetalleOferta){

        val last_oferta = getLastOferta()
        if (last_oferta == null) {
            oferta.Oferta_Id = 1
        } else {
            oferta.Oferta_Id = last_oferta.Oferta_Id!! + 1
        }


        val last_detalle_oferta = getLastDetalleOferta()
        if (last_detalle_oferta == null) {
            detalleOferta.Detalle_Oferta_Id = 1
        } else {
            detalleOferta.Detalle_Oferta_Id = last_detalle_oferta.Detalle_Oferta_Id!! + 1
        }


        oferta.save()
        detalleOferta.OfertasId=oferta.Oferta_Id
        detalleOferta.save()

        postEvent(RequestEventDetalleProducto.OK_SEND_EVENT_OFERTA, null, null,null)

    }


    override fun getLastOferta(): Oferta? {
        val lastOferta = SQLite.select().from(Oferta::class.java).where().orderBy(Oferta_Table.Oferta_Id, false).querySingle()
        return lastOferta
    }

    override fun getLastDetalleOferta(): DetalleOferta? {
        val lastDetalleOferta = SQLite.select().from(DetalleOferta::class.java).where().orderBy(DetalleOferta_Table.Detalle_Oferta_Id, false).querySingle()
        return lastDetalleOferta
    }

    //region Events

    private fun postEventListUnidadMedida(type: Int, listUnidadMedida: List<Unidad_Medida>?, messageError: String?) {
        val upMutable = listUnidadMedida as MutableList<Object>
        postEvent(type, upMutable, null, messageError)
    }


    private fun postEventOk(type: Int, listProducto: List<Producto>?, producto: Producto?) {
        val productoListMutable = listProducto as MutableList<Object>
        var productoMutable: Object? = null
        if (producto != null) {
            productoMutable = producto as Object
        }
        postEvent(type, productoListMutable, productoMutable, null)
    }


    private fun postEventError(type: Int,messageError:String?) {
        postEvent(type, null,null,messageError)
    }

    //Main Post Event
    private fun postEvent(type: Int, listModel1:MutableList<Object>?,model:Object?,errorMessage: String?) {
        val event = RequestEventDetalleProducto(type, listModel1, model, errorMessage)
        event.eventType = type
        eventBus?.post(event)
    }
    //endregion
    //endregion

}