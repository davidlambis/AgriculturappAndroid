package com.interedes.agriculturappv3.modules.ofertas.adapters

import android.graphics.BitmapFactory
import android.support.v4.content.ContextCompat
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import com.interedes.agriculturappv3.R
import com.interedes.agriculturappv3.libs.EventBus
import com.interedes.agriculturappv3.libs.GreenRobotEventBus
import com.interedes.agriculturappv3.modules.models.ofertas.DetalleOferta
import com.interedes.agriculturappv3.modules.models.ofertas.DetalleOferta_Table
import com.interedes.agriculturappv3.modules.models.ofertas.Oferta
import com.interedes.agriculturappv3.modules.models.producto.Producto
import com.interedes.agriculturappv3.modules.models.producto.Producto_Table
import com.interedes.agriculturappv3.modules.ofertas.events.OfertasEvent
import com.raizlabs.android.dbflow.sql.language.SQLite
import java.util.ArrayList

class OfertasAdapter(var lista: ArrayList<Oferta>) : RecyclerView.Adapter<OfertasAdapter.ViewHolder>() {

    init {
        eventBus = GreenRobotEventBus()
    }

    companion object {
        var eventBus: EventBus? = null
        fun postEvent(type: Int, oferta: Oferta?) {
            val ofertaMutable = oferta as Object
            val event = OfertasEvent(type, null, ofertaMutable, null)
            event.eventType = type
            eventBus?.post(event)
        }
    }

    override fun onBindViewHolder(holder: OfertasAdapter.ViewHolder, position: Int) {
        //Llama al método del holder para cargar los items
        holder.bindItems(lista[position], position)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): OfertasAdapter.ViewHolder {
        val v = LayoutInflater.from(parent.context).inflate(R.layout.content_list_general, parent, false)
        return OfertasAdapter.ViewHolder(v)
    }

    override fun getItemCount(): Int {
        return lista.size
    }


    fun setItems(newItems: List<Oferta>) {
        lista.addAll(newItems)
        notifyDataSetChanged()

    }

    fun clear() {
        lista.clear()
        notifyDataSetChanged()
    }

    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        fun bindItems(data: Oferta, pos: Int) = with(itemView) {
            val contentIcon: ImageView = itemView.findViewById(R.id.contentIcon)
            val txtTitle: TextView = itemView.findViewById(R.id.txtTitle)
            val txtDescription: TextView = itemView.findViewById(R.id.txtDescription)

            val txtCantidad: TextView = itemView.findViewById(R.id.txtQuantity)


            val txtDate: TextView = itemView.findViewById(R.id.txtDate)
            val btnAction1: Button = itemView.findViewById(R.id.btnButtomAction1)
            val btnAction2: Button = itemView.findViewById(R.id.btnButtomAction2)
            val btnAction3: Button = itemView.findViewById(R.id.btnButtomAction3)



            val options: LinearLayout =itemView.findViewById(R.id.options)
            options.visibility=View.GONE

            val optionsButtons: LinearLayout =itemView.findViewById(R.id.optionsButtons)
            optionsButtons.visibility=View.VISIBLE



            if(data.Producto!=null){
                val byte = data.Producto?.blobImagen?.getBlob()
                val bitmap = BitmapFactory.decodeByteArray(byte, 0, byte!!.size)
                contentIcon.setImageBitmap(bitmap)
                contentIcon.setScaleType(ImageView.ScaleType.CENTER_CROP);
                txtTitle.setText(data.Producto?.Nombre)
               // contentIcon.setImageResource(R.drawable.ic_ofertas)
            }else{

            }


            if(data.DetalleOfertaSingle!=null){

                var disponibilidad = ""
                if ( data.DetalleOfertaSingle?.Cantidad.toString().contains(".0")) {
                    disponibilidad = String.format(context?.getString(R.string.price_empty_signe)!!,
                            data.DetalleOfertaSingle?.Cantidad)
                } else {
                    disponibilidad = data.DetalleOfertaSingle?.Cantidad.toString()
                }
                txtCantidad.text =disponibilidad+" ${data.Producto?.NombreUnidadMedidaCantidad}"
            }


            if(data.Usuario!=null){

                var disponibilidad = ""
                if ( data.DetalleOfertaSingle?.Cantidad.toString().contains(".0")) {
                    disponibilidad = String.format(context?.getString(R.string.price_empty_signe)!!,
                            data.DetalleOfertaSingle?.Cantidad)
                } else {
                    disponibilidad = data.DetalleOfertaSingle?.Cantidad.toString()
                }


                var producto= String.format("%s %s ", disponibilidad, data.Producto?.NombreUnidadMedidaCantidad)
                var calidad= String.format("%s",data.Producto?.NombreCalidad)

                var precioOferta=String.format(context.getString(R.string.price_producto),
                        data.DetalleOfertaSingle?.Valor_Oferta, data.DetalleOfertaSingle?.NombreUnidadMedidaPrecio)

                txtDescription.text= "${data.Usuario?.Nombre} ${data.Usuario?.Apellidos}  te oferto $precioOferta por el cultivo de ${data.Producto?.Nombre} de $producto de $calidad"
                txtDate.text =data.getCreatedOnFormat()
            }





            btnAction1.setOnClickListener {
                postEvent(OfertasEvent.REQUEST_REFUSED_ITEM_EVENT, data)
            }

            btnAction2.setOnClickListener {
                postEvent(OfertasEvent.REQUEST_CONFIRM_ITEM_EVENT, data)
            }

            btnAction3.setOnClickListener {
                postEvent(OfertasEvent.REQUEST_CHAT_ITEM_EVENT, data)
            }

        }
    }
}