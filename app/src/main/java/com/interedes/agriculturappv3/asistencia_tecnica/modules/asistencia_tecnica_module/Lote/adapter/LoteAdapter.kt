package com.interedes.agriculturappv3.asistencia_tecnica.modules.asistencia_tecnica_module.Lote.adapter

import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.TextView
import com.interedes.agriculturappv3.R
import com.interedes.agriculturappv3.asistencia_tecnica.models.Lote
import com.interedes.agriculturappv3.asistencia_tecnica.modules.asistencia_tecnica_module.Lote.events.RequestEventLote
import com.interedes.agriculturappv3.libs.EventBus
import com.interedes.agriculturappv3.libs.GreenRobotEventBus
import kotlinx.android.synthetic.main.content_list_lotes.view.*

/**
 * Created by EnuarMunoz on 9/03/18.
 */

//Recibe directamente la lista y el listener del click

class LoteAdapter(val lista: ArrayList<Lote>) : RecyclerView.Adapter<LoteAdapter.ViewHolder>() {

    companion object {
        var instance:  LoteAdapter? = null
    }
    var eventBus: EventBus? = null
    init {
        eventBus = GreenRobotEventBus()
        instance = this
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        //Llama al método del holder para cargar los items
        holder.bindItems(lista[position], position)
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
        fun bindItems(data: Lote, pos: Int) = with(itemView) {
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
                LoteAdapter.instance?.postEventc(RequestEventLote.ITEM_EVENT,data)
            }

            deleteBtn.setOnClickListener {
                LoteAdapter.instance?.postEventc(RequestEventLote.ITEM_DELETE_EVENT,data)
            }


            btnEditLote.setOnClickListener {
                LoteAdapter.instance?.postEventc(RequestEventLote.ITEM_EDIT_EVENT,data)
            }
        }
    }

    fun postEventc(type: Int, lote:Lote?) {
        var loteMitable= lote as Object
        val event = RequestEventLote(type,null, loteMitable,null)
        event.eventType = type
        eventBus?.post(event)
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