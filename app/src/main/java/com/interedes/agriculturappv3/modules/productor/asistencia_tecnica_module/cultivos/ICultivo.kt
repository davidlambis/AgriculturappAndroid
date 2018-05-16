package com.interedes.agriculturappv3.modules.productor.asistencia_tecnica_module.cultivos

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.support.v7.app.AlertDialog
import com.interedes.agriculturappv3.modules.models.cultivo.Cultivo
import com.interedes.agriculturappv3.modules.models.detalletipoproducto.DetalleTipoProducto
import com.interedes.agriculturappv3.modules.models.lote.Lote
import com.interedes.agriculturappv3.modules.models.tipoproducto.TipoProducto
import com.interedes.agriculturappv3.modules.models.unidad_medida.Unidad_Medida
import com.interedes.agriculturappv3.modules.models.unidad_productiva.Unidad_Productiva
import com.interedes.agriculturappv3.modules.productor.asistencia_tecnica_module.cultivos.events.CultivoEvent

interface ICultivo {
    interface View {
        fun validarCampos(): Boolean
        fun limpiarCampos()

        fun disableInputs()
        fun enableInputs()

        fun showProgress()
        fun hideProgress()

        //Fun Cultivo CRUD
        fun registerCultivo()

        fun updateCultivo(cultivo: Cultivo?, loteId: Long?)
        fun deleteCultivo(cultivo: Cultivo): AlertDialog?
        fun setListCultivos(listCultivos: List<Cultivo>)
        fun setResults(cultivos: Int)


        //Dialog
        fun showAlertDialogFilterCultivo(isFilter: Boolean?)

        //Responses Messages
        fun messageErrorDialog(msg: String?): AlertDialog?

        fun requestResponseOk()
        fun requestResponseError(error: String?)
        fun onMessageOk(colorPrimary: Int, msg: String?)
        fun onMessageError(colorPrimary: Int, msg: String?)

        //Dialog
        fun showAlertDialogCultivo(cultivo: Cultivo?): AlertDialog?

        //Spinners y Date Pickers
        //Unidades Productivas
        fun setListUnidadProductiva(listUnidadProductiva: List<Unidad_Productiva>?)

        //Tipo Producto
        fun setListTipoProducto(listTipoProducto: List<TipoProducto>?)

        //Detalle Tipo Producto
        fun setListDetalleTipoProducto(listDetalleTipoProducto: List<DetalleTipoProducto>?)

        //Unidad Medida
        fun setListUnidadMedidas(listUnidadMedida: List<Unidad_Medida>?)

        //Lotes
        fun setListLotes(listLotes: List<Lote>?)

        //Validaciones
        fun validarListasFilterLote(): Boolean

        fun setLote(lote: Lote?)

        //Events
        fun onEventBroadcastReceiver(extras: Bundle, intent: Intent)

        fun showProgressHud()
        fun hideProgressHud()


        fun verificateConnection():AlertDialog?
    }

    interface Presenter {
        fun onCreate()
        fun onDestroy()
        fun onResume(context: Context)
        fun onPause(context: Context)
        fun onEventMainThread(cultivoEvent: CultivoEvent?)

        //Validacion
        fun validarCampos(): Boolean

        fun validarListasFilterLote(): Boolean

        //Methods Repository
        fun registerCultivo(cultivo: Cultivo?, loteId: Long?)

        fun updateCultivo(cultivo: Cultivo?, loteId: Long?)
        fun deleteCultivo(cultivo: Cultivo?, loteId: Long?)
        fun getListas()
        fun getListCultivos(lote_id: Long?)

        //METHODS VIEW
        fun setListSpinnerUnidadProductiva()

        fun setListSpinnerLote(unidad_productiva_id: Long?)
        fun setListSpinnerTipoProducto()
        fun setListSpinnerDetalleTipoProducto(detalle_tipo_produto_id: Long?)
        fun setListSpinnerUnidadMedida()

        //Conecttion
        fun checkConnection(): Boolean

        fun getLote(loteId: Long?)
    }

    interface Interactor {
        fun registerCultivo(cultivo: Cultivo?, loteId: Long?)
        fun updateCultivo(cultivo: Cultivo?, loteId: Long?)
        fun deleteCultivo(cultivo: Cultivo?, loteId: Long?)
        fun getListas()
        fun execute(lote_id: Long?)
        fun getLote(loteId: Long?)
        fun registerOnlineCultivo(cultivo: Cultivo?, loteId: Long?)
    }

    interface Repository {
        fun getListas()
        fun getListCultivos(lote_id: Long?)
        fun saveCultivo(mCultivo: Cultivo, loteId: Long?)
        fun registerOnlineCultivo(mCultivo: Cultivo?, loteId: Long?)
        fun updateCultivo(mCultivo: Cultivo, loteId: Long?)
        fun deleteCultivo(mCultivo: Cultivo, loteId: Long?)

        fun getCultivos(loteId: Long?): List<Cultivo>
        fun getLote(loteId: Long?)
        fun getLastCultivo(): Cultivo?

    }
}