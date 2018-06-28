package com.interedes.agriculturappv3.modules.productor.asistencia_tecnica_module.insumos.adapters

import android.graphics.BitmapFactory
import android.support.v7.widget.RecyclerView
import android.text.TextUtils
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import android.widget.RatingBar
import android.widget.TextView
import com.interedes.agriculturappv3.R
import com.interedes.agriculturappv3.modules.models.insumos.Insumo
import com.interedes.agriculturappv3.modules.productor.asistencia_tecnica_module.insumos.events.InsumosEvent
import com.interedes.agriculturappv3.libs.EventBus
import com.interedes.agriculturappv3.libs.GreenRobotEventBus
import com.interedes.agriculturappv3.modules.models.insumos.Insumo_Table
import com.interedes.agriculturappv3.modules.models.tratamiento.Tratamiento
import com.raizlabs.android.dbflow.sql.language.SQLite
import com.interedes.agriculturappv3.R.id.imageView
import android.graphics.Bitmap
import android.os.Handler


class InsumosAdapter(val lista: ArrayList<Tratamiento>) : RecyclerView.Adapter<InsumosAdapter.ViewHolder>() {

    companion object {
        var eventBus: EventBus? = null
        fun postEvent(type: Int, tratamiento: Tratamiento?) {
            val insumoMutable = tratamiento as Object
            val event = InsumosEvent(type, null, insumoMutable, null)
            event.eventType = type
            eventBus?.post(event)
        }
    }


    init {
        eventBus = GreenRobotEventBus()
    }

    override fun onBindViewHolder(holder: InsumosAdapter.ViewHolder, position: Int) {
        //Llama al método del holder para cargar los items
        holder.bindItems(lista[position], position)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): InsumosAdapter.ViewHolder {
        val v = LayoutInflater.from(parent.context).inflate(R.layout.content_list_insumos, parent, false)
        return InsumosAdapter.ViewHolder(v)
    }

    override fun getItemCount(): Int {
        return lista.size
    }

    fun setItems(newItems: List<Tratamiento>) {
        lista.addAll(newItems)
        notifyDataSetChanged()

    }

    fun clear() {
        lista.clear()
        notifyDataSetChanged()
    }

    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {


        fun bindItems(data: Tratamiento, pos: Int) = with(itemView) {
            val txt_nombre_insumo: TextView = itemView.findViewById(R.id.txtNombreComercial)
            val image_view_insumo: ImageView = itemView.findViewById(R.id.imageViewInsumo)
            val text_descripcion_insumo: TextView = itemView.findViewById(R.id.txtIngredienteActivo)
           /// val expandable: Expandable = itemView.findViewById(R.id.expandable)
            //val txt_descripcion_insumo: TextView = itemView.findViewById(R.id.txtIngredienteActivo)
            val btn_ver_tratamiento: Button = itemView.findViewById(R.id.btnVerTratamiento)
            val rating_bar: RatingBar = itemView.findViewById(R.id.ratingBar)
            val txtrating_bar: TextView = itemView.findViewById(R.id.txtRating)



            txt_nombre_insumo.text = data.Nombre_Insumo
            var firtsInsumo= SQLite.select().from(Insumo::class.java).where(Insumo_Table.Id.eq(data.InsumoId)).querySingle()
            if(firtsInsumo!=null){
                if(firtsInsumo.blobImagen!=null){
                    try {
                        val foto = firtsInsumo.blobImagen?.blob
                        //data.blobImagenEnfermedad= Blob(firtsFoto.blobImagen?.blob)
                        val bitmapBlob = BitmapFactory.decodeByteArray(foto, 0, foto!!.size)
                       // image_view_insumo.setImageBitmap(bitmapBlob)
                        image_view_insumo.setImageBitmap(bitmapBlob)


                    }catch (ex:Exception){
                        var ss= ex.toString()
                        Log.d("Convert Image", "defaultValue = " + ss);
                    }
                }
            }


            text_descripcion_insumo.text = data.Desc_Formulacion
            text_descripcion_insumo.maxLines=2
            text_descripcion_insumo.ellipsize= TextUtils.TruncateAt.END

            var calificacionTratamiento:Float?= data?.CalificacionPromedio?.toFloat()
            rating_bar.rating = calificacionTratamiento!!
            rating_bar.setIsIndicator(true)
            rating_bar.isClickable = false

            txtrating_bar.setText(context.getString(R.string.calificacion_tratamiento,data?.CalificacionPromedio))



            /*
            expandable.setExpandingListener(object : ExpandingListener {
                override fun onExpanded() {
                    txt_descripcion_insumo.visibility = View.VISIBLE
                }

                override fun onCollapsed() {
                    txt_descripcion_insumo.visibility = View.GONE
                }
            })

            */

            btn_ver_tratamiento.setOnClickListener {
                postEvent(InsumosEvent.ITEM_EVENT, data)
            }
        }
    }

/*
    //Used to display bitmap in the UI thread
    class BitmapDisplayer(var bitmap: Bitmap?) : Runnable {
        override fun run() {
            if (imageViewReused(photoToLoad))
                return
            if (bitmap != null)
                photoToLoad.imageView.setImageBitmap(bitmap)
            else
                photoToLoad.imageView.setImageResource(stub_id)
        }
    }*/



}