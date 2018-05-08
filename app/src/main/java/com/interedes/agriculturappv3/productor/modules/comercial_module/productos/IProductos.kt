package com.interedes.agriculturappv3.productor.modules.comercial_module.productos

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.support.v7.app.AlertDialog
import com.interedes.agriculturappv3.productor.models.cultivo.Cultivo
import com.interedes.agriculturappv3.productor.models.lote.Lote
import com.interedes.agriculturappv3.productor.models.producto.CalidadProducto
import com.interedes.agriculturappv3.productor.models.producto.Producto
import com.interedes.agriculturappv3.productor.models.unidad_medida.Unidad_Medida
import com.interedes.agriculturappv3.productor.models.unidad_productiva.Unidad_Productiva
import com.interedes.agriculturappv3.productor.modules.comercial_module.productos.events.ProductosEvent

interface IProductos {

    interface View {
        fun validarCampos(): Boolean
        fun validarListasAddProducto(): Boolean
        fun showProgress()
        fun hideProgress()
        fun disableInputs()
        fun enableInputs()
        fun limpiarCampos()
        fun showDialogProgress()
        fun hideDialogProgress()
        //Fun Productos CRUD
        fun registerProducto()

        fun updateProducto(mProducto: Producto)
        fun setListProductos(listProductos: List<Producto>)
        fun setResults(productos: Int)
        //Response Notify
        fun requestResponseOK()

        fun requestResponseError(error: String?)

        fun onMessageOk(colorPrimary: Int, msg: String?)
        fun onMessageError(colorPrimary: Int, msg: String?)
        //Set sppiners
        fun setListMoneda(listMoneda: List<Unidad_Medida>?)

        fun setListCalidad(listCalidadProducto: List<CalidadProducto>?)

        fun setListUnidadProductiva(listUnidadProductiva: List<Unidad_Productiva>?)
        fun setListLotes(listLotes: List<Lote>?)
        fun setListCultivos(listCultivos: List<Cultivo>?)


        //Dialog
        fun showAlertDialogAddProducto(producto: Producto?)

        fun verificateConnection(): AlertDialog?
        fun confirmDelete(producto: Producto): AlertDialog?
        fun showAlertDialogFilterProducto(isFilter: Boolean?)

        //Events
        fun onEventBroadcastReceiver(extras: Bundle, intent: Intent)

        fun setCultivo(cultivo: Cultivo?)

        fun takePictureWithCamera(fragment: ProductosFragment)
        fun choosePhotoFromGallery(fragment: ProductosFragment)
    }

    interface Presenter {
        fun onCreate()
        fun onDestroy()
        fun onResume(context: Context)
        fun onPause(context: Context)

        //Events
        fun onEventMainThread(event: ProductosEvent?)

        //Validacion
        fun validarCampos(): Boolean?

        fun validarListasAddProducto(): Boolean?

        //Methods
        fun registerProducto(producto: Producto, cultivo_id: Long)

        fun updateProducto(mProducto: Producto, cultivo_id: Long)
        fun deleteProducto(producto: Producto, cultivo_id: Long?)
        fun getListProductos(cultivo_id: Long?)
        fun getListas()
        fun getCultivo(cultivo_id: Long?)


        //Methods View
        fun setListSpinnerUnidadProductiva()

        fun setListSpinnerMoneda()
        fun setListSpinnerLote(unidad_productiva_id: Long?)
        fun setListSpinnerCultivo(lote_id: Long?)

        fun setListSpinnerCalidadProducto()
        //Conecttion
        fun checkConnection(): Boolean

    }

    interface Interactor {
        fun registerProducto(producto: Producto, cultivo_id: Long)
        fun registerOnlineProducto(producto: Producto, cultivo_id: Long)
        fun updateProducto(mProducto: Producto, cultivo_id: Long)
        fun deleteProducto(producto: Producto, cultivo_id: Long?)
        fun getListProductos(cultivo_id: Long?)
        fun getListas()
        fun getCultivo(cultivo_id: Long?)

    }

    interface Repository {
        fun getListas()
        fun getListProducto(cultivo_id: Long?)
        fun getProductos(cultivo_id: Long?): List<Producto>
        fun registerProducto(mProducto: Producto, cultivo_id: Long)
        fun registerOnlineProducto(mProducto: Producto, cultivo_id: Long)
        fun updateProducto(mProducto: Producto, cultivo_id: Long)
        fun deleteProducto(mProducto: Producto, cultivo_id: Long?)
        fun getCultivo(cultivo_id: Long?)
        fun getLastProducto(): Producto?
    }
}