package com.interedes.agriculturappv3.modules.comprador.productores.adapter

import android.app.Activity
import android.content.Context
import android.net.Uri
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.ProgressBar
import android.widget.RatingBar
import android.widget.TextView
import com.google.firebase.database.*
import com.interedes.agriculturappv3.R
import com.interedes.agriculturappv3.libs.EventBus
import com.interedes.agriculturappv3.libs.GreenRobotEventBus
import com.interedes.agriculturappv3.modules.comprador.productores.events.RequestEventProductor
import com.interedes.agriculturappv3.modules.models.chat.UserFirebase
import com.interedes.agriculturappv3.modules.models.producto.Producto
import com.squareup.picasso.Picasso

/**
 * Created by EnuarMunoz on 28/05/18.
 */
class ProductorMoreAdapter(val lista: ArrayList<Producto>?, context: Context?) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    companion object {
        var mUsersDBRef: DatabaseReference? = null

        val TYPE_MOVIE = 0
        val TYPE_LOAD = 1
        var contextLocal: Context? = null

        var loadMoreListener: OnLoadMoreListener? = null
        var isLoading = false
        var isMoreDataAvailable = true


        var eventBus: EventBus? = null
        fun postEvento(type: Int, producto: Producto) {
            var productonMutable = producto as Object
            val event = RequestEventProductor(type, null, productonMutable, null)
            event.eventType = type
            eventBus?.post(event)
        }
    }

    init {
        mUsersDBRef = FirebaseDatabase.getInstance().reference
        eventBus = GreenRobotEventBus()
        contextLocal = context
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        if (position >= itemCount - 1 && isMoreDataAvailable && !isLoading && loadMoreListener != null) {
            isLoading = true
            loadMoreListener?.onLoadMore()
        }
        if (getItemViewType(position) == TYPE_MOVIE) {
            (holder as ProductHolder).bindData(lista?.get(position)!!)
        }
        //No else part needed as load holder doesn't bind any data
    }


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {

        val inflater = LayoutInflater.from(parent.context)
        return if (viewType == TYPE_MOVIE) {
            ProductHolder(inflater.inflate(R.layout.content_list_producto_productor, parent, false))
        } else {
            LoadHolder(inflater.inflate(R.layout.item_progress, parent, false))
        }
    }


    private fun getViewHolder(parent: ViewGroup, inflater: LayoutInflater): View? {
        val v1 = inflater.inflate(R.layout.content_list_producto_productor, parent, false)
        return v1
    }


    override fun getItemViewType(position: Int): Int {
        return if (lista?.get(position)?.Enabled?.equals(true)!!) {
            TYPE_MOVIE
        } else {
            TYPE_LOAD
        }
    }

    override fun getItemCount(): Int {
        return lista?.size!!
    }


    fun setItems(newItems: List<Producto>) {
        lista?.addAll(newItems)
        notifyDataSetChanged()

    }

     /*fun clear() {
         lista.clear()
         notifyDataSetChanged()
     }*/

    /* VIEW HOLDERS */

    class ProductHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        var txtNombreProductor: TextView? = null
        var imgProductor: ImageView? = null
        var ratingBar: RatingBar? = null
        var txtProducto: TextView? = null
        var txtUbicacion: TextView? = null
        var txtFechaDisponibilidad: TextView? = null
        var txtDisponibilidad: TextView? = null
        var txtPrecio: TextView? = null

        init {
            txtNombreProductor = itemView.findViewById(R.id.txtNombreProductor)
            imgProductor = itemView.findViewById(R.id.imgProductor)

            ratingBar = itemView.findViewById(R.id.ratingBar)
            txtProducto = itemView.findViewById(R.id.txtProducto)
            txtUbicacion = itemView.findViewById(R.id.txtUbicacion)
            txtFechaDisponibilidad = itemView.findViewById(R.id.txtFechaDisponibilidad)
            txtDisponibilidad = itemView.findViewById(R.id.txtDisponibilidad)
            txtPrecio = itemView.findViewById(R.id.txtPrecio)
        }

        fun bindData(data: Producto) {
            txtProducto?.setText(data.Nombre)

            var disponibilidad = ""

            if (data.Stock.toString().contains(".0")) {
                disponibilidad = String.format(contextLocal?.getString(R.string.price_empty_signe)!!,
                        data.Stock)
            } else {
                disponibilidad = data.Stock.toString()
            }

            ratingBar?.rating = 3.5f

            txtNombreProductor?.setText(data.NombreProductor)
            txtDisponibilidad?.setText(String.format("%s: %s %s", data.NombreCalidad, disponibilidad, data.NombreUnidadMedidaCantidad))
            txtFechaDisponibilidad?.setText(data.getFechaLimiteDisponibilidadFormat())

            txtPrecio?.setText(String.format(contextLocal!!.getString(R.string.price_producto),
                    data.Precio, data.PrecioUnidadMedida))

            txtUbicacion?.setText(String.format("%s / %s", data.Ciudad, data.Departamento))
            txtFechaDisponibilidad?.setText(data.getFechaLimiteDisponibilidadFormat())


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

                                    Picasso.get()
                                            .load(user?.Imagen)
                                            .into(imgProductor, object : com.squareup.picasso.Callback {
                                                override fun onError(e: java.lang.Exception?) {
                                                    imgProductor?.setImageResource(R.drawable.ic_account_box_green)
                                                    imgProductor?.setScaleType(ImageView.ScaleType.CENTER_INSIDE);
                                                    // Toast.makeText(context,"Error foto",Toast.LENGTH_LONG).show()
                                                }
                                                override fun onSuccess() {
                                                    // Toast.makeText(context,"Loaded foto",Toast.LENGTH_LONG).show()
                                                }
                                            })


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


    class LoadHolder(itemView: View) : RecyclerView.ViewHolder(itemView)

    fun setMoreDataAvailable(moreDataAvailable: Boolean) {
        isMoreDataAvailable = moreDataAvailable
    }

    /* notifyDataSetChanged is final method so we can't override it
         call adapter.notifyDataChanged(); after update the list
         */
    fun notifyDataChanged() {
        notifyDataSetChanged()
        isLoading = false
    }


    interface OnLoadMoreListener {
        fun onLoadMore()
    }

    fun setLoadMoreListener(loadMoreListeners: OnLoadMoreListener) {
        loadMoreListener = loadMoreListeners
    }


}
