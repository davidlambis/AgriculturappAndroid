package com.interedes.agriculturappv3.modules.productor.accounting_module.transacciones

import com.interedes.agriculturappv3.modules.models.ventas.Transaccion

class TransaccionInteractor: IMainViewTransacciones.Interactor {

    var repository: IMainViewTransacciones.Repository? = null

    init {
        this.repository = TransaccionRespository()
    }

    override fun registerTransaccion(transaccion: Transaccion, cultivo_id: Long?,checkConection:Boolean) {
        repository?.saveTransaccion(transaccion,cultivo_id,checkConection)
    }


    override fun updateTransaccion(transaccion: Transaccion, cultivo_id: Long?,checkConection:Boolean) {
        repository?.updateTransaccion(transaccion,cultivo_id,checkConection)
    }

    override fun deleteProducccionTransaccion(transaccion: Transaccion, cultivo_id: Long?,checkConection:Boolean) {
        repository?.deleteTransaccion(transaccion,cultivo_id,checkConection)
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