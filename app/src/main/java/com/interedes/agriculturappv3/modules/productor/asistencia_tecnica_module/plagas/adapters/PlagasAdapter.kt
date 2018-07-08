package com.interedes.agriculturappv3.modules.productor.asistencia_tecnica_module.plagas.adapters


import android.graphics.BitmapFactory
import android.support.v7.widget.RecyclerView
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import com.interedes.agriculturappv3.R
import com.interedes.agriculturappv3.modules.productor.asistencia_tecnica_module.plagas.events.PlagasEvent
import com.interedes.agriculturappv3.libs.EventBus
import com.interedes.agriculturappv3.libs.GreenRobotEventBus
import com.interedes.agriculturappv3.modules.models.plagas.Enfermedad
import com.interedes.agriculturappv3.modules.models.plagas.FotoEnfermedad
import com.interedes.agriculturappv3.modules.models.plagas.FotoEnfermedad_Table
import com.raizlabs.android.dbflow.data.Blob
import com.raizlabs.android.dbflow.sql.language.SQLite
import com.robertlevonyan.views.expandable.Expandable
import com.robertlevonyan.views.expandable.ExpandingListener
import kotlinx.android.synthetic.main.content_list_plagas.view.*
import android.graphics.Bitmap
import java.io.ByteArrayOutputStream
import android.R.attr.bitmap
import android.os.Handler
import com.interedes.agriculturappv3.R.id.imageView
import com.squareup.picasso.Picasso
import java.io.File


class PlagasAdapter(val lista: ArrayList<Enfermedad>) : RecyclerView.Adapter<PlagasAdapter.ViewHolder>() {

    companion object {
        var eventBus: EventBus? = null
        fun postEvent(type: Int, plaga: Enfermedad?) {
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

    fun setItems(newItems: List<Enfermedad>) {
        lista.addAll(newItems)
        notifyDataSetChanged()

    }

    fun clear() {
        lista.clear()
        notifyDataSetChanged()
    }

    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        fun bindItems(data: Enfermedad, pos: Int) = with(itemView) {
            val txt_nombre_enfermedad: TextView = itemView.findViewById(R.id.txtNombreCultivo)
            val txt_nombre_cientifico: TextView = itemView.findViewById(R.id.txtNombreDetalleProducto)
            val image_enfermedad: ImageView = itemView.findViewById(R.id.imageViewProducto)
            val txt_descripcion_enfermedad: TextView = itemView.findViewById(R.id.txtDescripcionEnfermedad)
            val btn_ver_insumos: Button = itemView.findViewById(R.id.btnVerInsumos)
            val expandable : Expandable = itemView.findViewById(R.id.expandable)

            txt_nombre_enfermedad.text = data.NombreTipoEnfermedad
            txt_nombre_cientifico.text = data.NombreCientificoTipoEnfermedad
            txtDescripcionEnfermedad.text = data.Descripcion

          //txt_descripcion_enfermedad.text=data.DescripcionTipoEnfermedad
         //var firtsFoto= SQLite.select().from(FotoEnfermedad::class.java).where(FotoEnfermedad_Table.EnfermedadesId.eq(data.Id)).querySingle()

            if(data.RutaImagenEnfermedad!=null){
                val imgFile =   File(data.RutaImagenEnfermedad);
                if(imgFile.exists()){
                    //var myBitmap = BitmapFactory.decodeFile(imgFile.getAbsolutePath());
                    // imgPlaga.setImageBitmap(myBitmap);
                    Picasso.get()
                            .load(imgFile)
                            .fit()
                            .centerCrop()
                            .placeholder(R.drawable.emtpy_img_plaga)
                            .error(R.drawable.emtpy_img_plaga)
                            .into(image_enfermedad);

                }else{
                    val largeIcon = BitmapFactory.decodeResource(resources, R.drawable.emtpy_img_plaga)
                    image_enfermedad.setImageBitmap(largeIcon)
                }
                // uiHandler.post( Runnable() {
                /// });
            }else{
                val largeIcon = BitmapFactory.decodeResource(resources, R.drawable.emtpy_img_plaga)
                image_enfermedad.setImageBitmap(largeIcon)
                //imgPlaga.setImageBitmap(largeIcon)
            }

           /* if(data.blobImagenEnfermedad!=null){
                val foto = data.blobImagenEnfermedad?.blob
                val bitmapBlob = BitmapFactory.decodeByteArray(foto, 0, foto!!.size)
                /// val uiHandler = Handler()
                image_enfermedad.setImageBitmap(bitmapBlob)
                // uiHandler.post( Runnable() {
                /// });
            }else{
                val largeIcon = BitmapFactory.decodeResource(resources, R.drawable.emtpy_img_plaga)
                image_enfermedad.setImageBitmap(largeIcon)
            }*/

           // image_enfermedad.setImageResource(data.Imagen!!)
           // txt_descripcion_enfermedad.text = data.Descripcion



            image_enfermedad.setOnClickListener {
                postEvent(PlagasEvent.ITEM_EVENT_PLAGA, data)
            }

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