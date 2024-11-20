package com.example.mp3

import android.Manifest
import android.content.pm.PackageManager
import android.location.Location
import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
import com.google.firebase.firestore.FirebaseFirestore

class MapsActivity : AppCompatActivity(), OnMapReadyCallback {

    private lateinit var map: GoogleMap
    private val firestore = FirebaseFirestore.getInstance()
    private lateinit var fusedLocationClient: FusedLocationProviderClient

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_maps3)

        // Initialize the FusedLocationProviderClient
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this)

        // Initialize the map
        val mapFragment = supportFragmentManager.findFragmentById(R.id.map) as SupportMapFragment
        mapFragment.getMapAsync(this)
    }

    override fun onMapReady(googleMap: GoogleMap) {
        map = googleMap

        // Fetch locations from Firestore
        fetchLocationsFromFirestore()

        // Get the current location of the user
        getCurrentLocation()
    }

    private fun fetchLocationsFromFirestore() {
        // Check if permissions are granted
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED ||
            ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            Log.e("Permission Error", "Location permission not granted.")
            return
        }

        fusedLocationClient.lastLocation.addOnSuccessListener { location: Location? ->
            if (location != null) {
                val userLatLng = LatLng(location.latitude, location.longitude)
                val userLocation = Location("").apply {
                    latitude = userLatLng.latitude
                    longitude = userLatLng.longitude
                }

                firestore.collection("locations")
                    .get()
                    .addOnSuccessListener { documents ->
                        if (!documents.isEmpty) {
                            var firstLatLng: LatLng? = null

                            for (document in documents) {
                                val geoPoint = document.getGeoPoint("location")
                                if (geoPoint != null) {
                                    val latitude = geoPoint.latitude
                                    val longitude = geoPoint.longitude
                                    val locationName = document.getString("name") ?: "Unknown Location"
                                    val address = document.getString("address") ?: "No Address"
                                    val price = document.getDouble("price")?.toString() ?: "No Price"

                                    // Calculate distance from user location
                                    val propertyLocation = Location("").apply {
                                        this.latitude = latitude
                                        this.longitude = longitude
                                    }
                                    val distanceInMeters = userLocation.distanceTo(propertyLocation)

                                    // Only add marker if within 5 km (5000 meters)
                                    if (distanceInMeters <= 5000) {
                                        val location = LatLng(latitude, longitude)
                                        map.addMarker(
                                            MarkerOptions()
                                                .position(location)
                                                .title(locationName)
                                                .snippet("Address: $address\nPrice: $price")
                                        )

                                        // Set the first location to move the camera to it
                                        if (firstLatLng == null) {
                                            firstLatLng = location
                                        }
                                    }
                                }
                            }

                            // Move the camera to the first location (if available)
                            if (firstLatLng != null) {
                                map.moveCamera(CameraUpdateFactory.newLatLngZoom(firstLatLng, 10f))
                            }
                        }
                    }
                    .addOnFailureListener { exception ->
                        Log.e("Firestore Error", "Error fetching locations: ", exception)
                    }
            }
        }
    }

    private fun getCurrentLocation() {
        // Check if permissions are granted
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED ||
            ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, arrayOf(Manifest.permission.ACCESS_FINE_LOCATION), 1)
            return
        }
        
        try {
            fusedLocationClient.lastLocation.addOnSuccessListener { location: Location? ->
                if (location != null) {
                    val userLatLng = LatLng(location.latitude, location.longitude)
                    map.addMarker(MarkerOptions().position(userLatLng).title("You are here"))
                    map.moveCamera(CameraUpdateFactory.newLatLngZoom(userLatLng, 15f))
                }
            }.addOnFailureListener { exception ->
                Log.e("Location Error", "Error fetching current location: ", exception)
            }
        } catch (e: SecurityException) {
            Log.e("Permission Error", "Location permission not granted: ", e)
        }
    }

}