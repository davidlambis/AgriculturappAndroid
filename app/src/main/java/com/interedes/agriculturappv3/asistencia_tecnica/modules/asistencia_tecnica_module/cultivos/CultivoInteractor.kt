package com.interedes.agriculturappv3.asistencia_tecnica.modules.asistencia_tecnica_module.cultivos

import com.interedes.agriculturappv3.asistencia_tecnica.models.Cultivo


class CultivoInteractor : ICultivo.Interactor {


    var repository: ICultivo.Repository? = null

    init {
        repository = CultivoRepository()
    }

    override fun registerCultivo(cultivo: Cultivo?) {
        repository?.saveCultivo(cultivo!!)
    }

    override fun updateCultivo(cultivo: Cultivo?) {
        repository?.updateCultivo(cultivo!!)
    }

    override fun deleteCultivo(cultivo: Cultivo?) {
        repository?.deleteCultivo(cultivo!!)
    }

    override fun getAllCultivos() {
        repository?.getListAllCultivos()
    }

    override fun getListas() {
        repository?.getListas()
    }

    override fun loadLotesSpinner(unidadProductivaId: Long?) {
        repository?.loadLotesSpinner(unidadProductivaId)
    }

    override fun loadLotesSpinnerSearch(unidadProductivaId: Long?) {
        repository?.loadLotesSpinnerSearch(unidadProductivaId)
    }

    override fun searchCultivos(loteId: Long?) {
        repository?.searchCultivos(loteId)
    }

    override fun loadDetalleTipoProducto(tipoProductoId: Long?) {
        repository?.loadDetalleTipoProducto(tipoProductoId)
    }

}