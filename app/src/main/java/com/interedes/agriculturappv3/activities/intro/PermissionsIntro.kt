package com.interedes.agriculturappv3.activities.intro

import android.Manifest
import android.app.Activity
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Color
import android.os.Bundle
import android.support.v4.app.ActivityCompat
import android.support.v4.app.Fragment
import android.widget.Toast
import com.github.paolorotolo.appintro.AppIntro
import com.github.paolorotolo.appintro.AppIntroFragment
import com.github.paolorotolo.appintro.model.SliderPage
import com.interedes.agriculturappv3.R
import android.view.WindowManager
import android.os.Build
import android.view.View
import android.view.ViewGroup
import android.widget.FrameLayout
import android.widget.ImageView
import com.github.paolorotolo.appintro.AppIntro2
import com.interedes.agriculturappv3.activities.home.HomeActivity
import com.interedes.agriculturappv3.modules.models.usuario.Usuario
import com.interedes.agriculturappv3.modules.models.usuario.Usuario_Table
import com.interedes.agriculturappv3.modules.productor.ui.main_menu.MenuMainActivity
import com.raizlabs.android.dbflow.sql.language.SQLite


/**
 * Created by EnuarMunoz on 11/06/18.
 */
class PermissionsIntro: AppIntro() {

    var PERMISSIONS = arrayOf(Manifest.permission.CAMERA,
            Manifest.permission.WRITE_EXTERNAL_STORAGE,
            Manifest.permission.READ_EXTERNAL_STORAGE,
            Manifest.permission.ACCESS_FINE_LOCATION,
            Manifest.permission.ACCESS_COARSE_LOCATION,
            Manifest.permission.READ_PHONE_STATE,
            Manifest.permission.SEND_SMS,
            Manifest.permission.READ_SMS,
            Manifest.permission.RECEIVE_SMS,
            Manifest.permission.READ_CONTACTS
    )


    val PERMISSIONS_UBICACION = arrayOf(
            Manifest.permission.ACCESS_FINE_LOCATION,
            Manifest.permission.ACCESS_COARSE_LOCATION
    )

    val PERMISSIONS_STORAGE_CAMERA = arrayOf(
            Manifest.permission.CAMERA,
            Manifest.permission.WRITE_EXTERNAL_STORAGE,
            Manifest.permission.READ_EXTERNAL_STORAGE
            )

    val PERMISSIONS_PHONE_SMS= arrayOf(Manifest.permission.READ_PHONE_STATE,
            Manifest.permission.READ_SMS,
            Manifest.permission.SEND_SMS,
            Manifest.permission.RECEIVE_SMS,
            Manifest.permission.READ_CONTACTS)

    var PERMISSION_ALL: Int? = 9

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)


        /*if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            val w = window // in Activity's onCreate() for instance
            w.setFlags(WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS, WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS)
        }*/





       /* val sliderPage1 = SliderPage()
        sliderPage1.setTitle("Bienvenido!")

        //sliderPage1.setDescription("This is a demo of the AppIntro library, with permissions being requested on a slide!")
        sliderPage1.setImageDrawable(R.drawable.splash_complete)
        sliderPage1.setBgColor(Color.WHITE)
        sliderPage1.descColor=Color.BLACK
        sliderPage1.titleColor=Color.BLACK
        addSlide(AppIntroFragment.newInstance(sliderPage1))

        val sliderPage2 = SliderPage()
        sliderPage2.setTitle("Permiso Requerido")
        sliderPage2.setDescription("Permite AgriculturApp acceder a tu ubicacion.")
        sliderPage2.setImageDrawable(R.drawable.intro_location)
        sliderPage2.setBgColor(Color.WHITE)
        sliderPage2.titleColor=Color.BLACK
        sliderPage2.descColor=Color.BLACK
        addSlide(AppIntroFragment.newInstance(sliderPage2))

        val sliderPage3 = SliderPage()
        sliderPage3.setTitle("Permiso Requerido")
        sliderPage3.setDescription("Permite AgriculturApp acceder a la camara y galleria de imagenes.")
        sliderPage3.setImageDrawable(R.drawable.intro_photo_gallery)
        sliderPage3.setBgColor(Color.WHITE)
        sliderPage3.descColor=Color.BLACK
        sliderPage3.titleColor=Color.BLACK
        addSlide(AppIntroFragment.newInstance(sliderPage3))

        val sliderPage4 = SliderPage()
        sliderPage4.setTitle("Permiso Requerido")
        sliderPage4.titleColor=Color.BLACK
        sliderPage4.setDescription("Permite AgriculturApp acceder a tus contactos.")
        sliderPage4.setImageDrawable(R.drawable.intro_sms_contacts)
        sliderPage4.setBgColor(Color.WHITE)
        sliderPage4.descColor=Color.BLACK
        sliderPage4.titleColor=Color.BLACK
        addSlide(AppIntroFragment.newInstance(sliderPage4))

        val sliderPage5 = SliderPage()
        sliderPage5.setTitle("Permiso Requerido")
        sliderPage5.setDescription("Permite AgriculturApp generar copias de seguridad de tu informacion!")
        sliderPage5.setImageDrawable(R.drawable.intro_data_base)
        sliderPage5.setBgColor(Color.WHITE)
        sliderPage5.titleColor=Color.BLACK
        sliderPage5.descColor=Color.BLACK
        addSlide(AppIntroFragment.newInstance(sliderPage5))

        */


        // Hide Skip/Done button.
        showSkipButton(false)

        //isProgressButtonEnabled = true
        // SHOW or HIDE the statusbar
        showStatusBar(true)

        // getPager().
        //pager.addOnPageChangeListener();

        // Here we load a string array with a camera permission, and tell the library to request permissions on slide 2

       // if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
        addSlide(SampleSlideJava.newInstance(R.layout.layout_intro_1))
        addSlide(SampleSlideJava.newInstance(R.layout.layout_intro_2))
        addSlide(SampleSlideJava.newInstance(R.layout.layout_intro_3))
        addSlide(SampleSlideJava.newInstance(R.layout.layout_intro_4))
        addSlide(SampleSlideJava.newInstance(R.layout.layout_intro_5))
        askForPermissions(PERMISSIONS_UBICACION, 2)
        askForPermissions(PERMISSIONS_STORAGE_CAMERA, 3)
        askForPermissions(PERMISSIONS_PHONE_SMS, 4)
       /* }else{
            addSlide(SampleSlideJava.newInstance(R.layout.layout_intro_1))
        }*/




       /* // Declare a new image view
        val imageView = ImageView(this)
        // Bind a drawable to the imageview
        imageView.setImageResource(R.drawable.fondo_login_opt)
        // Set background color
        imageView.setBackgroundColor(Color.WHITE)
        // Set layout params
        imageView.layoutParams = FrameLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT)
        // Bind the background to the intro
        setBackgroundView(imageView)*/
    }




    private fun requestPermission() {
        //ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, PERMISSION_REQUEST_CODE);
        ActivityCompat.requestPermissions(this, PERMISSIONS, PERMISSION_ALL!!)
    }

    override fun onSkipPressed(currentFragment: Fragment?) {
        super.onSkipPressed(currentFragment)
        finish()
    }

    override fun onDonePressed(currentFragment: Fragment?) {
        super.onDonePressed(currentFragment)
        if (doPermissionGrantedStuffs()!!) {
            val usuario= getLastUserLogued()
            if (usuario != null) {
                val i = Intent(this, MenuMainActivity::class.java)
                i.flags = Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK
                startActivity(i)
            }else{
                val i = Intent(this, HomeActivity::class.java)
                i.flags = Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK
                startActivity(i)
            }
           // finish()
        }else{

            if(!doPermissionGrantedStuffsUbicacion()){
                getPager().currentItem = 1
                askForPermissions(PERMISSIONS_UBICACION, 2)
                askForPermissions(PERMISSIONS_STORAGE_CAMERA, 3)
                askForPermissions(PERMISSIONS_PHONE_SMS, 4)

            }else if(!doPermissionGrantedStuffsCameraandStorage()){

                getPager().currentItem = 2
                askForPermissions(PERMISSIONS_STORAGE_CAMERA, 3)
                askForPermissions(PERMISSIONS_PHONE_SMS, 4)
            }
            else if(!doPermissionGrantedStuffsPhoneSmS()){

                getPager().currentItem = 3
                askForPermissions(PERMISSIONS_PHONE_SMS, 4)
            }
        }


        /*if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {


        }else{
            var usuario= getLastUserLogued()
            if (usuario != null) {
                val i = Intent(this, MenuMainActivity::class.java)
                i.flags = Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK
                startActivity(i)
            }
            finish()
        }*/
    }

    private fun getLastUserLogued(): Usuario? {
        var usuarioLoguedHome = SQLite.select().from(Usuario::class.java)
                .where(Usuario_Table.UsuarioRemembered.eq(true))
                .querySingle()
        return usuarioLoguedHome
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
         if (grantResults.size > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                if (doPermissionGrantedStuffs()!!) {
                    //getPager().setCurrentItem(2);
                }else{
                    //getPager().currentItem = 0
                }
                ///Toast.makeText(this,"Permiso Aqui", Toast.LENGTH_LONG).show()
         } else {

                /*

                if(!doPermissionGrantedStuffsUbicacion()){
                    getPager().currentItem = 1
                    askForPermissions(PERMISSIONS_UBICACION, 2)

                }else if(!doPermissionGrantedStuffsCameraandStorage()){

                    getPager().currentItem = 2
                    askForPermissions(PERMISSIONS_STORAGE_CAMERA, 3)
                }
                else if(!doPermissionGrantedStuffsPhoneSmS()){

                    getPager().currentItem = 3
                    askForPermissions(PERMISSIONS_PHONE_SMS, 4)
                }

                Toast.makeText(this,"Permiso denegado", Toast.LENGTH_LONG).show()

                */
         }

        //if()
    }


    fun doPermissionGrantedStuffs(): Boolean? {
        for (permission in PERMISSIONS) {
            if (ActivityCompat.checkSelfPermission(this, permission) != PackageManager.PERMISSION_GRANTED) {
                val response = false
                return response
            }
        }
        /// String SIMSerialNumber=tm.getSimSerialNumber();
        val response = true
        return response
    }


    fun doPermissionGrantedStuffsUbicacion(): Boolean {
        for (permission in PERMISSIONS_UBICACION) {
            if (ActivityCompat.checkSelfPermission(this, permission) != PackageManager.PERMISSION_GRANTED) {
                val response = false
                return response
            }
        }
        /// String SIMSerialNumber=tm.getSimSerialNumber();
        val response = true
        return response
    }


    fun doPermissionGrantedStuffsCameraandStorage(): Boolean {
        for (permission in PERMISSIONS_STORAGE_CAMERA) {
            if (ActivityCompat.checkSelfPermission(this, permission) != PackageManager.PERMISSION_GRANTED) {
                val response = false
                return response
            }
        }
        /// String SIMSerialNumber=tm.getSimSerialNumber();
        val response = true
        return response
    }


    fun doPermissionGrantedStuffsPhoneSmS(): Boolean {
        for (permission in PERMISSIONS_PHONE_SMS) {
            if (ActivityCompat.checkSelfPermission(this, permission) != PackageManager.PERMISSION_GRANTED) {
                val response = false
                return response
            }
        }
        /// String SIMSerialNumber=tm.getSimSerialNumber();
        val response = true
        return response
    }


}