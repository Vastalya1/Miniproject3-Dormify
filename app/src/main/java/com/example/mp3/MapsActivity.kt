package com.example.mp3

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.location.Location
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.compose.ui.platform.ComposeView
import androidx.core.app.ActivityCompat
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import com.example.mp3.ui.theme.MP3Theme
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.Marker
import com.google.android.gms.maps.model.MarkerOptions
import com.google.android.libraries.places.api.Places
import com.google.android.libraries.places.api.net.PlacesClient
import com.google.firebase.firestore.BuildConfig
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch



class MapsActivity : AppCompatActivity(), OnMapReadyCallback {
    private lateinit var map: GoogleMap
    private val firestore = FirebaseFirestore.getInstance()
    private lateinit var fusedLocationClient: FusedLocationProviderClient
    private lateinit var placesClient: PlacesClient
    private lateinit var viewModel: UserViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_maps3)

        // Initialize Places API
        if (!Places.isInitialized()) {
            Places.initialize(applicationContext, getString(R.string.maps_api_key))
        }
        placesClient = Places.createClient(this)

        // Initialize ViewModel
        viewModel = ViewModelProvider(this, UserViewModelFactory(placesClient))[UserViewModel::class.java]

        // Initialize the FusedLocationProviderClient
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this)

        // Initialize the map
        val mapFragment = supportFragmentManager.findFragmentById(R.id.map) as SupportMapFragment
        mapFragment.getMapAsync(this)

        // Set up Compose View
        findViewById<ComposeView>(R.id.compose_view).apply {
            setContent {
                MP3Theme {
                    SearchInterface(viewModel)
                }
            }
        }

        // Observe location updates
        lifecycleScope.launch {
            viewModel.selectedLocationLatLng.collectLatest { latLng ->
                latLng?.let {
                    updateMapLocation(it)
                }
            }
        }
    }


    override fun onMapReady(googleMap: GoogleMap) {
        map = googleMap
        map.setInfoWindowAdapter(CustomInfoWindowAdapter(this))
        getCurrentLocation()
    }

    private fun updateMapLocation(latLng: LatLng) {
        map.clear()
        map.addMarker(MarkerOptions().position(latLng))
        map.animateCamera(CameraUpdateFactory.newLatLngZoom(latLng, 15f))
        fetchNearbyProperties(latLng)
    }

    private fun fetchNearbyProperties(center: LatLng) {
        val radius = 5000 // 5km in meters
        val centerLocation = Location("").apply {
            latitude = center.latitude
            longitude = center.longitude
        }

        firestore.collection("locations")
            .get()
            .addOnSuccessListener { documents ->
                for (document in documents) {
                    val geoPoint = document.getGeoPoint("location")
                    if (geoPoint != null) {
                        val propertyLocation = Location("").apply {
                            latitude = geoPoint.latitude
                            longitude = geoPoint.longitude
                        }

                        if (centerLocation.distanceTo(propertyLocation) <= radius) {
                            lifecycleScope.launch {
                                if (shouldShowProperty(document)) {
                                    addPropertyMarker(document)
                                }
                            }
                        }
                    }
                }
            }
    }

    private suspend fun shouldShowProperty(document: DocumentSnapshot): Boolean {
        val price = document.getDouble("price")?.toFloat() ?: 0f
        val type = document.getString("type")?.lowercase() ?: ""

        val budget = viewModel.budget.value
        val isPG = viewModel.isPGSelected.value
        val isRental = viewModel.isRentalSelected.value
        val isHostel = viewModel.isHostelSelected.value

        return price <= budget && when (type) {
            "pg" -> isPG
            "rental" -> isRental
            "hostel" -> isHostel
            else -> false
        }
    }

    private fun addPropertyMarker(document: DocumentSnapshot) {
        val geoPoint = document.getGeoPoint("location") ?: return
        val latLng = LatLng(geoPoint.latitude, geoPoint.longitude)
        val name = document.getString("name") ?: "Unknown"
        val address = document.getString("address") ?: "No Address"
        val price = document.getDouble("price")?.toString() ?: "No Price"

        map.addMarker(
            MarkerOptions()
                .position(latLng)
                .title(name)
                .snippet("Address: $address\nPrice: $price")
        )
    }

    private fun getCurrentLocation() {
        if (ActivityCompat.checkSelfPermission(
                this,
                Manifest.permission.ACCESS_FINE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            ActivityCompat.requestPermissions(
                this,
                arrayOf(Manifest.permission.ACCESS_FINE_LOCATION),
                LOCATION_PERMISSION_REQUEST_CODE
            )
            return
        }

        fusedLocationClient.lastLocation.addOnSuccessListener { location ->
            location?.let {
                val latLng = LatLng(it.latitude, it.longitude)
                map.addMarker(MarkerOptions().position(latLng).title("Your Location"))
                map.moveCamera(CameraUpdateFactory.newLatLngZoom(latLng, 15f))
            }
        }
    }

    companion object {
        private const val LOCATION_PERMISSION_REQUEST_CODE = 1
    }
}

class CustomInfoWindowAdapter(private val context: Context) : GoogleMap.InfoWindowAdapter {
    override fun getInfoContents(marker: Marker): View {
        val view = LayoutInflater.from(context).inflate(R.layout.custom_info_window, null)

        view.findViewById<TextView>(R.id.location_name).text = marker.title

        marker.snippet?.split("\n")?.let { parts ->
            view.findViewById<TextView>(R.id.location_address).text = parts.getOrNull(0) ?: "No Address"
            view.findViewById<TextView>(R.id.location_price).text = parts.getOrNull(1) ?: "No Price"
        }

        return view
    }

    override fun getInfoWindow(marker: Marker): View? = null
}