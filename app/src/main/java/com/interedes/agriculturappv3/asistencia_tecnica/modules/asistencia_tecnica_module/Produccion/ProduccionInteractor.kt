package com.interedes.agriculturappv3.asistencia_tecnica.modules.asistencia_tecnica_module.Produccion

import com.interedes.agriculturappv3.asistencia_tecnica.models.produccion.Produccion

/**
 * Created by usuario on 20/03/2018.
 */
class ProduccionInteractor:IMainProduccion.Interactor {

    var repository: IMainProduccion.Repository? = null

    init {
        this.repository = ProduccionRepository()
    }

    override fun registerProduccion(produccion: Produccion, cultivo_id: Long) {
       repository?.saveProduccion(produccion,cultivo_id)
    }

    override fun updateProducccion(produccion: Produccion, cultivo_id: Long) {
        repository?.updateProduccion(produccion,cultivo_id)
    }

    override fun deleteProducccion(produccion: Produccion, cultivo_id: Long?) {
        repository?.deleteProduccion(produccion,cultivo_id)
    }

    override fun execute(cultivo_id:Long?) {
       repository?.getListProduccion(cultivo_id)
    }

    override fun getListas() {
        repository?.getListas()
    }
}