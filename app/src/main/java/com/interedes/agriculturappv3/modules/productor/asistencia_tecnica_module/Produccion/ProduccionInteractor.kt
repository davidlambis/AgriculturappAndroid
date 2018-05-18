package com.interedes.agriculturappv3.modules.productor.asistencia_tecnica_module.Produccion

import com.interedes.agriculturappv3.modules.models.produccion.Produccion

/**
 * Created by usuario on 20/03/2018.
 */
class ProduccionInteractor:IMainProduccion.Interactor {


    var repository: IMainProduccion.Repository? = null

    init {
        this.repository = ProduccionRepository()
    }

    override fun saveProduccion(produccion: Produccion, cultivo_id: Long,checkConection:Boolean) {
       repository?.saveProduccion(produccion,cultivo_id,checkConection)
    }

    override fun updateProduccion(produccion: Produccion, cultivo_id: Long, checkConection: Boolean) {
        repository?.updateProduccion(produccion,cultivo_id,checkConection)
    }


    override fun deleteProducccion(produccion: Produccion, cultivo_id: Long?,checkConection:Boolean) {
        repository?.deleteProduccion(produccion,cultivo_id,checkConection)
    }

    override fun execute(cultivo_id:Long?) {
       repository?.getListProduccion(cultivo_id)
    }

    override fun getListas() {
        repository?.getListas()
    }

    override fun getCultivo(cultivo_id:Long?){
        repository?.getCultivo(cultivo_id)
    }
}