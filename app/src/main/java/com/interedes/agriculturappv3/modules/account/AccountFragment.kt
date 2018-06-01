package com.interedes.agriculturappv3.modules.account

import android.Manifest
import android.app.Activity
import android.app.ProgressDialog
import android.content.DialogInterface
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Color
import android.os.Bundle
import android.provider.MediaStore
import android.support.design.widget.Snackbar
import android.support.v4.app.ActivityCompat
import android.support.v4.app.Fragment
import android.support.v4.content.ContextCompat
import android.support.v7.app.AlertDialog
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.TextView
import android.widget.Toast
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageMetadata
import com.google.firebase.storage.StorageReference

import com.interedes.agriculturappv3.R
import com.interedes.agriculturappv3.modules.models.chat.UserFirebase
import com.interedes.agriculturappv3.modules.models.detalle_metodo_pago.DetalleMetodoPago
import com.interedes.agriculturappv3.modules.models.detalle_metodo_pago.DetalleMetodoPago_Table
import com.interedes.agriculturappv3.modules.models.metodopago.MetodoPago
import com.interedes.agriculturappv3.modules.models.metodopago.MetodoPago_Table
import com.interedes.agriculturappv3.modules.models.usuario.Usuario
import com.interedes.agriculturappv3.modules.productor.ui.main_menu.MenuMainActivity
import com.interedes.agriculturappv3.services.resources.RolResources
import com.kaopiz.kprogresshud.KProgressHUD
import com.raizlabs.android.dbflow.data.Blob
import com.raizlabs.android.dbflow.sql.language.SQLite
import com.squareup.picasso.Picasso
import kotlinx.android.synthetic.main.activity_menu_main.*
import kotlinx.android.synthetic.main.dialog_form_unidad_productiva.*
import kotlinx.android.synthetic.main.fragment_account.*
import kotlinx.android.synthetic.main.navigation_drawer_header.*
import java.io.ByteArrayOutputStream
import java.io.IOException
import java.util.HashMap

class AccountFragment : Fragment(),View.OnClickListener,IMainViewAccount.MainView {

    //CÁMARA
    val IMAGE_DIRECTORY = "/Productos"
    val REQUEST_GALLERY = 1
    val REQUEST_CAMERA = 2

    // var imageGlobalRutaFoto: String? = null
    var isFoto: Boolean? = false

    //FIREBASE
    private var mUserDBRef: DatabaseReference? = null
    private var mStorageRef: StorageReference? = null
    private var mCurrentUserID: String? = null


    var presenter: IMainViewAccount.Presenter? = null

    //Progress
    private var hud: KProgressHUD?=null

    //Model
    var metodoPagoGlobal:MetodoPago?=null
    var detalleMetodoPagoGlobal:DetalleMetodoPago?=null
    var userLogued:Usuario?=null

    var imageAccountGlobal: ByteArray? = null
    var imageBitmapAccountGlobal: Bitmap? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        presenter = AccountPresenter(this)
        presenter?.onCreate()
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {


        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_account, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        (activity as MenuMainActivity).toolbar.title = getString(R.string.tittle_account)
        ivBackButton.setOnClickListener(this)
        btnSaveAccount.setOnClickListener(this)
        user_take_picture_camera.setOnClickListener(this)
        user_take_picture_gallery.setOnClickListener(this)
        user_image_cancel.setOnClickListener(this)

        mCurrentUserID = FirebaseAuth.getInstance().currentUser!!.uid

        //init firebase
        mUserDBRef = FirebaseDatabase.getInstance().reference.child("Users")
        mStorageRef = FirebaseStorage.getInstance().reference.child("Photos").child("Users")

        /**populate views initially**/
        //populateTheViews()
        setupInjection()
    }

    private fun setupInjection() {
        presenter?.getListas()

        userLogued= presenter?.getUserLogued()

        if(userLogued!=null){

            edtNombres.setText(userLogued?.Nombre)
            edtApellidos.setText(userLogued?.Apellidos)
            edtCedula.setText(userLogued?.Identificacion)
            edtCelular.setText(userLogued?.PhoneNumber)
            edtCorreo.setText(userLogued?.Email)

            if(userLogued?.blobImagen!=null){
                // val bitmap = BitmapFactory.decodeByteArray(foto, 0, foto!!.size)
                // imgTipoProducto.setImageBitmap(bitmap)
                try {
                    val foto = userLogued?.blobImagen?.blob
                    imageBitmapAccountGlobal = BitmapFactory.decodeByteArray(foto, 0, foto!!.size)
                    imageAccountGlobal = convertBitmapToByte(imageBitmapAccountGlobal!!)
                    user_image.setImageBitmap(imageBitmapAccountGlobal)



                    //Picasso.with(activity).load(userPhoto).placeholder(R.drawable.ic_foto_producto_square).into(user_image)
                }catch (ex:Exception){
                    var ss= ex.toString()
                    Log.d("Convert Image", "defaultValue = " + ss);
                }
            }


            if(userLogued?.RolNombre?.equals(RolResources.PRODUCTOR)!!){

                 presenter?.setListSpinnerMetodoPago()
                 detalleMetodoPagoGlobal= SQLite.select().from(DetalleMetodoPago::class.java).where(DetalleMetodoPago_Table.Id.eq(userLogued?.DetalleMetodoPagoId)).querySingle()
                 metodoPagoGlobal= SQLite.select().from(MetodoPago::class.java).where(MetodoPago_Table.Id.eq(detalleMetodoPagoGlobal?.MetodoPagoId)).querySingle()

                if(metodoPagoGlobal!=null){
                    spinnerMetodoPago.setText(metodoPagoGlobal?.Nombre)
                }
                if(detalleMetodoPagoGlobal!=null){
                    presenter?.setListSpinnerDetalleMetodoPago(metodoPagoGlobal?.Id)
                    spinnerDetalleMetodoPago.setText(detalleMetodoPagoGlobal?.Nombre)
                }


            }else{
                detalleMetodoPagoGlobal= SQLite.select().from(DetalleMetodoPago::class.java).where(DetalleMetodoPago_Table.Id.eq(userLogued?.DetalleMetodoPagoId)).querySingle()

                spinnerMetodoPago.visibility=View.GONE
                spinnerDetalleMetodoPago.visibility=View.GONE
            }
        }
    }


    private fun populateTheViews() {
        mUserDBRef?.child(FirebaseAuth.getInstance().currentUser!!.uid)?.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                val currentuser = dataSnapshot.getValue<UserFirebase>(UserFirebase::class.java)
                try {
                    val userPhoto = currentuser!!.Imagen
                    val userName = currentuser!!.Nombre
                    val userLastName = currentuser!!.Apellido
                    val userIdentification = currentuser!!.Cedula
                    val userPhone = currentuser!!.Telefono

                    Picasso.with(activity).load(userPhoto).placeholder(R.drawable.ic_foto_producto_square).into(user_image)
                    edtNombres.setText(userName)
                    edtApellidos.setText(userLastName)
                    edtCedula.setText(userIdentification)
                    edtCelular.setText(userPhone)

                } catch (e: Exception) {
                    e.printStackTrace()
                }
            }
            override fun onCancelled(databaseError: DatabaseError) {

            }
        })
    }


    //region IMPLEMENTS METHODS INTERFACE

    override fun validarUpdateUser(): Boolean {
        //val email_pattern = "[a-zA-Z0-9._-]+@[a-z]+\\.+[a-z]+"
        var cancel = false
        var focusView: View? = null

        //VALIDATION PRODUCTOR
        //------------------------------------------------------------------------

        if(userLogued?.RolNombre?.equals(RolResources.COMPRADOR)!!){
            if (edtNombres?.text.toString().isEmpty()) {
                edtNombres?.setError(getString(R.string.error_field_required))
                focusView = edtNombres
                cancel = true
            } else if (edtApellidos?.text.toString().isEmpty()) {
                edtApellidos?.setError(getString(R.string.error_field_required))
                focusView = edtApellidos
                cancel = true
            } else if (edtCedula?.text.toString().isEmpty()) {
                edtCedula?.setError(getString(R.string.error_field_required))
                focusView = edtCedula
                cancel = true
            } else if (edtCorreo?.text.toString().isEmpty()) {
                edtCorreo?.setError(getString(R.string.error_field_required))
                focusView = edtCorreo
                cancel = true
            }  else if (edtCelular?.text.toString().isEmpty()) {
                edtCelular?.setError(getString(R.string.error_field_required))
                focusView = edtCelular
                cancel = true
            }


        }else if(userLogued?.RolNombre?.equals(RolResources.PRODUCTOR)!!){

            if (edtNombres?.text.toString().isEmpty()) {
                edtNombres?.setError(getString(R.string.error_field_required))
                focusView = edtNombres
                cancel = true
            } else if (edtApellidos?.text.toString().isEmpty()) {
                edtApellidos?.setError(getString(R.string.error_field_required))
                focusView = edtApellidos
                cancel = true
            } else if (edtCedula?.text.toString().isEmpty()) {
                edtCedula?.setError(getString(R.string.error_field_required))
                focusView = edtCedula
                cancel = true
            } else if (edtCorreo?.text.toString().isEmpty()) {
                edtCorreo?.setError(getString(R.string.error_field_required))
                focusView = edtCorreo
                cancel = true
            }  else if (edtCelular?.text.toString().isEmpty()) {
                edtCelular?.setError(getString(R.string.error_field_required))
                focusView = edtCelular
                cancel = true
            }



            else if (spinnerMetodoPago?.text.toString().isEmpty()) {
                spinnerMetodoPago?.setError(getString(R.string.error_field_required))
                focusView = spinnerMetodoPago
                cancel = true
            }

            else if (spinnerDetalleMetodoPago?.text.toString().isEmpty()) {
                spinnerDetalleMetodoPago?.setError(getString(R.string.error_field_required))
                focusView = spinnerDetalleMetodoPago
                cancel = true
            }
        }




        //Validation COMPRADOR
        //-------------------------------------------------------------------------------



        /* else if (!edtCorreo.text.toString().trim().matches(email_pattern.toRegex())) {
            edtCorreo?.setError(getString(R.string.edit_text_error_correo))
            focusView = edtCorreo
            cancel = true
        }*/

        if (cancel) {
            focusView?.requestFocus()
        } else {
            return true
        }
        return false
    }



    override fun showProgress() {
        //  swipeRefreshLayout.setRefreshing(true);
    }

    override fun hideProgress() {
        // swipeRefreshLayout.setRefreshing(false);
    }

    override fun showProgressHud(){
        hud = KProgressHUD.create(activity)
                .setStyle(KProgressHUD.Style.SPIN_INDETERMINATE)
                .setWindowColor(getResources().getColor(R.color.colorPrimary))
                .setLabel("Cargando...", resources.getColor(R.color.white))
        hud?.show()
    }

    override fun hideProgressHud(){
        hud?.dismiss()
    }

    override fun requestResponseOK() {
        userLogued=presenter?.getUserLogued()
        (activity as MenuMainActivity).tvNombreUsuario.setText(String.format(getString(R.string.nombre_usuario_nav), userLogued?.Nombre, userLogued?.Apellidos))
        onMessageOk(R.color.colorPrimary,getString(R.string.request_ok));
    }

    override fun requestResponseError(error: String?) {
        onMessageError(R.color.grey_luiyi, error)
    }

    override fun onMessageOk(colorPrimary: Int, message: String?) {
        val color = Color.WHITE
        val snackbar = Snackbar
                .make(container_fragment, message!!, Snackbar.LENGTH_LONG)
        val sbView = snackbar.view
        sbView.setBackgroundColor(ContextCompat.getColor(activity!!.applicationContext, colorPrimary))
        val textView = sbView.findViewById<View>(android.support.design.R.id.snackbar_text) as TextView
        textView.setCompoundDrawablesWithIntrinsicBounds(R.drawable.quantum_ic_cast_connected_white_24, 0, 0, 0)
        // textView.setCompoundDrawablePadding(getResources().getDimensionPixelOffset(R.dimen.activity_horizontal_margin));
        textView.setTextColor(color)
        snackbar.show()
    }

    override fun onMessageError(colorPrimary: Int, message: String?) {
        onMessageOk(colorPrimary, message)
    }


    override fun verificateConnection(): AlertDialog? {
        var builder = AlertDialog.Builder(context!!)
        builder.setTitle(getString(R.string.alert));
        builder.setMessage(getString(R.string.verificate_conexion));
        builder?.setPositiveButton(getString(R.string.confirm), DialogInterface.OnClickListener { dialog, which ->
            dialog.dismiss()
        })
        builder.setIcon(R.drawable.ic_produccion_cultivo);
        return builder.show();

    }

    fun verificateConnectionChangeFoto(): AlertDialog? {
        var builder = AlertDialog.Builder(context!!)
        builder.setTitle(getString(R.string.alert));
        builder.setMessage(getString(R.string.tittle_verificate_conection));
        builder?.setPositiveButton(getString(R.string.confirm), DialogInterface.OnClickListener { dialog, which ->
            dialog.dismiss()
        })
        builder.setIcon(R.drawable.ic_asistencia_tecnica_color_500);
        return builder.show();
    }

    override fun onEventBroadcastReceiver(extras: Bundle, intent: Intent) {
        if(extras!=null){
            if (extras.containsKey("state_conectivity")) {
                var state_conectivity = intent.extras!!.getBoolean("state_conectivity")
            }
        }
    }

    override fun setListMetodoPago(listMetodoPago: List<MetodoPago>?) {
        ///Adapaters
        spinnerMetodoPago!!.setAdapter(null)
        var uMedidaArrayAdapter = ArrayAdapter<MetodoPago>(activity, android.R.layout.simple_spinner_dropdown_item, listMetodoPago);
        spinnerMetodoPago!!.setAdapter(uMedidaArrayAdapter);
        spinnerMetodoPago!!.onItemClickListener = AdapterView.OnItemClickListener { adapterView, view, position, l ->
            metodoPagoGlobal = listMetodoPago!![position]
            spinnerDetalleMetodoPago?.setText("")
            presenter?.setListSpinnerDetalleMetodoPago(metodoPagoGlobal?.Id)
        }
    }

    override fun setListDetalleMetodoPago(listDetalleMetodoPago: List<DetalleMetodoPago>?) {
        ///Adapaters
        spinnerDetalleMetodoPago!!.setAdapter(null)
        var uMedidaArrayAdapter = ArrayAdapter<DetalleMetodoPago>(activity, android.R.layout.simple_spinner_dropdown_item, listDetalleMetodoPago);
        spinnerDetalleMetodoPago!!.setAdapter(uMedidaArrayAdapter);
        spinnerDetalleMetodoPago!!.onItemClickListener = AdapterView.OnItemClickListener { adapterView, view, position, l ->
            detalleMetodoPagoGlobal = listDetalleMetodoPago!![position]
        }
    }




    //endregion

    //region Click
    override fun onClick(p0: View?) {
        when (p0?.id) {

            R.id.ivBackButton -> {
                ivBackButton.setColorFilter(ContextCompat.getColor(activity!!.applicationContext, R.color.colorPrimary))
                (activity as MenuMainActivity).onBackPressed()
            }

            R.id.user_take_picture_camera -> {
                if (ActivityCompat.checkSelfPermission(activity!!.applicationContext, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
                    ActivityCompat.requestPermissions(activity!!, arrayOf(Manifest.permission.CAMERA), 1000)
                    return
                }
                takePictureWithCamera(this)
            }
            R.id.user_take_picture_gallery -> {
                if (ActivityCompat.checkSelfPermission(activity!!.applicationContext, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
                    ActivityCompat.requestPermissions(activity!!, arrayOf(Manifest.permission.CAMERA), 1000)
                    return
                }
                choosePhotoFromGallery(this)
            }
            R.id.user_image_cancel -> {
                isFoto = false
                user_image?.setImageResource(R.drawable.ic_foto_producto_square)
            }

            R.id.btnSaveAccount -> {
                if(presenter?.validarUpdateUser()!!){
                    saveDataUserLogued(userLogued)
                }
                /*val userDisplayName = edtNombres.getText().toString().trim()
                val userLastName = edtApellidos.getText().toString().trim()
                val userIdentificationName = edtCedula.getText().toString().trim()
                val userPhone = edtCelular.getText().toString().trim()

                /**Call the Firebase methods**/
                try {
                    updateUser(userDisplayName,userLastName,userIdentificationName,userPhone)
                    updateUserPhoto(imageAccountGlobal)
                } catch (e: Exception) {
                    e.printStackTrace()
                }
                */
            }
        }
    }

    private fun saveDataUserLogued(userLogued: Usuario?) {
        var userLoguedLocal=userLogued
        if(imageAccountGlobal!=null){
            val stringBuilder = StringBuilder()
            stringBuilder.append("data:image/jpeg;base64,")
            stringBuilder.append(android.util.Base64.encodeToString(imageAccountGlobal, android.util.Base64.DEFAULT))
            userLoguedLocal?.blobImagen = Blob(imageAccountGlobal)
            userLoguedLocal?.Fotopefil = stringBuilder.toString()
        }

        userLoguedLocal?.Nombre=edtNombres.text.toString()
        userLoguedLocal?.Apellidos=edtApellidos.text.toString()
        userLoguedLocal?.PhoneNumber=edtCelular.text.toString()
        userLoguedLocal?.DetalleMetodoPagoId=detalleMetodoPagoGlobal?.Id
        userLoguedLocal?.Identificacion=edtCedula.text.toString()
        presenter?.updateUserLogued(userLoguedLocal)
    }


    //endregion

    //region METHODS CAMERA
     fun takePictureWithCamera(fragment: AccountFragment) {
        //EasyImage.openCamera(fragment, 0)
        val intent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)

        startActivityForResult(intent, REQUEST_CAMERA)
    }

     fun choosePhotoFromGallery(fragment: AccountFragment) {
        val galleryIntent = Intent(Intent.ACTION_PICK,
                android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
         galleryIntent.setType("image/*")
        startActivityForResult(galleryIntent, REQUEST_GALLERY)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode == Activity.RESULT_CANCELED) {
            return
        } else if (resultCode == Activity.RESULT_OK) {
            if (requestCode == REQUEST_GALLERY) {
                if (data != null) {
                    val contentURI = data.data
                    try {
                        isFoto = true
                        imageBitmapAccountGlobal = MediaStore.Images.Media.getBitmap(context?.contentResolver, contentURI)
                        imageAccountGlobal = convertBitmapToByte(imageBitmapAccountGlobal!!)

                        if(presenter?.checkConnection()!!){
                            updateUserPhoto(imageAccountGlobal)
                        }else{
                            imageBitmapAccountGlobal=null
                            imageAccountGlobal=null
                            verificateConnectionChangeFoto()
                        }
                        //imageGlobalRutaFoto = saveImage(bitmap)
                        //Toast.makeText(context, "Image Saved!", Toast.LENGTH_SHORT).show()
                        //user_image?.setImageBitmap(bitmap)
                    } catch (e: IOException) {
                        e.printStackTrace()
                        Toast.makeText(context, "Failed!", Toast.LENGTH_SHORT).show()
                    }
                }

            } else if (requestCode == REQUEST_CAMERA) {
                if (data != null) {
                    isFoto = true
                    imageBitmapAccountGlobal = data.extras?.get("data") as Bitmap
                    imageAccountGlobal = convertBitmapToByte(imageBitmapAccountGlobal!!)

                    //user_image?.setImageBitmap(thumbnail)
                    if(presenter?.checkConnection()!!){
                        updateUserPhoto(imageAccountGlobal)
                    }else{
                        imageBitmapAccountGlobal=null
                        imageAccountGlobal=null
                        verificateConnectionChangeFoto()
                    }

                    //imageGlobalRutaFoto = saveImage(thumbnail)
                    // Toast.makeText(context, thumbnail.toString(), Toast.LENGTH_SHORT).show()
                }
            }
        }
    }

    fun convertBitmapToByte(bitmapCompressed: Bitmap): ByteArray? {
        val stream = ByteArrayOutputStream()
        bitmapCompressed.compress(Bitmap.CompressFormat.PNG, 90, stream)
        return stream.toByteArray()
        //return BitmapFactory.decodeByteArray(byteFormat, 0, byteFormat.size)
    }

    //endregion


    //region METHODS FIREBASE




    private fun updateUserPhoto(photoByteArray: ByteArray?) {
        // Create file metadata with property to delete
        val metadata = StorageMetadata.Builder()
                .setContentType("image/png")
                .setContentLanguage("en")
                .build()

        /*mStorageRef?.child(mCurrentUserID!!)?.putBytes(photoByteArray!!, metadata)?.addOnCompleteListener { task ->
            if (!task.isSuccessful) {
                //error saving photo
                Toast.makeText(activity, "Error", Toast.LENGTH_LONG).show()
            } else {
                //success saving photo
                val userPhotoLink = task.result.downloadUrl!!.toString()
                //now update the database with this user photo
                val childUpdates = HashMap<String, Any>()
                childUpdates["imagen"] = userPhotoLink
                mUserDBRef?.child(mCurrentUserID)?.updateChildren(childUpdates)

                Toast.makeText(activity, "Foto Actualizada", Toast.LENGTH_LONG).show()
            }
        }*/
        if(photoByteArray != null)
        {
           var progressDialog =  ProgressDialog(activity);
            progressDialog.setTitle("Uploading...");
            progressDialog.show();

              mStorageRef?.child(mCurrentUserID!!)?.putBytes(photoByteArray!!, metadata)?.addOnCompleteListener { task ->
                  if (!task.isSuccessful) {
                      //error saving photo
                      Toast.makeText(activity, "Error", Toast.LENGTH_LONG).show()
                      progressDialog.dismiss();
                      imageBitmapAccountGlobal=null
                      imageAccountGlobal=null
                  } else {
                      //success saving photo
                      val userPhotoLink = task.result.downloadUrl!!.toString()
                      //now update the database with this user photo
                      val childUpdates = HashMap<String, Any>()
                      childUpdates["imagen"] = userPhotoLink
                      mUserDBRef?.child(mCurrentUserID)?.updateChildren(childUpdates)
                      user_image?.setImageBitmap(imageBitmapAccountGlobal)
                      (activity as MenuMainActivity).circleImageView.setImageBitmap(imageBitmapAccountGlobal)
                      progressDialog.dismiss();
                      saveDataUserLogued(userLogued)
                      Toast.makeText(activity, "Foto Actualizada", Toast.LENGTH_LONG).show()
                  }
              }?.addOnFailureListener{
                          exception ->
                          progressDialog.dismiss();
                          imageBitmapAccountGlobal=null
                          imageAccountGlobal=null
                          Toast.makeText(activity, "Fallo  actualizacion de la imagen: "+exception.message, Toast.LENGTH_SHORT).show();

              }?.addOnProgressListener{
                          taskSnapshot ->
                          var progress = (100.0*taskSnapshot.getBytesTransferred()/taskSnapshot
                                  .getTotalByteCount());
                          progressDialog.setMessage("Esperando "+progress.toInt()+"%");
              }
        }
    }


    //endregion



    //region OVERRIDES METHODS
    override fun onDestroy() {
        super.onDestroy()
        presenter?.onDestroy()
    }


    override fun onPause() {
        super.onPause()
        presenter?.onPause( activity!!.applicationContext)
    }

    override fun onResume() {
        presenter?.onResume(activity!!.applicationContext)
        super.onResume()
    }

    //endregion

}
