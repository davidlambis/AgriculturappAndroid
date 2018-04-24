package com.interedes.agriculturappv3.productor.modules.accounting_module.reportes

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.support.v7.app.AlertDialog
import com.interedes.agriculturappv3.productor.models.cultivo.Cultivo
import com.interedes.agriculturappv3.productor.models.lote.Lote
import com.interedes.agriculturappv3.productor.models.unidad_productiva.UnidadProductiva
import com.interedes.agriculturappv3.productor.models.ventas.CategoriaPuk
import com.interedes.agriculturappv3.productor.models.ventas.resports.BalanceContable
import com.interedes.agriculturappv3.productor.modules.accounting_module.reportes.events.RequestEventReporte
import java.util.*

interface IMainViewReportes {

    interface MainView {
        //Validacion
        fun validarListasFilterReports(): Boolean
        fun validarCampos(): Boolean
        fun validarDatesSelect(): Boolean

        fun limpiarCampos()

        fun disableInputs()
        fun enableInputs()

        fun showProgress()
        fun hideProgress()

        //ProgresHud
        fun showProgressHud()
        fun hideProgressHud()

        //Fun Lote CRUD
        fun setResults(transacciones:Int)

        //GenericResponse Notify
        fun requestResponseOK()
        fun requestResponseError(error: String?)

        fun onMessageOk(colorPrimary: Int, msg: String?)
        fun onMessageError(colorPrimary: Int, msg: String?)
        fun requestResponseItemOK(string1:String?, string2:String?)

        //Set sppiners
        fun setListUnidadProductiva(listUnidadProductiva: List<UnidadProductiva>?)
        fun setListLotes(listLotes:List<Lote>?)
        fun setListCultivos(listCultivos:List<Cultivo>?)
        fun setCultivo(cultivo: Cultivo?)

        fun setListReportCategoriasPuk(categoriasList:List<CategoriaPuk>?)

        //Dialogs
        fun verificateConnection(): AlertDialog?
        fun showAlertDialogFilterReports()
        fun showAlertDialogSelectDate()

        //Balance
        fun setBalanceContable(balanceContable:BalanceContable?)


        //Events
        fun onEventBroadcastReceiver(extras: Bundle, intent: Intent)
    }

    interface Presenter {
        fun onCreate()
        fun onDestroy()
        fun onResume(context: Context)
        fun onPause(context: Context)

        //Events
        fun onEventMainThread(requestEvent: RequestEventReporte?)

        //Validacion
        fun validarCampos(): Boolean?
        fun validarListasFilterReports(): Boolean
        fun validarDatesSelect(): Boolean

        //Methods
        fun getCultivo(cultivo_id:Long?)
        fun getTotalTransacciones(cultivo_id:Long?, dateStart: Date?, dateEnd:Date?)
        fun getListas()

        //Methods View
        fun setListSpinnerUnidadProductiva()
        fun setListSpinnerLote(unidad_productiva_id:Long?)
        fun setListSpinnerCultivo(lote_id:Long?)



        //Conecttion
        fun checkConnection(): Boolean
    }

    interface Interactor {
        fun execute(cultivo_id:Long?,typeTransaccion:Long?)
        fun getListas()
        fun getCultivo(cultivo_id:Long?)
        fun getTotalTransacciones(cultivo_id:Long?, dateStart: Date?, dateEnd:Date?)
    }

    interface Repository {
        fun getListas()
        fun getCultivo(cultivo_id:Long?)
        fun getTotalTransacciones(cultivo_id:Long?,dateStart: Date?, dateEnd:Date?)
    }
}