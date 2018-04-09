package com.interedes.agriculturappv3.productor.modules.accounting_module

import com.interedes.agriculturappv3.productor.models.ventas.Transaccion

class VentasInteractor: IMainViewTransacciones.Interactor {

    var repository: IMainViewTransacciones.Repository? = null

    init {
        this.repository = VentasRespository()
    }

    override fun registerTransaccion(transaccion: Transaccion, cultivo_id: Long?) {
        repository?.saveTransaccion(transaccion,cultivo_id)
    }

    override fun updateTransaccion(transaccion: Transaccion, cultivo_id: Long?) {
        repository?.updateTransaccion(transaccion,cultivo_id)
    }

    override fun deleteProducccionTransaccion(transaccion: Transaccion, cultivo_id: Long?) {
        repository?.deleteTransaccion(transaccion,cultivo_id)
    }

    override fun execute(cultivo_id: Long?) {
        repository?.getListTransacciones(cultivo_id)
    }

    override fun getListas() {
        repository?.getListas()
    }

    override fun getCultivo(cultivo_id: Long?) {
        repository?.getCultivo(cultivo_id)
    }
}