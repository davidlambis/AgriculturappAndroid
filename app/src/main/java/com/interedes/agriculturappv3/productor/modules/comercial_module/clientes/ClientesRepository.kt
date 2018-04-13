package com.interedes.agriculturappv3.productor.modules.comercial_module.clientes

import com.interedes.agriculturappv3.R.string.venta
import com.interedes.agriculturappv3.libs.EventBus
import com.interedes.agriculturappv3.libs.GreenRobotEventBus
import com.interedes.agriculturappv3.productor.models.usuario.Usuario
import com.interedes.agriculturappv3.productor.modules.comercial_module.clientes.events.ClientesEvent
import com.interedes.agriculturappv3.services.listas.Listas
import com.raizlabs.android.dbflow.sql.language.SQLite


class ClientesRepository : IClientes.Repository {


    var eventBus: EventBus? = null

    init {
        eventBus = GreenRobotEventBus()
    }

    override fun getListClientes() {
        //TODO traer clientes por usuario productor(logueado)
        //val lista_clientes = SQLite.select().from(Usuario::class.java).queryList()
        val lista_clientes = Listas.listUsuarios()
        postEventOk(ClientesEvent.READ_EVENT, lista_clientes, null)
    }


    //region Eventos
    private fun postEventOk(type: Int, clientes: List<Usuario>?, cliente: Usuario?) {
        var clientesListMitable = clientes as MutableList<Object>
        var clienteMutable: Object? = null
        if (cliente != null) {
            clienteMutable = venta as Object
        }
        postEvent(type, clientesListMitable, clienteMutable, null)
    }

    //Main Post Event
    private fun postEvent(type: Int, listModel1: MutableList<Object>?, model: Object?, errorMessage: String?) {
        val event = ClientesEvent(type, listModel1, model, errorMessage)
        event.eventType = type
        eventBus?.post(event)
    }

    //endregion

}