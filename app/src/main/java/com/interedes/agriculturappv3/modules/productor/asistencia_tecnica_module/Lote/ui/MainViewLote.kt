package com.interedes.agriculturappv3.modules.productor.asistencia_tecnica_module.Lote.ui

import android.content.Intent
import android.os.Bundle
import android.support.v7.app.AlertDialog
import com.interedes.agriculturappv3.modules.models.lote.Lote
import com.interedes.agriculturappv3.modules.models.unidad_medida.Unidad_Medida
import com.interedes.agriculturappv3.modules.models.unidad_productiva.Unidad_Productiva

/**
 * Created by EnuarMunoz on 7/03/18.
 */
interface MainViewLote {
    //Acions Elements
    fun disableInputs()
    fun enableInputs()

    fun showProgress()
    fun hideProgress()


    fun hideElementsAndSetPropertiesOnConectionInternet()
    fun showElementsAndSetPropertiesOffConnectioninternet()


    //Set Properties
    fun setPropertiesTypeLocationGps()
    fun setPropertiesTypeLocationManual()

    //Fun Lote CRUD
    fun registerLote()
    fun updateLote(lote: Lote,unidad_productiva_id:Long?)
    fun setListLotes(lotes:List<Lote>)
    fun setResults(lotes:Int)

    //List Unidad_Productiva
    fun setListUP(listUnidadProductiva:List<Unidad_Productiva>)
    fun setListUPAdapterSpinner()

    ///ListUnidad Medida
    fun setListUnidadMedida(listUnidadMedida:List<Unidad_Medida>)
    fun setListUnidadMedidaAdapterSpinner()

    //VALIDATION
    fun validarCampos(): Boolean?
    fun limpiarCampos()


    //ProgresHud
    fun showProgressHud()
    fun showProgressHudCoords()
    fun hideProgressHud()

    //Response Notify
    fun requestResponseOk()
    fun requestResponseError(error : String?)

    fun onMessageOk(colorPrimary: Int, message: String?)
    fun onMessageError(colorPrimary: Int, message: String?)


    //UI
    fun showAlertDialogAddLote(lote: Lote?)
    fun showAlertTypeLocationLote(): AlertDialog?
    fun confirmDelete(lote: Lote):AlertDialog?
    fun verificateConnection():AlertDialog?
    fun showAlertDialogSelectUp():AlertDialog?

    //Events
    fun onEventBroadcastReceiver(extras: Bundle,intent: Intent)


}