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
        val v = LayoutInflater.from(parent.context).inflate(R.layout.content_list_general, parent, false)
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


            var txt_nombre_cultivo: TextView = itemView.findViewById(R.id.txtTitle)
            var txt_descripcion_cultivo: TextView = itemView.findViewById(R.id.txtDescription)
            var txt_fechas_cultivo: TextView = itemView.findViewById(R.id.txtDate)
            var txt_cosecha_estimada: TextView = itemView.findViewById(R.id.txtQuantity)
            var txtAdicional: TextView = itemView.findViewById(R.id.txtDescripcionAdditional)
            var icon: ImageView = itemView.findViewById(R.id.contentIcon)

            var btnDelete: ImageButton = itemView.findViewById(R.id.btnAction3)
            var btnAdd: ImageButton = itemView.findViewById(R.id.btnAction1)
            var btnEdit: ImageButton = itemView.findViewById(R.id.btnAction2)



            icon.setImageResource(R.drawable.ic_cultivos)
            txtAdicional.visibility=View.GONE

            btnEdit.setColorFilter(getContext().getResources().getColor(R.color.orange))
            btnAdd.visibility=View.GONE
            btnEdit.setColorFilter(getContext().getResources().getColor(R.color.orange))
            btnDelete.setColorFilter(getContext().getResources().getColor(R.color.red_900))




            //val txt_nombre_cultivo: TextView = itemView.findViewById(R.id.txtNombreCultivo)

            //txt_nombre_cultivo.text = data.Nombre
            txt_nombre_cultivo.text = data.Nombre_Detalle_Tipo_Producto
            txt_descripcion_cultivo.text = data.Descripcion
            txt_cosecha_estimada.text = data.EstimadoCosecha.toString()
            txt_fechas_cultivo.setText(String.format(context.getString(R.string.range_dates)!!, data.FechaIncio, data.FechaFin))

            itemView.setOnClickListener {
                CultivoAdapter.instance?.postEvent(CultivoEvent.ITEM_EVENT, data)
            }

            btnEdit.setOnClickListener {
                CultivoAdapter.instance?.postEvent(CultivoEvent.ITEM_EDIT_EVENT, data)
            }

            btnDelete.setOnClickListener {
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