package com.interedes.agriculturappv3.asistencia_tecnica.modules.asistencia_tecnica_module.Produccion.adapter

import android.content.Context
import android.support.v4.content.res.TypedArrayUtils.getString
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.TextView
import com.interedes.agriculturappv3.R
import com.interedes.agriculturappv3.asistencia_tecnica.models.produccion.Produccion
import com.interedes.agriculturappv3.asistencia_tecnica.modules.asistencia_tecnica_module.Produccion.events.RequestEventProduccion
import com.interedes.agriculturappv3.libs.EventBus
import com.interedes.agriculturappv3.libs.GreenRobotEventBus

/**
 * Created by usuario on 21/03/2018.
 */
class ProduccionAdapter(var lista: ArrayList<Produccion>,contextL:Context?) : RecyclerView.Adapter<ProduccionAdapter.ViewHolder>() {

    companion object {
        var instance:  ProduccionAdapter? = null
        var contextAdapter:  Context? = null
    }
    var eventBus: EventBus? = null
    init {
        eventBus = GreenRobotEventBus()
        instance = this
        contextAdapter=contextL
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        //Llama al método del holder para cargar los items
        holder.bindItems(lista[position], position)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val v = LayoutInflater.from(parent.context).inflate(R.layout.content_list_produccion, parent, false)
        return ViewHolder(v)
    }

    override fun getItemCount(): Int {
        return lista.size
    }


    fun setItems(newItems: List<Produccion>) {
        lista.addAll(newItems)
        notifyDataSetChanged()

    }

    fun clear() {
        lista.clear()
        notifyDataSetChanged()
    }


    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        fun bindItems(data: Produccion, pos: Int) = with(itemView) {
            var txtFechaInicioProduccion: TextView = itemView.findViewById(R.id.txtFechaInicio)
            var txtFechaFinProducccion: TextView = itemView.findViewById(R.id.txtFechaFin)
            var txtCantidadProduccion: TextView = itemView.findViewById(R.id.txtCantidadProduccion)
            var btnEditProducccion: ImageButton = itemView.findViewById(R.id.btnEditProducccion)
            var btnDeleteProduccion: ImageButton = itemView.findViewById(R.id.btnDeleteProduccion)

            //image.setImageBitmap(data.Imagen)
            // image.setImageResource(data.Imagen)
            txtFechaInicioProduccion.text = data.getFechaInicioFormat()
            txtFechaFinProducccion.text = data.getFechafinFormat()


            txtCantidadProduccion.text = String.format(contextAdapter?.getString(R.string.cantidad_estimada)!!,data.ProduccionReal, data.NombreUnidadMedida)



///            txtCantidadProduccion.text = String.format(contextAdapter?.getString(R.string.cantidad_estimada)!!, data.ProduccionReal, data.NombreUnidadMedida)

            //El listener en base a la posición
            itemView.setOnClickListener {
                ProduccionAdapter.instance?.postEventc(RequestEventProduccion.ITEM_EVENT,data)
            }

            btnEditProducccion.setOnClickListener {
                ProduccionAdapter.instance?.postEventc(RequestEventProduccion.ITEM_EDIT_EVENT,data)
            }

            btnDeleteProduccion.setOnClickListener {
                ProduccionAdapter.instance?.postEventc(RequestEventProduccion.ITEM_DELETE_EVENT,data)
            }
        }
    }

    fun postEventc(type: Int, produccion: Produccion?) {
        var produccionMutable= produccion as Object
        val event = RequestEventProduccion(type,null, produccionMutable,null)
        event.eventType = type
        eventBus?.post(event)
    }
}





