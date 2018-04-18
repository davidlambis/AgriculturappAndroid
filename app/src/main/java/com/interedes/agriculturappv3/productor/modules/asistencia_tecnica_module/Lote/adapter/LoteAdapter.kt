package com.interedes.agriculturappv3.productor.modules.asistencia_tecnica_module.Lote.adapter

import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.TextView
import com.interedes.agriculturappv3.R
import com.interedes.agriculturappv3.productor.modules.asistencia_tecnica_module.Lote.events.RequestEventLote
import com.interedes.agriculturappv3.libs.EventBus
import com.interedes.agriculturappv3.libs.GreenRobotEventBus
import com.interedes.agriculturappv3.productor.models.lote.Lote

/**
 * Created by EnuarMunoz on 9/03/18.
 */

//Recibe directamente la lista y el listener del click

class LoteAdapter(val lista: ArrayList<Lote>) : RecyclerView.Adapter<LoteAdapter.ViewHolder>() {

    companion object {
        var eventBus: EventBus? = null
        fun postEventc(type: Int, lote: Lote?) {
            var loteMitable= lote as Object
            val event = RequestEventLote(type,null, loteMitable,null)
            event.eventType = type
            eventBus?.post(event)
        }
    }

    init {
        eventBus = GreenRobotEventBus()
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


    fun setItems(newItems: List<Lote>) {
        lista.addAll(newItems)
        notifyDataSetChanged()

    }

    fun clear() {
        lista.clear()
        notifyDataSetChanged()
    }


    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        fun bindItems(data: Lote, pos: Int) = with(itemView) {

            var txt_name_lote: TextView = itemView.findViewById(R.id.txtTitle)
            var txt_description_lote: TextView = itemView.findViewById(R.id.txtDescription)
            var txtFechaLote: TextView = itemView.findViewById(R.id.txtDate)
            var txtAreaLote: TextView = itemView.findViewById(R.id.txtQuantity)
            var txtAdicional: TextView = itemView.findViewById(R.id.txtDescripcionAdditional)
            var icon: ImageView = itemView.findViewById(R.id.contentIcon)

            var btnDelete: ImageButton = itemView.findViewById(R.id.btnAction3)
            var btnAdd: ImageButton = itemView.findViewById(R.id.btnAction1)
            var btnEdit: ImageButton = itemView.findViewById(R.id.btnAction2)



            icon.setImageResource(R.drawable.ic_lote)
            txtAdicional.visibility=View.GONE

            btnEdit.setColorFilter(getContext().getResources().getColor(R.color.orange))
            btnAdd.visibility=View.GONE
            btnEdit.setColorFilter(getContext().getResources().getColor(R.color.orange))
            btnDelete.setColorFilter(getContext().getResources().getColor(R.color.red_900))


            txt_name_lote.text = data.Nombre
            txt_description_lote.text = data.Descripcion
            txtAreaLote.text =String.format(context.getString(R.string.cantidad_estimada)!!,data.Area, data.Nombre_Unidad_Medida)
            txtFechaLote.text = ""
           // txtCoordenada.text = data.Coordenadas

            //El listener en base a la posición
            itemView.setOnClickListener {
                postEventc(RequestEventLote.ITEM_EVENT,data)
            }

            btnDelete.setOnClickListener {
               postEventc(RequestEventLote.ITEM_DELETE_EVENT,data)
            }

            btnEdit.setOnClickListener {
               postEventc(RequestEventLote.ITEM_EDIT_EVENT,data)
            }
        }
    }


}


/*
class LoteAdapter(val lista: ArrayList<Lote>, val listener: (ListenerAdapterOnClickEventLote) -> Unit) : RecyclerView.Adapter<LoteAdapter.ViewHolder>() {


    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        //Llama al método del holder para cargar los items
        holder.bindItems(lista[position], position,listener)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val v = LayoutInflater.from(parent.context).inflate(R.layout.content_list_lotes, parent, false)
        return ViewHolder(v)
    }

    override fun getItemCount(): Int {
        return lista.size
    }


    fun setItems(newItems: List<Lote>) {
        lista.addAll(newItems)
        notifyDataSetChanged()

    }

    fun clear() {
        lista.clear()
        notifyDataSetChanged()
    }


    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        fun bindItems(data: Lote, pos: Int, listenerAdapetr: (ListenerAdapterOnClickEventLote) -> Unit) = with(itemView) {



            var txt_name_lote: TextView = itemView.findViewById(R.id.txt_name_lote)
            var txt_description_lote: TextView = itemView.findViewById(R.id.txt_description_lote)
            var txtAreaLote: TextView = itemView.findViewById(R.id.txtAreaLote)
            var txtFechaLote: TextView = itemView.findViewById(R.id.txtFechaLote)
            var txtCoordenada: TextView = itemView.findViewById(R.id.txtCoordenada)


            var deleteBtn: ImageButton = itemView.findViewById(R.id.btnDeleteLote)
            //image.setImageBitmap(data.Imagen)
           // image.setImageResource(data.Imagen)
            txt_name_lote.text = data.Nombre
            txt_description_lote.text = data.Descripcion
            txtAreaLote.text = data.Area.toString()
            txtFechaLote.text = ""
            txtCoordenada.text = data.Coordenadas

            //El listener en base a la posición
            itemView.setOnClickListener {
                var search= ListenerAdapterOnClickEventLote(pos,0);
                listenerAdapetr(search)
            }


            deleteBtn.setOnClickListener {
                var delete= ListenerAdapterOnClickEventLote(pos,2);
                listenerAdapetr(delete)
            }

        }

    }

}*/