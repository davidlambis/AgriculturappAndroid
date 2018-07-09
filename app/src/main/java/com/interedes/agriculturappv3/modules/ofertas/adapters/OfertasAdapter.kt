package com.interedes.agriculturappv3.modules.ofertas.adapters

import android.graphics.BitmapFactory
import android.net.Uri
import android.support.v4.content.ContextCompat
import android.support.v7.widget.RecyclerView
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import com.google.firebase.database.*
import com.interedes.agriculturappv3.R
import com.interedes.agriculturappv3.libs.EventBus
import com.interedes.agriculturappv3.libs.GreenRobotEventBus
import com.interedes.agriculturappv3.modules.models.chat.UserFirebase
import com.interedes.agriculturappv3.modules.models.ofertas.DetalleOferta
import com.interedes.agriculturappv3.modules.models.ofertas.DetalleOferta_Table
import com.interedes.agriculturappv3.modules.models.ofertas.Oferta
import com.interedes.agriculturappv3.modules.models.producto.Producto
import com.interedes.agriculturappv3.modules.models.producto.Producto_Table
import com.interedes.agriculturappv3.modules.ofertas.events.OfertasEvent
import com.interedes.agriculturappv3.services.resources.EstadosOfertasResources
import com.interedes.agriculturappv3.services.resources.RolResources
import com.interedes.agriculturappv3.services.resources.S3Resources
import com.raizlabs.android.dbflow.sql.language.SQLite
import com.squareup.picasso.Picasso
import de.hdodenhof.circleimageview.CircleImageView
import kotlinx.android.synthetic.main.activity_chat_message.*
import java.util.ArrayList
import com.interedes.agriculturappv3.R.id.imageView
import com.squareup.picasso.Callback
import com.interedes.agriculturappv3.R.id.imageView




class OfertasAdapter(var lista: ArrayList<Oferta>,rolNameUserLogued:String?) : RecyclerView.Adapter<OfertasAdapter.ViewHolder>() {

    companion object {
        var rolUserLogied:String?= ""
        var mUsersDBRef: DatabaseReference? = null
        var eventBus: EventBus? = null
        fun postEvent(type: Int, oferta: Oferta?) {
            val ofertaMutable = oferta as Object
            val event = OfertasEvent(type, null, ofertaMutable, null,null)
            event.eventType = type
            eventBus?.post(event)
        }
    }

    init {
        rolUserLogied= rolNameUserLogued
        mUsersDBRef = FirebaseDatabase.getInstance().reference
        eventBus = GreenRobotEventBus()
    }

    override fun onBindViewHolder(holder: OfertasAdapter.ViewHolder, position: Int) {
        //Llama al método del holder para cargar los items
        holder.bindItems(lista[position], position)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): OfertasAdapter.ViewHolder {
        val v = LayoutInflater.from(parent.context).inflate(R.layout.content_list_ofertas, parent, false)
        return OfertasAdapter.ViewHolder(v)
    }

    override fun getItemCount(): Int {
        return lista.size
    }


    fun setItems(newItems: List<Oferta>) {
        lista.addAll(newItems)
        notifyDataSetChanged()
    }

    fun clear() {
        lista.clear()
        notifyDataSetChanged()
    }

    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        fun bindItems(data: Oferta, pos: Int) = with(itemView) {
            val contentIcon: ImageView = itemView.findViewById(R.id.contentIcon)
            val txtTitle: TextView = itemView.findViewById(R.id.txtTitle)
            val txtDescription: TextView = itemView.findViewById(R.id.txtDescription)

            val txtCantidad: TextView = itemView.findViewById(R.id.txtQuantity)

            val txtDate: TextView = itemView.findViewById(R.id.txtDate)
            val publisher_name: TextView = itemView.findViewById(R.id.publisher_name)

            val btnAction1: Button = itemView.findViewById(R.id.btnButtomAction1)
            val btnAction3: Button = itemView.findViewById(R.id.btnButtomAction3)

            val optionsButtons: LinearLayout = itemView.findViewById(R.id.optionsButtons)

            val cardOferta:LinearLayout=itemView.findViewById(R.id.cardOferta)

            val circleView: CircleImageView =itemView.findViewById(R.id.circleView)
            val btnStatusOferta: Button =itemView.findViewById(R.id.btnStatusOferta)

            val btnChatItem: LinearLayout =itemView.findViewById(R.id.btnChatItem)



            /*
            val options: LinearLayout =itemView.findViewById(R.id.options)
            options.visibility=View.GONE

            val optionsButtons: LinearLayout =itemView.findViewById(R.id.optionsButtons)
            optionsButtons.visibility=View.VISIBLE
            */

            var disponibilidad = ""
            var precioOferta = ""
            var productoCantidad=""
            var calidad=""

            if(data.DetalleOfertaSingle!=null){
                if ( data.DetalleOfertaSingle?.Cantidad.toString().contains(".0")) {
                    disponibilidad = String.format(context?.getString(R.string.price_empty_signe)!!,
                            data.DetalleOfertaSingle?.Cantidad)
                } else {
                    disponibilidad = data.DetalleOfertaSingle?.Cantidad.toString()
                }

                precioOferta=String.format(context.getString(R.string.price_producto),
                        data.DetalleOfertaSingle?.Valor_Oferta, data.DetalleOfertaSingle?.NombreUnidadMedidaPrecio)
                /*var disponibilidad = ""
                if ( data.DetalleOfertaSingle?.Cantidad.toString().contains(".0")) {
                    disponibilidad = String.format(context?.getString(R.string.price_empty_signe)!!,
                            data.DetalleOfertaSingle?.Cantidad)
                } else {
                    disponibilidad = data.DetalleOfertaSingle?.Cantidad.toString()
                }
                txtCantidad.text =disponibilidad+" ${data.Producto?.NombreUnidadMedidaCantidad}"
                */
            }


            if(data.Producto!=null){

               /* if( data.Producto?.blobImagen!=null){
                    val byte = data.Producto?.blobImagen?.getBlob()
                    val bitmap = BitmapFactory.decodeByteArray(byte, 0, byte!!.size)
                    contentIcon.setImageBitmap(bitmap)
                }*/

                if( data.Producto?.blobImagen!=null){
                    val byte = data.Producto?.blobImagen?.getBlob()
                    val bitmap = BitmapFactory.decodeByteArray(byte, 0, byte!!.size)
                    contentIcon.setImageBitmap(bitmap)
                }else{
                    if(data.Producto?.Imagen!=null){
                        if(data.Producto?.Imagen!!.contains("Productos")){
                           /* try {
                                Picasso.with(context).load(S3Resources.RootImage+"${data.Producto?.Imagen}").placeholder(R.drawable.ic_foto_producto).into(contentIcon)
                                contentIcon.setScaleType(ImageView.ScaleType.CENTER_CROP);
                            } catch (e: Exception) {
                                e.printStackTrace()
                            }*/
                            Picasso.get()
                                    .load(S3Resources.RootImage+"${data.Producto?.Imagen}")
                                    .fit()
                                    .into(contentIcon, object : com.squareup.picasso.Callback {
                                        override fun onError(e: java.lang.Exception?) {

                                            contentIcon.setImageResource(R.drawable.ic_foto_producto)
                                            contentIcon.setScaleType(ImageView.ScaleType.CENTER_INSIDE);
                                            //) Toast.makeText(context,"Error foto",Toast.LENGTH_LONG).show()
                                        }

                                        override fun onSuccess() {
                                           // Toast.makeText(context,"Loaded foto",Toast.LENGTH_LONG).show()
                                        }
                                    })

                           /* val builder = Picasso.Builder(context)
                            builder.listener(object : Picasso.Listener {
                                override fun onImageLoadFailed(picasso: Picasso, uri: Uri, exception: Exception) {

                                    Toast.makeText(context,"Error foto",Toast.LENGTH_LONG).show()
                                    exception.printStackTrace()
                                }
                            })
                            builder.build().load(S3Resources.RootImage+"${data.Producto?.Imagen}").into(contentIcon)
                            */

                        }
                    }
                }

                txtTitle.setText(data.Producto?.Nombre)
                productoCantidad= String.format("%s %s ", disponibilidad, data.Producto?.NombreUnidadMedidaCantidad)
                calidad= String.format("%s",data.Producto?.NombreCalidad)
                // contentIcon.setImageResource(R.drawable.ic_ofertas)
            }else{

            }

            if(data.Usuario!=null){

                //TODO se valida que el usuario sea productor para mostrar opciones de editar la oferta
                publisher_name.text=data.Usuario?.Nombre+" ${data.Usuario?.Apellidos}"
                val query = mUsersDBRef?.child("Users")?.orderByChild("correo")?.equalTo(data.Usuario?.Email)
                query?.addListenerForSingleValueEvent(object : ValueEventListener {
                    override fun onDataChange(dataSnapshot: DataSnapshot) {
                        if (dataSnapshot.exists()) {
                            // dataSnapshot is the "issue" node with all children with id 0
                            for (issue in dataSnapshot.children) {
                                // do something with the individual "issues"
                                var user = issue.getValue<UserFirebase>(UserFirebase::class.java)
                                //if not current user, as we do not want to show ourselves then chat with ourselves lol

                                    try {
                                        //Picasso.with(context).load(user?.Imagen).placeholder(R.drawable.ic_account_box_green).into(circleView)

                                       /* val builder = Picasso.Builder(context)
                                        builder.listener(object : Picasso.Listener {
                                            override fun onImageLoadFailed(picasso: Picasso, uri: Uri, exception: Exception) {
                                                contentIcon.setImageResource(R.drawable.ic_foto_producto)
                                                contentIcon.setScaleType(ImageView.ScaleType.CENTER_INSIDE);
                                            }
                                        })
                                        builder.build().load(user?.Imagen).into(circleView)*/
                                        Picasso.get()
                                                .load(user?.Imagen)
                                                .fit()
                                                .into(circleView, object : com.squareup.picasso.Callback {
                                                    override fun onError(e: java.lang.Exception?) {
                                                        circleView.setImageResource(R.drawable.ic_account_box_green)
                                                        circleView.setScaleType(ImageView.ScaleType.CENTER_INSIDE);
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

                /*
                try {
                    val foto = data.Usuario?.blobImagenUser?.blob
                    var imageBitmapAccountGlobal = BitmapFactory.decodeByteArray(foto, 0, foto!!.size)
                    circleView.setImageBitmap(imageBitmapAccountGlobal)
                }catch (ex:Exception){
                    var ss= ex.toString()
                    Log.d("Convert Image", "defaultValue = " + ss);
                }
                */

               // if(data.Usuario?.Fotopefil!=null){
               /* try {
                    Picasso.with(context).load("https://s3.amazonaws.com/datatakefiles/ic_asistencia_tecnica_color_500.png").placeholder(R.drawable.default_avata).into(circleView)
                } catch (e: Exception) {
                    e.printStackTrace()
                }*/
               // }



                //txtDescription.text= "${data.Usuario?.Nombre} ${data.Usuario?.Apellidos}  te oferto $productoCantidad a un precio de  $precioOferta por el cultivo de ${data.Producto?.Nombre}   de $calidad"

                if(rolUserLogied.equals(RolResources.COMPRADOR)){
                    optionsButtons.visibility=View.GONE
                    txtDescription.text=String.format(context.getString(R.string.descripcion_oferta_comprador)
                            ,data.Usuario?.Nombre,data.Usuario?.Apellidos,productoCantidad,precioOferta,data.Producto?.Nombre,calidad
                    )
                }else{
                    optionsButtons.visibility=View.VISIBLE
                    txtDescription.text=String.format(context.getString(R.string.descripcion_oferta_productor)
                            ,data.Usuario?.Nombre,data.Usuario?.Apellidos,productoCantidad,precioOferta,data.Producto?.Nombre,calidad
                    )
                }


            }

            if(data.Nombre_Estado_Oferta.equals(EstadosOfertasResources.RECHAZADO_STRING)){
                cardOferta.background = ContextCompat.getDrawable(context, R.drawable.custom_drawable_card_view_red)
                btnStatusOferta.background=ContextCompat.getDrawable(context, R.drawable.custom_drawable_card_view_red)
                btnStatusOferta.setText(context.getString(R.string.title_oferta_refused))

                if(rolUserLogied.equals(RolResources.PRODUCTOR)){
                    optionsButtons.visibility=View.GONE
                    btnStatusOferta.visibility=View.VISIBLE
                }else{
                    optionsButtons.visibility=View.GONE
                    btnStatusOferta.visibility=View.VISIBLE
                }

            }else if(data.Nombre_Estado_Oferta.equals(EstadosOfertasResources.CONFIRMADO_STRING)){
                cardOferta.background = ContextCompat.getDrawable(context, R.drawable.custom_drawable_card_view_green)
                btnStatusOferta.background=ContextCompat.getDrawable(context, R.drawable.custom_drawable_card_view_green)
                btnStatusOferta.setText(context.getString(R.string.title_oferta_confirm))

                if(rolUserLogied.equals(RolResources.PRODUCTOR)){
                    optionsButtons.visibility=View.GONE
                    btnStatusOferta.visibility=View.VISIBLE
                }else{
                    optionsButtons.visibility=View.GONE
                    btnStatusOferta.visibility=View.VISIBLE
                }

            }else{
                cardOferta.background = ContextCompat.getDrawable(context, R.drawable.custom_drawable_card_view_orange)
                btnStatusOferta.background=ContextCompat.getDrawable(context, R.drawable.custom_drawable_card_view_orange)
                btnStatusOferta.setText(context.getString(R.string.title_oferta_vigente))

                if(rolUserLogied.equals(RolResources.PRODUCTOR)){
                    optionsButtons.visibility=View.VISIBLE
                    btnStatusOferta.visibility=View.GONE
                }else{
                    optionsButtons.visibility=View.GONE
                    btnStatusOferta.visibility=View.VISIBLE
                }
            }


            txtDate.text =data.getCreatedOnFormat()

            btnAction1.setOnClickListener {
                postEvent(OfertasEvent.REQUEST_REFUSED_ITEM_EVENT, data)
            }

            btnAction1.setOnClickListener {
                postEvent(OfertasEvent.REQUEST_REFUSED_ITEM_EVENT, data)
            }

            btnAction3.setOnClickListener {
                postEvent(OfertasEvent.REQUEST_CONFIRM_ITEM_EVENT, data)
            }

            btnChatItem.setOnClickListener{
                postEvent(OfertasEvent.REQUEST_CHAT_ITEM_EVENT, data)
            }
        }
    }
}