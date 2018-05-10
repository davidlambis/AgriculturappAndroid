package com.interedes.agriculturappv3.productor.modules.asistencia_tecnica_module.plagas.adapters

import android.graphics.BitmapFactory
import android.support.v7.widget.RecyclerView
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import com.interedes.agriculturappv3.R
import com.interedes.agriculturappv3.productor.models.plagas.TipoEnfermedad
import com.interedes.agriculturappv3.productor.modules.asistencia_tecnica_module.plagas.events.PlagasEvent
import com.interedes.agriculturappv3.libs.EventBus
import com.interedes.agriculturappv3.libs.GreenRobotEventBus
import com.interedes.agriculturappv3.productor.models.plagas.Enfermedad
import com.interedes.agriculturappv3.productor.models.plagas.FotoEnfermedad
import com.interedes.agriculturappv3.productor.models.plagas.FotoEnfermedad_Table
import com.raizlabs.android.dbflow.data.Blob
import com.raizlabs.android.dbflow.sql.language.SQLite

/**
 * Created by MAKERS on 26/03/2018.
 */
class SelectPlagasAdapter(val lista: ArrayList<Enfermedad>) : RecyclerView.Adapter<SelectPlagasAdapter.ViewHolder>() {

    companion object {
        var instance: SelectPlagasAdapter? = null
    }

    var eventBus: EventBus? = null

    init {
        eventBus = GreenRobotEventBus()
        SelectPlagasAdapter.instance = this
    }

    override fun onBindViewHolder(holder: SelectPlagasAdapter.ViewHolder, position: Int) {
        //Llama al método del holder para cargar los items
        holder.bindItems(lista[position], position)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SelectPlagasAdapter.ViewHolder {
        val v = LayoutInflater.from(parent.context).inflate(R.layout.content_list_select_plaga, parent, false)
        return SelectPlagasAdapter.ViewHolder(v)
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
            val txtNombrePlaga: TextView = itemView.findViewById(R.id.txtNombrePlaga)
            val imgPlaga: ImageView = itemView.findViewById(R.id.imgPlaga)


            txtNombrePlaga.text = data.NombreTipoEnfermedad
            var firtsFoto= SQLite.select().from(FotoEnfermedad::class.java).where(FotoEnfermedad_Table.EnfermedadesId.eq(data.Id)).querySingle()
            if(firtsFoto!=null){
                if(firtsFoto.blobImagen!=null){
                    try {
                        val foto = firtsFoto.blobImagen?.blob
                        data.blobImagenEnfermedad= Blob(firtsFoto.blobImagen?.blob)
                        val bitmapBlob = BitmapFactory.decodeByteArray(foto, 0, foto!!.size)
                        imgPlaga.setImageBitmap(bitmapBlob)
                    }catch (ex:Exception){
                        var ss= ex.toString()
                        Log.d("Convert Image", "defaultValue = " + ss);
                    }
                }
            }


            itemView.setOnClickListener {
                SelectPlagasAdapter.instance?.postEvent(PlagasEvent.ITEM_SELECT_PLAGA_EVENT, data)
            }
        }
    }

    fun postEvent(type: Int, enfermedad: Enfermedad?) {
        val tipoEnfermedad = enfermedad as Object
        val event = PlagasEvent(type, null, tipoEnfermedad, null)
        event.eventType = type
        eventBus?.post(event)
    }
}