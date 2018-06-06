package com.interedes.agriculturappv3.modules.productor.asistencia_tecnica_module.UnidadProductiva

import com.interedes.agriculturappv3.modules.models.unidad_productiva.Unidad_Productiva

/**
 * Created by usuario on 16/03/2018.
 */
class UpInteractor : IUnidadProductiva.Interactor {

    var upRepo: IUnidadProductiva.Repo? = null

    init {
        this.upRepo = UpRepository()
    }

    override fun registerUP(unidadProductivaModel: Unidad_Productiva?,checkConection:Boolean) {
        upRepo!!.saveUp(unidadProductivaModel!!,checkConection)
    }
    override fun updateUP(unidadProductivaModel: Unidad_Productiva,checkConection:Boolean) {
        upRepo!!.updateUp(unidadProductivaModel!!,checkConection)
    }

    override fun deleteUP(unidadProductivaModel: Unidad_Productiva,checkConection:Boolean) {
        upRepo!!.deleteUp(unidadProductivaModel!!,checkConection)
    }

    override fun execute() {
        upRepo!!.getListUPs()
    }

    override fun getListas() {
        upRepo!!.getListas()
    }
}