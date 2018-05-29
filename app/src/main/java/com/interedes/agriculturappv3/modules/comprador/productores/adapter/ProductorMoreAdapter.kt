package com.interedes.agriculturappv3.modules.comprador.productores.adapter

import android.app.Activity
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
import com.interedes.agriculturappv3.modules.models.chat.UserFirebase
import com.interedes.agriculturappv3.modules.models.producto.Producto
import com.squareup.picasso.Picasso

/**
 * Created by EnuarMunoz on 28/05/18.
 */
class ProductorMoreAdapter(recyclerView:RecyclerView, val products:ArrayList<Producto>? , activi: Activity): RecyclerView.Adapter<RecyclerView.ViewHolder>() {


    companion object {
        var mUsersDBRef: DatabaseReference? = null
        private val VIEW_TYPE_ITEM = 0
        private val VIEW_TYPE_LOADING = 1
        private var onLoadMoreListener: OnLoadMoreListener? = null
        private var isLoading: Boolean = false
        private var activity: Activity? = null
        private val visibleThreshold = 5
        private var lastVisibleItem: Int = 0
        private var totalItemCount:Int = 0
    }


    init {
        mUsersDBRef= FirebaseDatabase.getInstance().reference
        activity = activi

        val linearLayoutManager = recyclerView.layoutManager as LinearLayoutManager
        recyclerView.addOnScrollListener(object : RecyclerView.OnScrollListener() {
            override fun onScrolled(recyclerView: RecyclerView?, dx: Int, dy: Int) {
                super.onScrolled(recyclerView, dx, dy)
                totalItemCount = linearLayoutManager.itemCount
                lastVisibleItem = linearLayoutManager.findLastVisibleItemPosition()
                if (!isLoading && totalItemCount <= lastVisibleItem + visibleThreshold) {
                    if (onLoadMoreListener != null) {
                        onLoadMoreListener!!.onLoadMore()
                    }
                    isLoading = true
                }
            }
        })
    }


    fun setOnLoadMoreListener(mOnLoadMoreListener: OnLoadMoreListener) {
        onLoadMoreListener = mOnLoadMoreListener
    }


    override fun getItemViewType(position: Int): Int {
        return if (products?.get(position) == null) VIEW_TYPE_LOADING else VIEW_TYPE_ITEM
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {


        var viewHolder: View? = null
        val inflater = LayoutInflater.from(parent.context)

        when (viewType) {
            VIEW_TYPE_ITEM -> viewHolder = getViewHolder(parent, inflater)
            VIEW_TYPE_LOADING -> {
                viewHolder= inflater.inflate(R.layout.item_progress, parent, false)
            }
        }

        return UserViewHolder(viewHolder)

    }

    private fun getViewHolder(parent: ViewGroup, inflater: LayoutInflater): View? {
        val v1 = inflater.inflate(R.layout.content_list_producto_productor, parent, false)
        return v1
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {




        if (holder is UserViewHolder) {
            val data = products?.get(position)
            holder.txtProducto?.setText(data?.Nombre)

            var disponibilidad=""

            if(data?.Stock.toString().contains(".0")){
                disponibilidad= String.format(activity!!.getString(R.string.price_empty_signe),
                        data?.Stock)
            }else{
                disponibilidad=data?.Stock.toString()
            }

            holder.ratingBar?.rating = 3.5f

            holder.txtNombreProductor?.setText(data?.NombreProductor)
            holder.txtDisponibilidad?.setText(String.format("%s: %s %s", data?.NombreCalidad,disponibilidad,data?.NombreUnidadMedidaCantidad))
            holder.txtFechaDisponibilidad?.setText(data?.getFechaLimiteDisponibilidadFormat())

            holder.txtPrecio?.setText(String.format(activity!!.getString(R.string.price_producto),
                    data?.Precio,data?.PrecioUnidadMedida))

            holder.txtUbicacion?.setText(String.format("%s / %s", data?.Ciudad, data?.Departamento))
            holder.txtFechaDisponibilidad?.setText(data?.getFechaLimiteDisponibilidadFormat())


            val query = mUsersDBRef?.child("Users")?.orderByChild("correo")?.equalTo(data?.EmailProductor)
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
                                    Picasso.with(activity).load(user?.Imagen).placeholder(R.drawable.default_avata).into(holder.imgProductor)
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
        } else if (holder is LoadingViewHolder) {
            holder.progressBar.isIndeterminate = true
        }
    }

    override fun getItemCount(): Int {
        return if (products == null) 0 else products.size
    }

    fun setLoaded() {
        isLoading = false
    }

    private inner class LoadingViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        var progressBar: ProgressBar

        init {
            progressBar=view.findViewById(R.id.progressBar1)
        }
    }

    private inner class UserViewHolder(view: View?) : RecyclerView.ViewHolder(view) {
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
    }


}
