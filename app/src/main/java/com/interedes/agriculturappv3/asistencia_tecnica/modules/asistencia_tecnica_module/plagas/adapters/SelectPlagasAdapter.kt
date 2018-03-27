package com.interedes.agriculturappv3.asistencia_tecnica.modules.asistencia_tecnica_module.plagas.adapters

import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import com.interedes.agriculturappv3.R
import com.interedes.agriculturappv3.asistencia_tecnica.models.plagas.TipoEnfermedad
import com.interedes.agriculturappv3.asistencia_tecnica.modules.asistencia_tecnica_module.plagas.events.PlagasEvent
import com.interedes.agriculturappv3.libs.EventBus
import com.interedes.agriculturappv3.libs.GreenRobotEventBus

/**
 * Created by MAKERS on 26/03/2018.
 */
class SelectPlagasAdapter(val lista: ArrayList<TipoEnfermedad>) : RecyclerView.Adapter<SelectPlagasAdapter.ViewHolder>() {

    companion object {
        var instance: SelectPlagasAdapter? = null
    }

    var eventBus: EventBus? = null

    init {
        eventBus = GreenRobotEventBus()
        SelectPlagasAdapter.instance = this
    }

    override fun onBindViewHolder(holder: SelectPlagasAdapter.ViewHolder, position: Int) {
        //Llama al método del holder para cargar los items
        holder.bindItems(lista[position], position)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SelectPlagasAdapter.ViewHolder {
        val v = LayoutInflater.from(parent.context).inflate(R.layout.content_list_select_plaga, parent, false)
        return SelectPlagasAdapter.ViewHolder(v)
    }

    override fun getItemCount(): Int {
        return lista.size
    }

    fun setItems(newItems: List<TipoEnfermedad>) {
        lista.addAll(newItems)
        notifyDataSetChanged()

    }

    fun clear() {
        lista.clear()
        notifyDataSetChanged()
    }

    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        fun bindItems(data: TipoEnfermedad, pos: Int) = with(itemView) {
            val txtNombrePlaga: TextView = itemView.findViewById(R.id.txtNombrePlaga)
            val imgPlaga: ImageView = itemView.findViewById(R.id.imgPlaga)


            txtNombrePlaga.text = data.Nombre
            if (data.Nombre.equals("Roña")) {
                imgPlaga.setImageResource(R.drawable.aguacate)
            } else if (data.Nombre.equals("Mancha Angular")) {
                imgPlaga.setImageResource(R.drawable.frijol)
            } else if (data.Nombre.equals("Moko o Madurabiche")) {
                imgPlaga.setImageResource(R.drawable.platano)
            }

            itemView.setOnClickListener {
                SelectPlagasAdapter.instance?.postEvent(PlagasEvent.ITEM_SELECT_PLAGA_EVENT, data)
            }
        }
    }

    fun postEvent(type: Int, tipoEnfermedad: TipoEnfermedad?) {
        val tipoEnfermedad = tipoEnfermedad as Object
        val event = PlagasEvent(type, null, tipoEnfermedad, null)
        event.eventType = type
        eventBus?.post(event)
    }
}