package com.interedes.agriculturappv3.modules.productor.asistencia_tecnica_module.up.adapter

import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.TextView
import com.interedes.agriculturappv3.R
import com.interedes.agriculturappv3.modules.models.unidad_productiva.Unidad_Productiva
import com.interedes.agriculturappv3.modules.productor.asistencia_tecnica_module.UnidadProductiva.events.RequestEventUP
import com.interedes.agriculturappv3.libs.EventBus
import com.interedes.agriculturappv3.libs.GreenRobotEventBus

/**
 * Created by usuario on 16/03/2018.
 */
class UnidadProductivaAdapter(val lista: ArrayList<Unidad_Productiva>) : RecyclerView.Adapter<UnidadProductivaAdapter.ViewHolder>() {

    companion object {
        var eventBus: EventBus? = null
        fun postEventc(type: Int, unidadProductiva: Unidad_Productiva?) {
            var unidadMitable = unidadProductiva as Object
            val event = RequestEventUP(type, null, unidadMitable, null)
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


    fun setItems(newItems: List<Unidad_Productiva>) {
        lista.addAll(newItems)
        notifyDataSetChanged()

    }

    fun clear() {
        lista.clear()
        notifyDataSetChanged()
    }


    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        fun bindItems(data: Unidad_Productiva, pos: Int) = with(itemView) {


            var txt_unidad_productiva: TextView = itemView.findViewById(R.id.txtTitle)
            var txt_descripcion_unidadproductiva: TextView = itemView.findViewById(R.id.txtDescription)
            var txt_fecha_unidad_productiva: TextView = itemView.findViewById(R.id.txtDate)
            var txt_area_unidad_productiva: TextView = itemView.findViewById(R.id.txtQuantity)
            var txtAdicional: TextView = itemView.findViewById(R.id.txtDescripcionAdditional)
            var iconUp: ImageView = itemView.findViewById(R.id.contentIcon)
            var btnAddPoligonUp: ImageButton = itemView.findViewById(R.id.btnAction3)
            var btnAddLocationUp: ImageButton = itemView.findViewById(R.id.btnAction1)
            var btnEditUp: ImageButton = itemView.findViewById(R.id.btnAction2)



            iconUp.setImageResource(R.drawable.ic_unidad_productiva)
            txtAdicional.visibility = View.GONE
            btnEditUp.setColorFilter(getContext().getResources().getColor(R.color.orange))


            txt_unidad_productiva.text = data.nombre
            txt_descripcion_unidadproductiva.text = data.descripcion
            txt_area_unidad_productiva.text = data.Area.toString()
            if (data.Estado_Sincronizacion == true) {
                txt_fecha_unidad_productiva.text = context.getString(R.string.Sincronizado)
            } else {
                txt_fecha_unidad_productiva.text = context.getString(R.string.noSincronizado)
            }
            //txtCiudaDepartamento.text =data.Nombre_Ciudad+"-"+data.Nombre_Departamento

            //var badge_notification_location_up: TextView = itemView.findViewById(R.id.badge_notification_location_up)
            //var badge_notification_poligon_up: TextView = itemView.findViewById(R.id.badge_notification_poligon_up)

            //var btn_edit_up: ImageView = itemView.findViewById(R.id.btn_edit_up)
            //var btn_delete_up: ImageView = itemView.findViewById(R.id.btn_delete_up)


            if (data.Configuration_Point == true) {
                //indicator_is_configured_location.setImageResource(R.drawable.ic_done_white)
                // indicator_is_configured_location.setBackgroundResource(R.color.light_green_800)
                // badge_notification_location_up.visibility=View.GONE
                btnAddLocationUp.setImageResource(R.drawable.ic_geolocalizacion_btn)
            } else if (data.Configuration_Point == false) {
                // indicator_is_configured_location.setImageResource(R.drawable.ic_close_white)
                // indicator_is_configured_location.setBackgroundResource(R.color.red_900)
                // badge_notification_location_up.visibility=View.VISIBLE
                /*
                var ic_geolocalizacion_btn = context.resources.getDrawable(R.drawable.ic_geolocalizacion_btn)
                var icon = ic_geolocalizacion_btn?.mutate()
                icon?.setColorFilter(resources.getColor(R.color.gray_light), PorterDuff.Mode.SRC_OVER);
                btnAddLocationUp.setImageDrawable(icon)*/
                btnAddLocationUp.setImageResource(R.drawable.ic_geolocalizaciongrey)

            }

            if (data.Configuration_Poligon == true) {
                // indicator_is_configured_poligon.setImageResource(R.drawable.ic_done_white)
                // indicator_is_configured_poligon.setBackgroundResource(R.color.light_green_800)
                //  badge_notification_poligon_up.visibility=View.GONE
                btnAddPoligonUp.setImageResource(R.drawable.ic_geolocalizacion_poligon_btn)
            } else if (data.Configuration_Poligon == false) {
                //indicator_is_configured_poligon.setImageResource(R.drawable.ic_close_white)
                //indicator_is_configured_poligon.setBackgroundResource(R.color.red_900)
                // badge_notification_poligon_up.visibility=View.VISIBLE
                btnAddPoligonUp.setImageResource(R.drawable.ic_poligono_grey)
            }


            //El listener en base a la posición
            itemView.setOnClickListener {
                postEventc(RequestEventUP.ITEM_EVENT, data)
            }

            btnEditUp.setOnClickListener {
                postEventc(RequestEventUP.ITEM_EDIT_EVENT, data)
            }

            btnAddPoligonUp.setOnClickListener {
                postEventc(RequestEventUP.ADD_POLIGON_EVENT, data)
            }
        }
    }

}