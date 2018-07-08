package com.interedes.agriculturappv3.modules.productor.asistencia_tecnica_module.plagas.adapters

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.os.Handler
import android.support.v7.widget.RecyclerView
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
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
import com.squareup.picasso.Picasso
import java.io.ByteArrayOutputStream
import java.io.File

/**
 * Created by MAKERS on 26/03/2018.
 */
class SelectPlagasAdapter(val lista: ArrayList<Enfermedad>) : RecyclerView.Adapter<SelectPlagasAdapter.ViewHolder>() {

    companion object {

        var eventBus: EventBus? = null
        var instance: SelectPlagasAdapter? = null

        fun postEvent(type: Int, enfermedad: Enfermedad?) {
            val tipoEnfermedad = enfermedad as Object
            val event = PlagasEvent(type, null, tipoEnfermedad, null)
            event.eventType = type
            eventBus?.post(event)
        }
    }



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
           /* if(data.blobImagenEnfermedad!=null){
                val foto = data.blobImagenEnfermedad?.blob
                val bitmapBlob = BitmapFactory.decodeByteArray(foto, 0, foto!!.size)
               /// val uiHandler = Handler()
                imgPlaga.setImageBitmap(bitmapBlob)
               // uiHandler.post( Runnable() {
               /// });
            }else{
                val largeIcon = BitmapFactory.decodeResource(resources, R.drawable.emtpy_img_plaga)
                imgPlaga.setImageBitmap(largeIcon)
            }*/

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
                           .into(imgPlaga);

               }else{
                   val largeIcon = BitmapFactory.decodeResource(resources, R.drawable.emtpy_img_plaga)
                   imgPlaga.setImageBitmap(largeIcon)
               }
                // uiHandler.post( Runnable() {
                /// });
            }else{
                val largeIcon = BitmapFactory.decodeResource(resources, R.drawable.emtpy_img_plaga)
                imgPlaga.setImageBitmap(largeIcon)
                //imgPlaga.setImageBitmap(largeIcon)
            }

            /*val firtsFoto= SQLite.select().from(FotoEnfermedad::class.java).where(FotoEnfermedad_Table.EnfermedadesId.eq(data.Id)).querySingle()
            if(firtsFoto!=null){
                if(firtsFoto.blobImagen!=null){
                    try {
                        val foto = firtsFoto.blobImagen?.blob
                        data.blobImagenEnfermedad= Blob(firtsFoto.blobImagen?.blob)
                        val bitmapBlob = BitmapFactory.decodeByteArray(foto, 0, foto!!.size)
                        //imgPlaga.setImageBitmap(bitmapBlob)

                        val uiHandler = Handler()
                        uiHandler.post( Runnable() {
                            imgPlaga.setImageBitmap(bitmapBlob)
                        });


                    }catch (ex:Exception){
                        var ss= ex.toString()
                        Log.d("Convert Image", "defaultValue = " + ss);
                    }
                }
            }else{
                //val largeIcon = BitmapFactory.decodeResource(resources, R.drawable.emtpy_img_plaga)
                //val bos = ByteArrayOutputStream()
                //largeIcon.compress(Bitmap.CompressFormat.PNG, 100, bos)
                //data.blobImagenEnfermedad=Blob(bos.toByteArray())
                //imgPlaga.setImageBitmap(largeIcon)
               /* try {
                    Picasso.with(context).load(R.drawable.emtpy_img_plaga).resize(50, 50).placeholder(R.drawable.emtpy_img_plaga).centerCrop().into(imgPlaga)
                } catch (e: Exception) {
                    e.printStackTrace()
                }*/
            }
            */

            itemView.setOnClickListener {
               postEvent(PlagasEvent.ITEM_SELECT_PLAGA_EVENT, data)
            }
        }
    }


}