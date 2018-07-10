package com.interedes.agriculturappv3.modules.comprador.productores.adapter

import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.RatingBar
import android.widget.TextView
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.interedes.agriculturappv3.R
import com.interedes.agriculturappv3.libs.EventBus
import com.interedes.agriculturappv3.libs.GreenRobotEventBus
import com.interedes.agriculturappv3.modules.comprador.productores.events.RequestEventProductor
import com.interedes.agriculturappv3.modules.models.producto.Producto
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.ValueEventListener
import com.interedes.agriculturappv3.AgriculturApplication
import com.interedes.agriculturappv3.libs.GlideApp
import com.interedes.agriculturappv3.modules.models.chat.UserFirebase
import com.interedes.agriculturappv3.services.api.ApiInterface
import com.interedes.agriculturappv3.services.listas.Listas
import com.interedes.agriculturappv3.services.resources.S3Resources
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import java.util.concurrent.TimeUnit


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

        fun notifyDataChanged() {
            notifyDataSetChanged()
        }

        fun clear() {
            lista.clear()
            notifyDataSetChanged()
        }

    fun add( producto: Producto) {
        /*if(!list.contains(notification)){
            list.add(notification)
            notifyDataSetChanged()
        }*/

        if (!alreadyInAdapter(producto)) {
            //list.add(notification)
            //notifyDataSetChanged()
            this.lista.add(0, producto);
            notifyItemInserted(0)
            notifyDataSetChanged()
        }

        //notifyItemInserted(position)
    }

    fun getPositionNotificationById(id: Long): Int {
        var position = 0
        for (usenotificationr in lista) {
            if (usenotificationr.ProductoId!!.equals(id)) {
                break
            }
            position++
        }

        return position
    }

    private fun alreadyInAdapter(newProducto: Producto): Boolean {
        var alreadyInAdapter = false
        for (notification in this.lista) {
            if (notification.ProductoId!!.equals(newProducto.ProductoId)) {
                alreadyInAdapter = true
                break
            }
        }
        return alreadyInAdapter
    }


    fun remove(producto: Producto) {
        val pos = getPositionNotificationById(producto.ProductoId!!)
        lista.removeAt(pos)
        this.notifyDataSetChanged()
    }


    fun update(producto: Producto) {
        val pos = getPositionNotificationById(producto.ProductoId!!)
        lista.set(pos, producto)
        this.notifyDataSetChanged()
    }




        class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

            fun bindItems(data: Producto, pos: Int) = with(itemView) {


                var txtNombreProductor: TextView = itemView.findViewById(R.id.txtNombreProductor)
                var imgProductor: ImageView = itemView.findViewById(R.id.imgProductor)

                var ratingBar: RatingBar = itemView.findViewById(R.id.ratingBar)
                var txtProducto: TextView = itemView.findViewById(R.id.txtProducto)
                var txtUbicacion: TextView = itemView.findViewById(R.id.txtUbicacion)
                var txtFechaDisponibilidad:TextView = itemView.findViewById(R.id.txtFechaDisponibilidad)
                var txtDisponibilidad: TextView = itemView.findViewById(R.id.txtDisponibilidad)
                var txtPrecio: TextView = itemView.findViewById(R.id.txtPrecio)



                txtProducto?.setText(data.Nombre)

                var disponibilidad = ""

                if (data.Stock.toString().contains(".0")) {
                    disponibilidad = String.format(context.getString(R.string.price_empty_signe)!!,
                            data.Stock)
                } else {
                    disponibilidad = data.Stock.toString()
                }

                ratingBar?.rating = 3.5f

                txtNombreProductor?.setText(data.NombreProductor)
                txtDisponibilidad?.setText(String.format("%s: %s %s", data.NombreCalidad, disponibilidad, data.NombreUnidadMedidaCantidad))
                txtFechaDisponibilidad?.setText(data.getFechaLimiteDisponibilidadFormat())

                txtPrecio?.setText(String.format(context.getString(R.string.price_producto),
                        data.Precio, data.PrecioUnidadMedida))

                txtUbicacion?.setText(String.format("%s / %s", data.Ciudad, data.Departamento))
                txtFechaDisponibilidad?.setText(data.getFechaLimiteDisponibilidadFormat())

                GlideApp.with(context)
                        .load(S3Resources.RootImage+"${data.Usuario?.Fotopefil}")
                        .diskCacheStrategy(DiskCacheStrategy.ALL)
                        .productorPhoto()
                        .into(imgProductor);

               /* val apiService = ApiInterface.create()
                val queryCustom = Listas.queryGeneral("Email",data.EmailProductor!!)
                val callusuario = apiService.getUserByEmail(queryCustom)
                callusuario.delay(500, TimeUnit.MILLISECONDS)?.subscribeOn(Schedulers.io())?.observeOn(AndroidSchedulers.mainThread())?.subscribe({ searchResponse ->
                    //Log.d("search", searchString)
                    val usuario =searchResponse.value
                    if(usuario!=null){
                        for (item in usuario){
                            GlideApp.with(context)
                                    .load(S3Resources.RootImage+"${item.Fotopefil}")
                                    .diskCacheStrategy(DiskCacheStrategy.ALL)
                                    .productorPhoto()
                                    .into(imgProductor);
                        }
                    }
                },{ throwable ->{
                    val error= throwable.toString()
                }
                })*/


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
                                    //Picasso.with(contextLocal).load(user?.Imagen).placeholder(R.drawable.ic_account_box_green).into(imgProductor)
                                    /*val builder = Picasso.Builder(contextLocal!!)
                                    builder.listener(object : Picasso.Listener {
                                        override fun onImageLoadFailed(picasso: Picasso, uri: Uri, exception: Exception) {
                                            imgProductor?.setImageResource(R.drawable.ic_account_box_green)
                                            imgProductor?.setScaleType(ImageView.ScaleType.CENTER_INSIDE);
                                        }
                                    })
                                    builder.build().load(user?.Imagen).into(imgProductor)
                                    */
                                    /*Picasso.get()
                                            .load(user?.Imagen)
                                            .fit()
                                            .centerCrop()
                                            .placeholder(R.drawable.ic_account_box_green)
                                            .error(R.drawable.ic_account_box_green)
                                            .into(imgProductor);*/


                                } catch (e: Exception) {
                                    e.printStackTrace()
                                }

                            }
                        }
                    }

                    override fun onCancelled(databaseError: DatabaseError) {

                    }
                })

                itemView.setOnClickListener {
                    postEvento(RequestEventProductor.ITEM_EVENT, data)
                }
            }
        }
}



