package com.interedes.agriculturappv3.modules.account

import android.util.Log
import com.google.firebase.FirebaseException
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import com.interedes.agriculturappv3.libs.EventBus
import com.interedes.agriculturappv3.libs.GreenRobotEventBus
import com.interedes.agriculturappv3.modules.account.events.RequestEventAccount
import com.interedes.agriculturappv3.modules.models.detalle_metodo_pago.DetalleMetodoPago
import com.interedes.agriculturappv3.modules.models.metodopago.MetodoPago
import com.interedes.agriculturappv3.modules.models.usuario.PostUsuario
import com.interedes.agriculturappv3.modules.models.usuario.Usuario
import com.interedes.agriculturappv3.modules.models.usuario.Usuario_Table
import com.interedes.agriculturappv3.services.api.ApiInterface
import com.interedes.agriculturappv3.services.resources.RolResources
import com.raizlabs.android.dbflow.kotlinextensions.save
import com.raizlabs.android.dbflow.kotlinextensions.update
import com.raizlabs.android.dbflow.sql.language.SQLite
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.util.HashMap

class AccountRepository:IMainViewAccount.Repository {

    var eventBus: EventBus? = null
    var apiService: ApiInterface? = null
    //FIREBASE
    private var mUserDBRef: DatabaseReference? = null
    private var mStorageRef: StorageReference? = null
    var mAuth: FirebaseAuth? = null
    private var  mCurrentUserID: String? = null

    init {
        eventBus = GreenRobotEventBus()
        apiService = ApiInterface.create()
        mCurrentUserID = FirebaseAuth.getInstance().currentUser?.uid

        if(mCurrentUserID==null){
            mCurrentUserID=getUserLogued()?.IdFirebase
        }
        mAuth= FirebaseAuth.getInstance()
        mUserDBRef = FirebaseDatabase.getInstance().reference.child("Users")
        mStorageRef = FirebaseStorage.getInstance().reference.child("Photos").child("Users")
    }

    override fun verificateUserLoguedFirebaseFirebase(): FirebaseUser?
    {
       return  FirebaseAuth.getInstance().currentUser
    }

    override fun getUserLogued():Usuario?{
        val usuarioLogued = SQLite.select().from(Usuario::class.java).where(Usuario_Table.UsuarioRemembered.eq(true)).querySingle()
        return usuarioLogued
    }


    override fun changeFotoUserAccount(checkConction:Boolean){
        var usuarioLogued= getUserLogued()
        if(checkConction){
            if(verificateUserLoguedFirebaseFirebase()!=null){
                postEvent(RequestEventAccount.UPDATE_FOTO_ACCOUNT_EVENT, null,null,null)
            }else{
                loginFirebase(usuarioLogued,true)
            }
        }else{
            postEventError(RequestEventAccount.ERROR_VERIFICATE_CONECTION,null)
        }
    }


    override fun loginFirebase(usuario:Usuario?,isUpdatePhotoAccount:Boolean)
    {
        mAuth?.signInWithEmailAndPassword(usuario?.Email!!, usuario?.Contrasena!!)?.addOnCompleteListener({ task ->
            if (task.isSuccessful) {
                var mCurrentUserID =task.result.user.uid
                usuario.IdFirebase=mCurrentUserID
                usuario.save()

                if(isUpdatePhotoAccount){
                    postEvent(RequestEventAccount.UPDATE_FOTO_ACCOUNT_EVENT, null,null,null)
                }else{
                    updateUserRemote(usuario)
                }
            } else {
                try {
                    throw task.exception!!
                } catch (firebaseException: FirebaseException) {
                    postEventError(RequestEventAccount.ERROR_EVENT, firebaseException.toString())
                    Log.e("Error Post", firebaseException.toString())
                }
            }
        })
    }


    fun updateUserRemote(usuario: Usuario?){
        val postUsuario = PostUsuario(usuario?.Id,
                usuario?.Apellidos,
                usuario?.Nombre,
                usuario?.DetalleMetodoPagoId,
                usuario?.Email,
                usuario?.EmailConfirmed,
                usuario?.Estado,
                usuario?.FechaRegistro,
                usuario?.Fotopefil,
                usuario?.Identificacion,
                usuario?.Nro_movil,
                usuario?.NumeroCuenta,
                usuario?.PhoneNumber,
                usuario?.PhoneNumberConfirmed,
                usuario?.RolId,
                usuario?.UserName)
        val call = apiService?.updateUsuario(postUsuario, usuario?.Id!!)
        call?.enqueue(object : Callback<PostUsuario> {
            override fun onResponse(call: Call<PostUsuario>?, response: Response<PostUsuario>?) {
                if (response != null && response.code() == 200) {
                    //usuario?.Estado_SincronizacionUpdate=true

                    var userResponse:PostUsuario? = response.body()

                    var lasLogued:Usuario?= getUserLogued()
                    lasLogued?.Estado_SincronizacionUpdate=true
                    lasLogued?.Nombre=userResponse?.Nombre
                    lasLogued?.Apellidos=userResponse?.Apellidos
                    lasLogued?.Identificacion=userResponse?.Identificacion
                   // lasLogued?.blobImagen=usuario?.blobImagen
                    lasLogued?.save()


                    //usuario?.save()

                    var lisLogued= SQLite.select().from(Usuario::class.java).queryList()
                    updateUserFirebase(usuario?.Nombre,usuario?.Apellidos,usuario?.Identificacion,usuario?.PhoneNumber)
                    postEventOk(RequestEventAccount.UPDATE_EVENT,null,null)
                } else {
                    postEventError(RequestEventAccount.ERROR_EVENT, "Comprueba tu conexión")
                }
            }
            override fun onFailure(call: Call<PostUsuario>?, t: Throwable?) {
                postEventError(RequestEventAccount.ERROR_EVENT, "Comprueba tu conexión")
            }
        })

    }

    override fun updateUserLogued(usuario:Usuario?,checkConection:Boolean)
    {
        //ACCOUNT COMPRADOR
        /*------------------------------------------------------------------------------------------------------------*/
        if(usuario?.RolNombre.equals(RolResources.COMPRADOR)){
            if(checkConection){
                if(verificateUserLoguedFirebaseFirebase()!=null){
                    updateUserRemote(usuario)
                }else{
                    loginFirebase(usuario,false)
                }
            }else{
                postEventError(RequestEventAccount.ERROR_VERIFICATE_CONECTION,null)
            }
        }


        ///ACCOUNT PRODUCTOR
        /*------------------------------------------------------------------------------------------------------------*/
        else{
            if(checkConection){
                if(verificateUserLoguedFirebaseFirebase()!=null){
                    updateUserRemote(usuario)
                }else{
                    loginFirebase(usuario,false)
                }
            }else{
                usuario?.Estado_SincronizacionUpdate=false
                usuario?.update()
                postEventOk(RequestEventAccount.UPDATE_EVENT,null,null)
            }
        }
    }

    private fun updateUserFirebase(newDisplayName: String?, newLastName: String?,newIdentificacion: String?,phone:String?) {
        val childUpdates = HashMap<String?, Any?>()
        childUpdates["nombre"] = newDisplayName
        childUpdates["apellido"] = newLastName
        childUpdates["cedula"] = newIdentificacion
        childUpdates["telefono"] = phone
        mUserDBRef?.child(mCurrentUserID)?.updateChildren(childUpdates)
    }
    //region Métodos Interfaz
    override fun getListas() {

        //cargar los métodos de pago con internet
        val metodosPagoList = SQLite.select().from(MetodoPago::class.java).queryList()
        val detalleMetodosPagoList = SQLite.select().from(DetalleMetodoPago::class.java).queryList()

        //Cargar los detalles por método de pago con internet
        postEventListMetodoPago(RequestEventAccount.LIST_EVENT_METODO_PAGO, metodosPagoList)
        postEventListDetalleMetodoPago(RequestEventAccount.LIST_EVENT_DETALLE_METODO_PAGO, detalleMetodosPagoList)
    }


    //region Events
    private fun postEventListMetodoPago(type: Int, listMetodosPago: List<MetodoPago>?) {
        val listMutable = listMetodosPago as MutableList<Object>
        postEvent(type, listMutable, null, null)
    }

    private fun postEventListDetalleMetodoPago(type: Int, listDetallePago: List<DetalleMetodoPago>?) {
        val listMutable = listDetallePago as MutableList<Object>
        postEvent(type, listMutable, null, null)
    }

    private fun postEventOk(type: Int, lisUsurio: List<Usuario>?, usuario: Usuario?) {
        val usuarioListMutable:MutableList<Object>? = null
        var usuarioMutable: Object? = null
        if (usuario != null) {
            usuarioMutable = usuario as Object
        }
        if (lisUsurio != null) {
            usuarioListMutable as MutableList<Object>
        }

        postEvent(type, usuarioListMutable, usuarioMutable, null)
    }

    private fun postEventError(type: Int,messageError:String?) {
        postEvent(type, null,null,messageError)
    }

    //Main Post Event
    private fun postEvent(type: Int, listModel1:MutableList<Object>?,model:Object?,errorMessage: String?) {
        val event = RequestEventAccount(type, listModel1, model, errorMessage)
        event.eventType = type
        eventBus?.post(event)
    }
    //endregion
    //endregion

}