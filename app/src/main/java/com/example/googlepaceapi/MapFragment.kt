package com.example.googlepaceapi


import android.content.Intent
import android.location.Geocoder
import android.location.Location
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.google.android.gms.maps.*
import com.google.android.gms.maps.model.LatLng
import java.util.*
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.model.MarkerOptions
import android.app.Activity
import com.sucho.placepicker.AddressData
import com.sucho.placepicker.Constants
import com.sucho.placepicker.MapType


const val PLACE_PICKER_REQUEST = 999

class MapFragment : Fragment(), OnMapReadyCallback {

    private lateinit var googleMap: GoogleMap
    private lateinit var mapView: MapView
    private var fusedLocationClient: FusedLocationProviderClient? = null
    private var location: Location? = null
    private lateinit var texter: TextView
    private lateinit var markerOptions: MarkerOptions

    private var currentLatitude: Double = 0.0
    private var currentLongitude: Double = 0.0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(activity!!)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_map, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        bindViews(view)
        initMap()
        mapView.onCreate(savedInstanceState)

    }

    override fun onResume() {
        super.onResume()
        mapView.onResume()
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        Log.d("dinara_address", resultCode.toString())
        if (resultCode == Activity.RESULT_OK) {
            val addressData = data?.getParcelableExtra<AddressData>(Constants.ADDRESS_INTENT)
            val pickedLatLng = LatLng(addressData!!.latitude, addressData!!.longitude)
            markerOptions = MarkerOptions()
            if (addressData != null) {
                markerOptions.position(pickedLatLng)
            }
            googleMap.addMarker(
                markerOptions
            )
            googleMap.animateCamera(CameraUpdateFactory.newLatLng(pickedLatLng))
            Log.d("dinara_address", addressData.toString())
        }
    }

    override fun onMapReady(map: GoogleMap) {
        try {
            MapsInitializer.initialize(activity?.applicationContext)
        } catch (e: Exception) {
        }
        googleMap = map
        googleMap.uiSettings.isMapToolbarEnabled = false
        googleMap.isMyLocationEnabled = true
        googleMap.uiSettings.isMyLocationButtonEnabled = false
        googleMap.uiSettings.isCompassEnabled = true
        fusedLocationClient?.lastLocation?.addOnCompleteListener { result ->
            result.result?.let { location ->
                this@MapFragment.location = location
                val currentLatLng = LatLng(location.latitude, location.longitude)
                Log.d("location_part", currentLatLng.toString())
                currentLatitude = location.latitude
                currentLongitude = location.longitude
                googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(currentLatLng, 13.5f))
            }
        }
    }

    private fun bindViews(view: View) = with(view) {
        mapView = view.findViewById(R.id.map)
        texter = view.findViewById(R.id.texter)
        texter.setOnClickListener {
            val intent = com.sucho.placepicker.PlacePicker.IntentBuilder()
                .setLatLong(
                    currentLatitude,
                    currentLongitude
                )  // Initial Latitude and Longitude the Map will load into
                .showLatLong(true)  // Show Coordinates in the Activity
                .setMapZoom(12.0f)  // Map Zoom Level. Default: 14.0
                .setAddressRequired(true) // Set If return only Coordinates if cannot fetch Address for the coordinates. Default: True
                .hideMarkerShadow(true) // Hides the shadow under the map marker. Default: False
                .setMapType(MapType.NORMAL)
                .onlyCoordinates(true)  //Get only Coordinates from Place Picker
                .build(activity!!)
            startActivityForResult(intent, Constants.PLACE_PICKER_REQUEST)
        }
    }

    private fun initMap() {
        mapView.getMapAsync(this)
    }

}
