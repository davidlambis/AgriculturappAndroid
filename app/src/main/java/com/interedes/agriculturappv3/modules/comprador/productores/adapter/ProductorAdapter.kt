package com.interedes.agriculturappv3.modules.comprador.productores.adapter

import android.graphics.BitmapFactory
import android.support.v7.widget.RecyclerView
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.RatingBar
import android.widget.TextView
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.interedes.agriculturappv3.R
import com.interedes.agriculturappv3.libs.EventBus
import com.interedes.agriculturappv3.libs.GreenRobotEventBus
import com.interedes.agriculturappv3.modules.comprador.productores.events.RequestEventProductor
import com.interedes.agriculturappv3.modules.models.departments.Ciudad
import com.interedes.agriculturappv3.modules.models.producto.Producto
import com.raizlabs.android.dbflow.sql.language.SQLite
import kotlinx.android.synthetic.main.fragment_ofertas.view.*
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.ValueEventListener
import com.interedes.agriculturappv3.modules.models.chat.UserFirebase
import com.squareup.picasso.Picasso


class ProductorAdapter(val lista: ArrayList<Producto>) : RecyclerView.Adapter<ProductorAdapter.ViewHolder>() {


        companion object {
            private var mUsersDBRef: DatabaseReference? = null
            var eventBus: EventBus? = null
            fun postEvento(type: Int, producto: Producto) {
                var productonMutable= producto as Object
                val event = RequestEventProductor(type,null, productonMutable,null)
                event.eventType = type
                eventBus?.post(event)
            }
        }

        init {
            mUsersDBRef=FirebaseDatabase.getInstance().reference
            eventBus = GreenRobotEventBus()
        }

        override fun onBindViewHolder(holder: ViewHolder, position: Int) {
            //Llama al método del holder para cargar los items
            holder.bindItems(lista[position], position)
        }

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
            val v = LayoutInflater.from(parent.context).inflate(R.layout.content_list_producto_productor, parent, false)
            return ViewHolder(v)
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



                val txtNombreProductor: TextView = itemView.findViewById(R.id.txtNombreProductor)
                val imgProductor: ImageView = itemView.findViewById(R.id.imgProductor)

                val ratingBar: RatingBar = itemView.findViewById(R.id.ratingBar)
                val txtProducto: TextView = itemView.findViewById(R.id.txtProducto)
                val txtUbicacion: TextView = itemView.findViewById(R.id.txtUbicacion)
                val txtFechaDisponibilidad: TextView = itemView.findViewById(R.id.txtFechaDisponibilidad)
                val txtDisponibilidad: TextView = itemView.findViewById(R.id.txtDisponibilidad)
                val txtPrecio: TextView = itemView.findViewById(R.id.txtPrecio)


                txtProducto.setText(data.Nombre)

                var disponibilidad=""

                if(data.Stock.toString().contains(".0")){
                    disponibilidad=String.format(context!!.getString(R.string.price_empty_signe),
                            data.Stock)
                }else{
                    disponibilidad=data.Stock.toString()
                }

                ratingBar.rating = 3.5f

                txtNombreProductor.setText(data.NombreProductor)
                txtDisponibilidad.setText(String.format("%s: %s %s", data.NombreCalidad,disponibilidad,data.NombreUnidadMedidaCantidad))
                txtFechaDisponibilidad.setText(data.getFechaLimiteDisponibilidadFormat())

                txtPrecio?.setText(String.format(context!!.getString(R.string.price_producto),
                        data.Precio,data.PrecioUnidadMedida))

               txtUbicacion.setText(String.format("%s / %s", data.Ciudad, data.Departamento))
                txtFechaDisponibilidad.setText(data.getFechaLimiteDisponibilidadFormat())


                val query = mUsersDBRef?.child("Users")?.orderByChild("correo")?.equalTo(data.EmailProductor)
                query?.addListenerForSingleValueEvent(object : ValueEventListener {
                    override fun onDataChange(dataSnapshot: DataSnapshot) {
                        if (dataSnapshot.exists()) {
                            // dataSnapshot is the "issue" node with all children with id 0
                            for (issue in dataSnapshot.children) {
                                // do something with the individual "issues"
                                var user = issue.getValue<UserFirebase>(UserFirebase::class.java)
                                //if not current user, as we do not want to show ourselves then chat with ourselves lol
                                try {
                                    try {
                                        Picasso.with(context).load(user?.Imagen).placeholder(R.drawable.default_avata).into(imgProductor)
                                    } catch (e: Exception) {
                                        e.printStackTrace()
                                    }
                                } catch (e: Exception) {
                                    e.printStackTrace()
                                }
                            }
                        }
                    }

                    override fun onCancelled(databaseError: DatabaseError) {

                    }
                })


                /*
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
                }*/


                itemView.setOnClickListener {
                    postEvento(RequestEventProductor.ITEM_EVENT, data)
                }
            }
        }
}



