package com.interedes.agriculturappv3.modules.comprador.productores


import android.content.Intent
import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v7.app.AlertDialog
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup

import com.interedes.agriculturappv3.R
import com.interedes.agriculturappv3.modules.models.producto.Producto


class ProductoresFragment : Fragment(),IMainViewProductor.MainView {

    var presenter: IMainViewProductor.Presenter? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        presenter =  ProductorPresenter(this)
        presenter?.onCreate()
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_productores, container, false)
    }

    //region IMPLEMENTS METHODS INTERFACE

    override fun showProgress() {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun hideProgress() {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun showProgressHud() {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun hideProgressHud() {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun setListProducto(listTipoProducto: List<Producto>) {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun setResults(productos: Int) {
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

    override fun verificateConnection(): AlertDialog? {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun onEventBroadcastReceiver(extras: Bundle, intent: Intent) {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }


    //endregion
}
