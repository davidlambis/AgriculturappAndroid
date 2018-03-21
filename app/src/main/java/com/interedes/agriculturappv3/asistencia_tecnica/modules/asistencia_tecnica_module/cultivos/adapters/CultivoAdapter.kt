package com.interedes.agriculturappv3.asistencia_tecnica.modules.asistencia_tecnica_module.cultivos.adapters

import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.TextView
import com.interedes.agriculturappv3.R
import com.interedes.agriculturappv3.asistencia_tecnica.models.Cultivo
import com.interedes.agriculturappv3.asistencia_tecnica.modules.asistencia_tecnica_module.Lote.events.RequestEventLote
import com.interedes.agriculturappv3.asistencia_tecnica.modules.asistencia_tecnica_module.cultivos.events.CultivoEvent
import com.interedes.agriculturappv3.libs.EventBus
import com.interedes.agriculturappv3.libs.GreenRobotEventBus
import kotlinx.android.synthetic.main.content_list_cultivo.view.*


class CultivoAdapter(val lista: ArrayList<Cultivo>) : RecyclerView.Adapter<CultivoAdapter.ViewHolder>() {

    companion object {
        var instance: CultivoAdapter? = null
    }

    var eventBus: EventBus? = null

    init {
        eventBus = GreenRobotEventBus()
        CultivoAdapter.instance = this
    }

    override fun onBindViewHolder(holder: CultivoAdapter.ViewHolder, position: Int) {
        //Llama al método del holder para cargar los items
        holder.bindItems(lista[position], position)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CultivoAdapter.ViewHolder {
        val v = LayoutInflater.from(parent.context).inflate(R.layout.content_list_cultivo, parent, false)
        return CultivoAdapter.ViewHolder(v)
    }

    override fun getItemCount(): Int {
        return lista.size
    }

    fun setItems(newItems: List<Cultivo>) {
        lista.addAll(newItems)
        notifyDataSetChanged()

    }

    fun clear() {
        lista.clear()
        notifyDataSetChanged()
    }

    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        fun bindItems(data: Cultivo, pos: Int) = with(itemView) {
            val txt_nombre_cultivo: TextView = itemView.findViewById(R.id.txtNombreCultivo)
            val txt_descripcion_cultivo: TextView = itemView.findViewById(R.id.txtDescripcionCultivo)
            val txt_cosecha_estimada: TextView = itemView.findViewById(R.id.txtCosechaEstimada)
            val txt_fecha_inicio: TextView = itemView.findViewById(R.id.txtFechaInicio)
            val txt_fecha_fin: TextView = itemView.findViewById(R.id.txtFechaFin)
            val btn_edit_cultivo: ImageView = itemView.findViewById(R.id.btn_edit_cultivo)
            val btn_delete_cultivo: ImageButton = itemView.findViewById(R.id.btn_delete_cultivo)

            txt_nombre_cultivo.text = data.Nombre
            txt_descripcion_cultivo.text = data.Descripcion
            txt_cosecha_estimada.text = data.EstimadoCosecha.toString()
            txt_fecha_inicio.text = data.FechaIncio
            txt_fecha_fin.text = data.FechaFin

            itemView.setOnClickListener {
                CultivoAdapter.instance?.postEvent(CultivoEvent.ITEM_EVENT, data)
            }

            btn_edit_cultivo.setOnClickListener {
                CultivoAdapter.instance?.postEvent(CultivoEvent.ITEM_EDIT_EVENT, data)
            }

            btn_delete_cultivo.setOnClickListener {
                CultivoAdapter.instance?.postEvent(CultivoEvent.ITEM_DELETE_EVENT, data)
            }
        }
    }

    fun postEvent(type: Int, cultivo: Cultivo?) {
        val cultivoMutable = cultivo as Object
        val event = CultivoEvent(type, null, cultivoMutable, null)
        event.eventType = type
        eventBus?.post(event)
    }

}