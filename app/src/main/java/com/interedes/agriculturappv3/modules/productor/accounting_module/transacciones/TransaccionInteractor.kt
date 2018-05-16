package com.interedes.agriculturappv3.modules.productor.accounting_module.transacciones

import com.interedes.agriculturappv3.modules.models.ventas.Transaccion

class TransaccionInteractor: IMainViewTransacciones.Interactor {

    var repository: IMainViewTransacciones.Repository? = null

    init {
        this.repository = TransaccionRespository()
    }

    override fun registerTransaccion(transaccion: Transaccion, cultivo_id: Long?) {
        repository?.saveTransaccion(transaccion,cultivo_id)
    }

    override fun registerTransaccionOnline(transaccion: Transaccion, cultivo_id: Long?) {
        repository?.saveTransaccionOnline(transaccion,cultivo_id)
    }

    override fun updateTransaccion(transaccion: Transaccion, cultivo_id: Long?) {
        repository?.updateTransaccion(transaccion,cultivo_id)
    }

    override fun deleteProducccionTransaccion(transaccion: Transaccion, cultivo_id: Long?) {
        repository?.deleteTransaccion(transaccion,cultivo_id)
    }

    override fun execute(cultivo_id: Long?,typeTransaccion:Long?) {
        repository?.getListTransacciones(cultivo_id,typeTransaccion)
    }

    override fun getListas() {
        repository?.getListas()
    }

    override fun getCultivo(cultivo_id: Long?) {
        repository?.getCultivo(cultivo_id)
    }
}