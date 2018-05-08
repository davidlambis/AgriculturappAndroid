package com.interedes.agriculturappv3.productor.modules.accounting_module.transacciones

import com.interedes.agriculturappv3.productor.models.ventas.CategoriaPuk
import com.interedes.agriculturappv3.productor.models.ventas.Puk
import com.interedes.agriculturappv3.productor.models.ventas.Transaccion
import com.interedes.agriculturappv3.productor.modules.accounting_module.transacciones.events.RequestEventTransaccion
import com.interedes.agriculturappv3.libs.EventBus
import com.interedes.agriculturappv3.libs.GreenRobotEventBus
import com.interedes.agriculturappv3.productor.models.cultivo.Cultivo
import com.interedes.agriculturappv3.productor.models.cultivo.Cultivo_Table
import com.interedes.agriculturappv3.productor.models.lote.Lote
import com.interedes.agriculturappv3.productor.models.unidad_productiva.Unidad_Productiva
import com.interedes.agriculturappv3.productor.models.unidad_medida.Unidad_Medida
import com.interedes.agriculturappv3.productor.models.ventas.Transaccion_Table
import com.interedes.agriculturappv3.services.listas.Listas
import com.raizlabs.android.dbflow.kotlinextensions.delete
import com.raizlabs.android.dbflow.kotlinextensions.save
import com.raizlabs.android.dbflow.kotlinextensions.update
import com.raizlabs.android.dbflow.sql.language.SQLite

class TransaccionRespository: IMainViewTransacciones.Repository {


    var eventBus: EventBus? = null
    init {
        eventBus = GreenRobotEventBus()
    }

    override fun getListas() {
        var listUnidadProductiva = SQLite.select().from(Unidad_Productiva::class.java!!).queryList()
        var listLotes = SQLite.select().from(Lote::class.java!!).queryList()
        var listCultivos = SQLite.select().from(Cultivo::class.java!!).queryList()
        var listUnidadMedida= Listas.listaUnidadMedida()

        var listCategoriasPuk= Listas.listCategoriaPuk()
        var listPuk= Listas.listPuk()

        postEventListUnidadMedida(RequestEventTransaccion.LIST_EVENT_UNIDAD_MEDIDA,listUnidadMedida,null);
        postEventListUnidadProductiva(RequestEventTransaccion.LIST_EVENT_UP,listUnidadProductiva,null);
        postEventListLotes(RequestEventTransaccion.LIST_EVENT_LOTE,listLotes,null);
        postEventListCultivos(RequestEventTransaccion.LIST_EVENT_CULTIVO,listCultivos,null);

        postEventListCategoriaPuk(RequestEventTransaccion.LIST_EVENT_CATEGORIA_PUK,listCategoriasPuk,null);
        postEventListPuk(RequestEventTransaccion.LIST_EVENT_PUK,listPuk,null);
    }

    override fun getListTransacciones(cultivo_id: Long?,typeTransaccion:Long?) {
        var listaProduccion = getTransaccion(cultivo_id,typeTransaccion)
        postEventOk(RequestEventTransaccion.READ_EVENT,listaProduccion,null);
    }

    override fun getTransaccion(cultivo_id: Long?,typeTransaccion:Long?): List<Transaccion> {
        var listResponse:List<Transaccion>?=null
        if(cultivo_id==null){
            listResponse = SQLite.select().from(Transaccion::class.java!!).where(Transaccion_Table.CategoriaPuk_Id.eq(typeTransaccion)).queryList()
        }else{
            listResponse = SQLite.select().from(Transaccion::class.java!!).where(Transaccion_Table.Cultivo_Id.eq(cultivo_id)).and(Transaccion_Table.CategoriaPuk_Id.eq(typeTransaccion)).queryList()
        }
        return listResponse;
    }

    override fun saveTransaccion(transaccion: Transaccion, cultivo_id: Long?) {
        transaccion.save()
        var listProduccion = getTransaccion(cultivo_id,transaccion.CategoriaPuk_Id)
        postEventOk(RequestEventTransaccion.SAVE_EVENT,listProduccion,null);
    }

    override fun updateTransaccion(transaccion: Transaccion, cultivo_id: Long?) {
        transaccion.update()
        postEventOk(RequestEventTransaccion.UPDATE_EVENT, getTransaccion(cultivo_id,transaccion.CategoriaPuk_Id),transaccion);
    }

    override fun deleteTransaccion(transaccion: Transaccion, cultivo_id: Long?) {
        transaccion.delete()
        //SQLite.delete<Lote>(Lote::class.java).where(Lote_Table.Id.eq(lote.Id)).async().execute()
        postEventOk(RequestEventTransaccion.DELETE_EVENT, getTransaccion(cultivo_id,transaccion.CategoriaPuk_Id),transaccion);
    }

    override fun getCultivo(cultivo_id: Long?) {
        var cultivo = SQLite.select().from(Cultivo::class.java!!).where(Cultivo_Table.Id.eq(cultivo_id)).querySingle()
        postEventOkCultivo(RequestEventTransaccion.GET_EVENT_CULTIVO,cultivo)
    }

    //region Events
    private fun postEventListUnidadMedida(type: Int, listUnidadMedida:List<Unidad_Medida>?, messageError:String?) {
        var upMutable= listUnidadMedida as MutableList<Object>
        postEvent(type, upMutable,null,messageError)
    }

    private fun postEventListUnidadProductiva(type: Int, listUnidadMedida:List<Unidad_Productiva>?, messageError:String?) {
        var upMutable= listUnidadMedida as MutableList<Object>
        postEvent(type, upMutable,null,messageError)
    }

    private fun postEventListLotes(type: Int, listUnidadMedida:List<Lote>?, messageError:String?) {
        var upMutable= listUnidadMedida as MutableList<Object>
        postEvent(type, upMutable,null,messageError)
    }

    private fun postEventListCultivos(type: Int, listUnidadMedida:List<Cultivo>?, messageError:String?) {
        var upMutable= listUnidadMedida as MutableList<Object>
        postEvent(type, upMutable,null,messageError)
    }

    private fun postEventListCategoriaPuk(type: Int, listCategoriaPuk:List<CategoriaPuk>?, messageError:String?) {
        var upMutable= listCategoriaPuk as MutableList<Object>
        postEvent(type, upMutable,null,messageError)
    }


    private fun postEventListPuk(type: Int, listPuk:List<Puk>?, messageError:String?) {
        var upMutable= listPuk as MutableList<Object>
        postEvent(type, upMutable,null,messageError)
    }


    private fun postEventOk(type: Int, transacciones: List<Transaccion>?, trasaccion: Transaccion?) {
        var transaccionListMitable= transacciones as MutableList<Object>
        var ProducciconMutable:Object?=null
        if(trasaccion!=null){
            ProducciconMutable = trasaccion as Object
        }
        postEvent(type, transaccionListMitable,ProducciconMutable,null)
    }

    private fun postEventOkCultivo(type: Int,  cultivo: Cultivo?) {
        var CultivoMutable:Object?=null
        if(cultivo!=null){
            CultivoMutable = cultivo as Object
        }
        postEvent(type,null,CultivoMutable,null)
    }


    private fun postEventError(type: Int,messageError:String) {
        postEvent(type, null,null,messageError)
    }

    //Main Post Event
    private fun postEvent(type: Int, listModel1:MutableList<Object>?,model:Object?,errorMessage: String?) {
        val event = RequestEventTransaccion(type, listModel1, model, errorMessage)
        event.eventType = type
        eventBus?.post(event)
    }
    //endregion
}