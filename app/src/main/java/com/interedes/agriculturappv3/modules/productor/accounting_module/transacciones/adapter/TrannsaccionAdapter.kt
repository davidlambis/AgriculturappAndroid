package com.interedes.agriculturappv3.modules.productor.accounting_module.transacciones.adapter

import android.graphics.Typeface
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.TextView
import com.interedes.agriculturappv3.R
import com.interedes.agriculturappv3.modules.models.ventas.Transaccion
import com.interedes.agriculturappv3.modules.productor.accounting_module.transacciones.events.RequestEventTransaccion
import com.interedes.agriculturappv3.libs.EventBus
import com.interedes.agriculturappv3.libs.GreenRobotEventBus
import kotlinx.android.synthetic.main.content_list_general.view.*

class TrannsaccionAdapter(var lista: ArrayList<Transaccion>)    : RecyclerView.Adapter<TrannsaccionAdapter.ViewHolder>() {

    companion object {
        var eventBus: EventBus? = null
        fun postEventc(type: Int, transaccion: Transaccion?) {
            var transaccionnMutable= transaccion as Object
            val event = RequestEventTransaccion(type, null, transaccionnMutable, null)
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


    fun setItems(newItems: List<Transaccion>) {
        lista.addAll(newItems)
        notifyDataSetChanged()

    }

    fun clear() {
        lista.clear()
        notifyDataSetChanged()
    }


    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        fun bindItems(data: Transaccion, pos: Int) = with(itemView) {



            var txt_title: TextView = itemView.findViewById(R.id.txtTitle)
            var txt_descripcion: TextView = itemView.findViewById(R.id.txtDescription)
            var txtFechas: TextView = itemView.findViewById(R.id.txtDate)
            var txtCantidad: TextView = itemView.findViewById(R.id.txtQuantity)
            var txtAdicional: TextView = itemView.findViewById(R.id.txtDescripcionAdditional)
            var txtStatisSincronized: TextView = itemView.findViewById(R.id.txtStatisSincronized)
            var icon: ImageView = itemView.findViewById(R.id.contentIcon)

            var btnDelete: ImageButton = itemView.findViewById(R.id.btnAction3)
            var btnAdd: ImageButton = itemView.findViewById(R.id.btnAction1)
            var btnEdit: ImageButton = itemView.findViewById(R.id.btnAction2)



            icon.setImageResource(R.drawable.ic_contabilidad)



            btnEdit.setColorFilter(getContext().getResources().getColor(R.color.orange))
            btnAdd.visibility=View.GONE
            btnEdit.setColorFilter(getContext().getResources().getColor(R.color.orange))
            btnDelete.setColorFilter(getContext().getResources().getColor(R.color.red_900))


            txtStatisSincronized.visibility=View.VISIBLE
            if (data.Estado_Sincronizacion == true) {
                txtStatisSincronized.setTextColor(resources.getColor(R.color.green))
                txtStatisSincronized.text = context.getString(R.string.Sincronizado)
            } else {
                txtStatisSincronized.setTextColor(resources.getColor(R.color.red_900))
                txtStatisSincronized.text = context.getString(R.string.noSincronizado)
            }

            //image.setImageBitmap(data.Imagen)
            // image.setImageResource(data.Imagen)


            txtTitle.text=data.Descripcion_Puk+"( ${data.Nombre_Detalle_Producto_Cultivo} )"
            txtDescription.text=data.Descripcion_Puk+": ${data.Concepto}"
            txtFechas.text=data.getFechaTransacccion()
            txtCantidad.text= String.format(context?.getString(R.string.price)!!,data.Valor_Total)
            txtAdicional.text= data.Nombre_Cultivo
            txtAdicional.text= data.Nombre_Tercero
            txt_descripcion.text=data.Nombre_Estado_Transaccion
            txt_descripcion.setTextColor(context.resources.getColor(R.color.black))
            txt_descripcion.setTypeface(txt_descripcion.getTypeface(),Typeface.BOLD);

///            txtCantidadProduccion.text = String.format(contextAdapter?.getString(R.string.cantidad_estimada)!!, data.ProduccionReal, data.NombreUnidadMedida)

            //El listener en base a la posición
            itemView.setOnClickListener {
               postEventc(RequestEventTransaccion.ITEM_EVENT,data)
            }

            btnEdit.setOnClickListener {
               postEventc(RequestEventTransaccion.ITEM_EDIT_EVENT,data)
            }

            btnDelete.setOnClickListener {
               postEventc(RequestEventTransaccion.ITEM_DELETE_EVENT,data)
            }
        }
    }


}


