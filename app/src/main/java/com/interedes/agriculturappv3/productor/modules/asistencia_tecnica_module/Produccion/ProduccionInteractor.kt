package com.interedes.agriculturappv3.productor.modules.asistencia_tecnica_module.Produccion

import com.interedes.agriculturappv3.productor.models.produccion.Produccion

/**
 * Created by usuario on 20/03/2018.
 */
class ProduccionInteractor:IMainProduccion.Interactor {


    var repository: IMainProduccion.Repository? = null

    init {
        this.repository = ProduccionRepository()
    }

    override fun saveProduccion(produccion: Produccion, cultivo_id: Long) {
       repository?.saveProduccion(produccion,cultivo_id)
    }




    override fun saveProduccionOnline(produccion: Produccion, cultivo_id: Long) {
        repository?.saveProduccionOnline(produccion,cultivo_id)
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

    override fun getCultivo(cultivo_id:Long?){
        repository?.getCultivo(cultivo_id)
    }
}