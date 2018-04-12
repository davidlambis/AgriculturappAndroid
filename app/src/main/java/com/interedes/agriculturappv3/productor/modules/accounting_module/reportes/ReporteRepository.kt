package com.interedes.agriculturappv3.productor.modules.accounting_module.reportes

import com.interedes.agriculturappv3.libs.EventBus
import com.interedes.agriculturappv3.libs.GreenRobotEventBus
import com.interedes.agriculturappv3.productor.models.Cultivo
import com.interedes.agriculturappv3.productor.models.Cultivo_Table
import com.interedes.agriculturappv3.productor.models.Lote
import com.interedes.agriculturappv3.productor.models.UnidadProductiva
import com.interedes.agriculturappv3.productor.models.unidad_medida.Unidad_Medida
import com.interedes.agriculturappv3.productor.models.ventas.CategoriaPuk
import com.interedes.agriculturappv3.productor.models.ventas.Transaccion
import com.interedes.agriculturappv3.productor.models.ventas.Transaccion_Table
import com.interedes.agriculturappv3.productor.modules.accounting_module.reportes.events.RequestEventReporte
import com.interedes.agriculturappv3.services.listas.Listas
import com.raizlabs.android.dbflow.sql.language.SQLite

class ReporteRepository: IMainViewReportes.Repository {


    var eventBus: EventBus? = null
    init {
        eventBus = GreenRobotEventBus()
    }


    override fun getTotalTransacciones(cultivo_id: Long?) {

        var listCategoriaPuk=ArrayList<CategoriaPuk>()
        if(cultivo_id==null){
            var categoriaPukList=Listas.listCategoriaPuk()

            var listTransaciones=ArrayList<Transaccion>()
            for (itemCategorias in categoriaPukList){
                var transaccion= SQLite.select().from(Transaccion::class.java!!).where(Transaccion_Table.CategoriaPuk_Id.eq(itemCategorias.Id)).queryList()
                for (item in transaccion){
                    listTransaciones.add(item)
                }
                var categoria= CategoriaPuk(itemCategorias.Id, itemCategorias.Nombre,itemCategorias.Sigla,listTransaciones)
                listCategoriaPuk.add(categoria)
            }
        }else{

        }

        postEventListCategorias(RequestEventReporte.LIST_EVENT_REPORT_CATEGORIAS,listCategoriaPuk,null);
    }

    override fun getListas() {
        var listUnidadProductiva = SQLite.select().from(UnidadProductiva::class.java!!).queryList()
        var listLotes = SQLite.select().from(Lote::class.java!!).queryList()
        var listCultivos = SQLite.select().from(Cultivo::class.java!!).queryList()

        postEventListUnidadProductiva(RequestEventReporte.LIST_EVENT_UP,listUnidadProductiva,null);
        postEventListLotes(RequestEventReporte.LIST_EVENT_LOTE,listLotes,null);
        postEventListCultivos(RequestEventReporte.LIST_EVENT_CULTIVO,listCultivos,null);

    }

    override fun getCultivo(cultivo_id: Long?) {
        var cultivo = SQLite.select().from(Cultivo::class.java!!).where(Cultivo_Table.Id.eq(cultivo_id)).querySingle()
        postEventOkCultivo(RequestEventReporte.GET_EVENT_CULTIVO,cultivo)
    }


    //region Events
    private fun postEventListCategorias(type: Int, listCategoriaPuk:List<CategoriaPuk>?, messageError:String?) {
        var upMutable= listCategoriaPuk as MutableList<Object>
        postEvent(type, upMutable,null,messageError)
    }

    private fun postEventListUnidadMedida(type: Int, listUnidadMedida:List<Unidad_Medida>?, messageError:String?) {
        var upMutable= listUnidadMedida as MutableList<Object>
        postEvent(type, upMutable,null,messageError)
    }

    private fun postEventListUnidadProductiva(type: Int, listUnidadMedida:List<UnidadProductiva>?, messageError:String?) {
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




    private fun postEventOk(type: Int, transacciones: List<Transaccion>?, trasaccion: Transaccion?) {
        var transaccionListMitable= transacciones as MutableList<Object>
        var ProducciconMutable:Object?=null
        if(trasaccion!=null){
            ProducciconMutable = trasaccion as Object
        }
        postEvent(type, transaccionListMitable,ProducciconMutable,null)
    }

    private fun postEventOkCultivo(type: Int,  cultivo:Cultivo?) {
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
        val event = RequestEventReporte(type, listModel1, model, errorMessage)
        event.eventType = type
        eventBus?.post(event)
    }
    //endregion
}