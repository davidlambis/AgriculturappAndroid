package com.interedes.agriculturappv3.productor.modules.asistencia_tecnica_module.UnidadProductiva

import com.interedes.agriculturappv3.productor.models.unidad_productiva.Unidad_Productiva

/**
 * Created by usuario on 16/03/2018.
 */
class UpInteractor : IUnidadProductiva.Interactor {

    var upRepo: IUnidadProductiva.Repo? = null

    init {
        this.upRepo = UpRepository()
    }

    override fun registerUP(unidadProductivaModel: Unidad_Productiva?) {
        upRepo!!.saveUp(unidadProductivaModel!!)
    }

    override fun registerOnlineUP(unidadProductivaModel: Unidad_Productiva?) {
        upRepo?.registerOnlineUP(unidadProductivaModel)
    }

    override fun updateUP(unidadProductivaModel: Unidad_Productiva?) {
        upRepo!!.updateUp(unidadProductivaModel!!)
    }

    override fun deleteUP(unidadProductivaModel: Unidad_Productiva?) {
        upRepo!!.deleteUp(unidadProductivaModel!!)
    }

    override fun execute() {
        upRepo!!.getListUPs()
    }

    override fun getListas() {
        upRepo!!.getListas()
    }


}