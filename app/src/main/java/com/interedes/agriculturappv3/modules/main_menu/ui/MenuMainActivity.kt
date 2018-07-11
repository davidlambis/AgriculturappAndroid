package com.interedes.agriculturappv3.modules.productor.ui.main_menu

import android.Manifest
import android.annotation.SuppressLint
import android.app.*
import android.content.Context
import android.content.DialogInterface
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.BitmapFactory
import android.graphics.Color
import android.graphics.drawable.Drawable
import android.net.Uri
import android.os.Build
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.os.Environment
import android.os.Handler
import android.support.design.widget.BottomNavigationView
import android.support.design.widget.NavigationView
import android.support.design.widget.Snackbar
import android.support.v4.app.ActivityCompat
import android.support.v4.app.Fragment
import android.support.v4.app.FragmentManager
import android.support.v4.app.FragmentTransaction
import android.support.v4.content.ContextCompat
import android.support.v4.view.GravityCompat
import android.support.v7.app.ActionBarDrawerToggle
import android.support.v7.app.AlertDialog
import android.support.v7.widget.Toolbar
import android.util.Log
import android.view.*
import android.widget.TextView
import com.interedes.agriculturappv3.R
import com.interedes.agriculturappv3.modules.models.usuario.Usuario
import com.interedes.agriculturappv3.modules.main_menu.fragment.ui.adapter.AdapterFragmetMenu
import com.interedes.agriculturappv3.modules.main_menu.fragment.ui.MainMenuFragment
import com.interedes.agriculturappv3.modules.main_menu.ui.MenuPresenterImpl
import com.interedes.agriculturappv3.modules.main_menu.ui.MainViewMenu
import com.interedes.agriculturappv3.services.resources.Const_Resources
import android.widget.LinearLayout
import android.widget.Toast
import com.afollestad.materialdialogs.DialogAction
import com.afollestad.materialdialogs.GravityEnum
import com.afollestad.materialdialogs.MaterialDialog
import com.afollestad.materialdialogs.Theme
import com.afollestad.materialdialogs.simplelist.MaterialSimpleListAdapter
import com.afollestad.materialdialogs.simplelist.MaterialSimpleListItem
import com.daimajia.androidanimations.library.Techniques
import com.daimajia.androidanimations.library.YoYo
import com.google.android.gms.common.ConnectionResult
import com.google.android.gms.common.GoogleApiAvailability
import com.google.firebase.auth.FirebaseAuth
import com.interedes.agriculturappv3.AgriculturApplication
import com.interedes.agriculturappv3.activities.chat.chat_sms.user_sms_ui.UserSmsActivity
import com.interedes.agriculturappv3.activities.chat.online.conversations_user.ConversationsUsersActivity
import com.interedes.agriculturappv3.activities.intro.PermissionsIntro
import com.interedes.agriculturappv3.activities.login.ui.LoginActivity
import com.interedes.agriculturappv3.config.DataSource
import com.interedes.agriculturappv3.modules.account.AccountFragment
import com.interedes.agriculturappv3.modules.comprador.productos.ProductosCompradorFragment
import com.interedes.agriculturappv3.modules.models.sincronizacion.QuantitySync
import com.interedes.agriculturappv3.modules.notification.NotificationActivity
import com.interedes.agriculturappv3.modules.ofertas.OfertasFragment
import com.interedes.agriculturappv3.modules.productor.asistencia_tecnica_module.AsistenciaTecnicaFragment
import com.interedes.agriculturappv3.services.jobs.ChatRunJob
import com.interedes.agriculturappv3.services.chat.SharedPreferenceHelper
import com.interedes.agriculturappv3.services.jobs.ControlPlagasJob
import com.interedes.agriculturappv3.services.jobs.DataSyncJob
import com.interedes.agriculturappv3.services.jobs.FotosEnfermedadesInsumosjob
import com.interedes.agriculturappv3.services.resources.*
import com.interedes.agriculturappv3.services.sms.NotificationService
import com.kaopiz.kprogresshud.KProgressHUD
import com.nightonke.boommenu.BoomMenuButton
import com.nightonke.boommenu.Types.BoomType
import com.nightonke.boommenu.Types.ButtonType
import com.nightonke.boommenu.Types.PlaceType
import com.nightonke.boommenu.Util
import de.hdodenhof.circleimageview.CircleImageView
import kotlinx.android.synthetic.main.activity_menu_main.*
import kotlinx.android.synthetic.main.custom_message_toast.view.*
import kotlinx.android.synthetic.main.dialog_confirm.view.*
import kotlinx.android.synthetic.main.dialog_image_download_plaga.view.*
import kotlinx.android.synthetic.main.dialog_sync_data.view.*
import java.io.*
import java.text.Normalizer
import java.util.*


class MenuMainActivity : AppCompatActivity(), NavigationView.OnNavigationItemSelectedListener, MainViewMenu.MainView,View.OnClickListener {
    var isAppRunning: Boolean = false

    // var coordsService: CoordsService? = null
    //var coordsGlobal:Coords?=null
    var presenter: MenuPresenterImpl? = null
    var usuario_logued: Usuario? = null
    var inflaterGlobal: MenuInflater? = null
    var menuItemGlobal: MenuItem? = null
    var menuItemNotificationsGlobal: View? = null
    var mPhoneNumber: String? = null


    //Progress
    private var hud: KProgressHUD?=null

    //Firebase
    //FIREBASE
    var mCurrentUserID: String? = null
    var mAuth: FirebaseAuth? = null
    //Alert Dialog Sync
    var viewDialogSync:View?= null
    var _dialogSync: AlertDialog? = null
    //Chat Type

    //private var DIALOG_SET_TYPE_CHAT: Int = -1

    //Permission
    //public static final int MY_PERMISSIONS_REQUEST_READ_PHONE_STATE = 1;
    private val PERMISSION_REQUEST_CODE = 1
    internal var PERMISSION_ALL = 1
    internal var PERMISSIONS = arrayOf(Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.READ_EXTERNAL_STORAGE)


    private val READ_REQUEST_CODE_BACKUP = 10


    private var IS_IMPORT = false
    private var IS_EXPORT = false

    private val PLAY_SERVICES_RESOLUTION_REQUEST = 9000
    var  notificationCount:TextView?=null

    var extrasGlobal:Bundle?=null

    companion object {
        var instance: MenuMainActivity? = null
    }

    private val mOnNavigationItemSelectedListener = BottomNavigationView.OnNavigationItemSelectedListener { item ->
        when (item.itemId) {
            R.id.navigation_songs -> {

                return@OnNavigationItemSelectedListener true
            }
            R.id.navigation_albums -> {

                return@OnNavigationItemSelectedListener true
            }
        }
        false
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_menu_main)
        //Fabric.with(this,  Crashlytics());
        instance=this
        //Presenter
        presenter = MenuPresenterImpl(this)
        (presenter as MenuPresenterImpl).onCreate()
        setToolbarInjection()

        if (getLastUserLogued() != null) {
            usuario_logued =  presenter?.getLastUserLogued()
        }

        setNavDrawerInjection()
        setAdapterFragment()
        // this.coordsService = CoordsService(this)
        //fragmentManager.beginTransaction().add(R.id.container, AccountingFragment()).commit()
        //Firebase
        mCurrentUserID = FirebaseAuth.getInstance()?.currentUser?.uid
        if(mCurrentUserID==null){
            mCurrentUserID=getLastUserLogued()?.IdFirebase
        }

        val bottomNavigation: BottomNavigationView = findViewById(R.id.navigationViewBotom)
        bottomNavigation.setOnNavigationItemSelectedListener(mOnNavigationItemSelectedListener)
        //Status Chat
        presenter?.makeUserOnline(this)

        getListasIniciales()

        setupMenuFloating()

        septupInjection()
    }

    fun navigationVerificateDownloadPlagasyEnfermedades(){

        val inflater = this.layoutInflater
        var viewDialogConfirm = inflater.inflate(R.layout.dialog_confirm, null)
        viewDialogConfirm?.txtTitleConfirm?.setText("")
        //viewDialogConfirm?.txtTitleConfirm?.setText(usuario?.Nombre+" ${usuario?.Apellidos}")

        var content =String.format(getString(R.string.verifcate_download_info))
        viewDialogConfirm?.txtDescripcionConfirm?.setText(content)

        MaterialDialog.Builder(this)
                .title(getString(R.string.tittle_dowload_plagas))
                .customView(viewDialogConfirm!!, true)
                .positiveText(R.string.confirm)
                .negativeText(R.string.cancel)
                .positiveColorRes(R.color.light_green_800)
                .negativeColorRes(R.color.light_green_800)
                .titleGravity(GravityEnum.CENTER)
                .titleColorRes(R.color.colorPrimary)
                .contentColorRes(android.R.color.white)
                .backgroundColorRes(R.color.material_blue_grey_800)
                .dividerColorRes(R.color.light_green_800)
                .btnSelector(R.drawable.md_btn_selector_custom, DialogAction.POSITIVE)
                .positiveColor(Color.WHITE)
                .negativeColorAttr(android.R.attr.textColorSecondaryInverse)
                .theme(Theme.DARK)
                .onPositive(
                        { dialog1, which ->
                            if(presenter?.checkConnection()!!){
                                showProgressBar()
                                FotosEnfermedadesInsumosjob.scheduleFotosJob()
                                dialog1.dismiss()
                            }else{
                                onMessageToast(R.color.red_900,getString(R.string.verifcate_conection_info))
                                showDowloadPlagasEnfermedades(getString(R.string.message_conection_plaga))
                                dialog1.dismiss()
                            }

                        })
                .onNegative({ dialog1, which ->
                    dialog1.dismiss()
                    showDowloadPlagasEnfermedades("")
                    //onMessageToas(getString(R.string.content_sms_not_send),R.color.red_900)
                })
                .show()
    }

    fun showDowloadPlagasEnfermedades(string:String){
        val inflater = this.layoutInflater
        val viewDialog = inflater.inflate(R.layout.dialog_image_download_plaga, null)
        viewDialog?.txtDescripcion?.setText("")
        viewDialog?.txtDescripcion?.setText(String.format(getString(R.string.message_downloa_plaga),string))
        //var content =String.format(getString(R.string.verifcate_download_info))
        //viewDialogConfirm?.txtDescripcionConfirm?.setText(content)
       val dialog= MaterialDialog.Builder(this)
                .title(getString(R.string.tittle_dowload_plagas))
                .customView(viewDialog, true)
                .positiveText(R.string.confirm)
                .positiveColorRes(R.color.light_green_800)
                .titleGravity(GravityEnum.CENTER)
                .titleColorRes(R.color.colorPrimary)
                .contentColorRes(android.R.color.white)
                .backgroundColorRes(R.color.material_blue_grey_800)
                .dividerColorRes(R.color.light_green_800)
                .btnSelector(R.drawable.md_btn_selector_custom, DialogAction.POSITIVE)
                .positiveColor(Color.WHITE)
                .theme(Theme.DARK)
                .onPositive(
                        { dialog1, which ->
                            dialog1.dismiss()
                            //Toast.makeText(activity,"Enviar oferta",Toast.LENGTH_SHORT).show()
                            // _dialogOferta?.dismiss()
                        })
                .build()

        val lp = WindowManager.LayoutParams()
        lp.copyFrom(dialog.getWindow().getAttributes())
        lp.width = WindowManager.LayoutParams.MATCH_PARENT
        lp.height = WindowManager.LayoutParams.MATCH_PARENT
        dialog.show()
        dialog.getWindow().setAttributes(lp)

    }
    /*override fun syncFotosInsumosPlagas() {
        FotosEnfermedadesInsumosjob.scheduleFotosJob();
    }*/

    fun registerWithNotificationHubs() {
        //Log.i(FragmentActivity.TAG, " Registering with NotificationLocal Hubs")
        if (checkPlayServices()) {
            // Start IntentService to register this application with GCM.
            //RegistrationIntentService(this@MenuActivity, user, NEW_LOGIN_USER)
            //val intent = Intent(this, RegistrationIntentService::class.java)
            //startService(intent)
        }
    }


    fun  checkPlayServices():Boolean {
        var apiAvailability = GoogleApiAvailability.getInstance();
        var resultCode = apiAvailability.isGooglePlayServicesAvailable(this);
        if (resultCode != ConnectionResult.SUCCESS) {
            if (apiAvailability.isUserResolvableError(resultCode)) {
                apiAvailability.getErrorDialog(this, resultCode, PLAY_SERVICES_RESOLUTION_REQUEST)
                        .show();
            } else {
                //Log.i(TAG, "This device is not supported by Google Play Services.");
                onMessageToast(R.color.red_900,"This device is not supported by Google Play Services.");
                finish();
            }
            return false;
        }
        return true;
    }

    private fun septupInjection() {
        if (Build.VERSION.SDK_INT >= 23) {
            if (!hasPermissions(this, *PERMISSIONS)) {
                startActivity(Intent(getBaseContext(), PermissionsIntro::class.java))
            }
        }
        //Service SMS
        val notificationServiceIntent = Intent(this, NotificationService::class.java)
        startService(notificationServiceIntent)

        val usuarioLogued=getLastUserLogued()
        if (usuarioLogued?.RolNombre.equals(RolResources.PRODUCTOR)) {
            DataSyncJob.scheduleJob();
            ChatRunJob.scheduleJobChat()
            ControlPlagasJob.scheduleJobChat()
                val myTimerr = 5000
                ///Mensage
                Handler().postDelayed(Runnable {
                    try {
                        if(presenter?.checkListPlagas()!!<=0){
                            navigationVerificateDownloadPlagasyEnfermedades()
                        }else{
                            showDowloadPlagasEnfermedades("")
                        }
                    } catch (e: Exception) {
                    }
                }, myTimerr.toLong())

        } else if (usuarioLogued?.RolNombre.equals(RolResources.COMPRADOR)) {
            ChatRunJob.scheduleJobChat()
        }

        extrasGlobal= intent.extras
        if (extrasGlobal != null) {
            if (extrasGlobal!!.containsKey(TagNavigationResources.TAG_NAVIGATE_CHAT_ONLINE)) {
                val chatOnline = Intent(this, ConversationsUsersActivity::class.java)
                chatOnline.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
                startActivity(chatOnline)
            }else if(extrasGlobal!!.containsKey(TagNavigationResources.TAG_NAVIGATE_OFERTAS)){
                replaceFragment(OfertasFragment())
            }else if(extrasGlobal!!.containsKey(TagNavigationResources.TAG_NAVIGATE_CHAT_SMS)){
                val chat = Intent(this, UserSmsActivity::class.java)
                chat.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
                startActivity(chat)
            }
            else if(extrasGlobal!!.containsKey(TagNavigationResources.TAG_NAVIGATE_CONTROL_PLAGAS)){
                val myTimerr = 500
                ///Mensage
                Handler().postDelayed(Runnable {
                    try {
                        val bundle = Bundle()
                        bundle.putLong(TagNavigationResources.TAG_NAVIGATE_CONTROL_PLAGAS, TagNavigationResources.NAVIGATE_CONTROL_PLAGAS)
                        //val asistenciaTecnicaFragment: AsistenciaTecnicaFragment
                        val asistenciaTecnicaFragment = AsistenciaTecnicaFragment()
                        asistenciaTecnicaFragment.arguments = bundle
                        replaceFragment(asistenciaTecnicaFragment)

                    } catch (e: Exception) {
                    }
                }, myTimerr.toLong())
            }
        }
        /*var usuarioLogued=getLastUserLogued()
        if (usuarioLogued?.RolNombre.equals(RolResources.PRODUCTOR)) {
            presenter?.syncQuantityData(true)
        }*/
    }


    private fun setupMenuFloating() {

        val mActionBar = supportActionBar
        mActionBar!!.setDisplayShowHomeEnabled(false)
        mActionBar.setDisplayShowTitleEnabled(false)
        val mInflater = LayoutInflater.from(this)
        var mCustomView = mInflater.inflate(R.layout.custom_actionbar, null)

        var boomMenuButtonInActionBar:BoomMenuButton = mCustomView.findViewById(R.id.boom)
        mActionBar.setCustomView(mCustomView)
        mActionBar.setDisplayShowCustomEnabled(true)
        (mCustomView.getParent() as Toolbar).setContentInsetsAbsolute(0, 0)
       // var boomMenuButton:BoomMenuButton = findViewById(R.id.boom)


        var circleSubButtonDrawables:Array<Drawable?>?=null
        var drawablesResource:IntArray? = null
        var circleSubButtonTexts:Array<String>? = null
        var subButtonColors:Array<IntArray>?=null

        var placeType:PlaceType?=null


        val usuarioLogued=getLastUserLogued()

        if (usuarioLogued?.RolNombre.equals(RolResources.PRODUCTOR)) {

            placeType=PlaceType.HAM_4_1
            //placeType=PlaceType.CIRCLE_4_1
             drawablesResource = MenuBoomResources.drawablesResourceProductor
            circleSubButtonDrawables = arrayOfNulls<Drawable>(4)
            for (i in 0..3)
                circleSubButtonDrawables[i] = ContextCompat.getDrawable(this, drawablesResource[i])
             circleSubButtonTexts =MenuBoomResources.circleSubButtonTextsProductor
            subButtonColors = Array(4) { IntArray(2) }
            for (i in 0..3) {
                subButtonColors[i][1] = MenuBoomResources.GetRandomColor()
                subButtonColors[i][0] = Util.getInstance().getPressedColor(subButtonColors[i][1])
            }

        } else if (usuarioLogued?.RolNombre.equals(RolResources.COMPRADOR)) {
            //placeType=PlaceType.CIRCLE_1_1
            placeType=PlaceType.HAM_1_1
            circleSubButtonDrawables = arrayOfNulls<Drawable>(1)
            drawablesResource = MenuBoomResources.drawablesResourceComprador
            for (i in 0..0)
                circleSubButtonDrawables[i] = ContextCompat.getDrawable(this, drawablesResource[i])
            circleSubButtonTexts =MenuBoomResources.circleSubButtonTextsComprador
            subButtonColors = Array(1) { IntArray(2) }
            for (i in 0..0) {
                subButtonColors[i][1] = MenuBoomResources.GetRandomColor()
                subButtonColors[i][0] = Util.getInstance().getPressedColor(subButtonColors[i][1])
            }
        }

        // init the BMB with delay
        /*
        boomMenuButtonInActionBar.postDelayed(Runnable {
            // Now with Builder, you can init BMB more convenient
           var ss= BoomMenuButton.Builder()

                    .subButtons(circleSubButtonDrawables, subButtonColors, circleSubButtonTexts)
                    .button(ButtonType.CIRCLE)
                    .boom(BoomType.PARABOLA)
                    .place(PlaceType.CIRCLE_4_1)
                    .subButtonsShadow(Util.getInstance().dp2px(2f), Util.getInstance().dp2px(2f))
                    .onSubButtonClick { buttonIndex ->
                        Toast.makeText(
                                this,
                                "On click " + circleSubButtonTexts[buttonIndex],
                                Toast.LENGTH_SHORT).show()
                    }
                    .init(boomMenuButtonInActionBar)
        }, 1);
*/
        boomMenuButtonInActionBar.postDelayed(Runnable {
            // Now with Builder, you can init BMB more convenient
            BoomMenuButton.Builder()
                    //.addSubButton(ContextCompat.getDrawable(this, R.drawable.ic_sms), subButtonColors[0], "0")
                    //.addSubButton(ContextCompat.getDrawable(this, R.drawable.ic_action_backup), subButtonColors[1], "1")
                    ///.addSubButton(ContextCompat.getDrawable(this, R.drawable.ic_icon_database_backup), subButtonColors[2], "2")
                    .subButtons(circleSubButtonDrawables, subButtonColors, circleSubButtonTexts)
                    .button(ButtonType.HAM)
                    .boom(getBoomType())
                    .place(placeType)
                    .subButtonTextColor(ContextCompat.getColor(this, R.color.black))
                    .subButtonsShadow(Util.getInstance().dp2px(2F), Util.getInstance().dp2px(2F))
                    .onSubButtonClick { buttonIndex ->
                        if(circleSubButtonTexts!![buttonIndex].equals(MenuBoomResources.CHAT)){
                            showAlertTypeChat()
                        }else if(circleSubButtonTexts!![buttonIndex].equals(MenuBoomResources.SINCRONIZAR)){
                            presenter?.syncQuantityData(false)

                        }else if(circleSubButtonTexts!![buttonIndex].equals(MenuBoomResources.EXPORTAR)){

                            IS_IMPORT = false
                            IS_EXPORT = true

                            if (Build.VERSION.SDK_INT >= 23) {
                                if (!hasPermissions(applicationContext, *PERMISSIONS)) {
                                    requestPermission()
                                } else {
                                    val response = doPermissionGrantedStuffs()
                                    if (response) {
                                        startActivityForResult(Intent(Intent.ACTION_OPEN_DOCUMENT_TREE), READ_REQUEST_CODE_BACKUP)
                                    }
                                }
                            } else {
                                val response = doPermissionGrantedStuffs()
                                if (response) {
                                    startActivityForResult(Intent(Intent.ACTION_OPEN_DOCUMENT_TREE), READ_REQUEST_CODE_BACKUP)
                                }
                            }

                        }
                        else if(circleSubButtonTexts!![buttonIndex].equals(MenuBoomResources.IMPORTAR)){
                            IS_IMPORT = true
                            IS_EXPORT = false

                            if (Build.VERSION.SDK_INT >= 23) {
                                if (!hasPermissions(applicationContext, *PERMISSIONS)) {
                                    requestPermission()
                                } else {
                                    val response = doPermissionGrantedStuffs()
                                    if (response) {
                                        performFileSearch()
                                    }

                                }
                            } else {
                                val response = doPermissionGrantedStuffs()
                                if (response) {
                                    performFileSearch()
                                }
                            }

                        }

                       /* Toast.makeText(
                                this,
                                "On click " + circleSubButtonTexts[buttonIndex],
                                Toast.LENGTH_SHORT).show()*/
                    }
                    .init(boomMenuButtonInActionBar)

        }, 1)

    }

    private fun getBoomType(): BoomType {
        var random= Random().nextInt(5);  // [0...4] [min = 0, max = 4] Equivalente a BoomType.HORIZONTAL_THROW_2

        if (random==0) {
            return BoomType.LINE
        } else if (random==1) {
            return BoomType.PARABOLA
        } else if (random==2) {
            return BoomType.HORIZONTAL_THROW
        } else if (random==3) {
            return BoomType.PARABOLA_2
        } else if (random==4) {
            return BoomType.HORIZONTAL_THROW_2
        }
        return BoomType.PARABOLA
    }

    //region IMPORTING AND EXPORTEING DB
    fun performFileSearch() {
        // ACTION_OPEN_DOCUMENT is the intent to choose a file via the system's file
        // browser.
        val intent = Intent(Intent.ACTION_OPEN_DOCUMENT)

        // Filter to only show results that can be "opened", such as a
        // file (as opposed to a list of contacts or timezones)
        intent.addCategory(Intent.CATEGORY_OPENABLE)

        // Filter to show only images, using the image MIME data type.
        // If one wanted to search for ogg vorbis files, the type would be "audio/ogg".
        // To search for all documents available via installed storage providers,
        // it would be "*/*".
        intent.type = "*/*"
        startActivityForResult(intent, READ_REQUEST_CODE_BACKUP)
    }



    private fun exportDB(path: String) {
        // TODO Auto-generated method stub
        try {
            val posicion: Array<String>
            posicion = path.split(":".toRegex()).dropLastWhile { it.isEmpty() }.toTypedArray()

            var directory = ""
            if (posicion.size > 1) {
                directory = posicion[1]
            }

            val sd = Environment.getExternalStorageDirectory()
            //File data = Environment.getDataDirectory();
            if (sd.canWrite()) {

                var usuario= presenter?.getLastUserLogued()

               // val nombre= limpiarAcentos(usuario?.Nombre+"_"+usuario?.Apellidos)

                val nombre= usuario?.Nombre+"_"+usuario?.Apellidos
                val deleteSpaceNombre= nombre.replace(" ","")
                val deletenMinuNombre= deleteSpaceNombre.replace("ñ","n")
                val deletenMayuNombre= deletenMinuNombre.replace("Ñ","N")

                val currentDBPath = String.format("%s%s", applicationContext.getDatabasePath(DataSource.NAME).toString(), ".db")
                val backupDBPath = "$directory/$deletenMayuNombre"+"_db_agricultur_app.db"

                val currentDB = File(currentDBPath)
                val backupDB = File(sd, backupDBPath)


                val src = FileInputStream(currentDB).channel
                val dst = FileOutputStream(backupDB).channel
                dst.transferFrom(src, 0, src.size())
                src.close()
                dst.close()
                Toast.makeText(baseContext, backupDB.toString(),
                        Toast.LENGTH_LONG).show()
                Toast.makeText(baseContext, "Se exporto correctamente en: $backupDBPath",
                        Toast.LENGTH_LONG).show()

            }
        } catch (e: Exception) {
            Toast.makeText(baseContext, e.toString(), Toast.LENGTH_LONG)
                    .show()
        }
    }

    fun limpiarAcentos( cadena:String):String {
    var limpio:String ="";
    if (cadena !=null) {
        var valor = cadena;
        valor = valor.toUpperCase();
        // Normalizar texto para eliminar acentos, dieresis, cedillas y tildes
        limpio = Normalizer.normalize(valor, Normalizer.Form.NFD) ;
        // Quitar caracteres no ASCII excepto la enie, interrogacion que abre, exclamacion que abre, grados, U con dieresis.
        limpio = limpio.replace("[^\\p{ASCII}(N\u0303)(n\u0303)(\u00A1)(\u00BF)(\u00B0)(U\u0308)(u\u0308)]", "");
        // Regresar a la forma compuesta, para poder comparar la enie con la tabla de valores
        limpio = Normalizer.normalize(limpio, Normalizer.Form.NFC);
    }
    return limpio;
}

    //importing database
    private fun importDB(path: String) {
        // TODO Auto-generated method stub
        try {
            val posicion: Array<String>
            posicion = path.split(":".toRegex()).dropLastWhile { it.isEmpty() }.toTypedArray()
            var directory = ""
            if (posicion.size > 1) {
                directory = posicion[1]
            }
            val sd = Environment.getExternalStorageDirectory()
            val data = Environment.getDataDirectory()
            if (sd.canWrite()) {

                val currentDBPath = String.format("%s%s", applicationContext.getDatabasePath(DataSource.NAME).toString(),".db")

                val file = File(currentDBPath)
                var res = false

                if (file.exists()) {
                    res = true

                } else {
                    res = false
                }
                val backupDBPath = "/$directory"
                val backupDB = File(currentDBPath)
                val currentDB = File(sd, backupDBPath)

               /* if (backupDB.exists()) {
                    FileUtils.copyFile( FileInputStream(currentDBPath),  FileOutputStream(backupDB));
                    // Access the copied database so SQLiteHelper will cache it and mark
                    // it as created.
                }*/

                val src = FileInputStream(currentDB).channel
                val dst = FileOutputStream(backupDB).channel
                dst.transferFrom(src, 0, src.size())
                src.close()
                dst.close()

                /*val src = FileInputStream(currentDB).channel
                val dst = FileOutputStream(backupDB).channel
                dst.transferFrom(src, 0, src.size())
                src.close()
                dst.close()*/
                Toast.makeText(baseContext, backupDB.toString(),
                        Toast.LENGTH_LONG).show()
                Toast.makeText(baseContext, "DB Imported Succesfult",
                        Toast.LENGTH_LONG).show()
                restart(this, 1)
            }
        } catch (e: Exception) {
            Toast.makeText(baseContext, e.toString(), Toast.LENGTH_LONG)
                    .show()
        }
    }

    @SuppressLint("WrongConstant")
    fun restart(context: Context, delay: Int) {
        var delay = delay
        if (delay == 0) {
            delay = 1
        }
        Log.e("", "restarting app")
        val restartIntent = context.packageManager
                .getLaunchIntentForPackage(context.packageName)
        @SuppressLint("WrongConstant") val intent = PendingIntent.getActivity(
                context, 0,
                restartIntent, Intent.FLAG_ACTIVITY_CLEAR_TOP)
        val manager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager
        manager.set(AlarmManager.RTC, System.currentTimeMillis() + delay, intent)
        System.exit(2)
    }


    //endregion

    override fun getListasIniciales() {
        presenter?.getListasIniciales()
    }

    fun setAdapterFragment() {
        val fragmentManager = supportFragmentManager
        val MainMenuFragment = MainMenuFragment()
        val ProductosCompradorFragment = ProductosCompradorFragment()

        if (getLastUserLogued()?.RolNombre.equals(RolResources.PRODUCTOR)) {

            container.setPadding(0,0,0,0)
            navigationViewBotom.visibility=View.GONE
            AdapterFragmetMenu(MainMenuFragment, fragmentManager, R.id.container)
        } else if (getLastUserLogued()?.RolNombre.equals(RolResources.COMPRADOR)) {
            container.setPadding(0,0,0, resources.getDimension(R.dimen.margin_55).toInt())
            navigationViewBotom.visibility=View.VISIBLE
            AdapterFragmetMenu(ProductosCompradorFragment, fragmentManager, R.id.container)
        }
    }

    //TODO pasar al repository
    fun getLastUserLogued(): Usuario? {
      return presenter?.getLastUserLogued()
    }

    //region SETUP INJECTION


    private fun setToolbarInjection() {
        setSupportActionBar(toolbar)
        supportActionBar!!.setDisplayShowTitleEnabled(false)
        supportActionBar!!.setDisplayHomeAsUpEnabled(false)//devolver
        toolbar.setTitle(getString(R.string.title_menu))
    }

    private fun setNavDrawerInjection() {

        val mActionBarDrawerToggle = ActionBarDrawerToggle(this, drawer_layout, toolbar, R.string.nav_open, R.string.nav_close)
        mActionBarDrawerToggle.syncState()
        navigationView.setNavigationItemSelectedListener(this)
        drawer_layout.addDrawerListener(mActionBarDrawerToggle)
        val header = navigationView.getHeaderView(0)
        val headerViewHolder = HeaderViewHolder(header)


        headerViewHolder.tvNombreUsuario.setText(String.format(getString(R.string.nombre_usuario_nav), usuario_logued?.Nombre, usuario_logued?.Apellidos))
        headerViewHolder.tvIdentificacion.setText(usuario_logued?.Email)

        if(usuario_logued?.blobImagenUser!=null){
            // val bitmap = BitmapFactory.decodeByteArray(foto, 0, foto!!.size)
            // imgTipoProducto.setImageBitmap(bitmap)
            try {
                val foto = usuario_logued?.blobImagenUser?.blob
                var imageBitmapAccountGlobal = BitmapFactory.decodeByteArray(foto, 0, foto!!.size)
                headerViewHolder.circleImageView.setImageBitmap(imageBitmapAccountGlobal)
            }catch (ex:Exception){
                var ss= ex.toString()
                Log.d("Convert Image", "defaultValue = " + ss);
            }
        }

        if (getLastUserLogued()?.RolNombre.equals(RolResources.PRODUCTOR)) {
            //Menu Lateral
            headerViewHolder.itemSyncronizarDatos.visibility=View.VISIBLE
            headerViewHolder.itemSyncCloud.visibility=View.VISIBLE

        } else if (getLastUserLogued()?.RolNombre.equals(RolResources.COMPRADOR)) {
            //Menu Lateral
            headerViewHolder.itemSyncronizarDatos.visibility=View.GONE
            headerViewHolder.itemSyncCloud.visibility=View.GONE

        }


        headerViewHolder.tvNombreUsuario.setOnClickListener(this)
        headerViewHolder.circleImageView.setOnClickListener(this)



        headerViewHolder.itemSyncronizarDatos.setOnClickListener(this)
        headerViewHolder.itemCerrarSesion.setOnClickListener(this)
        headerViewHolder.itemOfertas.setOnClickListener(this)
        headerViewHolder.itemSyncCloud.setOnClickListener(this)

       // headerViewHolder.itemStartJob.setOnClickListener(this)
       // headerViewHolder.itemStopJob.setOnClickListener(this)



        // val header = navigationView.getHeaderView(0)
        // val headerViewHolder = HeaderViewHolder(header)
        // headerViewHolder.tvNombreUsuario.setText(usuarioLogued.getNombre() + " " + usuarioLogued.getApellido())
        // headerViewHolder.tvCCUsuario.setText("C.C " + usuarioLogued.getCedula())
        // headerViewHolder.tvEmpresaUsuario.setText(usuarioController.getEmpresaById(empresaId).getNombre())
        // if (ciudadId != 0L && departamentoId != 0L) {
        //    headerViewHolder.tvCiudadUsuario.setText(usuarioController.getCiudadById(ciudadId).getNombre() + ","
        //           + usuarioController.getDepartamentoById(departamentoId).getNombre())
        // }
    }
    //endregion

    class HeaderViewHolder(view: View) {
        val tvNombreUsuario: TextView = view.findViewById(R.id.tvNombreUsuario)
        val circleImageView: CircleImageView = view.findViewById(R.id.circleImageView)
        val tvIdentificacion: TextView = view.findViewById(R.id.tvIdentificacion)


       // val itemStartJob: LinearLayout = view.findViewById(R.id.itemStartJobService)
       //  val itemStopJob: LinearLayout = view.findViewById(R.id.itemStopJobService)

        val itemSyncCloud: LinearLayout = view.findViewById(R.id.itemSyncCloud)
        val itemOfertas: LinearLayout = view.findViewById(R.id.itemOfertas)
        val itemComprasRealizadas: LinearLayout = view.findViewById(R.id.itemComprasRealizadas)
        val itemSyncronizarDatos: LinearLayout = view.findViewById(R.id.itemSyncronizarDatos)
        val itemCerrarSesion: LinearLayout = view.findViewById(R.id.itemCerrarSesion)
    }


    //region MENU

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        //getMenuInflater().inflate(R.menu.menu_main_fragment, menu)
        inflaterGlobal = menuInflater
        inflaterGlobal?.inflate(R.menu.menu_main_fragment, menu)

        menuItemGlobal = menu?.findItem(R.id.action_menu_icon)
        menuItemGlobal?.isVisible = false




        val menuItemNotification = menu?.findItem(R.id.action_cartNotification)
        menuItemNotificationsGlobal = menu?.findItem(R.id.action_cartNotification)?.getActionView()
        menuItemNotificationsGlobal?.setOnClickListener(object : View.OnClickListener {
            override fun onClick(v: View) {
                onOptionsItemSelected(menuItemNotification!!)
            }
        })

        updateCountNotifications()
        //contentCountNotifications=notificatioItem?.findViewById<View>(R.id.contentCountNotifications) as FrameLayout;

        //val item1 = menu?.findItem(R.id.action_cartNotification);
       // MenuItemCompat.setActionView(item1, R.layout.custom_action_item_layout_notification);
        ///var content  = MenuItemCompat.getActionView(item1) as FrameLayout;

       /* var itemSync=menu?.findItem(R.id.action_menu_sync)
        if (getLastUserLogued()?.RolNombre.equals(RolResources.PRODUCTOR)) {
            itemSync?.isVisible = true
        } else if (getLastUserLogued()?.RolNombre.equals(RolResources.COMPRADOR)) {
            itemSync?.isVisible = false
        }*/


        return true

        //return super.onCreateOptionsMenu(menu);
        //return true
       // return super.onCreateOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        // Handle item selection
        when (item.itemId) {
            R.id.action_cartNotification -> {
                ///startActivity(Intent(this, ConversationsUsersActivity::class.java))

                val notificationIntent = Intent(this, NotificationActivity::class.java)
                notificationIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
                startActivity(notificationIntent)

                return true
            }

        /*R.id.action_menu_icon_chat -> {
           ///startActivity(Intent(this, ConversationsUsersActivity::class.java))
           showAlertTypeChat()
           return true
       }

      R.id.action_menu_email -> {
           startActivity(Intent(this, UserSmsActivity::class.java))
           return true
       }

            R.id.action_menu_sync -> {
                presenter?.syncQuantityData()
                return true
            }*/

            else -> return super.onOptionsItemSelected(item)
        }
    }
    /*
    override fun showAlertTypeChat(): AlertDialog? {
        var builder = AlertDialog.Builder(this)
        builder.setTitle(getString(R.string.select_type_chat))
        Toast.makeText(this, getString(R.string.message_location_lote), Toast.LENGTH_SHORT).show()
        //var options=arrayOf(getString(R.string.location_type_gps), getString(R.string.location_type_manual))
        var options  = resources.getStringArray(R.array.array_type_chat)
        //La lista varia dependiendo del tipo de conectividad
        builder.setSingleChoiceItems(options, DIALOG_SET_TYPE_CHAT, DialogInterface.OnClickListener { dialog, which ->
            when (which) {
            //Position State Location GPS
                0 -> {
                    DIALOG_SET_TYPE_CHAT=0
                    dialog.dismiss()
                    startActivity(Intent(this, ConversationsUsersActivity::class.java))
                    //scheduleDismiss();
                }
            //Position State Location Manual
                else -> {
                    DIALOG_SET_TYPE_CHAT=1
                    dialog.dismiss()
                    startActivity(Intent(this, UserSmsActivity::class.java))
                    //showAlertDialogSelectUp()
                }
            }
        })
        builder.setIcon(R.drawable.ic_localizacion_finca);
        return builder.show();
    }*/


    override fun showAlertTypeChat(){

        val adapter=MaterialSimpleListAdapter({dialog, index, item ->
            if(index==0){
                dialog.dismiss()

                val chat = Intent(this, UserSmsActivity::class.java)
                chat.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
                startActivity(chat)


            }else{
                dialog.dismiss()

                val chatOnline = Intent(this, ConversationsUsersActivity::class.java)
                chatOnline.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
                startActivity(chatOnline)

            }
        })

        adapter.add(MaterialSimpleListItem.Builder(this)
                    .content(getString(R.string.tittle_chat_sms))
                    .icon(R.drawable.ic_action_email_green)
                    .backgroundColor(Color.WHITE)
                  //  .backgroundColorRes(R.color.black)
                    .build());
        adapter.add(
                 MaterialSimpleListItem.Builder(this)
                    .content(getString(R.string.tittle_chat_online))
                    .icon(R.drawable.ic_action_sms_green)
                         .backgroundColor(Color.WHITE)
                       //  .backgroundColorRes(R.color.black)
                    .build());

        MaterialDialog.Builder(this)
                .title(R.string.select_type_chat)
                .autoDismiss(true)
                .adapter(adapter, null).show();
    }



    //endregion

    //region EVENTS UI
    override fun onNavigationItemSelected(item: MenuItem): Boolean {
        // Handle navigation view item clicks here.
        val id = item.itemId

        /*
        if (id == R.id.nav_proveedores) {
            // Handle the camera action
            // val i = Intent(this, SettingsActivity::class.java)
            // startActivity(i)
        } else if (id == R.id.nav_ofertas) {

        } else if (id == R.id.nav_compras_realizadas) {


        } else if (id == R.id.nav_singout_session) {
            showExit()

        }

        else if (id == R.id.nav_sync_data) {
            showAlertDialogSyncDataConfirm()
        }

        */

        drawer_layout.closeDrawer(GravityCompat.START)
        return true
    }

    fun showExit(): Dialog {
        val builder = AlertDialog.Builder(this)
        builder.setTitle("Confirmación")
        builder.setNegativeButton("Cancelar") { dialog, which -> Log.i("Dialogos", "Confirmacion Cancelada.") }
        builder.setMessage("¿Cerrar Sesión?")
        builder.setPositiveButton("Aceptar") { dialog, which ->
            val userLogued=getLastUserLogued()
            //userLogued?.UsuarioRemembered = false
            //userLogued?.save()
            presenter?.makeUserOffline(this)
            presenter?.logOut(userLogued)

            val intent =  Intent(this, NotificationService::class.java);
            stopService(intent)

            //
            DataSyncJob.cancel();
            ChatRunJob.cancel()
            FotosEnfermedadesInsumosjob.cancel();
            ControlPlagasJob.cancel()


            //startActivity(Intent(this, LoginActivity::class.java)
             //       .addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_NEW_TASK))

             val intentLogin =  Intent(this, LoginActivity::class.java)
            intentLogin.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP
                     or Intent.FLAG_ACTIVITY_NEW_TASK
                     or Intent.FLAG_ACTIVITY_CLEAR_TASK)
            startActivity(intentLogin);


            finish()
        }
        builder.setIcon(R.mipmap.ic_launcher)
        return builder.show()
    }

    override fun onClick(p0: View?) {
        when (p0?.id) {
            R.id.tvNombreUsuario -> {
                drawer_layout.closeDrawer(GravityCompat.START)
                replaceFragment(AccountFragment())
            }

            R.id.circleImageView -> {
                drawer_layout.closeDrawer(GravityCompat.START)
                replaceFragment(AccountFragment())
            }

            R.id.itemCerrarSesion -> {
            showExit()
                }
            R.id.itemSyncCloud -> {
                drawer_layout.closeDrawer(GravityCompat.START)
                presenter?.syncQuantityData(false)



                /*if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {

                    var intent =  Intent(this, ProgresService::class.java)
                    intent.setAction(Const_Resources.ACTION_RUN_ISERVICE)
                    startForegroundService(intent)
                }*/

                //startService(intent);
                //var intent =  Intent(this, ProgresService::class.java)
                //ContextCompat.startForegroundService(this, intent)

                /*
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                    var intent =  Intent(this, JobSyncService::class.java)
                    //intent.setAction(Const_Resources.ACTION_RUN_ISERVICE)
                    //startForegroundService(intent)
                    //this.startForegroundService(intent)
                    ContextCompat.startForegroundService(this, intent)
                    //Service.startForeground()
                }else{
                    startService(intent)
                }*/
                //NotificationManager.startServiceInForeground()
            }



                /*
            R.id.itemStartJobService -> {


                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {







                    var jobService = ComponentName(getPackageName(), JobServiceExample::class.java.name);
                    var jobInfo = JobInfo.Builder(MYJOBID, jobService).setPeriodic(500).build();
                    /*
                     * setPeriodic(long intervalMillis)
                     * Specify that this job should recur with the provided interval,
                     * not more than once per period.
                     */

                    var jobId = jobScheduler?.schedule(jobInfo);
                    if(jobScheduler?.schedule(jobInfo)!!>0){
                        Toast.makeText(this,
                                "Successfully scheduled job: " + jobId,
                                Toast.LENGTH_SHORT).show();
                    }else{
                        Toast.makeText(this,
                                "RESULT_FAILURE: " + jobId,
                                Toast.LENGTH_SHORT).show();
                    }




                } else {
                    TODO("VERSION.SDK_INT < LOLLIPOP")


                }



            }

            R.id.itemStopJobService -> {

                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                    jobScheduler?.cancelAll();

                    /*
                    val allPendingJobs = jobScheduler?.getAllPendingJobs()
                    var s = "";
                    if (allPendingJobs != null) {
                        for( j in allPendingJobs){

                            var jId = j.getId();
                            jobScheduler?.cancel(jId);
                            s += "jobScheduler.cancel(" + jId + " )";
                        }
                    }
                    Toast.makeText(this, s, Toast.LENGTH_SHORT).show();

                    */

                } else {
                    TODO("VERSION.SDK_INT < LOLLIPOP")


                }


                //or
                //jobScheduler.cancelAll();



            }*/



            R.id.itemOfertas -> {
                drawer_layout.closeDrawer(GravityCompat.START)
                replaceFragment(OfertasFragment())
            }

            R.id.itemSyncronizarDatos -> {
                drawer_layout.closeDrawer(GravityCompat.START)
                navigationVerificateDownloadPlagasyEnfermedades()

                navigationVerificateDownloadPlagasyEnfermedades()
            }
        }
    }

    override fun onBackPressed() {
        val count = supportFragmentManager.backStackEntryCount
        if (count == 1) {
            super.onBackPressed()
            if (getLastUserLogued()?.RolNombre.equals(RolResources.PRODUCTOR)) {
                replaceCleanFragment(MainMenuFragment())
            } else if (getLastUserLogued()?.RolNombre.equals(RolResources.COMPRADOR)) {
                replaceCleanFragment(ProductosCompradorFragment())
            }
        } else {
            supportFragmentManager.popBackStack()
        }
        /*
        if (count == 1) {
            super.onBackPressed()
            replaceCleanFragment(MainMenuFragment())

        } else {
            supportFragmentManager.popBackStackImmediate()
        }*/
    }




    //endregion

    //region NAVIGATION FRAGMENTS

    fun replaceFragment(fragment: Fragment) {
        /*val fragmentManager = supportFragmentManager
        val fragmentTransaction = fragmentManager?.beginTransaction()
        fragmentTransaction?.replace(R.id.container, fragment)
        fragmentTransaction?.addToBackStack(fragment::class.java.getName())
        fragmentTransaction?.commit()*/

        val fragmentManager: FragmentManager
        val fragmentTransaction: FragmentTransaction
        fragmentManager = supportFragmentManager
        fragmentTransaction = fragmentManager.beginTransaction()
        val inboxFragment = fragment
        fragmentTransaction.replace(R.id.container, inboxFragment)
        // fragmentTransaction?.addToBackStack(fragment::class.java.getName())
        fragmentTransaction?.addToBackStack(null)
        fragmentTransaction.commit()
    }
/*
    fun replaceCleanFragment(fragment: Fragment) {
        val fragmentManager = supportFragmentManager
        fragmentManager.popBackStack(null, FragmentManager.POP_BACK_STACK_INCLUSIVE)
        val fragmentTransaction = fragmentManager?.beginTransaction()
        fragmentTransaction?.replace(R.id.container, fragment)
        fragmentTransaction?.addToBackStack(null)
        fragmentTransaction?.commit()
    }*/


    fun replaceCleanFragment(fragment: Fragment) {
        val fragmentManager: FragmentManager
        val fragmentTransaction: FragmentTransaction

        fragmentManager = supportFragmentManager
        fragmentManager.popBackStack(null, FragmentManager.POP_BACK_STACK_INCLUSIVE)
        fragmentTransaction = fragmentManager.beginTransaction()
        val inboxFragment = fragment
        fragmentTransaction.replace(R.id.container, inboxFragment)
        fragmentTransaction.commit()

    }
    //endregion

    //region Implemetacion Interfaz ViewMenu
    override fun updateCountNotifications() {
        notificationCount=  menuItemNotificationsGlobal?.findViewById<View>(R.id.cart_badgeNotification) as TextView;
        val countNotifications= presenter?.getCountNotifications()
        if(countNotifications!!>0){
            if(notificationCount!=null){
                try {
                    runOnUiThread {
                        notificationCount?.setText(countNotifications.toString())
                        YoYo.with(Techniques.Pulse)
                                .repeat(10)
                                .playOn(notificationCount)
                    }
                }catch (ex:Exception){
                    var error =ex.toString()
                }
            }
        }
    }

    override fun showProgressHud(){
        hud = KProgressHUD.create(this)
                .setStyle(KProgressHUD.Style.SPIN_INDETERMINATE)
                .setWindowColor(getResources().getColor(R.color.colorPrimary))
                .setLabel("Cargando...", resources.getColor(R.color.white_solid))
                .setDetailsLabel("Sincronizando Informacion")
        hud?.show()
    }

    override fun hideProgressHud(){
        hud?.dismiss()
    }

    override fun showProgressBar() {
        progressBarSync?.visibility=View.VISIBLE
    }

    override fun hideProgressBar() {
        progressBarSync?.visibility=View.GONE
    }


    override fun requestResponseOK() {
        onMessageOk(R.color.colorPrimary,getString(R.string.request_ok));
    }

    override fun requestResponseError(error: String?) {
        onMessageError(R.color.red_900,error);
    }

    /*override fun showAlertDialogSyncDataConfirm(){
        MaterialDialog.Builder(this)
                .title(R.string.title_sync_data_enfermedades)
                .content(R.string.content_message_syn_data_enfermedades, true)
                .positiveText(R.string.confirm)
                .negativeText(R.string.cancel)
                .titleColorRes(R.color.light_green_800)
                .limitIconToDefaultSize()
                .iconRes(R.drawable.ic_plagas)
                .dividerColorRes(R.color.colorPrimary)
                .onPositive(
                        { dialog1, which ->
                            dialog1.dismiss()
                            showProgressBar()
                            ///presenter?.getListSyncEnfermedadesAndTratamiento()
                            FotosEnfermedadesInsumosjob.scheduleFotosJob()
                            onMessageToast(R.color.green_900,"Sincronizando informacion de plagas,enfermedades y tratamientos")
                        })
                .onNegative({ dialog1, which ->
                    dialog1.dismiss()
                })
                .show()
    }*/



    override fun verificateSync(quantitySync: QuantitySync?): AlertDialog? {
        val inflater = this.layoutInflater
        viewDialogSync = inflater.inflate(R.layout.dialog_sync_data , null)

        var message= ""
        if(quantitySync?.CantidadRegistrosSync!!.toInt()==0 && quantitySync?.CantidadUpdatesSync!!.toInt()==0 ){
            message=getString(R.string.tittle_no_sync)
            viewDialogSync?.imageViewSyncNone?.visibility=View.VISIBLE
        }else{
            message=getString(R.string.tiitle_verificate_sync)
            viewDialogSync?.imageViewSyncNone?.visibility=View.GONE
        }

        viewDialogSync?.txtCantidadRegistros?.setText(String.format(getString(R.string.size_register_sync),quantitySync?.CantidadRegistrosSync))
        viewDialogSync?.txtCantidadActulizaciones?.setText(String.format(getString(R.string.size_updates_sync),quantitySync?.CantidadUpdatesSync))


        if(presenter?.checkConnection()!!){
            viewDialogSync?.viewEstateConect?.setBackgroundResource(R.drawable.is_online_user);
            viewDialogSync?.txtconectividad?.setText(getString(R.string.on_connectividad));
        }else{
            viewDialogSync?.viewEstateConect?.setBackgroundResource(R.drawable.is_offline_user);
            viewDialogSync?.txtconectividad?.setText(getString(R.string.off_connectividad));
        }

        val builder = AlertDialog.Builder(this)
                .setView(viewDialogSync)
                .setTitle(getString(R.string.alert))
                .setMessage(message)

                .setPositiveButton(getString(R.string.confirm), DialogInterface.OnClickListener { dialog, which ->
                    //presenter?.syncData()
                    ///AgriculturApplication.instance.showNotification(true)

                    if(presenter?.checkConnection()!!){
                        setQuantitySyncAutomatic(quantitySync)
                    }else{
                        verificateConnection()
                    }

                })
                .setNegativeButton(getString(R.string.cancel), DialogInterface.OnClickListener { dialog, which ->
                    dialog.dismiss()
                })
                .setIcon(R.drawable.ic_cloud_sync)
                .create()
        builder.show()

        _dialogSync=builder
        return _dialogSync
    }

    override fun setQuantitySync(quantitySync: QuantitySync?){
        verificateSync(quantitySync)
    }

    override fun setQuantitySyncAutomatic(quantitySync: QuantitySync?){
        if(quantitySync?.CantidadRegistrosSync!!.toInt()>0 || quantitySync?.CantidadUpdatesSync!!.toInt()>0 ){
            if(presenter?.checkConnection()!!){
                val myTimerr = 2000
                ///Mensage
                Handler().postDelayed(Runnable {
                    try {
                        val preferences = SharedPreferenceHelper.getInstance(this)
                        if(preferences.runingService.equals(Status_Sync_Data_Resources.STOP)){

                            DataSyncJob.cancel()
                            SharedPreferenceHelper.getInstance(this).savePostSyncData(Status_Sync_Data_Resources.RUNNING);
                            AgriculturApplication.instance.showNotification(true)
                        }

                    } catch (e: Exception) {
                    }
                }, myTimerr.toLong())
            }else{
                onMessageToast(R.color.red_900,"Tienes informacion por sincronizar verifica tu conexion")
                //onMessageError(R.color.red_900,"Tienes informacion por sincronizar verifca tu conexion")
            }
        }
    }

    override fun verificateConnection(): AlertDialog? {
        var builder = AlertDialog.Builder(this)
        builder.setTitle(getString(R.string.alert));
        builder.setMessage(getString(R.string.verificate_conexion));
        builder?.setPositiveButton(getString(R.string.confirm), DialogInterface.OnClickListener { dialog, which ->
            dialog.dismiss()
        })
        builder.setIcon(R.mipmap.ic_launcher_round);
        return builder.show();
    }

    override fun onConnectivity() {

       if(viewDialogSync!=null){
           viewDialogSync?.viewEstateConect?.setBackgroundResource(R.drawable.is_online_user);
           viewDialogSync?.txtconectividad?.setText(getString(R.string.on_connectividad));
       }

        /// mUserDBRef?.database?.goOnline()
        /*
        var userStatus= mUserDBRef?.child(mCurrentUserID+"/status")
        var userLastOnlineRef= mUserDBRef?.child(mCurrentUserID+"/last_Online")
        userStatus?.setValue(Status_Chat.ONLINE)
        userLastOnlineRef?.setValue(ServerValue.TIMESTAMP);
        */
        val retIntent = Intent(Const_Resources.SERVICE_CONECTIVITY)
        retIntent.putExtra("state_conectivity", true)
        this?.sendBroadcast(retIntent)
        onMessageOk(R.color.colorPrimary, getString(R.string.on_connectividad))


        presenter?.makeUserOnline(this)
        val usuarioLogued=getLastUserLogued()
        if (usuarioLogued?.RolNombre.equals(RolResources.PRODUCTOR)) {
            presenter?.syncQuantityData(true)
        }
    }

    override fun offConnectivity() {

        if(viewDialogSync!=null){
            viewDialogSync?.viewEstateConect?.setBackgroundResource(R.drawable.is_offline_user);
            viewDialogSync?.txtconectividad?.setText(getString(R.string.off_connectividad));
        }

      /*  var userStatus= mUserDBRef?.child(mCurrentUserID+"/status")
        var userLastOnlineRef= mUserDBRef?.child(mCurrentUserID+"/last_Online")
        userStatus?.setValue(Status_Chat.ONLINE)
        userLastOnlineRef?.setValue(ServerValue.TIMESTAMP);*/

       // mUserDBRef?.database?.goOffline()
        val retIntent = Intent(Const_Resources.SERVICE_CONECTIVITY)
        retIntent.putExtra("state_conectivity", false)
        this?.sendBroadcast(retIntent)
        onMessageError(R.color.grey_luiyi, getString(R.string.off_connectividad))
        presenter?.makeUserOffline(this)

    }

    override fun onMessageOk(colorPrimary: Int, message: String?) {
        val color = Color.WHITE
        val snackbar = Snackbar
                .make(container, message!!, Snackbar.LENGTH_LONG)
        val sbView = snackbar.view
        sbView.setBackgroundColor(ContextCompat.getColor(this, colorPrimary))
        val textView = sbView.findViewById<View>(android.support.design.R.id.snackbar_text) as TextView
        textView.setCompoundDrawablesWithIntrinsicBounds(R.drawable.quantum_ic_cast_connected_white_24, 0, 0, 0)
        // textView.setCompoundDrawablePadding(getResources().getDimensionPixelOffset(R.dimen.activity_horizontal_margin));
        textView.setTextColor(color)
        snackbar.show()
    }

    override fun onMessageError(colorPrimary: Int, message: String?) {
        onMessageOk(colorPrimary, message)
    }


    override fun onMessageToast(colorPrimary: Int, message: String?){
        val inflater = this.layoutInflater
        var viewToast = inflater.inflate(R.layout.custom_message_toast, null)
        viewToast.txtMessageToastCustom.setText(message)
        viewToast.contetnToast.setBackgroundColor(ContextCompat.getColor(this,colorPrimary))
        var mytoast =  Toast(this);
        mytoast.setView(viewToast);
        mytoast.setDuration(Toast.LENGTH_LONG);
        mytoast.show();
        ///onMessageError(R.color.red_900, getString(R.string.disabledGPS))
    }
    //endregion

    //region OVERRIDES METHODS
    override fun onResume() {
        super.onResume()
        presenter?.onResume(this)
    }

    override fun onDestroy() {
        isAppRunning = false;
        presenter?.onDestroy(this)
        Log.d("TAG SERVICE", "START RUN SERVICE")
        super.onDestroy()
    }

    override fun onStart() {
        super.onStart()
        //mAuth.addAuthStateListener(mAuthListener);
        //ServiceUtils.stopServiceFriendChat(getApplicationContext(), false);
       // ServiceUtils.startServiceFriendChat(getApplicationContext())
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        // The ACTION_OPEN_DOCUMENT intent was sent with the request code
        // READ_REQUEST_CODE. If the request code seen here doesn't match, it's the
        // response to some other intent, and the code below shouldn't run at all.

        if (resultCode == Activity.RESULT_CANCELED) {
            return
        }

        if(resultCode== Activity.RESULT_OK){
             var uri: Uri? = null
             if (data != null) {

                 if(requestCode==READ_REQUEST_CODE_BACKUP){

                    uri = data.data
                    //Log.i(TAG, "Uri: " + uri!!.toString())
                    val file = File(uri.path)
                    val path = file.absolutePath
                    if (IS_EXPORT==true) {
                        exportDB(path)
                    } else if(IS_IMPORT==true) {
                        importDB(path)
                    }else{
                        //return
                    }
                }else{

                     //return
                 }
            }
        }
    }


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
        ActivityCompat.requestPermissions(this, PERMISSIONS, PERMISSION_ALL)
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        when (requestCode) {
            PERMISSION_REQUEST_CODE -> if (grantResults.size > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                doPermissionGrantedStuffs()
            } else {
                Toast.makeText(this,
                        "Permiso denegado", Toast.LENGTH_LONG).show()

            }/* else if ((Build.VERSION.SDK_INT >= 23 && !shouldShowRequestPermissionRationale(permissions[0])) ||
                        (Build.VERSION.SDK_INT >= 23 && !shouldShowRequestPermissionRationale(permissions[1]))) {
                    //Toast.makeText(MainActivity.this, "Go to Settings and Grant the permission to use this feature.", Toast.LENGTH_SHORT).show();
                    // User selected the Never Ask Again Option
                    Intent i = new Intent();
                    i.setAction(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
                    i.addCategory(Intent.CATEGORY_DEFAULT);
                    i.setData(Uri.parse("package:" + getApplicationContext().getPackageName()));
                    i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    i.addFlags(Intent.FLAG_ACTIVITY_NO_HISTORY);
                    i.addFlags(Intent.FLAG_ACTIVITY_EXCLUDE_FROM_RECENTS);
                    getApplicationContext().startActivity(i);

                } */
        }
    }


    fun doPermissionGrantedStuffs(): Boolean {
        /// String SIMSerialNumber=tm.getSimSerialNumber();
        for (permission in PERMISSIONS) {
            if (ActivityCompat.checkSelfPermission(applicationContext, permission) != PackageManager.PERMISSION_GRANTED) {
                val response = false
                return response
            }
        }
        val response = true
        return response
    }

    //endregion
}




