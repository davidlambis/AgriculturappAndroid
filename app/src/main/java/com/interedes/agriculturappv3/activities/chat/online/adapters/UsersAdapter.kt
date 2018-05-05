package com.interedes.agriculturappv3.activities.chat.online.adapters

import android.content.Intent
import android.graphics.drawable.Drawable
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.ValueEventListener
import com.interedes.agriculturappv3.R
import com.interedes.agriculturappv3.activities.chat.online.ChatMessageActivity
import com.interedes.agriculturappv3.libs.EventBus
import com.interedes.agriculturappv3.libs.GreenRobotEventBus
import com.interedes.agriculturappv3.productor.models.chat.UserFirebase
import com.interedes.agriculturappv3.services.resources.Status_Chat
import com.squareup.picasso.Picasso
import de.hdodenhof.circleimageview.CircleImageView


class UsersAdapter(var lista: ArrayList<UserFirebase>) : RecyclerView.Adapter<UsersAdapter.ViewHolder>() {


    companion object {
        lateinit var instance: UsersAdapter
        var mUsersDBRef: DatabaseReference? = null
        var eventBus: EventBus? = null
        /*fun postEventc(type: Int, produccion: Produccion?) {
            var produccionMutable= produccion as Object
            val event = RequestEventProduccion(type,null, produccionMutable,null)
            event.eventType = type
            eventBus?.post(event)
        }*/
    }

    init {
        instance=this
        eventBus = GreenRobotEventBus()

    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        //Llama al método del holder para cargar los items
        holder.bindItems(lista[position], position)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val v = LayoutInflater.from(parent.context).inflate(R.layout.content_list_general, parent, false)
        return ViewHolder(v)
    }

    override fun getItemCount(): Int {
        return lista.size
    }


    fun setItems(newItems: List<UserFirebase>) {
        lista.addAll(newItems)
        notifyDataSetChanged()

    }

    fun clear() {
        lista.clear()
        notifyDataSetChanged()
    }

    fun remove(position: Int) {
        lista.removeAt(position)
        notifyItemRemoved(position)
    }

    fun add(position: Int, person: UserFirebase) {
        lista.add(position, person)
        notifyItemInserted(position)
    }

    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        fun bindItems(data: UserFirebase, pos: Int) = with(itemView) {


            var personNameTxtV: TextView = itemView.findViewById(R.id.txtTitle)
            var personImageImgV: ImageView = itemView.findViewById(R.id.contentIcon)
            personImageImgV.visibility=View.GONE

            var txtUserType: TextView = itemView.findViewById(R.id.txtDescription)

            var txtDescripcionAdditional: TextView = itemView.findViewById(R.id.txtDescripcionAdditional)
            txtDescripcionAdditional.setText(data.Status)

            var optionsContent: LinearLayout = itemView.findViewById(R.id.options)
            optionsContent.visibility=View.GONE

            var contentQuantdate: LinearLayout = itemView.findViewById(R.id.contentQuantdate)
            contentQuantdate.visibility=View.GONE

            var contentIconUser: CircleImageView = itemView.findViewById(R.id.contentIconUser)
            contentIconUser.visibility=View.VISIBLE

            var imgStatus: ImageView = itemView.findViewById(R.id.imgStatus)
            imgStatus.visibility=View.VISIBLE

            if(data.Status.equals(Status_Chat.ONLINE)){
                imgStatus.setImageResource(R.drawable.is_online_user)
            }else{
                imgStatus.setImageResource(R.drawable.is_offline_user)
            }





            personNameTxtV.setText(data.Nombre+" "+data.Apellido)
            txtUserType.setText(data.Rol)
            try {
                Picasso.with(context).load(data.Imagen).placeholder(R.drawable.default_avata).into(contentIconUser)
            } catch (e: Exception) {
                e.printStackTrace()
            }


            //Change State
            mUsersDBRef?.child(data.User_Id)?.addListenerForSingleValueEvent(object : ValueEventListener {
                override fun onCancelled(p0: DatabaseError?) {

                }

                override fun onDataChange(dataSnapshot: DataSnapshot) {
                    if (dataSnapshot.value != null) {
                        //val avataStr = dataSnapshot.value as String
                        var user = dataSnapshot.getValue<UserFirebase>(UserFirebase::class.java)
                        txtDescripcionAdditional.setText(user?.Status)
                        if(user?.Status.equals(Status_Chat.ONLINE)){
                            imgStatus.setImageResource(R.drawable.is_online_user)
                        }else{
                            imgStatus.setImageResource(R.drawable.is_offline_user)
                        }

                        UsersAdapter.instance.notifyDataSetChanged()
                    }
                }
            })

            itemView.setOnClickListener {
                //postEventc(RequestEventProduccion.ITEM_EVENT,data)
                val goToUpdate = Intent(context, ChatMessageActivity::class.java)
                goToUpdate.putExtra("USER_ID", data.User_Id)
                goToUpdate.putExtra("FOTO", data.Imagen)
                context.startActivity(goToUpdate)
            }



/*
            var txtFechasProduccion: TextView = itemView.findViewById(R.id.txtDate)
            var txtCantidadProduccion: TextView = itemView.findViewById(R.id.txtQuantity)
            var txtAdicional: TextView = itemView.findViewById(R.id.txtDescripcionAdditional)
            var icon: ImageView = itemView.findViewById(R.id.contentIcon)

            var btnDelete: ImageButton = itemView.findViewById(R.id.btnAction3)
            var btnAdd: ImageButton = itemView.findViewById(R.id.btnAction1)
            var btnEdit: ImageButton = itemView.findViewById(R.id.btnAction2)



            icon.setImageResource(R.drawable.ic_produccion_cultivo)
            txtAdicional.visibility=View.GONE
            txtCantidadProduccion.visibility=View.GONE
            txtAdicional.visibility=View.GONE
            txt_descripcion.visibility=View.GONE



            btnEdit.setColorFilter(getContext().getResources().getColor(R.color.orange))
            btnAdd.visibility=View.GONE
            btnEdit.setColorFilter(getContext().getResources().getColor(R.color.orange))
            btnDelete.setColorFilter(getContext().getResources().getColor(R.color.red_900))



            val user = mUsersList[position]
            holder.personNameTxtV.setText(user.getDisplayName())

            try {
                Picasso.with(mContext).load(user.getImage()).placeholder(R.mipmap.ic_launcher).into(holder.personImageImgV)
            } catch (e: Exception) {
                e.printStackTrace()
            }

*/


            //image.setImageBitmap(data.Imagen)
            // image.setImageResource(data.Imagen)


          //  txtFechasProduccion.text = String.format(context.getString(R.string.range_dates)!!,data.getFechaInicioFormat(), data.getFechafinFormat())
          //  txt_title.text = String.format(context.getString(R.string.cantidad_estimada)!!,data.ProduccionReal, data.NombreUnidadMedida)



///            txtCantidadProduccion.text = String.format(contextAdapter?.getString(R.string.cantidad_estimada)!!, data.ProduccionReal, data.NombreUnidadMedida)

            //El listener en base a la posición
          /*  itemView.setOnClickListener {
                postEventc(RequestEventProduccion.ITEM_EVENT,data)
            }

            btnEdit.setOnClickListener {
                postEventc(RequestEventProduccion.ITEM_EDIT_EVENT,data)
            }

            btnDelete.setOnClickListener {
                postEventc(RequestEventProduccion.ITEM_DELETE_EVENT,data)
            }*/
        }
    }
}
