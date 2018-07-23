package com.interedes.agriculturappv3.activities.chat.online.conversations_user.adapter

import android.content.Intent
import android.graphics.Typeface
import android.support.v7.widget.RecyclerView
import android.util.TypedValue
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.github.thunder413.datetimeutils.DateTimeUtils
import com.google.firebase.database.*
import com.interedes.agriculturappv3.R
import com.interedes.agriculturappv3.activities.chat.online.messages_chat.ChatMessageActivity
import com.interedes.agriculturappv3.libs.EventBus
import com.interedes.agriculturappv3.libs.GlideApp
import com.interedes.agriculturappv3.libs.GreenRobotEventBus
import com.interedes.agriculturappv3.modules.models.chat.Room
import com.interedes.agriculturappv3.modules.models.chat.RoomConversation
import com.interedes.agriculturappv3.modules.models.chat.UserFirebase
import com.interedes.agriculturappv3.services.api.ApiInterface
import com.interedes.agriculturappv3.services.listas.Listas
import com.interedes.agriculturappv3.services.resources.Chat_Resources
import com.interedes.agriculturappv3.services.resources.S3Resources
import com.interedes.agriculturappv3.services.resources.Status_Chat
import com.squareup.picasso.Picasso
import de.hdodenhof.circleimageview.CircleImageView
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import java.util.*
import java.util.concurrent.TimeUnit


class UsersAdapter(var lista: ArrayList<RoomConversation>) : RecyclerView.Adapter<UsersAdapter.ViewHolder>() {


    companion object {
        lateinit var instance: UsersAdapter
        var mUsersDBRef: DatabaseReference? = null
        var mRoomDBRef: DatabaseReference? = null
        var eventBus: EventBus? = null
        /*fun postEventc(type: Int, produccion: Produccion?) {
            var produccionMutable= produccion as Object
            val event = RequestEventProduccion(type,null, produccionMutable,null)
            event.eventType = type
            eventBus?.post(event)
        }*/
    }

    init {
        instance =this
        eventBus = GreenRobotEventBus()
        mUsersDBRef = Chat_Resources.mUserDBRef
        mRoomDBRef = Chat_Resources.mRoomDBRef
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


    fun setItems(newItems: List<RoomConversation>) {
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

    fun add(position: Int, person: RoomConversation) {
        lista.add(position, person)
        notifyItemInserted(position)
    }

    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        fun bindItems(data: RoomConversation, pos: Int) = with(itemView) {

            val personNameTxtV: TextView = itemView.findViewById(R.id.txtTitle)
            val personImageImgV: ImageView = itemView.findViewById(R.id.contentIcon)
            personImageImgV.visibility=View.GONE

            val txtUserType: TextView = itemView.findViewById(R.id.txtDescription)

            val txtDescripcionAdditional: TextView = itemView.findViewById(R.id.txtDescripcionAdditional)

            val txtLastMessage: TextView = itemView.findViewById(R.id.txtStatisSincronized)

            val optionsContent: LinearLayout = itemView.findViewById(R.id.options)
            optionsContent.visibility=View.GONE

           // var contentQuantdate: LinearLayout = itemView.findViewById(R.id.contentQuantdate)
           // contentQuantdate.visibility=View.GONE
            val txtDate: TextView = itemView.findViewById(R.id.txtDate)

            val contentIconUser: CircleImageView = itemView.findViewById(R.id.contentIconUser)
            contentIconUser.visibility=View.VISIBLE

            val imgStatus: ImageView = itemView.findViewById(R.id.imgStatus)
            imgStatus.visibility=View.VISIBLE

            if(data.UserFirebase?.Status.equals(Status_Chat.ONLINE)){
                imgStatus.setImageResource(R.drawable.is_online_user)
                txtDescripcionAdditional.setText( context.getString(R.string.online))
                txtDate.setText("")
            }else{
                imgStatus.setImageResource(R.drawable.is_offline_user)
                txtDescripcionAdditional.setText( context.getString(R.string.offline))
                if(data.UserFirebase?.Last_Online!=null){
                    txtDate.setText(DateTimeUtils.getTimeAgo(context, Date(data.UserFirebase?.Last_Online!!)))
                }
            }

            if(data.Room!=null){
                txtLastMessage.visibility=View.VISIBLE
                txtLastMessage.setText(data.Room?.LastMessage)
                txtLastMessage.setTypeface(null, Typeface.NORMAL);
                txtLastMessage.setTextSize(TypedValue.COMPLEX_UNIT_SP, 11F);
            }

            personNameTxtV.setText(data.UserFirebase?.Nombre+" "+data.UserFirebase?.Apellido)
            txtUserType.setText(data.UserFirebase?.Rol)


            /*try {
                //Picasso.with(context).load(data.Imagen).placeholder(R.drawable.default_avata).into(contentIconUser)
                    Picasso.get()
                            .load(data.UserFirebase?.Imagen)
                            .fit()
                            .centerCrop()
                            .placeholder(R.drawable.default_avata)
                            .error(R.drawable.default_avata)
                            .into(contentIconUser);


            } catch (e: Exception) {
                e.printStackTrace()
            }*/

            //Change State
            mRoomDBRef?.child(data.UserFirebase?.User_Id)?.child(Chat_Resources.getRoomById(data.Room?.User_From))?.addValueEventListener(object : ValueEventListener {
                override fun onCancelled(p0: DatabaseError?) {
                }
                override fun onDataChange(dataSnapshot: DataSnapshot) {
                    if (dataSnapshot.value != null) {
                        //val avataStr = dataSnapshot.value as String
                        val room = dataSnapshot.getValue<Room>(Room::class.java)
                        try {
                            txtLastMessage.setText(room?.LastMessage)
                        } catch (e: Exception) {
                            e.printStackTrace()
                        }
                        //  UsersAdapter.instance.notifyDataSetChanged()
                    }
                }
            })


            //Change State
           mUsersDBRef?.child(data.UserFirebase?.User_Id)?.addValueEventListener(object : ValueEventListener {
                override fun onCancelled(p0: DatabaseError?) {
                }
                override fun onDataChange(dataSnapshot: DataSnapshot) {
                    if (dataSnapshot.value != null) {
                        //val avataStr = dataSnapshot.value as String
                        val user = dataSnapshot.getValue<UserFirebase>(UserFirebase::class.java)
                        if(user?.Status.equals(Status_Chat.ONLINE)){
                            data.UserFirebase?.Status=Status_Chat.ONLINE
                            imgStatus.setImageResource(R.drawable.is_online_user)
                            txtDescripcionAdditional.setText( context.getString(R.string.online))
                            txtDate.setText("")
                        }else{
                            data.UserFirebase?.Status=Status_Chat.OFFLINE
                            imgStatus.setImageResource(R.drawable.is_offline_user)
                            txtDescripcionAdditional.setText( context.getString(R.string.offline))
                            if(data.UserFirebase?.Last_Online!=null){
                                txtDate.setText(DateTimeUtils.getTimeAgo(context, Date(user?.Last_Online!!)))
                            }
                        }
                      //  UsersAdapter.instance.notifyDataSetChanged()
                    }
                }
            })

              val apiService = ApiInterface.create()
              val queryCustom = Listas.queryGeneral("Email",data.UserFirebase?.Correo!!)
              val callusuario = apiService.getUserByEmail(queryCustom)
              callusuario.delay(100, TimeUnit.MILLISECONDS)?.subscribeOn(Schedulers.io())?.observeOn(AndroidSchedulers.mainThread())?.subscribe({ searchResponse ->
                  //Log.d("search", searchString)
                  val usuario =searchResponse.value
                  if(usuario!=null){
                      for (item in usuario){
                          data.UserFirebase?.Imagen=S3Resources.RootImage+"${item.Fotopefil}"
                          if(item.Fotopefil!=null){

                              GlideApp.with(context)
                                      .load(S3Resources.RootImage+"${item.Fotopefil}")
                                      .diskCacheStrategy(DiskCacheStrategy.ALL)
                                      .productorPhoto()
                                      .into(contentIconUser);
                          }else{
                              contentIconUser.setImageResource(R.drawable.ic_account_box_green)
                              contentIconUser.scaleType= ImageView.ScaleType.CENTER_INSIDE
                          }

                      }
                  }
              },{ throwable ->
                  val error= throwable.toString()

              })

            itemView.setOnClickListener {
                //postEventc(RequestEventProduccion.ITEM_EVENT,data)
                val goToUpdate = Intent(context, ChatMessageActivity::class.java)
                goToUpdate.putExtra("USER_FIREBASE", data.UserFirebase)
                goToUpdate.putExtra("ROOM", data.Room)
                goToUpdate.putExtra("USER_ID", data.UserFirebase?.User_Id)
                goToUpdate.putExtra("FOTO", data.UserFirebase?.Imagen)
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
