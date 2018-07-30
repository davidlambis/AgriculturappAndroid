package com.interedes.agriculturappv3.modules.main_menu.ui

import android.content.Context
import android.graphics.Bitmap
import android.preference.PreferenceManager
import android.util.Base64
import android.util.Log
import com.google.android.gms.tasks.OnCompleteListener
import com.google.firebase.FirebaseException
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.database.*
import com.google.firebase.iid.FirebaseInstanceId
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import com.interedes.agriculturappv3.config.DataSource
import com.interedes.agriculturappv3.libs.EventBus
import com.interedes.agriculturappv3.libs.GreenRobotEventBus
import com.interedes.agriculturappv3.libs.eventbus_rx.Rx_Bus
import com.interedes.agriculturappv3.modules.main_menu.ui.events.RequestEventMainMenu
import com.interedes.agriculturappv3.modules.main_menu.ui.events.RequestSendChat
import com.interedes.agriculturappv3.modules.models.Notification.NotificationLocal
import com.interedes.agriculturappv3.modules.models.Notification.NotificationLocal_Table
import com.interedes.agriculturappv3.modules.models.chat.Room
import com.interedes.agriculturappv3.modules.models.chat.UserFirebase
import com.interedes.agriculturappv3.modules.models.control_plaga.ControlPlaga
import com.interedes.agriculturappv3.modules.models.control_plaga.ControlPlaga_Table
import com.interedes.agriculturappv3.modules.models.cultivo.Cultivo
import com.interedes.agriculturappv3.modules.models.cultivo.Cultivo_Table
import com.interedes.agriculturappv3.modules.models.departments.Ciudad
import com.interedes.agriculturappv3.modules.models.departments.DeparmentsResponse
import com.interedes.agriculturappv3.modules.models.departments.Departamento
import com.interedes.agriculturappv3.modules.models.lote.Lote
import com.interedes.agriculturappv3.modules.models.lote.Lote_Table
import com.interedes.agriculturappv3.modules.models.ofertas.*
import com.interedes.agriculturappv3.modules.models.plagas.Enfermedad
import com.interedes.agriculturappv3.modules.models.produccion.Produccion
import com.interedes.agriculturappv3.modules.models.produccion.Produccion_Table
import com.interedes.agriculturappv3.modules.models.producto.*
import com.interedes.agriculturappv3.modules.models.sincronizacion.GetSincronizacionResponse
import com.interedes.agriculturappv3.modules.models.sincronizacion.GetSincronizacionTransacciones
import com.interedes.agriculturappv3.modules.models.sincronizacion.GetSynProductosUserResponse
import com.interedes.agriculturappv3.modules.models.sincronizacion.QuantitySync
import com.interedes.agriculturappv3.modules.models.tipoproducto.TipoProducto
import com.interedes.agriculturappv3.modules.models.tipoproducto.TipoProductoResponse
import com.interedes.agriculturappv3.modules.models.unidad_medida.CategoriaMedida
import com.interedes.agriculturappv3.modules.models.unidad_medida.CategoriaMedidaResponse
import com.interedes.agriculturappv3.modules.models.unidad_productiva.Unidad_Productiva
import com.interedes.agriculturappv3.modules.models.unidad_productiva.Unidad_Productiva_Table
import com.interedes.agriculturappv3.modules.models.usuario.Usuario
import com.interedes.agriculturappv3.modules.models.usuario.Usuario_Table
import com.interedes.agriculturappv3.modules.models.ventas.*
import com.interedes.agriculturappv3.modules.models.ventas.RequestApi.CategoriaPucResponse
import com.interedes.agriculturappv3.modules.models.ventas.RequestApi.EstadoTransaccionResponse
import com.interedes.agriculturappv3.services.resources.Const_Resources
import com.interedes.agriculturappv3.services.api.ApiInterface
import com.interedes.agriculturappv3.services.resources.ListasResources
import com.interedes.agriculturappv3.services.resources.Chat_Resources
import com.interedes.agriculturappv3.services.resources.RolResources
import com.interedes.agriculturappv3.services.resources.Status_Chat
import com.raizlabs.android.dbflow.config.FlowManager
import com.raizlabs.android.dbflow.data.Blob
import com.raizlabs.android.dbflow.kotlinextensions.delete
import com.raizlabs.android.dbflow.kotlinextensions.save
import com.raizlabs.android.dbflow.sql.language.SQLite
import com.raizlabs.android.dbflow.structure.database.transaction.FastStoreModelTransaction
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.io.ByteArrayOutputStream
import java.io.IOException
import java.text.SimpleDateFormat
import java.util.*

class MenuRepository: MainViewMenu.Repository {



    var eventBus: EventBus? = null
    var apiService: ApiInterface? = null

    //FIREBASE
    private var mUserDBRef: DatabaseReference? = null
    var mRoomDBRef: DatabaseReference? = null

    private var mStorageRef: StorageReference? = null
    var mAuth: FirebaseAuth? = null
    private var  mCurrentUserID: String? = null


    //Chat
    private var mReceiverId: String? = null
    private var mSenderId: String? = null
    private var roomGlobalUserSender: Room? = null
    private var roomGlobalUserReceiver: Room? = null

    init {
        eventBus = GreenRobotEventBus()
        apiService = ApiInterface.create()

        mCurrentUserID = FirebaseAuth.getInstance().currentUser?.uid
        if(mCurrentUserID==null){
            mCurrentUserID=getLastUserLogued()?.IdFirebase
        }


        mAuth= FirebaseAuth.getInstance()
        mUserDBRef = Chat_Resources.mUserDBRef
        mRoomDBRef = Chat_Resources.mRoomDBRef
        mStorageRef = FirebaseStorage.getInstance().reference.child("Photos").child("Users")
    }



    override fun checkListPlagas(): Long {
        val list= SQLite.select().from(Enfermedad::class.java).queryList()
        return list.size.toLong()
    }

    //Notifications
    override fun getCountNotifications(): Int {
        val userLogued=getLastUserLogued()
        val lisNotificationsPending = SQLite.select().from(NotificationLocal::class.java).where(NotificationLocal_Table.ReadNotification.eq(false)).and(NotificationLocal_Table.userLoguedId.eq(userLogued?.Id)).queryList()
        return  lisNotificationsPending.size
    }

    override fun verificateUserLoguedFirebaseFirebase(): FirebaseUser?
    {
        return  FirebaseAuth.getInstance().currentUser
    }

    override fun makeUserOnline(checkConection:Boolean,context: Context) {
        val userLogued=getLastUserLogued()
        if(userLogued!=null){
            val verficateLoguedFirebase=verificateUserLoguedFirebaseFirebase()
            if(checkConection){
                if(verficateLoguedFirebase==null){
                    loginFirebase(userLogued,context)
                }else{
                    makeUserOnlineSet()
                }
            }
        }
    }

    private fun makeUserOnlineSet() {
        val userStatus= mUserDBRef?.child(mCurrentUserID+"/status")
        val userLastOnlineRef= mUserDBRef?.child(mCurrentUserID+"/last_Online")
        userStatus?.setValue(Status_Chat.ONLINE)
        userStatus?.onDisconnect()?.setValue(Status_Chat.OFFLINE)
        userLastOnlineRef?.onDisconnect()?.setValue(ServerValue.TIMESTAMP);

        //Status FCM
        val userTokenMessaginStatus= mUserDBRef?.child(mCurrentUserID+"/statusTokenFcm")

        userTokenMessaginStatus?.setValue(true)
    }

    override fun makeUserOffline(checkConection:Boolean,context: Context) {
        val verficateLoguedFirebase=verificateUserLoguedFirebaseFirebase()
        if(verficateLoguedFirebase!=null){
                makeUserOfflineSet()
        }
    }

    private fun makeUserOfflineSet() {
        val userStatus= mUserDBRef?.child(mCurrentUserID+"/status")
        val userLastOnlineRef= mUserDBRef?.child(mCurrentUserID+"/last_Online")
        userStatus?.setValue(Status_Chat.OFFLINE)
        userLastOnlineRef?.setValue(ServerValue.TIMESTAMP);
    }

    override fun loginFirebase(usuario:Usuario,context: Context)
    {
        mAuth?.signInWithEmailAndPassword(usuario.Email!!, usuario.Contrasena!!)?.addOnCompleteListener({ task ->
            if (task.isSuccessful) {
                val mCurrentUserIDLogin =task.result.user.uid
                usuario.IdFirebase=mCurrentUserIDLogin
                usuario.save()
                resetTokenFCM(context)
                mCurrentUserID = mCurrentUserIDLogin
                makeUserOnlineSet()
            } else {
                try {
                    throw task.exception!!
                } catch (firebaseException: FirebaseException) {
                   // postEventError(RequestEventAccount.ERROR_EVENT, firebaseException.toString())
                    Log.e("Error Post", firebaseException.toString())
                }
            }
        })
    }



    //region CHAT ONLINE

    override fun navigateChatOnline(checkConection: Boolean, usuario: Usuario) {
       if(checkConection){
           getUserToFirebase(usuario)
       }else{
           postEventError(RequestEventMainMenu.ERROR_EVENT, "Verifique su conexion")
       }
    }

    override fun navigateChatOnlineNotification(uiFirebase: String) {
        mUserDBRef?.child("$uiFirebase")?.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                if (dataSnapshot.value != null) {
                    val user = dataSnapshot.getValue<UserFirebase>(UserFirebase::class.java)
                    //user = issue.getValue<UserFirebase>(UserFirebase::class.java)
                    mReceiverId=user?.User_Id
                    mSenderId=FirebaseAuth.getInstance().currentUser!!.uid
                    findRoomUser(user!!)
                }
            }
            override fun onCancelled(databaseError: DatabaseError) {
                postEventError(RequestEventMainMenu.ERROR_EVENT,databaseError.message)
            }
        })
    }

    private fun getUserToFirebase(usuarioTo: Usuario?) {
        if(usuarioTo!=null){
            val query = mUserDBRef?.orderByChild("correo")?.equalTo(usuarioTo?.Email)
            query?.addListenerForSingleValueEvent(object : ValueEventListener {
                override fun onDataChange(dataSnapshot: DataSnapshot) {
                    if (dataSnapshot.exists()) {
                        // dataSnapshot is the "issue" node with all children with id 0
                        var user: UserFirebase?=null
                        for (issue in dataSnapshot.children) {
                            // do something with the individual "issues"
                            user = issue.getValue<UserFirebase>(UserFirebase::class.java)
                            mReceiverId=user?.User_Id
                            mSenderId=FirebaseAuth.getInstance().currentUser!!.uid
                            //if not current user, as we do not want to show ourselves then chat with ourselves lol
                        }
                        findRoomUser(user!!)
                        //sendMessageToFirebase(message, senderId, mReceiverId)
                    }
                }
                override fun onCancelled(databaseError: DatabaseError) {
                    val error = databaseError.message
                    postEventError(RequestEventMainMenu.ERROR_EVENT, error)
                    //postEvent(RequestEventDetalleProducto.OK_SEND_EVENT_OFERTA, null, null,null)
                }
            })
        }
    }

    private fun findRoomUser(userFirebase: UserFirebase) {
        //val query = mRoomDBRef?.child("/"+mCompradorSenderId+"/"+mProductorReceiverId)
        val query = mRoomDBRef?.child(mSenderId)?.orderByChild("user_To")?.equalTo(Chat_Resources.getRoomById(mReceiverId))
        query?.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                if (dataSnapshot.exists()) {
                    //var room = dataSnapshot.value as HashMap<*, *>
                    //roomId=room.get("idRoom") as String;
                    var roomFind= Room()

                    for (issue in dataSnapshot.children) {
                        val room = issue.getValue<Room>(Room::class.java)
                        //roomId=room?.IdRoom
                        roomGlobalUserSender= room!!
                    }
                    //sendMessageToFirebase(message,oferta,userFirebase)
                    val sendChatRequest= RequestSendChat(roomGlobalUserSender,userFirebase)
                    Rx_Bus.publish(sendChatRequest)
                }else{
                    createRoomUser(userFirebase,mReceiverId,true,userFirebase)
                }
            }
            override fun onCancelled(databaseError: DatabaseError) {
                val error = databaseError.message
                postEventError(RequestEventMainMenu.ERROR_EVENT, error)
            }
        })
    }

    fun getDateFormatApi(date: Date): String? {
        val format1 = SimpleDateFormat("yyyy-MM-dd")
        return format1.format(date)
    }

    private fun createRoomUser(user: UserFirebase?, idProductor:String?, roomComprador:Boolean, userFirebase: UserFirebase) {
        val date= Calendar.getInstance().time
        val dateString =getDateFormatApi(date)

        if(idProductor!= null){
            if(roomComprador){

                val room = Room(Chat_Resources.getRoomByCompradorProductor(mSenderId,mReceiverId), mSenderId, mReceiverId, 0, 0,dateString,"")
                mRoomDBRef?.child(mSenderId)?.child(Chat_Resources.getRoomById(mReceiverId))?.setValue(room)?.addOnCompleteListener(OnCompleteListener<Void> { task ->
                    if (!task.isSuccessful) {
                        val error = task.exception
                        postEventError(RequestEventMainMenu.ERROR_EVENT, error.toString())
                    } else {
                        roomGlobalUserSender=room
                        createRoomUser(user,mReceiverId,false,userFirebase)
                    }
                })
            }else{
                val room = Room(Chat_Resources.getRoomByCompradorProductor(mSenderId,mReceiverId), mReceiverId, mSenderId, 0, 0,dateString,"")
                mRoomDBRef?.child(mReceiverId)?.child(Chat_Resources.getRoomById(mSenderId))?.setValue(room)?.addOnCompleteListener(OnCompleteListener<Void> { task ->
                    if (!task.isSuccessful) {
                        val error = task.exception
                        postEventError(RequestEventMainMenu.ERROR_EVENT, error.toString())
                        //error
                    } else {
                        roomGlobalUserReceiver=room
                        createRoomUser(user,null,false,userFirebase)
                    }
                })
            }
        }else{
            updateDatesRoomUser()

            val roomDateComprador= mRoomDBRef?.child(mSenderId)?.child(Chat_Resources.getRoomById(mReceiverId)+"/date")
            roomDateComprador?.setValue(ServerValue.TIMESTAMP);

            val roomDateProductor= mRoomDBRef?.child(mReceiverId)?.child(Chat_Resources.getRoomById(mSenderId)+"/date")
            roomDateProductor?.setValue(ServerValue.TIMESTAMP);

            val sendChatRequest= RequestSendChat(roomGlobalUserSender,userFirebase)
            Rx_Bus.publish(sendChatRequest)

        }
    }


    private fun updateDatesRoomUser() {
        //success adding user to db as well
        val roomDateLastComprador= mRoomDBRef?.child(mSenderId)?.child(Chat_Resources.getRoomById(mReceiverId)+"/date_Last")
        roomDateLastComprador?.setValue(ServerValue.TIMESTAMP);

        val roomDateLastProductor= mRoomDBRef?.child(mReceiverId)?.child(Chat_Resources.getRoomById(mSenderId)+"/date_Last")
        roomDateLastProductor?.setValue(ServerValue.TIMESTAMP);
    }





    //endregion

    //region TOKEN
    private fun resetTokenFCM(context: Context) {
        try {
            // Check for current token
            val originalToken = getTokenFromPrefs(context)
            Log.d("TAG", "Token before deletion: $originalToken")
            // Resets Instance ID and revokes all tokens.
            Thread(Runnable {
                try {
                    FirebaseInstanceId.getInstance().deleteInstanceId()
                    // Clear current saved token
                    saveTokenToPrefs("",context)

                    // Check for success of empty token
                    val tokenCheck = getTokenFromPrefs(context)
                    Log.d("TAG", "Token deleted. Proof: $tokenCheck")

                    // Now manually call onTokenRefresh()
                    Log.d("TAG", "Getting new token")
                    FirebaseInstanceId.getInstance().token

                } catch (e: IOException) {
                    e.printStackTrace()
                }
            }).start()
        } catch (e: IOException) {
            e.printStackTrace()
        }
    }

    private fun saveTokenToPrefs(_token:String?,context: Context )
    {
        // Access Shared Preferences
        //var preferences = PreferenceManager.getDefaultSharedPreferences(this);
        //var editor = preferences.edit();
        val preferences = PreferenceManager.getDefaultSharedPreferences(context)
        // Save to SharedPreferences
        //editor.putString("registration_id", _token);
        //editor.apply();
        preferences.edit().putString(Const_Resources.FIREBASE_TOKEN, _token).apply()
    }

    private fun  getTokenFromPrefs(context:Context):String?
    {
        val preferences = PreferenceManager.getDefaultSharedPreferences(context);
        return preferences.getString(Const_Resources.FIREBASE_TOKEN, null);
    }



    //endregion

    override fun logOut(usuario: Usuario?) {
        try {
            mAuth = FirebaseAuth.getInstance()
            if (mAuth?.currentUser != null) {
                mAuth?.signOut()

                var currentUserID = mAuth?.currentUser?.uid
                if(currentUserID==null){
                    currentUserID=getLastUserLogued()?.IdFirebase
                }

                val userTokenMessaginStatus= mUserDBRef?.child(currentUserID+"/statusTokenFcm")

                userTokenMessaginStatus?.setValue(false)
            }
            usuario?.UsuarioRemembered = false
            //usuario?.AccessToken = null
            usuario?.save()
        } catch (e: Exception) {
           // postEvent(RequestEvent.ERROR_EVENT, e.message.toString())
        }
    }

    override fun syncQuantityData(automatic:Boolean) {
        val usuarioLogued=getLastUserLogued()
        val counRegisterUnidadesProductivas=SQLite.select().from(Unidad_Productiva::class.java)
                .where(Unidad_Productiva_Table.Estado_Sincronizacion.eq(false))
                .and(Unidad_Productiva_Table.UsuarioId.eq(usuarioLogued?.Id))
                .queryList().count()

        val counRegisterLotes=SQLite.select().from(Lote::class.java).where(Lote_Table.EstadoSincronizacion.eq(false))
                .and(Lote_Table.UsuarioId.eq(usuarioLogued?.Id))
                .queryList().count()
        val counRegisterCultivos=SQLite.select().from(Cultivo::class.java).where(Cultivo_Table.EstadoSincronizacion.eq(false))
                .and(Cultivo_Table.UsuarioId.eq(usuarioLogued?.Id))
                .queryList().count()
        val counRegisterControlPlagas=SQLite.select().from(ControlPlaga::class.java).where(ControlPlaga_Table.Estado_Sincronizacion.eq(false))
                .and(ControlPlaga_Table.UsuarioId.eq(usuarioLogued?.Id))
                .queryList().count()
        val counRegisterProducccion=SQLite.select().from(Produccion::class.java).where(Produccion_Table.Estado_Sincronizacion.eq(false))
                .and(Produccion_Table.UsuarioId.eq(usuarioLogued?.Id))
                .queryList().count()
        val counRegisterProductos=SQLite.select().from(Producto::class.java).where(Producto_Table.Estado_Sincronizacion.eq(false))
                .and(Producto_Table.userId.eq(usuarioLogued?.Id))
                .queryList().count()
        val counRegisterTransacciones=SQLite.select().from(Transaccion::class.java).where(Transaccion_Table.Estado_Sincronizacion.eq(false))
                .and(Transaccion_Table.UsuarioId.eq(usuarioLogued?.Id))
                .queryList().count()

        val registerTotal= counRegisterUnidadesProductivas+
                counRegisterControlPlagas+
                counRegisterCultivos+
                counRegisterLotes+counRegisterProducccion+counRegisterProductos+counRegisterTransacciones

        val countUpdatesUnidadesProductivas=SQLite.select()
                .from(Unidad_Productiva::class.java)
                .where(Unidad_Productiva_Table.Estado_Sincronizacion.eq(true))
                .and(Unidad_Productiva_Table.UsuarioId.eq(usuarioLogued?.Id))
                .and(Unidad_Productiva_Table.Estado_SincronizacionUpdate.eq(false)).queryList().count()


        val countUpdatesLotes=SQLite.select().from(Lote::class.java)
                .where(Lote_Table.EstadoSincronizacion.eq(true))
                .and(Lote_Table.UsuarioId.eq(usuarioLogued?.Id))
                .and(Lote_Table.Estado_SincronizacionUpdate.eq(false)).queryList().count()


        val countUpdatesCultivos=SQLite.select().from(Cultivo::class.java)
                .where(Cultivo_Table.EstadoSincronizacion.eq(true))
                .and(Cultivo_Table.UsuarioId.eq(usuarioLogued?.Id))
                .and(Cultivo_Table.Estado_SincronizacionUpdate.eq(false)).queryList().count()



        val countUpdatesControlPlagas=SQLite.select().from(ControlPlaga::class.java)
                .where(ControlPlaga_Table.Estado_Sincronizacion.eq(true))
                .and(ControlPlaga_Table.UsuarioId.eq(usuarioLogued?.Id))
                .and(ControlPlaga_Table.Estado_SincronizacionUpdate.eq(false)).queryList().count()

        val countUpdatesProducccion=SQLite.select().from(Produccion::class.java)
                .where(Produccion_Table.Estado_Sincronizacion.eq(true))
                .and(Produccion_Table.UsuarioId.eq(usuarioLogued?.Id))
                .and(Produccion_Table.Estado_SincronizacionUpdate.eq(false)).queryList().count()

        val countUpdatesProductos=SQLite.select().from(Producto::class.java)
                .where(Producto_Table.Estado_Sincronizacion.eq(true))
                .and(Producto_Table.userId.eq(usuarioLogued?.Id))
                .and(Producto_Table.Estado_SincronizacionUpdate.eq(false)).queryList().count()

        val countUpdatesTransacciones=SQLite.select().from(Transaccion::class.java)
                .where(Transaccion_Table.Estado_Sincronizacion.eq(true))
                .and(Transaccion_Table.UsuarioId.eq(usuarioLogued?.Id))
                .and(Transaccion_Table.Estado_SincronizacionUpdate.eq(false)).queryList().count()

        val updatesTotal= countUpdatesUnidadesProductivas+
                countUpdatesLotes+
                countUpdatesCultivos+
                countUpdatesControlPlagas+countUpdatesProducccion+countUpdatesProductos+countUpdatesTransacciones


        if(automatic){
            val quantitySync= QuantitySync(registerTotal.toLong(),updatesTotal.toLong())
            postEventOkQuntitySync(RequestEventMainMenu.SYNC_RESUME_AUTOMATIC,quantitySync)
        }else{
            val quantitySync= QuantitySync(registerTotal.toLong(),updatesTotal.toLong())
            postEventOkQuntitySync(RequestEventMainMenu.SYNC_RESUME,quantitySync)
        }
    }


    //Listas Iniciales
    fun getLastUp(usuario:Usuario?): Unidad_Productiva? {

        if(usuario!=null){

            val lastUnidadProductiva = SQLite.select()
                    .from(Unidad_Productiva::class.java)
                    .where(Unidad_Productiva_Table.UsuarioId.eq(usuario.Id)).orderBy(Unidad_Productiva_Table.Unidad_Productiva_Id, false)
                    .querySingle()
            return lastUnidadProductiva
        }else{
            val lastUnidadProductiva = SQLite.select()
                    .from(Unidad_Productiva::class.java)
                    .orderBy(Unidad_Productiva_Table.Unidad_Productiva_Id, false)
                    .querySingle()
            return lastUnidadProductiva

        }
    }

    fun getLastLote(usuario:Usuario?): Lote? {


        if(usuario!=null){
            val lastLote = SQLite.select().from(Lote::class.java)
                    .where(Lote_Table.UsuarioId.eq(usuario.Id))
                    .orderBy(Lote_Table.LoteId, false).querySingle()
            return lastLote


        }else{
            val lastLote = SQLite.select().from(Lote::class.java)
                    .orderBy(Lote_Table.LoteId, false).querySingle()
            return lastLote

        }

    }

    fun getLastCultivo(usuario:Usuario?): Cultivo? {
        if(usuario!=null){
            val lastCultivo = SQLite.select().from(Cultivo::class.java)
                    .where(Cultivo_Table.UsuarioId.eq(usuario.Id))
                    .orderBy(Cultivo_Table.CultivoId, false).querySingle()
            return lastCultivo

        }else{
            val lastCultivo = SQLite.select().from(Cultivo::class.java)
                    .orderBy(Cultivo_Table.CultivoId, false).querySingle()
            return lastCultivo
        }
    }

    fun getLastProduccion(usuario:Usuario?): Produccion? {
        if(usuario!=null){
            val lastProduccion = SQLite.select().from(Produccion::class.java)
                    .where(Produccion_Table.UsuarioId.eq(usuario.Id))
                    .orderBy(Produccion_Table.ProduccionId, false).querySingle()
            return lastProduccion
        }else{
            val lastProduccion = SQLite.select().from(Produccion::class.java)
                    .orderBy(Produccion_Table.ProduccionId, false).querySingle()
            return lastProduccion
        }
    }

    fun getLastControlPlaga(usuario:Usuario?): ControlPlaga? {

        if(usuario!=null){
            val lastControlPlaga = SQLite.select().from(ControlPlaga::class.java)
                    .where(ControlPlaga_Table.UsuarioId.eq(usuario.Id)).orderBy(ControlPlaga_Table.ControlPlagaId, false).querySingle()
            return lastControlPlaga

        }else{
            val lastControlPlaga = SQLite.select().from(ControlPlaga::class.java)
                    .orderBy(ControlPlaga_Table.ControlPlagaId, false).querySingle()
            return lastControlPlaga
        }



    }

    fun getLastTransaccion(usuario:Usuario?): Transaccion? {
        if(usuario!=null){
            val lastTransaccion = SQLite.select().from(Transaccion::class.java)
                    .where(Transaccion_Table.UsuarioId.eq(usuario.Id)).orderBy(Transaccion_Table.TransaccionId, false).querySingle()
            return lastTransaccion
        }else{
            val lastTransaccion = SQLite.select().from(Transaccion::class.java)
                    .orderBy(Transaccion_Table.TransaccionId, false).querySingle()
            return lastTransaccion
        }
    }

     fun getLastOferta(): Oferta? {
        val lastOferta = SQLite.select().from(Oferta::class.java).where().orderBy(Oferta_Table.Oferta_Id, false).querySingle()
        return lastOferta
    }

     fun getLastDetalleOferta(): DetalleOferta? {
        val lastDetalleOferta = SQLite.select().from(DetalleOferta::class.java).where().orderBy(DetalleOferta_Table.Detalle_Oferta_Id, false).querySingle()
        return lastDetalleOferta
    }

    fun getLastTercero(usuario:Usuario?): Tercero? {

        if(usuario!=null){
            val lastTercero = SQLite.select().from(Tercero::class.java).where(Tercero_Table.Usuario_Id.eq(usuario.Id)).orderBy(Tercero_Table.TerceroId, false).querySingle()
            return lastTercero

        }else{
            val lastTercero = SQLite.select().from(Tercero::class.java).orderBy(Tercero_Table.TerceroId, false).querySingle()
            return lastTercero
        }


    }

    fun getLastProducto(usuario:Usuario?): Producto? {

        if(usuario!=null){
            val lastProducto = SQLite.select().from(Producto::class.java)
                    .where(Producto_Table.userId.eq(usuario.Id)).orderBy(Producto_Table.ProductoId, false).querySingle()
            return lastProducto

        }else{
            val lastProducto = SQLite.select().from(Producto::class.java)
                    .orderBy(Producto_Table.ProductoId, false).querySingle()
            return lastProducto
        }
    }


    //region Métodos Interfaz

    override fun getLastUserLogued(): Usuario? {

        val usuarioLogued = SQLite.select().from(Usuario::class.java).where(Usuario_Table.UsuarioRemembered.eq(true)).querySingle()
        return usuarioLogued
    }

    override fun getListasIniciales() {
        val usuario= getLastUserLogued()
        //LISTAS ROL PRODUCTOR
        /*-----------------------------------------------------------------------------------------------------------------*/
        if(usuario?.RolNombre?.equals(RolResources.PRODUCTOR)!!){

            val users=SQLite.select().from(Usuario::class.java).where(Usuario_Table.RolNombre.eq(RolResources.COMPRADOR)).queryList()
            for (user in users){
                user.delete()
            }
            val query = ListasResources.queryGeneral("UsuarioId",usuario.Id.toString())
            val callInformacionSinronized = apiService?.getSyncInformacionUsuario(query)
            callInformacionSinronized?.enqueue(object : Callback<GetSincronizacionResponse> {
                override fun onResponse(call: Call<GetSincronizacionResponse>?, response: Response<GetSincronizacionResponse>?) {
                    if (response != null && response.code() == 200) {

                        val unidadesProductivas = response.body()?.value as MutableList<Unidad_Productiva>


                        //TODO Delete information in local, add new remote
                       SQLite.select()
                                .from(Unidad_Productiva::class.java)
                                .where(Unidad_Productiva_Table.Estado_Sincronizacion.eq(true))
                                .and(Unidad_Productiva_Table.UsuarioId.eq(usuario.Id))
                                .and(Unidad_Productiva_Table.Estado_SincronizacionUpdate.eq(true))
                                .queryList().forEach{it.delete()}

                        SQLite.select()
                                .from(Lote::class.java)
                                .where(Lote_Table.EstadoSincronizacion.eq(true))
                                .and(Lote_Table.UsuarioId.eq(usuario.Id))
                                .and(Lote_Table.Estado_SincronizacionUpdate.eq(true))
                                .queryList()
                                .forEach{it.delete()}

                        SQLite.select()
                                .from(Cultivo::class.java)
                                .where(Cultivo_Table.EstadoSincronizacion.eq(true))
                                .and(Cultivo_Table.UsuarioId.eq(usuario.Id))
                                .and(Cultivo_Table.Estado_SincronizacionUpdate.eq(true))
                                .queryList().forEach{it.delete()}

                        SQLite.select()
                                .from(Produccion::class.java)
                                .where(Produccion_Table.Estado_Sincronizacion.eq(true))
                                .and(Produccion_Table.UsuarioId.eq(usuario.Id))
                                .and(Produccion_Table.Estado_SincronizacionUpdate.eq(true))
                                .queryList().forEach{it.delete()}

                        SQLite.select()
                                .from(ControlPlaga::class.java)
                                .where(ControlPlaga_Table.Estado_Sincronizacion.eq(true))
                                .and(ControlPlaga_Table.UsuarioId.eq(usuario.Id))
                                .and(ControlPlaga_Table.Estado_SincronizacionUpdate.eq(true))
                                .queryList().forEach{it.delete()}

                       /*Delete.table(Unidad_Productiva::class.java);
                        Delete.table(Lote::class.java);
                        Delete.table(Cultivo::class.java);
                        Delete.table(Produccion::class.java);
                        Delete.table(ControlPlaga::class.java);*/
                       /*SQLite.delete<Unidad_Productiva>(Unidad_Productiva::class.java)
                                .where(Unidad_Productiva_Table.Estado_Sincronizacion.eq(true))
                                .and(Unidad_Productiva_Table.UsuarioId.eq(usuario.Id))
                                .and(Unidad_Productiva_Table.Estado_SincronizacionUpdate.eq(true))
                                .async()
                                .execute()

                        SQLite.delete<Lote>(Lote::class.java)
                                .where(Lote_Table.EstadoSincronizacion.eq(true))
                                .and(Lote_Table.UsuarioId.eq(usuario.Id))
                                .and(Lote_Table.Estado_SincronizacionUpdate.eq(true))
                                .async()
                                .execute()

                        SQLite.delete<Cultivo>(Cultivo::class.java)
                                .where(Cultivo_Table.EstadoSincronizacion.eq(true))
                                .and(Cultivo_Table.UsuarioId.eq(usuario.Id))
                                .and(Cultivo_Table.Estado_SincronizacionUpdate.eq(true))
                                .async()
                                .execute()

                        SQLite.delete<Produccion>(Produccion::class.java)
                                .where(Produccion_Table.Estado_Sincronizacion.eq(true))
                                .and(Produccion_Table.UsuarioId.eq(usuario.Id))
                                .and(Produccion_Table.Estado_SincronizacionUpdate.eq(true))
                                .async()
                                .execute()

                        SQLite.delete<ControlPlaga>(ControlPlaga::class.java)
                                .where(ControlPlaga_Table.Estado_Sincronizacion.eq(true))
                                .and(ControlPlaga_Table.UsuarioId.eq(usuario.Id))
                                .and(ControlPlaga_Table.Estado_SincronizacionUpdate.eq(true))
                                .async()
                                .execute()
                                */


                        //TODO Add information new remote
                        for (item in unidadesProductivas) {
                            val unidadProductivaVerficateSave= SQLite.select()
                                    .from(Unidad_Productiva::class.java)
                                    .where(Unidad_Productiva_Table.Id_Remote.eq(item.Id_Remote))
                                    .and(Unidad_Productiva_Table.Estado_SincronizacionUpdate.eq(false))
                                    .querySingle()


                            //TODO Verifica si tiene pendiente actualizacion por sincronizar
                            if (unidadProductivaVerficateSave !=null){
                                item.Unidad_Productiva_Id=unidadProductivaVerficateSave.Unidad_Productiva_Id
                            }else{
                                val last_up = getLastUp(null)
                                if (last_up == null) {
                                    item.Unidad_Productiva_Id = 1
                                } else {
                                    item.Unidad_Productiva_Id = last_up.Unidad_Productiva_Id!! + 1
                                }

                                item.UsuarioId=usuario.Id
                                item.Nombre_Ciudad= if (item.Ciudad!=null) item.Ciudad?.Nombre else null
                                item.Nombre_Unidad_Medida= if (item.UnidadMedida!=null) item.UnidadMedida?.Descripcion else null
                                item.Nombre_Departamento= if (item.Ciudad!=null) item.Ciudad?.Departamento?.Nombre else null
                                item.Estado_Sincronizacion=true
                                item.Estado_SincronizacionUpdate=true

                                if(item.LocalizacionUps?.size!!>0){
                                    for (localizacion in item.LocalizacionUps!!){
                                        item.DireccionAproximadaGps=localizacion.DireccionAproximadaGps
                                        item.Latitud=localizacion.Latitud?.toDouble()
                                        item.Longitud=localizacion.Longitud?.toDouble()
                                        item.Coordenadas=localizacion.Coordenadas
                                        item.Direccion=localizacion.Direccion
                                        item.LocalizacionUpId=localizacion.Id
                                    }
                                }

                                item.Configuration_Point=true
                                item.Configuration_Poligon=false
                                item.save()
                            }

                            if(item.Lotes?.size!!>0){
                                for (lote in item.Lotes!!){
                                    val loteVerficateSave= SQLite.select()
                                            .from(Lote::class.java)
                                            .where(Lote_Table.Id_Remote.eq(lote.Id_Remote))
                                            .and(Lote_Table.Estado_SincronizacionUpdate.eq(false))
                                            .querySingle()

                                    //TODO Verifica si tiene pendiente actualizacion por sincronizar
                                    if (loteVerficateSave!=null){
                                        lote.LoteId=loteVerficateSave.LoteId
                                    }else{

                                        val last_lote = getLastLote(null)
                                        if (last_lote == null) {
                                            lote.LoteId = 1
                                        } else {
                                            lote.LoteId = last_lote.LoteId + 1
                                        }

                                        val coordenadas =lote.Localizacion

                                        if(coordenadas!=null){
                                            if(coordenadas.isNotEmpty()){
                                                val separated = coordenadas.split("/".toRegex()).dropLastWhile { it.isEmpty() }.toTypedArray()
                                                val latitud= separated!![0].toDoubleOrNull() // this will contain "Fruit"
                                                val longitud=separated!![1].toDoubleOrNull() // this will contain " they taste good"
                                                lote.Latitud=latitud
                                                lote.Longitud=longitud
                                                lote.Coordenadas=coordenadas
                                            }
                                        }
                                        lote.UsuarioId=usuario.Id
                                        lote.Unidad_Productiva_Id=item.Unidad_Productiva_Id
                                        lote.Nombre_Unidad_Medida= if (lote.UnidadMedida!=null) lote.UnidadMedida?.Descripcion else null
                                        lote.Nombre_Unidad_Productiva= item.nombre

                                        lote.Nombre= if (lote.Nombre==null) "" else lote.Nombre
                                        lote.Descripcion= if (lote.Descripcion==null) "" else lote.Descripcion
                                        lote.Estado_SincronizacionUpdate=true
                                        lote.EstadoSincronizacion=true

                                        lote.save()
                                    }

                                    if(lote.Cultivos?.size!!>0){

                                        for(cultivo in lote.Cultivos!!){


                                            val cultivoVerficateSave= SQLite.select()
                                                    .from(Cultivo::class.java)
                                                    .where(Cultivo_Table.Id_Remote.eq(cultivo.Id_Remote))
                                                    .and(Cultivo_Table.Estado_SincronizacionUpdate.eq(false))
                                                    .querySingle()
                                            //TODO Verifica si tiene pendiente actualizacion por sincronizar
                                            if (cultivoVerficateSave!=null){
                                                cultivo.CultivoId=cultivoVerficateSave.CultivoId
                                            }else{
                                                val last_cultivo = getLastCultivo(null)
                                                if (last_cultivo == null) {
                                                    cultivo.CultivoId = 1
                                                } else {
                                                    cultivo.CultivoId = last_cultivo.CultivoId!! + 1
                                                }
                                                cultivo.UsuarioId=usuario.Id
                                                cultivo.LoteId=lote.LoteId
                                                cultivo.NombreUnidadProductiva= item.nombre
                                                cultivo.NombreLote= lote.Nombre
                                                cultivo.EstadoSincronizacion= true
                                                cultivo.Estado_SincronizacionUpdate= true
                                                cultivo.stringFechaInicio=cultivo.getFechaStringFormatApi(cultivo.FechaIncio)
                                                cultivo.stringFechaFin=cultivo.getFechaStringFormatApi(cultivo.FechaFin)

                                                cultivo.Nombre_Tipo_Producto= if (cultivo.detalleTipoProducto!=null) cultivo.detalleTipoProducto?.tipoProducto?.Nombre else null
                                                cultivo.Nombre_Detalle_Tipo_Producto= if (cultivo.detalleTipoProducto!=null) cultivo.detalleTipoProducto?.Descripcion else null
                                                cultivo.Id_Tipo_Producto= if (cultivo.detalleTipoProducto!=null) cultivo.detalleTipoProducto?.TipoProductoId else null
                                                cultivo.Nombre_Unidad_Medida=if (cultivo.unidadMedida!=null) cultivo.unidadMedida?.Descripcion else null

                                                cultivo.save()
                                            }

                                            if(cultivo.produccions?.size!!>0){

                                                for(produccion in cultivo.produccions!!){

                                                    val produccionVerficateSave= SQLite.select()
                                                            .from(Produccion::class.java)
                                                            .where(Producto_Table.Id_Remote.eq(produccion.Id_Remote))
                                                            .and(Producto_Table.Estado_SincronizacionUpdate.eq(false))
                                                            .querySingle()

                                                    //TODO Verifica si tiene pendiente actualizacion por sincronizar
                                                    if (produccionVerficateSave!=null){
                                                        produccion.ProduccionId=produccionVerficateSave.ProduccionId
                                                    }else{
                                                        val last_productions = getLastProduccion(null)
                                                        if (last_productions == null) {
                                                            produccion.ProduccionId = 1
                                                        } else {
                                                            produccion.ProduccionId = last_productions.ProduccionId!! + 1
                                                        }

                                                        produccion.UsuarioId=usuario.Id
                                                        produccion.CultivoId=cultivo.CultivoId
                                                        produccion.Estado_Sincronizacion=true
                                                        produccion.Estado_SincronizacionUpdate=true
                                                        produccion.NombreCultivo=cultivo.Nombre
                                                        produccion.NombreLote=lote.Nombre
                                                        produccion.NombreUnidadProductiva=item.nombre
                                                        produccion.FechaInicioProduccion=produccion.getFechaDate(produccion.StringFechaInicio)
                                                        produccion.FechaFinProduccion=produccion.getFechaDate(produccion.StringFechaFin)
                                                        produccion.NombreUnidadMedida= if (produccion.unidadMedida!=null) produccion.unidadMedida?.Descripcion else null
                                                        produccion.save()
                                                    }
                                                }
                                            }



                                            if(cultivo.controlPlagas?.size!!>0){
                                                for(controlplaga in cultivo.controlPlagas!!){

                                                    val controlPlagasVerficateSave= SQLite.select()
                                                            .from(ControlPlaga::class.java)
                                                            .where(ControlPlaga_Table.Id_Remote.eq(controlplaga.Id_Remote))
                                                            .and(ControlPlaga_Table.Estado_SincronizacionUpdate.eq(false))
                                                            .querySingle()

                                                    //TODO Verifica si tiene pendiente actualizacion por sincronizar
                                                    if (controlPlagasVerficateSave!=null){
                                                        controlplaga.ControlPlagaId=controlPlagasVerficateSave.ControlPlagaId
                                                    }else{
                                                        val last_controlplaga = getLastControlPlaga(null)
                                                        if (last_controlplaga == null) {
                                                            controlplaga.ControlPlagaId = 1
                                                        } else {
                                                            controlplaga.ControlPlagaId = last_controlplaga.ControlPlagaId!! + 1
                                                        }

                                                        controlplaga.UsuarioId=usuario.Id
                                                        controlplaga.CultivoId=cultivo.CultivoId
                                                        controlplaga.Estado_Sincronizacion=true
                                                        controlplaga.Estado_SincronizacionUpdate=true
                                                        controlplaga.Fecha_aplicacion_local=controlplaga.getFechaDate(controlplaga.Fecha_aplicacion)
                                                        controlplaga.Fecha_Erradicacion_Local=if(controlplaga.FechaErradicacion!=null)controlplaga.getFechaDate(controlplaga.FechaErradicacion)else null
                                                        controlplaga.save()
                                                    }
                                                }
                                            }
                                        }
                                    }
                                }
                            }
                        }



                        Log.d("SYNC DATA", "UP, Lotes, Cultivos,Produccion,ControlPlagas Loaded" )

                        loadTransacciones(usuario)

                    } else {
                        postEventError(RequestEventMainMenu.ERROR_EVENT, "Comprueba tu conexión a Internet")
                    }
                }
                override fun onFailure(call: Call<GetSincronizacionResponse>?, t: Throwable?) {
                    postEventError(RequestEventMainMenu.ERROR_EVENT, "Comprueba tu conexión a Internet")
                }
            })


        }
        //LISTAS ROL COMPRADOR
        /*-----------------------------------------------------------------------------------------------------------------*/
        else if(usuario.RolNombre?.equals(RolResources.COMPRADOR)!!){
            //Limpiar BD con informacion de un productor
            //TODO Delete information in local, add new remote


            SQLite.select()
                    .from(Oferta::class.java)
                    .queryList().forEach{it.delete()}

            SQLite.select()
                    .from(DetalleOferta::class.java)
                    .queryList().forEach{it.delete()}

            loadOfertas(usuario)

        }
        //LISTAS PARA AMBOS ROLES
        /*------------------------------------------------------------------------------------------------------------------*/
        //Tipos de Producto
        //val listTipoProducto: ArrayList<TipoProducto> = Listas.listaTipoProducto()

        //Categorías de Producto
        //getLastUserLogued
        //
    }

    fun getCategoriaPuk(){
        //Categorias Puk
        val categoriaspuk = apiService?.getCategoriasPuc()
        categoriaspuk?.enqueue(object : Callback<CategoriaPucResponse> {
            override fun onResponse(call: Call<CategoriaPucResponse>?, response: Response<CategoriaPucResponse>?) {
                if (response != null && response.code() == 200) {
                    val categorias: MutableList<CategoriaPuk> = response.body()?.value!!
                    /*val fastStoreModelTransactionCategorias=FastStoreModelTransaction
                            .insertBuilder(FlowManager.getModelAdapter(CategoriaPuk::class.java))
                            .addAll(categorias)
                            .build()
                    val databaseCategorias = FlowManager.getDatabase(DataSource::class.java)
                    val transactionCategorias = databaseCategorias.beginTransactionAsync(fastStoreModelTransactionCategorias).build();
                    transactionCategorias.execute()*/
                    for (item in categorias) {
                        /*val fastStoreModelTransactionDetalleCategorias=FastStoreModelTransaction
                                .insertBuilder(FlowManager.getModelAdapter(Puk::class.java))
                                .addAll(item.Pucs)
                                .build()
                        val databasedetalleCategorias = FlowManager.getDatabase(DataSource::class.java)
                        val transaction = databasedetalleCategorias.beginTransactionAsync(fastStoreModelTransactionDetalleCategorias).build();
                        transaction.execute()*/
                        item.save()
                        for (puk in item.Pucs!!) {
                            puk.save()
                        }
                    }

                    Log.d("SYNC DATA", "Categorias Puk Loaded" )
                    getEstadosTransaccion()

                } else {
                    postEventError(RequestEventMainMenu.ERROR_EVENT, "Comprueba tu conexión a Internet")
                }
            }
            override fun onFailure(call: Call<CategoriaPucResponse>?, t: Throwable?) {
                postEventError(RequestEventMainMenu.ERROR_EVENT, "Comprueba tu conexión a Internet")
            }
        })
    }


    fun getEstadosTransaccion(){
        //Estados de  transaccion
        val estadoTransaccion = SQLite.select().from(Estado_Transaccion::class.java).queryList()
        if(estadoTransaccion.size>0){

        }else{
            val estadosTransaccion = apiService?.getEstadosTransaccion()
            estadosTransaccion?.enqueue(object : Callback<EstadoTransaccionResponse> {
                override fun onResponse(call: Call<EstadoTransaccionResponse>?, response: Response<EstadoTransaccionResponse>?) {
                    if (response != null && response.code() == 200) {

                        val estadostransaccion: MutableList<Estado_Transaccion> = response.body()?.value!!
                        /*
                        val fastStoreModelTransactionEstadoTransaccion=FastStoreModelTransaction
                                .insertBuilder(FlowManager.getModelAdapter(Estado_Transaccion::class.java))
                                .addAll(estadostransaccion)
                                .build()
                        val databaseEstadotransacion = FlowManager.getDatabase(DataSource::class.java)
                        val transactionCategorias = databaseEstadotransacion.beginTransactionAsync(fastStoreModelTransactionEstadoTransaccion).build();
                        transactionCategorias.execute()*/
                        for (item in estadostransaccion) {
                            item.save()
                        }
                        Log.d("SYNC DATA", "Estados Transaccion  Loaded" )
                        getdetalleProducto()
                    } else {
                        postEventError(RequestEventMainMenu.ERROR_EVENT, "Comprueba tu conexión a Internet")
                    }
                }
                override fun onFailure(call: Call<EstadoTransaccionResponse>?, t: Throwable?) {
                    postEventError(RequestEventMainMenu.ERROR_EVENT, "Comprueba tu conexión a Internet")
                }
            })
        }
    }


    fun getdetalleProducto(){
        //val tipoProducto = SQLite.select().from(TipoProducto::class.java).queryList()
        val callTipoProducto = apiService?.getTipoAndDetalleTipoProducto()
        callTipoProducto?.enqueue(object : Callback<TipoProductoResponse> {
            override fun onResponse(call: Call<TipoProductoResponse>?, response: Response<TipoProductoResponse>?) {
                if (response != null && response.code() == 200) {
                    val tiposProducto = response.body()?.value as MutableList<TipoProducto>

                    for (item in tiposProducto) {
                        try {
                            val base64String = item.Icono
                            val base64Image = base64String?.split(",".toRegex())?.dropLastWhile { it.isEmpty() }!!.toTypedArray()[1]
                            val byte = Base64.decode(base64Image, Base64.DEFAULT)
                            item.Imagen = Blob(byte)
                        }catch (ex:Exception){
                            val ss= ex.toString()
                            Log.d("Convert Image", "defaultValue = " + ss);
                        }

                        item.save()

                        /*val fastStoreModelTransactionDetalleTipoProducto=FastStoreModelTransaction
                                .insertBuilder(FlowManager.getModelAdapter(DetalleTipoProducto::class.java))
                                .addAll(item.DetalleTipoProductos)
                                .build()
                        val databaseDetalleTipoProducto = FlowManager.getDatabase(DataSource::class.java)
                        val transactionDetalleTipoProducto = databaseDetalleTipoProducto.beginTransactionAsync(fastStoreModelTransactionDetalleTipoProducto).build();
                        transactionDetalleTipoProducto.execute()*/
                        for (detalleTipoProducto in item.DetalleTipoProductos!!) {
                            detalleTipoProducto.save()
                        }
                    }

                    Log.d("SYNC DATA", "Detalle Productos  Loaded" )
                    loadDepartmentsCities()
                } else {
                    postEventError(RequestEventMainMenu.ERROR_EVENT, "Comprueba tu conexión a Internet")
                }
            }
            override fun onFailure(call: Call<TipoProductoResponse>?, t: Throwable?) {
                postEventError(RequestEventMainMenu.ERROR_EVENT, "Comprueba tu conexión a Internet")
            }
        })
    }

    fun loadDepartmentsCities(){
        //Departamentos y Ciudades
        val call = apiService?.getDepartamentos()
        call?.enqueue(object : Callback<DeparmentsResponse> {
                override fun onResponse(call: Call<DeparmentsResponse>?, response: Response<DeparmentsResponse>?) {
                    if (response != null && response.code() == 200) {
                        val departamentos: MutableList<Departamento> = response.body()?.value!!

                        SQLite.select()
                                .from(Departamento::class.java)
                                .queryList().forEach{it.delete()}

                        SQLite.select()
                                .from(Ciudad::class.java)
                                .queryList().forEach{it.delete()}



                        val fastStoreModelTransactionDepartamentos=FastStoreModelTransaction
                                .insertBuilder(FlowManager.getModelAdapter(Departamento::class.java))
                                .addAll(departamentos)
                                .build()
                        val databaseDepart = FlowManager.getDatabase(DataSource::class.java)
                         val transactionDepartamentos = databaseDepart.beginTransactionAsync(fastStoreModelTransactionDepartamentos).build();
                        transactionDepartamentos.execute()
                        for (item in departamentos) {
                            val municipe=FastStoreModelTransaction
                                    .insertBuilder(FlowManager.getModelAdapter(Ciudad::class.java))
                                    .addAll(item.ciudades)
                                    .build()
                            val databaseCiudad = FlowManager.getDatabase(DataSource::class.java)
                            val transactionCiudad = databaseCiudad.beginTransactionAsync(municipe).build();
                            transactionCiudad.execute()
                            //item.save()
                            /* for (ciudad in item.ciudades!!) {
                                ciudad.save()
                            }*/
                        }
                        Log.d("SYNC DATA", "Departamentos y Ciudades  Loaded" )
                        loadCategoriasMedidas()
                    } else {
                       // postEventError(RequestEventMainMenu.ERROR_EVENT, "Comprueba tu conexión a Internet")
                        Log.d("ERROR SYNC DATA", "ERROR Departamentos y Ciudades  Loaded" )
                    }
                }
                override fun onFailure(call: Call<DeparmentsResponse>?, t: Throwable?) {
                    //postEventError(RequestEventMainMenu.ERROR_EVENT, "Comprueba tu conexión a Internet")
                    Log.d("ERROR SYNC DATA", "ERROR Departamentos y Ciudades  Loaded" )
                }
        })
    }

    fun loadCategoriasMedidas(){
        //Categorías Medida
        val callCategoriaMedida = apiService?.getCategoriasMedida()
        callCategoriaMedida?.enqueue(object : Callback<CategoriaMedidaResponse> {
            override fun onResponse(call: Call<CategoriaMedidaResponse>?, response: Response<CategoriaMedidaResponse>?) {
                if (response != null && response.code() == 200) {
                    val categoriasMedida = response.body()?.value as MutableList<CategoriaMedida>


                    /*val fastStoreModelTransactionCategoriaMedida=FastStoreModelTransaction
                            .insertBuilder(FlowManager.getModelAdapter(CategoriaMedida::class.java))
                            .addAll(categoriasMedida)
                            .build()

                    val databaseCategoriaMedida = FlowManager.getDatabase(DataSource::class.java)
                    val transactionCategoriasMedidas = databaseCategoriaMedida.beginTransactionAsync(fastStoreModelTransactionCategoriaMedida).build();
                    transactionCategoriasMedidas.execute()*/
                    for (item in categoriasMedida) {
                        /*val fastStoreModelTransactionUnidadMedidas=FastStoreModelTransaction
                                .insertBuilder(FlowManager.getModelAdapter(Unidad_Medida::class.java))
                                .addAll(item.UnidadMedidas)
                                .build()
                        val databaseUnidadMedidas = FlowManager.getDatabase(DataSource::class.java)
                        val transactionUnidadMedidas = databaseUnidadMedidas.beginTransactionAsync(fastStoreModelTransactionUnidadMedidas).build();
                        transactionUnidadMedidas.execute()*/
                        item.save()
                        for (itemUnidadmedida in item.UnidadMedidas!!){
                            itemUnidadmedida.save()
                        }
                    }

                    Log.d("SYNC DATA", "Categorias  y Unidades de Medida  Loaded" )
                    loadEstadoOfertas()

                } else {
                    postEventError(RequestEventMainMenu.ERROR_EVENT, "Comprueba tu conexión a Internet")
                }
            }
            override fun onFailure(call: Call<CategoriaMedidaResponse>?, t: Throwable?) {
                postEventError(RequestEventMainMenu.ERROR_EVENT, "Comprueba tu conexión a Internet")
            }
        })

    }

    fun loadEstadoOfertas(){
        //Calidades de Producto
        val callEstadoOfertas = apiService?.getEstadosOfertas()
        callEstadoOfertas?.enqueue(object : Callback<EstadoOfertaResponse> {
            override fun onResponse(call: Call<EstadoOfertaResponse>?, response: Response<EstadoOfertaResponse>?) {
                if (response != null && response.code() == 200) {
                    val listEstadosOfertas = response.body()?.value as MutableList<EstadoOferta>
                   /* val fastStoreModelTransactionEstadoOfertas=FastStoreModelTransaction
                            .insertBuilder(FlowManager.getModelAdapter(EstadoOferta::class.java))
                            .addAll(listEstadosOfertas)
                            .build()
                    val databaseEstadoOferta = FlowManager.getDatabase(DataSource::class.java)
                    val transactionEstadoOferta = databaseEstadoOferta.beginTransactionAsync(fastStoreModelTransactionEstadoOfertas).build();
                    transactionEstadoOferta.execute()*/
                    for (item in listEstadosOfertas) {
                        item.save()
                    }

                    Log.d("SYNC DATA", "Estados de Oferta  Loaded" )
                    getCalidadesProductos()
                } else {
                    postEventError(RequestEventMainMenu.ERROR_EVENT, "Comprueba tu conexión a Internet")
                }
            }
            override fun onFailure(call: Call<EstadoOfertaResponse>?, t: Throwable?) {
                postEventError(RequestEventMainMenu.ERROR_EVENT, "Comprueba tu conexión a Internet")
            }
        })
    }


    fun getCalidadesProductos(){
        //Calidades de Producto
        val callCalidadProducto = apiService?.getCalidadesProducto()
        callCalidadProducto?.enqueue(object : Callback<CalidadProductoResponse> {
            override fun onResponse(call: Call<CalidadProductoResponse>?, response: Response<CalidadProductoResponse>?) {
                if (response != null && response.code() == 200) {
                    val calidadesProducto = response.body()?.value as MutableList<CalidadProducto>
                    for (item in calidadesProducto) {
                        item.save()
                    }
                    Log.d("SYNC DATA", "Calidades de producto  Loaded" )
                } else {
                    postEventError(RequestEventMainMenu.ERROR_EVENT, "Comprueba tu conexión a Internet")
                }
            }
            override fun onFailure(call: Call<CalidadProductoResponse>?, t: Throwable?) {
                postEventError(RequestEventMainMenu.ERROR_EVENT, "Comprueba tu conexión a Internet")
            }
        })
    }

    fun loadTransacciones(usuario: Usuario?) {
        //Sinc transacciones
        val queryTransacciones = ListasResources.queryGeneral("userId",usuario?.Id.toString())
        val callInformacionTransaccionesSinronized = apiService?.getSyncInformacionUsuarioTransacciones(queryTransacciones)
        callInformacionTransaccionesSinronized?.enqueue(object : Callback<GetSincronizacionTransacciones> {
            override fun onResponse(call: Call<GetSincronizacionTransacciones>?, response: Response<GetSincronizacionTransacciones>?) {
                if (response != null && response.code() == 200) {
                    val transacciones = response.body()?.value as MutableList<Transaccion>

                    SQLite.select()
                            .from(Transaccion::class.java)
                            .where(Transaccion_Table.Estado_Sincronizacion.eq(true))
                            .and(Transaccion_Table.UsuarioId.eq(usuario?.Id))
                            .and(Transaccion_Table.Estado_SincronizacionUpdate.eq(true))
                            .queryList().forEach{it.delete()}


                    SQLite.select()
                            .from(Tercero::class.java)
                            .where(Tercero_Table.Estado_Sincronizacion.eq(true))
                            .and(Tercero_Table.Usuario_Id.eq(usuario?.Id))
                            .and(Tercero_Table.Estado_SincronizacionUpdate.eq(true))
                            .queryList().forEach{it.delete()}


                    for (item in transacciones) {

                        val transaccionVerficateSave= SQLite.select()
                                .from(Transaccion::class.java)
                                .where(Transaccion_Table.Id_Remote.eq(item.Id_Remote))
                                .and(Transaccion_Table.Estado_SincronizacionUpdate.eq(false))
                                .querySingle()

                        //TODO Verifica si tiene pendiente actualizacion por sincronizar
                        if (transaccionVerficateSave!=null){
                            item.TransaccionId=transaccionVerficateSave.TransaccionId
                        }else{
                            val lastTransaccion = getLastTransaccion(null)
                            if (lastTransaccion == null) {
                                item.TransaccionId = 1
                            } else {
                                item.TransaccionId = lastTransaccion.TransaccionId!! + 1
                            }

                            item.UsuarioId=usuario?.Id
                            item.Nombre_Tercero= if (item.Tercero!=null) item.Tercero?.Nombre else null
                            item.Nombre_Estado_Transaccion= if (item.EstadoTransaccion!=null) item.EstadoTransaccion?.Nombre else null
                            item.Estado_Sincronizacion=true
                            item.Identificacion_Tercero= if (item.Tercero!=null) item.Tercero?.NitRut else null
                            item.Descripcion_Puk=if (item.Puc!=null) item.Puc?.Descripcion else null
                            item.CategoriaPuk_Id=if (item.Puc!=null) item.Puc?.CategoriaId else null
                            item.Estado_Sincronizacion=true
                            item.Estado_SincronizacionUpdate=true
                            val fechaDate=item.getFechaDate(item.FechaString)
                            item.Fecha_Transaccion=fechaDate

                            val dateFechaFromatMMddyyy =item.getFechaTransacccionFormatMMddyyyy()
                            item.FechaString=dateFechaFromatMMddyyy

                            item.Valor_Unitario=if (item.Valor_Total!=null && item.Cantidad!=null)  item.Valor_Total!! / item.Cantidad?.toLong()!! else null

                            val cultivo =SQLite.select().from(Cultivo::class.java).where(Cultivo_Table.Id_Remote.eq(item.Cultivo_Id)).querySingle()
                            item.Nombre_Cultivo=cultivo?.Nombre
                            item.Nombre_Detalle_Producto_Cultivo=cultivo?.Nombre_Detalle_Tipo_Producto
                            item.Cultivo_Id=cultivo?.CultivoId

                            val terceroVerificateSave= SQLite.select()
                                    .from(Tercero::class.java)
                                    .where(Tercero_Table.Id_Remote.eq(item.TerceroId))
                                    .and(Tercero_Table.Estado_SincronizacionUpdate.eq(false))
                                    .querySingle()

                            if (terceroVerificateSave!=null){
                                item.Tercero?.TerceroId=terceroVerificateSave.TerceroId
                                item.TerceroId=terceroVerificateSave.TerceroId
                            }else{
                                val lastTercero = getLastTercero(null)
                                if (lastTercero == null) {
                                    item.Tercero?.TerceroId = 1
                                } else {
                                    item.Tercero?.TerceroId = lastTercero.TerceroId!! + 1
                                }

                                item.Tercero?.Estado_Sincronizacion=true
                                item.Tercero?.Estado_SincronizacionUpdate=true
                                item.TerceroId=item.Tercero?.TerceroId
                                item.Tercero?.save()

                            }

                            item.save()
                        }
                    }

                    Log.d("SYNC DATA", "Transacciones Loaded" )
                    loadProductos(usuario)
                } else {
                    postEventError(RequestEventMainMenu.ERROR_EVENT, "Comprueba tu conexión a Internet")
                }
            }
            override fun onFailure(call: Call<GetSincronizacionTransacciones>?, t: Throwable?) {
                postEventError(RequestEventMainMenu.ERROR_EVENT, "Comprueba tu conexión a Internet")
            }
        })
    }

    fun loadProductos(usuario:Usuario?){
        //Get Productos by user
        val queryProductos = ListasResources.queryGeneral("userId",usuario?.Id.toString())
        val callProductos = apiService?.getSyncProductos(queryProductos)
        callProductos?.enqueue(object : Callback<GetSynProductosUserResponse> {
            override fun onResponse(call: Call<GetSynProductosUserResponse>?, response: Response<GetSynProductosUserResponse>?) {
                if (response != null && response.code() == 200) {
                    val productos = response.body()?.value as MutableList<Producto>

                    SQLite.select()
                            .from(Producto::class.java)
                            .where(Producto_Table.userId.eq(usuario?.Id))
                            .and(Producto_Table.Estado_Sincronizacion.eq(true))
                            .and(Producto_Table.Estado_SincronizacionUpdate.eq(true))
                            .queryList().forEach{it.delete()}

                    for(producto in productos){

                        val cultivo= SQLite.select().from(Cultivo::class.java).where(Cultivo_Table.Id_Remote.eq(producto.cultivoId)).querySingle()


                        val productoVerficateSave= SQLite.select()
                                .from(Producto::class.java)
                                .where(Producto_Table.Id_Remote.eq(producto.Id_Remote))
                                .and(Producto_Table.Estado_SincronizacionUpdate.eq(false))
                                .querySingle()

                        //TODO Verifica si tiene pendiente actualizacion por sincronizar
                        if (productoVerficateSave!=null){
                            producto.ProductoId=productoVerficateSave.ProductoId
                        }else{

                            val last_producto = getLastProducto(null)
                            if (last_producto == null) {
                                producto.ProductoId = 1
                            } else {
                                producto.ProductoId = last_producto.ProductoId!! + 1
                            }

                            producto.NombreCultivo= if(producto.Cultivo!=null)producto.Cultivo?.Nombre else null
                            producto.NombreLote= if(producto.Cultivo!=null)producto.Cultivo?.Lote?.Nombre else null
                            producto.NombreUnidadProductiva= if(producto.Cultivo!=null)producto.Cultivo?.Lote?.UnidadProductiva?.nombre else null
                            producto.NombreUnidadMedidaCantidad=if(producto.UnidadMedida!= null)producto.UnidadMedida?.Descripcion else null
                            producto.NombreCalidad=if(producto.Calidad!=null)producto.Calidad?.Nombre else null
                            producto.NombreUnidadMedidaPrecio=producto.PrecioUnidadMedida
                            producto.Estado_Sincronizacion=true
                            producto.Estado_SincronizacionUpdate=true
                            producto.Usuario_Logued=usuario?.Id

                            producto.cultivoId=if(cultivo!=null)cultivo?.CultivoId else null

                            producto.NombreDetalleTipoProducto=if(producto.Cultivo!=null)producto.Cultivo?.detalleTipoProducto?.Nombre else null
                            /*
                            try {
                                val base64String = producto?.Imagen
                                val base64Image = base64String?.split(",".toRegex())?.dropLastWhile { it.isEmpty() }!!.toTypedArray()[1]
                                val byte = Base64.decode(base64Image, Base64.DEFAULT)
                                producto.blobImagen = Blob(byte)
                            }catch (ex:Exception){
                                var ss= ex.toString()
                                Log.d("Convert Image", "defaultValue = " + ss);
                            }

                            */

                            producto.save()
                        }
                    }
                    //    }
                  //  }
                    postEvent(RequestEventMainMenu.SYNC_FOTOS_PRODUCTOS, null,null,null)
                    Log.d("SYNC DATA", "Productos Loaded" )
                    loadOfertas(usuario)
                } else {
                    postEventError(RequestEventMainMenu.ERROR_EVENT, "Comprueba tu conexión a Internet")
                }
            }
            override fun onFailure(call: Call<GetSynProductosUserResponse>?, t: Throwable?) {
                postEventError(RequestEventMainMenu.ERROR_EVENT, "Comprueba tu conexión a Internet")
            }
        })
    }

    fun convertBitmapToByte(bitmapCompressed: Bitmap): ByteArray? {
        val stream = ByteArrayOutputStream()
        bitmapCompressed.compress(Bitmap.CompressFormat.JPEG, 80, stream)
        return stream.toByteArray()
        //return BitmapFactory.decodeByteArray(byteFormat, 0, byteFormat.size)
    }

    private fun loadOfertas(usuario: Usuario?) {
        var queryOfertas = ""
        var callOfertas: Call<OfertaResponse>? = null
        val orderDEsc = ListasResources.queryOrderByDesc("Id")
        if (usuario?.RolNombre.equals(RolResources.COMPRADOR)) {
            queryOfertas = ListasResources.queryGeneralWithContains("Oferta","UsuarioId", usuario?.Id.toString())
            callOfertas = apiService?.getOfertasComprador(queryOfertas, orderDEsc)
        } else {
            queryOfertas = ListasResources.queryGeneralWithContains("Oferta","usuarioto", usuario?.Id.toString())
            callOfertas = apiService?.getOfertasProductor(queryOfertas, orderDEsc)
        }

        callOfertas?.enqueue(object : Callback<OfertaResponse> {
            override fun onResponse(call: Call<OfertaResponse>?, response: Response<OfertaResponse>?) {
                if (response != null && response.code() == 200) {
                    if (usuario?.RolNombre.equals(RolResources.PRODUCTOR)) {
                        val listOferta = SQLite.select().from(Oferta::class.java).where(Oferta_Table.UsuarioTo.eq(usuario?.Id)).queryList()
                        for (oferta in listOferta) {
                            SQLite.select()
                                    .from(DetalleOferta::class.java)
                                    .where(DetalleOferta_Table.OfertasId.eq(oferta.Oferta_Id))
                                    .queryList().forEach{it.delete()}

                            oferta.delete()
                        }
                    }

                    val detalleOfertas = response.body()?.value as MutableList<DetalleOferta>
                    for (detalleoferta in detalleOfertas) {

                        val ofertaVerficateSave = SQLite.select()
                                .from(Oferta::class.java)
                                .where(Oferta_Table.Id_Remote.eq(detalleoferta.Oferta?.Id_Remote))
                                .querySingle()
                        //TODO Verifica si tiene pendiente actualizacion por sincronizar
                        if (ofertaVerficateSave != null) {
                            detalleoferta.Oferta?.Oferta_Id = ofertaVerficateSave.Oferta_Id
                        } else {
                            val last_oferta = getLastOferta()
                            if (last_oferta == null) {
                                detalleoferta.Oferta?.Oferta_Id = 1
                            } else {
                                detalleoferta.Oferta?.Oferta_Id = last_oferta.Oferta_Id!! + 1
                            }
                        }
                        detalleoferta.Oferta!!.CreatedOnLocal = detalleoferta.Oferta!!.getFechaDate(detalleoferta.Oferta!!.CreatedOn)
                        detalleoferta.Oferta!!.UpdatedOnLocal = detalleoferta.Oferta!!.getFechaDate(detalleoferta.Oferta!!.UpdatedOn)
                        detalleoferta.Oferta!!.Nombre_Estado_Oferta = if (detalleoferta.Oferta!!.Estado_Oferta != null) detalleoferta.Oferta!!.Estado_Oferta?.Nombre else null
                        detalleoferta.Oferta!!.save()


                        val detalleOfertaVerficateSave = SQLite.select()
                                .from(DetalleOferta::class.java)
                                .where(DetalleOferta_Table.Id_Remote.eq(detalleoferta.Id_Remote))
                                .querySingle()
                        if (detalleOfertaVerficateSave != null) {
                            detalleoferta.Detalle_Oferta_Id = detalleOfertaVerficateSave.Detalle_Oferta_Id
                        } else {
                            val last_detalle_oferta = getLastDetalleOferta()
                            if (last_detalle_oferta == null) {
                                detalleoferta.Detalle_Oferta_Id = 1
                            } else {
                                detalleoferta.Detalle_Oferta_Id = last_detalle_oferta.Detalle_Oferta_Id!! + 1
                            }
                        }
                        detalleoferta.NombreUnidadMedidaPrecio = if (detalleoferta.UnidadMedida != null) detalleoferta.UnidadMedida?.Descripcion else null
                        detalleoferta.OfertasId = detalleoferta.Oferta?.Oferta_Id
                        detalleoferta.Detalle_Oferta_Id = detalleOfertaVerficateSave?.Detalle_Oferta_Id
                        detalleoferta.save()

                        if (usuario?.RolNombre.equals(RolResources.COMPRADOR)) {
                            //UserTo
                            val usuario = detalleoferta?.Producto?.Cultivo?.Lote?.UnidadProductiva?.Usuario
                            if (usuario != null) {
                                usuario.save()
                            }

                            //TODO Unidades Productivas
                            val unidaProductiva = detalleoferta.Producto?.Cultivo?.Lote?.UnidadProductiva
                            if (unidaProductiva != null) {
                                val unidadProductivaVerficateSave = SQLite.select()
                                        .from(Unidad_Productiva::class.java)
                                        .where(Unidad_Productiva_Table.Id_Remote.eq(unidaProductiva.Id_Remote))
                                        .querySingle()
                                //TODO Verifica si ya existe
                                if (unidadProductivaVerficateSave != null) {
                                    unidaProductiva.Unidad_Productiva_Id = unidadProductivaVerficateSave?.Unidad_Productiva_Id
                                } else {
                                    val last_up = getLastUp(null)
                                    if (last_up == null) {
                                        unidaProductiva.Unidad_Productiva_Id = 1
                                    } else {
                                        unidaProductiva.Unidad_Productiva_Id = last_up.Unidad_Productiva_Id!! + 1
                                    }
                                }
                                unidaProductiva.UsuarioId = usuario?.Id
                                unidaProductiva.Nombre_Ciudad = if (unidaProductiva.Ciudad != null) unidaProductiva.Ciudad?.Nombre else null
                                unidaProductiva.Nombre_Unidad_Medida = if (unidaProductiva.UnidadMedida != null) unidaProductiva.UnidadMedida?.Descripcion else null
                                unidaProductiva.Nombre_Departamento = if (unidaProductiva.Ciudad != null) unidaProductiva.Ciudad?.Departamento?.Nombre else null
                                unidaProductiva.save()
                            }


                            //TODO Lote
                            val lote = detalleoferta.Producto?.Cultivo?.Lote
                            if (lote != null) {
                                val loteVerficateSave = SQLite.select()
                                        .from(Lote::class.java)
                                        .where(Lote_Table.Id_Remote.eq(lote.Id_Remote))
                                        .querySingle()
                                //TODO Verifica si ya existe
                                if (loteVerficateSave != null) {
                                    lote.LoteId = loteVerficateSave.LoteId
                                } else {
                                    val last_lote = getLastLote(null)
                                    if (last_lote == null) {
                                        lote.LoteId = 1
                                    } else {
                                        lote.LoteId = last_lote.LoteId + 1
                                    }
                                }

                                val coordenadas = lote.Localizacion
                                if (coordenadas != null) {
                                    val separated = coordenadas.split("/".toRegex()).dropLastWhile { it.isEmpty() }.toTypedArray()
                                    val latitud = separated!![0].toDoubleOrNull() // this will contain "Fruit"
                                    val longitud = separated!![1].toDoubleOrNull() // this will contain " they taste good"
                                    lote.Latitud = latitud
                                    lote.Longitud = longitud
                                    lote.Coordenadas = coordenadas
                                }

                                lote.Unidad_Productiva_Id = unidaProductiva?.Unidad_Productiva_Id
                                //lote.Nombre_Unidad_Medida= if (lote.UnidadMedida!=null) lote.UnidadMedida?.Descripcion else null
                                lote.Nombre_Unidad_Productiva = unidaProductiva?.nombre
                                lote.Nombre = if (lote.Nombre == null) "" else lote.Nombre
                                lote.Descripcion = if (lote.Descripcion == null) "" else lote.Descripcion
                                // lote.EstadoSincronizacion=true
                                // lote.Estado_SincronizacionUpdate=true
                                lote.save()
                            }

                            //TODO Cultivo
                            val cultivo = detalleoferta.Producto?.Cultivo
                            if (cultivo != null) {
                                val cultivoVerficateSave = SQLite.select()
                                        .from(Cultivo::class.java)
                                        .where(Cultivo_Table.Id_Remote.eq(cultivo.Id_Remote))
                                        .querySingle()
                                //TODO Verifica si tiene pendiente actualizacion por sincronizar
                                if (cultivoVerficateSave != null) {
                                    cultivo.CultivoId = cultivoVerficateSave.CultivoId
                                } else {
                                    val last_cultivo = getLastCultivo(null)
                                    if (last_cultivo == null) {
                                        cultivo.CultivoId = 1
                                    } else {
                                        cultivo.CultivoId = last_cultivo.CultivoId!! + 1
                                    }
                                }

                                cultivo.LoteId = lote?.LoteId
                                cultivo.NombreUnidadProductiva = unidaProductiva?.nombre
                                cultivo.NombreLote = lote?.Nombre
                                cultivo.EstadoSincronizacion = true
                                cultivo.Estado_SincronizacionUpdate = true
                                cultivo.stringFechaInicio = cultivo.getFechaStringFormatApi(cultivo.FechaIncio)
                                cultivo.stringFechaFin = cultivo.getFechaStringFormatApi(cultivo.FechaFin)

                                //cultivo.Nombre_Tipo_Producto= if (detalleTipoProducto.tipoProducto!=null) detalleTipoProducto?.tipoProducto?.Nombre else null
                                //cultivo.Nombre_Detalle_Tipo_Producto=detalleTipoProducto.Nombre
                                //cultivo.Id_Tipo_Producto= detalleTipoProducto.TipoProductoId
                                //cultivo.Nombre_Unidad_Medida=if (cultivo.unidadMedida!=null) cultivo.unidadMedida?.Descripcion else null
                                //cultivo.EstadoSincronizacion=true
                                //cultivo.Estado_SincronizacionUpdate=true
                                cultivo.save()
                            }

                            //TODO Producto
                            val producto = detalleoferta.Producto
                            if (producto != null) {
                                val productoVerficateSave = SQLite.select()
                                        .from(Producto::class.java)
                                        .where(Producto_Table.Id_Remote.eq(producto.Id_Remote))
                                        .querySingle()

                                if (productoVerficateSave != null) {
                                    producto.ProductoId = productoVerficateSave.ProductoId
                                } else {
                                    val last_producto = getLastProducto(null)
                                    if (last_producto == null) {
                                        producto.ProductoId = 1
                                    } else {
                                        producto.ProductoId = last_producto.ProductoId!! + 1
                                    }
                                }
                                producto.EmailProductor = usuario?.Email
                                producto.NombreProductor = "${usuario?.Nombre} ${usuario?.Apellidos}"
                                producto.CodigoUp = unidaProductiva?.Unidad_Productiva_Id.toString()
                                producto.Ciudad = unidaProductiva?.Nombre_Ciudad
                                producto.Departamento = unidaProductiva?.Nombre_Departamento
                                //producto.TipoProductoId=detalleTipoProducto.TipoProductoId
                                producto.NombreCultivo = cultivo?.Nombre
                                producto.NombreLote = if (lote != null) lote.Nombre else null
                                producto.NombreUnidadProductiva = if (unidaProductiva != null) unidaProductiva.nombre else null
                                producto.NombreUnidadMedidaCantidad = if (producto.UnidadMedida != null) producto.UnidadMedida?.Descripcion else null
                                producto.NombreCalidad = if (producto.Calidad != null) producto.Calidad?.Nombre else null
                                producto.NombreUnidadMedidaPrecio = producto.PrecioUnidadMedida
                                producto.Usuario_Logued = usuario?.Id
                                //producto.NombreDetalleTipoProducto=detalleTipoProducto.Nombre
                                //producto.Estado_Sincronizacion=true
                                //producto.Estado_SincronizacionUpdate=true
                                /*try {
                                    val base64String = producto?.Imagen
                                    val base64Image = base64String?.split(",".toRegex())?.dropLastWhile { it.isEmpty() }!!.toTypedArray()[1]
                                    val byte = Base64.decode(base64Image, Base64.DEFAULT)
                                    producto.blobImagen = Blob(byte)
                                } catch (ex: Exception) {
                                    var ss = ex.toString()
                                    Log.d("Convert Image", "defaultValue = " + ss);
                                }*/
                                producto.save()
                            }
                        } else if (usuario?.RolNombre.equals(RolResources.PRODUCTOR)) {
                            if (detalleoferta.Oferta?.Usuario != null) {
                                /*if(oferta.Usuario?.Fotopefil!=null){
                                    try {
                                        val base64String = oferta.Usuario?.Fotopefil
                                        val base64Image = base64String?.split(",".toRegex())?.dropLastWhile { it.isEmpty() }!!.toTypedArray()[1]
                                        val byte = Base64.decode(base64Image, Base64.DEFAULT)
                                        oferta.Usuario?.blobImagenUser = Blob(byte)
                                    }catch (ex:Exception){
                                        var ss= ex.toString()
                                        Log.d("Convert Image", "defaultValue = " + ss);
                                    }
                                }*/
                                detalleoferta.Oferta?.Usuario!!.save()
                            }
                        }
                    }


                    if (usuario?.RolNombre.equals(RolResources.PRODUCTOR)) {
                        Log.d("SYNC DATA", "Ofertas Loaded" )
                        getCategoriaPuk()
                    }else if (usuario?.RolNombre.equals(RolResources.COMPRADOR)) {
                        Log.d("SYNC DATA", "Ofertas Loaded" )
                        getdetalleProducto()
                    }

                } else {
                    postEventError(RequestEventMainMenu.ERROR_EVENT, "Comprueba tu conexión a Internet")
                }
            }
            override fun onFailure(call: Call<OfertaResponse>?, t: Throwable?) {
                postEventError(RequestEventMainMenu.ERROR_EVENT, "Comprueba tu conexión a Internet")
            }
        })
    }


    //region Events
    private fun postEventOk(type: Int) {
        postEvent(type, null,null,null)
    }

    private fun postEventOkQuntitySync(type: Int,  quantitySync: QuantitySync?) {
        var QuantitySyncMutable:Object?=null
        if(quantitySync!=null){
            QuantitySyncMutable = quantitySync as Object
        }
        postEvent(type, null,QuantitySyncMutable,null)
    }

    private fun postEventError(type: Int,messageError:String?) {
        postEvent(type, null,null,messageError)
    }

    //Main Post Event
    private fun postEvent(type: Int, listModel1:MutableList<Object>?,model:Object?,errorMessage: String?) {
        val event = RequestEventMainMenu(type, listModel1, model, errorMessage)
        event.eventType = type
        eventBus?.post(event)
    }
    //endregion
    //endregion
}