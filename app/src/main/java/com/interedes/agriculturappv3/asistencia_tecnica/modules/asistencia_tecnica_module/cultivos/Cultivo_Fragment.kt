package com.interedes.agriculturappv3.asistencia_tecnica.modules.asistencia_tecnica_module.cultivos


import android.app.AlertDialog
import android.content.DialogInterface
import android.content.Intent
import android.os.Bundle
import android.support.v4.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Toast

import com.interedes.agriculturappv3.R
import com.interedes.agriculturappv3.asistencia_tecnica.models.Cultivo
import com.interedes.agriculturappv3.asistencia_tecnica.models.UnidadProductiva
import com.interedes.agriculturappv3.asistencia_tecnica.models.unidad_medida.Unidad_Medida
import kotlinx.android.synthetic.main.dialog_form_cultivo.view.*
import kotlinx.android.synthetic.main.fragment_cultivo.*

/**
 * A simple [Fragment] subclass.
 */
class Cultivo_Fragment : Fragment(), View.OnClickListener, ICultivo.View {


    var presenter: ICultivo.Presenter? = null

    init {
        presenter = CultivoPresenter(this)
        presenter?.onCreate()
    }

    //Dialog
    var viewDialog: View? = null
    var _dialogRegisterUpdate: AlertDialog? = null

    //Variables Globales
    var listUnidadProductivaGlobal: List<UnidadProductiva>? = java.util.ArrayList<UnidadProductiva>()
    var listUnidadMedidaGlobal: List<Unidad_Medida>? = java.util.ArrayList<Unidad_Medida>()

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_cultivo, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        fabAddCultivo.setOnClickListener(this)
    }

    //region on Click
    override fun onClick(p0: View?) {
        when (p0?.id) {
            R.id.fabAddCultivo -> {
                showAlertDialogCultivo(null)
            }
            R.id.ivClosetDialogCultivo -> {
                _dialogRegisterUpdate?.dismiss()
            }
        }
    }
    //endregion

    //region Métodos Interfaz
    override fun validarCampos(): Boolean {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun limpiarCampos() {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun disableInputs() {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun enableInputs() {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun showProgress() {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun hideProgress() {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun hideElements() {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun registerCultivo() {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun updateCultivo() {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun setListCultivos(listCultivos: List<Cultivo>) {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun setResults(cultivos: Int) {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun requestResponseOK() {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun requestResponseError(error: String?) {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun onMessageOk(colorPrimary: Int, msg: String?) {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun onMessageError(colorPrimary: Int, msg: String?) {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun showAlertDialogCultivo(cultivo: Cultivo?) {
        val inflater = this.layoutInflater
        viewDialog = inflater.inflate(R.layout.dialog_form_cultivo, null)
        setAdaptersSpinner()

        val btnCloseDialog= viewDialog?.ivClosetDialogCultivo
        //Set Events
        btnCloseDialog?.setOnClickListener(this)
        val dialog = AlertDialog.Builder(activity)
                .setView(viewDialog)
                .setIcon(R.drawable.ic_cultivos)
                . setTitle(getString(R.string.title_add_cultivo))
                .setPositiveButton(getString(R.string.btn_save), null) //Set to null. We override the onclick
                .setNegativeButton(getString(R.string.close), DialogInterface.OnClickListener { dialog, which ->

                })
                .create()
        dialog.setOnShowListener(DialogInterface.OnShowListener {
            val button = (dialog as AlertDialog).getButton(AlertDialog.BUTTON_POSITIVE)
            button.setOnClickListener {
                // TODO Do something
                //registerUp()
                //Dismiss once everything is OK.
                //dialog.dismiss()
            }
        })
        dialog?.show()
        _dialogRegisterUpdate=dialog

    }

    override fun setListUnidadProductiva(listUnidadProductiva: List<UnidadProductiva>) {
        listUnidadProductivaGlobal = listUnidadProductiva
    }

    override fun setListUnidadMedidas(listUnidadMedida: List<Unidad_Medida>) {
        listUnidadMedidaGlobal = listUnidadMedida
    }

    override fun setAdaptersSpinner() {
        if (viewDialog != null) {
            viewDialog?.spinnerUnidadProductiva!!.setAdapter(null)
            val unidadProductivaArrayAdapter = ArrayAdapter<UnidadProductiva>(activity, android.R.layout.simple_spinner_dropdown_item, listUnidadProductivaGlobal)
            viewDialog?.spinnerUnidadProductiva!!.setAdapter(unidadProductivaArrayAdapter)
            viewDialog?.spinnerUnidadProductiva!!.onItemClickListener = AdapterView.OnItemClickListener { adapterView, view, position, l ->
                Toast.makeText(activity, "" + position.toString(), Toast.LENGTH_SHORT).show()
            }

            viewDialog?.spinnerUnidadMedidaCosecha!!.setAdapter(null)
            val unidadMedidaArrayAdapter = ArrayAdapter<Unidad_Medida>(activity, android.R.layout.simple_spinner_dropdown_item, listUnidadMedidaGlobal)
            viewDialog?.spinnerUnidadMedidaCosecha!!.setAdapter(unidadMedidaArrayAdapter)
            viewDialog?.spinnerUnidadMedidaCosecha!!.onItemClickListener = AdapterView.OnItemClickListener { adapterView, view, position, l ->
                Toast.makeText(activity, "" + position.toString(), Toast.LENGTH_SHORT).show()
            }
        }
    }

    override fun onEventBroadcastReceiver(extras: Bundle, intent: Intent) {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }
    //endregion
}
