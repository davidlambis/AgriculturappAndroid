package com.interedes.agriculturappv3.modules.comprador.productos.adapter

import android.graphics.BitmapFactory
import android.support.v7.widget.RecyclerView
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import com.interedes.agriculturappv3.R
import com.interedes.agriculturappv3.libs.EventBus
import com.interedes.agriculturappv3.libs.GreenRobotEventBus
import com.interedes.agriculturappv3.modules.comprador.productos.events.RequestEventProductosComprador
import com.interedes.agriculturappv3.modules.models.tipoproducto.TipoProducto


class ProductosCompradorAdapter(val lista: ArrayList<TipoProducto>) : RecyclerView.Adapter<ProductosCompradorAdapter.ViewHolder>() {

    companion object {
        var eventBus: EventBus? = null
        fun postEvento(type: Int, tipoProducto: TipoProducto) {
            var produccionMutable= tipoProducto as Object
            val event = RequestEventProductosComprador(type,null, produccionMutable,null)
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
        val v = LayoutInflater.from(parent.context).inflate(R.layout.conten_list_tipo_producto, parent, false)
        return ViewHolder(v)
    }

    override fun getItemCount(): Int {
        return lista.size
    }

    fun setItems(newItems: List<TipoProducto>) {
        lista.addAll(newItems)
        notifyDataSetChanged()

    }

    fun clear() {
        lista.clear()
        notifyDataSetChanged()
    }

    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        fun bindItems(data: TipoProducto, pos: Int) = with(itemView) {
            val txtNombreTipoProducto: TextView = itemView.findViewById(R.id.txtNombreDetalleTipoProducto)
            val imgTipoProducto: ImageView = itemView.findViewById(R.id.imgTipoProducto)

            /*
            if (data.Nombre.equals("Aguacate")) {
                val options = BitmapFactory.Options()
                options.inSampleSize = 8
                val icon = BitmapFactory.decodeResource(context.resources,
                        R.drawable.aguacate, options)
                imgTipoProducto.setImageBitmap(icon)
            } else if (data.Nombre.equals("Fríjol")) {
                val options = BitmapFactory.Options()
                options.inSampleSize = 8
                val icon = BitmapFactory.decodeResource(context.resources,
                        R.drawable.frijol, options)
                imgTipoProducto.setImageBitmap(icon)
            } else if (data.Nombre.equals("Plátano")) {
                val options = BitmapFactory.Options()
                options.inSampleSize = 8
                val icon = BitmapFactory.decodeResource(context.resources,
                        R.drawable.platano, options)
                imgTipoProducto.setImageBitmap(icon)
            }*/

            if(data.Imagen!=null){
                // val bitmap = BitmapFactory.decodeByteArray(foto, 0, foto!!.size)
                // imgTipoProducto.setImageBitmap(bitmap)
                try {
                    val foto = data.Imagen?.blob
                    val bitmapBlob = BitmapFactory.decodeByteArray(foto, 0, foto!!.size)
                    imgTipoProducto.setImageBitmap(bitmapBlob)
                }catch (ex:Exception){
                    var ss= ex.toString()
                    Log.d("Convert Image", "defaultValue = " + ss);
                }
            }

            txtNombreTipoProducto.text = data.Nombre

            itemView.setOnClickListener {
                postEvento(RequestEventProductosComprador.ITEM_EVENT, data)
            }
        }
    }


}