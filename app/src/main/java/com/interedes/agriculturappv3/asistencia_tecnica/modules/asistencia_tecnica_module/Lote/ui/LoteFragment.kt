package com.interedes.agriculturappv3.asistencia_tecnica.modules.asistencia_tecnica_module.Lote.ui


import android.Manifest
import android.content.*
import android.content.pm.PackageManager
import android.graphics.Color
import android.graphics.drawable.AnimationDrawable
import android.location.Location
import android.os.Bundle
import android.os.Handler
import android.support.design.widget.AppBarLayout
import android.support.design.widget.CoordinatorLayout
import android.support.design.widget.Snackbar
import android.support.v4.app.ActivityCompat
import android.support.v4.app.Fragment
import android.support.v4.content.ContextCompat
import android.support.v4.widget.SwipeRefreshLayout
import android.support.v7.app.AlertDialog
import android.support.v7.widget.GridLayoutManager
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import com.google.android.gms.maps.*
import com.google.android.gms.maps.model.*
import com.google.maps.android.SphericalUtil

import com.interedes.agriculturappv3.R
import com.interedes.agriculturappv3.asistencia_tecnica.models.Lote
import com.interedes.agriculturappv3.asistencia_tecnica.modules.asistencia_tecnica_module.Lote.presenter.LotePresenter
import com.interedes.agriculturappv3.asistencia_tecnica.modules.asistencia_tecnica_module.Lote.presenter.LotePresenterImpl
import kotlinx.android.synthetic.main.fragment_lote.*
import kotlinx.android.synthetic.main.dialog_lote.view.*
import java.util.ArrayList
import kotlinx.android.synthetic.main.map_layout.*
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.CameraUpdateFactory
import com.interedes.agriculturappv3.asistencia_tecnica.models.UP
import com.interedes.agriculturappv3.asistencia_tecnica.modules.asistencia_tecnica_module.Lote.adapter.LoteAdapter
import com.interedes.agriculturappv3.asistencia_tecnica.modules.ui.main_menu.MenuMainActivity
import com.kaopiz.kprogresshud.KProgressHUD
import kotlinx.android.synthetic.main.content_recicler_list.*


/**
 * A simple [Fragment] subclass.
 */
class LoteFragment : Fragment(), OnMapReadyCallback, SwipeRefreshLayout.OnRefreshListener, GoogleMap.OnMapClickListener ,View.OnClickListener , MainViewLote ,GoogleMap.OnMyLocationButtonClickListener{

    //Mapa
    private var mMap: GoogleMap? = null
    private var LastMarkerDrawingLote: Marker? = null
    private var polygon: Polygon?=null
    private var bounds: LatLngBounds? = null
    private val builder = LatLngBounds.Builder()
    private var LOADED_MAPA:Boolean=false

    //marker and location
    private var locationLote:Location= Location("")
    val listMarkerLote= ArrayList<Marker>()
    var markerLocation:Marker?=null

   //Presenter
    var presenter: LotePresenter? = null
    val handler = Handler()

    //Dialog
    private var _dialogRegisterUpdate: AlertDialog? = null
    private var _dialogTypeLocation: AlertDialog? = null
    var viewDialog:View?= null;

    //Coords
    private var latitud: Double = 0.0
    private var longitud: Double = 0.0

    //Progress
    /** These can be lateinit as they are set in onCreate */
    private var hud: KProgressHUD?=null

    //UbicationLote
    private var UBICATION_MANUAL: Boolean = false
    private var UBICATION_GPS: Boolean = false
    private var DIALOG_SET_TYPE_UBICATION: Int = -1

    //Adapter
    var adapter:LoteAdapter?=null
    var lotesList:ArrayList<Lote>?=ArrayList<Lote>()
    var loteGlobal:Lote?=null

    //Globals
    var Unidad_Productiva_Id:Long=0

    companion object {
        var instance:  LoteFragment? = null
    }

    init {
        instance=this
        //Presenter
         presenter = LotePresenterImpl(this)
        (presenter as LotePresenterImpl).onCreate()

    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        return  inflater.inflate(R.layout.fragment_lote, null)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initAdapter()
        fabAddLote.setOnClickListener(this)
        fabLocationLote.setOnClickListener(this)
        mapViewLotes.onCreate(savedInstanceState)
        mapViewLotes.onResume()
        swipeRefreshLayout.setOnRefreshListener(this);
        try {
            MapsInitializer.initialize(context!!)
        } catch (e: Exception) {
            Log.i("ErrorMap", e.message)
        }
        (activity as MenuMainActivity).toolbar.title=getString(R.string.title_lote)
        presenter?.getLotes();
        mapViewLotes.getMapAsync(this)
        loadInitTaskHandler()
        configAppBarLayout()
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
        //recyclerView?.layoutManager = LinearLayoutManager(activity)
        recyclerView?.layoutManager =GridLayoutManager(activity, 2)
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
        handler.postDelayed({
            if ((activity as MenuMainActivity).presenter?.checkConnection()!!){
                hideElementsAndSetPropertiesOnConectionInternet()
            }else{
                if(LOADED_MAPA==false){
                    showElementsAndSetPropertiesOffConnectioninternet()
                }
                onMessageError(R.color.grey_luiyi,getString(R.string.sin_conexion))
            }
        }, 10000)
     }


    override fun hideElementsAndSetPropertiesOnConectionInternet(){
        if(imgOffConection.visibility==View.VISIBLE) imgOffConection.visibility=View.GONE
        if(UBICATION_MANUAL==true){
            DIALOG_SET_TYPE_UBICATION=1
        }else if(UBICATION_GPS==true){
            DIALOG_SET_TYPE_UBICATION=0
        }else{
            DIALOG_SET_TYPE_UBICATION=-1
        }

    }
    override fun showElementsAndSetPropertiesOffConnectioninternet(){
        if(imgOffConection.visibility==View.GONE) imgOffConection.visibility=View.VISIBLE
        if(UBICATION_GPS==true){
            DIALOG_SET_TYPE_UBICATION=0
        }else{
            DIALOG_SET_TYPE_UBICATION=-1
        }
    }

    private fun initCollapsingToolbar() {
        /*val collapsingToolbar = collapsing_toolbar_layout as CollapsingToolbarLayout
        collapsingToolbar.title = " "
        appBarLayout = app_bar_layout as AppBarLayout
        appBarLayout?.setExpanded(false)*/

        /*
        // hiding & showing the txtPostTitle when toolbar expanded & collapsed
        appBarLayout.addOnOffsetChangedListener(object : AppBarLayout.OnOffsetChangedListener {
            internal var isShow = false
            internal var scrollRange = -1

            override fun onOffsetChanged(appBarLayout: AppBarLayout, verticalOffset: Int) {
                if (scrollRange == -1) {
                    scrollRange = appBarLayout.totalScrollRange
                }
                if (scrollRange + verticalOffset == 0) {
                    collapsingToolbar.title = ""
                    toolbarLote.setBackgroundColor(activity!!.applicationContext.resources.getColor(R.color.white))
                    isShow = true
                } else if (isShow) {
                    collapsingToolbar.title = " "
                    isShow = false
                }
            }
        })*/
    }

    //endregion

    //region MAPA
    //On Map Reding
    override fun onMapReady(map: GoogleMap?) {
        mMap = map
        if (ActivityCompat.checkSelfPermission(activity!!, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(activity!!, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(activity!!, arrayOf(Manifest.permission.ACCESS_FINE_LOCATION), 1000)
        } else {
            mMap?.setMyLocationEnabled(true)
            mMap?.getUiSettings()?.isZoomControlsEnabled = true
            mMap?.getUiSettings()?.isZoomGesturesEnabled = true
            mMap?.setOnMapClickListener(this)
            mMap?.setOnMyLocationButtonClickListener(this);
            // Setting a click event handler for the map
            mMap?.setOnMapLoadedCallback(GoogleMap.OnMapLoadedCallback {
                LOADED_MAPA=true
                hideElementsAndSetPropertiesOnConectionInternet()
            })
            animateLocationMap()
        }
    }

    override fun onMyLocationButtonClick(): Boolean {
        showAlertTypeLocationLote()
        /*if(latitud!=0.0 && longitud!=0.0){
            addMarkerLocation(latitud,longitud)
        }else {
            presenter?.startGps(activity as MenuMainActivity)
        }*/
        //Toast.makeText(activity,"Click",Toast.LENGTH_SHORT).show();
        return true;
    }

    private fun animateLocationMap() {
        mMap?.clear()

        val locations = ArrayList<LatLng>()
        locations.add(LatLng(2.930625, -75.271928))
        locations.add(LatLng(2.930534, -75.271674))
        locations.add(LatLng(2.930248, -75.271566))
        locations.add(LatLng(2.930063,  -75.271644))
        locations.add(LatLng(2.930288, -75.272092))
        locations.add(LatLng(2.930625, -75.271928))

        val area = SphericalUtil.computeArea(locations)
        val rectOptions = PolygonOptions()
        rectOptions.addAll(locations)
        rectOptions.strokeColor(ContextCompat.getColor(activity!!, android.R.color.holo_orange_dark))
        rectOptions.fillColor(ContextCompat.getColor(activity!!, android.R.color.holo_orange_light))

        // Get back the mutable Polygon
        polygon = mMap?.addPolygon(rectOptions)
        getPolygonCenterPoint(locations)
        val width = resources.displayMetrics.widthPixels
        val height = resources.displayMetrics.heightPixels
        val padding = (width * 0.10).toInt() // offset from edges of the map 10% of screen
        val cu = CameraUpdateFactory.newLatLngBounds(bounds, width, height, padding)
        mMap?.animateCamera(cu)

        for (lote in this!!.lotesList!!){
            var latlngLote =  LatLng(lote.Latitud!!, lote.Longitud!!);
            var markerLote=addMarker(latlngLote, latlngLote.latitude.toString() + " : " + latlngLote.longitude, lote.Nombre!!, BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_YELLOW))
            listMarkerLote.add(markerLote!!)
        }
        // double area= calculateAreaOfGPSPolygonOnEarthInSquareMeters(locationsGlobals);
        Toast.makeText(activity, "AREA: " + String.format("%.0f mts", area), Toast.LENGTH_LONG).show()

    }

    ///Convert Coords Poligon to LatLng Unique
    private fun getPolygonCenterPoint(polygonPointsList: ArrayList<LatLng>): LatLng? {
        var centerLatLng: LatLng? = null
        for (i in polygonPointsList.indices) {
            builder.include(polygonPointsList[i])
            /*Location locationadd= new Location("");
            locationadd.setLatitude(polygonPointsList.get(i).latitude);
            locationadd.setLatitude(polygonPointsList.get(i).latitude);
            locationsGlobals.add(locationadd);*/
        }
        bounds = builder.build()
        centerLatLng = bounds?.getCenter()
        return centerLatLng
    }

    override fun onMapClick(latLng: LatLng) {
        if (LastMarkerDrawingLote != null) {
            LastMarkerDrawingLote?.remove()
        }
        if(UBICATION_MANUAL==true){
            // Creating a marker
            LastMarkerDrawingLote = drawMarker(latLng, latLng.latitude.toString() + " : " + latLng.longitude, "Location", BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_GREEN))
            txtCoordsLote.setText(String.format(getString(R.string.coords),latLng.latitude,latLng.longitude))
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
    private fun addMarkerLocation(lat:Double,lng:Double) {
        // Instantiating CircleOptions to draw a circle around the marker
       //Get Zoom last(Obtener zoom anterior)
        var zoom= mMap?.getCameraPosition()!!.zoom
       /// Toast.makeText(getApplicationContext(),"Zoom: "+String.valueOf(zoom),Toast.LENGTH_SHORT).show();
        var coordenadas =  LatLng(lat, lng)
        var myposition = CameraUpdateFactory.newLatLngZoom(coordenadas, zoom)
        if(markerLocation != null) markerLocation?.remove()

        //Define color del marker
        var bitmapMarker= BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_BLUE)
        markerLocation = addMarker(coordenadas,"Mi Ubicacion","",bitmapMarker)

        if(UBICATION_GPS==true){
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
        viewDialog?.progressBar?.visibility= View.VISIBLE;
        progressBar?.visibility= View.VISIBLE;
        swipeRefreshLayout.setRefreshing(true);
    }

    override fun hideProgress() {
        viewDialog?.progressBar?.visibility= View.GONE;
        progressBar?.visibility= View.GONE;
        swipeRefreshLayout.setRefreshing(false);
    }

    override fun validarCampos(): Boolean? {
        var cancel = false
        var focusView: View? = null

        if (viewDialog?.spinnerUnidadProductiva?.text.toString().isEmpty()) {
            viewDialog?.spinnerUnidadProductiva?.setError(getString(R.string.error_field_required))
            focusView =  viewDialog?.spinnerUnidadProductiva
            cancel = true
        }
        else if (viewDialog?.name_lote?.text.toString().isEmpty()) {
            viewDialog?.name_lote?.setError(getString(R.string.error_field_required))
            focusView =  viewDialog?.name_lote
            cancel = true
        } else if (viewDialog?.description_lote?.text.toString().isEmpty()) {
            viewDialog?.description_lote?.setError(getString(R.string.error_field_required))
            focusView =  viewDialog?.description_lote
            cancel = true
        } else if (viewDialog?.area_lote?.text.toString().isEmpty()) {
            viewDialog?.area_lote?.setError(getString(R.string.error_field_required))
            focusView =  viewDialog?.area_lote
            cancel = true
        } else if (viewDialog?.coordenadas_lote?.text.toString().isEmpty()) {
            viewDialog?.coordenadas_lote?.setError(getString(R.string.error_field_required))
            focusView =  viewDialog?.coordenadas_lote
            cancel = true
        } /* else if (!edtCorreo.text.toString().trim().matches(email_pattern.toRegex())) {
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
        viewDialog?.name_lote?.isEnabled = b
        viewDialog?.description_lote?.isEnabled = b
        viewDialog?.area_lote?.isEnabled = b
    }

    override fun limpiarCampos() {
        viewDialog?.name_lote?.setText("");
        viewDialog?.description_lote?.setText("");
        viewDialog?.area_lote?.setText("");
        viewDialog?.coordenadas_lote?.setText("");
        if(LastMarkerDrawingLote != null) LastMarkerDrawingLote?.remove()
        LastMarkerDrawingLote=null
    }


    override fun setPropertiesTypeLocationGps(){
        UBICATION_GPS=true
        UBICATION_MANUAL=false
        if (LastMarkerDrawingLote != null) {
            LastMarkerDrawingLote?.remove()
        }
        if(LastMarkerDrawingLote != null) LastMarkerDrawingLote?.remove()
        LastMarkerDrawingLote=null
        _dialogTypeLocation?.dismiss()
        txtCoordsLote.setText("")
    }
    override fun setPropertiesTypeLocationManual(){
        presenter?.closeServiceGps()
        if(markerLocation != null) markerLocation?.remove()
        UBICATION_GPS=false
        UBICATION_MANUAL=true
        if (fabAddLote.getVisibility() == View.GONE) {
            fabAddLote.visibility=View.VISIBLE
        }
        _dialogTypeLocation?.dismiss()
        Toast.makeText(activity,"Ubicacion Manual",Toast.LENGTH_SHORT).show()
        txtCoordsLote.setText("")
    }


    override fun requestResponseOk() {
        _dialogRegisterUpdate?.dismiss()
        onMessageOk(R.color.colorPrimary,getString(R.string.request_ok));
    }

    override fun onMessageOk(colorPrimary: Int, message: String?) {
        val color = Color.WHITE
        val snackbar = Snackbar
                .make(container, message!!, Snackbar.LENGTH_LONG)
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

    override fun setListUP(listUp: List<UP>) {
         ///Adapaters
        viewDialog?.spinnerUnidadProductiva!!.setAdapter(null);
        var upArrayAdapter = ArrayAdapter<UP>(activity, android.R.layout.simple_spinner_dropdown_item, listUp);
        viewDialog?.spinnerUnidadProductiva!!.setAdapter(upArrayAdapter);
        viewDialog?.spinnerUnidadProductiva!!.onItemClickListener = AdapterView.OnItemClickListener { adapterView, view, position, l ->
            Unidad_Productiva_Id=listUp[position].Id!!

            Toast.makeText(activity,""+Unidad_Productiva_Id.toString(),Toast.LENGTH_SHORT).show()
        }
    }

    override fun loadListUp() {
        presenter?.listUP()
    }

    override fun setListLotes(lotes: List<Lote>) {
        adapter?.clear()
        lotesList?.clear()
        listMarkerLote.clear()

        adapter?.setItems(lotes)
        hideProgress()
        setResults(lotes.size)
        if(mapViewLotes.getVisibility()==View.VISIBLE){
            animateLocationMap()
        }
    }

    override fun setResults(lotes: Int) {
         var results = String.format(getString(R.string.results_global_search),
        lotes);
        txtResults.setText(results);
    }

    override fun registerLote() {
        if (presenter?.validarCampos() == true) {
            val lote =  Lote()
            lote.Nombre=viewDialog?.name_lote?.text.toString()
            lote.Descripcion=viewDialog?.description_lote?.text.toString()
            lote.Area=viewDialog?.area_lote?.text.toString().toDoubleOrNull()
            lote.Coordenadas=viewDialog?.coordenadas_lote?.text.toString()
            lote.Unidad_Medida_Id=1
            lote.Unidad_Productiva_Id=Unidad_Productiva_Id
            lote.Latitud=locationLote.latitude
            lote.Longitud=locationLote.longitude
            presenter?.registerLote(lote)
        }
    }

    override fun updateLote() {
        if (presenter?.validarCampos() == true) {
            var lote =  Lote()
            lote.Id=loteGlobal!!.Id
            lote.Nombre=viewDialog?.name_lote?.text.toString()
            lote.Descripcion=viewDialog?.description_lote?.text.toString()
            lote.Area=viewDialog?.area_lote?.text.toString().toDoubleOrNull()
            lote.Coordenadas=viewDialog?.coordenadas_lote?.text.toString()
            lote.Unidad_Medida_Id=loteGlobal?.Unidad_Medida_Id
            lote.Unidad_Productiva_Id=loteGlobal?.Unidad_Productiva_Id
            lote.Latitud=loteGlobal?.Latitud
            lote.Longitud=loteGlobal?.Longitud
            presenter?.updateLote(lote)
        }
    }



    override fun requestResponseError(error: String?) {
        onMessageError(R.color.grey_luiyi,error)
    }


    //UI ELements
    override fun showAlertDialogAddLote(lote:Lote?): AlertDialog? {
        var dialog = AlertDialog.Builder(activity!!)
        val inflater = this.layoutInflater
        viewDialog = inflater.inflate(R.layout.dialog_lote, null)
        val coordsLote = viewDialog?.coordenadas_lote

        val btnClosetDialogLote = viewDialog?.ivClosetDialogLote
        val btnRegister = viewDialog?.btnRegisterLote
        val btnUpdateLote = viewDialog?.btnUpdateLote
        val btnCancelLote = viewDialog?.btnCancelLote

        loadListUp()

        btnUpdateLote?.setOnClickListener(this)
        btnRegister?.setOnClickListener(this)
        btnCancelLote?.setOnClickListener(this)
        btnClosetDialogLote?.setOnClickListener(this)
        //REGISTER
        if(lote==null){
            btnRegister?.visibility=View.VISIBLE
            btnUpdateLote?.visibility=View.GONE
            if(UBICATION_MANUAL==true){
                if (LastMarkerDrawingLote != null) {
                    locationLote.latitude=LastMarkerDrawingLote!!.getPosition().latitude
                    locationLote.longitude=LastMarkerDrawingLote!!.getPosition().longitude
                    coordsLote?.setText(LastMarkerDrawingLote!!.getPosition().latitude.toString() + " / " + LastMarkerDrawingLote!!.getPosition().longitude)
                }
            }else{
                locationLote.latitude=latitud!!
                locationLote.longitude=longitud!!
                coordsLote?.setText(latitud.toString() + " / " + longitud.toString())
            }
        }
        //UPDATE
        else{
            btnRegister?.visibility=View.GONE
            btnUpdateLote?.visibility=View.VISIBLE
            viewDialog?.name_lote?.setText(lote.Nombre)
            viewDialog?.description_lote?.setText(lote.Descripcion)
            viewDialog?.area_lote?.setText(lote.Area.toString())
            viewDialog?.coordenadas_lote?.setText(lote.Coordenadas)
        }

        dialog?.setView(viewDialog)
        dialog?.setTitle(getString(R.string.add_lote))
        dialog?.setNegativeButton(getString(R.string.close), DialogInterface.OnClickListener { dialog, which ->
            Snackbar.make(viewDialog?.coordenadas_lote!!, "No se realizaron cambios", Snackbar.LENGTH_LONG)
                    .setAction("Action", null).show()
        })
       // dialog?.setMessage(getString(R.string.message_add_lote))
        dialog?.setIcon(R.drawable.ic_lote)
        _dialogRegisterUpdate = dialog?.show()
        return _dialogRegisterUpdate
    }

    override fun showAlertTypeLocationLote(): AlertDialog? {
        var  builder = AlertDialog.Builder(activity!!)
        builder.setTitle(getString(R.string.location_lote_select_type))
        //var options=arrayOf(getString(R.string.location_type_gps), getString(R.string.location_type_manual))
        var options=arrayOf("")
        //La lista varia dependiendo del tipo de conectividad
        if(LOADED_MAPA==true || (activity as MenuMainActivity).presenter?.checkConnection()==true){
            options=resources.getStringArray(R.array.array_type_get_location_with_conection)
            hideElementsAndSetPropertiesOnConectionInternet()
        }else{
            options=resources.getStringArray(R.array.array_type_get_location_empty_conection)
            showElementsAndSetPropertiesOffConnectioninternet()
        }

        builder.setSingleChoiceItems(options,DIALOG_SET_TYPE_UBICATION,DialogInterface.OnClickListener { dialog, which ->
            when (which) {
                //Position State Location GPS
                0 -> {
                    _dialogTypeLocation= dialog as AlertDialog?
                    setPropertiesTypeLocationGps()
                    presenter?.startGps(activity as MenuMainActivity)

                    var  imageView =  ImageView(activity);
                    imageView.setBackgroundResource(R.drawable.spin_animation);
                    var drawable =  imageView.getBackground() as AnimationDrawable;
                    drawable.start();

                    hud = KProgressHUD.create(activity)
                        .setCustomView(imageView)
                        .setWindowColor(resources.getColor(R.color.white))
                        .setLabel("Cargando...",resources.getColor(R.color.grey_luiyi));
                    hud?.show()
                    //scheduleDismiss();
                }
                //Position State Location Manual
                else -> {
                    _dialogTypeLocation= dialog as AlertDialog?
                    setPropertiesTypeLocationManual()
                }
            }
        })
        builder.setIcon(R.drawable.ic_localizacion_finca);
        return builder.show();
    }


     override fun confirmDelete(lote:Lote): AlertDialog? {
         var  builder = AlertDialog.Builder(activity!!)
         builder.setTitle(getString(R.string.confirmation));
         builder.setNegativeButton(getString(R.string.close), DialogInterface.OnClickListener { dialog, which ->

         })
         builder.setMessage(getString(R.string.alert_delete_lote));
         builder?.setPositiveButton(getString(R.string.confirm), DialogInterface.OnClickListener { dialog, which ->
             loteGlobal=lote
             presenter?.deleteLote(lote)
         })
        builder.setIcon(R.drawable.ic_lote);
        return builder.show();
    }




    //endregion

    //region Events
    override fun onClick(v: View?) {
        when (v?.id) {
            R.id.fabAddLote -> {
                showAlertDialogAddLote(null)
            }
            R.id.ivClosetDialogLote->_dialogRegisterUpdate?.dismiss()
            R.id.btnCancelLote->_dialogRegisterUpdate?.dismiss()
            R.id.btnRegisterLote -> registerLote()
            R.id.btnUpdateLote -> updateLote()
            R.id.fabLocationLote -> showAlertTypeLocationLote()
        }
    }


    //Escuchador de eventos
    override fun onEventBroadcastReceiver(extras: Bundle, intent:Intent) {
        if(extras!=null){
            if (extras.containsKey("state_conectivity")) {
                var state_conectivity = intent.extras!!.getBoolean("state_conectivity")
                if(state_conectivity==true){
                    hideElementsAndSetPropertiesOnConectionInternet()
                }
            }
            if(extras.containsKey("latitud") && extras.containsKey("longitud") ){
                latitud=intent.extras!!.getDouble("latitud")
                longitud=intent.extras!!.getDouble("longitud")

                if (fabAddLote.getVisibility() == View.GONE) {
                    fabAddLote.visibility= View.VISIBLE
                }
                addMarkerLocation(latitud,longitud)
                hud?.dismiss()

                txtCoordsLote.setText(String.format(getString(R.string.coords),latitud,longitud))
                //Toast.makeText(activity,"Broadcast: "+longitud.toString(), Toast.LENGTH_SHORT).show()
                // tvCoords.setText(String.valueOf(location.getLatitude()) + " , " + String.valueOf(location.getLongitude()));
            }
        }
    }
    //endregion

    //region Overrides Methods
    //call this method in your onCreateMethod
    override fun onDestroy() {
        super.onDestroy()
        presenter?.onDestroy()
        handler.removeCallbacksAndMessages(null);
        if(mapViewLotes != null) {
            mapViewLotes.onDestroy();
        }
    }

    override fun onLowMemory() {
        super.onLowMemory()
        mapViewLotes.onLowMemory()
    }

    override fun onRefresh() {
       showProgress()
        presenter?.getLotes()
    }

    override fun onPause() {
        super.onPause()
        presenter?.onPause( activity!!.applicationContext)
    }

    override fun onResume() {
        mapViewLotes.onResume()
        presenter?.onResume(activity!!.applicationContext)
        super.onResume()
    }
    //endregion

}
