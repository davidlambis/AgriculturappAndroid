package com.interedes.agriculturappv3.modules.account

import com.google.firebase.auth.FirebaseAuth
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
    private var mCurrentUserID: String? = null

    init {
        eventBus = GreenRobotEventBus()
        apiService = ApiInterface.create()
        mCurrentUserID = FirebaseAuth.getInstance().currentUser!!.uid
        mUserDBRef = FirebaseDatabase.getInstance().reference.child("Users")
        mStorageRef = FirebaseStorage.getInstance().reference.child("Photos").child("Users")
    }

    override fun getUserLogued():Usuario?{
        val usuarioLogued = SQLite.select().from(Usuario::class.java).where(Usuario_Table.UsuarioRemembered.eq(true)).querySingle()
        return usuarioLogued
    }

    override fun updateUserLogued(usuario:Usuario?,checkConection:Boolean)
    {
        //ACCOUNT COMPRADOR
        /*------------------------------------------------------------------------------------------------------------*/
        if(usuario?.RolNombre.equals(RolResources.COMPRADOR)){
            if(checkConection){
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
                            usuario?.Estado_SincronizacionUpdate=true
                            usuario?.update()
                            updateUser(usuario?.Nombre,usuario?.Apellidos,usuario?.Identificacion,usuario?.PhoneNumber)
                            postEventOk(RequestEventAccount.UPDATE_EVENT,null,null)
                        } else {
                            postEventError(RequestEventAccount.ERROR_EVENT, "Comprueba tu conexión")
                        }
                    }
                    override fun onFailure(call: Call<PostUsuario>?, t: Throwable?) {
                        postEventError(RequestEventAccount.ERROR_EVENT, "Comprueba tu conexión")
                    }
                })
            }else{
                postEventError(RequestEventAccount.ERROR_VERIFICATE_CONECTION,null)
            }
        }

        ///ACCOUNT PRODUCTOR
        /*------------------------------------------------------------------------------------------------------------*/
        else{
            if(checkConection){
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
                            usuario?.Estado_SincronizacionUpdate=true
                            usuario?.update()
                            updateUser(usuario?.Nombre,usuario?.Apellidos,usuario?.Identificacion,usuario?.PhoneNumber)
                            postEventOk(RequestEventAccount.UPDATE_EVENT,null,null)
                        } else {
                            postEventError(RequestEventAccount.ERROR_EVENT, "Comprueba tu conexión")
                        }
                    }
                    override fun onFailure(call: Call<PostUsuario>?, t: Throwable?) {
                        postEventError(RequestEventAccount.ERROR_EVENT, "Comprueba tu conexión")
                    }
                })
            }else{
                usuario?.Estado_SincronizacionUpdate=false
                usuario?.update()
                postEventOk(RequestEventAccount.UPDATE_EVENT,null,null)
            }
        }
    }

    private fun updateUser(newDisplayName: String?, newLastName: String?,newIdentificacion: String?,phone:String?) {
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