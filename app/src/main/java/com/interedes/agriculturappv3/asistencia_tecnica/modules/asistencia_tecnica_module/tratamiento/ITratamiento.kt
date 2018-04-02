package com.interedes.agriculturappv3.asistencia_tecnica.modules.asistencia_tecnica_module.tratamiento

import com.interedes.agriculturappv3.asistencia_tecnica.models.Cultivo
import com.interedes.agriculturappv3.asistencia_tecnica.models.Lote
import com.interedes.agriculturappv3.asistencia_tecnica.models.Tratamiento
import com.interedes.agriculturappv3.asistencia_tecnica.models.UnidadProductiva
import com.interedes.agriculturappv3.asistencia_tecnica.models.control_plaga.ControlPlaga
import com.interedes.agriculturappv3.asistencia_tecnica.models.produccion.Produccion
import com.interedes.agriculturappv3.asistencia_tecnica.models.unidad_medida.Unidad_Medida
import com.interedes.agriculturappv3.asistencia_tecnica.modules.asistencia_tecnica_module.tratamiento.events.TratamientoEvent


interface ITratamiento {

    interface View {
        fun setTratamiento(tratamiento: Tratamiento?)
        fun showAlertDialogFilterControlPlaga()
        fun setListUnidadProductiva(listUnidadProductiva: List<UnidadProductiva>?)
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