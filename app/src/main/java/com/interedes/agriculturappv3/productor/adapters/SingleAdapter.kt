package com.interedes.agriculturappv3.productor.adapters

import android.content.Context
import android.support.v7.widget.RecyclerView
import android.view.View
import android.view.ViewGroup
import android.view.LayoutInflater
import com.interedes.agriculturappv3.R
import com.interedes.agriculturappv3.productor.models.ItemLista
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import com.interedes.agriculturappv3.services.Resources_Menu

//Recibe directamente la lista y el listener del click
class SingleAdapter(val lista: ArrayList<ItemLista>,var TYPE_MENU:Int,var context:Context?, val listener: (Int) -> Unit) : RecyclerView.Adapter<SingleAdapter.ViewHolder>() {



    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        //Llama al método del holder para cargar los items
        holder.bindItems(lista[position], position,listener)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val v = LayoutInflater.from(parent.context).inflate(R.layout.single_item, parent, false)
        return ViewHolder(v,TYPE_MENU,context )
    }

    override fun getItemCount(): Int {
        return lista.size
    }


    class ViewHolder(itemView: View, var TYPE_MENU: Int,var context:Context?) : RecyclerView.ViewHolder(itemView) {

        fun bindItems(data: ItemLista, pos: Int, listener: (Int) -> Unit) = with(itemView) {
            val image: ImageView = itemView.findViewById(R.id.imageView)
            val textNombre: TextView = itemView.findViewById(R.id.textView)
            val content: LinearLayout = itemView.findViewById(R.id.content)
            //image.setImageBitmap(data.Imagen)
            image.setImageResource(data.Imagen)
            textNombre.text = data.Nombre
            content.setBackgroundResource(R.drawable.sombra_drawing)

            //Aplicar diseños al menu
            if(Resources_Menu.MENU_MAIN==TYPE_MENU){
                if(data.Identificador.equals("asistencia_tecnica")){
                    textNombre.setTextColor(context.resources.getColor(R.color.purple))
                }else if(data.Identificador.equals("comercial")){
                    textNombre.setTextColor(context.resources.getColor(R.color.blue))
                }else if(data.Identificador.equals("contabilidad")){
                    textNombre.setTextColor(context.resources.getColor(R.color.green))
                }


            }else if(Resources_Menu.MENU_MODULE_ASISTENCIA_TECNICA==TYPE_MENU){
                textNombre.setTextColor(context.resources.getColor(R.color.purple))
            }
            else if(Resources_Menu.MENU_MODULE_COMERCIAL==TYPE_MENU){
                textNombre.setTextColor(context.resources.getColor(R.color.blue))
            }
            else if(Resources_Menu.MENU_MODULE_ACCOUNTANT==TYPE_MENU){
                textNombre.setTextColor(context.resources.getColor(R.color.green))
            }

            //El listener en base a la posición
            itemView.setOnClickListener {
                listener(pos)
            }

        }

    }

}