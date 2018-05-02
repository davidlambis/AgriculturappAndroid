package com.interedes.agriculturappv3.productor.modules.account

import android.Manifest
import android.app.Activity
import android.app.ProgressDialog
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.support.v4.app.ActivityCompat
import android.support.v4.app.Fragment
import android.support.v4.content.ContextCompat
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import com.google.android.gms.tasks.OnCompleteListener
import com.google.android.gms.tasks.Task
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageMetadata
import com.google.firebase.storage.StorageReference
import com.google.firebase.storage.UploadTask

import com.interedes.agriculturappv3.R
import com.interedes.agriculturappv3.productor.models.chat.UserFirebase
import com.interedes.agriculturappv3.productor.modules.ui.main_menu.MenuMainActivity
import com.squareup.picasso.Picasso
import kotlinx.android.synthetic.main.activity_menu_main.*
import kotlinx.android.synthetic.main.content_recyclerview.*
import kotlinx.android.synthetic.main.fragment_account.*
import java.io.ByteArrayOutputStream
import java.io.IOException
import java.util.HashMap

class AccountFragment : Fragment(),View.OnClickListener {

    //CÁMARA
    val IMAGE_DIRECTORY = "/Productos"
    val REQUEST_GALLERY = 1
    val REQUEST_CAMERA = 2
    var imageGlobal: ByteArray? = null
    // var imageGlobalRutaFoto: String? = null
    var isFoto: Boolean? = false

    //FIREBASE
    private var mUserDBRef: DatabaseReference? = null
    private var mStorageRef: StorageReference? = null
    private var mCurrentUserID: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

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
        populateTheViews()
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

                    Picasso.with(activity).load(userPhoto).placeholder(R.drawable.ic_foto_producto_square).into(user_image)
                    edtNombres.setText(userName)
                    edtApellidos.setText(userLastName)
                    edtCedula.setText(userIdentification)

                } catch (e: Exception) {
                    e.printStackTrace()
                }
            }
            override fun onCancelled(databaseError: DatabaseError) {

            }
        })
    }


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
                val userDisplayName = edtNombres.getText().toString().trim()
                val userLastName = edtApellidos.getText().toString().trim()
                val userIdentificationName = edtCedula.getText().toString().trim()

                /**Call the Firebase methods**/
                try {
                    updateUser(userDisplayName,userLastName,userIdentificationName)
                    updateUserPhoto(imageGlobal)
                } catch (e: Exception) {
                    e.printStackTrace()
                }
            }
        }
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
                        val bitmap = MediaStore.Images.Media.getBitmap(context?.contentResolver, contentURI)
                        imageGlobal = convertBitmapToByte(bitmap)
                        //imageGlobalRutaFoto = saveImage(bitmap)
                        //Toast.makeText(context, "Image Saved!", Toast.LENGTH_SHORT).show()
                        user_image?.setImageBitmap(bitmap)
                    } catch (e: IOException) {
                        e.printStackTrace()
                        Toast.makeText(context, "Failed!", Toast.LENGTH_SHORT).show()
                    }

                }

            } else if (requestCode == REQUEST_CAMERA) {
                if (data != null) {
                    isFoto = true
                    val thumbnail = data.extras?.get("data") as Bitmap
                    imageGlobal = convertBitmapToByte(thumbnail)
                    user_image?.setImageBitmap(thumbnail)
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


    private fun updateUser(newDisplayName: String, newLastName: String,newIdentificacion: String) {
        val childUpdates = HashMap<String, Any>()
        childUpdates["nombre"] = newDisplayName
        childUpdates["apellido"] = newLastName
        childUpdates["cedula"] = newIdentificacion
        mUserDBRef?.child(mCurrentUserID)?.updateChildren(childUpdates)
    }

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
                  } else {
                      //success saving photo
                      val userPhotoLink = task.result.downloadUrl!!.toString()
                      //now update the database with this user photo
                      val childUpdates = HashMap<String, Any>()
                      childUpdates["imagen"] = userPhotoLink
                      mUserDBRef?.child(mCurrentUserID)?.updateChildren(childUpdates)
                      progressDialog.dismiss();
                      Toast.makeText(activity, "Foto Actualizada", Toast.LENGTH_LONG).show()
                      progressDialog.dismiss();
                  }
              }?.addOnFailureListener{
                          exception ->
                          progressDialog.dismiss();
                          Toast.makeText(activity, "Failed "+exception.message, Toast.LENGTH_SHORT).show();

              }?.addOnProgressListener{
                          taskSnapshot ->
                          var progress = (100.0*taskSnapshot.getBytesTransferred()/taskSnapshot
                                  .getTotalByteCount());
                          progressDialog.setMessage("Uploaded "+progress.toInt()+"%");
              }
        }
    }


    //endregion

}
