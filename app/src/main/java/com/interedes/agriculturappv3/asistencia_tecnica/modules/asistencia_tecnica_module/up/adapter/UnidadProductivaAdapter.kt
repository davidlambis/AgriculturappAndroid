package com.interedes.agriculturappv3.asistencia_tecnica.modules.asistencia_tecnica_module.up.adapter

import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.RelativeLayout
import android.widget.TextView
import com.interedes.agriculturappv3.R
import com.interedes.agriculturappv3.asistencia_tecnica.models.UnidadProductiva
import com.interedes.agriculturappv3.events.ListenerAdapterOnClickEvent
import com.interedes.agriculturappv3.libs.EventBus
import com.interedes.agriculturappv3.libs.GreenRobotEventBus
import kotlinx.android.synthetic.main.content_list_lotes.view.*

/**
 * Created by usuario on 16/03/2018.
 */
class UnidadProductivaAdapter(val lista: ArrayList<UnidadProductiva>): RecyclerView.Adapter<UnidadProductivaAdapter.ViewHolder>() {

    companion object {
        var instance:  UnidadProductivaAdapter? = null
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
        val v = LayoutInflater.from(parent.context).inflate(R.layout.content_list_unidad_productiva, parent, false)
        return ViewHolder(v)
    }

    override fun getItemCount(): Int {
        return lista.size
    }


    fun setItems(newItems: List<UnidadProductiva>) {
        lista.addAll(newItems)
        notifyDataSetChanged()

    }

    fun clear() {
        lista.clear()
        notifyDataSetChanged()
    }


    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        fun bindItems(data: UnidadProductiva, pos: Int) = with(itemView) {
            var txt_unidad_productiva: TextView = itemView.findViewById(R.id.txtNombreUnidadProductiva)
            var txt_descripcion_unidadproductiva: TextView = itemView.findViewById(R.id.txtDescripcinUnidadProductiva)
            var txt_area_unidad_productiva: TextView = itemView.findViewById(R.id.txtAreaUnidadProductiva)
            var txtCiudaDepartamento: TextView = itemView.findViewById(R.id.txtCiudadDepartamentUp)


            var btnAddPoligonUp: RelativeLayout = itemView.findViewById(R.id.btnAddPoligonUp)
            var btnAddLocationUp: RelativeLayout = itemView.findViewById(R.id.btnAddPoligonUp)

            var badge_notification_location_up: TextView = itemView.findViewById(R.id.badge_notification_location_up)
            var badge_notification_poligon_up: TextView = itemView.findViewById(R.id.badge_notification_poligon_up)

            //image.setImageBitmap(data.Imagen)
            // image.setImageResource(data.Imagen)
            txt_unidad_productiva.text = data.Nombre
            txt_descripcion_unidadproductiva.text = data.Descripcion
            txt_area_unidad_productiva.text = data.UpArea.toString()
            txtCiudaDepartamento.text =data.Nombre_Ciudad+"-"+data.Nombre_Departamento


            //El listener en base a la posición
            itemView.setOnClickListener {
                UnidadProductivaAdapter.instance?.postEventc(ListenerAdapterOnClickEvent.ITEM_EVENT,data)
            }

            btnAddPoligonUp.setOnClickListener {
                UnidadProductivaAdapter.instance?.postEventc(ListenerAdapterOnClickEvent.ADD_POLIGON_EVENT,data)
            }


            btnAddLocationUp.setOnClickListener {
                UnidadProductivaAdapter.instance?.postEventc(ListenerAdapterOnClickEvent.ADD_LOCATION_EVENT,data)
            }
        }
    }

    fun postEventc(type: Int, unidadProductiva: UnidadProductiva?) {
        var unidadMitable= unidadProductiva as Object
        val event = ListenerAdapterOnClickEvent(type, unidadMitable)
        event.eventType = type
        eventBus?.post(event)
    }
}