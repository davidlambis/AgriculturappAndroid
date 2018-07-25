package com.interedes.agriculturappv3.modules.comprador.detail_producto

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*
import com.interedes.agriculturappv3.libs.EventBus
import com.interedes.agriculturappv3.libs.GreenRobotEventBus
import com.interedes.agriculturappv3.modules.comprador.detail_producto.events.RequestEventDetalleProducto
import com.interedes.agriculturappv3.modules.models.chat.ChatMessage
import com.interedes.agriculturappv3.modules.models.chat.UserFirebase
import com.interedes.agriculturappv3.modules.models.ofertas.*
import com.interedes.agriculturappv3.modules.models.producto.Producto
import com.interedes.agriculturappv3.modules.models.producto.Producto_Table
import com.interedes.agriculturappv3.modules.models.tipoproducto.TipoProducto
import com.interedes.agriculturappv3.modules.models.tipoproducto.TipoProducto_Table
import com.interedes.agriculturappv3.modules.models.unidad_medida.Unidad_Medida
import com.interedes.agriculturappv3.modules.models.unidad_medida.Unidad_Medida_Table
import com.interedes.agriculturappv3.modules.models.usuario.Usuario
import com.interedes.agriculturappv3.modules.models.usuario.Usuario_Table
import com.interedes.agriculturappv3.services.api.ApiInterface
import com.raizlabs.android.dbflow.kotlinextensions.save
import com.raizlabs.android.dbflow.sql.language.SQLite
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.math.BigDecimal
import java.math.MathContext
import java.text.SimpleDateFormat
import java.util.*
import com.google.android.gms.tasks.OnCompleteListener
import com.interedes.agriculturappv3.modules.models.Notification.FcmNotificationBuilder
import com.interedes.agriculturappv3.modules.models.chat.Room
import com.interedes.agriculturappv3.services.resources.*


class DetailProductoRepository :IMainViewDetailProducto.Repository {

    var eventBus: EventBus? = null
    var apiService: ApiInterface? = null
    var apiServiceFcm: ApiInterface? = null
    var mUserDBRef: DatabaseReference? = null
    var mMessagesDBRef: DatabaseReference? = null
    var mRoomDBRef: DatabaseReference? = null
    private var mProductorReceiverId: String? = null
    private var mCompradorSenderId: String? = null
    private var roomId: String? = null

    init {
        eventBus = GreenRobotEventBus()
        apiService = ApiInterface.create()
        apiServiceFcm= ApiInterface.getClienNotifcation()
        mUserDBRef = Chat_Resources.mUserDBRef
        mMessagesDBRef = Chat_Resources.mMessagesDBRef
        mRoomDBRef = Chat_Resources.mRoomDBRef
    }

    //region Métodos Interfaz
    override fun getLastUserLogued(): Usuario? {
        val usuarioLogued = SQLite.select().from(Usuario::class.java).where(Usuario_Table.UsuarioRemembered.eq(true)).querySingle()
        return usuarioLogued
    }

    override fun getListas() {
        val listUnidadMedidaPrecios = SQLite.select().from(Unidad_Medida::class.java).where(Unidad_Medida_Table.CategoriaMedidaId.eq(CategoriaMediaResources.Moneda)).queryList()
        postEventListUnidadMedida(RequestEventDetalleProducto.LIST_EVENT_UNIDAD_MEDIDA_PRICE, listUnidadMedidaPrecios, null)
    }

    override fun getProducto(producto_id: Long): Producto? {
        val producto= SQLite.select().from(Producto::class.java).where(Producto_Table.ProductoId.eq(producto_id)).querySingle()
        return producto
    }

    override fun getTipoProducto(tipo_producto_id: Long): TipoProducto? {
        val tipoProducto= SQLite.select().from(TipoProducto::class.java).where(TipoProducto_Table.Id.eq(tipo_producto_id)).querySingle()
        return tipoProducto
    }

    override fun verificateCantProducto(producto_id:Long?,cantidad:Double?):Boolean? {
        var response= false
        val producto= SQLite.select().from(Producto::class.java).where(Producto_Table.ProductoId.eq(producto_id)).querySingle()
        if(cantidad!!<=producto?.Stock!!){
            response=true
        }
        return response
    }


    //OFERTA
    override fun postOferta(oferta: Oferta, checkConection:Boolean){

        oferta.UsuarioId=getLastUserLogued()?.Id
        //TODO si existe conexion a internet
        if(checkConection){

            val postOferta = PostOferta(
                    0,
                    oferta.CreatedOn,
                    EstadosOfertasResources.VIGENTE,
                    oferta.UpdatedOn,
                    oferta.UsuarioId,
                    oferta.UsuarioTo
            )
            val call = apiService?.postOfertaComprador(postOferta)
            call?.enqueue(object : Callback<PostOferta> {
                override fun onResponse(call: Call<PostOferta>?, response: Response<PostOferta>?) {
                    if (response != null && response.code() == 201 || response?.code() == 200) {

                        oferta.Id_Remote = response.body()?.Id
                        val varloTotalOfertaBig = BigDecimal(oferta.Valor_Oferta!!, MathContext.DECIMAL64)
                        val postDetalleOferta = PostDetalleOferta(
                                0,
                                oferta.CalidadId,
                                oferta.Cantidad,
                                oferta.Id_Remote,
                                oferta.ProductoId,
                                oferta.UnidadMedidaId,
                                varloTotalOfertaBig,
                                varloTotalOfertaBig,
                                varloTotalOfertaBig
                        )
                        val call = apiService?.postDetalleOfertaComprador(postDetalleOferta)
                        call?.enqueue(object : Callback<PostDetalleOferta> {
                            override fun onResponse(call: Call<PostDetalleOferta>?, response: Response<PostDetalleOferta>?) {
                                if (response != null && response.code() == 201 || response?.code() == 200) {
                                    val response = response.body()


                                    val detalleOferta=DetalleOferta()
                                    detalleOferta.Id_Remote=response?.Id
                                    detalleOferta.OfertasId=oferta?.Id_Remote
                                    detalleOferta.Cantidad= oferta.Cantidad
                                    detalleOferta.CalidadId=oferta.CalidadId
                                    detalleOferta.ProductoId=oferta.ProductoId
                                    detalleOferta.UnidadMedidaId=oferta.UnidadMedidaId
                                    detalleOferta.Valor_Oferta=oferta.Valor_Oferta
                                    detalleOferta.Valor_minimo=oferta.Valor_Oferta
                                    detalleOferta.Valor_transaccion=oferta.Valor_Oferta
                                    detalleOferta.NombreUnidadMedidaPrecio=oferta.NombreUnidadMedidaPrecio

                                    oferta.Nombre_Estado_Oferta=EstadosOfertasResources.VIGENTE_STRING


                                    val usuarioTo= SQLite.select().from(Usuario::class.java).where(Usuario_Table.Id.eq(oferta.UsuarioTo)).querySingle()

                                    sendMessageNotificationUser(usuarioTo,oferta)
                                    saveOfertaLocal(oferta,detalleOferta)

                                } else {
                                    postEventError(RequestEventDetalleProducto.ERROR_EVENT, "Comprueba tu conexión")
                                }
                            }
                            override fun onFailure(call: Call<PostDetalleOferta>?, t: Throwable?) {
                                postEventError(RequestEventDetalleProducto.ERROR_EVENT, "Comprueba tu conexión")
                            }
                        })

                    } else {
                        postEventError(RequestEventDetalleProducto.ERROR_EVENT, "Comprueba tu conexión")
                    }
                }
                override fun onFailure(call: Call<PostOferta>?, t: Throwable?) {
                    postEventError(RequestEventDetalleProducto.ERROR_EVENT, "Comprueba tu conexión")
                }
            })
        }else{
            postEvent(RequestEventDetalleProducto.ERROR_VERIFICATE_CONECTION,null,null,null)
        }
    }

    private fun sendMessageNotificationUser(usuarioTo: Usuario?,oferta:Oferta) {
        if(usuarioTo!=null){
            val query = mUserDBRef?.orderByChild("correo")?.equalTo(usuarioTo?.Email)
            query?.addListenerForSingleValueEvent(object : ValueEventListener {
                override fun onDataChange(dataSnapshot: DataSnapshot) {
                    if (dataSnapshot.exists()) {
                        // dataSnapshot is the "issue" node with all children with id 0
                        var user:UserFirebase?=null
                        for (issue in dataSnapshot.children) {
                            // do something with the individual "issues"
                            user = issue.getValue<UserFirebase>(UserFirebase::class.java)
                            mProductorReceiverId=user?.User_Id
                            mCompradorSenderId=FirebaseAuth.getInstance().currentUser!!.uid
                            //if not current user, as we do not want to show ourselves then chat with ourselves lol
                        }

                        findRoomUser(user!!,oferta)
                        //sendMessageToFirebase(message, senderId, mReceiverId)
                    }
                }
                override fun onCancelled(databaseError: DatabaseError) {
                    val error = databaseError.message
                    postEventError(RequestEventDetalleProducto.ERROR_EVENT, error)
                    //postEvent(RequestEventDetalleProducto.OK_SEND_EVENT_OFERTA, null, null,null)
                }
            })
        }
    }

    private fun findRoomUser(userFirebase: UserFirebase,oferta:Oferta) {
        //val query = mRoomDBRef?.child("/"+mCompradorSenderId+"/"+mProductorReceiverId)
        val query = mRoomDBRef?.child(mCompradorSenderId)?.orderByChild("user_To")?.equalTo(Chat_Resources.getRoomById(mProductorReceiverId))
        query?.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                if (dataSnapshot.exists()) {
                    //var room = dataSnapshot.value as HashMap<*, *>
                    //roomId=room.get("idRoom") as String;
                    var roomFind=Room()

                    for (issue in dataSnapshot.children) {
                        val room = issue.getValue<Room>(Room::class.java)
                        roomId=room?.IdRoom
                        roomFind= room!!
                    }
                    val message=getMessageOferta(oferta)
                    sendMessageToFirebase(message,oferta,userFirebase)
                }else{
                    createRoomUser(userFirebase,mProductorReceiverId,true,oferta,userFirebase)
                }
            }
            override fun onCancelled(databaseError: DatabaseError) {
                val error = databaseError.message
                postEventError(RequestEventDetalleProducto.ERROR_EVENT, error)
            }
        })
    }

    fun getDateFormatApi(date: Date): String? {
        val format1 = SimpleDateFormat("yyyy-MM-dd")
        return format1.format(date)
    }

    private fun createRoomUser(user: UserFirebase?,idProductor:String?,roomComprador:Boolean,oferta:Oferta,userFirebase:UserFirebase) {
        val date= Calendar.getInstance().time
        val dateString =getDateFormatApi(date)

         if(idProductor!= null){
            if(roomComprador){
                val room = Room(Chat_Resources.getRoomByCompradorProductor(mCompradorSenderId,mProductorReceiverId), mCompradorSenderId, mProductorReceiverId, 0, 0,dateString,"")
                mRoomDBRef?.child(mCompradorSenderId)?.child(Chat_Resources.getRoomById(mProductorReceiverId))?.setValue(room)?.addOnCompleteListener(OnCompleteListener<Void> { task ->
                    if (!task.isSuccessful) {
                        val error = task.exception
                        postEventError(RequestEventDetalleProducto.ERROR_EVENT, error.toString())
                    } else {
                        createRoomUser(user,mProductorReceiverId,false,oferta,userFirebase)
                    }
                })
            }else{
                val room = Room(Chat_Resources.getRoomByCompradorProductor(mCompradorSenderId,mProductorReceiverId), mProductorReceiverId, mCompradorSenderId, 0, 0,dateString,"")
                mRoomDBRef?.child(mProductorReceiverId)?.child(Chat_Resources.getRoomById(mCompradorSenderId))?.setValue(room)?.addOnCompleteListener(OnCompleteListener<Void> { task ->
                    if (!task.isSuccessful) {
                        val error = task.exception
                        postEventError(RequestEventDetalleProducto.ERROR_EVENT, error.toString())
                        //error
                    } else {
                        createRoomUser(user,null,false,oferta,userFirebase)
                    }
                })
            }
        }else{
            updateDatesRoomUser()


            val roomDateComprador= mRoomDBRef?.child(mCompradorSenderId)?.child(Chat_Resources.getRoomById(mProductorReceiverId)+"/date")
            roomDateComprador?.setValue(ServerValue.TIMESTAMP);

            val roomDateProductor= mRoomDBRef?.child(mProductorReceiverId)?.child(Chat_Resources.getRoomById(mCompradorSenderId)+"/date")
            roomDateProductor?.setValue(ServerValue.TIMESTAMP);


            roomId=Chat_Resources.getRoomByCompradorProductor(mCompradorSenderId,mProductorReceiverId)



             val message=getMessageOferta(oferta)


            sendMessageToFirebase(message,oferta,userFirebase)
        }
    }

    private fun getMessageOferta(oferta: Oferta): String {

        var message= ""
        var disponibilidad = ""
        var precioOferta = ""
        var productoCantidad=""
        var calidad=""

        val producto =SQLite.select().from(Producto::class.java).where(Producto_Table.Id_Remote.eq(oferta.ProductoId)).querySingle()

        if (oferta?.Cantidad.toString().contains(".0")) {
            disponibilidad = String.format("%.0f",
                    oferta?.Cantidad)
        } else {
            disponibilidad = oferta?.Cantidad.toString()
        }

        productoCantidad= String.format("%s %s ", disponibilidad, producto?.NombreUnidadMedidaCantidad)
        calidad= String.format("%s",producto?.NombreCalidad)


        precioOferta=String.format("$ %,.0f %2s",
                oferta?.Valor_Oferta, oferta?.NombreUnidadMedidaPrecio)

        //val usuarioTo= SQLite.select().from(Usuario::class.java).where(Usuario_Table.Id.eq(oferta.UsuarioTo)).querySingle()
        val userLogued= getLastUserLogued()
         message=String.format("%s %s oferto %s a un precio de %s por el cultivo de %s de %s"
                ,userLogued?.Nombre,userLogued?.Apellidos,productoCantidad,precioOferta,producto?.Nombre,calidad)
        return  message
    }

    private fun updateDatesRoomUser() {
        //success adding user to db as well
        val roomDateLastComprador= mRoomDBRef?.child(mCompradorSenderId)?.child(Chat_Resources.getRoomById(mProductorReceiverId)+"/date_Last")
        roomDateLastComprador?.setValue(ServerValue.TIMESTAMP);

        val roomDateLastProductor= mRoomDBRef?.child(mProductorReceiverId)?.child(Chat_Resources.getRoomById(mCompradorSenderId)+"/date_Last")
        roomDateLastProductor?.setValue(ServerValue.TIMESTAMP);
    }


    private fun updateLastMessageRoomUser(message:String?) {
        //success adding user to db as well
        val lastMessageRoomComprador= mRoomDBRef?.child(mCompradorSenderId)?.child(Chat_Resources.getRoomById(mProductorReceiverId)+"/lastMessage")
        lastMessageRoomComprador?.setValue(message)

        val lastMessageRoomProductor= mRoomDBRef?.child(mProductorReceiverId)?.child(Chat_Resources.getRoomById(mCompradorSenderId)+"/lastMessage")
        lastMessageRoomProductor?.setValue(message)
    }

    private fun sendMessageToFirebase(message: String,oferta:Oferta,userFirebase: UserFirebase) {
        val time= System.currentTimeMillis()
        val dateFormat = SimpleDateFormat("MM/dd/yyyy")
        val date = Date()
        val fecha = dateFormat.format(date)
        //Obtener Hora
        val cal = Calendar.getInstance()
        val timeFormat = SimpleDateFormat("HH:mm")
        val hora = timeFormat.format(cal.time)
        val newMsg = ChatMessage(roomId,message, mCompradorSenderId, mProductorReceiverId,fecha,hora,time)
        mMessagesDBRef?.push()?.setValue(newMsg)?.addOnCompleteListener { task ->
            if (!task.isSuccessful) {
                val error = task.exception
                postEventError(RequestEventDetalleProducto.ERROR_EVENT, error.toString())
                //error
                //Toast.makeText(applicationContext, "Error " + task.exception!!.localizedMessage, Toast.LENGTH_SHORT).show()
            } else {
                updateDatesRoomUser()
                updateLastMessageRoomUser(newMsg.message)
                val producto =SQLite.select().from(Producto::class.java).where(Producto_Table.Id_Remote.eq(oferta.ProductoId)).querySingle()

                if(userFirebase.StatusTokenFcm!=null){
                    if(userFirebase.StatusTokenFcm==true){
                        var imagen=""
                        if(producto?.Imagen!!.contains("Productos")){
                            imagen=S3Resources.RootImage+"${producto.Imagen}"
                        }else{
                            imagen="https://s3.amazonaws.com/agriculturapp/NotificationLocal/notification_products.jpg"
                        }
                        //sendNotifcationOferta(message,userFirebase.TokenFcm!!,imagen)
                        sendPushNotificationToReceiver(message,userFirebase,imagen)
                    }
                }

                if(userFirebase?.Status.equals(Status_Chat.ONLINE)){
                    postEvent(RequestEventDetalleProducto.OK_SEND_EVENT_OFERTA, null, null,null)
                    //sendMessageToFirebase(message, senderId, mReceiverId)
                }else{
                    postEvenNotifySms(RequestEventDetalleProducto.OK_SEND_EVENT_SMS,oferta)
                }
            }
        }
    }


    private fun sendPushNotificationToReceiver(message: String,userSelected:UserFirebase,imagen:String) {
        val userLogued= getLastUserLogued()
        var uiUserFirebase:String?=null
        uiUserFirebase = FirebaseAuth.getInstance().currentUser?.uid
        if(uiUserFirebase==null){
            uiUserFirebase=getLastUserLogued()?.IdFirebase
        }

        val fcmNotificationBuilder= FcmNotificationBuilder()
        fcmNotificationBuilder.title=userLogued?.Nombre+" ${userLogued?.Apellidos}"
        fcmNotificationBuilder.image_url=imagen
        fcmNotificationBuilder.message=message
        fcmNotificationBuilder.user_name=userLogued?.Nombre+" ${userLogued?.Apellidos}"
        fcmNotificationBuilder.ui=uiUserFirebase
        fcmNotificationBuilder.receiver_firebase_token=userSelected.TokenFcm
        fcmNotificationBuilder.room_id=roomId
        fcmNotificationBuilder.type_notification=NotificationTypeResources.NOTIFICATION_TYPE_OFERTA
        fcmNotificationBuilder.send()
    }

    /*
    fun sendNotifcationOferta(message:String, token:String,imageUrl:String){
        val dataPostNotification= FcmNotificationBuilder(NotificationTypeResources.NOTIFICATION_TYPE_OFERTA,message,imageUrl)
        val postNotification = PostNotification(token,dataPostNotification
                )
        val call = apiServiceFcm?.postSendNotifcation("Key="+Const_Resources.FIREBASE_TOKEN,postNotification)
        call?.enqueue(object : Callback<ResponsePostNotification> {
            override fun onResponse(call: Call<ResponsePostNotification>?, response: Response<ResponsePostNotification>?) {
                if (response != null && response.code() == 200) {
                    val value = response.body()
                    postEvent(RequestEventDetalleProducto.OK_SEND_EVENT_OFERTA, null, null,null)
                    //postEventOk(CultivoEvent.SAVE_EVENT, getCultivos(loteId), mCultivo)
                } else {
                    postEventError(RequestEventDetalleProducto.ERROR_EVENT, "Comprueba tu conexión")
                    Log.e("error", response?.message().toString())
                }
            }
            override fun onFailure(call: Call<ResponsePostNotification>?, t: Throwable?) {
                postEventError(RequestEventDetalleProducto.ERROR_EVENT, "Comprueba tu conexión")
            }
        })
    }
*/
    override fun saveOfertaLocal(oferta: Oferta, detalleOferta: DetalleOferta){

        val last_oferta = getLastOferta()
        if (last_oferta == null) {
            oferta.Oferta_Id = 1
        } else {
            oferta.Oferta_Id = last_oferta.Oferta_Id!! + 1
        }

        val last_detalle_oferta = getLastDetalleOferta()
        if (last_detalle_oferta == null) {
            detalleOferta.Detalle_Oferta_Id = 1
        } else {
            detalleOferta.Detalle_Oferta_Id = last_detalle_oferta.Detalle_Oferta_Id!! + 1
        }

        oferta.save()
        detalleOferta.OfertasId=oferta.Oferta_Id
        detalleOferta.save()
    }

    override fun getLastOferta(): Oferta? {
        val lastOferta = SQLite.select().from(Oferta::class.java).where().orderBy(Oferta_Table.Oferta_Id, false).querySingle()
        return lastOferta
    }

    override fun getLastDetalleOferta(): DetalleOferta? {
        val lastDetalleOferta = SQLite.select().from(DetalleOferta::class.java).where().orderBy(DetalleOferta_Table.Detalle_Oferta_Id, false).querySingle()
        return lastDetalleOferta
    }

    //region Events
    private fun postEventListUnidadMedida(type: Int, listUnidadMedida: List<Unidad_Medida>?, messageError: String?) {
        val upMutable = listUnidadMedida as MutableList<Object>
        postEvent(type, upMutable, null, messageError)
    }


    private fun postEventOk(type: Int, listProducto: List<Producto>?, producto: Producto?) {
        val productoListMutable = listProducto as MutableList<Object>
        var productoMutable: Object? = null
        if (producto != null) {
            productoMutable = producto as Object
        }
        postEvent(type, productoListMutable, productoMutable, null)
    }


    private fun postEvenNotifySms(type: Int,  oferta: Oferta?) {

        var ofertaMutable: Object? = null
        if (oferta != null) {
            ofertaMutable = oferta as Object
        }
        postEvent(type, null, ofertaMutable, null)
    }

    private fun postEventError(type: Int,messageError:String?) {
        postEvent(type, null,null,messageError)
    }

    //Main Post Event
    private fun postEvent(type: Int, listModel1:MutableList<Object>?,model:Object?,errorMessage: String?) {
        val event = RequestEventDetalleProducto(type, listModel1, model, errorMessage)
        event.eventType = type
        eventBus?.post(event)
    }
    //endregion
    //endregion
}