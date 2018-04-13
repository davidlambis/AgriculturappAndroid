package com.interedes.agriculturappv3.productor.modules.accounting_module.reportes

import java.util.*

class ReporteInteractor: IMainViewReportes.Interactor {

    var repository: IMainViewReportes.Repository? = null

    init {
        this.repository = ReporteRepository()
    }



    override fun getTotalTransacciones(cultivo_id: Long?, dateStart: Date?, dateEnd: Date?) {
        repository?.getTotalTransacciones(cultivo_id,dateStart,dateEnd)
    }

    override fun execute(cultivo_id: Long?, typeTransaccion: Long?) {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun getListas() {
        repository?.getListas()
    }

    override fun getCultivo(cultivo_id: Long?) {
        repository?.getCultivo(cultivo_id)
    }
}