package com.interedes.agriculturappv3.modules.account

import android.Manifest
import android.app.Activity
import android.app.ProgressDialog
import android.content.Context
import android.content.DialogInterface
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Color
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.os.Environment
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
import com.daimajia.androidanimations.library.Techniques
import com.daimajia.androidanimations.library.YoYo
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
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
import com.interedes.agriculturappv3.services.resources.MetodoPagoResources
import com.interedes.agriculturappv3.services.resources.RequestAccessPhoneResources
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
import id.zelory.compressor.Compressor;
import java.io.File
import java.text.SimpleDateFormat
import java.util.*


class AccountFragment : Fragment(),View.OnClickListener,IMainViewAccount.MainView {

    //CÁMARA
    val IMAGE_DIRECTORY = "/Productos"


    // var imageGlobalRutaFoto: String? = null
    var isFoto: Boolean? = false

    //FIREBASE
    private var mUserDBRef: DatabaseReference? = null
    private var mStorageRef: StorageReference? = null
    private var mCurrentUserID: String? = null

    private var userFirebaseVerificate:FirebaseUser?=null


    var presenter: IMainViewAccount.Presenter? = null

    //Progress
    private var hud: KProgressHUD?=null

    //Model
    var metodoPagoGlobal:MetodoPago?=null
    var detalleMetodoPagoGlobal:DetalleMetodoPago?=null
    var userLogued:Usuario?=null

    var imageAccountGlobal: ByteArray? = null
    var imageBitmapAccountGlobal: Bitmap? = null

    private var mCurrentPhotoPath: String? = null



    private var mCurrentPhotoFile: File? = null
    private var dateFormatter: SimpleDateFormat? = null
    private var destFile: File? = null
    private var imageCaptureUri: Uri? = null



    private val PERMISSION_REQUEST_CODE = 1
    var PERMISSION_ALL = 3
    var PERMISSIONS = arrayOf(Manifest.permission.CAMERA,Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.READ_EXTERNAL_STORAGE)
    var IS_ACCESS_CAMERA=false

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
        user_image_check.setOnClickListener(this)

       userFirebaseVerificate=presenter?.verificateUserLoguedFirebaseFirebase()
        if(userFirebaseVerificate==null){
            mCurrentUserID=presenter?.getUserLogued()?.IdFirebase
        }else{
            mCurrentUserID=userFirebaseVerificate?.uid
        }


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

            if(userLogued?.blobImagenUser!=null){
                // val bitmap = BitmapFactory.decodeByteArray(foto, 0, foto!!.size)
                // imgTipoProducto.setImageBitmap(bitmap)
                try {
                    val foto = userLogued?.blobImagenUser?.blob
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
                    if (metodoPagoGlobal?.Nombre.equals(MetodoPagoResources.TRANFERENCIA_BANCARIA)) {
                        textInputLayoutNumeroCuenta?.visibility = View.VISIBLE
                        edtNumeroCuenta?.setText(userLogued?.NumeroCuenta)
                    }
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

            else if(textInputLayoutNumeroCuenta.visibility==View.VISIBLE && edtNumeroCuenta?.text.toString().isEmpty()){
                textInputLayoutNumeroCuenta?.setError(getString(R.string.error_field_required))
                focusView = textInputLayoutNumeroCuenta
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
        errorUpdatePhotoAccount()
        onMessageError(R.color.grey_luiyi, error)
    }


    fun errorUpdatePhotoAccount(){
        if(userLogued?.blobImagenUser!=null){
            try {
                val foto = userLogued?.blobImagenUser?.blob
                imageBitmapAccountGlobal = BitmapFactory.decodeByteArray(foto, 0, foto!!.size)
                imageAccountGlobal = convertBitmapToByte(imageBitmapAccountGlobal!!)
                user_image.setImageBitmap(imageBitmapAccountGlobal)
            }catch (ex:Exception){
                var ss= ex.toString()
                Log.d("Convert Image", "defaultValue = " + ss);
            }
        }else{
            imageBitmapAccountGlobal=null
            imageAccountGlobal=null
        }
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
            errorUpdatePhotoAccount()
            dialog.dismiss()

        })
        builder.setIcon(R.drawable.ic_produccion_cultivo);
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

            textInputLayoutNumeroCuenta?.visibility = View.GONE
            edtNumeroCuenta?.setText("")

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

            if (metodoPagoGlobal?.Nombre.equals(MetodoPagoResources.TRANFERENCIA_BANCARIA)) {
                textInputLayoutNumeroCuenta?.visibility = View.VISIBLE
                edtNumeroCuenta?.setText("")
            } else {
                textInputLayoutNumeroCuenta?.visibility = View.GONE
                edtNumeroCuenta?.setText("")
            }
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

                /*if (ActivityCompat.checkSelfPermission(activity!!.applicationContext, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
                    ActivityCompat.requestPermissions(activity!!, arrayOf(Manifest.permission.CAMERA, Manifest.permission.READ_EXTERNAL_STORAGE,
                            Manifest.permission.WRITE_EXTERNAL_STORAGE), 1000)
                    return
                }*/

                IS_ACCESS_CAMERA=true

                if (Build.VERSION.SDK_INT >= 23) {
                    if (!hasPermissions(activity, *PERMISSIONS)) {
                        requestPermission()
                    } else {
                        val response = doPermissionGrantedStuffs()
                        if (response) {
                            takePictureWithCamera(this)
                            //startActivityForResult(Intent(Intent.ACTION_OPEN_DOCUMENT_TREE), READ_REQUEST_CODE)
                        }
                    }
                } else {
                    val response = doPermissionGrantedStuffs()
                    if (response) {
                        takePictureWithCamera(this)
                        //startActivityForResult(Intent(Intent.ACTION_OPEN_DOCUMENT_TREE), READ_REQUEST_CODE)
                    }
                }
            }
            R.id.user_take_picture_gallery -> {

                IS_ACCESS_CAMERA=false

                if (Build.VERSION.SDK_INT >= 23) {
                    if (!hasPermissions(activity, *PERMISSIONS)) {
                        requestPermission()
                    } else {
                        val response = doPermissionGrantedStuffs()
                        if (response) {
                            choosePhotoFromGallery(this)
                            //startActivityForResult(Intent(Intent.ACTION_OPEN_DOCUMENT_TREE), READ_REQUEST_CODE)
                        }
                    }
                } else {
                    val response = doPermissionGrantedStuffs()
                    if (response) {
                        choosePhotoFromGallery(this)
                        //startActivityForResult(Intent(Intent.ACTION_OPEN_DOCUMENT_TREE), READ_REQUEST_CODE)
                    }
                }

                /*if (ActivityCompat.checkSelfPermission(activity!!.applicationContext, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
                    ActivityCompat.requestPermissions(activity!!, arrayOf(Manifest.permission.CAMERA, Manifest.permission.READ_EXTERNAL_STORAGE,
                            Manifest.permission.WRITE_EXTERNAL_STORAGE), 1000)
                    return
                }
                choosePhotoFromGallery(this)*/
            }


            R.id.user_image_cancel -> {
                isFoto = false
                user_image?.setImageResource(R.drawable.ic_foto_producto_square)
                showButtonsImageUser()
                if(userLogued?.blobImagenUser!=null){
                    // val bitmap = BitmapFactory.decodeByteArray(foto, 0, foto!!.size)
                    // imgTipoProducto.setImageBitmap(bitmap)
                    try {
                        val foto = userLogued?.blobImagenUser?.blob
                        imageBitmapAccountGlobal = BitmapFactory.decodeByteArray(foto, 0, foto!!.size)
                        imageAccountGlobal = convertBitmapToByte(imageBitmapAccountGlobal!!)
                        user_image.setImageBitmap(imageBitmapAccountGlobal)

                        //Picasso.with(activity).load(userPhoto).placeholder(R.drawable.ic_foto_producto_square).into(user_image)
                    }catch (ex:Exception){
                        var ss= ex.toString()
                        Log.d("Convert Image", "defaultValue = " + ss);
                    }
                }


            }

            R.id.user_image_check->{
                showButtonsImageUser()
                presenter?.changeFotoUserAccount()
            }

            R.id.btnSaveAccount -> {
                if(presenter?.validarUpdateUser()!!){
                    userLogued=presenter?.getUserLogued()
                    saveDataUserLogued()
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

    private fun saveDataUserLogued() {
        var usuario=presenter?.getUserLogued()
        if(imageAccountGlobal!=null){
            /*
            userLogued?.blobImagenUser = Blob(imageAccountGlobal)
            val stringBuilder = StringBuilder()
            stringBuilder.append("data:image/jpeg;base64,")
            stringBuilder.append(android.util.Base64.encodeToString(imageAccountGlobal, android.util.Base64.DEFAULT))
            userLogued?.Fotopefil = stringBuilder.toString()*/
            user_image?.isDrawingCacheEnabled = true
            val bitmap = user_image?.getDrawingCache()
            val byte = convertBitmapToByte(bitmap!!)
            usuario?.blobImagenUser = Blob(byte)
            val stringBuilder = StringBuilder()
            stringBuilder.append("data:image/jpeg;base64,")
            stringBuilder.append(android.util.Base64.encodeToString(byte, android.util.Base64.DEFAULT))
            usuario?.Fotopefil = stringBuilder.toString()
        }

        usuario?.Nombre=edtNombres.text.toString()
        usuario?.Apellidos=edtApellidos.text.toString()
        usuario?.PhoneNumber=edtCelular.text.toString()
        usuario?.DetalleMetodoPagoId=detalleMetodoPagoGlobal?.Id
        usuario?.Identificacion=edtCedula.text.toString()
        usuario?.NumeroCuenta=edtNumeroCuenta.text.toString()
        presenter?.updateUserLogued(usuario!!)
    }
    //endregion

    //region METHODS CAMERA
     fun takePictureWithCamera(fragment: AccountFragment) {
        //EasyImage.openCamera(fragment, 0)

       /* mCurrentPhotoFile =  File(Environment.getExternalStorageDirectory().path+"/" + IMAGE_DIRECTORY);
        if (!mCurrentPhotoFile?.exists()!!) {
            mCurrentPhotoFile?.mkdirs();
        }
        destFile = File(mCurrentPhotoFile, "img_"
                + dateFormatter?.format(Date()).toString() + ".png")
        imageCaptureUri = Uri.fromFile(destFile)

        val intentCamera = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
        intentCamera.putExtra(MediaStore.EXTRA_OUTPUT, imageCaptureUri)
        startActivityForResult(intentCamera, REQUEST_CAMERA)*/
        val intent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
        startActivityForResult(intent, RequestAccessPhoneResources.ACCESS_REQUEST_CAMERA)

       /* val intent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
        if (intent.resolveActivity(activity?.getPackageManager()) != null) {
            // Create the File where the photo should go
            var photoFile: File? = null
            try {
                photoFile = createImageFile()
                mCurrentPhotoFile=photoFile
            } catch (ex: IOException) {
                // Error occurred while creating the File
                //Log.i(TAG, "IOException")
            }
            // Continue only if the File was successfully created
            if (photoFile != null) {
                intent.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(photoFile))
                startActivityForResult(intent, REQUEST_CAMERA)
                //startActivityForResult(cameraIntent, REQUEST_IMAGE_CAPTURE)
            }
        }*/
    }


    fun  createImageFile():File? {

        var image:File? =null

        try {
            // Create an image file name
            var timeStamp =  SimpleDateFormat("yyyyMMdd_HHmmss").format( Date())
            var imageFileName = "JPEG_" + timeStamp + "_"
            var storageDir = Environment.getExternalStoragePublicDirectory(
                    Environment.DIRECTORY_PICTURES);
            image = File.createTempFile(
                    imageFileName,  // prefix
                    ".jpg",         // suffix
                    storageDir      // directory
            );
            // Save a file: path for use with ACTION_VIEW intents
            mCurrentPhotoPath = "file:" + image.getAbsolutePath();
        }catch (ex:Exception){
            image=null
        }
        return image;
    }


     fun choosePhotoFromGallery(fragment: AccountFragment) {
        val galleryIntent = Intent(Intent.ACTION_PICK,
                android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
         galleryIntent.setType("image/*")
        startActivityForResult(galleryIntent, RequestAccessPhoneResources.ACCESS_REQUEST_GALLERY)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode == Activity.RESULT_CANCELED) {
            return
        } else if (resultCode == Activity.RESULT_OK) {
            if (requestCode == RequestAccessPhoneResources.ACCESS_REQUEST_GALLERY) {
                if (data != null) {
                    val contentURI = data.data
                    try {
                        isFoto = true

                        var rutaGaleria=getRealPathFromURI(contentURI)
                        var file=getFileRuta(rutaGaleria)
                        val compressedImage = Compressor(activity)
                                .setMaxHeight(400)
                                .setQuality(100)
                                .compressToBitmap(file)

                        imageBitmapAccountGlobal =compressedImage
                        ///imageBitmapAccountGlobal = MediaStore.Images.Media.getBitmap(context?.contentResolver, contentURI)

                        imageAccountGlobal = convertBitmapToByte(imageBitmapAccountGlobal!!)

                        hideButtonsImageUser()

                        YoYo.with(Techniques.Pulse)
                                .repeat(5)
                                .playOn(user_image_check)


                        //imageGlobalRutaFoto = saveImage(bitmap)
                        //Toast.makeText(context, "Image Saved!", Toast.LENGTH_SHORT).show()
                        user_image?.setImageBitmap(imageBitmapAccountGlobal)
                    } catch (e: IOException) {
                        e.printStackTrace()
                        Toast.makeText(context, "Failed!", Toast.LENGTH_SHORT).show()
                    }
                }

            } else if (requestCode == RequestAccessPhoneResources.ACCESS_REQUEST_CAMERA) {
                if (data != null) {

                    try{
                        /*   val contentURI = data.data
                    var rutaGaleria=getRealPathFromURI(contentURI)
                    var file=getFileRuta(rutaGaleria)
                    val compressedImage = Compressor(activity)
                            .setMaxHeight(400)
                            .setQuality(100)
                            .compressToBitmap(file)*/


                        //isFoto = true
                        ////imageBitmapAccountGlobal = compressedImage
                        imageBitmapAccountGlobal = data.extras?.get("data") as Bitmap
                        Toast.makeText(context, imageCaptureUri.toString(), Toast.LENGTH_SHORT).show()

                        //imageBitmapAccountGlobal = data.extras?.get("data") as Bitmap
                        /* val compressedImage = Compressor(activity)
                                 .setMaxHeight(400)
                                 .setQuality(100)
                                 .compressToBitmap(mCurrentPhotoFile)
                         imageBitmapAccountGlobal=compressedImage*/
                        imageAccountGlobal = convertBitmapToByte(imageBitmapAccountGlobal!!)
                        //user_image?.setImageBitmap(imageBitmapAccountGlobal)

                        hideButtonsImageUser()

                        YoYo.with(Techniques.Pulse)
                                .repeat(5)
                                .playOn(user_image_check)


                        user_image?.setImageBitmap(imageBitmapAccountGlobal)
                        //imageGlobalRutaFoto = saveImage(thumbnail)
                        // Toast.makeText(context, thumbnail.toString(), Toast.LENGTH_SHORT).show()


                    } catch (e: IOException) {
                    e.printStackTrace()
                    Toast.makeText(context, "Failed!", Toast.LENGTH_SHORT).show()
                    }

                }
            }
        }
    }


    private fun showButtonsImageUser(){
        user_image_check.visibility=View.GONE
        user_image_cancel.visibility=View.GONE
        user_take_picture_camera.visibility=View.VISIBLE
        user_take_picture_gallery.visibility=View.VISIBLE

    }

    private fun hideButtonsImageUser(){
        user_image_check.visibility=View.VISIBLE
        user_image_cancel.visibility=View.VISIBLE
        user_take_picture_camera.visibility=View.GONE
        user_take_picture_gallery.visibility=View.GONE
    }

    private fun getFileRuta(path: String): File? {
        var file: File? = null
        file = File(path)
        return file
    }

    private fun getRealPathFromURI(contentURI: Uri): String {
        val result: String
        val cursor = activity?.getContentResolver()?.query(contentURI, null, null, null, null)
        if (cursor == null) { // Source is Dropbox or other similar local file path
            result = contentURI.path
        } else {
            cursor!!.moveToFirst()
            val idx = cursor!!.getColumnIndex(MediaStore.Images.ImageColumns.DATA)
            result = cursor!!.getString(idx)
            cursor!!.close()
        }
        return result
    }

    override fun uplodateFotoUserAccount(){
        mCurrentUserID=presenter?.verificateUserLoguedFirebaseFirebase()?.uid
        updateUserPhoto(imageAccountGlobal)
    }

    fun convertBitmapToByte(bitmapCompressed: Bitmap): ByteArray? {
        val stream = ByteArrayOutputStream()
        bitmapCompressed.compress(Bitmap.CompressFormat.PNG, 100, stream)
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
                      saveDataUserLogued()
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


    //region PERMISSIONS

    fun hasPermissions(context: Context?, vararg permissions: String): Boolean {
        if (android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.M && context != null && permissions != null) {
            for (permission in permissions) {
                if (ActivityCompat.checkSelfPermission(context, permission) != PackageManager.PERMISSION_GRANTED) {
                    return false
                }
            }
        }
        return true
    }

    private fun requestPermission() {
        //ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, PERMISSION_REQUEST_CODE);
        ActivityCompat.requestPermissions(activity!!, PERMISSIONS, PERMISSION_ALL)
    }


    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        when (requestCode) {
            PERMISSION_REQUEST_CODE -> if (grantResults.size > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                if(doPermissionGrantedStuffs()){
                    if(IS_ACCESS_CAMERA){
                        takePictureWithCamera(this)
                    }else{
                        choosePhotoFromGallery(this)
                    }
                }
            } else {
                Toast.makeText(activity,
                        "Permiso denegado", Toast.LENGTH_LONG).show()

            }
        }
    }


    fun doPermissionGrantedStuffs(): Boolean {
        /// String SIMSerialNumber=tm.getSimSerialNumber();
        for (permission in PERMISSIONS) {
            if (ActivityCompat.checkSelfPermission(activity!!, permission) != PackageManager.PERMISSION_GRANTED) {
                val response = false
                return response
            }
        }
        val response = true
        return response
    }

    //endregion

}
