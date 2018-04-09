package com.interedes.agriculturappv3.productor.modules.asistencia_tecnica_module.plagas.adapters


import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import com.interedes.agriculturappv3.R
import com.interedes.agriculturappv3.productor.models.plagas.TipoEnfermedad
import com.interedes.agriculturappv3.productor.modules.asistencia_tecnica_module.plagas.events.PlagasEvent
import com.interedes.agriculturappv3.libs.EventBus
import com.interedes.agriculturappv3.libs.GreenRobotEventBus
import com.robertlevonyan.views.expandable.Expandable
import com.robertlevonyan.views.expandable.ExpandingListener


class PlagasAdapter(val lista: ArrayList<TipoEnfermedad>) : RecyclerView.Adapter<PlagasAdapter.ViewHolder>() {

    companion object {
        var eventBus: EventBus? = null
        fun postEvent(type: Int, plaga: TipoEnfermedad?) {
            val plagaMutable = plaga as Object
            val event = PlagasEvent(type, null, plagaMutable, null)
            event.eventType = type
            eventBus?.post(event)
        }

    }



    init {
        eventBus = GreenRobotEventBus()
    }

    override fun onBindViewHolder(holder: PlagasAdapter.ViewHolder, position: Int) {
        //Llama al método del holder para cargar los items
        holder.bindItems(lista[position], position)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PlagasAdapter.ViewHolder {
        val v = LayoutInflater.from(parent.context).inflate(R.layout.content_list_plagas, parent, false)
        return PlagasAdapter.ViewHolder(v)
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
            val txt_nombre_enfermedad: TextView = itemView.findViewById(R.id.txtNombreEnfermedad)
            val txt_nombre_cientifico: TextView = itemView.findViewById(R.id.txtNombreCientifico)
            val image_enfermedad: ImageView = itemView.findViewById(R.id.imageViewEnfermedad)
            val txt_descripcion_enfermedad: TextView = itemView.findViewById(R.id.txtDescripcionEnfermedad)
            val btn_ver_insumos: Button = itemView.findViewById(R.id.btnVerInsumos)
            val expandable : Expandable = itemView.findViewById(R.id.expandable)

            txt_nombre_enfermedad.text = data.Nombre
            txt_nombre_cientifico.text = data.NombreCientifico
            image_enfermedad.setImageResource(data.Imagen!!)
            txt_descripcion_enfermedad.text = data.Descripcion

            btn_ver_insumos.setOnClickListener {
                postEvent(PlagasEvent.ITEM_OPEN_EVENT, data)
            }


            expandable.setExpandingListener(object : ExpandingListener {
                override fun onExpanded() {
                   txt_descripcion_enfermedad.visibility = View.VISIBLE
                }

                override fun onCollapsed() {
                    txt_descripcion_enfermedad.visibility = View.GONE
                }
            })

        }
    }



}