package com.interedes.agriculturappv3.productor.modules.comercial_module.productos.adapters

import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import com.interedes.agriculturappv3.R
import com.interedes.agriculturappv3.libs.EventBus
import com.interedes.agriculturappv3.libs.GreenRobotEventBus
import com.interedes.agriculturappv3.productor.models.producto.Producto
import com.interedes.agriculturappv3.productor.modules.comercial_module.productos.events.ProductosEvent
import java.util.ArrayList
import android.graphics.BitmapFactory
import android.widget.ImageButton
import android.widget.TextView


class ProductosAdapter(var lista: ArrayList<Producto>) : RecyclerView.Adapter<ProductosAdapter.ViewHolder>() {


    init {
        eventBus = GreenRobotEventBus()
    }

    companion object {
        var eventBus: EventBus? = null
        fun postEvent(type: Int, producto: Producto?) {
            val productoMutable = producto as Object
            val event = ProductosEvent(type, null, productoMutable, null)
            event.eventType = type
            eventBus?.post(event)
        }
    }

    override fun onBindViewHolder(holder: ProductosAdapter.ViewHolder, position: Int) {
        //Llama al método del holder para cargar los items
        holder.bindItems(lista[position], position)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ProductosAdapter.ViewHolder {
        val v = LayoutInflater.from(parent.context).inflate(R.layout.content_list_productos, parent, false)
        return ProductosAdapter.ViewHolder(v)
    }

    override fun getItemCount(): Int {
        return lista.size
    }


    fun setItems(newItems: List<Producto>) {
        lista.addAll(newItems)
        notifyDataSetChanged()

    }

    fun clear() {
        lista.clear()
        notifyDataSetChanged()
    }

    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        fun bindItems(data: Producto, pos: Int) = with(itemView) {
            val image: ImageView = itemView.findViewById(R.id.imageViewProducto)
            val txtNombreCultivo: TextView = itemView.findViewById(R.id.txtNombreCultivo)
            val txtNombreDetalleProducto: TextView = itemView.findViewById(R.id.txtNombreDetalleProducto)
            val txtDescripcionProducto: TextView = itemView.findViewById(R.id.txtDescripcionProducto)
            val txtFechaDisponibilidad: TextView = itemView.findViewById(R.id.txtFechaDisponibilidad)
            val txtPrecioProducto: TextView = itemView.findViewById(R.id.txtPrecioProducto)
            val btnEdit: ImageButton = itemView.findViewById(R.id.btnAction2)
            val btnDelete: ImageButton = itemView.findViewById(R.id.btnAction3)

            val byte = data.Imagen?.getBlob()
            val bitmap = BitmapFactory.decodeByteArray(byte, 0, byte!!.size)
            image.setImageBitmap(bitmap)

            txtNombreCultivo.text = String.format(context.getString(R.string.title_adapter_cultivo), data.NombreCultivo)
            txtNombreDetalleProducto.text = String.format(context.getString(R.string.title_adapter_detalle_producto), data.NombreDetalleTipoProducto)
            txtDescripcionProducto.text = String.format(context.getString(R.string.title_adapter_descripcion_producto), data.Descripcion)
            txtFechaDisponibilidad.text = String.format(context.getString(R.string.title_adapter_fecha_disponibilidad), data.getFechaLimiteDisponibilidadFormat())
            txtPrecioProducto.text = String.format(context.getString(R.string.title_adapter_precio_producto), data.Precio)

            btnEdit.setOnClickListener {
                postEvent(ProductosEvent.ITEM_EDIT_EVENT, data)
            }

            btnDelete.setOnClickListener {
                postEvent(ProductosEvent.ITEM_DELETE_EVENT, data)
            }

        }
    }

}