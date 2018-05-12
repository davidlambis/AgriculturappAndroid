package com.interedes.agriculturappv3.productor.modules.asistencia_tecnica_module.control_plagas.adapters

import android.support.v4.content.ContextCompat
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import com.interedes.agriculturappv3.R
import com.interedes.agriculturappv3.productor.models.control_plaga.ControlPlaga
import com.interedes.agriculturappv3.productor.modules.asistencia_tecnica_module.control_plagas.events.ControlPlagasEvent
import com.interedes.agriculturappv3.libs.EventBus
import com.interedes.agriculturappv3.libs.GreenRobotEventBus
import java.text.SimpleDateFormat
import java.util.*

class ControlPlagasAdapter(var lista: ArrayList<ControlPlaga>) : RecyclerView.Adapter<ControlPlagasAdapter.ViewHolder>() {


    init {
        eventBus = GreenRobotEventBus()
    }

    companion object {
        var eventBus: EventBus? = null
        fun postEvent(type: Int, controlPlaga: ControlPlaga?) {
            val controlPlagaMutable = controlPlaga as Object
            val event = ControlPlagasEvent(type, null, controlPlagaMutable, null)
            event.eventType = type
            eventBus?.post(event)
        }
    }


    override fun onBindViewHolder(holder: ControlPlagasAdapter.ViewHolder, position: Int) {
        //Llama al método del holder para cargar los items
        holder.bindItems(lista[position], position)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ControlPlagasAdapter.ViewHolder {
        val v = LayoutInflater.from(parent.context).inflate(R.layout.content_list_control_plagas, parent, false)
        return ControlPlagasAdapter.ViewHolder(v)
    }

    override fun getItemCount(): Int {
        return lista.size
    }


    fun setItems(newItems: List<ControlPlaga>) {
        lista.addAll(newItems)
        notifyDataSetChanged()

    }

    fun clear() {
        lista.clear()
        notifyDataSetChanged()
    }

    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        fun bindItems(data: ControlPlaga, pos: Int) = with(itemView) {
            val txtNombrePlaga: TextView = itemView.findViewById(R.id.txtNombrePlaga)
            val txtFechaAplicacion: TextView = itemView.findViewById(R.id.txtFechaAplicacion)
            val txtInsumo: TextView = itemView.findViewById(R.id.txtInsumo)
            val txtDosis: TextView = itemView.findViewById(R.id.txtDosis)
            val txtEstadoErradicacion: TextView = itemView.findViewById(R.id.txtEstadoErradicacion)
            val btnDeleteControlPlaga: ImageButton = itemView.findViewById(R.id.btnDeleteControlPlaga)
            val cardControlPlaga: LinearLayout = itemView.findViewById(R.id.cardControlPlaga)
            val btnErradicar: Button = itemView.findViewById(R.id.btnErradicar)
            val titleFechaErradicacion: TextView = itemView.findViewById(R.id.titleFechaErradicacion)
            val txtFechaErradicacion: TextView = itemView.findViewById(R.id.txtFechaErradicacion)
            val ivControlPlaga: ImageView = itemView.findViewById(R.id.ivControlPlaga)

            val txtSincronizacion: TextView = itemView.findViewById(R.id.txtSincronizacion)

            if (data.Estado_Sincronizacion == true) {
                txtSincronizacion.setTextColor(resources.getColor(R.color.green))
                txtSincronizacion.text = context.getString(R.string.Sincronizado)
            } else {
                txtSincronizacion.setTextColor(resources.getColor(R.color.red_900))
                txtSincronizacion.text = context.getString(R.string.noSincronizado)
            }

            if (data.EstadoErradicacion == false) {
                cardControlPlaga.background = ContextCompat.getDrawable(context, R.drawable.custom_drawable_card_view_red)
                txtEstadoErradicacion.visibility = View.VISIBLE
                txtEstadoErradicacion.text = resources.getString(R.string.title_vigente)
                btnErradicar.visibility = View.VISIBLE
                ivControlPlaga.visibility = View.VISIBLE
                ivControlPlaga.setImageResource(R.drawable.ic_plagas)
            } else {
                cardControlPlaga.background = ContextCompat.getDrawable(context, R.drawable.custom_drawable_card_view_green)
                txtEstadoErradicacion.visibility = View.VISIBLE
                txtEstadoErradicacion.text = resources.getString(R.string.title_erradicada)
                btnErradicar.visibility = View.GONE
                titleFechaErradicacion.visibility = View.VISIBLE
                txtFechaErradicacion.visibility = View.VISIBLE
                val format1 = SimpleDateFormat("dd/MM/yyyy")
                var formatted: String? = null
                if (data.Fecha_Erradicacion != null) {
                    formatted = format1.format(data.Fecha_Erradicacion)
                }
                txtFechaErradicacion.text = formatted
                ivControlPlaga.visibility = View.VISIBLE
                ivControlPlaga.setImageResource(R.drawable.ic_plagas_erradicada)
            }
            txtNombrePlaga.text = data.NombrePlaga
            txtFechaAplicacion.text = data.getFechaAplicacionFormat()
            txtDosis.text = data.Dosis.toString()
            //txtInsumo.text = data.No

            btnDeleteControlPlaga.setOnClickListener {
                postEvent(ControlPlagasEvent.ITEM_DELETE_EVENT, data)
            }

            btnErradicar.setOnClickListener {
                postEvent(ControlPlagasEvent.ITEM_ERRADICAR_EVENT, data)
            }
        }
    }
}