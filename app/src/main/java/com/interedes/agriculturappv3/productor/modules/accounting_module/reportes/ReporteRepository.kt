package com.interedes.agriculturappv3.productor.modules.accounting_module.reportes

import com.interedes.agriculturappv3.libs.EventBus
import com.interedes.agriculturappv3.libs.GreenRobotEventBus
import com.interedes.agriculturappv3.productor.models.cultivo.Cultivo
import com.interedes.agriculturappv3.productor.models.cultivo.Cultivo_Table
import com.interedes.agriculturappv3.productor.models.lote.Lote
import com.interedes.agriculturappv3.productor.models.unidad_productiva.Unidad_Productiva
import com.interedes.agriculturappv3.productor.models.unidad_medida.Unidad_Medida
import com.interedes.agriculturappv3.productor.models.ventas.CategoriaPuk
import com.interedes.agriculturappv3.productor.models.ventas.Transaccion
import com.interedes.agriculturappv3.productor.models.ventas.Transaccion_Table
import com.interedes.agriculturappv3.productor.models.ventas.resports.BalanceContable
import com.interedes.agriculturappv3.productor.models.ventas.resports.CountOfPost
import com.interedes.agriculturappv3.productor.modules.accounting_module.reportes.events.RequestEventReporte
import com.interedes.agriculturappv3.services.listas.Listas
import com.interedes.agriculturappv3.services.resources.CategoriaPukResources
import com.raizlabs.android.dbflow.sql.language.Method
import com.raizlabs.android.dbflow.sql.language.SQLite
import java.util.*
import java.text.SimpleDateFormat


class ReporteRepository: IMainViewReportes.Repository {


    var eventBus: EventBus? = null
    init {
        eventBus = GreenRobotEventBus()
    }


    override fun getTotalTransacciones(cultivo_id: Long?,  dateStart: Date?, dateEnd: Date?) {
        var listCategoriaPuk=ArrayList<CategoriaPuk>()
        var categoriaPukList=Listas.listCategoriaPuk()

        var sumIngresos:CountOfPost?=null
        var sumEgresos:CountOfPost?=null
        var balance:Double?=null

        if(cultivo_id==null && dateStart==null && dateEnd==null){
            for (itemCategorias in categoriaPukList){
                var listTransaciones=ArrayList<Transaccion>()
                var valor_total= 0.0
                var transaccion= SQLite.select().from(Transaccion::class.java!!).where(Transaccion_Table.CategoriaPuk_Id.eq(itemCategorias.Id)).queryList()
                for (item in transaccion){
                    listTransaciones.add(item)
                    valor_total= valor_total!!+item.Valor_Total!!
                }
                var categoria= CategoriaPuk(itemCategorias.Id, itemCategorias.Nombre,itemCategorias.Sigla,valor_total,null,listTransaciones)
                listCategoriaPuk.add(categoria)
            }

            sumIngresos = SQLite.select(
                    Method.sum(Transaccion_Table.Valor).`as`("count"))
                    .from<Transaccion>(Transaccion::class.java)
                    .where(Transaccion_Table.CategoriaPuk_Id.eq(CategoriaPukResources.INGRESO))
                    .queryCustomSingle(CountOfPost::class.java)


            sumEgresos = SQLite.select(
                    Method.sum(Transaccion_Table.Valor).`as`("count"))
                    .from<Transaccion>(Transaccion::class.java)
                    .where(Transaccion_Table.CategoriaPuk_Id.eq(CategoriaPukResources.GASTO))
                    .queryCustomSingle(CountOfPost::class.java)

            balance= sumIngresos?.count!!-sumEgresos?.count!!


        }else if(cultivo_id!=null && dateStart==null && dateEnd==null){
            for (itemCategorias in categoriaPukList){
                var listTransaciones=ArrayList<Transaccion>()
                var valor_total= 0.0
                var transaccion= SQLite.select().from(Transaccion::class.java!!).where(Transaccion_Table.CategoriaPuk_Id.eq(itemCategorias.Id)).and(Transaccion_Table.Cultivo_Id.eq(cultivo_id)).queryList()
                for (item in transaccion){
                    listTransaciones.add(item)
                    valor_total= valor_total!!+item.Valor_Total!!
                }
                var categoria= CategoriaPuk(itemCategorias.Id, itemCategorias.Nombre,itemCategorias.Sigla,valor_total,null,listTransaciones)
                listCategoriaPuk.add(categoria)
            }


            sumIngresos = SQLite.select(
                    Method.sum(Transaccion_Table.Valor).`as`("count"))
                    .from<Transaccion>(Transaccion::class.java)
                    .where(Transaccion_Table.CategoriaPuk_Id.eq(CategoriaPukResources.INGRESO))
                    .and(Transaccion_Table.Cultivo_Id.eq(cultivo_id))
                    .queryCustomSingle(CountOfPost::class.java)


            sumEgresos = SQLite.select(
                    Method.sum(Transaccion_Table.Valor).`as`("count"))
                    .from<Transaccion>(Transaccion::class.java)
                    .where(Transaccion_Table.CategoriaPuk_Id.eq(CategoriaPukResources.GASTO))
                    .and(Transaccion_Table.Cultivo_Id.eq(cultivo_id))
                    .queryCustomSingle(CountOfPost::class.java)

            balance= sumIngresos?.count!!-sumEgresos?.count!!
        }

        else if(cultivo_id==null && dateStart!=null && dateEnd!=null){

            var format1 = SimpleDateFormat("MM-dd-yyyy")
            var formattedStart = format1.format(dateStart.getTime())
            var formattedEnd = format1.format(dateEnd.getTime())



            for (itemCategorias in categoriaPukList){
                var listTransaciones=ArrayList<Transaccion>()
                var valor_total= 0.0
                var transaccion= SQLite.select().from(Transaccion::class.java!!)
                        .where(Transaccion_Table.CategoriaPuk_Id.eq(itemCategorias.Id))
                        .and(Transaccion_Table.FechaString.between(formattedStart).and(formattedEnd))
                        .queryList()
                for (item in transaccion){
                    listTransaciones.add(item)
                    valor_total= valor_total!!+item.Valor_Total!!
                }
                var categoria= CategoriaPuk(itemCategorias.Id, itemCategorias.Nombre,itemCategorias.Sigla,valor_total,null,listTransaciones)
                listCategoriaPuk.add(categoria)
            }


            sumIngresos = SQLite.select(
                    Method.sum(Transaccion_Table.Valor).`as`("count"))
                    .from<Transaccion>(Transaccion::class.java)
                    .where(Transaccion_Table.CategoriaPuk_Id.eq(CategoriaPukResources.INGRESO))
                    .and(Transaccion_Table.FechaString.between(formattedStart).and(formattedEnd))
                    .queryCustomSingle(CountOfPost::class.java)

            sumEgresos = SQLite.select(
                    Method.sum(Transaccion_Table.Valor).`as`("count"))
                    .from<Transaccion>(Transaccion::class.java)
                    .where(Transaccion_Table.CategoriaPuk_Id.eq(CategoriaPukResources.GASTO))
                    .and(Transaccion_Table.FechaString.between(formattedStart).and(formattedEnd))
                    .queryCustomSingle(CountOfPost::class.java)

            balance= sumIngresos?.count!!-sumEgresos?.count!!
        }

        else if(cultivo_id!=null && dateStart!=null && dateEnd!=null){

            var format1 = SimpleDateFormat("MM-dd-yyyy")
            var formattedStart = format1.format(dateStart.getTime())
            var formattedEnd = format1.format(dateEnd.getTime())

            for (itemCategorias in categoriaPukList){
                var listTransaciones=ArrayList<Transaccion>()
                var valor_total= 0.0
                var transaccion= SQLite.select().from(Transaccion::class.java!!)
                        .where(Transaccion_Table.CategoriaPuk_Id.eq(itemCategorias.Id))
                        .and(Transaccion_Table.Cultivo_Id.eq(cultivo_id))
                        .and(Transaccion_Table.FechaString.between(formattedStart).and(formattedEnd))
                        .queryList()
                for (item in transaccion){
                    listTransaciones.add(item)
                    valor_total= valor_total!!+item.Valor_Total!!
                }
                var categoria= CategoriaPuk(itemCategorias.Id, itemCategorias.Nombre,itemCategorias.Sigla,valor_total,null,listTransaciones)
                listCategoriaPuk.add(categoria)
            }


            sumIngresos = SQLite.select(
                    Method.sum(Transaccion_Table.Valor).`as`("count"))
                    .from<Transaccion>(Transaccion::class.java)
                    .where(Transaccion_Table.CategoriaPuk_Id.eq(CategoriaPukResources.INGRESO))
                    .and(Transaccion_Table.Cultivo_Id.eq(cultivo_id))
                    .and(Transaccion_Table.FechaString.between(formattedStart).and(formattedEnd))
                    .queryCustomSingle(CountOfPost::class.java)

            sumEgresos = SQLite.select(
                    Method.sum(Transaccion_Table.Valor).`as`("count"))
                    .from<Transaccion>(Transaccion::class.java)
                    .where(Transaccion_Table.CategoriaPuk_Id.eq(CategoriaPukResources.GASTO))
                    .and(Transaccion_Table.Cultivo_Id.eq(cultivo_id))
                    .and(Transaccion_Table.FechaString.between(formattedStart).and(formattedEnd))
                    .queryCustomSingle(CountOfPost::class.java)

            balance= sumIngresos?.count!!-sumEgresos?.count!!
        }




        var balanceContable= BalanceContable(sumIngresos?.count!!,sumEgresos?.count!!,balance)
        postEventOkBalanceContable(RequestEventReporte.EVENT_BALANCE_CONTABLE,balanceContable)
        postEventListCategorias(RequestEventReporte.LIST_EVENT_REPORT_CATEGORIAS,listCategoriaPuk,null)

    }

    override fun getListas() {
        var listUnidadProductiva = SQLite.select().from(Unidad_Productiva::class.java!!).queryList()
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

    private fun postEventOkBalanceContable(type: Int,  balance: BalanceContable?) {
        var BalanceMutable:Object?=null
        if(balance!=null){
            BalanceMutable = balance as Object
        }
        postEvent(type, null,BalanceMutable,null)
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
        val event = RequestEventReporte(type, listModel1, model, errorMessage)
        event.eventType = type
        eventBus?.post(event)
    }
    //endregion
}