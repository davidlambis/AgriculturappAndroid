package com.interedes.agriculturappv3.modules.productor.asistencia_tecnica_module.Lote


import android.Manifest
import android.annotation.SuppressLint
import android.app.ActivityManager
import android.app.Dialog
import android.content.Context
import android.content.DialogInterface
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Color
import android.graphics.drawable.AnimationDrawable
import android.location.Location
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.os.Handler
import android.provider.Settings
import android.support.design.widget.AppBarLayout
import android.support.design.widget.CoordinatorLayout
import android.support.design.widget.Snackbar
import android.support.v4.app.ActivityCompat
import android.support.v4.app.Fragment
import android.support.v4.content.ContextCompat
import android.support.v4.widget.SwipeRefreshLayout
import android.support.v7.app.AlertDialog
import android.support.v7.widget.LinearLayoutManager
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import android.widget.*
import com.daimajia.androidanimations.library.Techniques
import com.daimajia.androidanimations.library.YoYo
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.MapsInitializer
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.model.*
import com.google.maps.android.SphericalUtil

import com.interedes.agriculturappv3.R
import com.interedes.agriculturappv3.modules.models.lote.Lote
import com.interedes.agriculturappv3.modules.models.unidad_productiva.Unidad_Productiva
import com.interedes.agriculturappv3.modules.models.unidad_medida.Unidad_Medida
import com.interedes.agriculturappv3.modules.models.unidad_medida.Unidad_Medida_Table
import com.interedes.agriculturappv3.modules.models.unidad_productiva.Unidad_Productiva_Table
import com.interedes.agriculturappv3.modules.productor.asistencia_tecnica_module.Lote.adapter.LoteAdapter
import com.interedes.agriculturappv3.modules.productor.ui.main_menu.MenuMainActivity
import com.interedes.agriculturappv3.services.coords.CoordsServiceKotlin
import com.interedes.agriculturappv3.services.resources.RequestAccessPhoneResources
import com.kaopiz.kprogresshud.KProgressHUD
import com.raizlabs.android.dbflow.sql.language.SQLite
import kotlinx.android.synthetic.main.activity_menu_main.*
import kotlinx.android.synthetic.main.content_recyclerview.*
import kotlinx.android.synthetic.main.custom_message_toast.view.*
import kotlinx.android.synthetic.main.dialog_form_lote.view.*
import kotlinx.android.synthetic.main.fragment_lote.*
import java.util.ArrayList


class Lote_Fragment : Fragment(), MainViewLote.View, OnMapReadyCallback, SwipeRefreshLayout.OnRefreshListener, GoogleMap.OnMapClickListener, View.OnClickListener, GoogleMap.OnMyLocationButtonClickListener, GoogleMap.OnMarkerDragListener {


    //Mapa
    private var mMap: GoogleMap? = null
    private var LastMarkerDrawingLote: Marker? = null
    private var polygon: Polygon? = null
    private var bounds: LatLngBounds? = null
    private val builder = LatLngBounds.Builder()
    private var LOADED_MAPA: Boolean = false

    //marker and location
    private var locationLote: Location = Location("")
    val listMarkerLote = ArrayList<Marker>()
    val listMarkerUp = ArrayList<Marker>()
    var markerLocation: Marker? = null

    //Presenter
    var presenter: MainViewLote.Presenter? = null
    val handler = Handler()

    //Dialog
    var _dialogRegisterUpdate: AlertDialog? = null
    private var _dialogTypeLocation: AlertDialog? = null
    var viewDialog: View? = null;

    //Coords
    private var latitud: Double? = 0.0
    private var longitud: Double? = 0.0

    //Progress
    /** These can be lateinit as they are set in onCreate */
    private var hud: KProgressHUD? = null
    private var hudCoords: KProgressHUD? = null

    //UbicationLote
    private var UBICATION_MANUAL: Boolean = false
    private var UBICATION_MAPA: Boolean = false
    private var UBICATION_GPS: Boolean = false
    private var DIALOG_SET_TYPE_UBICATION: Int = -1

    //Adapter
    var adapter: LoteAdapter? = null
    var lotesList: ArrayList<Lote>? = ArrayList<Lote>()
    var loteGlobal: Lote? = null


    //Globals

    var unidadMedidaGlobal: Unidad_Medida? = null
    var unidadProductivaGlobalSppiner: Unidad_Productiva? = null
    var unidadProductivaGlobalDialog: Unidad_Productiva? = null
    var listUnidadProductivaGlobal: List<Unidad_Productiva>? = ArrayList<Unidad_Productiva>()
    var listUnidadMedidaGlobal: List<Unidad_Medida>? = ArrayList<Unidad_Medida>()

    private var DIALOG_SELECTT_POSITION_UP: Int = 0
    private var DIALOG_SELECT_ALL_UP: Boolean = true
    var Unidad_Productiva_Id_Selected: Long? = null


    var INI_TASK_HANDLER: Boolean? = false

    //PERMISOS
    private val PERMISSION_REQUEST_CODE = 1
    internal var PERMISSION_ALL = 1
    internal var PERMISSIONS = arrayOf(Manifest.permission.ACCESS_FINE_LOCATION)

    companion object {
        var instance: Lote_Fragment? = null
    }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        instance = this
        //Presenter
        //(presenter as LotePresenterImpl).onCreate()

        presenter = LotePresenterImpl(this)
        presenter?.onCreate()

        /// presenter?.onCreate();
    }


    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_lote, null)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        configAppBarLayout()
        //initCollapsingToolbar()
        initAdapter()
        fabAddLote.setOnClickListener(this)
        fabLocationLote.setOnClickListener(this)
        fabUnidadProductiva.setOnClickListener(this)
        swipeRefreshLayout.setOnRefreshListener(this)
        checkDoneMapView.setOnClickListener(this)
        checkCloseMapView.setOnClickListener(this)
        ivBackButton.setOnClickListener(this)
        mapViewLotes.onCreate(savedInstanceState)
        mapViewLotes.onResume()
        try {
            MapsInitializer.initialize(context!!)
        } catch (e: Exception) {
            Log.i("ErrorMap", e.message)
        }
        (activity as MenuMainActivity).toolbar.title = getString(R.string.title_lote)
        presenter?.getLotes(Unidad_Productiva_Id_Selected);
        mapViewLotes.getMapAsync(this)
        loadInitTaskHandler()
    }


    //region METHODS
    private fun configAppBarLayout() {
        if (app_bar?.getLayoutParams() != null) {
            var params = app_bar?.layoutParams as CoordinatorLayout.LayoutParams
            val behavior = AppBarLayout.Behavior()
            behavior.setDragCallback(object : AppBarLayout.Behavior.DragCallback() {
                override fun canDrag(appBarLayout: AppBarLayout): Boolean {
                    return false
                }
            })
            params.behavior = behavior
        }
    }

    private fun initAdapter() {
        recyclerView?.layoutManager = LinearLayoutManager(activity)
        // recyclerView?.layoutManager = GridLayoutManager(activity, 2)
        adapter = LoteAdapter(lotesList!!)
        recyclerView?.adapter = adapter
        /*
       adapter = LoteAdapter(lotes!!) { position ->
          var lote= lotes!![position.position]
            if(position.type==0){
                Toast.makeText(activity,"Lectura: "+lote.Nombre,Toast.LENGTH_LONG).show()
            }
            if(position.type==2){
                Toast.makeText(activity,"Eliminar: "+lote.Nombre,Toast.LENGTH_LONG).show()
            }
       }
       recyclerView?.adapter = adapter
       */
    }

    fun loadInitTaskHandler() {
        if (INI_TASK_HANDLER == true) {
            handler.postDelayed({
                if ((activity as MenuMainActivity).presenter?.checkConnection()!!) {
                    hideElementsAndSetPropertiesOnConectionInternet()
                } else {
                    if (LOADED_MAPA == false) {
                        showElementsAndSetPropertiesOffConnectioninternet()
                    }
                    onMessageError(R.color.grey_luiyi, getString(R.string.sin_conexion))
                }
            }, 10000)
        }

    }


    override fun hideElementsAndSetPropertiesOnConectionInternet() {
        if (imgOffConection.visibility == View.VISIBLE) imgOffConection.visibility = View.GONE
        if (UBICATION_MANUAL == true) {
            DIALOG_SET_TYPE_UBICATION = 1
        } else if (UBICATION_GPS == true) {
            DIALOG_SET_TYPE_UBICATION = 0
        }
        else if (UBICATION_MAPA == true) {
            DIALOG_SET_TYPE_UBICATION = 2
        }
        else {
            DIALOG_SET_TYPE_UBICATION = -1
        }
    }

    override fun showElementsAndSetPropertiesOffConnectioninternet() {
        if (imgOffConection.visibility == View.GONE) imgOffConection.visibility = View.VISIBLE
        if (UBICATION_GPS == true) {
            DIALOG_SET_TYPE_UBICATION = 0
        }

        else if (UBICATION_MANUAL == true) {
            DIALOG_SET_TYPE_UBICATION = 1
        }

        else {
            DIALOG_SET_TYPE_UBICATION = -1
        }
    }

    /*
    private fun initCollapsingToolbar() {
        app_bar.addOnOffsetChangedListener(object : AppBarLayout.OnOffsetChangedListener {
            internal var isShow = false
            internal var scrollRange = -1

            override fun onOffsetChanged(appBarLayout: AppBarLayout, verticalOffset: Int) {
                if (scrollRange == -1) {
                    scrollRange = appBarLayout.totalScrollRange
                    fabLocationLote.visibility = View.VISIBLE
                    fabUnidadProductiva.visibility = View.VISIBLE
                }
                if (scrollRange + verticalOffset >= 0 && scrollRange + verticalOffset < 120) {

                    isShow = true
                    fabLocationLote.visibility = View.GONE
                    fabUnidadProductiva.visibility = View.GONE
                } else if (isShow) {
                    fabLocationLote.visibility = View.VISIBLE
                    fabUnidadProductiva.visibility = View.VISIBLE
                    isShow = false
                }
            }
        })
    }*/

    //endregion

    //region MAPA
    //On Map Reding
   // @SuppressLint("MissingPermission")
    override fun onMapReady(map: GoogleMap?) {
        mMap = map
        mMap?.setOnMarkerDragListener(this);
        val positionInitial = LatLng(4.565473550710278, -74.058837890625)
        /// mMap.addMarker(new MarkerOptions().position(positionInitial).title("Ecuador"));
        mMap?.moveCamera(CameraUpdateFactory.newLatLngZoom(positionInitial, 6f))



        //mMap?.setMyLocationEnabled(true)
        mMap?.getUiSettings()?.isZoomControlsEnabled = true
        mMap?.getUiSettings()?.isZoomGesturesEnabled = true
        mMap?.setOnMapClickListener(this)
        mMap?.setOnMyLocationButtonClickListener(this);
            // Setting a click event handler for the map
        mMap?.setOnMapLoadedCallback(GoogleMap.OnMapLoadedCallback {
            LOADED_MAPA = true
            hideElementsAndSetPropertiesOnConectionInternet()
        })

            animateLocationMap(lotesList!!)

    }

    override fun onMyLocationButtonClick(): Boolean {
        showAlertTypeLocationLote()
        return true;
    }


    private fun animateLocationMap(lotes: List<Lote>) {

        try{
            mMap?.clear()
            //Si existe poligono se dibuja
            if (DIALOG_SELECT_ALL_UP == false && unidadProductivaGlobalDialog?.Configuration_Poligon == true && unidadProductivaGlobalDialog?.Configuration_Point == true) {
                var locations = ArrayList<LatLng>()
                locations.add(LatLng(2.930625, -75.271928))
                locations.add(LatLng(2.930534, -75.271674))
                locations.add(LatLng(2.930248, -75.271566))
                locations.add(LatLng(2.930063, -75.271644))
                locations.add(LatLng(2.930288, -75.272092))
                locations.add(LatLng(2.930625, -75.271928))
                polygon = addPoligonUp(locations, ContextCompat.getColor(activity!!, android.R.color.holo_orange_dark), ContextCompat.getColor(activity!!, android.R.color.holo_orange_light))
            }

            //si se selecciona la opcion todos, y no se encontraron lotes regiostrados y existen up registradas
            if (DIALOG_SELECT_ALL_UP == true && lotes.size <= 0 && listUnidadProductivaGlobal?.size!! > 0) {
                var builder = LatLngBounds.Builder()
                for (up in listUnidadProductivaGlobal!!) {
                    var latlngUp = LatLng(up.Latitud!!, up.Longitud!!);

                    //var markerUP = addMarker(latlngUp, up.Nombre!!, up.Descripcion!!, BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_RED))
                    var markerUP = addMarker(latlngUp, up.nombre!!, up.descripcion!!, BitmapDescriptorFactory.fromResource(R.drawable.ic_marker_unidad_productiva))
                    listMarkerUp.add(markerUP!!)
                    builder.include(latlngUp)
                }
                var bounds = builder.build()
                var cu = CameraUpdateFactory.newLatLngBounds(bounds, 70)
                mMap?.animateCamera(cu)
            }
            //si se selecciona una up, y  no se encontraron lotes registrados
            else if (DIALOG_SELECT_ALL_UP == false && lotes.size <= 0) {
                var builder = LatLngBounds.Builder()
                var latlngUp = LatLng(unidadProductivaGlobalDialog?.Latitud!!, unidadProductivaGlobalDialog?.Longitud!!)
                var markerUP = addMarker(latlngUp, unidadProductivaGlobalDialog?.nombre!!, unidadProductivaGlobalDialog?.descripcion!!, BitmapDescriptorFactory.fromResource(R.drawable.ic_marker_unidad_productiva))
                listMarkerUp.add(markerUP!!)
                builder.include(latlngUp)
                var bounds = builder.build()
                var cu = CameraUpdateFactory.newLatLngBounds(bounds, 70)
                mMap?.animateCamera(cu)
            }
            //si se selecciona una up, y  se encontraron lotes regiostrados
            else if (DIALOG_SELECT_ALL_UP == false && lotes.size > 0 || DIALOG_SELECT_ALL_UP == true && lotes.size > 0) {
                var locations = ArrayList<LatLng>()
                val area = SphericalUtil.computeArea(locations)
                //Ajustar camara mostrando todos los marcadores
                var builder = LatLngBounds.Builder();
                for (l in lotes) {
                    var centerLatLng: LatLng? = LatLng(l.Latitud!!, l.Longitud!!)
                    builder.include(centerLatLng)
                }


                //mMap?.moveCamera(CameraUpdateFactory.newLatLngBounds(bounds, 50));
                //mMap?.setPadding(0, getResources().getDrawable(R.drawable.logo_agr_app).getIntrinsicHeight(), 0, 0);
                for (lote in lotes) {

                    if(lote.Latitud!=0.0 && lote.Longitud!=0.0 || lote.Latitud!=null && lote.Longitud!=null){
                        var latlngLote = LatLng(lote.Latitud!!, lote.Longitud!!);
                        var markerLote = addMarker(latlngLote, lote.Nombre!!, lote.Descripcion!!, BitmapDescriptorFactory.fromResource(R.drawable.ic_marker_lote))
                        listMarkerLote.add(markerLote!!)
                    }

                }

                //Add Marker UP
                if (DIALOG_SELECT_ALL_UP == true) {
                    for (unidadPro in this!!.listUnidadProductivaGlobal!!) {
                        var centerLatLng: LatLng? = LatLng(unidadPro?.Latitud!!, unidadPro?.Longitud!!)
                        addMarker(centerLatLng!!, unidadPro?.nombre!!, unidadPro?.descripcion!!, BitmapDescriptorFactory.fromResource(R.drawable.ic_marker_unidad_productiva))
                        builder.include(centerLatLng)
                    }
                } else {
                    if (unidadProductivaGlobalDialog != null) {
                        var centerLatLng: LatLng? = LatLng(unidadProductivaGlobalDialog?.Latitud!!, unidadProductivaGlobalDialog?.Longitud!!)
                        addMarker(centerLatLng!!, unidadProductivaGlobalDialog?.nombre!!, unidadProductivaGlobalDialog?.descripcion!!, BitmapDescriptorFactory.fromResource(R.drawable.ic_marker_unidad_productiva))
                        builder.include(centerLatLng)
                    }
                }


                var bounds = builder.build()
                var cu = CameraUpdateFactory.newLatLngBounds(bounds, 70)
                mMap?.animateCamera(cu)

                // double area= calculateAreaOfGPSPolygonOnEarthInSquareMeters(locationsGlobals);
                //Toast.makeText(activity, "AREA: " + String.format("%.0f mts", area), Toast.LENGTH_LONG).show()
            }
            //si no enontaron   up registradas, y  no se encontraron lotes regiostrados
            else {
                val positionInitial = LatLng(4.565473550710278, -74.058837890625)
                mMap?.animateCamera(CameraUpdateFactory.newLatLngZoom(positionInitial, 6f))
            }

        }catch (ex:Exception){

        }



    }

    override fun onMarkerDragEnd(p0: Marker?) {
        Log.d("System out", "onMarkerDragEnd..." + p0?.getPosition()?.latitude + "..." + p0?.getPosition()?.longitude);
        mMap?.animateCamera(CameraUpdateFactory.newLatLng(p0?.getPosition()))
        txtCoordsLote.setText(String.format(getString(R.string.coords), p0?.getPosition()?.latitude, p0?.getPosition()?.longitude))
        //LastMarkerDrawingLote?.setPosition(LatLng(p0?.getPosition()?.latitude!!, p0?.getPosition()?.longitude!!))
    }

    override fun onMarkerDragStart(p0: Marker?) {
        Log.d("System out", "onMarkerDragStart..." + p0?.getPosition()?.latitude + "..." + p0?.getPosition()?.longitude);
        txtCoordsLote.setText(String.format(getString(R.string.coords), p0?.getPosition()?.latitude, p0?.getPosition()?.longitude))
    }

    override fun onMarkerDrag(p0: Marker?) {
        Log.d("System out", "onMarkerDragStart..." + p0?.getPosition()?.latitude + "..." + p0?.getPosition()?.longitude);
        txtCoordsLote.setText(String.format(getString(R.string.coords), p0?.getPosition()?.latitude, p0?.getPosition()?.longitude))
    }


    private fun addPoligonUp(locations: ArrayList<LatLng>, color: Int, color1: Int): Polygon? {
        // Instantiating CircleOptions to draw a circle around the marker
        var poligonOption = PolygonOptions()
        //mMap.clear();
        // Specifying the psition of the marker
        poligonOption.addAll(locations)
        // Title marker
        poligonOption.strokeColor(color)
        // Fill color of the marker
        poligonOption.fillColor(color1)
        // Adding the circle to the GoogleMap
        return mMap?.addPolygon(poligonOption)
    }

    ///Convert Coords Poligon to LatLng Unique
    private fun getPolygonCenterPoint(polygonPointsList: ArrayList<LatLng>): LatLng? {
        var centerLatLng: LatLng? = null
        for (i in polygonPointsList.indices) {
            builder.include(polygonPointsList[i])
        }
        bounds = builder.build()
        centerLatLng = bounds?.getCenter()
        return centerLatLng
    }

    override fun onMapClick(latLng: LatLng) {
        if (UBICATION_MAPA == true) {
            if (LastMarkerDrawingLote != null) {
                LastMarkerDrawingLote?.remove()
            }

            // Creating a marker
            latitud=latLng.latitude
            longitud=latLng.longitude
            LastMarkerDrawingLote = drawMarker(latLng, latLng.latitude.toString() + " / " + latLng.longitude.toString(), getString(R.string.location_gps), BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_GREEN))
            txtCoordsLote.setText(String.format(getString(R.string.coords), latLng.latitude, latLng.longitude))
        }
    }

    //Properties and Drawing Marker
    private fun addMarker(latLng: LatLng, title: String, snippet: String, bitmapDescriptor: BitmapDescriptor): Marker? {
        // Instantiating CircleOptions to draw a circle around the marker
        val markerOption = MarkerOptions()
        //mMap.clear();
        // Specifying the psition of the marker
        markerOption.position(latLng)
        // Title marker
        markerOption.title(title)
        // Fill color of the marker
        markerOption.icon(bitmapDescriptor)
        //Snip
        markerOption.snippet(snippet)
        markerOption.draggable(true)
        // Adding the circle to the GoogleMap
        return mMap?.addMarker(markerOption)
    }

    //Properties and Drawing Marker
    private fun addMarkerLocation(lat: Double, lng: Double) {
        // Instantiating CircleOptions to draw a circle around the marker
        //Get Zoom last(Obtener zoom anterior)
        var zoom = mMap?.getCameraPosition()!!.zoom
        /// Toast.makeText(getApplicationContext(),"Zoom: "+String.valueOf(zoom),Toast.LENGTH_SHORT).show();
        var coordenadas = LatLng(lat, lng)
        var myposition = CameraUpdateFactory.newLatLngZoom(coordenadas, zoom)
        if (markerLocation != null) markerLocation?.remove()

        //Define color del marker
        var bitmapMarker = BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_BLUE)
        markerLocation = addMarker(coordenadas, "Mi Ubicacion", "", bitmapMarker)

        if (UBICATION_GPS == true) {
            mMap?.animateCamera(myposition);
        }
    }

    private fun drawMarker(latLng: LatLng, title: String, snippet: String, bitmapDescriptor: BitmapDescriptor): Marker? {
        // Instantiating CircleOptions to draw a circle around the marker
        val markerOption = MarkerOptions()
        //mMap.clear();
        // Specifying the psition of the marker
        markerOption.position(latLng)
        // Title marker
        markerOption.title(title)
        // Fill color of the marker
        markerOption.icon(bitmapDescriptor)
        //Snip
        markerOption.snippet(snippet)
        markerOption.draggable(true)
        mMap?.animateCamera(CameraUpdateFactory.newLatLng(latLng))
        // Adding the circle to the GoogleMap
        return mMap?.addMarker(markerOption)
    }

    //endregion

    //region Methods Interface MainViewLote
    override fun showProgress() {
        // viewDialog?.progressBar?.visibility= View.VISIBLE;

        // progressBar?.visibility= View.VISIBLE;
        swipeRefreshLayout.setRefreshing(true);
    }

    override fun hideProgress() {
        // viewDialog?.progressBar?.visibility= View.GONE;
        //   progressBar?.visibility= View.GONE;
        swipeRefreshLayout.setRefreshing(false);
    }

    override fun validarCampos(): Boolean? {
        var cancel = false
        var focusView: View? = null

        if (viewDialog?.spinnerUnidadProductiva?.text.toString().isEmpty() && viewDialog?.spinnerUnidadProductiva?.visibility == View.VISIBLE) {
            viewDialog?.spinnerUnidadProductiva?.setError(getString(R.string.error_field_required))
            focusView = viewDialog?.spinnerUnidadProductiva
            cancel = true
        } else if (viewDialog?.name_lote?.text.toString().isEmpty()) {
            viewDialog?.name_lote?.setError(getString(R.string.error_field_required))
            focusView = viewDialog?.name_lote
            cancel = true
        } else if (viewDialog?.description_lote?.text.toString().isEmpty()) {
            viewDialog?.description_lote?.setError(getString(R.string.error_field_required))
            focusView = viewDialog?.description_lote
            cancel = true
        } else if (viewDialog?.area_lote?.text.toString().isEmpty()) {
            viewDialog?.area_lote?.setError(getString(R.string.error_field_required))
            focusView = viewDialog?.area_lote
            cancel = true
        } else if (viewDialog?.spinnerUnidadMedidaLote?.text.toString().isEmpty()) {
            viewDialog?.spinnerUnidadMedidaLote?.setError(getString(R.string.error_field_required))
            focusView = viewDialog?.spinnerUnidadMedidaLote
            cancel = true
        } else if (viewDialog?.coordenadas_lote?.text.toString().isEmpty()) {
            viewDialog?.coordenadas_lote?.setError(getString(R.string.error_field_required))
            focusView = viewDialog?.coordenadas_lote
            cancel = true
        }
        else if(presenter?.verificateAreaLoteBiggerUp(unidadProductivaGlobalSppiner?.Unidad_Productiva_Id ,viewDialog?.area_lote?.text.toString()?.toDouble())==true){
            viewDialog?.area_lote?.setError(getString(R.string.verifcate_area_lote))
            focusView = viewDialog?.area_lote
            cancel = true
        }
        else if (viewDialog?.edtLatitud?.text.toString().isEmpty()) {
            viewDialog?.edtLatitud?.setError(getString(R.string.error_field_required))
            focusView = viewDialog?.edtLatitud
            cancel = true
        }else if (viewDialog?.edtLongitud?.text.toString().isEmpty()) {
            viewDialog?.edtLongitud?.setError(getString(R.string.error_field_required))
            focusView = viewDialog?.edtLongitud
            cancel = true
        }else if(!isParceableDouble(viewDialog?.edtLatitud?.text.toString())){
            viewDialog?.edtLatitud?.setError(getString(R.string.verifcate_coord))
            focusView = viewDialog?.edtLatitud
            cancel = true
        }
        else if(!isParceableDouble(viewDialog?.edtLongitud?.text.toString())){
            viewDialog?.edtLongitud?.setError(getString(R.string.verifcate_coord))
            focusView = viewDialog?.edtLongitud
            cancel = true
        }


        /* else if (!edtCorreo.text.toString().trim().matches(email_pattern.toRegex())) {
            edtCorreo?.setError(getString(R.string.edit_text_error_correo))
            focusView = edtCorreo
            cancel = true
        }*/
        if (cancel) {
            focusView?.requestFocus()
        } else {
            return true;
        }
        return false
    }

    override fun disableInputs() {
        setInputs(false)
    }

    override fun enableInputs() {
        setInputs(true)
    }

    private fun setInputs(b: Boolean) {
        if (viewDialog != null) {
            viewDialog?.name_lote?.isEnabled = b
            viewDialog?.description_lote?.isEnabled = b
            viewDialog?.area_lote?.isEnabled = b
            viewDialog?.btnSaveLote?.isEnabled = b
            viewDialog?.btnSaveLote?.isClickable = b
        }
    }

    override fun limpiarCampos() {

        if(viewDialog!=null){
            viewDialog?.name_lote?.setText("");
            viewDialog?.description_lote?.setText("");
            viewDialog?.area_lote?.setText("");
            viewDialog?.coordenadas_lote?.setText("");
            viewDialog?.edtLatitud?.setText("");
            viewDialog?.edtLongitud?.setText("");
        }

        if (LastMarkerDrawingLote != null) LastMarkerDrawingLote?.remove()
        LastMarkerDrawingLote = null
        contentButtonsDrawingLoteMapa.visibility = View.GONE
        app_bar.layoutParams.width = AppBarLayout.LayoutParams.MATCH_PARENT
        app_bar.layoutParams.height = resources.getDimensionPixelSize(R.dimen.height_mapa)


        if(UBICATION_GPS){
            closeServiceGps()
        }
        latitud=0.0
        longitud=0.0
        UBICATION_GPS = false
        UBICATION_MANUAL = false
        UBICATION_MAPA=false
        DIALOG_SET_TYPE_UBICATION=-1




    }


    override fun setPropertiesTypeLocationGps() {
        //var intent =  Intent(activity, CoordsServiceKotlin::class.java);
        //activity!!.startService(intent)
        latitud=0.0
        longitud=0.0
        presenter?.startGps(activity!!)
        UBICATION_GPS = true
        UBICATION_MANUAL = false
        UBICATION_MAPA=false
        if (LastMarkerDrawingLote != null) {
            LastMarkerDrawingLote?.remove()
        }
        if (LastMarkerDrawingLote != null) LastMarkerDrawingLote?.remove()
        LastMarkerDrawingLote = null
        _dialogTypeLocation?.dismiss()
        txtCoordsLote.setText("")
    }

    fun setPropertiesDisabledGps() {
        //var intent =  Intent(activity, CoordsServiceKotlin::class.java);
        //activity!!.startService(intent)
        //UBICATION_GPS = false
        if (markerLocation != null) markerLocation?.remove()
        LastMarkerDrawingLote = null
        txtCoordsLote.setText("")
    }

    override fun setPropertiesTypeLocationManual() {
        //var intent =  Intent(activity, CoordsServiceKotlin::class.java);
        //activity!!.stopService(intent)
        latitud=0.0
        longitud=0.0
        presenter?.closeServiceGps(activity!!)
        UBICATION_GPS = false
        UBICATION_MANUAL = true
        UBICATION_MAPA=false
        if (markerLocation != null) markerLocation?.remove()
        LastMarkerDrawingLote = null
        _dialogTypeLocation?.dismiss()
        txtCoordsLote.setText("")
    }

    override fun setPropertiesTypeLocationMapa() {
        //var intent =  Intent(activity, CoordsServiceKotlin::class.java);
        //activity!!.stopService(intent)
        latitud=0.0
        longitud=0.0
        presenter?.closeServiceGps(activity!!)
        if (markerLocation != null) markerLocation?.remove()
        UBICATION_GPS = false
        UBICATION_MANUAL = false
        UBICATION_MAPA=true
        _dialogTypeLocation?.dismiss()
        Toast.makeText(activity, "Ubicacion Manual", Toast.LENGTH_SHORT).show()
        txtCoordsLote.setText("")

        contentButtonsDrawingLoteMapa.visibility = View.VISIBLE
        app_bar.layoutParams.width = AppBarLayout.LayoutParams.MATCH_PARENT
        app_bar.layoutParams.height = AppBarLayout.LayoutParams.MATCH_PARENT
    }


    override fun requestResponseOk() {
        if (_dialogRegisterUpdate != null) {
            _dialogRegisterUpdate?.dismiss()
        }
        onMessageOk(R.color.colorPrimary, getString(R.string.request_ok));
    }

    override fun requestResponseError(error: String?) {
        if (_dialogRegisterUpdate != null) {
            _dialogRegisterUpdate?.dismiss()
        }

        limpiarCampos()
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

    override fun onMessageDisabledGps(){
        val inflater = this.layoutInflater
        var viewToast = inflater.inflate(R.layout.custom_message_toast, null)
        viewToast.txtMessageToastCustom.setText(getString(R.string.disabledGPS))
        viewToast.contetnToast.setBackgroundColor(ContextCompat.getColor(activity!!.applicationContext, R.color.red_900))
        var mytoast =  Toast(activity);
        mytoast.setView(viewToast);
        mytoast.setDuration(Toast.LENGTH_LONG);
        mytoast.show();
        ///onMessageError(R.color.red_900, getString(R.string.disabledGPS))
    }

    override fun setListUP(listUnidadProductiva: List<Unidad_Productiva>) {
        listUnidadProductivaGlobal = listUnidadProductiva
    }

    override fun setListUnidadMedida(listUnidadMedida: List<Unidad_Medida>) {
        listUnidadMedidaGlobal = listUnidadMedida
    }

    override fun setListUPAdapterSpinner() {
        if (viewDialog != null) {
            ///Adapaters
            viewDialog?.spinnerUnidadProductiva!!.setAdapter(null);
            var upArrayAdapter = ArrayAdapter<Unidad_Productiva>(activity, android.R.layout.simple_list_item_activated_1, listUnidadProductivaGlobal);
            viewDialog?.spinnerUnidadProductiva!!.setAdapter(upArrayAdapter);
            viewDialog?.spinnerUnidadProductiva!!.onItemClickListener = AdapterView.OnItemClickListener { adapterView, view, position, l ->
                unidadProductivaGlobalSppiner = listUnidadProductivaGlobal!![position] as Unidad_Productiva
                if(viewDialog!=null){
                    viewDialog?.area_lote?.setText("")
                }
                /// Toast.makeText(activity,""+Unidad_Productiva_Id.toString(),Toast.LENGTH_SHORT).show()
            }
        }
    }

    override fun setListUnidadMedidaAdapterSpinner() {
        if (viewDialog != null) {
            ///Adapaters
            viewDialog?.spinnerUnidadMedidaLote!!.setAdapter(null)
            var uMedidaArrayAdapter = ArrayAdapter<Unidad_Medida>(activity, android.R.layout.simple_list_item_activated_1, listUnidadMedidaGlobal);
            viewDialog?.spinnerUnidadMedidaLote!!.setAdapter(uMedidaArrayAdapter);
            viewDialog?.spinnerUnidadMedidaLote!!.onItemClickListener = AdapterView.OnItemClickListener { adapterView, view, position, l ->
                unidadMedidaGlobal = listUnidadMedidaGlobal!![position] as Unidad_Medida
                //Toast.makeText(activity,""+ unidadMedidaGlobal!!.Id.toString(),Toast.LENGTH_SHORT).show()
            }
        }
    }


    override fun setListLotes(lotes: List<Lote>) {
        adapter?.clear()
        lotesList?.clear()
        listMarkerLote.clear()

        adapter?.setItems(lotes)
        hideProgress()
        setResults(lotes.size)
        animateLocationMap(lotes)
    }

    override fun setResults(lotes: Int) {
        var results = String.format(getString(R.string.results_global_search),
                lotes);
        txtResults.setText(results);
    }

    override fun registerLote() {
        if (presenter?.validarCampos() == true) {
            val lote = Lote()
            lote.Nombre = viewDialog?.name_lote?.text.toString()
            lote.Descripcion = viewDialog?.description_lote?.text.toString()
            lote.Area = viewDialog?.area_lote?.text.toString().toDoubleOrNull()
            lote.Coordenadas = viewDialog?.coordenadas_lote?.text.toString()
            lote.Localizacion = lote.Coordenadas
            lote.Unidad_Productiva_Id = unidadProductivaGlobalSppiner?.Unidad_Productiva_Id
            lote.Latitud = latitud
            lote.Longitud = longitud
            lote.Nombre_Unidad_Productiva = unidadProductivaGlobalSppiner?.nombre
            lote.Unidad_Medida_Id = unidadMedidaGlobal?.Id
            lote.Nombre_Unidad_Medida = unidadMedidaGlobal?.Descripcion
            presenter?.registerLote(lote, unidadProductivaGlobalSppiner?.Unidad_Productiva_Id)
        }
    }

    override fun updateLote(lote: Lote, unidad_productiva_id: Long?) {
        if (presenter?.validarCampos() == true) {
            //var lote = Lote()
            //lote.Id = loteGlobal!!.Id
            lote.Nombre = viewDialog?.name_lote?.text.toString()
            lote.Descripcion = viewDialog?.description_lote?.text.toString()
            lote.Area = viewDialog?.area_lote?.text.toString().toDoubleOrNull()
            lote.Coordenadas = viewDialog?.coordenadas_lote?.text.toString()
            lote.Localizacion = lote.Coordenadas
            lote.Unidad_Productiva_Id = loteGlobal?.Unidad_Productiva_Id
            lote.Latitud = loteGlobal?.Latitud
            lote.Longitud = loteGlobal?.Longitud
            lote.Nombre_Unidad_Productiva = loteGlobal?.Nombre_Unidad_Productiva
            lote.Unidad_Medida_Id = unidadMedidaGlobal?.Id
            lote.Nombre_Unidad_Medida = unidadMedidaGlobal?.Descripcion
            presenter?.updateLote(lote, loteGlobal?.Unidad_Productiva_Id)
        }
    }


    //UI ELements
    override fun showAlertDialogAddLote(lote: Lote?) {
        val inflater = this.layoutInflater
        viewDialog = inflater.inflate(R.layout.dialog_form_lote, null)
        val coordsLote = viewDialog?.coordenadas_lote
        val btnClosetDialogLote = viewDialog?.ivClosetDialogLote

        setListUPAdapterSpinner()
        setListUnidadMedidaAdapterSpinner()
        btnClosetDialogLote?.setOnClickListener(this)
        viewDialog?.btnSaveLote?.setOnClickListener(this)
        loteGlobal = lote
        //REGISTER
        if (lote == null) {

            viewDialog?.txtTitle?.setText(getString(R.string.add_lote))

            coordsLote?.setText(latitud.toString() + " / " + longitud.toString())



            /*if (UBICATION_MAPA == true) {
                if (LastMarkerDrawingLote != null) {

                    locationLote.latitude = LastMarkerDrawingLote!!.getPosition().latitude
                    locationLote.longitude = LastMarkerDrawingLote!!.getPosition().longitude


                    latitud=LastMarkerDrawingLote!!.getPosition().latitude
                    longitud=LastMarkerDrawingLote!!.getPosition().longitude

                    coordsLote?.setText(latitud.toString() + " / " + longitud.toString())
                }
            } else if(UBICATION_MANUAL==true){
                latitud=0.0
                longitud=0.0
                locationLote.latitude = latitud!!
                locationLote.longitude = longitud!!
                coordsLote?.setText(latitud.toString() + " / " + longitud.toString())
            }

            else {
                locationLote.latitude = latitud!!
                locationLote.longitude = longitud!!
                coordsLote?.setText(latitud.toString() + " / " + longitud.toString())
            }

            */

            viewDialog?.edtLatitud?.setText(latitud.toString())
            viewDialog?.edtLongitud?.setText(longitud.toString())

        }
        //UPDATE
        else {
            viewDialog?.txtTitle?.setText(getString(R.string.edit_lote))
            //unidadMedidaGlobal = Unidad_Medida(lote.Id, lote.Nombre_Unidad_Medida, null)
           unidadProductivaGlobalSppiner = SQLite.select().from(Unidad_Productiva::class.java).where(Unidad_Productiva_Table.Unidad_Productiva_Id.eq(lote.Unidad_Productiva_Id)).querySingle()
            unidadMedidaGlobal = SQLite.select().from(Unidad_Medida::class.java).where(Unidad_Medida_Table.Id.eq(lote.Unidad_Medida_Id)).querySingle()
            viewDialog?.name_lote?.setText(lote.Nombre)
            viewDialog?.description_lote?.setText(lote.Descripcion)

            viewDialog?.coordenadas_lote?.setText(lote.Coordenadas)
            viewDialog?.spinnerUnidadProductiva?.setText(unidadProductivaGlobalSppiner?.nombre)
            viewDialog?.spinnerUnidadMedidaLote?.setText(lote.Nombre_Unidad_Medida)
            viewDialog?.spinnerUnidadProductiva?.setDropDownHeight(0)

            if(lote.Area.toString().contains(".0")){
                viewDialog?.area_lote?.setText(String.format(context!!.getString(R.string.price_empty_signe),
                        lote.Area))
            }else{
                viewDialog?.area_lote?.setText(lote.Area.toString())
            }

            val coordenadas =lote.Localizacion
            if(coordenadas!=null){
                val separated = coordenadas?.split("/".toRegex())?.dropLastWhile { it.isEmpty() }?.toTypedArray()
                if(isParceableDouble( separated!![0].toString())==true && isParceableDouble( separated!![1].toString())==true){

                    var latitudL= separated!![0].toDoubleOrNull() // this will contain "Fruit"
                    var longitudL=separated!![1].toDoubleOrNull() // this will contain " they taste good"
                    lote.Latitud=latitudL
                    lote.Longitud=longitudL
                    lote.Coordenadas=coordenadas

                    latitud=latitudL
                    longitud=longitudL

                }else{
                    latitud=0.0
                    longitud=0.0

                }
            }

            viewDialog?.edtLatitud?.setText(latitud.toString())
            viewDialog?.edtLongitud?.setText(longitud.toString())
            //viewDialog?.spinnerUnidadProductiva?.visibility = View.GONE
        }

        /*
        val dialog = AlertDialog.Builder(context!!)
                .setView(viewDialog)
                .setIcon(R.drawable.ic_lote)
                . setTitle(getString(R.string.add_lote))
                .setPositiveButton(getString(R.string.btn_save), null) //Set to null. We override the onclick
                .setNegativeButton(getString(R.string.close), DialogInterface.OnClickListener { dialog, which ->
                })
                .create()
        dialog.setOnShowListener(DialogInterface.OnShowListener {
            val button = (dialog as AlertDialog).getButton(android.app.AlertDialog.BUTTON_POSITIVE)
            button.setOnClickListener {
                // TODO Do something
                if(lote!=null){
                    updateLote()
                }else{
                    registerLote()
                }
            }
        })
        dialog?.show()
        _dialogRegisterUpdate=dialog
        */
        /*
        val dialog = MaterialDialog.Builder(activity!!)
                .customView(viewDialog!!, true)
                .backgroundColorRes(R.color.white_solid)
                .autoDismiss(false)
                .theme(Theme.LIGHT)
                .build()*/

        viewDialog?.edtLatitud?.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(p0: Editable?) {

            }
            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
            }
            override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {

                if(!viewDialog?.edtLatitud?.text.toString().isEmpty()){
                    if(isParceableDouble(viewDialog?.edtLatitud?.text.toString())){
                        latitud=  viewDialog?.edtLatitud?.text.toString()?.toDoubleOrNull()
                        viewDialog?.coordenadas_lote?.setText(String.format(getString(R.string.coords), latitud, longitud))
                    }else{
                        var focusView:View? =null
                        viewDialog?.edtLatitud?.setError(getString(R.string.verifcate_coord))
                        focusView=viewDialog?.edtLatitud
                        focusView?.requestFocus()
                    }
                }else{
                    //viewDialog?.edtLatitud?.setText("0.0")
                    latitud=0.0
                    viewDialog?.coordenadas_lote?.setText(String.format(getString(R.string.coords), latitud, longitud))
                }
            }
        })


        viewDialog?.edtLongitud?.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(p0: Editable?) {

            }
            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
            }
            override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {




                if(!viewDialog?.edtLongitud?.text.toString().isEmpty()){
                    if(isParceableDouble(viewDialog?.edtLongitud?.text.toString())){
                        longitud=  viewDialog?.edtLongitud?.text.toString()?.toDoubleOrNull()
                        viewDialog?.coordenadas_lote?.setText(String.format(getString(R.string.coords), latitud, longitud))

                    }else{
                        var focusView:View? =null
                        viewDialog?.edtLongitud?.setError(getString(R.string.verifcate_coord))
                        focusView=viewDialog?.edtLongitud
                        focusView?.requestFocus()
                    }
                }else{
                    //viewDialog?.edtLongitud?.setText("0.0")
                    longitud=0.0
                    viewDialog?.coordenadas_lote?.setText(String.format(getString(R.string.coords), latitud, longitud))
                }


            }
        })


        viewDialog?.area_lote?.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(p0: Editable?) {
            }

            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
            }

            override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {

                if (viewDialog?.spinnerUnidadProductiva?.text.toString().isEmpty() && viewDialog?.spinnerUnidadProductiva?.visibility == View.VISIBLE) {
                    var focusView:View? =null
                    viewDialog?.spinnerUnidadProductiva?.setError(getString(R.string.error_field_required))
                    focusView=viewDialog?.spinnerUnidadProductiva
                    focusView?.requestFocus()

                }else{
                    if(!viewDialog?.area_lote?.text.toString().isEmpty()){

                        var areaLote=  viewDialog?.area_lote?.text.toString()?.toDouble()
                        if(presenter?.verificateAreaLoteBiggerUp(unidadProductivaGlobalSppiner?.Unidad_Productiva_Id ,areaLote)==true){
                            var focusView:View? =null
                            viewDialog?.area_lote?.setError(getString(R.string.verifcate_area_lote))
                            focusView=viewDialog?.area_lote
                            focusView?.requestFocus()

                        }
                    }
                }
            }
        })



        val dialog = AlertDialog.Builder(context!!, android.R.style.Theme_Light_NoTitleBar)
                .setView(viewDialog)
                .create()

        val lp = WindowManager.LayoutParams()
        lp.copyFrom(dialog.getWindow().getAttributes())
        lp.width = WindowManager.LayoutParams.MATCH_PARENT
        lp.height = WindowManager.LayoutParams.MATCH_PARENT
        //Hide KeyBoard
        dialog.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
        dialog.show()
        dialog.getWindow().setAttributes(lp)
        _dialogRegisterUpdate = dialog


    }

    override fun showGpsDisabledDialog(): Dialog {
        // Use the Builder class for convenient dialog construction
        val builder = android.app.AlertDialog.Builder(activity)
        builder.setMessage(R.string.please_enable_gps)
                .setPositiveButton(R.string.confirm) { dialog, id ->
                    val settingsIntent = Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS)
                    startActivityForResult(settingsIntent, RequestAccessPhoneResources.ENABLED_REQUEST_GPS)
                    // showProgressHudCoords()
                }
        builder.setTitle(R.string.gps_disabled)
        builder.setIcon(R.drawable.logo_agr_app)
        // Create the AlertDialog object and return it
        return builder.show()
    }

    override fun closeServiceGps(){
        presenter?.closeServiceGps(activity!!)
    }

    private fun isParceableDouble(cadena: String): Boolean {
        try {
            java.lang.Double.parseDouble(cadena)
            return true
        } catch (nfe: NumberFormatException) {
            return false
        }

    }

    override fun showAlertTypeLocationLote(): AlertDialog? {
        var builder = AlertDialog.Builder(activity!!)
        builder.setTitle(getString(R.string.location_lote_select_type))
        Toast.makeText(activity, getString(R.string.message_location_lote), Toast.LENGTH_SHORT).show()
        //var options=arrayOf(getString(R.string.location_type_gps), getString(R.string.location_type_manual))
        var options = arrayOf("")
        //La lista varia dependiendo del tipo de conectividad
        if (LOADED_MAPA == true || (activity as MenuMainActivity).presenter?.checkConnection() == true) {
            options = resources.getStringArray(R.array.array_type_get_location_with_conection)
            hideElementsAndSetPropertiesOnConectionInternet()
        } else {
            options = resources.getStringArray(R.array.array_type_get_location_empty_conection)
            showElementsAndSetPropertiesOffConnectioninternet()
        }
        builder.setSingleChoiceItems(options, DIALOG_SET_TYPE_UBICATION, DialogInterface.OnClickListener { dialog, which ->
            when (which) {

            //Position State Location GPS
                0 -> {
                    _dialogTypeLocation = dialog as AlertDialog?

                    if (Build.VERSION.SDK_INT >= 23) {
                        if (!hasPermissions(context, *PERMISSIONS)) {
                            requestPermission()
                        } else {
                            setPropertiesTypeLocationGps()
                        }
                    } else {
                        setPropertiesTypeLocationGps()
                    }

                    /*if (LotePresenterImpl.instance?.coordsService == null) {
                        presenter?.startGps(activity as MenuMainActivity)
                    }else{
                        if(!isMyServiceRunning(CoordsServiceKotlin::class.java)){
                            presenter?.startGps(activity as MenuMainActivity)
                        }
                    }*/
                    //scheduleDismiss();
                }
            //Position State Location Manual
                1 -> {
                    _dialogTypeLocation = dialog as AlertDialog?
                    setPropertiesTypeLocationManual()
                    showAlertDialogAddLote(null)
                    //scheduleDismiss();
                }
            //Position State Location Mapa
                else -> {
                    _dialogTypeLocation = dialog as AlertDialog?
                    setPropertiesTypeLocationMapa()
                    //showAlertDialogSelectUp()
                }
            }
        })
        builder.setIcon(R.drawable.ic_localizacion_finca);
        return builder.show();
    }



    private fun isMyServiceRunning(serviceClass: Class<*>): Boolean {
        val manager = activity!!.getSystemService(Context.ACTIVITY_SERVICE) as ActivityManager
        for (service in manager.getRunningServices(Integer.MAX_VALUE)) {
            if (serviceClass.name == service.service.className) {
                return true
            }
        }
        return false
    }



    override fun showProgressHud() {
        hud = KProgressHUD.create(activity)
                .setStyle(KProgressHUD.Style.SPIN_INDETERMINATE)
                .setWindowColor(getResources().getColor(R.color.colorPrimary))
                .setLabel("Cargando...", resources.getColor(R.color.white));
        hud?.show()
    }

    override fun showProgressHudCoords(){
        var imageView = ImageView(activity);
        imageView.setBackgroundResource(R.drawable.spin_animation);
        var drawable = imageView.getBackground() as AnimationDrawable;
        drawable.start();
        hudCoords = KProgressHUD.create(activity)
                .setCustomView(imageView)
                .setWindowColor(resources.getColor(R.color.white))
                .setLabel("Cargando...", resources.getColor(R.color.grey_luiyi));
        hudCoords?.show()

    }

    override fun hideProgressHudCoords(){
        hudCoords?.dismiss()
    }

    override fun hideProgressHud() {
        hud?.dismiss()
    }

    override fun confirmDelete(lote: Lote): AlertDialog? {
        var builder = AlertDialog.Builder(activity!!)
        builder.setTitle(getString(R.string.confirmation));
        builder.setNegativeButton(getString(R.string.close), DialogInterface.OnClickListener { dialog, which ->

        })
        builder.setMessage(getString(R.string.alert_delete_lote));
        builder?.setPositiveButton(getString(R.string.confirm), DialogInterface.OnClickListener { dialog, which ->
            presenter?.deleteLote(lote, Unidad_Productiva_Id_Selected)
        })
        builder.setIcon(R.drawable.ic_lote);
        return builder.show();
    }

    override fun verificateConnection(): AlertDialog? {
        var builder = AlertDialog.Builder(activity!!)
        builder.setTitle(getString(R.string.alert));
        builder.setMessage(getString(R.string.verificate_conexion));
        builder?.setPositiveButton(getString(R.string.confirm), DialogInterface.OnClickListener { dialog, which ->
            dialog.dismiss()
        })
        builder.setIcon(R.drawable.ic_lote);
        return builder.show();
    }


    override fun showAlertDialogSelectUp(): AlertDialog? {
        var dialog = AlertDialog.Builder(activity!!)
        var upArraString = arrayOf<String>("Todos")
        for (up in this!!.listUnidadProductivaGlobal!!) {
            upArraString += up.nombre.toString()
        }
        //REGISTER
        dialog.setSingleChoiceItems(upArraString, DIALOG_SELECTT_POSITION_UP, DialogInterface.OnClickListener { dialog, which ->
            if (which == 0) {
                DIALOG_SELECTT_POSITION_UP = which
                DIALOG_SELECT_ALL_UP = true
                Unidad_Productiva_Id_Selected = null
                presenter?.getLotes(Unidad_Productiva_Id_Selected)
                dialog.dismiss()
            } else {
                DIALOG_SELECT_ALL_UP = false
                var position = which - 1
                Unidad_Productiva_Id_Selected = listUnidadProductivaGlobal!![position].Unidad_Productiva_Id
                unidadProductivaGlobalDialog = listUnidadProductivaGlobal!![position]
                DIALOG_SELECTT_POSITION_UP = which
                //Toast.makeText(activity,""+Unidad_Productiva_Id_Selected,Toast.LENGTH_LONG).show()
                presenter?.getLotes(Unidad_Productiva_Id_Selected)
                dialog.dismiss()
            }
        })
        dialog?.setTitle(getString(R.string.title_filter_unidad_productiva))
        dialog?.setPositiveButton(getString(R.string.confirm), DialogInterface.OnClickListener { dialog, which ->
            presenter?.getLotes(Unidad_Productiva_Id_Selected)
        })
        // dialog?.setMessage(getString(R.string.message_add_lote))
        dialog?.setIcon(R.drawable.ic_lote)
        return dialog?.show()
    }
    //endregion

    //region Events
    override fun onClick(v: View?) {
        when (v?.id) {
            R.id.fabAddLote -> {
                if (UBICATION_MANUAL == false && UBICATION_GPS == false && UBICATION_MAPA==false) {
                    showAlertTypeLocationLote()
                } else if (UBICATION_MAPA == true) {
                    contentButtonsDrawingLoteMapa.visibility = View.VISIBLE
                    app_bar.layoutParams.width = AppBarLayout.LayoutParams.MATCH_PARENT
                    //app_bar.layoutParams.height = container_fragment.height
                    app_bar.layoutParams.height =AppBarLayout.LayoutParams.MATCH_PARENT
                    //app_bar.layoutParams.height=WindowManager.LayoutParams.FLAG_FULLSCREEN
                    //val appbar = findViewById(R.id.appbar) as AppBarLayout
                    //val heightDp = (resources.displayMetrics.heightPixels / 1).toFloat()
                   /// val lp = app_bar.layoutParams as CoordinatorLayout.LayoutParams
                    //lp.height = heightDp.toInt()

                } else if (UBICATION_GPS == true) {
                    showAlertDialogAddLote(null)
                }

                else if (UBICATION_MANUAL == true) {
                    showAlertDialogAddLote(null)
                }
            }
            R.id.ivClosetDialogLote ->{
                /*if(UBICATION_GPS){
                    closeServiceGps()
                }*/
                _dialogRegisterUpdate?.dismiss()
            }
            R.id.fabLocationLote -> showAlertTypeLocationLote()
            R.id.fabUnidadProductiva -> showAlertDialogSelectUp()
            R.id.ivBackButton -> {

                if(UBICATION_GPS){
                    closeServiceGps()
                }
                ivBackButton.setColorFilter(ContextCompat.getColor(activity!!.applicationContext, R.color.colorPrimary))
                (activity as MenuMainActivity).onBackPressed()
            }

            R.id.checkDoneMapView -> {
                if (UBICATION_MAPA == true && LastMarkerDrawingLote == null) {
                    Toast.makeText(activity, getString(R.string.message_location_lote_marker), Toast.LENGTH_LONG).show()
                } else {
                    showAlertDialogAddLote(null)
                }
            }

            R.id.checkCloseMapView -> {
                contentButtonsDrawingLoteMapa.visibility = View.GONE
                app_bar.layoutParams.width = AppBarLayout.LayoutParams.MATCH_PARENT
                app_bar.layoutParams.height = resources.getDimensionPixelSize(R.dimen.height_mapa)
                setPropertiesTypeLocationMapa()
                //app_bar.layoutParams.width=AppBarLayout.LayoutParams.MATCH_PARENT
                //var layoutParams1 =  LinearLayout.LayoutParams(AppBarLayout.LayoutParams.MATCH_PARENT,
                //R.dimen.height_mapa)
            }

            R.id.btnSaveLote -> {
                if (loteGlobal != null) {
                    updateLote(loteGlobal!!, loteGlobal?.Unidad_Productiva_Id)
                } else {
                    registerLote()
                }
            }
        }
    }


    //Escuchador de eventos
    override fun onEventBroadcastReceiver(extras: Bundle, intent: Intent) {
        if (extras != null) {
            if (extras.containsKey("state_conectivity")) {
                var state_conectivity = intent.extras!!.getBoolean("state_conectivity")
                if (state_conectivity == true) {
                    hideElementsAndSetPropertiesOnConectionInternet()
                }
            }

            if (extras.containsKey("is_enabled_gps")) {
                var state_gps = intent.extras!!.getBoolean("is_enabled_gps")
                if(!state_gps){
                    setPropertiesDisabledGps()
                }
            }


            if (extras.containsKey("latitud") && extras.containsKey("longitud")) {

                if (UBICATION_GPS == true) {

                    latitud = intent.extras!!.getDouble("latitud")
                    longitud = intent.extras!!.getDouble("longitud")


                    addMarkerLocation(latitud!!, longitud!!)
                    hideProgressHudCoords()
                    txtCoordsLote.setText(String.format(getString(R.string.coords), latitud, longitud))

                    YoYo.with(Techniques.Pulse)
                            .repeat(2)
                            .playOn(fabAddLote)

                    if(viewDialog != null){
                        viewDialog?.edtLatitud?.setText(latitud.toString())
                        viewDialog?.edtLongitud?.setText(longitud.toString())
                        viewDialog?.coordenadas_lote?.setText(String.format(getString(R.string.coords), latitud, longitud))
                    }
                }
                //Toast.makeText(activity,"Broadcast: "+longitud.toString(), Toast.LENGTH_SHORT).show()
                // tvCoords.setText(String.valueOf(location.getLatitude()) + " , " + String.valueOf(location.getLongitude()));
            }
        }
    }
    //endregion


    //region PERMISSIONS
    fun hasPermissions(context: Context?, vararg permissions: String): Boolean {
        if (android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.M && context != null && permissions != null) {
            for (permission in permissions) {
                if (ContextCompat.checkSelfPermission(context, permission) != PackageManager.PERMISSION_GRANTED) {
                    return false
                }
            }
        }
        return true
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        //RESPONSE GPS
        if (requestCode == RequestAccessPhoneResources.ENABLED_REQUEST_GPS) {
            if(presenter?.isLocationEnabled()!!){
                presenter?.startGps(activity!!)
            }else{
                UBICATION_GPS=false
                onMessageDisabledGps()
            }
        }
    }

    private fun requestPermission() {
        //ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, PERMISSION_REQUEST_CODE);
        //ContextCompat.requestPermissions(activity!!, PERMISSIONS, PERMISSION_ALL)
        requestPermissions(PERMISSIONS, PERMISSION_ALL)
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        when (requestCode) {
            PERMISSION_REQUEST_CODE ->
                if (grantResults.size > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    setPropertiesTypeLocationGps()

                    //showAlertDialogAddUnidadProductiva(null)
                } else if (grantResults[0] == PackageManager.PERMISSION_DENIED) {
                    if (shouldShowRequestPermissionRationale(Manifest.permission.ACCESS_FINE_LOCATION)) {
                        Toast.makeText(activity,
                                "Permiso denegado", Toast.LENGTH_LONG).show()

                        if(_dialogTypeLocation!=null){
                            _dialogTypeLocation?.dismiss()
                        }
                    } else {
                        if (hasPermissions(context, *PERMISSIONS)) {
                            setPropertiesTypeLocationGps()
                        } else {
                            val builder = AlertDialog.Builder(context!!)
                            builder.setMessage(R.string.enable_permissions_gps_settings)
                                    .setPositiveButton(R.string.confirm) { dialog, id ->
                                        val intent = Intent()
                                        intent.setAction(Settings.ACTION_APPLICATION_DETAILS_SETTINGS)
                                        val uri = Uri.fromParts("package", "com.interedes.agriculturappv3", null)
                                        intent.setData(uri)
                                        startActivity(intent)
                                    }
                            builder.setTitle(R.string.gps_disabled)
                            builder.setIcon(R.drawable.logo_agr_app)
                            // Create the AlertDialog object and return it
                            builder.show()

                        }
                    }
                }
        }
    }
    //endregion

    //region Overrides Methods
    //call this method in your onCreateMethod
    override fun onDestroy() {
        super.onDestroy()

        handler.removeCallbacksAndMessages(null);
        if (mapViewLotes != null) {
            mapViewLotes.onDestroy();
        }

        INI_TASK_HANDLER = false

        presenter?.onDestroy()
    }

    override fun onLowMemory() {
        super.onLowMemory()
        mapViewLotes.onLowMemory()
    }

    override fun onRefresh() {
        showProgress()
        presenter?.getLotes(Unidad_Productiva_Id_Selected)
    }

    override fun onPause() {
        super.onPause()
        presenter?.onPause(activity!!.applicationContext)
    }

    override fun onResume() {
        mapViewLotes.onResume()
        presenter?.onResume(activity!!.applicationContext)
        super.onResume()
    }
    //endregion
}