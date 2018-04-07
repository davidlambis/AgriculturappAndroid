package com.interedes.agriculturappv3.productor.modules.asistencia_tecnica_module.Produccion.adapter

import android.content.Context
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.TextView
import com.interedes.agriculturappv3.R
import com.interedes.agriculturappv3.productor.models.produccion.Produccion
import com.interedes.agriculturappv3.productor.modules.asistencia_tecnica_module.Produccion.events.RequestEventProduccion
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
        val v = LayoutInflater.from(parent.context).inflate(R.layout.content_list_general, parent, false)
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



            var txt_title: TextView = itemView.findViewById(R.id.txtTitle)
            var txt_descripcion: TextView = itemView.findViewById(R.id.txtDescription)
            var txtFechasProduccion: TextView = itemView.findViewById(R.id.txtDate)
            var txtCantidadProduccion: TextView = itemView.findViewById(R.id.txtQuantity)
            var txtAdicional: TextView = itemView.findViewById(R.id.txtDescripcionAdditional)
            var icon: ImageView = itemView.findViewById(R.id.contentIcon)

            var btnDelete: ImageButton = itemView.findViewById(R.id.btnAction3)
            var btnAdd: ImageButton = itemView.findViewById(R.id.btnAction1)
            var btnEdit: ImageButton = itemView.findViewById(R.id.btnAction2)



            icon.setImageResource(R.drawable.ic_produccion_cultivo)
            txtAdicional.visibility=View.GONE
            txtCantidadProduccion.visibility=View.GONE
            txtAdicional.visibility=View.GONE
            txt_descripcion.visibility=View.GONE



            btnEdit.setColorFilter(getContext().getResources().getColor(R.color.orange))
            btnAdd.visibility=View.GONE
            btnEdit.setColorFilter(getContext().getResources().getColor(R.color.orange))
            btnDelete.setColorFilter(getContext().getResources().getColor(R.color.red_900))





            //image.setImageBitmap(data.Imagen)
            // image.setImageResource(data.Imagen)


            txtFechasProduccion.text = String.format(contextAdapter?.getString(R.string.range_dates)!!,data.getFechaInicioFormat(), data.getFechafinFormat())
            txt_title.text = String.format(contextAdapter?.getString(R.string.cantidad_estimada)!!,data.ProduccionReal, data.NombreUnidadMedida)



///            txtCantidadProduccion.text = String.format(contextAdapter?.getString(R.string.cantidad_estimada)!!, data.ProduccionReal, data.NombreUnidadMedida)

            //El listener en base a la posición
            itemView.setOnClickListener {
                ProduccionAdapter.instance?.postEventc(RequestEventProduccion.ITEM_EVENT,data)
            }

            btnEdit.setOnClickListener {
                ProduccionAdapter.instance?.postEventc(RequestEventProduccion.ITEM_EDIT_EVENT,data)
            }

            btnDelete.setOnClickListener {
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





