package com.interedes.agriculturappv3.modules.productor.comercial_module.productos

import android.util.Log
import com.interedes.agriculturappv3.libs.EventBus
import com.interedes.agriculturappv3.libs.GreenRobotEventBus
import com.interedes.agriculturappv3.modules.models.cultivo.Cultivo
import com.interedes.agriculturappv3.modules.models.cultivo.Cultivo_Table
import com.interedes.agriculturappv3.modules.models.lote.Lote
import com.interedes.agriculturappv3.modules.models.producto.CalidadProducto
import com.interedes.agriculturappv3.modules.models.producto.PostProducto
import com.interedes.agriculturappv3.modules.models.producto.Producto
import com.interedes.agriculturappv3.modules.models.producto.Producto_Table
import com.interedes.agriculturappv3.modules.models.unidad_medida.Unidad_Medida
import com.interedes.agriculturappv3.modules.models.unidad_medida.Unidad_Medida_Table
import com.interedes.agriculturappv3.modules.models.unidad_productiva.Unidad_Productiva
import com.interedes.agriculturappv3.modules.productor.comercial_module.productos.events.ProductosEvent
import com.interedes.agriculturappv3.services.api.ApiInterface
import com.raizlabs.android.dbflow.kotlinextensions.delete
import com.raizlabs.android.dbflow.kotlinextensions.save
import com.raizlabs.android.dbflow.kotlinextensions.update
import com.raizlabs.android.dbflow.sql.language.SQLite
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response


class ProductosRepository : IProductos.Repository {
    var eventBus: EventBus? = null
    var apiService: ApiInterface? = null

    init {
        eventBus = GreenRobotEventBus()
        apiService = ApiInterface.create()
    }

    //region Métodos Interfaz
    override fun getListas() {
        val listUnidadProductiva = SQLite.select().from(Unidad_Productiva::class.java).queryList()
        val listLotes = SQLite.select().from(Lote::class.java).queryList()
        val listCultivos = SQLite.select().from(Cultivo::class.java).queryList()
        //val listUnidadMedida = Listas.listaUnidadMedida()
        val listUnidadMedida = SQLite.select().from(Unidad_Medida::class.java).where(Unidad_Medida_Table.CategoriaMedidaId.eq(2)).queryList()
        val listCalidadesProducto = SQLite.select().from(CalidadProducto::class.java).queryList()


        postEventListUnidadMedida(ProductosEvent.LIST_EVENT_UNIDAD_MEDIDA, listUnidadMedida, null)
        postEventListUnidadProductiva(ProductosEvent.LIST_EVENT_UP, listUnidadProductiva, null)
        postEventListLotes(ProductosEvent.LIST_EVENT_LOTE, listLotes, null)
        postEventListCultivos(ProductosEvent.LIST_EVENT_CULTIVO, listCultivos, null)
        postEventListCalidades(ProductosEvent.LIST_EVENT_CALIDADES, listCalidadesProducto, null)
    }

    override fun getListProducto(cultivo_id: Long?) {
        val listaProductos = getProductos(cultivo_id)
        postEventOk(ProductosEvent.READ_EVENT, listaProductos, null);
    }

    override fun getProductos(cultivo_id: Long?): List<Producto> {
        var listResponse: List<Producto>? = null
        if (cultivo_id == null) {
            listResponse = SQLite.select().from(Producto::class.java).queryList()
        } else {
            listResponse = SQLite.select().from(Producto::class.java).where(Producto_Table.cultivoId.eq(cultivo_id)).queryList()
        }
        return listResponse;
    }

    override fun registerProducto(mProducto: Producto, cultivo_id: Long) {
        val last_producto = getLastProducto()
        if (last_producto == null) {
            mProducto.Id = 1
        } else {
            mProducto.Id = last_producto.Id!! + 1
        }
        postEventOk(ProductosEvent.SAVE_EVENT, getProductos(cultivo_id), null)
    }

    override fun registerOnlineProducto(mProducto: Producto, cultivo_id: Long) {

        val cultivo = SQLite.select().from(Cultivo::class.java).where(Cultivo_Table.Id.eq(cultivo_id)).querySingle()
        if (cultivo?.EstadoSincronizacion == true) {
            val postProducto = PostProducto(mProducto.Id,
                    mProducto.CalidadId,
                    1,
                    mProducto.CodigoUp,
                    mProducto.Descripcion,
                    mProducto.FechaLimiteDisponibilidad,
                    mProducto.Imagen,
                    false,
                    mProducto.Precio,
                    mProducto.PrecioSpecial,
                    mProducto.Stock,
                    mProducto.cultivoId,
                    null)

            val call = apiService?.postProducto(postProducto)
            call?.enqueue(object : Callback<Producto> {
                override fun onResponse(call: Call<Producto>?, response: Response<Producto>?) {
                    if (response != null && response.code() == 201) {
                        val value = response.body()
                        mProducto.Id = value?.Id!!
                        mProducto.EstadoSincronizacion = true
                        mProducto.save()
                        postEventOk(ProductosEvent.SAVE_EVENT, getProductos(cultivo_id), null)
                    } else {
                        postEventError(ProductosEvent.ERROR_EVENT, "Comprueba tu conexión")
                        Log.e("error", response?.message().toString())
                    }
                }

                override fun onFailure(call: Call<Producto>?, t: Throwable?) {
                    postEventError(ProductosEvent.ERROR_EVENT, "Comprueba tu conexión")
                }

            })


        } else {
            registerProducto(mProducto, cultivo_id)
            postEventOk(ProductosEvent.SAVE_EVENT, getProductos(cultivo_id), null)
        }

    }

    override fun updateProducto(mProducto: Producto, cultivo_id: Long) {
        if (mProducto.EstadoSincronizacion == true) {
            val postProducto = PostProducto(mProducto.Id,
                    mProducto.CalidadId,
                    1,
                    mProducto.CodigoUp,
                    mProducto.Descripcion,
                    mProducto.FechaLimiteDisponibilidad,
                    mProducto.Imagen,
                    false,
                    mProducto.Precio,
                    mProducto.PrecioSpecial,
                    mProducto.Stock,
                    mProducto.cultivoId,
                    null)

            val call = apiService?.updateProducto(postProducto, mProducto.Id!!)
            call?.enqueue(object : Callback<Producto> {
                override fun onResponse(call: Call<Producto>?, response: Response<Producto>?) {
                    if (response != null && response.code() == 200) {
                        mProducto.update()
                        postEventOk(ProductosEvent.SAVE_EVENT, getProductos(cultivo_id), null)
                    } else {
                        postEventError(ProductosEvent.ERROR_EVENT, "Comprueba tu conexión")
                    }
                }

                override fun onFailure(call: Call<Producto>?, t: Throwable?) {
                    postEventError(ProductosEvent.ERROR_EVENT, "Comprueba tu conexión")
                }
            })
        } else {
            postEventError(ProductosEvent.ERROR_EVENT, "Error!. El Producto no se ha subido")
        }
    }

    override fun deleteProducto(mProducto: Producto, cultivo_id: Long?) {
        if (mProducto.EstadoSincronizacion == true) {
            val call = apiService?.deleteProducto(mProducto.Id!!)
            call?.enqueue(object : Callback<Producto> {
                override fun onResponse(call: Call<Producto>?, response: Response<Producto>?) {
                    if (response != null && response.code() == 204) {
                        mProducto.delete()
                        postEventOk(ProductosEvent.DELETE_EVENT, getProductos(cultivo_id), null)
                    }
                }

                override fun onFailure(call: Call<Producto>?, t: Throwable?) {
                    postEventError(ProductosEvent.ERROR_EVENT, "Comprueba tu conexión")
                }

            })
        } else {
            postEventError(ProductosEvent.ERROR_EVENT, "Comprueba tu conexión")
        }

    }

    override fun getCultivo(cultivo_id: Long?) {
        val cultivo = SQLite.select().from(Cultivo::class.java).where(Cultivo_Table.Id.eq(cultivo_id)).querySingle()
        postEventOkCultivo(ProductosEvent.GET_EVENT_CULTIVO, cultivo)
    }

    override fun getLastProducto(): Producto? {
        val lastProducto = SQLite.select().from(Producto::class.java).where().orderBy(Producto_Table.Id, false).querySingle()
        return lastProducto
    }


    //endregion

    //region Events

    private fun postEventListUnidadMedida(type: Int, listUnidadMedida: List<Unidad_Medida>?, messageError: String?) {
        val upMutable = listUnidadMedida as MutableList<Object>
        postEvent(type, upMutable, null, messageError)
    }

    private fun postEventListUnidadProductiva(type: Int, listUp: List<Unidad_Productiva>?, messageError: String?) {
        val upMutable = listUp as MutableList<Object>
        postEvent(type, upMutable, null, messageError)
    }

    private fun postEventListLotes(type: Int, listLote: List<Lote>?, messageError: String?) {
        val loteMutable = listLote as MutableList<Object>
        postEvent(type, loteMutable, null, messageError)
    }

    private fun postEventListCultivos(type: Int, listCultivo: List<Cultivo>?, messageError: String?) {
        val cultivoMutable = listCultivo as MutableList<Object>
        postEvent(type, cultivoMutable, null, messageError)
    }

    private fun postEventListCalidades(type: Int, listCalidad: List<CalidadProducto>?, messageError: String?) {
        val calidadMutable = listCalidad as MutableList<Object>
        postEvent(type, calidadMutable, null, messageError)
    }


    private fun postEventError(type: Int, messageError: String?) {
        val event = ProductosEvent(type, null, null, messageError)
        event.eventType = type
        eventBus?.post(event)
    }

    private fun postEventOk(type: Int, productos: List<Producto>?, producto: Producto?) {
        var productoListMitable = productos as MutableList<Object>
        var productoMutable: Object? = null
        if (producto != null) {
            productoMutable = producto as Object
        }
        postEvent(type, productoListMitable, productoMutable, null)
    }

    private fun postEventOkCultivo(type: Int, cultivo: Cultivo?) {
        var CultivoMutable: Object? = null
        if (cultivo != null) {
            CultivoMutable = cultivo as Object
        }
        postEvent(type, null, CultivoMutable, null)
    }

    //Main Post Event
    private fun postEvent(type: Int, listModel1: MutableList<Object>?, model: Object?, errorMessage: String?) {
        val event = ProductosEvent(type, listModel1, model, errorMessage)
        event.eventType = type
        eventBus?.post(event)
    }

    //endregiopn
}