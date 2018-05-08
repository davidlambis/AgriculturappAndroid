package com.interedes.agriculturappv3.productor.modules.asistencia_tecnica_module.tratamiento

import android.content.Context
import android.content.Intent
import android.os.Bundle
import com.interedes.agriculturappv3.productor.models.cultivo.Cultivo
import com.interedes.agriculturappv3.productor.models.lote.Lote
import com.interedes.agriculturappv3.productor.models.Tratamiento
import com.interedes.agriculturappv3.productor.models.control_plaga.ControlPlaga
import com.interedes.agriculturappv3.productor.models.unidad_medida.Unidad_Medida
import com.interedes.agriculturappv3.productor.models.unidad_productiva.Unidad_Productiva
import com.interedes.agriculturappv3.productor.modules.asistencia_tecnica_module.tratamiento.events.TratamientoEvent


interface ITratamiento {

    interface View {
        fun setTratamiento(tratamiento: Tratamiento?)
        fun showAlertDialogFilterControlPlaga()
        fun setListUnidadProductiva(listUnidadProductiva: List<Unidad_Productiva>?)
        fun setListLotes(listLotes: List<Lote>?)
        fun setListCultivos(listCultivos: List<Cultivo>?)
        fun validarListasAddControlPlaga(): Boolean
        fun showAlertDialogAddControlPlaga(controlPlaga: ControlPlaga?)
        fun setListUnidadMedida(listUnidadMedida: List<Unidad_Medida>?)
        fun registerControlPlaga()
        fun validarCampos(): Boolean

        fun showProgress()
        fun hideProgress()

        //ProgresHud
        fun showProgressHud()

        fun hideProgressHud()

        fun disableInputs()
        fun enableInputs()
        fun loadControlPlagas(listControlPlaga: List<ControlPlaga>)

        //Events
        fun onEventBroadcastReceiver(extras: Bundle, intent: Intent)
    }

    interface Presenter {
        fun onCreate()
        fun onDestroy()
        fun getTratamiento(insumoId: Long?)
        fun onEventMainThread(tratamientoEvent: TratamientoEvent?)
        fun setListSpinnerUnidadProductiva()
        fun setListSpinnerLote(unidad_productiva_id: Long?)
        fun setListSpinnerCultivo(lote_id: Long?, tipoProductoId: Long?)
        fun getListas()
        fun validarListasAddControlPlaga(): Boolean?
        fun setListSpinnerUnidadMedida()
        fun validarCampos(): Boolean?
        fun registerControlPlaga(controlPlaga: ControlPlaga, cultivo_id: Long?)
        fun onResume(context: Context)
        fun onPause(context: Context)
        //Conection
        fun checkConnection(): Boolean
    }

    interface Interactor {
        fun getTratamiento(insumoId: Long?)
        fun getListas()
        fun registerControlPlaga(controlPlaga: ControlPlaga, cultivo_id: Long?)
    }

    interface Repository {
        fun getTratamiento(insumoId: Long?)
        fun getListas()
        fun registerControlPlaga(controlPlaga: ControlPlaga, cultivo_id: Long?)
    }
}